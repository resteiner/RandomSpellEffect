package com.ryansteiner.randomspelleffect.utils

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.ryansteiner.randomspelleffect.data.DEFAULT_DAMAGE_PREFERENCES
import com.ryansteiner.randomspelleffect.data.RPG_DAMAGE_PREFERENCES
import com.ryansteiner.randomspelleffect.data.RPG_SYSTEM_ID
import com.ryansteiner.randomspelleffect.data.SHARED_PREFERENCES_NAME
import java.util.*

class PreferencesManager(context: Context) {

    val PREFS_NAME = "${context.packageName}.prefs"
    var mPreferences: SharedPreferences? = null

    init {
        mPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    fun getSystem(): Int? {
        return when {
            mPreferences != null -> {
                val system = mPreferences!!.getInt(RPG_SYSTEM_ID, -1)
                system
            }
            else -> null
        }
    }

    fun selectSystem(system: Int) {
        mPreferences?.edit()?.putInt(RPG_SYSTEM_ID, system)?.apply()
        //mPreferences?.edit()?.apply()

    }

    fun getDamagePreferences(): List<Int>? {
        return when {
            mPreferences != null -> {
                val storedString = mPreferences!!.getString(RPG_DAMAGE_PREFERENCES, "")
                val stringTokenizer = StringTokenizer(storedString)
                val mutList = mutableListOf<Int>()
                for (i in 0 until stringTokenizer.countTokens()) {
                    mutList[i] = Integer.parseInt(stringTokenizer.nextToken())
                }
                val list = mutList.toList()
                list
            }
            else -> null
        }
    }

    fun setDamagePreferences(damagePrefs: List<Int>?) {
        if (damagePrefs == null || damagePrefs.count() == 0) {
            mPreferences?.edit()?.putString(RPG_DAMAGE_PREFERENCES, DEFAULT_DAMAGE_PREFERENCES)
        } else {
            val stringBuilder = StringBuilder()
            for (i in 0 until damagePrefs.count()) {
                stringBuilder.append(damagePrefs[i]).append(",")
            }
            val damageString = stringBuilder.toString() ?: DEFAULT_DAMAGE_PREFERENCES
            mPreferences?.edit()?.putString(RPG_DAMAGE_PREFERENCES, damageString)

        }
    }


}