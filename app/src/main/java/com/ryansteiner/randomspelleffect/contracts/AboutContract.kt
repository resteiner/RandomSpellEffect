package com.ryansteiner.randomspelleffect.contracts

import android.content.Intent
import android.view.Window


/**
 * Created by Ryan Steiner on 2020/01/13.
 */

interface AboutContract {

    interface View : BaseContract.View {
        fun onLoaded()
        fun onGoToMainActivity(intent: Intent)
    }

    interface Presenter : BaseContract.Presenter {
        fun load()
        fun goToMainActivity(w: Window)
    }
}