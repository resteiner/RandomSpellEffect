package com.ryansteiner.randomspelleffect.utils

import android.content.Context
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteQueryBuilder
import android.util.Log
import com.ryansteiner.randomspelleffect.data.*
import com.ryansteiner.randomspelleffect.data.models.SpellEffect
import android.R.id



class MyDatabaseUtils(context: Context) {

    private val TAG = "MyDatabaseUtils"
    private var mDatabase: SQLiteDatabase? = null
    private val mContext = context

    fun loadDatabase(): SQLiteDatabase {
        val database = DatabaseHelper(mContext).readableDatabase
        Log.d(TAG, "loadDatabase - database = $database")
        updateDatabaseReference(database)
        return database
    }

    fun updateDatabaseReference(db: SQLiteDatabase?) {
        if (db != null) {
            mDatabase = db
        }
        Log.d(TAG, "updateDatabaseReference - mDatabase = $mDatabase")
    }

    fun getRandomSpellEffect(): SpellEffect? {
        val totalCount = DatabaseUtils.queryNumEntries(mDatabase, DB_SPELLEFFECT_TABLE_NAME)
        val randomSelection = (1..totalCount).random()
        val randomizedId = randomSelection.toString()



        Log.d(
            TAG,
            "generateSingleSpellEffect - DB_SPELLEFFECT_TABLE_NAME = $DB_SPELLEFFECT_TABLE_NAME"
        )
        Log.d(TAG, "generateSingleSpellEffect - totalCount = $totalCount")
        Log.d(TAG, "generateSingleSpellEffect - randomSelection = $randomSelection")
        Log.d(TAG, "generateSingleSpellEffect - randomizedId = $randomizedId")

        val spellEffect = getSpellEffectById(randomizedId)

        return spellEffect
    }

    fun getSpellEffectById(id: String): SpellEffect? {
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
                    db?.close()
                    return result
                }
            }
            db?.close()
            return null
        } else {
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
        Log.d(TAG, "getSpellEffectsByIds - mDatabase = $mDatabase")
        Log.d(TAG, "getSpellEffectsByIds - db = $db")

        //TODO There should be a way to get several results from one query, but I keep getting: Cannot bind argument at index 1 because the index is out of range.  The statement has 0 parameters.
        var list = mutableListOf<SpellEffect>()

        if (db != null && ids != null && ids.count() > 0) {
            ids.forEach {
                Log.d(TAG, "getSpellEffectsByIds - ids(it) = $it")
                val spellEffect = getSpellEffectById(it)
                Log.d(TAG, "getSpellEffectsByIds - spellEffect = $spellEffect")
                if (spellEffect != null) {
                    list.add(spellEffect)
                }
            }
        }
        return list
    }
}