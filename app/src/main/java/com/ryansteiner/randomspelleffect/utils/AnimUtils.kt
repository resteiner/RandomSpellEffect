package com.ryansteiner.randomspelleffect.utils

import android.util.Log
import android.view.View
import androidx.viewpager.widget.ViewPager
import kotlin.math.abs

private const val MIN_SCALE = 0.75f
private const val MIN_ALPHA = 0.5f
private const val MAX_ANGLE = 15f

class ZoomOutPageTransformer : ViewPager.PageTransformer {

    override fun transformPage(view: View, position: Float) {
        view.apply {
            val pageWidth = width
            val pageHeight = height
            when {
                position < -1 -> { // [-Infinity,-1)
                    // This page is way off-screen to the left.
                    alpha = 0f
                }
                position <= 1 -> { // [-1,1]
                    // Modify the default slide transition to shrink the page as well
                    val scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position))
                    val vertMargin = pageHeight * (1 - scaleFactor) / 2
                    val horzMargin = pageWidth * (1 - scaleFactor) / 2
                    translationX = if (position < 0) {
                        horzMargin - vertMargin / 2
                    } else {
                        horzMargin + vertMargin / 2
                    }

                    // Scale the page down (between MIN_SCALE and 1)
                    scaleX = scaleFactor
                    scaleY = scaleFactor

                    // Fade the page relative to its size.
                    alpha = (MIN_ALPHA +
                            (((scaleFactor - MIN_SCALE) / (1 - MIN_SCALE)) * (1 - MIN_ALPHA)))
                }
                else -> { // (1,+Infinity]
                    // This page is way off-screen to the right.
                    alpha = 0f
                }
            }
        }
    }
}

class TiltAnglePageTransformer2 : ViewPager.PageTransformer {

    override fun transformPage(view: View, position: Float) {
        view.apply {
            val pageWidth = width
            val pageHeight = height

            view.cameraDistance = 20000f

            //view.translationX = -position*pageWidth
            //view.pivotX = 0f
            //view.pivotY = 0f

            when {
                position < -1 -> {
                    alpha = 0f
                }
                position <= 0 -> {
                    alpha = 1f
                    pivotX = 0f
                    translationX = position * (pageWidth * 0.25f)
                    view.rotationY = (45 * abs(position))
                }
                position <= 1 -> {
                    alpha = 1f
                    view.pivotX = pageWidth.toFloat()
                    translationX = position * (pageWidth * 0.25f)
                    view.rotationY = (-45 * abs(position))
                }
                else -> {
                    alpha = 0f
                }
            }
        }
    }
}
class TiltAnglePageTransformer : ViewPager.PageTransformer {

    private val TAG = "TiltAnglePageTransfo"

    override fun transformPage(view: View, position: Float) {
        view.apply {
            val pageWidth = width
            val pageHeight = height

            view.cameraDistance = 20000f

            //view.translationX = -position*pageWidth
            Log.d(TAG, "TiltAnglePageTransformer - position = $position")
            view.pivotX = 0f
            view.pivotY = 1f

            val x = abs(position)
            val y = (x-0)/(1-0) * (0.5 - 1) + 1
            val scaleFactor = y.toFloat()

            when {
                position < -1 -> {
                    alpha = 0f
                    scaleX = 1f
                    scaleY = 1f
                }
                position <= 0 -> {
                    alpha = 1f
                    //pivotX = 0f
                    translationX = position * (pageWidth * 0.25f)
                    translationY = -(position * (pageHeight * 0.4f))
                    //val scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position))
                    scaleX = scaleFactor
                    scaleY = scaleFactor
                    view.rotation = (-15 * abs(position))
                }
                position <= 1 -> {
                    alpha = 1f
                    //view.pivotX = pageWidth.toFloat()
                    translationX = position * (pageWidth * 0.25f)
                    translationY = (position * (pageHeight * 0.4f))
                    scaleX = scaleFactor
                    scaleY = scaleFactor
                    view.rotation = (15 * abs(position))
                }
                else -> {
                    alpha = 0f
                    scaleX = 1f
                    scaleY = 1f
                }
            }
        }
    }
}