package com.ryansteiner.randomspelleffect.views

import android.content.Context
import android.util.AttributeSet
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import android.widget.Scroller
import androidx.viewpager.widget.ViewPager
import java.lang.reflect.Field

/**
 * Adapted from https://stackoverflow.com/questions/23683956/changing-the-speed-of-transition-of-viewpager-and-setcurrentitem
 */


class ViewPagerCustomDuration : ViewPager {
    private var mScroller: FixedSpeedScroller? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    /*
     * Override the Scroller instance with our own class so we can change the
     * duration
     */
    private fun init() {
        try {
            val viewpager: Class<*> = ViewPager::class.java
            val scroller: Field = viewpager.getDeclaredField("mScroller")
            scroller.setAccessible(true)
            mScroller = FixedSpeedScroller(
                context,
                DecelerateInterpolator()
            )
            scroller.set(this, mScroller)
        } catch (ignored: Exception) {
        }
    }

    /*
     * Set the factor by which the duration will change
     */
    fun setScrollDuration(duration: Int) {
        mScroller!!.setScrollDuration(duration)
    }

    private inner class FixedSpeedScroller : Scroller {
        private var mDuration = 500

        constructor(context: Context?) : super(context) {}
        constructor(context: Context?, interpolator: Interpolator?) : super(context, interpolator) {}
        constructor(context: Context?, interpolator: Interpolator?, flywheel: Boolean) : super(context, interpolator, flywheel) {}

        override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int, duration: Int) { // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, mDuration)
        }

        override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int) { // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, mDuration)
        }

        fun setScrollDuration(duration: Int) {
            mDuration = duration
        }
    }
}