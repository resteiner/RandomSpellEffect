package com.ryansteiner.randomspelleffect.contracts

import android.content.Context

/**
 * Created by Ryan Steiner on 2019/11/06.
 */

interface BaseContract {

    interface View {
        fun onShowToastMessage(message: String?)
        fun onShowToastMessage(messageResId: Int)
        fun onLoadingViewToggle(visible: Boolean)
        fun glideAnimatedLoadingIcon(context: Context)
    }

    interface Presenter {
        fun loadingViewToggle(visible: Boolean)
    }
}