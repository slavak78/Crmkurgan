package calendar.adapters

import calendar.utils.CalendarProperties
import androidx.viewpager.widget.PagerAdapter
import calendar.extensions.CalendarGridView
import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import ru.crmkurgan.main.R
import calendar.listeners.DayRowClickListener
import calendar.utils.SelectedDay
import java.util.*

class CalendarPageAdapter(
    private val mContext: Context,
    private val mCalendarProperties: CalendarProperties
) : PagerAdapter() {
    private var mCalendarGridView: CalendarGridView? = null
    private var mPageMonth = 0
    override fun getCount(): Int {
        return CalendarProperties.CALENDAR_SIZE
    }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    @SuppressLint("InflateParams")
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mCalendarGridView = inflater.inflate(R.layout.calendar_view_grid, null) as CalendarGridView
        val gridView = mCalendarGridView
        loadMonth(position)
        gridView?.onItemClickListener = DayRowClickListener(
            this,
            mCalendarProperties, mPageMonth
        )
        container.addView(gridView)
        return gridView!!
    }

    fun addSelectedDay(selectedDay: SelectedDay?) {
        val selected = mCalendarProperties.selectedDays
        if (!selected.contains(selectedDay)) {
            selected.add(selectedDay)
            informDatePicker()
            return
        }
        selected.remove(selectedDay)
        informDatePicker()
    }

    val selectedDays: List<SelectedDay>
        get() = mCalendarProperties.selectedDays
    var selectedDay: SelectedDay?
        get() = mCalendarProperties.selectedDays[0]
        set(selectedDay) {
            mCalendarProperties.setSelectedDay(selectedDay)
            informDatePicker()
        }

    private fun informDatePicker() {
        val listener = mCalendarProperties.onSelectionAbilityListener
            listener?.onChange(mCalendarProperties.selectedDays.size > 0)
    }

    private fun loadMonth(position: Int) {
        val days = ArrayList<Date?>()

        // Get Calendar object instance
        val calendar = mCalendarProperties.firstPageCalendarDate.clone() as Calendar

        // Add months to Calendar (a number of months depends on ViewPager position)
        calendar.add(Calendar.MONTH, position)

        // Set day of month as 1
        calendar[Calendar.DAY_OF_MONTH] = 1

        // Get a number of the first day of the week
        val dayOfWeek = calendar[Calendar.DAY_OF_WEEK]

        // Count when month is beginning
        val firstDayOfWeek = calendar.firstDayOfWeek
        val monthBeginningCell =
            (if (dayOfWeek < firstDayOfWeek) 7 else 0) + dayOfWeek - firstDayOfWeek

        // Subtract a number of beginning days, it will let to load a part of a previous month
        calendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell)

        /*
        Get all days of one page (42 is a number of all possible cells in one page
        (a part of previous month, current month and a part of next month))
         */while (days.size < 42) {
            days.add(calendar.time)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        mPageMonth = calendar[Calendar.MONTH] - 1
        val calendarDayAdapter = CalendarDayAdapter(
            this, mContext,
            mCalendarProperties, days, mPageMonth
        )
        mCalendarGridView?.adapter = calendarDayAdapter
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    init {
        informDatePicker()
    }
}