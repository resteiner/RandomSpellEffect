package com.ryansteiner.randomspelleffect.contracts

/**
 * Created by Ryan Steiner on 2019/11/06.
 */

interface BaseContract {

    interface View {
        fun onShowToastMessage(message: String?)
        fun onShowToastMessage(messageResId: Int)
        fun onProgressViewToggle(visible: Boolean)
    }

    interface Presenter {
        fun progressViewToggle(visible: Boolean)
    }
}