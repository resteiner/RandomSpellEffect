package com.ryansteiner.randomspelleffect.utils

import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView

class TextSpanUtils() {

    fun setTextClickable(textView: TextView, string: String, linkStartPostion: Int, linkEndPosition: Int, handler: () -> Unit, drawUnderline: Boolean) {
        //val text = textView.text
        //val start = text.indexOf(subString)
        //val end = start + subString.length

        val span = SpannableString(string)
        span.setSpan(TextClickHandler(handler, drawUnderline), linkStartPostion, linkEndPosition, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        textView.linksClickable = true
        textView.isClickable = true
        textView.movementMethod = LinkMovementMethod.getInstance()

        textView.text = span
    }

}

class TextClickHandler(
    private val handler: () -> Unit,
    private val drawUnderline: Boolean
) : ClickableSpan() {
    override fun onClick(widget: View?) {
        handler()
    }

    override fun updateDrawState(ds: TextPaint?) {
        if (drawUnderline) {
            super.updateDrawState(ds)
        } else {
            ds?.isUnderlineText = false
        }
    }
}