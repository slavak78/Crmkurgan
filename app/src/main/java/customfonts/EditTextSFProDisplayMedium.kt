package customfonts

import android.content.Context
import androidx.appcompat.widget.AppCompatEditText
import android.graphics.Typeface
import android.util.AttributeSet

class EditTextSFProDisplayMedium : AppCompatEditText {
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
                "fonts/NeoSansPro_Medium.ttf"
            )
        }
    }
}