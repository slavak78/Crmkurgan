package calendar.adapters

import android.content.Context
import android.widget.ArrayAdapter
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import ru.crmkurgan.main.R
import android.graphics.Typeface
import android.view.View
import android.widget.ImageView
import calendar.CalendarView
import calendar.EventDay
import calendar.utils.*
import com.annimon.stream.Stream
import java.util.*

internal class CalendarDayAdapter(
    private val mCalendarPageAdapter: CalendarPageAdapter,
    context: Context?,
    private val mCalendarProperties: CalendarProperties,
    dates: ArrayList<Date?>?,
    pageMonth: Int
) : ArrayAdapter<Date?>(
    context!!, mCalendarProperties.itemLayoutResource, dates!!
) {
    private val mLayoutInflater: LayoutInflater
    private val mPageMonth: Int
    private val mToday = DateUtils.calendar
    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        var view1 = view
        if (view1 == null) {
            view1 = mLayoutInflater.inflate(mCalendarProperties.itemLayoutResource, parent, false)
        }
        val dayLabel = view1?.findViewById<TextView>(R.id.dayLabel)
        val dayIcon = view?.findViewById<ImageView>(R.id.dayIcon)
        val day: Calendar = GregorianCalendar()
        day.time = getItem(position)!!
        dayIcon?.let { loadIcon(it, day) }
        dayLabel?.let { setLabelColors(it, day) }
        dayLabel?.text = day[Calendar.DAY_OF_MONTH].toString()
        return view1!!
    }

    private fun setLabelColors(dayLabel: TextView, day: Calendar) {
        val mProperties = mCalendarProperties
        if (isCurrentMonthDay(day)) {
            DayColorsUtils.setDayColors(
                dayLabel, mProperties.anotherMonthsDaysLabelsColor,
                Typeface.NORMAL, R.drawable.background_transparent
            )
            return
        }
        if (isSelectedDay(day)) {
            Stream.of(mCalendarPageAdapter.selectedDays)
                .filter { selectedDay: SelectedDay -> selectedDay.calendar == day }
                .findFirst().ifPresent { selectedDay: SelectedDay -> selectedDay.view = dayLabel }
            DayColorsUtils.setSelectedDayColors(dayLabel, mProperties)
            return
        }

        // Setting disabled days color
        if (isActiveDay(day)) {
            DayColorsUtils.setDayColors(
                dayLabel, mProperties.disabledDaysLabelsColor,
                Typeface.NORMAL, R.drawable.background_transparent
            )
            return
        }

        // Setting current month day color
        DayColorsUtils.setCurrentMonthDayColors(day, mToday, dayLabel, mProperties)
    }

    private fun isSelectedDay(day: Calendar): Boolean {
        return mCalendarProperties.calendarType != CalendarView.CLASSIC && day[Calendar.MONTH] == mPageMonth && mCalendarPageAdapter.selectedDays.contains(
            SelectedDay(day)
        )
    }

    private fun isCurrentMonthDay(day: Calendar): Boolean {
        val mProperties = mCalendarProperties
        return day[Calendar.MONTH] != mPageMonth ||
                (mProperties.minimumDate != null && day.before(mProperties.minimumDate)
                        || mProperties.maximumDate != null && day.after(mProperties.maximumDate))
    }

    private fun isActiveDay(day: Calendar): Boolean {
        return mCalendarProperties.disabledDays?.contains(day) == true
    }

    private fun loadIcon(dayIcon: ImageView, day: Calendar) {
        val mProperties = mCalendarProperties
        if (!mProperties.eventsEnabled) {
            dayIcon.visibility = View.GONE
            return
        }
        Stream.of(mProperties.eventDays)
            .filter { eventDate: EventDay -> eventDate.calendar == day }
            .findFirst().executeIfPresent { eventDay: EventDay ->
                ImageUtils.loadImage(dayIcon, eventDay.imageDrawable)
                if (isCurrentMonthDay(day) || isActiveDay(day)) {
                    dayIcon.alpha = 0.12f
                }
            }
        val runnable = Runnable {}
        Stream.of(mProperties.eventDays)
            .filter { eventDate: EventDay -> eventDate.calendar == day }
            .findFirst().executeIfAbsent(runnable)
    }

    init {
        mPageMonth = if (pageMonth < 0) 11 else pageMonth
        mLayoutInflater = LayoutInflater.from(context)
    }
}