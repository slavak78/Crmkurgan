package calendar

import android.content.Context
import calendar.utils.CalendarProperties.calendar
import calendar.utils.DateUtils.isMonthAfter
import calendar.utils.DateUtils.calendar
import calendar.utils.DateUtils.isMonthBefore
import calendar.utils.CalendarProperties
import androidx.appcompat.widget.AppCompatButton
import android.view.LayoutInflater
import android.view.View
import ru.crmkurgan.main.R
import calendar.listeners.OnSelectionAbilityListener
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import calendar.exceptions.OutOfDateRangeException
import com.annimon.stream.Optional
import java.util.*

class DatePicker(
    private val mContext: Context,
    private val mCalendarProperties: CalendarProperties
) {
    private var mCancelButton: AppCompatButton? = null
    private var mOkButton: AppCompatButton? = null
    private var mTodayButton: AppCompatButton? = null
    fun show(): DatePicker {
        val layoutInflater = LayoutInflater.from(mContext)
        val view = layoutInflater.inflate(R.layout.date_picker_dialog, null)
        if (mCalendarProperties.pagesColor != 0) {
            view.setBackgroundColor(mCalendarProperties.pagesColor)
        }
        mCancelButton = view.findViewById(R.id.negative_button)
        mOkButton = view.findViewById(R.id.positive_button)
        mTodayButton = view.findViewById(R.id.today_button)
        setTodayButtonVisibility()
        setDialogButtonsColors()
        setOkButtonState(mCalendarProperties.calendarType == calendar.CalendarView.ONE_DAY_PICKER)
        mCalendarProperties.onSelectionAbilityListener =
            OnSelectionAbilityListener { enabled: Boolean -> setOkButtonState(enabled) }
        val calendarView: CalendarView = calendar.CalendarView(mContext, mCalendarProperties)
        val calendarContainer = view.findViewById<FrameLayout>(R.id.calendarContainer)
        calendarContainer.addView(calendarView)
        Optional.ofNullable(mCalendarProperties.calendar).ifPresent { calendar: Calendar? ->
            try {
                calendarView.setDate(calendar!!)
            } catch (exception: OutOfDateRangeException) {
                exception.printStackTrace()
            }
        }
        val alertBuilder = AlertDialog.Builder(mContext)
        val alertdialog = alertBuilder.create()
        alertdialog.setView(view)
        mCancelButton.setOnClickListener(View.OnClickListener { v: View? -> alertdialog.cancel() })
        mOkButton.setOnClickListener(View.OnClickListener { v: View? ->
            alertdialog.cancel()
            mCalendarProperties.onSelectDateListener!!.onSelect(calendarView.selectedDates)
        })
        mTodayButton.setOnClickListener(View.OnClickListener { v: View? -> calendarView.showCurrentMonthPage() })
        alertdialog.show()
        return this
    }

    private fun setDialogButtonsColors() {
        if (mCalendarProperties.dialogButtonsColor != 0) {
            mCancelButton!!.setTextColor(
                ContextCompat.getColor(
                    mContext,
                    mCalendarProperties.dialogButtonsColor
                )
            )
            mTodayButton!!.setTextColor(
                ContextCompat.getColor(
                    mContext,
                    mCalendarProperties.dialogButtonsColor
                )
            )
        }
    }

    private fun setOkButtonState(enabled: Boolean) {
        mOkButton!!.isEnabled = enabled
        if (mCalendarProperties.dialogButtonsColor != 0) {
            mOkButton!!.setTextColor(
                ContextCompat.getColor(
                    mContext,
                    if (enabled) mCalendarProperties.dialogButtonsColor else R.color.disabledDialogButtonColor
                )
            )
        }
    }

    private fun setTodayButtonVisibility() {
        if (calendar.utils.DateUtils.isMonthAfter(
                mCalendarProperties.maximumDate,
                calendar.utils.DateUtils.calendar
            )
            || calendar.utils.DateUtils.isMonthBefore(
                mCalendarProperties.minimumDate,
                calendar.utils.DateUtils.calendar
            )
        ) {
            mTodayButton!!.visibility = View.GONE
        }
    }
}