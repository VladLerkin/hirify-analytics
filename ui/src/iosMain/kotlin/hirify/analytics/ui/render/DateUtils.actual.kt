package hirify.analytics.ui.render

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.Foundation.*

@OptIn(ExperimentalForeignApi::class)
actual fun getCurrentDateInfo(): DateInfo {
    val date = NSDate()
    val calendar = NSCalendar.currentCalendar
    val components = calendar.components(NSCalendarUnitYear or NSCalendarUnitMonth or NSCalendarUnitDay, fromDate = date)
    val year = components.year.toInt()
    val month = components.month.toInt()
    val day = components.day.toInt()
    val range = calendar.rangeOfUnit(NSCalendarUnitDay, inUnit = NSCalendarUnitMonth, forDate = date)
    return DateInfo(year, month, day, range.useContents { length.toInt() })
}
