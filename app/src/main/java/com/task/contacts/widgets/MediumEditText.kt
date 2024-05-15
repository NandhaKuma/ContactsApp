package com.task.contacts.widgets

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputEditText

class MediumEditText : TextInputEditText {
    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        val t = Typeface.createFromAsset(context.assets, "fonts/futura_medium.otf")
        typeface = t
    }
}