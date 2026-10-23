package hirify.analytics.core.ai

import io.ktor.client.request.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.serialization.json.*

/**
 * Client for OpenAI API.
 */
class OpenAiClient : BaseAiClient() {

    override suspend fun sendPrompt(prompt: String, config: AiConfig): String {
        val message = sendChat(
            messages = listOf(AiMessage(role = "user", content = prompt)),
            config = config
        )
        return message.content.orEmpty()
    }

    override suspend fun sendChat(
        messages: List<AiMessage>,
        config: AiConfig,
        tools: List<AiToolDescriptor>
    ): AiMessage {
        val apiKey = config.getApiKeyForProvider()
        require(apiKey.isNotBlank()) { "OpenAI API key is required. Please configure it in the AI Settings menu." }

        val baseUrl = config.baseUrl.ifBlank { "https://api.openai.com/v1" }
        val url = "$baseUrl/chat/completions"

        val requestBody = OpenAiCompatibleJson.buildChatRequest(
            messages, config, tools,
            maxTokensField = "max_completion_tokens",
            // Only GPT-6 needs it for tools; older models reject the parameter.
            reasoningEffort = "none".takeIf { config.model.startsWith("gpt-6") }
        ).toString()
        println("[AI-DEBUG] OpenAI Request JSON: $requestBody")

        for (attempt in 1..MAX_ATTEMPTS) {
            var responseJson: JsonObject? = null
            try {
                val responseText = executeRequest(url, requestBody) {
                    header("Authorization", "Bearer $apiKey")
                }
                val parsed = json.parseToJsonElement(responseText).jsonObject
                responseJson = parsed

                if (parsed.isRateLimitError()) error("Rate limit exceeded")

                val choice = parsed["choices"]?.jsonArray?.firstOrNull()
                    ?: error("No choices in OpenAI response: $responseText")
                val messageObj = choice.jsonObject["message"]?.jsonObject
                    ?: error("No message in OpenAI choice")
                return OpenAiCompatibleJson.parseAssistantMessage(messageObj)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                val isRateLimit = e.message?.let { "Rate limit exceeded" in it || "429" in it } == true
                if (attempt >= MAX_ATTEMPTS || !isRateLimit) throw e

                // Honour the server-suggested wait time if present, otherwise back off linearly
                val waitMs = (responseJson.suggestedRetryDelayMs() ?: (2000L * attempt)) + 500L
                println("[AI-DEBUG] OpenAI Rate Limit hit! Retrying in ${waitMs}ms (Attempt $attempt of $MAX_ATTEMPTS)...")
                delay(waitMs)
            }
        }
        error("Unreachable: retry loop always returns or throws")
    }

    private fun JsonObject.isRateLimitError(): Boolean {
        val errorObj = this["error"]?.jsonObject ?: return false
        return errorObj["code"]?.jsonPrimitive?.contentOrNull == "rate_limit_exceeded" ||
            errorObj["type"]?.jsonPrimitive?.contentOrNull == "tokens" ||
            errorObj["message"]?.jsonPrimitive?.contentOrNull?.contains("Rate limit") == true
    }

    /** Parses "Please try again in Xms." / "in Xs." from the error message. */
    private fun JsonObject?.suggestedRetryDelayMs(): Long? {
        val errorMsg = this?.get("error")?.jsonObject?.get("message")?.jsonPrimitive?.contentOrNull ?: return null
        MILLIS_REGEX.find(errorMsg)?.let { return it.groupValues[1].toLongOrNull() }
        return SECONDS_REGEX.find(errorMsg)?.let { ((it.groupValues[1].toDoubleOrNull() ?: 1.0) * 1000).toLong() }
    }

    private companion object {
        const val MAX_ATTEMPTS = 6
        val MILLIS_REGEX = Regex("""in (\d+)ms""")
        val SECONDS_REGEX = Regex("""in (\d+(?:\.\d+)?)s""")
    }
}
