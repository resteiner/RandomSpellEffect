package com.ryansteiner.randomspelleffect.utils

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import com.ryansteiner.randomspelleffect.data.*
import java.util.*

class PreferencesManager(context: Context) {

    val TAG = "PreferencesManageredit"
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
                val splitString = storedString.split(",")
                val mutList = mutableListOf<Int>()
                Log.d(TAG, "getDamagePreferences - splitString = ${splitString}")
                Log.d(TAG, "getDamagePreferences - splitString.count() = ${splitString.count()}")
                for (i in 0 until splitString.count()) {
                    Log.d(TAG, "getDamagePreferences - splitString[i] = ${splitString[i]}")
                    val thisString = splitString[i]
                    if (!thisString.isNullOrBlank()) {
                        val thisInt = Integer.parseInt(thisString)
                        mutList.add(thisInt)
                    }
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
                ?.apply()
        } else {
            val stringBuilder = StringBuilder()
            for (i in 0 until damagePrefs.count()) {
                stringBuilder.append(damagePrefs[i])
                if (i < damagePrefs.count()) {
                    stringBuilder.append(",")
                }
            }
            val damageString = stringBuilder.toString() ?: DEFAULT_DAMAGE_PREFERENCES
            mPreferences?.edit()?.putString(RPG_DAMAGE_PREFERENCES, damageString)?.apply()

        }
    }

    fun getGameEffects(): Map<String, Boolean>? {
        return when {
            mPreferences != null -> {
                val storedIntGameplay = mPreferences!!.getInt(SPELL_EFFECTS_GAMEPLAY, 1)
                val storedIntRoleplay = mPreferences!!.getInt(SPELL_EFFECTS_ROLEPLAY, 1)

                val gameplayBool = when {
                    (storedIntGameplay > 0) -> true
                    else -> false
                }

                val roleplayBool = when {
                    (storedIntRoleplay > 0) -> true
                    else -> false
                }

                val map: MutableMap<String, Boolean> = mutableMapOf()
                map[SPELL_EFFECTS_GAMEPLAY] = gameplayBool
                map[SPELL_EFFECTS_ROLEPLAY] = roleplayBool

                map

            }
            else -> {
                null
            }
        }
    }

    fun setGameEffects(gameplay: Boolean, roleplay: Boolean) {
        Log.d(TAG, "")


        val gameplayInt = when (gameplay) {
            true -> 1
            else -> 0
        }
        val roleplayInt = when (roleplay) {
            true -> 1
            else -> 0
        }

        mPreferences?.edit()?.putInt(SPELL_EFFECTS_GAMEPLAY, gameplayInt)?.apply()
        mPreferences?.edit()?.putInt(SPELL_EFFECTS_ROLEPLAY, roleplayInt)?.apply()
    }

}