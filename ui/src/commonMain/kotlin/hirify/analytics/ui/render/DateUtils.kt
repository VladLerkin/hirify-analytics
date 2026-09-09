package hirify.analytics.ui.render

data class DateInfo(val year: Int, val month: Int, val dayOfMonth: Int, val daysInMonth: Int)

expect fun getCurrentDateInfo(): DateInfo
