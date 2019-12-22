package com.ryansteiner.randomspelleffect.utils

import android.content.Context
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.core.content.ContextCompat
import com.ryansteiner.randomspelleffect.R
import com.ryansteiner.randomspelleffect.data.*
import com.ryansteiner.randomspelleffect.data.models.SpellEffect
import com.ryansteiner.randomspelleffect.data.models.GameplayModifier
import com.ryansteiner.randomspelleffect.data.models.Spell


class MyDatabaseUtils(context: Context) {

    private val TAG = "MyDatabaseUtils"
    private var mDatabase: SQLiteDatabase? = null
    private val mContext = context
    private var mPreferencesManager = PreferencesManager(context)


    fun loadDatabase(): SQLiteDatabase {
        val database = DatabaseHelper(mContext).readableDatabase
        Log.d(TAG, "loadDatabase - database = $database")
        updateDatabaseReference(database)
        return database
    }

    private fun updateDatabaseReference(db: SQLiteDatabase?) {
        if (db != null) {
            mDatabase = db
        }
        Log.d(TAG, "updateDatabaseReference - mDatabase = $mDatabase")
    }

    fun getRandomSpellEffect(): SpellEffect? {
        val totalCount = DatabaseUtils.queryNumEntries(mDatabase, DB_SPELLEFFECT_TABLE_NAME)
        val randomSelection = (1..totalCount).random()
        val randomizedId = randomSelection.toString()
        return getSpellEffectById(randomizedId)
    }

    private fun getSpellEffectById(id: String): SpellEffect? {
        //TODO need to find out why mDatabase is null here...
        val db = when {
            (mDatabase == null) -> {
                DatabaseHelper(mContext).readableDatabase
            }
            else -> mDatabase
        }
        if (db != null) {
            val selectQuery = "SELECT * FROM $DB_SPELLEFFECT_TABLE_NAME WHERE $TABLE_COL_ID = ?"

            db?.rawQuery(selectQuery, arrayOf(id)).use {
                // .use requires API 16
                if (it!!.moveToFirst()) {
                    val result = SpellEffect()
                    result.mId = it.getInt(it.getColumnIndex(TABLE_COL_ID))
                    result.mDescription = it.getString(it.getColumnIndex(TABLE_COL_DESCRIPTION))
                    result.mType = it.getInt(it.getColumnIndex(TABLE_COL_TYPE))
                    result.mTarget = it.getInt(it.getColumnIndex(TABLE_COL_TARGET))
                    result.mHasGameplayImpact =
                        it.getInt(it.getColumnIndex(TABLE_COL_HASGAMEPLAYIMPACT))
                    result.mTags = it.getString(it.getColumnIndex(TABLE_COL_TAGS))
                    result.mHowBadIsIt = it.getInt(it.getColumnIndex(TABLE_COL_HOWBADISIT))
                    result.mUsesImage = it.getString(it.getColumnIndex(TABLE_COL_USESIMAGE))
                    val isNetLibramInt = it.getInt(it.getColumnIndex(TABLE_COL_ISNETLIBRAM))
                    result.mIsNetLibram = when {
                        (isNetLibramInt >= 1) -> true
                        else -> false
                    }
                    db.close()
                    return result
                }
            }
            db.close()
            return null
        } else {
            db?.close()
            return null
        }
    }

    fun getSpellEffectsByIds(ids: List<String>): List<SpellEffect>? {
        //TODO need to find out why mDatabase is null here...
        val db = when {
            (mDatabase == null) -> {
                DatabaseHelper(mContext).readableDatabase
            }
            else -> mDatabase
        }

        //TODO There should be a way to get several results from one query, but I keep getting: Cannot bind argument at index 1 because the index is out of range.  The statement has 0 parameters.
        var list = mutableListOf<SpellEffect>()

        if (db != null && ids != null && ids.count() > 0) {
            ids.forEach {
                Log.d(TAG, "getSpellEffectsByIds - ids(it) = $it")
                val spellEffect = getSpellEffectById(it)
                Log.d(TAG, "getSpellEffectsByIds - spellEffect = $spellEffect")
                if (spellEffect != null) {
                    spellEffect.mBackgroundImageId = getIntForCardBackground()
                    list.add(spellEffect)

                }
            }
        }
        db?.close()
        return list
    }

