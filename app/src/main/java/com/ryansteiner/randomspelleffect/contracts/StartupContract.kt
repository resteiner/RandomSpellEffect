package com.ryansteiner.randomspelleffect.contracts


/**
 * Created by Ryan Steiner on 2019/11/06.
 */

interface StartupContract {

    interface View : BaseContract.View {
        fun onLoaded()
    }

    interface Presenter : BaseContract.Presenter {
        fun load()
    }
}