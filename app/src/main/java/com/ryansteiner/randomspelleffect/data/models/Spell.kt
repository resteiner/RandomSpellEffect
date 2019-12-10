package com.ryansteiner.randomspelleffect.data.models

import java.io.Serializable

data class Spell(

    var mId: Int = -1,
    var mTitle: String? = null,
    var mNameWithAAn: String? = null,
    var mDescription: String? = null,
    var mDND5EDescriptions: Map<String, String>? = null,
    var mDND5EDice: Map<String, String>? = null,
    var mDND5EPageNumber: String? = null,
    var mSWADEDescriptions: Map<String, String>? = null,
    var mSWADEDice: Map<String, String>? = null,
    var mSWADEPageNumber: String? = null

) : Serializable