package com.ryansteiner.randomspelleffect.data.models

import java.io.Serializable

data class Song(

    var mId: Int? = null,
    var mName: String? = null,
    var mArtist: String? = null,
    var mUrl: String? = null,
    var mStartAt: Int? = null

) : Serializable