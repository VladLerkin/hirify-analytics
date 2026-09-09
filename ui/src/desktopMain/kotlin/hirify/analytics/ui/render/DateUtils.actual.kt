package hirify.analytics.ui.render

import java.time.LocalDate

actual fun getCurrentDateInfo(): DateInfo {
    val date = LocalDate.now()
    return DateInfo(date.year, date.monthValue, date.dayOfMonth, date.lengthOfMonth())
}
