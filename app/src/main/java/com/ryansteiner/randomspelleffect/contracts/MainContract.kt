package com.ryansteiner.randomspelleffect.contracts

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.ryansteiner.randomspelleffect.data.models.ParseSpellEffectStringResult
import com.ryansteiner.randomspelleffect.data.models.Song
import com.ryansteiner.randomspelleffect.data.models.Spell
import com.ryansteiner.randomspelleffect.data.models.SpellEffect
import com.ryansteiner.randomspelleffect.utils.PreferencesManager
import com.ryansteiner.randomspelleffect.utils.SpellsList


/**
 * Created by Ryan Steiner on 2019/11/06.
 */

interface MainContract {

    interface View : BaseContract.View {
        fun onInitializedView()
        fun onGeneratedSingleSpellEffect(spellEffect: SpellEffect)
        fun onLoadedDatabase()
        fun test()
        fun updateDebugText(systemText: String?)
        fun updatePreferences(prefs: PreferencesManager?)
        fun updateColorLayer(colorId: Int?, visibility: Boolean)
        fun updatePatternLayer(patternId: Int?, visibility: Boolean)
        fun updateDiceRoll(selectedSpellDice: String?)
        fun updateSpellInfoContainer(visibility: Boolean, spellText: String?, selectedSpellDescriptionWithDamageLevel: String?, selectedSpellPageNumber: String?)
        fun onClickSettings(showSettings: Boolean)
        fun songVideoInit(showVideo: Boolean, song: Song?)
        fun onGetSpellEffects(spellEffects: List<SpellEffect>?)
        fun toggleNetLibramInfoOverlay()
    }

    interface Presenter : BaseContract.Presenter {
        fun initializeView()
        fun generateSingleSpellEffect()
        fun loadDatabase(context: Context)
        fun getPreferences()
        fun parseSpellStringForVariables(string: String?, system: Int): ParseSpellEffectStringResult?
        fun updateDamagePreferences(damagePrefs: List<Int>?)
        fun updateSpellList(spellsList: SpellsList)
        fun clickSettings(showSettings: Boolean)
        fun retrieveSpellEffectById(id: Int)
        fun getSpellEffects()
    }
}