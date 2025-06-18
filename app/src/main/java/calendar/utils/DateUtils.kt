package calendar.utils

import android.content.Context
import com.annimon.stream.Stream
import ru.crmkurgan.main.R
import java.util.*
import java.util.concurrent.TimeUnit

object DateUtils {
    @JvmStatic
    val calendar: Calendar
        get() {
            val calendar = Calendar.getInstance()
            setMidnight(calendar)
            return calendar
        }

    @JvmStatic
    fun setMidnight(calendar: Calendar?) {
        calendar?.let {
            it[Calendar.HOUR_OF_DAY] = 0
            it[Calendar.MINUTE] = 0
            it[Calendar.SECOND] = 0
            it[Calendar.MILLISECOND] = 0
        }
    }

    @JvmStatic
    fun isMonthBefore(firstCalendar: Calendar?, secondCalendar: Calendar): Boolean {
        if (firstCalendar == null) {
            return false
        }
        val firstDay = firstCalendar.clone() as Calendar
        setMidnight(firstDay)
        val dayOfMonth = Calendar.DAY_OF_MONTH
        firstDay[dayOfMonth] = 1
        val secondDay = secondCalendar.clone() as Calendar
        setMidnight(secondDay)
        secondDay[dayOfMonth] = 1
        return secondDay.before(firstDay)
    }

    @JvmStatic
    fun isMonthAfter(firstCalendar: Calendar?, secondCalendar: Calendar): Boolean {
        if (firstCalendar == null) {
            return false
        }
        val firstDay = firstCalendar.clone() as Calendar
        val dayOfMonth = Calendar.DAY_OF_MONTH
        setMidnight(firstDay)
        firstDay[dayOfMonth] = 1
        val secondDay = secondCalendar.clone() as Calendar
        setMidnight(secondDay)
        secondDay[dayOfMonth] = 1
        return secondDay.after(firstDay)
    }

    @JvmStatic
    fun getMonthAndYearDate(context: Context, calendar: Calendar): String {
        return String.format(
            "%s  %s",
            context.resources.getStringArray(R.array.material_calendar_months_array)[calendar[Calendar.MONTH]],
            calendar[Calendar.YEAR]
        )
    }

    @JvmStatic
    fun getMonthsBetweenDates(startCalendar: Calendar, endCalendar: Calendar): Int {
        val year = Calendar.YEAR
        val month = Calendar.MONTH
        val years = endCalendar[year] - startCalendar[year]
        return years * 12 + endCalendar[month] - startCalendar[month]
    }

    private fun getDaysBetweenDates(startCalendar: Calendar, endCalendar: Calendar): Long {
        val milliseconds = endCalendar.timeInMillis - startCalendar.timeInMillis
        return TimeUnit.MILLISECONDS.toDays(milliseconds)
    }

    fun isFullDatesRange(days: List<Calendar>): Boolean {
        val listSize = days.size
        if (days.isEmpty() || listSize == 1) {
            return true
        }
        val sortedCalendars = Stream.of(days).sortBy { obj: Calendar -> obj.timeInMillis }
            .toList()
        return listSize.toLong() == getDaysBetweenDates(
            sortedCalendars[0],
            sortedCalendars[listSize - 1]
        ) + 1
    }
}