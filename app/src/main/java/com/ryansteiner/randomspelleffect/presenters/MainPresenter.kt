package com.ryansteiner.randomspelleffect.presenters

import android.content.Context
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import androidx.core.content.ContextCompat
import com.ryansteiner.randomspelleffect.R
import com.ryansteiner.randomspelleffect.contracts.MainContract
import com.ryansteiner.randomspelleffect.data.*
import com.ryansteiner.randomspelleffect.data.models.*
import com.ryansteiner.randomspelleffect.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.util.logging.Handler
import android.R.attr.data
import android.app.Activity
import com.ryansteiner.randomspelleffect.data.models.Song
import com.ryansteiner.randomspelleffect.data.models.Spell
import com.ryansteiner.randomspelleffect.views.activities.MainActivity


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
        Log.d(TAG, "initializeView  [${mPreferencesManager?.getCurrentLifeTime()}]")
        val view: MainContract.View? = getView()
        getPreferences()


        view?.onInitializedView()
    }

    override fun loadDatabase(context: Context) {
        Log.d(TAG, "loadDatabase  [${mPreferencesManager?.getCurrentLifeTime()}]")
        val view: MainContract.View? = getView()
        val database = MyDatabaseUtils(mContext).loadDatabase()
        mDatabase = database
        view?.onLoadedDatabase()
    }

    override fun generateSingleSpellEffect() {
        Log.d(TAG, "generateSingleSpellEffect  [${mPreferencesManager?.getCurrentLifeTime()}]")
        val view: MainContract.View? = getView()

        val spellEffect = MyDatabaseUtils(mContext).getRandomSpellEffect()
        //val spellEffect = getSingleSpellEffect("34") //DEBUG
        if (spellEffect != null) {
            view?.onGeneratedSingleSpellEffect(spellEffect)
        } else {
            view?.test()
        }


    }

    override fun getSpellEffects() {
        Log.d(TAG, "getSpellEffects  [${mPreferencesManager?.getCurrentLifeTime()}]")

        val view: MainContract.View? = getView()
        val count = DatabaseUtils.queryNumEntries(mDatabase, DB_SPELLEFFECT_TABLE_NAME).toInt()
        val previousCards = mPreferencesManager?.getPreviousCardsList() ?: listOf()
        val mutList: MutableList<String> = mutableListOf()
        /*mutList.add("5") //0
        mutList.add("16") //1
        mutList.add("21") //2
        mutList.add("1") //3
        mutList.add("13") //4
        mutList.add("49") //5
        mutList.add("21") //6
        mutList.add("4") //7
        mutList.add("6") //8*/
        mutList.add("50")
        mutList.add("51")
        mutList.add("10")

        while (mutList.count() < NUMBER_OF_CARDS_TO_LOAD) {
            Log.d(TAG, "getSpellEffects - while loop")
            val random = (1..count).random().toString()
            if (!mutList.contains(random) && !previousCards.contains(random)) {
                mutList.add(random)
            }
        }
        Log.d(TAG, "getSpellEffects - previousCards = $previousCards")
        Log.d(TAG, "getSpellEffects - mutList = $mutList")

        val listOfSpellEffects = MyDatabaseUtils(mContext).getSpellEffectsByIds(mutList)
        view?.onGetSpellEffects(listOfSpellEffects)


    }


    private fun getSpellEffectsCompleted(list: List<SpellEffect>?) {
        val view: MainContract.View? = getView()

    }

    private fun getSingleSpellEffect(id: String): SpellEffect? {
        Log.d(TAG, "getSingleSpellEffect  [${mPreferencesManager?.getCurrentLifeTime()}]")
        val db = mDatabase
        val selectQuery = "SELECT  * FROM $DB_SPELLEFFECT_TABLE_NAME WHERE $TABLE_COL_ID = ?"

        db?.rawQuery(selectQuery, arrayOf(id)).use {
            // .use requires API 16
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
                val isNetLibramInt = it.getInt(it.getColumnIndex(TABLE_COL_ISNETLIBRAM))
                result.mIsNetLibram = when {
                    (isNetLibramInt >= 1) -> true
                    else -> false
                }
                return result
            }
        }
        return null
    }

    override fun getPreferences() {
        Log.d(TAG, "getPreferences  [${mPreferencesManager?.getCurrentLifeTime()}]")
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

    override fun parseSpellStringForVariables(string: String?, system: Int): ParseSpellEffectStringResult? {
        Log.d(TAG, "parseSpellStringForVariables  [${mPreferencesManager?.getCurrentLifeTime()}]")
        mSystem = system
        var finalString: String?
        var workingString = string
        var result = ParseSpellEffectStringResult()

        //TODO Need to make sure that if Settings doesn't have "CASTER" that it will exclude DB entries with requiresCaster=1
        when (workingString) {
            null -> return null
            else -> {
                workingString = parseTargetVariable(workingString)

                workingString = parseColorVariable(workingString)

                workingString = parseSingleInanimateObjectVariable(workingString)

                workingString = parsePluralInanimateObjectsVariable(workingString)

                workingString = parseCreatureVariable(workingString)

                val difficultTerrainResult = parseDifficultTerrainVariable(workingString)
                workingString = difficultTerrainResult?.mFullString
                if (difficultTerrainResult.mGameplayModifier != null) {
                    result.mGameplayModifier = difficultTerrainResult.mGameplayModifier
                }

                workingString = parseHiccupsVariable(workingString)

                workingString = parseMaterialVariable(workingString)


                val toughnessResult = parseToughnessVariable(workingString)
                workingString = toughnessResult?.mFullString
                if (toughnessResult.mGameplayModifier != null) {
                    result.mGameplayModifier = toughnessResult.mGameplayModifier
                }


                val songPair = parseSongVariable(workingString)
                workingString = songPair.first
                result.mSong = songPair.second

                val spellPair = parseSpellVariable(workingString)
                workingString = spellPair.first
                result.mSpell = spellPair.second

                finalString = workingString

                result.mFullString = finalString

                return result
            }
        }
    }

    private fun parseTargetVariable(string: String?): String? {
        Log.d(TAG, "parseTargetVariable  [${mPreferencesManager?.getCurrentLifeTime()}]")
        //Change any TARGET text

        val targets = mPreferencesManager?.getTargets()

        var workingString = string
        Log.d(TAG, "parseTargetVariable - string = $string")
        if (workingString != null && workingString.contains("TARGET")) {
            Log.d(TAG, "parseTargetVariable - targets = $targets")
            var targetText = ""
            val availableTargets: MutableMap<String, Int> = mutableMapOf()
            if (!targets.isNullOrEmpty()) {
                for ((k, v) in targets) {
                    if (v) {
                        availableTargets[k] = 100
                    }
                }
            }
            if (availableTargets.isNullOrEmpty()) {
                return workingString
            } else {
                for (i in 0 until availableTargets.count()) {
                    val div = 100 / availableTargets.count()
                    Log.d(TAG, "parseTargetVariable - div = $div")
                    val keys = availableTargets.keys.toTypedArray()
                    val currentKey = keys[i]
                    val multiplier = i + 1
                    availableTargets[currentKey] = div * multiplier
                    Log.d(TAG, "parseTargetVariable - currentKey - availableTargets[currentKey] = $currentKey - ${availableTargets[currentKey]}")
                }
            }
            val targetRandomSelect = (1..100).random()
            Log.d(TAG, "parseTargetVariable - targetRandomSelect = ${targetRandomSelect}")
            val filteredTargets = availableTargets.filter { it.value > targetRandomSelect }
            val selectedTargetKey = filteredTargets.keys.toTypedArray().firstOrNull()
            Log.d(TAG, "parseTargetVariable - selectedTargetKey = ${selectedTargetKey}")
            targetText = when (selectedTargetKey) {
                TARGET_CASTER -> mContext.resources.getString(R.string.caster)
                TARGET_NEAREST_ALLY -> mContext.resources.getString(R.string.nearest_ally)
                TARGET_NEAREST_ENEMY -> mContext.resources.getString(R.string.nearest_enemy)
                TARGET_NEAREST_CREATURE -> mContext.resources.getString(R.string.closest_living_creature)
                else -> mContext.resources.getString(R.string.target_string_error)

            }

            workingString = workingString.replace("TARGET", targetText)
            Log.d(TAG, "parseTargetVariable - workingString = $workingString")
            return workingString
        } else {
            return workingString
        }


    }

    private fun parseColorVariable(string: String?): String? {
        Log.d(TAG, "parseColorVariable  [${mPreferencesManager?.getCurrentLifeTime()}]")
        val view: MainContract.View? = getView()
        val gameEffectName = GAME_EFFECT_COLOR
        var workingString = string

        //Change any COLOR text
        if (workingString != null && workingString.contains(gameEffectName)) {
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


                    workingString = workingString.replace(gameEffectName, colorString)
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


                    workingString = workingString.replace(gameEffectName, patternString)
                    view?.updateColorLayer(null, false)
                    view?.updatePatternLayer(patternId, true)

                }
            }
        }
        return workingString
    }

    private fun parseSingleInanimateObjectVariable(string: String?): String? {
        Log.d(TAG, "parseSingleInanimateObjectVariable  [${mPreferencesManager?.getCurrentLifeTime()}]")
        var workingString = string
        val gameEffectName = GAME_EFFECT_SINGLE_INANIMATE_OBJECT

        if (workingString != null && workingString.contains(gameEffectName)) {
            Log.d(TAG, "Deal with any SINGLEIO in the text - workingString = $workingString")

            val randomObject = GameEffectsList.InanimateObjectsSingular().mAllObjects.random()

            workingString = workingString.replace(gameEffectName, randomObject)
        }

        return workingString
    }

    private fun parsePluralInanimateObjectsVariable(string: String?): String? {
        Log.d(TAG, "parsePluralInanimateObjectsVariable  [${mPreferencesManager?.getCurrentLifeTime()}]")
        var workingString = string
        val gameEffectName = GAME_EFFECT_MULTIPLE_INANIMATE_OBJECTS

        if (workingString != null && workingString.contains(gameEffectName)) {
            Log.d(TAG, "Deal with any PLURALIOS in the text - workingString = $workingString")

            val randomObject = GameEffectsList.InanimateObjectsPlural().mAllObjects.random()

            workingString = workingString.replace(gameEffectName, randomObject)
        }
        return workingString
    }

    private fun parseDifficultTerrainVariable(string: String?): ParseSpellEffectStringResult {
        Log.d(TAG, "parseDifficultTerrainVariable  [${mPreferencesManager?.getCurrentLifeTime()}]")
        val gameEffectName = GAME_EFFECT_DIFFICULT_TERRAIN
        var result = ParseSpellEffectStringResult()
        var gameplayModifier: GameplayModifier? = null
        var workingString = string
        val db = mDatabase

        if (workingString != null && workingString.contains(gameEffectName)) {
            val name = gameEffectName
            gameplayModifier = MyDatabaseUtils(mContext).getGameplayModifierByName(name)

            var selectedModifierName = ""

            if (gameplayModifier != null) {
                selectedModifierName = when (mSystem) {
                    RPG_SYSTEM_D20 -> gameplayModifier!!.mDND5EName ?: "ERROR with DND5E/Name"
                    RPG_SYSTEM_SAVAGEWORLDS -> gameplayModifier!!.mSWADEName ?: "ERROR with SWADE/Name"
                    else -> gameplayModifier!!.mGenericName ?: "ERROR with Generic/Name"
                }
            }

            workingString = workingString.replace(gameEffectName, selectedModifierName)
        }

        result.mFullString = workingString
        result.mGameplayModifier = gameplayModifier

        return result
    }

    private fun parseToughnessVariable(string: String?): ParseSpellEffectStringResult {
        Log.d(TAG, "parseDifficultTerrainVariable  [${mPreferencesManager?.getCurrentLifeTime()}]")
        val result = ParseSpellEffectStringResult()
        var gameplayModifier: GameplayModifier? = null
        var workingString = string
        val db = mDatabase
        val gameEffectNameReduce = GAME_EFFECT_REDUCE_TOUGHNESS
        val gameEffectNameIncrease = GAME_EFFECT_INCREASE_TOUGHNESS

        val damageOptions = mPreferencesManager?.getDamagePreferences() ?: listOf(DAMAGE_INT_LOW, DAMAGE_INT_MED, DAMAGE_INT_HIGH)

        val selectedDamageLevel = damageOptions.random()

        if (workingString != null && workingString.contains(gameEffectNameReduce)) {
            gameplayModifier = MyDatabaseUtils(mContext).getGameplayModifierByName(gameEffectNameReduce)

            val replacementName = gameplayModifier?.getNameBySystem(mSystem) ?: "ERROR"
            //val replacementDesc = gameplayModifier?.getDescriptionsBySystemAndLevel(mSystem, selectedDamageLevel) ?: "ERROR"
            //val replacementText = "$replacementName ($replacementDesc)"

            workingString = workingString.replace(gameEffectNameReduce, replacementName)

        } else if (workingString != null && workingString.contains(gameEffectNameIncrease)) {
            gameplayModifier = MyDatabaseUtils(mContext).getGameplayModifierByName(gameEffectNameIncrease)

            val replacementName = gameplayModifier?.getNameBySystem(mSystem) ?: "ERROR"
            //val replacementDesc = gameplayModifier?.getDescriptionsBySystemAndLevel(mSystem, selectedDamageLevel) ?: "ERROR"
            //val replacementText = "$replacementName ($replacementDesc)"

            workingString = workingString.replace(gameEffectNameIncrease, replacementName)
        }

        result.mFullString = workingString
        result.mGameplayModifier = gameplayModifier

        return result
    }

    private fun parseHiccupsVariable(string: String?): String? {
        Log.d(TAG, "parseHiccupsVariable  [${mPreferencesManager?.getCurrentLifeTime()}]")
        var workingString = string

        if (workingString != null && workingString.contains("HICCUPS")) {
            Log.d(TAG, "Deal with any HICCUPS in the text - workingString = $workingString")
            val hiccupEffect = GameEffectsList().getEffectByName("Hiccups")
            val hiccupBySystem = when (mSystem) {
                RPG_SYSTEM_D20 -> {
                    hiccupEffect?.mDND5EDescription
                }
                RPG_SYSTEM_SAVAGEWORLDS -> {
                    hiccupEffect?.mSWADEDescription
                }
                else -> {
                    "SYSTEM ERROR"
                }
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
        //TODO this needs to return a Results object so that I can do extra info for the critters
        Log.d(TAG, "parseCreatureVariable  [${mPreferencesManager?.getCurrentLifeTime()}]")
        var workingString = string

        val db = mDatabase
        val count = DatabaseUtils.queryNumEntries(db, DB_CREATURE_TABLE_NAME)
        val safeCount = count.toInt()
        //db?.close()
        val random = (1..safeCount).random()

        val creature = getCreatureById(db, random)

        if (workingString != null && workingString.contains("CREATURES")) {
            val creatureName = creature?.mNamePlural ?: ""
            workingString = workingString.replace("CREATURES", creatureName)
        } else if (workingString != null && workingString.contains("CREATURE")) {
            val creatureName = creature?.mName ?: ""
            workingString = workingString.replace("CREATURE", creatureName)

        }

        return workingString
    }

    private fun parseSpellVariable(string: String?): Pair<String?, Spell?> {
        Log.d(TAG, "parseSpellVariable  [${mPreferencesManager?.getCurrentLifeTime()}]")
        val view: MainContract.View? = getView()
        var workingString = string
        var visibility = false
        var selectedSpellDescriptionWithDamageLevel = ""
        var selectedSpellDice = ""
        var selectedSpellPageNumber = ""
        var spellText = ""
        var selectedSpell: Spell? = null
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
            /*if (mSpellsList == null) {
                mSpellsList = SpellsList(mContext)
            }*/
            selectedSpell = MyDatabaseUtils(mContext).getRandomSpell()
            Log.d(TAG, "parseSpellVariable - mSpellsList = $mSpellsList")
            Log.d(TAG, "parseSpellVariable - selectedSpell = $selectedSpell")
            spellText = selectedSpell?.mNameWithAAn ?: "ERROR"
            if (selectedSpell != null) {
                when (mSystem) {
                    RPG_SYSTEM_D20 -> {
                        selectedSpellDescriptionWithDamageLevel =
                            selectedSpell!!.mDND5EDescriptions!![selectedDamageLevel] ?: ""
                        selectedSpellDice = selectedSpell!!.mDND5EDice!![selectedDamageLevel] ?: ""
                        selectedSpellPageNumber = selectedSpell!!.mDND5EPageNumber ?: ""
                    }
                    RPG_SYSTEM_SAVAGEWORLDS -> {
                        selectedSpellDescriptionWithDamageLevel =
                            selectedSpell!!.mSWADEDescriptions!![selectedDamageLevel] ?: ""
                        selectedSpellDice = selectedSpell!!.mSWADEDice!![selectedDamageLevel] ?: ""
                        selectedSpellPageNumber = selectedSpell!!.mSWADEPageNumber ?: ""
                    }
                    else -> {
                        selectedSpellDescriptionWithDamageLevel =
                            selectedSpell!!.mSWADEDescriptions!![selectedDamageLevel] ?: "NO SYSTEM"
                        selectedSpellDice =
                            selectedSpell!!.mSWADEDice!![selectedDamageLevel] ?: "NO SYSTEM"
                        selectedSpellPageNumber = selectedSpell!!.mSWADEPageNumber ?: "NO SYSTEM"
                    }
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
        Log.d(TAG, "parseSpellVariable - selectedSpell = $selectedSpell")
        return Pair(workingString, selectedSpell)
    }


    private fun parseSongVariable(string: String?): Pair<String?, Song?> {
        Log.d(TAG, "parseSongVariable  [${mPreferencesManager?.getCurrentLifeTime()}]")
        val view: MainContract.View? = getView()

        var workingString = string
        var song: Song? = null

        if (workingString != null && workingString.contains("SONG")) {

            val db = mDatabase
            val count = DatabaseUtils.queryNumEntries(db, DB_SONGS_TABLE_NAME)
            val safeCount = count.toInt()
            //db?.close()
            val random = (1..safeCount).random()

            song = getSongById(db, random)
            Log.d(TAG, "parseSongVariable - song = $song")
            val songTitle = song?.mName ?: "SONG TITLE ERROR"
            val songArtist = song?.mArtist ?: ""
            val fullSongString = when (songArtist.isNullOrBlank()) {
                true -> "\"$songTitle\""
                else -> "\"$songTitle\" by $songArtist"
            }
            workingString = workingString.replace("SONG", fullSongString)
            val url = song?.mUrl
            /*if (!url.isNullOrBlank()) {
                view?.songVideoInit(true, song)
            }*/

        }

        return Pair(workingString, song)
    }

    private fun rollDice() {

    }

    override fun updateDamagePreferences(damagePrefs: List<Int>?) {
        Log.d(TAG, "updateDamagePreferences  [${mPreferencesManager?.getCurrentLifeTime()}]")
        mDamagePreferences = damagePrefs
    }

    override fun updateSpellList(spellsList: SpellsList) {
        Log.d(TAG, "updateSpellList  [${mPreferencesManager?.getCurrentLifeTime()}]")
        mSpellsList = spellsList
    }

    override fun clickSettings(showSettings: Boolean) {
        val view: MainContract.View? = getView()
        view?.onClickSettings(showSettings)
    }

    override fun retrieveSpellEffectById(id: Int) {
        Log.d(TAG, "retrieveSpellEffectById  [${mPreferencesManager?.getCurrentLifeTime()}]")
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