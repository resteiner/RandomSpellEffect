package com.ryansteiner.randomspelleffect.presenters

import android.content.Context
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import android.view.View.GONE
import androidx.core.content.ContextCompat
import com.ryansteiner.randomspelleffect.R
import com.ryansteiner.randomspelleffect.contracts.BaseContract
import com.ryansteiner.randomspelleffect.contracts.MainContract
import com.ryansteiner.randomspelleffect.contracts.StartupContract
import com.ryansteiner.randomspelleffect.data.*
import com.ryansteiner.randomspelleffect.data.models.SpellEffect
import com.ryansteiner.randomspelleffect.utils.*
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.ref.WeakReference
import kotlin.math.roundToInt
import androidx.room.RoomMasterTable.TABLE_NAME



/**
 * Created by Ryan Steiner on 2019/11/06.
 */

class MainPresenter(context: Context) : BasePresenter<MainContract.View>(context), MainContract.Presenter {

    private val TAG = "MainPresenter"
    private var mDatabase: SQLiteDatabase? = null
    private var mPreferencesManager: PreferencesManager? = null
    private var mSystem: Int? = null
    private var mDamagePreferences: List<Int>? = null
    private var mSpellsList: SpellsList? = null

    override fun initializeView() {
        val view: MainContract.View? = getView()
        getPreferences()


        view?.onInitializedView()
    }

    override fun loadDatabase(context: Context) {
        val view: MainContract.View? = getView()

        val database = DatabaseHelper(context).readableDatabase
        mDatabase = database
        view?.onLoadedDatabase(database)
    }

    override fun generateSingleSpellEffect() {
        val view: MainContract.View? = getView()

        val totalCount = DatabaseUtils.queryNumEntries(mDatabase, DB_SPELLEFFECT_TABLE_NAME)
        val randomSelection = (1..totalCount).random()
        val randomizedId = randomSelection.toString()



        Log.d(TAG, "generateSingleSpellEffect - DB_SPELLEFFECT_TABLE_NAME = $DB_SPELLEFFECT_TABLE_NAME")
        Log.d(TAG, "generateSingleSpellEffect - totalCount = $totalCount")
        Log.d(TAG, "generateSingleSpellEffect - randomSelection = $randomSelection")
        Log.d(TAG, "generateSingleSpellEffect - randomizedId = $randomizedId")

        val spellEffect = getSingleSpellEffect(randomizedId)
        //val spellEffect = getSingleSpellEffect("34") //DEBUG
        if (spellEffect != null) {
            view?.onGeneratedSingleSpellEffect(spellEffect)
        } else {
            view?.test()
        }


    }

    private fun getSingleSpellEffect(id: String): SpellEffect? {
        val db = mDatabase
        val selectQuery = "SELECT  * FROM $DB_SPELLEFFECT_TABLE_NAME WHERE $TABLE_COL_ID = ?"


        Log.d(TAG, "getSingleSpellEffect - id = $id")
        Log.d(TAG, "getSingleSpellEffect - selectQuery = $selectQuery")
        Log.d(TAG, "getSingleSpellEffect - db = $db")
        Log.d(TAG, "getSingleSpellEffect - DB_SPELLEFFECT_TABLE_NAME = $DB_SPELLEFFECT_TABLE_NAME")
        Log.d(TAG, "getSingleSpellEffect - TABLE_COL_ID = $TABLE_COL_ID")
        Log.d(TAG, "getSingleSpellEffect - TABLE_COL_DESCRIPTION = $TABLE_COL_DESCRIPTION")
        Log.d(TAG, "getSingleSpellEffect - TABLE_COL_TYPE = $TABLE_COL_TYPE")
        Log.d(TAG, "getSingleSpellEffect - TABLE_COL_TARGET = $TABLE_COL_TARGET")
        Log.d(TAG, "getSingleSpellEffect - TABLE_COL_HASGAMEPLAYIMPACT = $TABLE_COL_HASGAMEPLAYIMPACT")
        Log.d(TAG, "getSingleSpellEffect - TABLE_COL_TAGS = $TABLE_COL_TAGS")
        Log.d(TAG, "getSingleSpellEffect - TABLE_COL_HOWBADISIT = $TABLE_COL_HOWBADISIT")


        db?.rawQuery(selectQuery, arrayOf(id)).use { // .use requires API 16
            if (it!!.moveToFirst()) {
                val result = SpellEffect()
                result.mId = it.getInt(it.getColumnIndex(TABLE_COL_ID))
                result.mDescription = it.getString(it.getColumnIndex(TABLE_COL_DESCRIPTION))
                result.mType = it.getInt(it.getColumnIndex(TABLE_COL_TYPE))
                result.mTarget = it.getInt(it.getColumnIndex(TABLE_COL_TARGET))
                result.mHasGameplayImpact = it.getInt(it.getColumnIndex(TABLE_COL_HASGAMEPLAYIMPACT))
                result.mTags = it.getString(it.getColumnIndex(TABLE_COL_TAGS))
                result.mHowBadIsIt = it.getInt(it.getColumnIndex(TABLE_COL_HOWBADISIT))
                result.mUsesImage = it.getString(it.getColumnIndex(TABLE_COL_USESIMAGE))
                return result
            }
        }
        return null
    }

