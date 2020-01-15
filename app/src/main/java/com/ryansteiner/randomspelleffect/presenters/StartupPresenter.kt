package com.ryansteiner.randomspelleffect.presenters

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import android.view.Window
import com.ryansteiner.randomspelleffect.contracts.BaseContract
import com.ryansteiner.randomspelleffect.contracts.MainContract
import com.ryansteiner.randomspelleffect.contracts.StartupContract
import com.ryansteiner.randomspelleffect.data.RPG_SYSTEM_D20
import com.ryansteiner.randomspelleffect.data.RPG_SYSTEM_GENERIC
import com.ryansteiner.randomspelleffect.data.RPG_SYSTEM_SAVAGEWORLDS
import com.ryansteiner.randomspelleffect.utils.PreferencesManager
import com.ryansteiner.randomspelleffect.views.activities.MainActivity
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

        val hasBeenOnboardedInt = mPreferencesManager?.getHasBeenOnboarded() ?: -1
        val hasBeenOnboarded = when  {
            (hasBeenOnboardedInt > 0) -> true
            else -> false
        }

        Log.d(TAG, "updateSystem - mPreferencesManager?.getSystem() = ${mPreferencesManager?.getSystem()}")
        //DEBUG//////////////////////////////////////////////////////////
        //Force tutorial
        ////////////////////////////////////////////////////////////////
        //mPreferencesManager?.setHasBeenOnboarded(0)
        view?.onLoaded(hasBeenOnboarded)
    }

    override fun updateSystem(system: Int?) {
        val view: StartupContract.View? = getView()
        val safeSystem = if (system == null || system < 0) {
            RPG_SYSTEM_GENERIC
        } else {
            system
        }
        Log.d(TAG, "[SystemIssue] updateSystem - system = $system")
        Log.d(TAG, "[SystemIssue] updateSystem - safeSystem = $safeSystem")
        Log.d(TAG, "[SystemIssue] updateSystem - RPG_SYSTEM_GENERIC = $RPG_SYSTEM_GENERIC")
        Log.d(TAG, "[SystemIssue] updateSystem - RPG_SYSTEM_D20 = $RPG_SYSTEM_D20")
        Log.d(TAG, "[SystemIssue] updateSystem - RPG_SYSTEM_SAVAGEWORLDS = $RPG_SYSTEM_SAVAGEWORLDS")
        mPreferencesManager?.selectSystem(safeSystem)
        view?.onUpdatedSystem()
    }

    override fun goToMainActivity(w: Window) {
        val view: StartupContract.View? = getView()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            w.exitTransition = null
        }

        val intent = Intent(mContext, MainActivity::class.java)
        view?.onGoToMainActivity(intent)
    }




}