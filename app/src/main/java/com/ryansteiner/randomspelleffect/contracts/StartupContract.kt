package com.ryansteiner.randomspelleffect.contracts

import android.content.Intent
import android.view.Window


/**
 * Created by Ryan Steiner on 2019/11/06.
 */

interface StartupContract {

    interface View : BaseContract.View {
        fun onLoaded(hasBeenOnboarded: Boolean)
        fun onGoToMainActivity(intent: Intent)
        fun onUpdatedSystem()
    }

    interface Presenter : BaseContract.Presenter {
        fun load()
        fun goToMainActivity(w: Window)
        fun updateSystem(system: Int?)
    }
}