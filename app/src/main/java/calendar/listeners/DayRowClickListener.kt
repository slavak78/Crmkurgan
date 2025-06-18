package calendar.listeners

import android.view.View
import calendar.adapters.CalendarPageAdapter
import calendar.utils.CalendarProperties
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

class DayRowClickListener(
    private val mCalendarPageAdapter: CalendarPageAdapter,
    private val mCalendarProperties: CalendarProperties,
    pageMonth: Int
) : AdapterView.OnItemClickListener {
    private val mPageMonth: Int
    override fun onItemClick(adapterView: AdapterView<*>, view: View, position: Int, id: Long) {
        val day: Calendar = GregorianCalendar()
        day.time = adapterView.getItemAtPosition(position) as Date
        val properties = mCalendarProperties
        if (properties.onDayClickListener != null) {
            onClick(day)
        }
        when (properties.calendarType) {
            CalendarView.ONE_DAY_PICKER -> selectOneDay(view, day)
            CalendarView.MANY_DAYS_PICKER -> selectManyDays(view, day)
            CalendarView.RANGE_PICKER -> selectRange(view, day)
            CalendarView.CLASSIC -> mCalendarPageAdapter.selectedDay = SelectedDay(view, day)
        }
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
        if (isCurrentMonthDay(day) && isActiveDay(day)) {
            val selectedDay = SelectedDay(dayLabel, day)
            val adapter = mCalendarPageAdapter
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
        val sel = selectedDays.size
        if (sel > 1) {
            clearAndSelectOne(dayLabel, day)
        }
        if (sel == 1) {
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
        val previousSelectedDay = adapter.selectedDay
        val properties = mCalendarProperties
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

    private fun onClick(day: Calendar) {
        val propertiesDays = mCalendarProperties.eventDays
        if (propertiesDays == null) {
            createEmptyEventDay(day)
            return
        }
        Stream.of(propertiesDays)
            .filter { eventDate: EventDay -> eventDate.calendar == day }
            .findFirst()
            .ifPresentOrElse({ eventDay: EventDay -> callOnClickListener(eventDay) }) {
                createEmptyEventDay(
                    day
                )
            }
    }

    private fun createEmptyEventDay(day: Calendar) {
        val eventDay = EventDay(day)
        callOnClickListener(eventDay)
    }

    private fun callOnClickListener(eventDay: EventDay) {
        val properties = mCalendarProperties
        val enabledDay = (properties.disabledDays.contains(eventDay.calendar)
                || !isBetweenMinAndMax(eventDay.calendar))
        eventDay.isEnabled = enabledDay
        properties.onDayClickListener.onDayClick(eventDay)
    }

    init {
        mPageMonth = if (pageMonth < 0) 11 else pageMonth
    }
}