package customfonts

import android.content.Context
import androidx.appcompat.widget.AppCompatTextView
import android.graphics.Typeface
import android.util.AttributeSet

class TextViewSFProDisplayBold : AppCompatTextView {
    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyle: Int
    ) : super(
        context!!,
        attrs,
        defStyle
    ) {
        init()
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?
    ) : super(
        context!!,
        attrs
    ) {
        init()
    }

    constructor(context: Context?) : super(context!!) {
        init()
    }

    private fun init() {
        if (!isInEditMode) {
            typeface = Typeface.createFromAsset(
                context.assets,
                "fonts/ride_rewrite_bungee.ttf"
            )
        }
    }
}