    fun getRandomSpell(): Spell? {
        //TODO need to find out why mDatabase is null here...
        val db = when {
            (mDatabase == null) -> {
                DatabaseHelper(mContext).readableDatabase
            }
            else -> mDatabase
        }
        val totalCount = DatabaseUtils.queryNumEntries(db, DB_SPELLS_TABLE_NAME)
        val randomSelection = (1..totalCount).random()
        val randomizedId = randomSelection.toString()
        db?.close()
        return getSpellById(randomizedId)
    }

    private fun getSpellById(id: String): Spell? {
        val spell = Spell()

        //TODO need to find out why mDatabase is null here...
        val db = when {
            (mDatabase == null) -> {
                DatabaseHelper(mContext).readableDatabase
            }
            else -> mDatabase
        }
        if (db != null) {
            val selectQuery = "SELECT * FROM $DB_SPELLS_TABLE_NAME WHERE $TABLE_COL_ID = ?"

            db?.rawQuery(selectQuery, arrayOf(id)).use {
                // .use requires API 16
                if (it!!.moveToFirst()) {
                    spell.mId = it.getInt(it.getColumnIndex(TABLE_COL_ID))
                    spell.mTitle = it.getString(it.getColumnIndex(TABLE_COL_NAME))
                    spell.mNameWithAAn = it.getString(it.getColumnIndex(TABLE_COL_NAME_WITH_A_AN))
                    spell.mDescription = it.getString(it.getColumnIndex(TABLE_COL_GENERIC_DESCRIPTION))
                    val dnd5eDescLow = it.getString(it.getColumnIndex(TABLE_COL_DND5E_DESC_LOW))
                    val dnd5eDescMed = it.getString(it.getColumnIndex(TABLE_COL_DND5E_DESC_MEDIUM))
                    val dnd5eDescHigh = it.getString(it.getColumnIndex(TABLE_COL_DND5E_DESC_HIGH))
                    val mut5eDescsList: MutableMap<String, String> = mutableMapOf()
                    mut5eDescsList[DAMAGE_STRING_LOW] = dnd5eDescLow
                    mut5eDescsList[DAMAGE_STRING_MED] = dnd5eDescMed
                    mut5eDescsList[DAMAGE_STRING_HIGH] = dnd5eDescHigh
                    spell.mDND5EDescriptions = mut5eDescsList
                    val swadeDescLow = it.getString(it.getColumnIndex(TABLE_COL_SWADE_DESC_LOW))
                    val swadeDescMed = it.getString(it.getColumnIndex(TABLE_COL_SWADE_DESC_MEDIUM))
                    val swadeDescHigh = it.getString(it.getColumnIndex(TABLE_COL_SWADE_DESC_HIGH))
                    val mutSwadeDescsList: MutableMap<String, String> = mutableMapOf()
                    mutSwadeDescsList[DAMAGE_STRING_LOW] = swadeDescLow
                    mutSwadeDescsList[DAMAGE_STRING_MED] = swadeDescMed
                    mutSwadeDescsList[DAMAGE_STRING_HIGH] = swadeDescHigh
                    spell.mSWADEDescriptions = mutSwadeDescsList
                    val mut5eDiceList: MutableMap<String, String> = mutableMapOf()
                    val dnd5eDiceLow = it.getString(it.getColumnIndex(TABLE_COL_DND5E_DICE_LOW))
                    val dnd5eDiceMed = it.getString(it.getColumnIndex(TABLE_COL_DND5E_DICE_MEDIUM))
                    val dnd5eDiceHigh = it.getString(it.getColumnIndex(TABLE_COL_DND5E_DICE_HIGH))
                    mut5eDiceList[DAMAGE_STRING_LOW] = dnd5eDiceLow
                    mut5eDiceList[DAMAGE_STRING_MED] = dnd5eDiceMed
                    mut5eDiceList[DAMAGE_STRING_HIGH] = dnd5eDiceHigh
                    spell.mDND5EDice = mut5eDiceList
                    val mutSwadeDiceList: MutableMap<String, String> = mutableMapOf()
                    val swadeDiceLow = it.getString(it.getColumnIndex(TABLE_COL_SWADE_DICE_LOW))
                    val swadeDiceMed = it.getString(it.getColumnIndex(TABLE_COL_SWADE_DICE_MEDIUM))
                    val swadeDiceHigh = it.getString(it.getColumnIndex(TABLE_COL_SWADE_DICE_HIGH))
                    mutSwadeDiceList[DAMAGE_STRING_LOW] = swadeDiceLow
                    mutSwadeDiceList[DAMAGE_STRING_MED] = swadeDiceMed
                    mutSwadeDiceList[DAMAGE_STRING_HIGH] = swadeDiceHigh
                    spell.mSWADEDice = mut5eDiceList
                    spell.mDND5EPageNumber = it.getString(it.getColumnIndex(TABLE_COL_DND5E_PAGE))
                    spell.mSWADEPageNumber = it.getString(it.getColumnIndex(TABLE_COL_SWADE_PAGE))
                }
            }
        }
        db?.close()
        return when {
            spell.mTitle != null -> {spell}
            else -> {null}
        }
    }

