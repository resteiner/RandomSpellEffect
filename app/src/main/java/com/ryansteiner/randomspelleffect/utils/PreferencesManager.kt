package com.ryansteiner.randomspelleffect.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.ryansteiner.randomspelleffect.data.*

class PreferencesManager(context: Context) {

    val TAG = "PreferencesManageredit"
    val PREFS_NAME = "${context.packageName}.prefs"
    var mPreferences: SharedPreferences? = null
    var mPreviousCardIds: List<String>? = null

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
                for (i in 0 until splitString.count()) {
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
                val storedIntGamePlay = mPreferences!!.getInt(SPELL_EFFECTS_GAMEPLAY, 1)
                val storedIntRolePlay = mPreferences!!.getInt(SPELL_EFFECTS_ROLEPLAY, 1)

                val gamePlayBool = when {
                    (storedIntGamePlay > 0) -> true
                    else -> false
                }

                val rolePlayBool = when {
                    (storedIntRolePlay > 0) -> true
                    else -> false
                }

                val map: MutableMap<String, Boolean> = mutableMapOf()
                map[SPELL_EFFECTS_GAMEPLAY] = gamePlayBool
                map[SPELL_EFFECTS_ROLEPLAY] = rolePlayBool

                map

            }
            else -> {
                null
            }
        }
    }

    fun setGameEffects(gamePlay: Boolean, rolePlay: Boolean) {
        Log.d(TAG, "")


        val gamePlayInt = when (gamePlay) {
            true -> 1
            else -> 0
        }
        val rolePlayInt = when (rolePlay) {
            true -> 1
            else -> 0
        }

        mPreferences?.edit()?.putInt(SPELL_EFFECTS_GAMEPLAY, gamePlayInt)?.apply()
        mPreferences?.edit()?.putInt(SPELL_EFFECTS_ROLEPLAY, rolePlayInt)?.apply()
    }

    fun getTargets(): Map<String, Boolean>? {
        return when {
            mPreferences != null -> {
                val storedIntTargetCaster = mPreferences!!.getInt(TARGET_CASTER, 1)
                val storedIntTargetNearestAlly = mPreferences!!.getInt(TARGET_NEAREST_ALLY, 1)
                val storedIntTargetNearestEnemy = mPreferences!!.getInt(TARGET_NEAREST_ENEMY, 1)
                val storedIntTargetNearestCreature = mPreferences!!.getInt(TARGET_NEAREST_CREATURE, 1)

                val targetCasterBool = when {
                    (storedIntTargetCaster > 0) -> true
                    else -> false
                }

                val targetNearestAllyBool = when {
                    (storedIntTargetNearestAlly > 0) -> true
                    else -> false
                }

                val targetNearestEnemyBool = when {
                    (storedIntTargetNearestEnemy > 0) -> true
                    else -> false
                }

                val targetNearestCreatureBool = when {
                    (storedIntTargetNearestCreature > 0) -> true
                    else -> false
                }


                val map: MutableMap<String, Boolean> = mutableMapOf()
                map[TARGET_CASTER] = targetCasterBool
                map[TARGET_NEAREST_ALLY] = targetNearestAllyBool
                map[TARGET_NEAREST_ENEMY] = targetNearestEnemyBool
                map[TARGET_NEAREST_CREATURE] = targetNearestCreatureBool

                map

            }
            else -> {
                null
            }
        }
    }

    fun setTargets(caster: Boolean?, nearestAlly: Boolean?, nearestEnemy: Boolean?, nearestCreature: Boolean?) {
        if (caster != null) {
            val casterInt = when (caster) {
                true -> 1
                else -> 0
            }
            mPreferences?.edit()?.putInt(TARGET_CASTER, casterInt)?.apply()
        }
        if (nearestAlly != null) {
            val nearestAllyInt = when (nearestAlly) {
                true -> 1
                else -> 0
            }
            mPreferences?.edit()?.putInt(TARGET_NEAREST_ALLY, nearestAllyInt)?.apply()
        }
        if (nearestEnemy != null) {
            val nearestEnemyInt = when (nearestEnemy) {
                true -> 1
                else -> 0
            }
            mPreferences?.edit()?.putInt(TARGET_NEAREST_ENEMY, nearestEnemyInt)?.apply()
        }
        if (nearestCreature != null) {
            val nearestCreatureInt = when (nearestCreature) {
                true -> 1
                else -> 0
            }
            mPreferences?.edit()?.putInt(TARGET_NEAREST_CREATURE, nearestCreatureInt)?.apply()
        }
    }

    fun setAppLifeTimeStart(time: Long) {
        mPreferences?.edit()?.putLong(APP_LIFECYCLE_START_TIME, time)?.apply()
    }

    fun getCurrentLifeTime(): Long? {

        return when (mPreferences) {
            null -> null

            else -> {
                val startTime = mPreferences!!.getLong(APP_LIFECYCLE_START_TIME, -1)
                val currentTime = System.currentTimeMillis()
                return currentTime - startTime

            }

        }
    }

    fun addToPreviousCardsList(cardIds: MutableList<String>) {
        val mutList = mutableListOf<String>()

        if (mPreviousCardIds.isNullOrEmpty()) {
            mPreviousCardIds = getPreviousCardsList()
        }

        if (mPreviousCardIds != null) {
            if (mPreviousCardIds!!.count() > MAX_NUMBER_OF_CARDS_TO_REMEMBER) {
                for (i in 0 until mPreviousCardIds!!.count()) {
                    if (i > 9) {
                        val card = mPreviousCardIds!![i]
                        mutList.add(card)
                    }
                }
            } else {
                mutList.addAll(mPreviousCardIds!!)
            }
        }

        mutList.addAll(cardIds)
        mPreviousCardIds = mutList
        val workingList = mPreviousCardIds ?: listOf()

        val stringBuilder = StringBuilder()
            for (i in 0 until workingList.count()) {
                stringBuilder.append(workingList[i])
                if (i < workingList.count()) {
                    stringBuilder.append(",")
                }
            }

        val previousCardsString = stringBuilder.toString()
        mPreferences?.edit()?.putString(PREVIOUSLY_VIEWED_CARDS, previousCardsString)?.apply()

    }

    fun getPreviousCardsList(): List<String>? {
        return when {
            mPreferences != null -> {
                val storedString = mPreferences!!.getString(PREVIOUSLY_VIEWED_CARDS, "")
                val splitString = storedString.split(",")
                val mutList = mutableListOf<String>()
                for (i in 0 until splitString.count()) {
                    val thisString = splitString[i]
                    if (!thisString.isNullOrBlank()) {
                        mutList.add(thisString)
                    }
                }
                val list = mutList.toList()
                list
            }
            else -> null
        }
    }
}