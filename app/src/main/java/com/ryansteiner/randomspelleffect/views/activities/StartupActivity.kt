package com.ryansteiner.randomspelleffect.views.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.ryansteiner.randomspelleffect.R
import com.ryansteiner.randomspelleffect.contracts.StartupContract
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_startup)

        mPresenter = StartupPresenter(this)
        mPresenter?.bindView(this)

        glideAnimatedLoadingIcon(this)
        mPresenter?.loadingViewToggle(true)


        initializeView()
        setupOnClickListeners()

    }

    private fun initializeView(){

        Log.d(TAG, "initializeView - mPresenter = $mPresenter")
        mStartupText?.text = "Loading"

        mPresenter?.load()

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

    private fun setupOnClickListeners(){

        /*ivRendererListButton?.setOnClickListener {
            when (View.GONE) {
                mRenderSelectorListContainer.visibility -> mRenderSelectorListContainer?.visibility =
                    View.VISIBLE
                else -> mRenderSelectorListContainer?.visibility = View.GONE
            }
        }*/



    }

    override fun onLoaded() {


        val intent = Intent(this, MainActivity::class.java)
        mStartupText?.text = "Loaded"
        //Handler().postDelayed({}, 100)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.exitTransition = null
        }
        Handler().postDelayed({
            startActivity(intent)
            //mPresenter?.loadingViewToggle(false)
        }, 500)

    }

   /* override fun onSelectItem(id: Int) {

        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(INTENT_EXTRA_WALLPAPER_ID_INT, id)
        startActivityForResult(intent, REQUEST_CODE_MAIN_ACTIVITY)
    }

    override fun onCustomListItemClick(item: LiveWallpaperInfo) {
        mPresenter?.selectItem(item)
    }*/
}
