package calendar.listeners

import android.view.View

import calendar.adapters.CalendarPageAdapter
import calendar.utils.CalendarProperties
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.AdapterView
import calendar.utils.SelectedDay
import android.widget.TextView
import calendar.CalendarUtils
import calendar.CalendarView
import ru.crmkurgan.main.R
import calendar.utils.DayColorsUtils
import calendar.EventDay
import calendar.utils.DateUtils
import com.annimon.stream.Stream
import java.util.*

class DayRowLongClickListener(
    private val mCalendarPageAdapter: CalendarPageAdapter,
    private val mCalendarProperties: CalendarProperties,
    pageMonth: Int
) : OnItemLongClickListener {
    private val mPageMonth: Int
    override fun onItemLongClick(
        adapterView: AdapterView<*>,
        view: View,
        position: Int,
        id: Long
    ): Boolean {
        val day: Calendar = GregorianCalendar()
        day.time = adapterView.getItemAtPosition(position) as Date
        if (mCalendarProperties.getOnDateLongClickListener() != null) {
            onLongClick(day)
        }
        when (mCalendarProperties.calendarType) {
            CalendarView.ONE_DAY_PICKER -> selectOneDay(view, day)
            CalendarView.MANY_DAYS_PICKER -> selectManyDays(view, day)
            CalendarView.RANGE_PICKER -> selectRange(view, day)
            CalendarView.CLASSIC -> mCalendarPageAdapter.selectedDay = SelectedDay(view, day)
        }
        return true
    }

    private fun selectOneDay(view: View, day: Calendar) {
        val previousSelectedDay = mCalendarPageAdapter.selectedDay
        val dayLabel = view.findViewById<TextView>(R.id.dayLabel)
        if (isAnotherDaySelected(previousSelectedDay, day)) {
            selectDay(dayLabel, day)
            reverseUnselectedColor(previousSelectedDay)
        }
    }

    private fun selectManyDays(view: View, day: Calendar) {
        val dayLabel = view.findViewById<TextView>(R.id.dayLabel)
        val adapter = mCalendarPageAdapter
        if (isCurrentMonthDay(day) && isActiveDay(day)) {
            val selectedDay = SelectedDay(dayLabel, day)
            if (!adapter.selectedDays.contains(selectedDay)) {
                DayColorsUtils.setSelectedDayColors(dayLabel, mCalendarProperties)
            } else {
                reverseUnselectedColor(selectedDay)
            }
            adapter.addSelectedDay(selectedDay)
        }
    }

    private fun selectRange(view: View, day: Calendar) {
        val dayLabel = view.findViewById<TextView>(R.id.dayLabel)
        if (!isCurrentMonthDay(day) || !isActiveDay(day)) {
            return
        }
        val selectedDays = mCalendarPageAdapter.selectedDays
        val size = selectedDays.size
        if (size > 1) {
            clearAndSelectOne(dayLabel, day)
        }
        if (size == 1) {
            selectOneAndRange(dayLabel, day)
        }
        if (selectedDays.isEmpty()) {
            selectDay(dayLabel, day)
        }
    }

    private fun clearAndSelectOne(dayLabel: TextView, day: Calendar) {
        Stream.of(mCalendarPageAdapter.selectedDays)
            .forEach { selectedDay: SelectedDay? -> reverseUnselectedColor(selectedDay) }
        selectDay(dayLabel, day)
    }

    private fun selectOneAndRange(dayLabel: TextView, day: Calendar) {
        val adapter = mCalendarPageAdapter
        val properties = mCalendarProperties
        val previousSelectedDay = adapter.selectedDay
        Stream.of(
            CalendarUtils.getDatesRange(
                previousSelectedDay?.calendar, day
            )
        )
            .filter { calendar: Calendar? -> !properties.disabledDays.contains(calendar) }
            .forEach { calendar: Calendar? ->
                adapter.addSelectedDay(
                    SelectedDay(
                        calendar
                    )
                )
            }
        DayColorsUtils.setSelectedDayColors(dayLabel, properties)
        adapter.addSelectedDay(SelectedDay(dayLabel, day))
        adapter.notifyDataSetChanged()
    }

    private fun selectDay(dayLabel: TextView, day: Calendar) {
        DayColorsUtils.setSelectedDayColors(dayLabel, mCalendarProperties)
        mCalendarPageAdapter.selectedDay = SelectedDay(dayLabel, day)
    }

    private fun reverseUnselectedColor(selectedDay: SelectedDay?) {
        DayColorsUtils.setCurrentMonthDayColors(
            selectedDay?.calendar,
            DateUtils.calendar, selectedDay?.view as TextView, mCalendarProperties
        )
    }

    private fun isCurrentMonthDay(day: Calendar): Boolean {
        return day[Calendar.MONTH] == mPageMonth && isBetweenMinAndMax(day)
    }

    private fun isActiveDay(day: Calendar): Boolean {
        return !mCalendarProperties.disabledDays.contains(day)
    }

    private fun isBetweenMinAndMax(day: Calendar): Boolean {
        val min = mCalendarProperties.minimumDate
        val max = mCalendarProperties.maximumDate
        return !(min != null && day.before(min)
                || max != null && day.after(max))
    }

    private fun isAnotherDaySelected(selectedDay: SelectedDay?, day: Calendar): Boolean {
        return (selectedDay != null && day != selectedDay.calendar
                && isCurrentMonthDay(day) && isActiveDay(day))
    }

    private fun onLongClick(day: Calendar) {
        val days = mCalendarProperties.eventDays
        if (days == null) {
            createEmptyEventDay(day)
            return
        }
        Stream.of(days)
            .filter { eventDate: EventDay -> eventDate.calendar == day }
            .findFirst()
            .ifPresentOrElse({ eventDay: EventDay -> callOnLongClickListener(eventDay) }) {
                createEmptyEventDay(
                    day
                )
            }
    }

    private fun createEmptyEventDay(day: Calendar) {
        val eventDay = EventDay(day)
        callOnLongClickListener(eventDay)
    }

    private fun callOnLongClickListener(eventDay: EventDay) {
        val properties = mCalendarProperties
        val enabledDay = (properties.disabledDays.contains(eventDay.calendar)
                || !isBetweenMinAndMax(eventDay.calendar))
        eventDay.isEnabled = enabledDay
        properties.getOnDateLongClickListener().onDateLongClick(eventDay)
    }

    init {
        mPageMonth = if (pageMonth < 0) 11 else pageMonth
    }
}