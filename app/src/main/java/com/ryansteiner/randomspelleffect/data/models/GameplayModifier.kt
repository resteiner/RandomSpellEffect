package com.ryansteiner.randomspelleffect.data.models

import com.ryansteiner.randomspelleffect.data.*
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

) : Serializable {
    fun getDescriptionsBySystemAndLevel(system: Int?, level: Int?): String {
        val systemSafe = system ?: RPG_SYSTEM_GENERIC
        val levelSafe = level ?: DAMAGE_INT_MED

        return when (systemSafe) {
            RPG_SYSTEM_D20 -> when (levelSafe) {
                DAMAGE_INT_LOW -> mDND5EDescriptions!![DAMAGE_STRING_LOW] ?: ""
                DAMAGE_INT_HIGH -> mDND5EDescriptions!![DAMAGE_STRING_HIGH] ?: ""
                else -> mDND5EDescriptions!![DAMAGE_STRING_MED] ?: ""
            }
            RPG_SYSTEM_SAVAGEWORLDS -> when (levelSafe) {
                DAMAGE_INT_LOW -> mSWADEDescriptions!![DAMAGE_STRING_LOW] ?: ""
                DAMAGE_INT_HIGH -> mSWADEDescriptions!![DAMAGE_STRING_HIGH] ?: ""
                else -> mSWADEDescriptions!![DAMAGE_STRING_MED] ?: ""
            }
            else -> when (levelSafe) {
                DAMAGE_INT_LOW -> mGenericDescriptions!![DAMAGE_STRING_LOW] ?: ""
                DAMAGE_INT_HIGH -> mGenericDescriptions!![DAMAGE_STRING_HIGH] ?: ""
                else -> mGenericDescriptions!![DAMAGE_STRING_MED] ?: ""
            }
        }
    }

    fun getNameBySystem(system: Int?): String {
        val systemSafe = system ?: RPG_SYSTEM_GENERIC

        return when (systemSafe) {
            RPG_SYSTEM_D20 -> mDND5EName ?: ""
            RPG_SYSTEM_SAVAGEWORLDS -> mSWADEName ?: ""
            else -> mGenericName ?: ""
        }
    }

    fun getDuration(length: String?): String {

        var workingString = ""
        var numberOfDice = when (length) {
            GAME_EFFECT_DURATION_LONG -> 10
            GAME_EFFECT_DURATION_SHORT -> 3
            else -> 5
        }
        var randomDieType = when ((1..6).random()) {
            1 -> 4
            3 -> 8
            4 -> 10
            5 -> 12
            6 -> 20
            else -> 6
        }

        var totalRoll = 0

        for (i in 0 until numberOfDice) {
            val roll = (1..randomDieType).random()
            totalRoll += roll
        }

        workingString = when (length) {
            GAME_EFFECT_DURATION_LONG -> "${numberOfDice}d$randomDieType ($totalRoll) ${mGenericDescriptions!![DAMAGE_STRING_HIGH]}"
            GAME_EFFECT_DURATION_MEDIUM -> "${numberOfDice}d$randomDieType ($totalRoll) ${mGenericDescriptions!![DAMAGE_STRING_MED]}"
            else -> "${numberOfDice}d$randomDieType ($totalRoll) ${mGenericDescriptions!![DAMAGE_STRING_LOW]}"
        }
        return workingString
    }

}


