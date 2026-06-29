package hirify.analytics.core.analytics

import hirify.analytics.core.ai.AiSettingsStorage
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.isSuccess

class HirifyApiClient(
    private val httpClient: HttpClient,
    private val aiSettingsStorage: AiSettingsStorage
) {
    private val baseUrl = "https://api.hirify.me/api/partner/analytics/vacancies/count"

    suspend fun getAnalyticsCount(filter: VacancyFilter): Result<CountResponse> {
        val apiKey = aiSettingsStorage.loadConfig().hirifyApiKey.trim()
        if (apiKey.isBlank()) {
            return Result.failure(Exception("Hirify API key is not configured in AI Settings"))
        }

        return try {
            val response = httpClient.get(baseUrl) {
                header("X-API-Key", apiKey)
                
                filter.dateFrom?.let { parameter("date_from", it) }
                filter.dateTo?.let { parameter("date_to", it) }
                filter.dateField?.let { parameter("date_field", it) }
                filter.status?.let { parameter("status", it) }
                filter.includeScam?.let { parameter("include_scam", it) }
                filter.macroregion?.let { parameter("macroregion", it) }
                filter.remoteType?.let { parameter("remote_type", it) }
                filter.source?.let { parameter("source", it) }
                filter.sourceSecondary?.let { parameter("source_secondary", it) }
                filter.specializations?.let { parameter("specializations", it) }
                filter.grade?.let { parameter("grade", it) }
                filter.skills?.let { parameter("skills", it) }
                filter.skillsMatchType?.let { parameter("skills_match_type", it) }
                filter.title?.let { parameter("title", it) }
                filter.companyType?.let { parameter("company_type", it) }
                filter.employeeType?.let { parameter("employee_type", it) }
                filter.workFormat?.let { parameter("work_format", it) }
                filter.vacancyLanguage?.let { parameter("vacancy_language", it) }
                filter.englishLevel?.let { parameter("english_level", it) }
                filter.country?.let { parameter("country", it) }
                filter.groupBy?.let { parameter("group_by", it) }
            }
            println("API Request URL: ${response.call.request.url}")
            
            if (response.status.isSuccess()) {
                val countResponse = response.body<CountResponse>()
                Result.success(countResponse)
            } else {
                Result.failure(Exception("API returned error: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchDictionary(dictionary: String, query: String): Result<List<DictionaryItem>> {
        val apiKey = aiSettingsStorage.loadConfig().hirifyApiKey.trim()
        if (apiKey.isBlank()) {
            return Result.failure(Exception("Hirify API key is not configured in AI Settings"))
        }

        return try {
            val response = httpClient.get("https://api.hirify.me/api/partner/dictionary/$dictionary") {
                header("X-API-Key", apiKey)
                if (query.isNotBlank()) {
                    parameter("search", query)
                }
                parameter("limit", 20)
            }

            if (response.status.isSuccess()) {
                val dictResponse = response.body<DictionaryResponse>()
                Result.success(dictResponse.data)
            } else {
                Result.failure(Exception("API returned error: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
