package com.ryansteiner.randomspelleffect.data.models

data class ParseSpellEffectStringResult(

    var mFullString: String? = null,
    var mSpell: Spell? = null,
    var mSong: Song? = null,
    var mGameplayModifier: GameplayModifier? = null


)