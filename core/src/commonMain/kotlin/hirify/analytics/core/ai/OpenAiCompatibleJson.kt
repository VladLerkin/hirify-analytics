package hirify.analytics.core.ai

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject

/**
 * Shared JSON mapping for OpenAI-compatible chat completion APIs
 * (used by [OpenAiClient] and [CustomClient]).
 */
internal object OpenAiCompatibleJson {

    /** Builds a `/chat/completions` request body. */
    fun buildChatRequest(
        messages: List<AiMessage>,
        config: AiConfig,
        tools: List<AiToolDescriptor>
    ): JsonObject = buildJsonObject {
        put("model", config.model)
        put("temperature", config.temperature)
        put("max_tokens", config.maxTokens)
        putJsonArray("messages") {
            for (msg in messages) addJsonObject {
                put("role", msg.role)
                // 'content' must be a string (not null) for 'assistant' and 'tool' roles.
                val content = msg.content ?: when (msg.role) {
                    "assistant", "tool" -> ""
                    else -> null
                }
                content?.let { put("content", it) }

                msg.toolCallId?.let { put("tool_call_id", it) }
                msg.toolCalls?.let { calls ->
                    putJsonArray("tool_calls") {
                        for (call in calls) addJsonObject {
                            put("id", call.id)
                            put("type", call.type)
                            putJsonObject("function") {
                                put("name", call.function.name)
                                put("arguments", call.function.arguments)
                            }
                        }
                    }
                }
            }
        }
        if (tools.isNotEmpty()) {
            putJsonArray("tools") {
                for (tool in tools) addJsonObject {
                    put("type", "function")
                    putJsonObject("function") {
                        put("name", tool.name)
                        put("description", tool.description)
                        put("parameters", tool.parameters)
                    }
                }
            }
        }
    }

    /** Parses `choices[0].message` of a chat completion response into an [AiMessage]. */
    fun parseAssistantMessage(messageObj: JsonObject): AiMessage = AiMessage(
        role = messageObj["role"]?.jsonPrimitive?.content ?: "assistant",
        content = messageObj["content"]?.jsonPrimitive?.contentOrNull,
        toolCalls = messageObj["tool_calls"]?.jsonArray?.map { callElement ->
            val callObj = callElement.jsonObject
            val funcObj = callObj["function"]?.jsonObject ?: error("No function in tool call")
            AiToolCall(
                id = callObj["id"]?.jsonPrimitive?.content.orEmpty(),
                type = callObj["type"]?.jsonPrimitive?.content ?: "function",
                function = AiFunctionCall(
                    name = funcObj["name"]?.jsonPrimitive?.content.orEmpty(),
                    arguments = funcObj["arguments"]?.jsonPrimitive?.content ?: "{}"
                )
            )
        }
    )
}
