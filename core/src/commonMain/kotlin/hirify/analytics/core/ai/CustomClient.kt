package hirify.analytics.core.ai

import io.ktor.client.request.*
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.*

/**
 * Клиент для работы с пользовательскими OpenAI-совместимыми API.
 * Поддерживает любые сервисы, совместимые с OpenAI Chat Completions API.
 */
class CustomClient : BaseAiClient() {

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
        require(config.baseUrl.isNotBlank()) { "Base URL is required for custom API" }

        val url = "${config.baseUrl.trimEnd('/')}/chat/completions"
        val requestBody = OpenAiCompatibleJson.buildChatRequest(messages, config, tools).toString()
        println("[AI-DEBUG] Custom API Request JSON: $requestBody")

        try {
            val apiKey = config.getApiKeyForProvider()
            val responseText = executeRequest(url, requestBody) {
                if (apiKey.isNotBlank()) {
                    header("Authorization", "Bearer $apiKey")
                }
            }

            val responseJson = json.parseToJsonElement(responseText).jsonObject
            val choice = responseJson["choices"]?.jsonArray?.firstOrNull()
                ?: error("No choices in API response")
            val messageObj = choice.jsonObject["message"]?.jsonObject
                ?: error("No message in API response")

            return OpenAiCompatibleJson.parseAssistantMessage(messageObj)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            throw Exception(
                "Failed to connect to custom API at ${config.baseUrl}. " +
                "Make sure the endpoint is correct and supports OpenAI-compatible chat completions. " +
                "Original error: ${e.message}",
                e
            )
        }
    }
}
