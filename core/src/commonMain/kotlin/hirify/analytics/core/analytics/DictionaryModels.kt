package hirify.analytics.core.analytics

import kotlinx.serialization.Serializable

@Serializable
data class DictionaryItem(
    val code: String,
    val name: String? = null,
    val name_en: String? = null
)

@Serializable
data class DictionaryResponse(
    val data: List<DictionaryItem>
)
