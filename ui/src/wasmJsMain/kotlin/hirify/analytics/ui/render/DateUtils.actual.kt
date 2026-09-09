package hirify.analytics.ui.render

import kotlin.js.Date

actual fun getCurrentDateInfo(): DateInfo {
    val date = Date()
    val year = date.getFullYear()
    val month = date.getMonth() + 1
    val day = date.getDate()
    val nextMonth = Date(year, month, 0) // 0th day of next month is the last day of current month
    val daysInMonth = nextMonth.getDate()
    return DateInfo(year, month, day, daysInMonth)
}
