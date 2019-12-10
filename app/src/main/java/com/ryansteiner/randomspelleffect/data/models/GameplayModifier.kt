package com.ryansteiner.randomspelleffect.data.models

import java.io.Serializable

data class GameplayModifier(

    var mId: Int? = null,
    var mName: String? = null,
    var mGenericName: String? = null,
    var mDND5EName: String? = null,
    var mSWADEName: String? = null,
    var mGenericDescriptions: Map<String, String?>? = null,
    var mDND5EDescriptions: Map<String, String?>? = null,
    var mSWADEDescriptions: Map<String, String?>? = null,
    var mDND5EPageInfo: String? = null,
    var mSWADEPageInfo: String? = null

) : Serializable