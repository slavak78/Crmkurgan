package calendar

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.BitmapDrawable
import androidx.core.content.ContextCompat
import java.util.*
import java.util.concurrent.TimeUnit

object CalendarUtils {
    fun getDrawableText(
        context: Context,
        text: String,
        typeface: Typeface?,
        color: Int,
        size: Int
    ): Drawable {
        val resources = context.resources
        val bitmap = Bitmap.createBitmap(48, 48, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.typeface =
            typeface ?: Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.color = ContextCompat.getColor(context, color)
        val scale = resources.displayMetrics.density
        paint.textSize = (size * scale).toInt().toFloat()
        val bounds = Rect()
        paint.getTextBounds(text, 0, text.length, bounds)
        val x = (bitmap.width - bounds.width()) / 2
        val y = (bitmap.height + bounds.height()) / 2
        canvas.drawText(text, x.toFloat(), y.toFloat(), paint)
        return BitmapDrawable(resources, bitmap)
    }

    fun getDatesRange(firstDay: Calendar, lastDay: Calendar): ArrayList<Calendar> {
        val lastTime=lastDay.time
        val firstTime=firstDay.time
        return if (lastDay.before(firstDay)) {
            getCalendarsBetweenDates(lastTime, firstTime)
        } else getCalendarsBetweenDates(firstTime, lastTime)
    }

    private fun getCalendarsBetweenDates(dateFrom: Date, dateTo: Date): ArrayList<Calendar> {
        val calendars = ArrayList<Calendar>()
        val calendarFrom = Calendar.getInstance()
        calendarFrom.time = dateFrom
        val calendarTo = Calendar.getInstance()
        calendarTo.time = dateTo
        val daysBetweenDates = TimeUnit.MILLISECONDS.toDays(
            calendarTo.timeInMillis - calendarFrom.timeInMillis
        )
        for (i in 1 until daysBetweenDates) {
            val calendar = calendarFrom.clone() as Calendar
            calendars.add(calendar)
            calendar.add(Calendar.DATE, i.toInt())
        }
        return calendars
    }
}