package hirify.analytics.core.analytics

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*

@Serializable
data class VacancyFilter(
    val dateFrom: String? = null,
    val dateTo: String? = null,
    val dateField: String? = "created",
    val status: String? = "all",
    val includeScam: Boolean? = false,
    val macroregion: String? = null,
    val remoteType: String? = null,
    val source: String? = null,
    val sourceSecondary: String? = null,
    val specializations: String? = null,
    val grade: String? = null,
    val skills: String? = null,
    val skillsMatchType: String? = "OR",
    val title: String? = null,
    val companyType: String? = null,
    val employeeType: String? = null,
    val workFormat: String? = null,
    val vacancyLanguage: String? = null,
    val englishLevel: String? = null,
    val country: String? = null,
    val groupBy: String? = "month"
)

@Serializable
data class CountResponse(
    val group_by: String? = null,
    val buckets: JsonElement? = null,
    val total: Int = 0,
    val rows_sum: Int? = null,
    val note: String? = null
) {
    fun getParsedBuckets(): Map<String, Int> {
        val element = buckets ?: return emptyMap()
        println("getParsedBuckets: element type is ${element::class.simpleName}, content: $element")
        if (element is JsonObject) {
            val map = element.mapValues { it.value.jsonPrimitive.intOrNull ?: 0 }
            println("getParsedBuckets: parsed map: $map")
            return map
        }
        println("getParsedBuckets: element is not JsonObject!")
        return emptyMap()
    }
}
