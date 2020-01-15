package com.ryansteiner.randomspelleffect.views.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewTreeObserver
import android.widget.RadioButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.ryansteiner.randomspelleffect.R
import com.ryansteiner.randomspelleffect.contracts.AboutContract
import com.ryansteiner.randomspelleffect.data.EXTRA_IS_FAQ
import com.ryansteiner.randomspelleffect.data.RPG_SYSTEM_D20
import com.ryansteiner.randomspelleffect.data.RPG_SYSTEM_GENERIC
import com.ryansteiner.randomspelleffect.data.RPG_SYSTEM_SAVAGEWORLDS
import com.ryansteiner.randomspelleffect.presenters.AboutPresenter
import com.ryansteiner.randomspelleffect.presenters.StartupPresenter
import kotlinx.android.synthetic.main.activity_about.*
import kotlinx.android.synthetic.main.activity_startup.*

/**
 * Created by Ryan Steiner on 2020/01/13.
 */

class AboutActivity : BaseActivity(), AboutContract.View {

    private val TAG = "StartupActivity"

    private var mPresenter: AboutPresenter? = null
    private var mAboutPageViewAdapter: AboutPageViewAdapter? = null
    private lateinit var mViewPager: ViewPager
    private var mViewPagerState = -1
    private var mIsFaq = false
    private var mIsClicking = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        mPresenter = AboutPresenter(this)
        mPresenter?.bindView(this)

        glideAnimatedLoadingIcon(this)
        mPresenter?.loadingViewToggle(true)

        val extras = intent.extras
        Log.d(TAG, "initializeView - extras = $extras")
        if (extras != null) {
            mIsFaq = extras.getBoolean(EXTRA_IS_FAQ)
        }
        Log.d(TAG, "initializeView - extras.getBoolean(EXTRA_IS_FAQ) = ${extras.getBoolean(EXTRA_IS_FAQ)}")

        mPresenter?.load()
    }

    private fun initializeView() {
        Log.d(TAG, "initializeView - mPresenter = $mPresenter")

        if (mAboutPageViewAdapter == null) {
            mAboutPageViewAdapter = AboutPageViewAdapter(supportFragmentManager)
        }

        mViewPager = findViewById(R.id.mAboutPager)
        mViewPager?.pageMargin = 120
        Log.d(TAG, "initializeView - mViewPager = $mViewPager")

        val aboutFragment = AboutFragment.newInstance(this, false)
        aboutFragment.setCallback(this)
        val aboutTitle = resources.getString(R.string.menu_about)
        mAboutPageViewAdapter?.addFragment(aboutFragment, aboutTitle)

        val faqFragment = AboutFragment.newInstance(this, true)
        faqFragment.setCallback(this)
        val faqTitle = resources.getString(R.string.menu_faq)
        mAboutPageViewAdapter?.addFragment(faqFragment, faqTitle)

        mViewPager?.adapter = mAboutPageViewAdapter

        val highlightTextColor = ContextCompat.getColor(this, R.color.colorButtonTextHighlight)
        val offTextColor = ContextCompat.getColor(this, R.color.colorButtonText)
        val highlightBackgroundColor = ContextCompat.getColor(this, R.color.colorButtonBackgroundHighlight)
        val offBackgoundColor = ContextCompat.getColor(this, R.color.colorButtonBackground)


        mViewPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {
                Log.d(TAG, "onPageScrollStateChanged - state = $state")
                mViewPagerState = state
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                //do nothing
            }

            override fun onPageSelected(position: Int) {
                Log.d(TAG, "onPageSelected - position = $position")
                if (mViewPagerState == 2) {
                    mPresenter?.loadingViewToggle(false)
                    val fragmentInt = mViewPager.currentItem
                    when (fragmentInt) {
                        1 -> {
                            mIsFaq = false
                            tAboutButton.setTextColor(offTextColor)
                            tAboutButton.setBackgroundColor(offBackgoundColor)
                            tFaqButton.setTextColor(highlightTextColor)
                            tFaqButton.setBackgroundColor(highlightBackgroundColor)
                        }
                        else -> {
                            mIsFaq = true
                            tAboutButton.setTextColor(highlightTextColor)
                            tAboutButton.setBackgroundColor(highlightBackgroundColor)
                            tFaqButton.setTextColor(offTextColor)
                            tFaqButton.setBackgroundColor(offBackgoundColor)
                        }
                    }
                }


            }

        })

        Log.d(TAG, "initializeView - mIsFaq = $mIsFaq")
        if (mIsFaq) {
            tAboutButton.setTextColor(offTextColor)
            tAboutButton.setBackgroundColor(offBackgoundColor)
            tFaqButton.setTextColor(highlightTextColor)
            tFaqButton.setBackgroundColor(highlightBackgroundColor)
            tFaqButton.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    tFaqButton.performClick()
                }
            })
        } else {
            tAboutButton.setTextColor(highlightTextColor)
            tAboutButton.setBackgroundColor(highlightBackgroundColor)
            tFaqButton.setTextColor(offTextColor)
            tFaqButton.setBackgroundColor(offBackgoundColor)
            mPresenter?.loadingViewToggle(false)
        }


    }

    private fun setupOnClickListeners() {

        tAboutButton.setOnClickListener {
                mViewPager.currentItem = 0
        }

        tFaqButton.setOnClickListener {
                mViewPager.currentItem = 1

        }

        iAboutTitleButton.setOnClickListener {
            mPresenter?.loadingViewToggle(true)
            mPresenter?.goToMainActivity(window)
        }


    }

    override fun onLoaded() {
        initializeView()
        setupOnClickListeners()
    }

    override fun onGoToMainActivity(intent: Intent) {
        startActivity(intent)
    }

}

class AboutPageViewAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private val TAG = "AboutPageViewAdapter"
    val fragments: ArrayList<Fragment> = ArrayList()
    val titles: ArrayList<String> = ArrayList()

    override fun getCount(): Int = fragments?.count()

    override fun getItem(position: Int): Fragment = fragments[position]

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }

    fun addFragment(fragment: Fragment, title: String) {
        Log.d(TAG, "addFragment - fragment = $fragment")
        Log.d(TAG, "addFragment - title = $title")
        fragments.add(fragment)
        titles.add(title)
    }

    fun clearFragments() {
        fragments.clear()
        titles.clear()
    }
}
