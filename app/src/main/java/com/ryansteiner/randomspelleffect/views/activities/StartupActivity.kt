package com.ryansteiner.randomspelleffect.views.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.View.VISIBLE
import android.widget.RadioButton
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.ryansteiner.randomspelleffect.R
import com.ryansteiner.randomspelleffect.contracts.StartupContract
import com.ryansteiner.randomspelleffect.data.RPG_SYSTEM_D20
import com.ryansteiner.randomspelleffect.data.RPG_SYSTEM_GENERIC
import com.ryansteiner.randomspelleffect.data.RPG_SYSTEM_SAVAGEWORLDS
import com.ryansteiner.randomspelleffect.presenters.StartupPresenter
import com.ryansteiner.randomspelleffect.utils.PreferencesManager
import kotlinx.android.synthetic.main.activity_startup.*
import kotlinx.android.synthetic.main.include_full_loading_screen.*

/**
 * Created by Ryan Steiner on 2019/11/06.
 */

class StartupActivity : BaseActivity(), StartupContract.View/*, StartupListAdapter.OnCustomListItemClickListener*/ {

    private val TAG = "StartupActivity"

    private var mPresenter: StartupPresenter? = null
    private var mSystem: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_startup)

        mPresenter = StartupPresenter(this)
        mPresenter?.bindView(this)

        glideAnimatedLoadingIcon(this)
        mPresenter?.loadingViewToggle(true)

        mPresenter?.load()
    }

    private fun initializeView() {

        Log.d(TAG, "initializeView - mPresenter = $mPresenter")
        //mStartupText?.text = "Loading"


        //currently using icons from https://feathericons.com/ - MIT license, Ok to use w/o attribution

        //val context = applicationContext

        /*context.let {
            val allWallpapers = StaticListOfWallpapers(this)
            mStartupRecyclerView?.adapter = null

            mStartupRecyclerView?.setHasFixedSize(false)
            val layoutManager = LinearLayoutManager(it, LinearLayoutManager.VERTICAL, false)
            mStartupRecyclerView?.layoutManager = layoutManager

            val adapter = StartupListAdapter(it, allWallpapers.fullList, this)
            mStartupRecyclerView?.adapter = adapter


        }*/
    }

    private fun setupOnClickListeners() {

        /*ivRendererListButton?.setOnClickListener {
            when (View.GONE) {
                mRenderSelectorListContainer.visibility -> mRenderSelectorListContainer?.visibility =
                    View.VISIBLE
                else -> mRenderSelectorListContainer?.visibility = View.GONE
            }
        }*/


    }

    override fun onLoaded(hasBeenOnboarded: Boolean) {

        initializeView()
        setupOnClickListeners()

       when (hasBeenOnboarded) {
            true -> {
                Handler().postDelayed({
                    mPresenter?.goToMainActivity(window)
                }, 250)
            }
            else -> {
                Log.d(TAG, "[SystemIssue] onLoaded - hasBeenOnboarded = $hasBeenOnboarded")
                selectSystem()
            }
        }


    }

    private fun selectSystem() {
        Log.d(TAG, "[SystemIssue] selectSystem - Start")
        mStartupChooseSystemContainer.visibility = VISIBLE
        mPresenter?.loadingViewToggle(false)
    }

    override fun onUpdatedSystem() {
        mPresenter?.loadingViewToggle(true)
        mPresenter?.goToMainActivity(window)
    }

    override fun onGoToMainActivity(intent: Intent) {
        startActivity(intent)
    }

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            val checked = view.isChecked


            when (view.id) {
                R.id.mStartupRadioSystemGeneric ->
                    if (checked) {
                        Handler().postDelayed({
                            mPresenter?.updateSystem(RPG_SYSTEM_GENERIC)
                        }, 500)
                    }
                R.id.mStartupRadioSystemDND5E ->
                    if (checked) {
                        Handler().postDelayed({
                            mPresenter?.updateSystem(RPG_SYSTEM_D20)
                        }, 500)
                    }
                R.id.mStartupRadioSystemSWADE ->
                    if (checked) {
                        Handler().postDelayed({
                            mPresenter?.updateSystem(RPG_SYSTEM_SAVAGEWORLDS)
                        }, 500)
                    }
            }
        }
    }
}
