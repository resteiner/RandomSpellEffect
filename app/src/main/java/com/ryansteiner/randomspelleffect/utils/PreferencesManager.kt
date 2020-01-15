package com.ryansteiner.randomspelleffect.utils

import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.core.content.ContextCompat
import com.ryansteiner.randomspelleffect.R
import com.ryansteiner.randomspelleffect.data.*

class PreferencesManager(context: Context) {

    val TAG = "PreferencesManageredit"
    val PREFS_NAME = "${context.packageName}.prefs"
    var mPreferences: SharedPreferences? = null
    var mPreviousCardIds: List<String>? = null
    var mPreviousCardImages: List<Int>? = null
    var mContext = context
    val mDrawableList = listOf(
        R.drawable.tarot_card_image_001,
        R.drawable.tarot_card_image_002,
        R.drawable.tarot_card_image_003,
        R.drawable.tarot_card_image_004,
        R.drawable.tarot_card_image_005,
        R.drawable.tarot_card_image_006,
        R.drawable.tarot_card_image_007,
        R.drawable.tarot_card_image_008,
        R.drawable.tarot_card_image_009,
        R.drawable.tarot_card_image_010,
        R.drawable.tarot_card_image_011,
        R.drawable.tarot_card_image_012,
        R.drawable.tarot_card_image_013,
        R.drawable.tarot_card_image_014,
        R.drawable.tarot_card_image_015,
        R.drawable.tarot_card_image_016,
        R.drawable.tarot_card_image_017,
        R.drawable.tarot_card_image_018,
        R.drawable.tarot_card_image_019,
        R.drawable.tarot_card_image_020,
        R.drawable.tarot_card_image_021,
        R.drawable.tarot_card_image_022,
        R.drawable.tarot_card_image_023,
        R.drawable.tarot_card_image_024,
        R.drawable.tarot_card_image_025,
        R.drawable.tarot_card_image_026,
        R.drawable.tarot_card_image_027,
        R.drawable.tarot_card_image_028,
        R.drawable.tarot_card_image_029,
        R.drawable.tarot_card_image_030,
        R.drawable.tarot_card_image_031,
        R.drawable.tarot_card_image_032,
        R.drawable.tarot_card_image_033,
        R.drawable.tarot_card_image_034,
        R.drawable.tarot_card_image_035,
        R.drawable.tarot_card_image_036,
        R.drawable.tarot_card_image_037,
        R.drawable.tarot_card_image_038,
        R.drawable.tarot_card_image_039,
        R.drawable.tarot_card_image_040,
        R.drawable.tarot_card_image_041,
        R.drawable.tarot_card_image_042,
        R.drawable.tarot_card_image_043,
        R.drawable.tarot_card_image_044,
        R.drawable.tarot_card_image_045,
        R.drawable.tarot_card_image_046,
        R.drawable.tarot_card_image_047,
        R.drawable.tarot_card_image_048,
        R.drawable.tarot_card_image_049,
        R.drawable.tarot_card_image_050,
        R.drawable.tarot_card_image_051,
        R.drawable.tarot_card_image_052,
        R.drawable.tarot_card_image_053,
        R.drawable.tarot_card_image_054,
        R.drawable.tarot_card_image_055,
        R.drawable.tarot_card_image_056,
        R.drawable.tarot_card_image_057,
        R.drawable.tarot_card_image_058,
        R.drawable.tarot_card_image_059,
        R.drawable.tarot_card_image_060,
        R.drawable.tarot_card_image_061,
        R.drawable.tarot_card_image_062,
        R.drawable.tarot_card_image_063,
        R.drawable.tarot_card_image_064,
        R.drawable.tarot_card_image_065,
        R.drawable.tarot_card_image_066,
        R.drawable.tarot_card_image_067,
        R.drawable.tarot_card_image_068,
        R.drawable.tarot_card_image_069,
        R.drawable.tarot_card_image_070,
        R.drawable.tarot_card_image_071,
        R.drawable.tarot_card_image_072,
        R.drawable.tarot_card_image_073,
        R.drawable.tarot_card_image_074,
        R.drawable.tarot_card_image_075,
        R.drawable.tarot_card_image_076,
        R.drawable.tarot_card_image_077,
        R.drawable.tarot_card_image_078,
        R.drawable.tarot_card_image_079,
        R.drawable.tarot_card_image_080,
        R.drawable.tarot_card_image_081,
        R.drawable.tarot_card_image_082,
        R.drawable.tarot_card_image_083,
        R.drawable.tarot_card_image_084,
        R.drawable.tarot_card_image_085,
        R.drawable.tarot_card_image_086,
        R.drawable.tarot_card_image_087,
        R.drawable.tarot_card_image_088,
        R.drawable.tarot_card_image_089,
        R.drawable.tarot_card_image_090,
        R.drawable.tarot_card_image_091,
        R.drawable.tarot_card_image_092,
        R.drawable.tarot_card_image_093,
        R.drawable.tarot_card_image_094,
        R.drawable.tarot_card_image_095,
        R.drawable.tarot_card_image_096,
        R.drawable.tarot_card_image_097,
        R.drawable.tarot_card_image_098,
        R.drawable.tarot_card_image_099,
        R.drawable.tarot_card_image_100,
        R.drawable.tarot_card_image_101,
        R.drawable.tarot_card_image_102,
        R.drawable.tarot_card_image_103,
        R.drawable.tarot_card_image_104,
        R.drawable.tarot_card_image_105,
        R.drawable.tarot_card_image_106,
        R.drawable.tarot_card_image_107,
        R.drawable.tarot_card_image_108,
        R.drawable.tarot_card_image_109,
        R.drawable.tarot_card_image_110,
        R.drawable.tarot_card_image_111,
        R.drawable.tarot_card_image_112,
        R.drawable.tarot_card_image_113,
        R.drawable.tarot_card_image_114,
        R.drawable.tarot_card_image_115,
        R.drawable.tarot_card_image_041
    )

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
        Log.d(TAG, "[SystemIssue] selectSystem - system = $system")
        Log.d(TAG, "[SystemIssue] selectSystem - mPreferences = $mPreferences")
        mPreferences?.edit()?.putInt(RPG_SYSTEM_ID, system)?.apply()

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
            else -> listOf(DAMAGE_INT_LOW, DAMAGE_INT_MED, DAMAGE_INT_HIGH)
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

