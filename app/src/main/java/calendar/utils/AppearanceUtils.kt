package calendar.utils

import android.widget.TextView
import ru.crmkurgan.main.R
import androidx.constraintlayout.widget.ConstraintLayout
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageButton
import java.util.ArrayList

object AppearanceUtils {
    @JvmStatic
    fun setAbbreviationsLabels(view: View, color: Int, firstDayOfWeek: Int) {
        val labels: MutableList<TextView> = ArrayList()
        labels.add(view.findViewById(R.id.mondayLabel))
        labels.add(view.findViewById(R.id.tuesdayLabel))
        labels.add(view.findViewById(R.id.wednesdayLabel))
        labels.add(view.findViewById(R.id.thursdayLabel))
        labels.add(view.findViewById(R.id.fridayLabel))
        labels.add(view.findViewById(R.id.saturdayLabel))
        labels.add(view.findViewById(R.id.sundayLabel))
        val abbreviations =
            view.context.resources.getStringArray(R.array.material_calendar_day_abbreviations_array)
        for (i in 0..6) {
            val label = labels[i]
            label.text = abbreviations[(i + firstDayOfWeek - 1) % 7]
            if (color != 0) {
                label.setTextColor(color)
            }
        }
    }

    @JvmStatic
    fun setHeaderColor(view: View, color: Int) {
        if (color == 0) {
            return
        }
        val calendarHeader = view.findViewById<ConstraintLayout>(R.id.calendarHeader)
        calendarHeader.setBackgroundColor(color)
    }

    @JvmStatic
    fun setHeaderLabelColor(view: View, color: Int) {
        if (color == 0) {
            return
        }
        (view.findViewById<View>(R.id.currentDateLabel) as TextView).setTextColor(color)
    }

    @JvmStatic
    fun setAbbreviationsBarColor(view: View, color: Int) {
        if (color == 0) {
            return
        }
        view.findViewById<View>(R.id.abbreviationsBar).setBackgroundColor(color)
    }

    @JvmStatic
    fun setPagesColor(view: View, color: Int) {
        if (color == 0) {
            return
        }
        view.findViewById<View>(R.id.calendarViewPager).setBackgroundColor(color)
    }

    @JvmStatic
    fun setPreviousButtonImage(view: View, drawable: Drawable?) {
        if (drawable == null) {
            return
        }
        (view.findViewById<View>(R.id.previousButton) as ImageButton).setImageDrawable(drawable)
    }

    @JvmStatic
    fun setForwardButtonImage(view: View, drawable: Drawable?) {
        if (drawable == null) {
            return
        }
        (view.findViewById<View>(R.id.forwardButton) as ImageButton).setImageDrawable(drawable)
    }

    @JvmStatic
    fun setHeaderVisibility(view: View, visibility: Int) {
        val calendarHeader = view.findViewById<ConstraintLayout>(R.id.calendarHeader)
        calendarHeader.visibility = visibility
    }
}