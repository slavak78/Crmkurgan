package calendar

import android.content.Context
import calendar.utils.AppearanceUtils.setHeaderColor
import calendar.utils.AppearanceUtils.setHeaderVisibility
import calendar.utils.AppearanceUtils.setHeaderLabelColor
import calendar.utils.AppearanceUtils.setAbbreviationsBarColor
import calendar.utils.AppearanceUtils.setAbbreviationsLabels
import calendar.utils.AppearanceUtils.setPagesColor
import calendar.utils.AppearanceUtils.setPreviousButtonImage
import calendar.utils.AppearanceUtils.setForwardButtonImage
import calendar.utils.DateUtils.setMidnight
import calendar.utils.DateUtils.isMonthBefore
import calendar.utils.DateUtils.isMonthAfter
import calendar.utils.DateUtils.getMonthAndYearDate
import calendar.utils.DateUtils.getMonthsBetweenDates
import calendar.utils.DateUtils.calendar
import android.widget.LinearLayout
import calendar.adapters.CalendarPageAdapter
import android.widget.TextView
import calendar.extensions.CalendarViewPager
import calendar.utils.CalendarProperties
import android.view.LayoutInflater
import ru.crmkurgan.main.R
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.annotation.ColorRes
import android.widget.ImageButton
import calendar.listeners.OnCalendarPageChangeListener
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import calendar.listeners.OnDateLongClickListener
import kotlin.Throws
import calendar.exceptions.OutOfDateRangeException
import calendar.exceptions.ErrorsMessages
import calendar.listeners.OnDayClickListener
import calendar.utils.SelectedDay
import com.annimon.stream.Stream
import java.util.*

