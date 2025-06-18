package calendar

import calendar.utils.DateUtils.setMidnight
import androidx.annotation.DrawableRes
import android.graphics.drawable.Drawable
import androidx.annotation.RestrictTo
import java.util.*

class EventDay {
    val calendar: Calendar

    @get:RestrictTo(RestrictTo.Scope.LIBRARY)
    var imageDrawable: Any? = null
        private set
    private var mIsDisabled = false

    constructor(day: Calendar) {
        calendar = day
    }

    constructor(day: Calendar, @DrawableRes drawable: Int) {
        setMidnight(day)
        calendar = day
        imageDrawable = drawable
    }

    constructor(day: Calendar, drawable: Drawable?) {
        setMidnight(day)
        calendar = day
        imageDrawable = drawable
    }

    @set:RestrictTo(RestrictTo.Scope.LIBRARY)
    var isEnabled: Boolean
        get() = !mIsDisabled
        set(enabled) {
            mIsDisabled = enabled
        }
}