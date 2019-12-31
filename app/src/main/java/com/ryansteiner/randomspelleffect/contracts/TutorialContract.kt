package com.ryansteiner.randomspelleffect.contracts

import com.ryansteiner.randomspelleffect.data.models.SpellEffect


/**
 * Created by Ryan Steiner on 2019/12/30.
 */

interface TutorialContract {

    interface View : BaseContract.View {
        fun onLoaded()
        fun onGetSpellEffects(spellEffects: List<SpellEffect>?)
    }

    interface Presenter : BaseContract.Presenter {
        fun load()
        fun getSpellEffects()
    }
}