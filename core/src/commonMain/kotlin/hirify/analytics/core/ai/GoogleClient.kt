package hirify.analytics.core.ai

import kotlinx.serialization.json.*

/**
 * Client for Google Gemini API.
 */
class GoogleClient : BaseAiClient() {
    
    override suspend fun sendPrompt(prompt: String, config: AiConfig): String {
        val apiKey = config.getApiKeyForProvider()
        require(apiKey.isNotBlank()) { "Google API key is required. Please configure it in the AI Settings menu." }
        
        val baseUrl = config.baseUrl.ifBlank { "https://generativelanguage.googleapis.com/v1" }
        val url = "$baseUrl/models/${config.model}:generateContent?key=$apiKey"
        
        val requestBody = buildJsonObject {
            putJsonArray("contents") {
                addJsonObject {
                    putJsonArray("parts") {
                        addJsonObject {
                            put("text", prompt)
                        }
                    }
                }
            }
            putJsonObject("generationConfig") {
                put("temperature", config.temperature)
                put("maxOutputTokens", config.maxTokens)
            }
            // Add safety settings to avoid false blocks
            // for genealogical content (names, dates, family relationships)
            putJsonArray("safetySettings") {
                addJsonObject {
                    put("category", "HARM_CATEGORY_HARASSMENT")
                    put("threshold", "BLOCK_NONE")
                }
                addJsonObject {
                    put("category", "HARM_CATEGORY_HATE_SPEECH")
                    put("threshold", "BLOCK_NONE")
                }
                addJsonObject {
                    put("category", "HARM_CATEGORY_SEXUALLY_EXPLICIT")
                    put("threshold", "BLOCK_NONE")
                }
                addJsonObject {
                    put("category", "HARM_CATEGORY_DANGEROUS_CONTENT")
                    put("threshold", "BLOCK_NONE")
                }
            }
        }
        
        // Log request for debugging
        val requestBodyString = requestBody.toString()
        println("[DEBUG_LOG] GoogleClient: Sending request to Gemini API")
        println("[DEBUG_LOG] GoogleClient: Request body length: ${requestBodyString.length}")
        println("[DEBUG_LOG] GoogleClient: Request body preview (first 1000 chars): ${requestBodyString.take(1000)}")
        
        val responseText = executeRequest(url, requestBodyString)
        
        val responseJson = json.parseToJsonElement(responseText).jsonObject
        
        // Log full response for debugging
        println("[DEBUG_LOG] GoogleClient: Full Gemini API response: $responseText")
        
        // Extract response text from Gemini API structure
        val firstCandidate = responseJson["candidates"]?.jsonArray?.firstOrNull()
        if (firstCandidate == null) {
            // Check for block information
            responseJson["promptFeedback"]?.jsonObject?.let { promptFeedback ->
                println("[DEBUG_LOG] GoogleClient: promptFeedback found: $promptFeedback")
                promptFeedback["blockReason"]?.jsonPrimitive?.content?.let { blockReason ->
                    error("Google Gemini blocked the request: $blockReason")
                }
            }
            error("No candidates in Google Gemini response. Full response: $responseText")
        }
        
        val firstPart = firstCandidate.jsonObject["content"]?.jsonObject
            ?.get("parts")?.jsonArray?.firstOrNull()
            ?: error("No parts in Google Gemini response")
        
        return firstPart.jsonObject["text"]?.jsonPrimitive?.content
            ?: error("No text in Google Gemini response")
    }
}
