package com.ryansteiner.randomspelleffect.data.models

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable


/**
 * Created by Ryan Steiner on 2019/11/07.
 */


class FullCard : Serializable {
    private var mSpellEffect: SpellEffect? = null
    private var mMainText: String? = null
    private var mSpell: Spell? = null


    fun FullCard(spellEffect: SpellEffect, mainText: String, spell: Spell) {
        this.mSpellEffect = spellEffect
        this.mMainText = mainText
        this.mSpell = spell
    }

    fun setSpellEffect(spellEffect: SpellEffect) {
        this.mSpellEffect = spellEffect
    }

    fun getSpellEffect(): SpellEffect? {
        return mSpellEffect
    }

    fun setMainText(mainText: String) {
        this.mMainText = mainText
    }

    fun getMainText(): String? {
        return mMainText
    }

    fun setSpell(spell: Spell?) {
        if (spell != null) {
            this.mSpell = spell
        }
    }

    fun getSpell(): Spell? {
        return mSpell
    }


}
