package hirify.analytics.core.ai.agent

import hirify.analytics.core.ai.*
import hirify.analytics.core.analytics.VacancyFilter
import kotlinx.serialization.json.Json

class AgentService(
    private val openAiClient: OpenAiClient,
    private val googleClient: GoogleClient,
    private val yandexClient: YandexClient,
    private val ollamaClient: OllamaClient,
    private val customClient: CustomClient
) {
    suspend fun parseVoiceToFilter(transcript: String, config: AiConfig): VacancyFilter {
        val prompt = """
            You are an AI assistant that extracts job search filter parameters from a user's voice dictation.
            Convert the following Russian dictation into a JSON object matching this schema. Only return valid JSON, no markdown formatting.
            Allowed keys: specializations, grade, macroregion, remoteType, source, companyType, workFormat, skills, skillsMatchType.
            Allowed specialization codes: backend_dev, frontend_dev, ios_dev, android_dev, devops, qa_auto, pm. (Can be comma-separated)
            Allowed grade codes: trainee, junior, middle, senior, lead, head, director, c_level. (Can be comma-separated, e.g. "middle, senior")
            Allowed macroregion codes: russia, cis, europe, usa, canada. (Can be comma-separated)
            Allowed workFormat: remote, hybrid, onsite. (Can be comma-separated)
            Allowed remoteType: global, russia, europe, usa. (Can be comma-separated)
            Allowed companyType: startup, corporation, product_company. (Can be comma-separated)
            Allowed skills: any comma-separated string (e.g. "java, kotlin").
            Allowed skillsMatchType: "AND" or "OR".
            
            Example dictation: "Покажи вакансии сеньор бэкендеров на удаленке в Европе со знанием java и kotlin"
            JSON: {"specializations":"backend_dev", "grade":"senior", "workFormat":"remote", "macroregion":"europe", "skills":"java, kotlin", "skillsMatchType":"AND"}
            
            Dictation: "$transcript"
        """.trimIndent()
        
        val client = when (config.getProvider()) {
            AiProvider.OPENAI -> openAiClient
            AiProvider.GOOGLE -> googleClient
            AiProvider.YANDEX -> yandexClient
            AiProvider.OLLAMA -> ollamaClient
            AiProvider.CUSTOM -> customClient
            AiProvider.LOCAL_LLAMATIK -> ollamaClient // fallback
        }
        
        val response = client.sendPrompt(prompt, config)
        val jsonStr = response.trim().removePrefix("```json").removeSuffix("```").trim()
        
        return try {
            val json = Json { ignoreUnknownKeys = true; isLenient = true }
            json.decodeFromString<VacancyFilter>(jsonStr)
        } catch (e: Exception) {
            VacancyFilter() // fallback
        }
    }
}
