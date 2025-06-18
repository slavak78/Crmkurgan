package calendar.utils

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import calendar.CalendarView.Companion.ONE_DAY_PICKER
import calendar.CalendarView.Companion.RANGE_PICKER
import calendar.EventDay
import ru.crmkurgan.main.R
import calendar.exceptions.UnsupportedMethodsException
import calendar.exceptions.ErrorsMessages
import calendar.listeners.*
import com.annimon.stream.Stream
import java.util.*
import kotlin.collections.ArrayList

class CalendarProperties(private val mContext: Context) {
    var calendarType = 0
    private var mHeaderColor = 0
    private var mHeaderLabelColor = 0
    private var mSelectionColor = 0
    private var mTodayLabelColor = 0
    var dialogButtonsColor = 0
    var itemLayoutResource = 0
    private var mDisabledDaysLabelsColor = 0
    var pagesColor = 0
    var abbreviationsBarColor = 0
    var abbreviationsLabelsColor = 0
    private var mDaysLabelsColor = 0
    private var mSelectionLabelColor = 0
    private var mAnotherMonthsDaysLabelsColor = 0
    var headerVisibility = 0
    var eventsEnabled = false
    var previousButtonSrc: Drawable? = null
    var forwardButtonSrc: Drawable? = null
    val firstPageCalendarDate: Calendar = DateUtils.calendar
    var calendar: Calendar? = null
    var minimumDate: Calendar? = null
    var maximumDate: Calendar? = null
    var onDayClickListener: OnDayClickListener? = null
    private var mOnDateLongClickListener: OnDateLongClickListener? = null
    var onSelectDateListener: OnSelectDateListener? = null
    var onSelectionAbilityListener: OnSelectionAbilityListener? = null
    var onPreviousPageChangeListener: OnCalendarPageChangeListener? = null
    var onForwardPageChangeListener: OnCalendarPageChangeListener? = null
    var eventDays: List<EventDay> = ArrayList()
    private var mDisabledDays: List<Calendar> = ArrayList()
    private var mSelectedDays: MutableList<SelectedDay> = ArrayList()
    var headerColor: Int
        get() = if (mHeaderColor <= 0) {
            mHeaderColor
        } else ContextCompat.getColor(mContext, mHeaderColor)
        set(headerColor) {
            mHeaderColor = headerColor
        }
    var headerLabelColor: Int
        get() = if (mHeaderLabelColor <= 0) {
            mHeaderLabelColor
        } else ContextCompat.getColor(
            mContext,
            mHeaderLabelColor
        )
        set(headerLabelColor) {
            mHeaderLabelColor = headerLabelColor
        }
    var selectionColor: Int
        get() = if (mSelectionColor == 0) {
            ContextCompat.getColor(mContext, R.color.defaultColor)
        } else mSelectionColor
        set(selectionColor) {
            mSelectionColor = selectionColor
        }
    var todayLabelColor: Int
        get() = if (mTodayLabelColor == 0) {
            ContextCompat.getColor(mContext, R.color.defaultColor)
        } else mTodayLabelColor
        set(todayLabelColor) {
            mTodayLabelColor = todayLabelColor
        }

    fun getOnDateLongClickListener(): OnDateLongClickListener? {
        return mOnDateLongClickListener
    }

    fun setOnDateLongClickListener(onDateLongClickListener: OnDateLongClickListener?) {
        mOnDateLongClickListener = onDateLongClickListener
    }

    var disabledDays: Collection<Calendar>?
        get() = mDisabledDays
        set(disabledDays) {
            mSelectedDays.removeAll(disabledDays)
            mDisabledDays = Stream.of<Calendar>(disabledDays)
                .map { calendar: Calendar ->
                    DateUtils.setMidnight(calendar)
                    calendar
                }.toList()
        }
    val selectedDays: List<SelectedDay>
        get() = mSelectedDays

    fun setSelectedDay(calendar: Calendar?) {
        setSelectedDay(SelectedDay(calendar))
    }

    fun setSelectedDay(selectedDay: SelectedDay) {
        mSelectedDays.clear()
        mSelectedDays.add(selectedDay)
    }

    fun setSelectedDays(selectedDays: List<Calendar>) {
        val type = calendarType
        if (type == ONE_DAY_PICKER) {
            throw UnsupportedMethodsException(ErrorsMessages.ONE_DAY_PICKER_MULTIPLE_SELECTION)
        }
        if (type == RANGE_PICKER && !DateUtils.isFullDatesRange(
                selectedDays
            )
        ) {
            throw UnsupportedMethodsException(ErrorsMessages.RANGE_PICKER_NOT_RANGE)
        }
        mSelectedDays = Stream.of(selectedDays)
            .map { calendar: Calendar ->
                DateUtils.setMidnight(calendar)
                SelectedDay(calendar)
            }.filterNot { value: SelectedDay -> mDisabledDays.contains(value.calendar) }
            .toList()
    }

    var disabledDaysLabelsColor: Int
        get() = if (mDisabledDaysLabelsColor == 0) {
            ContextCompat.getColor(mContext, R.color.nextMonthDayColor)
        } else mDisabledDaysLabelsColor
        set(disabledDaysLabelsColor) {
            mDisabledDaysLabelsColor = disabledDaysLabelsColor
        }
    var daysLabelsColor: Int
        get() = if (mDaysLabelsColor == 0) {
            ContextCompat.getColor(mContext, R.color.currentMonthDayColor)
        } else mDaysLabelsColor
        set(daysLabelsColor) {
            mDaysLabelsColor = daysLabelsColor
        }
    var selectionLabelColor: Int
        get() = if (mSelectionLabelColor == 0) {
            ContextCompat.getColor(mContext, android.R.color.white)
        } else mSelectionLabelColor
        set(selectionLabelColor) {
            mSelectionLabelColor = selectionLabelColor
        }
    var anotherMonthsDaysLabelsColor: Int
        get() = if (mAnotherMonthsDaysLabelsColor == 0) {
            ContextCompat.getColor(mContext, R.color.nextMonthDayColor)
        } else mAnotherMonthsDaysLabelsColor
        set(anotherMonthsDaysLabelsColor) {
            mAnotherMonthsDaysLabelsColor = anotherMonthsDaysLabelsColor
        }

    companion object {
        const val CALENDAR_SIZE = 2401
        const val FIRST_VISIBLE_PAGE = CALENDAR_SIZE / 2
    }
}