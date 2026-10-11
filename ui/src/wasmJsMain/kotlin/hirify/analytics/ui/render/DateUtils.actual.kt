package hirify.analytics.ui.render

private fun jsCurrentYear(): Int = js("new Date().getFullYear()")
private fun jsCurrentMonth(): Int = js("new Date().getMonth() + 1")
private fun jsCurrentDay(): Int = js("new Date().getDate()")

/** Day 0 of the next month is the last day of the current month. */
private fun jsDaysInMonth(year: Int, month: Int): Int = js("new Date(year, month, 0).getDate()")

actual fun getCurrentDateInfo(): DateInfo {
    val year = jsCurrentYear()
    val month = jsCurrentMonth()
    return DateInfo(year, month, jsCurrentDay(), jsDaysInMonth(year, month))
}
