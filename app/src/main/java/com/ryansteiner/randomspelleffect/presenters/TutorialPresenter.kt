package com.ryansteiner.randomspelleffect.presenters

import android.content.Context
import android.content.SharedPreferences
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.ryansteiner.randomspelleffect.contracts.BaseContract
import com.ryansteiner.randomspelleffect.contracts.StartupContract
import com.ryansteiner.randomspelleffect.contracts.TutorialContract
import com.ryansteiner.randomspelleffect.data.*
import com.ryansteiner.randomspelleffect.utils.MyDatabaseUtils
import com.ryansteiner.randomspelleffect.utils.PreferencesManager
import java.lang.ref.WeakReference

/**
 * Created by Ryan Steiner on 2019/12/30.
 */

class TutorialPresenter(context: Context) : BasePresenter<TutorialContract.View>(context), TutorialContract.Presenter {

    private val TAG = "TutorialPresenter"
    private var mPreferencesManager: PreferencesManager? = null
    private var mDatabase: SQLiteDatabase? = null

    override fun load() {
        val view: TutorialContract.View? = getView()


        mPreferencesManager = PreferencesManager(mContext)

        val onboardInt = mPreferencesManager?.getHasBeenOnboarded() ?: -1

        val hasBeenOnboarded = when {
            onboardInt > 0 -> {true}
            else -> {false}

        }

        when (hasBeenOnboarded) {

            true -> {
                //do nothing
            }
            else -> {
                //Set defaults
                mPreferencesManager?.setTargets(caster = true, nearestAlly = true, nearestEnemy = true, nearestCreature = true)
                mPreferencesManager?.setDamagePreferences(null)
                mPreferencesManager?.setGameEffects(gamePlay = true, rolePlay = true)
                mPreferencesManager?.selectSystem(RPG_SYSTEM_GENERIC)
            }
        }

        val database = MyDatabaseUtils(mContext).loadDatabase()
        mDatabase = database



        view?.onLoaded()
    }

    override fun getSpellEffects() {
        Log.d(TAG, "getSpellEffects  [${mPreferencesManager?.getCurrentLifeTime()}]")

        val view: TutorialContract.View? = getView()
        val count = DatabaseUtils.queryNumEntries(mDatabase, DB_SPELLEFFECT_TABLE_NAME).toInt()
        var previousCards = mPreferencesManager?.getPreviousCardsList() ?: listOf()
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
        mutList.add("53")

        while (mutList.count() < NUMBER_OF_CARDS_TO_LOAD) {
            val random = (1..count).random()
            val randomString = random.toString()
            if (!mutList.contains(randomString) && !previousCards.contains(randomString)) {
                mutList.add(randomString)
            }
        }
        mPreferencesManager?.addToPreviousCardsList(mutList)

        val listOfSpellEffects = MyDatabaseUtils(mContext).getSpellEffectsByIds(mutList)
        view?.onGetSpellEffects(listOfSpellEffects)


    }


}