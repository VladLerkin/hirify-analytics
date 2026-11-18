package hirify.analytics.core.analytics

import kotlinx.serialization.Serializable

@Serializable
enum class ChartPeriod(val months: Int?, val storageKey: String) {
    ALL(null, "all"),
    LAST_2_MONTHS(2, "2_months"),
    LAST_3_MONTHS(3, "3_months"),
    LAST_6_MONTHS(6, "6_months"),
    LAST_12_MONTHS(12, "12_months");

    companion object {
        fun fromStorageKey(key: String?): ChartPeriod =
            entries.firstOrNull { it.storageKey == key } ?: ALL
    }
}