    override fun getPreferences() {
        val view: MainContract.View? = getView()

        if (mPreferencesManager == null) {
            mPreferencesManager = PreferencesManager(mContext)
        }

        val systemInt = mPreferencesManager?.getSystem()
        val systemString = when (systemInt) {
            RPG_SYSTEM_D20 -> "D20"
            RPG_SYSTEM_SAVAGEWORLDS -> "Savage Worlds"
            else -> "ERROR"
        }

        view?.updateDebugText(systemString)
        view?.updatePreferences(mPreferencesManager)

    }

    override fun parseSpellStringForVariables(string: String?, system: Int): String? {
        mSystem = system
        var finalString: String?
        var workingString = string
        when (workingString) {
            null -> return workingString
            else -> {
                workingString = parseTargetVariable(workingString)
                Log.d(TAG, "parseSpellStringForVariables - parseTargetVariable(workingString) = $workingString")
                workingString = parseColorVariable(workingString)
                Log.d(TAG, "parseSpellStringForVariables - parseColorVariable(workingString) = $workingString")
                workingString = parseSingleInanimateObjectVariable(workingString)
                Log.d(TAG, "parseSpellStringForVariables - parseSingleInanimateObjectVariable(workingString) = $workingString")
                workingString = parsePluralInanimateObjectsVariable(workingString)
                Log.d(TAG, "parseSpellStringForVariables - parsePluralInanimateObjectsVariable(workingString) = $workingString")
                workingString = parseCreatureVariable(workingString)
                Log.d(TAG, "parseSpellStringForVariables - parseCreatureVariable(workingString) = $workingString")
                workingString = parseDifficultTerrainVariable(workingString)
                Log.d(TAG, "parseSpellStringForVariables - parseDifficultTerrainVariable(workingString) = $workingString")
                workingString = parseHiccupsVariable(workingString)
                Log.d(TAG, "parseSpellStringForVariables - parseHiccupsVariable(workingString) = $workingString")
                workingString = parseMaterialVariable(workingString)
                Log.d(TAG, "parseSpellStringForVariables - parseMaterialVariable(workingString) = $workingString")
                workingString = parseSpellVariable(workingString)
                Log.d(TAG, "parseSpellStringForVariables - parseSpellVariable(workingString) = $workingString")

                finalString = workingString
                return finalString
            }
        }
    }

    private fun parseTargetVariable(string: String?): String? {
        //Change any TARGET text
        var workingString = string
        if (workingString != null && workingString.contains("TARGET")) {
            Log.d(TAG, "parseTargetVariable - TARGET condition true")
            val targetRandomSelect = (1..100).random()
            val targetText = when {
                targetRandomSelect > 75 -> "caster"
                targetRandomSelect > 50 -> "nearest enemy"
                targetRandomSelect > 25 -> "nearest ally"
                targetRandomSelect > 0 -> "closest living creature to the caster"
                else -> "TARGET ERROR"
            }
            workingString = workingString.replace("TARGET", targetText)
            Log.d(TAG, "parseTargetVariable - workingString = $workingString")
            return workingString
        } else {
            return workingString
        }


    }