    fun getGameplayModifierByName(name: String): GameplayModifier? {
        var modifier: GameplayModifier? = null

        //TODO need to find out why mDatabase is null here...
        val db = when {
            (mDatabase == null) -> {
                DatabaseHelper(mContext).readableDatabase
            }
            else -> mDatabase
        }
        if (db != null) {
            val selectQuery = "SELECT * FROM $DB_GAMEPLAY_MODIFIERS_TABLE_NAME WHERE $TABLE_COL_NAME = ?"

            db?.rawQuery(selectQuery, arrayOf(name)).use {
                // .use requires API 16
                if (it!!.moveToFirst()) {
                    modifier = GameplayModifier()
                    modifier?.mId = it.getInt(it.getColumnIndex(TABLE_COL_ID))
                    modifier?.mName = it.getString(it.getColumnIndex(TABLE_COL_NAME))
                    modifier?.mGenericName = it.getString(it.getColumnIndex(TABLE_COL_GENERIC_NAME))
                    modifier?.mDND5EName = it.getString(it.getColumnIndex(TABLE_COL_DND5E_NAME))
                    modifier?.mSWADEName = it.getString(it.getColumnIndex(TABLE_COL_SWADE_NAME))
                    val genericDescriptionLow = it.getString(it.getColumnIndex(TABLE_COL_GENERIC_DESCRIPTION_LOW))
                    val genericDescriptionMed = it.getString(it.getColumnIndex(TABLE_COL_GENERIC_DESCRIPTION_MED))
                    val genericDescriptionHigh = it.getString(it.getColumnIndex(TABLE_COL_GENERIC_DESCRIPTION_HIGH))
                    val genericPairLow = Pair<String, String?>(DAMAGE_STRING_LOW, genericDescriptionLow)
                    val genericPairMed = Pair<String, String?>(DAMAGE_STRING_MED, genericDescriptionMed)
                    val genericPairHigh = Pair<String, String?>(DAMAGE_STRING_HIGH, genericDescriptionHigh)
                    modifier?.mGenericDescriptions = mapOf(genericPairLow, genericPairMed, genericPairHigh)
                    val dnd5eDescriptionLow = it.getString(it.getColumnIndex(TABLE_COL_DND5E_DESC_LOW))
                    val dnd5eDescriptionMed = it.getString(it.getColumnIndex(TABLE_COL_DND5E_DESC_MEDIUM))
                    val dnd5eDescriptionHigh = it.getString(it.getColumnIndex(TABLE_COL_DND5E_DESC_HIGH))
                    val dnd5ePairLow = Pair<String, String?>(DAMAGE_STRING_LOW, dnd5eDescriptionLow)
                    val dnd5ePairMed = Pair<String, String?>(DAMAGE_STRING_MED, dnd5eDescriptionMed)
                    val dnd5ePairHigh = Pair<String, String?>(DAMAGE_STRING_HIGH, dnd5eDescriptionHigh)
                    modifier?.mDND5EDescriptions = mapOf(dnd5ePairLow, dnd5ePairMed, dnd5ePairHigh)
                    val swadeDescriptionLow = it.getString(it.getColumnIndex(TABLE_COL_SWADE_DESC_LOW))
                    val swadeDescriptionMed = it.getString(it.getColumnIndex(TABLE_COL_SWADE_DESC_MEDIUM))
                    val swadeDescriptionHigh = it.getString(it.getColumnIndex(TABLE_COL_SWADE_DESC_HIGH))
                    val swadePairLow = Pair<String, String?>(DAMAGE_STRING_LOW, swadeDescriptionLow)
                    val swadePairMed = Pair<String, String?>(DAMAGE_STRING_MED, swadeDescriptionMed)
                    val swadePairHigh = Pair<String, String?>(DAMAGE_STRING_HIGH, swadeDescriptionHigh)
                    modifier?.mSWADEDescriptions = mapOf(swadePairLow, swadePairMed, swadePairHigh)
                    modifier?.mDND5EPageInfo = it.getString(it.getColumnIndex(TABLE_COL_DND5E_PAGE))
                    modifier?.mSWADEPageInfo = it.getString(it.getColumnIndex(TABLE_COL_SWADE_PAGE))
                }
            }
        }
        db?.close()
        return modifier
    }

