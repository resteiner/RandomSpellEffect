package com.ryansteiner.randomspelleffect.data.models

import java.io.Serializable


/**
 * Created by Ryan Steiner on 2019/11/07.
 */


class FullCard : Serializable {
    private var mSpellEffect: SpellEffect? = null
    private var mMainText: String? = null
    private var mSpell: Spell? = null
    private var mSong: Song? = null
    private var mGameplayModifier: GameplayModifier? = null


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

    fun setSong(song: Song?) {
        if (song != null) {
            this.mSong = song
        }
    }

    fun getSong(): Song? {
        return mSong
    }

    fun setGameplayModifier(mod: GameplayModifier?) {
        if (mod != null) {
            this.mGameplayModifier = mod
        }
    }

    fun getGameplayModifier(): GameplayModifier? {
        return mGameplayModifier
    }


}
