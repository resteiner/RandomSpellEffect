package com.ryansteiner.randomspelleffect.presenters

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import android.view.Window
import androidx.fragment.app.FragmentManager
import com.ryansteiner.randomspelleffect.contracts.AboutContract
import com.ryansteiner.randomspelleffect.contracts.BaseContract
import com.ryansteiner.randomspelleffect.contracts.MainContract
import com.ryansteiner.randomspelleffect.contracts.StartupContract
import com.ryansteiner.randomspelleffect.data.RPG_SYSTEM_D20
import com.ryansteiner.randomspelleffect.data.RPG_SYSTEM_GENERIC
import com.ryansteiner.randomspelleffect.data.RPG_SYSTEM_SAVAGEWORLDS
import com.ryansteiner.randomspelleffect.utils.PreferencesManager
import com.ryansteiner.randomspelleffect.views.activities.AboutPageViewAdapter
import com.ryansteiner.randomspelleffect.views.activities.MainActivity
import java.lang.ref.WeakReference

/**
 * Created by Ryan Steiner on 2020/01/13.
 */

class AboutPresenter(context: Context) : BasePresenter<AboutContract.View>(context), AboutContract.Presenter {

    private val TAG = "AboutPresenter"
    private var mPreferencesManager: PreferencesManager? = null

    override fun load() {
        val view: AboutContract.View? = getView()


        mPreferencesManager = PreferencesManager(mContext)
        Log.d(TAG, "load  [${mPreferencesManager?.getCurrentLifeTime()}]")


        view?.onLoaded()
    }

    override fun goToMainActivity(w: Window) {
        val view: AboutContract.View? = getView()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            w.exitTransition = null
        }

        val intent = Intent(mContext, MainActivity::class.java)
        view?.onGoToMainActivity(intent)
    }


}