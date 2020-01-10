package com.ryansteiner.randomspelleffect.presenters

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.ryansteiner.randomspelleffect.contracts.BaseContract
import com.ryansteiner.randomspelleffect.contracts.StartupContract
import com.ryansteiner.randomspelleffect.data.RPG_SYSTEM_D20
import com.ryansteiner.randomspelleffect.utils.PreferencesManager
import java.lang.ref.WeakReference

/**
 * Created by Ryan Steiner on 2019/11/06.
 */

class StartupPresenter(context: Context) : BasePresenter<StartupContract.View>(context), StartupContract.Presenter {

    private val TAG = "StartupPresenter"
    private var mPreferencesManager: PreferencesManager? = null
    //private val mContext = context

    override fun load() {
        val view: StartupContract.View? = getView()


        mPreferencesManager = PreferencesManager(mContext)
        mPreferencesManager?.setAppLifeTimeStart(System.currentTimeMillis())
        Log.d(TAG, "load  [${mPreferencesManager?.getCurrentLifeTime()}]")
        var currentRPGSystem = mPreferencesManager?.getSystem()
        when {

            (currentRPGSystem == null || currentRPGSystem <= 0) -> {
                Log.d(TAG, "load - currentRPGSystem <= 0 = $currentRPGSystem")
                mPreferencesManager?.selectSystem(RPG_SYSTEM_D20)
                currentRPGSystem = mPreferencesManager?.getSystem()
            }
            else -> {}
        }
        val currentDamagePrefs = mPreferencesManager?.getDamagePreferences()

        when {

            (currentDamagePrefs == null || currentDamagePrefs.count() <= 0) -> {
                mPreferencesManager?.setDamagePreferences(null)
            }
            else -> {}
        }

        val currentTargetSettings = mPreferencesManager?.getTargets()
        var setDefaultTargets = false
        if (currentTargetSettings.isNullOrEmpty()) {
            setDefaultTargets = true
        } else {
            var counter = 0
            for ((k,v) in currentTargetSettings) {
                    if (v) {
                        counter++
                    }
            }
            setDefaultTargets = counter <= 0

        }

        if (setDefaultTargets) {
            mPreferencesManager?.setTargets(true, true, true, true)
        }
        Log.d(TAG, "load - mPreferencesManager?.getTargets() AFTER = ${mPreferencesManager?.getTargets()}")

        //TODO I don't think we actually need the onboard check here
        val hasBeenOnboardedInt = mPreferencesManager?.getHasBeenOnboarded() ?: -1
        val hasBeenOnboarded = when  {
            (hasBeenOnboardedInt > 0) -> true
            else -> false
        }

        view?.onLoaded()
    }
}