    fun getGameplayAttributeByNameAndSystem(name: String?, system: Int?): String? {

        var workingString = name

        //TODO need to find out why mDatabase is null here...
        val db = when {
            (mDatabase == null) -> {
                DatabaseHelper(mContext).readableDatabase
            }
            else -> mDatabase
        }
        if (db != null) {
            val selectQuery = "SELECT * FROM $DB_GAMEPLAY_ATTRIBUTES_TABLE_NAME WHERE $TABLE_COL_NAME = ?"

            db?.rawQuery(selectQuery, arrayOf(name)).use {
                // .use requires API 16
                if (it!!.moveToFirst()) {
                    workingString = when (system) {
                        RPG_SYSTEM_D20 -> it.getString(it.getColumnIndex(TABLE_COL_DND5E_NAME))
                        RPG_SYSTEM_SAVAGEWORLDS -> it.getString(it.getColumnIndex(TABLE_COL_SWADE_NAME))
                        else -> it.getString(it.getColumnIndex(TABLE_COL_GENERIC_NAME))
                    }
                }
            }
        }
        db?.close()
        return workingString
    }

    private fun getIntForCardBackground(): Int? {
        val previousImages = mPreferencesManager?.getPreviousCardImages()
        Log.d(TAG, "init - previousImages = $previousImages")
        val backgroundsCount = mPreferencesManager?.getListOfCardBackgroundImagesCount()
        var selectedInt: Int? = null
        var loopLimit = 0
        val randomNumber = (0 until backgroundsCount).random()
        while (selectedInt == null && loopLimit < 50) {
            loopLimit++
            if (previousImages != null && previousImages!!.count() > 0) {
                if (!previousImages.contains(randomNumber)) {
                    selectedInt = randomNumber
                }
            } else {
                selectedInt = randomNumber
            }
        }
        if (selectedInt != null) {
            mPreferencesManager?.addToPreviousCardImages(selectedInt)
        }
        Log.d(TAG, "init - selectedInt = $selectedInt")
        return selectedInt
    }
}