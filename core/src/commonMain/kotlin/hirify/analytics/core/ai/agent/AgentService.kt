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
            Allowed keys: specializations, grade, macroregion, remoteType, source, companyType, workFormat, skills, skillsMatchType, englishLevel, vacancyLanguage, employeeType, includeScam, country, title, status.
            Allowed specialization codes: backend_dev, frontend_dev, ios_dev, android_dev, devops, qa_auto, pm. (Can be comma-separated)
            Allowed grade codes: trainee, junior, middle, senior, lead, head, director, c_level. (Can be comma-separated, e.g. "middle, senior")
            Allowed macroregion codes: russia, cis, europe, usa, canada, latam, mena, ssa, apac, sea, oceania. (Can be comma-separated)
            Allowed workFormat: remote, hybrid, onsite. (Can be comma-separated)
            Allowed remoteType: global, russia, europe, usa. (Can be comma-separated)
            Allowed companyType: startup, corporation, product_company, outsourcing_company. (Can be comma-separated)
            Allowed employeeType: employment, b2b_contract. (Can be comma-separated)
            Allowed vacancyLanguage: ru, en. (Can be comma-separated)
            Allowed englishLevel: a1, a2, b1, b2, c1, c2. (Can be comma-separated)
            Allowed status: all, active, archived.
            Allowed includeScam: true, false.
            Allowed country: ISO country codes (e.g. "de", "us", "ru", "cy"). (Can be comma-separated)
            Allowed title: search string (e.g. "devops", "sre", "product manager").
            Allowed skills: any comma-separated string (e.g. "java, kotlin"). The user will often dictate English IT skills using Russian words, transliterations, or with misspellings (e.g., "спринг", "котлин", "нода", "сикуэл", "реак"). You MUST analyze the phonetic spelling of such words and ALWAYS translate and correct them to the standard English IT terms (e.g., "Spring", "Kotlin", "Node.js", "SQL", "React"). If there's a typo in English (e.g., "jvascript"), correct it to the proper term (e.g., "JavaScript"). Validate against a professional IT skills dictionary.
            Allowed skillsMatchType: "AND" or "OR".
            
            Example dictation: "Покажи вакансии сеньор бэкендеров на удаленке в Европе со знанием java и kotlin в штат на английском"
            JSON: {"specializations":"backend_dev", "grade":"senior", "workFormat":"remote", "macroregion":"europe", "skills":"java, kotlin", "skillsMatchType":"AND", "employeeType":"employment", "vacancyLanguage":"en"}
            
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
