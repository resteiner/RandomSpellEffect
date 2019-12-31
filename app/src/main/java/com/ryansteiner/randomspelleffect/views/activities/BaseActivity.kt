package com.ryansteiner.randomspelleffect.views.activities

import android.content.Context
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.ryansteiner.randomspelleffect.R
import com.ryansteiner.randomspelleffect.contracts.BaseContract
import com.ryansteiner.randomspelleffect.presenters.BasePresenter
import kotlinx.android.synthetic.main.include_full_loading_screen.*

/**
 * Created by Ryan Steiner on 2019/11/06.
 */

abstract class BaseActivity : AppCompatActivity(), BaseContract.View {

    private val TAG = "BaseActivity"
    private var mAnimationList = null

    override fun onShowToastMessage(message: String?) {
        val safeMessage = when {
            message.isNullOrBlank() -> {resources.getString(R.string.generic_error_message)}
            else -> {message}
        }

        runOnUiThread {
            Toast.makeText(this, safeMessage, Toast.LENGTH_LONG).show()
        }
    }

    override fun onShowToastMessage(messageResId: Int) {
        val safeMessage = when {
            messageResId < 0 -> {resources.getString(R.string.generic_error_message)}
            else -> {getString(messageResId)}
        }

        runOnUiThread {
            Toast.makeText(this, safeMessage, Toast.LENGTH_LONG).show()
        }
    }

    override fun onLoadingViewToggle(visible: Boolean) {
        mFullScreenLoadingContainer?.visibility = when (visible) {
            true -> VISIBLE
            else -> GONE
        }
        /*when (visible) {
            true -> {
                val animDuration = 500L
                val alphaAnimA = AlphaAnimation(0.25f, 1.0f)
                alphaAnimA.duration = animDuration
                alphaAnimA.repeatCount = Animation.INFINITE
                alphaAnimA.repeatMode = Animation.REVERSE
                tLoadingTextEllipsesA.startAnimation(alphaAnimA)

                val alphaAnimB = AlphaAnimation(1.0f, 0.25f)
                alphaAnimB.duration = animDuration
                alphaAnimB.startOffset = 250
                alphaAnimB.repeatCount = Animation.INFINITE
                alphaAnimB.repeatMode = Animation.REVERSE
                tLoadingTextEllipsesB.startAnimation(alphaAnimB)

                val alphaAnimC = AlphaAnimation(1.0f, 0.25f)
                alphaAnimC.duration = animDuration
                alphaAnimC.startOffset = 0
                alphaAnimC.repeatCount = Animation.INFINITE
                alphaAnimC.repeatMode = Animation.REVERSE
                tLoadingTextEllipsesC.startAnimation(alphaAnimC)

            }
            else -> {
                tLoadingTextEllipsesA.clearAnimation()
                tLoadingTextEllipsesB.clearAnimation()
                tLoadingTextEllipsesC.clearAnimation()
            }
        }*/
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun glideAnimatedLoadingIcon(context: Context){

        Glide
            .with(context)
            .load(ContextCompat.getDrawable(context, R.drawable.loading_anim))
            .into(iLoadingIcon)

        Glide
            .with(context)
            .load(ContextCompat.getDrawable(context, R.drawable.ellipses_anim))
            .into(iLoadingEllipses)

    }
}