class CalendarView : LinearLayout {
    private var mContext: Context? = null
    private var mCalendarPageAdapter: CalendarPageAdapter? = null
    private var mCurrentMonthLabel: TextView? = null
    private var mCurrentPage = 0
    private var mViewPager: CalendarViewPager? = null
    private var mCalendarProperties: CalendarProperties? = null

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initControl(context, attrs)
        initCalendar()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initControl(context, attrs)
        initCalendar()
    }

    constructor(context: Context?, calendarProperties: CalendarProperties?) : super(context) {
        mContext = context
        mCalendarProperties = calendarProperties
        val inflater =
            context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.calendar_view, this)
        initUiElements()
        initAttributes()
        initCalendar()
    }

    private fun initControl(context: Context, attrs: AttributeSet?) {
        mContext = context
        mCalendarProperties = CalendarProperties(context)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.calendar_view, this)
        initUiElements()
        setAttributes(attrs)
    }

    private fun setAttributes(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CalendarView)
        try {
            initCalendarProperties(typedArray)
            initAttributes()
        } finally {
            typedArray.recycle()
        }
    }

    private fun initCalendarProperties(typedArray: TypedArray) {
        val headerColor = typedArray.getColor(R.styleable.CalendarView_headerColor, 0)
        val properties = mCalendarProperties
        properties?.headerColor = headerColor
        val headerLabelColor = typedArray.getColor(R.styleable.CalendarView_headerLabelColor, 0)
        properties?.headerLabelColor = headerLabelColor
        val abbreviationsBarColor =
            typedArray.getColor(R.styleable.CalendarView_abbreviationsBarColor, 0)
        properties?.abbreviationsBarColor = abbreviationsBarColor
        val abbreviationsLabelsColor =
            typedArray.getColor(R.styleable.CalendarView_abbreviationsLabelsColor, 0)
        properties?.abbreviationsLabelsColor = abbreviationsLabelsColor
        val pagesColor = typedArray.getColor(R.styleable.CalendarView_pagesColor, 0)
        properties?.pagesColor = pagesColor
        val daysLabelsColor = typedArray.getColor(R.styleable.CalendarView_daysLabelsColor, 0)
        properties?.daysLabelsColor = daysLabelsColor
        val anotherMonthsDaysLabelsColor =
            typedArray.getColor(R.styleable.CalendarView_anotherMonthsDaysLabelsColor, 0)
        properties?.anotherMonthsDaysLabelsColor = anotherMonthsDaysLabelsColor
        val todayLabelColor = typedArray.getColor(R.styleable.CalendarView_todayLabelColor, 0)
        properties?.todayLabelColor = todayLabelColor
        val selectionColor = typedArray.getColor(R.styleable.CalendarView_selectionColor, 0)
        properties?.selectionColor = selectionColor
        val selectionLabelColor =
            typedArray.getColor(R.styleable.CalendarView_selectionLabelColor, 0)
        properties?.selectionLabelColor = selectionLabelColor
        val disabledDaysLabelsColor =
            typedArray.getColor(R.styleable.CalendarView_disabledDaysLabelsColor, 0)
        properties?.disabledDaysLabelsColor = disabledDaysLabelsColor
        val calendarType = typedArray.getInt(
            R.styleable.CalendarView_type,
            CLASSIC
        )
        properties?.calendarType = calendarType

        // Set picker mode !DEPRECATED!
        if (typedArray.getBoolean(R.styleable.CalendarView_datePicker, false)) {
            properties?.calendarType = ONE_DAY_PICKER
        }
        val eventsEnabled = typedArray.getBoolean(
            R.styleable.CalendarView_eventsEnabled,
            properties?.calendarType == CLASSIC
        )
        properties?.eventsEnabled = eventsEnabled
        val previousButtonSrc = typedArray.getDrawable(R.styleable.CalendarView_previousButtonSrc)
        properties?.previousButtonSrc = previousButtonSrc
        val forwardButtonSrc = typedArray.getDrawable(R.styleable.CalendarView_forwardButtonSrc)
        properties?.forwardButtonSrc = forwardButtonSrc
    }

    private fun initAttributes() {
        val view = rootView
        val properties = mCalendarProperties
        properties?.headerColor?.let { setHeaderColor(view, it) }
        properties?.headerVisibility?.let { setHeaderVisibility(view, it) }
        properties?.headerLabelColor?.let { setHeaderLabelColor(view, it) }
        properties?.abbreviationsBarColor?.let { setAbbreviationsBarColor(view, it) }
        properties?.abbreviationsLabelsColor?.let {
            setAbbreviationsLabels(
                view, it,
                properties.firstPageCalendarDate.firstDayOfWeek
            )
        }
        properties?.pagesColor?.let { setPagesColor(view, it) }
        setPreviousButtonImage(view, properties?.previousButtonSrc)
        setForwardButtonImage(view, properties?.forwardButtonSrc)
        setCalendarRowLayout()
    }

    fun setHeaderColor(@ColorRes color: Int) {
        val properties = mCalendarProperties
        properties?.headerColor = color
        setHeaderColor(rootView, color)
    }

    fun setHeaderVisibility(visibility: Int) {
        val properties = mCalendarProperties
        properties?.headerVisibility = visibility
        setHeaderVisibility(rootView, visibility)
    }

    fun setHeaderLabelColor(@ColorRes color: Int) {
        val properties = mCalendarProperties
        properties?.headerLabelColor = color
        setHeaderLabelColor(rootView, color)
    }

    fun setPreviousButtonImage(drawable: Drawable?) {
        val properties = mCalendarProperties
        properties?.previousButtonSrc = drawable
        setPreviousButtonImage(rootView, drawable)
    }

    fun setForwardButtonImage(drawable: Drawable?) {
        val properties = mCalendarProperties
        properties?.forwardButtonSrc = drawable
        setForwardButtonImage(rootView, drawable)
    }

    private fun setCalendarRowLayout() {
        val properties = mCalendarProperties
        if (properties?.eventsEnabled == true) {
            properties.itemLayoutResource = R.layout.calendar_view_day
        } else {
            properties?.itemLayoutResource = R.layout.calendar_view_picker_day
        }
    }

    private fun initUiElements() {
        val forwardButton = findViewById<ImageButton>(R.id.forwardButton)
        forwardButton.setOnClickListener(onNextClickListener)
        val previousButton = findViewById<ImageButton>(R.id.previousButton)
        previousButton.setOnClickListener(onPreviousClickListener)
        mCurrentMonthLabel = findViewById(R.id.currentDateLabel)
        mViewPager = findViewById(R.id.calendarViewPager)
    }

    private fun initCalendar() {
        mCalendarPageAdapter = mContext?.let {
            mCalendarProperties?.let { it1 ->
                CalendarPageAdapter(
                    it,
                    it1
                )
            }
        }
        val pager = mViewPager
        pager?.adapter = mCalendarPageAdapter
        pager?.addOnPageChangeListener(onPageChangeListener)
        setUpCalendarPosition(Calendar.getInstance())
    }

    private fun setUpCalendarPosition(calendar: Calendar) {
        setMidnight(calendar)
        val properties = mCalendarProperties
        if (properties?.calendarType == ONE_DAY_PICKER) {
            properties.setSelectedDay(calendar)
        }
        val date = properties?.firstPageCalendarDate
        date?.time = calendar.time
        date?.add(
            Calendar.MONTH,
            -CalendarProperties.FIRST_VISIBLE_PAGE
        )
        mViewPager?.currentItem = CalendarProperties.FIRST_VISIBLE_PAGE
    }

    fun setOnPreviousPageChangeListener(listener: OnCalendarPageChangeListener?) {
        mCalendarProperties?.onPreviousPageChangeListener = listener
    }

    fun setOnForwardPageChangeListener(listener: OnCalendarPageChangeListener?) {
        mCalendarProperties?.onForwardPageChangeListener = listener
    }

    private val onNextClickListener =
        OnClickListener {
            val pager = mViewPager
            pager?.currentItem = pager?.currentItem?.plus(1)!!
        }
    private val onPreviousClickListener =
        OnClickListener {
            val pager = mViewPager
            pager?.currentItem = pager?.currentItem?.minus(1)!!
        }
    private val onPageChangeListener: OnPageChangeListener = object : OnPageChangeListener {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
        }

        override fun onPageSelected(position: Int) {
            val calendar = mCalendarProperties?.firstPageCalendarDate?.clone() as Calendar
            calendar.add(Calendar.MONTH, position)
            if (!isScrollingLimited(calendar, position)) {
                setHeaderName(calendar, position)
            }
        }

        override fun onPageScrollStateChanged(state: Int) {}
    }

    private fun isScrollingLimited(calendar: Calendar, position: Int): Boolean {
        val properties = mCalendarProperties
        val pager = mViewPager
        if (isMonthBefore(properties?.minimumDate, calendar)) {
            pager?.currentItem = position + 1
            return true
        }
        if (isMonthAfter(properties?.maximumDate, calendar)) {
            pager?.currentItem = position - 1
            return true
        }
        return false
    }

    private fun setHeaderName(calendar: Calendar, position: Int) {
        mCurrentMonthLabel?.text = mContext?.let { getMonthAndYearDate(it, calendar) }
        callOnPageChangeListeners(position)
    }

    private fun callOnPageChangeListeners(position: Int) {
        val currentPage = mCurrentPage
        val properties = mCalendarProperties
        val forward = properties?.onForwardPageChangeListener
        if (position > currentPage) {
            forward?.onChange()
        }
        val previous = mCalendarProperties?.onPreviousPageChangeListener
        if (position < currentPage) {
            previous?.onChange()
        }
        mCurrentPage = position
    }

    fun setOnDayClickListener(onDayClickListener: OnDayClickListener?) {
        mCalendarProperties?.onDayClickListener = onDayClickListener
    }

    fun setOnDateLongClickListener(onDateLongClickListener: OnDateLongClickListener?) {
        mCalendarProperties?.setOnDateLongClickListener(onDateLongClickListener)
    }

    @Throws(OutOfDateRangeException::class)
    fun setDate(date: Calendar) {
        val min = mCalendarProperties?.minimumDate
        val max = mCalendarProperties?.maximumDate
        if (min != null && date.before(min)) {
            throw OutOfDateRangeException(ErrorsMessages.OUT_OF_RANGE_MIN)
        }
        if (max != null && date.after(max)) {
            throw OutOfDateRangeException(ErrorsMessages.OUT_OF_RANGE_MAX)
        }
        setUpCalendarPosition(date)
        mCurrentMonthLabel?.text = mContext?.let { getMonthAndYearDate(it, date) }
        mCalendarPageAdapter?.notifyDataSetChanged()
    }

    @Throws(OutOfDateRangeException::class)
    fun setDate(currentDate: Date?) {
        val calendar = Calendar.getInstance()
        currentDate?.let {
            calendar.time = it
        }
        setDate(calendar)
    }

    fun setEvents(eventDays: List<EventDay>) {
        val properties = mCalendarProperties
        if (properties?.eventsEnabled == true) {
            properties.eventDays = eventDays
            mCalendarPageAdapter?.notifyDataSetChanged()
        }
    }

    var selectedDates: List<Calendar>
        get() = Stream.of(mCalendarPageAdapter!!.selectedDays)
            .map { obj: SelectedDay -> obj.calendar }
            .sortBy { calendar: Calendar? -> calendar }.toList()
        set(selectedDates) {
            mCalendarProperties?.setSelectedDays(selectedDates)
            mCalendarPageAdapter?.notifyDataSetChanged()
        }

    @get:Deprecated("")
    val selectedDate: Calendar
        get() = firstSelectedDate
    private val firstSelectedDate: Calendar
        get() = Stream.of(mCalendarPageAdapter!!.selectedDays)
            .map { obj: SelectedDay -> obj.calendar }.findFirst().get()
    private val currentPageDate: Calendar
        get() {
            val calendar = mCalendarProperties?.firstPageCalendarDate?.clone() as Calendar
            calendar[Calendar.DAY_OF_MONTH] = 1
            mViewPager?.currentItem?.let { calendar.add(Calendar.MONTH, it) }
            return calendar
        }

    fun setMinimumDate(calendar: Calendar?) {
        mCalendarProperties?.minimumDate = calendar
    }

    fun setMaximumDate(calendar: Calendar?) {
        mCalendarProperties?.maximumDate = calendar
    }

    fun showCurrentMonthPage() {
        val pager = mViewPager
        pager?.setCurrentItem(
            pager.currentItem
                    - getMonthsBetweenDates(
                calendar,
                currentPageDate
            ), true
        )
    }

    fun setDisabledDays(disabledDays: List<Calendar>?) {
        mCalendarProperties?.disabledDays = disabledDays
    }

    companion object {
        const val CLASSIC = 0
        const val ONE_DAY_PICKER = 1
        const val MANY_DAYS_PICKER = 2
        const val RANGE_PICKER = 3
    }
}