    private fun parseColorVariable(string: String?): String? {
        val view: MainContract.View? = getView()
        var workingString = string

        //Change any COLOR text
        if (workingString != null && workingString.contains("COLOR")) {
            //val totalNumberOfColors = spellEffect.colors.count()
            //val randomSelection = ((1..totalNumberOfColors).random()) - 1

            val randomColorOrPattern = (1..100).random()
            val gameColors = GameEffectsList.gameColors()
            when {
                randomColorOrPattern > 40 -> { //use a color

                    val colorMap = gameColors.mMapOfColors

                    val upperLimit = colorMap!!.size - 1
                    val random = (0..upperLimit).random()
                    val mapEntry = colorMap!!.entries.elementAt(random)
                    val colorString = mapEntry.component1()
                    val colorIdInt = mapEntry.component2()
                    val colorId = ContextCompat.getColor(mContext, colorIdInt)
                    Log.d(TAG, "onGeneratedSingleSpellEffect - COLOR colorString = $colorString")
                    Log.d(TAG, "onGeneratedSingleSpellEffect - COLOR colorIdInt = $colorIdInt")
                    Log.d(TAG, "onGeneratedSingleSpellEffect - COLOR colorId = $colorId")


                    workingString = workingString.replace("COLOR", colorString)
                    view?.updateColorLayer(colorId, true)
                    view?.updatePatternLayer(null, false)
                }
                else -> { //use a pattern

                    val patternMap = gameColors.mMapOfPatterns

                    val upperLimit = patternMap!!.size - 1
                    val random = (0..upperLimit).random()
                    val mapEntry = patternMap!!.entries.elementAt(random)
                    val patternString = mapEntry.component1()
                    val patternId = mapEntry.component2()
                    Log.d(TAG, "onGeneratedSingleSpellEffect - COLOR patternString = $patternString")
                    Log.d(TAG, "onGeneratedSingleSpellEffect - COLOR patternId = $patternId")


                    workingString = workingString.replace("COLOR", patternString)
                    view?.updateColorLayer(null, false)
                    view?.updatePatternLayer(patternId, true)

                }
            }
        }
        return workingString
    }
    private fun parseSingleInanimateObjectVariable(string: String?): String? {
        var workingString = string

        if (workingString != null && workingString.contains("SINGLEIO")) {
            Log.d(TAG, "Deal with any SINGLEIO in the text - workingString = $workingString")

            val randomObject = GameEffectsList.InanimateObjectsSingular().mAllObjects.random()

            workingString = workingString.replace("SINGLEIO", randomObject)
        }

        return workingString
    }
    private fun parsePluralInanimateObjectsVariable(string: String?): String? {
        var workingString = string

        if (workingString != null && workingString.contains("PLURALIOS")) {
            Log.d(TAG, "Deal with any PLURALIOS in the text - workingString = $workingString")

            val randomObject = GameEffectsList.InanimateObjectsPlural().mAllObjects.random()

            workingString = workingString.replace("PLURALIOS", randomObject)
        }
        return workingString
    }
    private fun parseDifficultTerrainVariable(string: String?): String? {
        var workingString = string

        if (workingString != null && workingString.contains("DIFFICULTTERRAIN")) {
            val dTEffect = GameEffectsList().getEffectByName("Difficult Terrain")
            val difficultTerrainText = when (mSystem) {
                RPG_SYSTEM_D20 -> dTEffect?.mDND5EName ?: ""
                RPG_SYSTEM_SAVAGEWORLDS -> dTEffect?.mSWADEName ?: ""
                else -> "ERROR"
            }
            workingString = workingString.replace("DIFFICULTTERRAIN", difficultTerrainText)

        }
        return workingString
    }
    private fun parseHiccupsVariable(string: String?): String? {
        var workingString = string

        if (workingString != null && workingString.contains("HICCUPS")) {
            Log.d(TAG, "Deal with any HICCUPS in the text - workingString = $workingString")
            val hiccupEffect = GameEffectsList().getEffectByName("Hiccups")
            val hiccupBySystem = when (mSystem) {
                RPG_SYSTEM_D20 -> {hiccupEffect?.mDND5EDescription}
                RPG_SYSTEM_SAVAGEWORLDS -> {hiccupEffect?.mSWADEDescription}
                else -> {"SYSTEM ERROR"}
            }
            val hiccupText = "${hiccupEffect?.mName} causes $hiccupBySystem"
            workingString = workingString.replace("HICCUPS", hiccupText)

        }

        return workingString
    }
    private fun parseMaterialVariable(string: String?): String? {
        var workingString = string

        if (workingString != null && workingString.contains("MATERIAL")) {
            val allMaterials = GameEffectsList.objectMaterials().mAllMaterials
            val hiccupText = allMaterials.random()
            workingString = workingString.replace("MATERIAL", hiccupText)

        }

        return workingString
    }

    private fun parseCreatureVariable(string: String?): String? {
        var workingString = string

        if (workingString != null && workingString.contains("CREATURE")) {

            val db = mDatabase
            val count = DatabaseUtils.queryNumEntries(db, DB_CREATURE_TABLE_NAME)
            val safeCount = count.toInt()
            //db?.close()
            val random = (1..safeCount).random()

            val creature = getCreatureById(db, random)
            val creatureName = creature?.mName ?: ""
            workingString = workingString.replace("CREATURE", creatureName)

        }

        return workingString
    }

