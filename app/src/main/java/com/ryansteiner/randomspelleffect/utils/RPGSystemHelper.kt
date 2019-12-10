package com.ryansteiner.randomspelleffect.utils

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.ryansteiner.randomspelleffect.data.*
import com.ryansteiner.randomspelleffect.data.models.Creature
import com.ryansteiner.randomspelleffect.data.models.GameEffect
import com.ryansteiner.randomspelleffect.data.models.Song

class RPGSystemHelper(context: Context) {
    companion object {
        val DURATION_SHORT = 1001
        val DURATION_MEDIUM = 1002
        val DURATION_LONG = 1003

        val DAMAGE_LOW = 2001
        val DAMAGE_MEDIUM = 2002
        val DAMAGE_HIGH = 2003
    }

    var mPreferencesManager: PreferencesManager? = null
    val mContext: Context = context

    fun getDuration(duration: Int): String {
        if (mPreferencesManager == null) {
            mPreferencesManager = PreferencesManager(mContext)
        }

        val system = mPreferencesManager?.getSystem()
        var durationText = ""

        durationText = when (system) {
            RPG_SYSTEM_D20 -> {
                when (duration) {
                    DURATION_SHORT -> {
                        "1d4 minutes"
                    }
                    DURATION_MEDIUM -> {
                        "1d8 hours"
                    }
                    DURATION_LONG -> {
                        "2d6 days"
                    }
                    else -> {
                        "It looks like you're using the D20 system, but the duration for this spell is messed up"
                    }
                }
            }
            RPG_SYSTEM_SAVAGEWORLDS -> {
                when (duration) {
                    DURATION_SHORT -> {
                        "1d4 minutes"
                    }
                    DURATION_MEDIUM -> {
                        "1d8 hours"
                    }
                    DURATION_LONG -> {
                        "2d6 days"
                    }
                    else -> {
                        "It looks like you're using the D20 system, but the duration for this spell is messed up"
                    }
                }
            }
            else -> {
                "Something went wrong with determining your prefered RPG system"
            }
        }

        return durationText


    }
}

fun getCreatureById(database: SQLiteDatabase?, id: Int): Creature? {
    val db = database
    val idString = id.toString()
    val selectQuery = "SELECT  * FROM $DB_CREATURE_TABLE_NAME WHERE $TABLE_COL_ID = ?"
    val result = Creature()

    db?.rawQuery(selectQuery, arrayOf(idString)).use {
        // .use requires API 16
        if (it!!.moveToFirst()) {
            result.mId = it.getInt(it.getColumnIndex(TABLE_COL_ID))
            result.mName = it.getString(it.getColumnIndex(TABLE_COL_NAME))
            result.mNamePlural = it.getString(it.getColumnIndex(TABLE_COL_NAME_PLURAL))
            result.mDND5EPage = it.getString(it.getColumnIndex(TABLE_COL_DND5E_PAGE))
            result.mSWADEPage = it.getString(it.getColumnIndex(TABLE_COL_SWADE_PAGE))
        }
    }
    return result
}

fun getSongById(database: SQLiteDatabase?, id: Int): Song? {
    val db = database
    val idString = id.toString()
    val selectQuery = "SELECT  * FROM $DB_SONGS_TABLE_NAME WHERE $TABLE_COL_ID = ?"
    val result = Song()

    db?.rawQuery(selectQuery, arrayOf(idString)).use {
        // .use requires API 16
        if (it!!.moveToFirst()) {
            result.mId = it.getInt(it.getColumnIndex(TABLE_COL_ID))
            result.mName = it.getString(it.getColumnIndex(TABLE_COL_NAME))
            result.mArtist = it.getString(it.getColumnIndex(TABLE_COL_ARTIST))
            result.mUrl = it.getString(it.getColumnIndex(TABLE_COL_URL))
            result.mStartAt = it.getInt(it.getColumnIndex(TABLE_COL_START_AT))
        }
    }
    return result
}