    fun addToPreviousCardsList(cardIds: List<String>) {
        val mutList = mutableListOf<String>()


        mPreviousCardIds = getPreviousCardsList()?.reversed()


        if (mPreviousCardIds != null) {
            if (mPreviousCardIds!!.count() >= MAX_NUMBER_OF_CARDS_TO_REMEMBER) {
                for (i in 0 until mPreviousCardIds!!.count()) {
                    val lessThan = cardIds.count() - 1
                    if (i > lessThan) {
                        val card = mPreviousCardIds!![i]
                        mutList.add(card)
                    } else {
                        val card = cardIds!![i]
                        mutList.add(card)

                    }
                }
            } else {
                mutList.addAll(mPreviousCardIds!!)
            }
        }


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

    fun addToPreviousCardImages(imageId: Int) {
        val list = mutableListOf<Int>()
        if (mPreviousCardImages != null && mPreviousCardImages!!.count() > 0) {
            if (mPreviousCardImages!!.count() > 20) {
                for (i in 0 until mPreviousCardImages!!.count())
                    if (i > 0) {
                        val number = mPreviousCardImages!![i]
                        list.add(number)
                    }
            } else {
                list.addAll(mPreviousCardImages!!)
            }
        }

        list.add(imageId)

        mPreviousCardImages = list.toList()

        val stringBuilder = StringBuilder()
        for (i in 0 until list.count()) {
            stringBuilder.append(list[i])
            if (i < list.count()) {
                stringBuilder.append(",")
            }
        }

        val previousCardImageStrings = stringBuilder.toString()

        mPreferences?.edit()?.putString(PREVIOUSLY_USED_IMAGES, previousCardImageStrings)?.apply()

    }

    fun getPreviousCardImages(): List<Int>? {
        return when {
            mPreferences != null -> {
                val storedString = mPreferences!!.getString(PREVIOUSLY_USED_IMAGES, "")
                val splitString = storedString.split(",")
                val mutList = mutableListOf<Int>()
                for (i in 0 until splitString.count()) {
                    val thisString = splitString[i]
                    if (!thisString.isNullOrBlank()) {
                        val imageInt = thisString.toInt()
                        mutList.add(imageInt)
                    }
                }
                val list = mutList.toList()
                Log.d(TAG, "getPreviousCardImages - list = $list")
                list
            }
            else -> null
        }
    }

    fun getListOfCardBackgroundImagesCount(): Int {
        return mDrawableList.count()
    }
    fun getCardBackgroundImagesAtIndex(index: Int): Drawable? {
        val imageId = mDrawableList[index]
        return ContextCompat.getDrawable(mContext, imageId)
    }


    fun getHasBeenOnboarded(): Int? {
        return when {
            mPreferences != null -> {
                val hasBeenOnboarded = mPreferences!!.getInt(HAS_BEEN_ONBOARDED, -1)
                hasBeenOnboarded
            }
            else -> null
        }
    }

    fun setHasBeenOnboarded(hasBeenOnboarded: Int) {
        mPreferences?.edit()?.putInt(HAS_BEEN_ONBOARDED, hasBeenOnboarded)?.apply()
        //mPreferences?.edit()?.apply()

    }


}