    private fun parseSpellVariable(string: String?): String? {
        val view: MainContract.View? = getView()
        var workingString = string
        var visibility = false
        var selectedSpellDescriptionWithDamageLevel = ""
        var selectedSpellDice = ""
        var selectedSpellPageNumber = ""
        var spellText = ""
        Log.d(TAG, "parseSpellVariable - workingString 1 = $workingString")


        //Deal with any SPELL in the text
        Log.d(TAG, "parseSpellVariable - mSystem = $mSystem")
        val damageOptions = mutableListOf<String>()
        if (workingString != null && workingString.contains("SPELL")) {
            Log.d(TAG, "parseSpellVariable - workingString 2 = $workingString")

            if (mDamagePreferences != null && mDamagePreferences!!.count() > 0) {
                if (mDamagePreferences!!.contains(DAMAGE_INT_LOW)) {
                    damageOptions.add(DAMAGE_STRING_LOW)
                }
                if (mDamagePreferences!!.contains(DAMAGE_INT_MED)) {
                    damageOptions.add(DAMAGE_STRING_MED)
                }
                if (mDamagePreferences!!.contains(DAMAGE_INT_HIGH)) {
                    damageOptions.add(DAMAGE_STRING_HIGH)
                }
            } else {
                damageOptions.add(DAMAGE_STRING_LOW)
                damageOptions.add(DAMAGE_STRING_MED)
                damageOptions.add(DAMAGE_STRING_HIGH)
            }

            val selectedDamageLevel = damageOptions.random()
            val selectedSpell = mSpellsList?.getRandomSpell()
            spellText = selectedSpell?.mTitle ?: "ERROR"
            when (mSystem) {
                RPG_SYSTEM_D20 -> {
                    selectedSpellDescriptionWithDamageLevel = selectedSpell!!.mDND5EDescriptions!![selectedDamageLevel] ?: ""
                    selectedSpellDice = selectedSpell!!.mDND5EDice!![selectedDamageLevel] ?: ""
                    selectedSpellPageNumber = selectedSpell!!.mDND5EPageNumber ?: ""
                }
                RPG_SYSTEM_SAVAGEWORLDS -> {
                    selectedSpellDescriptionWithDamageLevel = selectedSpell!!.mSWADEDescriptions!![selectedDamageLevel] ?: ""
                    selectedSpellDice = selectedSpell!!.mSWADEDice!![selectedDamageLevel] ?: ""
                    selectedSpellPageNumber = selectedSpell!!.mSWADEPageNumber ?: ""
                }
                else -> {
                    selectedSpellDescriptionWithDamageLevel = selectedSpell!!.mSWADEDescriptions!![selectedDamageLevel] ?: "NO SYSTEM"
                    selectedSpellDice = selectedSpell!!.mSWADEDice!![selectedDamageLevel] ?: "NO SYSTEM"
                    selectedSpellPageNumber = selectedSpell!!.mSWADEPageNumber ?: "NO SYSTEM"
                }
            }



            visibility = true
            view?.updateDiceRoll(selectedSpellDice)

            Log.d(TAG, "parseSpellVariable - workingString 3 = $workingString")
            workingString = workingString.replace("SPELL", spellText)


            //tSpellDiceRoll.text = selectedSpellDice

        }

        view?.updateSpellInfoContainer(visibility, spellText, selectedSpellDescriptionWithDamageLevel, selectedSpellPageNumber)


        Log.d(TAG, "parseSpellVariable - workingString 4 = $workingString")
        return workingString
    }

    private fun rollDice() {

    }

    override fun updateDamagePreferences(damagePrefs: List<Int>?) {
        mDamagePreferences = damagePrefs
    }

    override fun updateSpellList(spellsList: SpellsList) {
        mSpellsList = spellsList
    }

    override fun clickSettings(showSettings: Boolean) {
        val view: MainContract.View? = getView()
        view?.onClickSettings(showSettings)
    }

    override fun retrieveSpellEffectById(id: Int) {
        val view: MainContract.View? = getView()
        
        if (id > 0) {

            val stringedId = id.toString()
            val spellEffect = getSingleSpellEffect(stringedId)
            if (spellEffect != null) {
                view?.onGeneratedSingleSpellEffect(spellEffect)
            } else {
                view?.test()
            }


        } else {
            view?.test()

        }
    }
}