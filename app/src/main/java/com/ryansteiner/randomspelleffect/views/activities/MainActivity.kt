package com.ryansteiner.randomspelleffect.views.activities

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import com.ryansteiner.randomspelleffect.R
import com.ryansteiner.randomspelleffect.contracts.MainContract
import com.ryansteiner.randomspelleffect.data.*
import com.ryansteiner.randomspelleffect.presenters.MainPresenter
import com.ryansteiner.randomspelleffect.utils.*
import kotlinx.android.synthetic.main.activity_main.*
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.android.synthetic.main.include_net_libram_info_page.*
import android.content.Intent
import android.net.Uri
import androidx.viewpager.widget.ViewPager
import android.util.DisplayMetrics
import kotlinx.android.synthetic.main.include_side_menu.*
import android.animation.ObjectAnimator
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.ActionBar
import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.AnimatedVectorDrawable
import android.view.View.*
import android.view.animation.AccelerateDecelerateInterpolator
import kotlinx.android.synthetic.main.fragment_main_card.*
import android.os.Build
import android.os.Handler
import android.os.PowerManager
import android.util.AttributeSet
import android.view.*
import android.view.animation.DecelerateInterpolator
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.view.MarginLayoutParamsCompat
import androidx.core.view.children
import androidx.core.view.marginLeft
import androidx.core.view.marginTop
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.PagerAdapter
import com.bugsnag.android.Bugsnag
import com.bumptech.glide.Glide
import com.ryansteiner.randomspelleffect.data.models.*
import com.ryansteiner.randomspelleffect.views.ViewPagerCustomDuration
import kotlinx.android.synthetic.main.include_tutorial.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


/**
 * Created by Ryan Steiner on 2019/11/06.
 */

class MainActivity : BaseActivity(), MainContract.View,
    MainCardFragment.OnFragmentInteractionListener {

    private val TAG = "MainActivity"

    private lateinit var mViewPager: ViewPager
    private var mPresenter: MainPresenter? = null
    private var mDatabase: SQLiteDatabase? = null
    private var mIsTutorial = false
    private var mPreferencesManager: PreferencesManager? = null
    private val mSpellsList = SpellsList(this)
    private var mCurrentSpellEffect: SpellEffect? = null
    private var mScreenWidth = 0
    private var mScreenHeight = 0
    private var mMenuStartingLocation = 0f
    private var mMenuClosedLocation = 0f
    private var mMenuIsClosed = true
    private var mMenuIsAnimating = false
    private var mViewPagerRefreshWatch = false
    private var mViewPagerScrollStateWatch = false
    private var mViewPagerState = -1
    private var mViewPagerSelection = -1
    private var mPagerAdapter: MainCardPageViewAdapter? = null
    private val mBorderColors = mapOf<Int, Int>(
        Pair(SEVERITY_NEUTRAL, R.color.colorBlueYonder),
        Pair(SEVERITY_GOOD_HIGH, R.color.colorGreenApple),
        Pair(SEVERITY_GOOD_MID, R.color.colorGreenCadmium),
        Pair(SEVERITY_GOOD_LOW, R.color.colorBlueBdazzled),
        Pair(SEVERITY_GOOD_NEAR_ENEMIES, R.color.colorOrangeTerraCotta),
        Pair(SEVERITY_GOOD_NEAR_ALLIES, R.color.colorPurpleDeepKoamaru),
        Pair(SEVERITY_BAD_LOW, R.color.colorYellowKhaki),
        Pair(SEVERITY_BAD_MID, R.color.colorOrangeOutrageous),
        Pair(SEVERITY_BAD_YIKES, R.color.colorRedCardinal)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.enterTransition = null
        }
        setContentView(R.layout.activity_main)

        Log.d(TAG, "onCreate  [${mPreferencesManager?.getCurrentLifeTime()}]")

        mPresenter = MainPresenter(this)
        mPresenter?.bindView(this)

        glideAnimatedLoadingIcon(this)
        mPresenter?.loadingViewToggle(true)

        mViewPager = findViewById(R.id.mMainCardPager)

        //mPreferencesManager = PreferencesManager(this)
        //val spellsList = SpellsList(this)
        //mPresenter?.updateSpellList(spellsList)

        //mPresenter?.updateSpellList(mSpellsList)

        ///DEBUG/////////////////////////////////////////////
        //Keep screen on while working///////////////////////
        /////////////////////////////////////////////////////
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        /////////////////////////////////////////////////////


        mPresenter?.loadingViewToggle(true)

        initializePreferencesAndDatabase()

    }

    override fun onDestroy() {
        super.onDestroy()
        ///DEBUG/////////////////////////////////////////////
        //Keep screen on while working///////////////////////
        /////////////////////////////////////////////////////
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        /////////////////////////////////////////////////////
    }

    private fun initializePreferencesAndDatabase() {
        mPresenter?.getPreferences()
        mPresenter?.initializeView()

    }

    override fun onLoadedDatabase() {
        Log.d(TAG, "onLoadedDatabase  [${mPreferencesManager?.getCurrentLifeTime()}]")
        mPresenter?.getSpellEffects()
    }

    override fun onGetSpellEffects(spellEffects: List<SpellEffect>?) {

        Log.d(TAG, "onGetSpellEffects  [${mPreferencesManager?.getCurrentLifeTime()}]")
        val system = mPreferencesManager?.getSystem() ?: -1
        val fullCards = mutableListOf<FullCard?>()
        val cardIds = mutableListOf<String>()
        if (spellEffects != null && spellEffects.count() > 0) {
            for (i in 0 until spellEffects.count()) {
                val fullCard = FullCard()
                val spellEffect = spellEffects[i]
                val desc = spellEffect?.getDescription()
                val requiredSpellType = spellEffect?.getRequiresSpellType()
                val parsedResult: ParseSpellEffectStringResult? = mPresenter?.parseSpellStringForVariables(desc, system, requiredSpellType)
                val cardText = parsedResult?.mFullString ?: "ERROR Parsing Spell Pair in onGetSpellEffects"
                val cardId = spellEffect?.getIdAsString() ?: "ERROR retrieving the cardId as a String..."
                cardIds.add(cardId)
                fullCard.setSpellEffect(spellEffect)
                fullCard.setMainText(cardText)
                fullCard.setSpell(parsedResult?.mSpell)
                fullCard.setGameplayModifier(parsedResult?.mGameplayModifier)
                fullCard.setSong(parsedResult?.mSong)
                fullCards.add(fullCard)
            }
        }


        //mPreferencesManager?.addToPreviousCardsList(cardIds)

        setupViewPager(fullCards.toList())
    }


    private fun setupViewPager(fullCards: List<FullCard?>?) {
        Log.d(TAG, "setupViewPager  [${mPreferencesManager?.getCurrentLifeTime()}]")
        Log.d(TAG, "setupViewPager  [${fullCards}]")
        val system = mPreferencesManager?.getSystem() ?: -1
        val damagePrefs = mPreferencesManager?.getDamagePreferences()
        val hasBeenOnboarded = mPreferencesManager?.getHasBeenOnboarded() ?: -1
        Log.d(TAG, "setupViewPager - hasBeenOnboarded = $hasBeenOnboarded")
        if (mPagerAdapter == null) {
            if (fullCards != null && fullCards.count() > 0) {

                mViewPager = findViewById(R.id.mMainCardPager)
                mViewPager.pageMargin = 120
                mViewPager.setPageTransformer(true, TiltAnglePageTransformer())
                val pagerAdapter = MainCardPageViewAdapter(supportFragmentManager)
                mPagerAdapter = pagerAdapter
                fullCards.forEach {
                    if (it != null) {
                        val spellEffect = it.getSpellEffect()
                        val title = spellEffect?.getIdAsString() ?: "ERROR - Could not get Id as string because spellEffect was null"
                        val id = spellEffect?.getId()
                        Log.d(TAG, "setupViewPager - it(full card) = ${it}")
                        val fragment = MainCardFragment.newInstance(this, it)
                        fragment.setCallback(this)
                        fragment.setSystem(system)
                        fragment.setDamagePrefs(damagePrefs)
                        pagerAdapter.addFragment(fragment, title)
                    }

                }

                val lastCard = FullCard()
                val lastTitle = "End"
                val lastFragment = MainCardFragment.newInstance(this, lastCard)
                pagerAdapter.addFragment(lastFragment, lastTitle)

                mViewPager.adapter = pagerAdapter
            }
            val count = mViewPager.adapter?.count

            mPreviousPageButton.visibility = GONE

            mViewPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

                override fun onPageScrollStateChanged(state: Int) {
                    Log.d(TAG, "onPageScrollStateChanged - state = $state")
                    mViewPagerState = state
                    /* if (state == ViewPager.SCROLL_STATE_SETTLING && mViewPagerSelection == lastPosition) {
                         mViewPagerScrollStateWatch = true

                     }

                     if (state == ViewPager.SCROLL_STATE_IDLE && mViewPagerScrollStateWatch) {
                         mViewPagerScrollStateWatch = false
                         onLoadingViewToggle(true)
                         Handler().postDelayed({
                             mViewPager?.setCurrentItem(0, false)
                             mPresenter?.getSpellEffects()
                         }, 200)
                     }*/
                }

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                    mNextPageButton.visibility = GONE
                    mPreviousPageButton.visibility = GONE
                }

                override fun onPageSelected(position: Int) {
                    Log.d(TAG, "onPageSelected - position = $position")
                    mViewPagerSelection = position
                    if (mViewPagerState == 2) {
                        val count = mViewPager?.adapter?.count ?: -100
                        val lastPosition = count - 1
                        Log.d(TAG, "onPageSelected - lastPosition = $lastPosition")
                        if (position == lastPosition) {
                            mPresenter?.loadingViewToggle(true)
                            Handler().postDelayed({
                                mViewPager?.setCurrentItem(0, false)
                                mPresenter?.getSpellEffects()
                            }, 10)
                        }
                    }


                }

            })

            mPresenter?.loadingViewToggle(false)
        } else {
            mPagerAdapter?.clearFragments()
            fullCards?.forEach {
                if (it != null) {
                    val spellEffect = it.getSpellEffect()
                    val title = spellEffect?.getIdAsString() ?: "ERROR - Could not get Id as string because spellEffect was null"
                    val id = spellEffect?.getId()
                    Log.d(TAG, "setupViewPager - it(full card) = ${it}")
                    val fragment = MainCardFragment.newInstance(this, it)
                    fragment.setCallback(this)
                    fragment.setSystem(system)
                    fragment.setDamagePrefs(damagePrefs)
                    mPagerAdapter?.addFragment(fragment, title)
                }

            }
            val lastCard = FullCard()
            val lastFragment = MainCardFragment.newInstance(this, lastCard)
            val lastTitle = "End"
            mPagerAdapter?.addFragment(lastFragment, lastTitle)
            mPagerAdapter?.notifyDataSetChanged()
            Handler().postDelayed({
                mPresenter?.loadingViewToggle(false)
            }, 10)
        }
        when {
            hasBeenOnboarded <= 0 -> {
                runTutorial()
            }
        }
    }

    override fun onInitializedView() {
        Log.d(TAG, "onInitializedView  [${mPreferencesManager?.getCurrentLifeTime()}]")
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        mScreenHeight = height
        val width = displayMetrics.widthPixels
        mScreenWidth = width

        val menuStartingLocation = mSideMenu.translationX
        mMenuStartingLocation = menuStartingLocation
        val menuClosedLocation = menuStartingLocation - (width * 0.8f)
        mMenuClosedLocation = menuClosedLocation

        mMenuIsAnimating = true
        val transAnimation = ObjectAnimator.ofFloat(mSideMenu, "translationX", mMenuStartingLocation, mMenuClosedLocation)
        transAnimation.duration = 1
        transAnimation.addListener(object : Animator.AnimatorListener {
            override fun onAnimationEnd(p0: Animator?) {
                mMenuIsAnimating = false
                mMenuIsClosed = true
            }

            override fun onAnimationStart(p0: Animator?) {
                //do nothing
            }

            override fun onAnimationRepeat(p0: Animator?) {
                //do nothing
            }

            override fun onAnimationCancel(p0: Animator?) {
                //do nothing
            }
        })
        transAnimation.start()
        mMenuTabBackground.isClickable = false

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val drawable = ContextCompat.getDrawable(this, R.drawable.menu_to_close_icon_anim) as? AnimatedVectorDrawable
            iMenuIcon?.setImageDrawable(drawable)
        }*/

        mSettingsContainer.visibility = GONE
        mNetLibramInfo.visibility = GONE

        initializeSettingsPopup()
        setupClickListeners()
    }

    private fun setupClickListeners() {
        Log.d(TAG, "setupClickListeners  [${mPreferencesManager?.getCurrentLifeTime()}]")
        mGenerateNewSpellEffectButton?.setOnClickListener {
            mPresenter?.generateSingleSpellEffect()
        }

        iSpellInfoCollapseButton?.setOnClickListener {
            //
        }

        mSettingsButton.setOnClickListener {
            mPresenter?.clickSettings(true)
        }

        iSettingsBackButton.setOnClickListener {
            mPresenter?.clickSettings(false)
        }

        tNetLibramLink.setOnClickListener {
            openNetLibramLink()
        }

        iPreviousPageButton.setOnClickListener {
            val index = mViewPager?.currentItem
            val prev = index - 1
            if (index > 0) {
                mViewPager?.setCurrentItem(prev, true)
            }
        }
        mPreviousPageButton.setOnClickListener {
            iPreviousPageButton.performClick()
        }

        iNextPageButton.setOnClickListener {
            val index = mViewPager?.currentItem
            val next = index + 1
            val total = mViewPager?.adapter?.count ?: 999
            if (index < total) {
                mViewPager?.setCurrentItem(next, true)
            }
        }
        mNextPageButton.setOnClickListener {
            iNextPageButton.performClick()
        }

        mNetLibramInfo.setOnClickListener {
            toggleNetLibramInfoOverlay()
        }

        mMenuTab?.setOnClickListener {
            when {
                mMenuIsClosed -> {
                    toggleSideMenu(false)
                    mMenuTabBackground.isClickable = true
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        val drawable = ContextCompat.getDrawable(this, R.drawable.menu_to_close_icon_anim) as? AnimatedVectorDrawable
                        iMenuIcon.setImageDrawable(drawable)
                        drawable?.start()
                        iMenuIcon.animate()
                            .setDuration(500)
                            .rotation(360f)
                            .setInterpolator(DecelerateInterpolator())
                            .setListener(object : AnimatorListenerAdapter() {
                                override fun onAnimationEnd(animation: Animator) {
                                    super.onAnimationEnd(animation)
                                    iMenuIcon.rotation = 0f
                                }
                            })
                    }
                }
                else -> {
                    toggleSideMenu(true)
                    mMenuTabBackground.isClickable = false
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        val drawable = ContextCompat.getDrawable(this, R.drawable.close_to_menu_icon_anim) as? AnimatedVectorDrawable
                        iMenuIcon.setImageDrawable(drawable)
                        drawable?.start()
                        iMenuIcon.animate()
                            .setDuration(500)
                            .rotation(-360f)
                            .setInterpolator(DecelerateInterpolator())
                            .setListener(object : AnimatorListenerAdapter() {
                                override fun onAnimationEnd(animation: Animator) {
                                    super.onAnimationEnd(animation)
                                    iMenuIcon.rotation = 0f
                                }
                            })
                    }
                }
            }
        }

        mMenuTabBackground?.setOnClickListener {
            mMenuTab?.performClick()
        }

        tSideMenuSettingsButton.setOnClickListener {
            toggleSideMenu(false)
            mPresenter?.clickSettings(true)
        }
        tSideMenuAboutButton.setOnClickListener {
            mPresenter?.goToAbout(window, false)
            toggleSideMenu(false)
        }
        tSideMenuFaqButton.setOnClickListener {
            mPresenter?.goToAbout(window, true)
            toggleSideMenu(false)
        }

    }

    private fun toggleSideMenu(shouldOpen: Boolean) {
        Log.d(TAG, "toggleSideMenu  [${mPreferencesManager?.getCurrentLifeTime()}]")
        if (!mMenuIsAnimating) {
            mMenuIsAnimating = true
            when (mMenuIsClosed) {
                true -> {
                    val transAnimation = ObjectAnimator.ofFloat(mSideMenu, "translationX", mMenuClosedLocation, mMenuStartingLocation)
                    transAnimation.duration = 250
                    //transAnimation.interpolator =
                    transAnimation.addListener(object : Animator.AnimatorListener {
                        override fun onAnimationEnd(p0: Animator?) {
                            mMenuIsAnimating = false
                            mMenuIsClosed = false
                        }

                        override fun onAnimationStart(p0: Animator?) {
                            //do nothing
                        }

                        override fun onAnimationRepeat(p0: Animator?) {
                            //do nothing
                        }

                        override fun onAnimationCancel(p0: Animator?) {
                            //do nothing
                        }
                    })
                    transAnimation.start()
                }
                else -> {
                    val transAnimation = ObjectAnimator.ofFloat(mSideMenu, "translationX", mMenuStartingLocation, mMenuClosedLocation)
                    transAnimation.duration = 250
                    //transAnimation.interpolator =
                    transAnimation.addListener(object : Animator.AnimatorListener {
                        override fun onAnimationEnd(p0: Animator?) {
                            mMenuIsAnimating = false
                            mMenuIsClosed = true
                        }

                        override fun onAnimationStart(p0: Animator?) {
                            //do nothing
                        }

                        override fun onAnimationRepeat(p0: Animator?) {
                            //do nothing
                        }

                        override fun onAnimationCancel(p0: Animator?) {
                            //do nothing
                        }
                    })
                    transAnimation.start()
                }
            }
        }
    }

    override fun onGeneratedSingleSpellEffect(spellEffect: SpellEffect) {
        Log.d(TAG, "onGeneratedSingleSpellEffect  [${mPreferencesManager?.getCurrentLifeTime()}]")

        mCurrentSpellEffect = spellEffect

        val system = mPreferencesManager?.getSystem() ?: -1
        val damagePrefs = mPreferencesManager?.getDamagePreferences()

        mPresenter?.updateDamagePreferences(damagePrefs)

    }

    override fun updateColorLayer(colorId: Int?, visibility: Boolean) {
        if (colorId != null) {
            //TODO - Remove - Not used
        }

    }

    override fun updatePatternLayer(patternId: Int?, visibility: Boolean) {
        if (patternId != null) {
            //TODO Remove - Not used
        }

    }

    override fun updateDiceRoll(selectedSpellDice: String?) {
        /*TODO Remove - Not Used
        */

    }

    override fun updateSpellInfoContainer(visibility: Boolean, spellText: String?, selectedSpellDescriptionWithDamageLevel: String?, selectedSpellPageNumber: String?) {
        //TODO Remove - Not used
    }

    override fun test() {
        //TODO Remove - Not used
    }

    override fun updateDebugText(systemText: String?) {
        Log.d(TAG, "updateDebugText  [${mPreferencesManager?.getCurrentLifeTime()}]")
        //tDebugSystemText.text = systemText
    }

    override fun updatePreferences(prefs: PreferencesManager?) {
        Log.d(TAG, "updatePreferences  [${mPreferencesManager?.getCurrentLifeTime()}]")
        mPreferencesManager = prefs

        val hasBeenOnboarded = prefs?.getHasBeenOnboarded() ?: -1
        Log.d(TAG, "[SystemIssue] updatePreferences - hasBeenOnboarded = $hasBeenOnboarded")

        when {
            hasBeenOnboarded > 0 -> {
                val system = prefs?.getSystem() ?: -1
                when (system) {
                    RPG_SYSTEM_GENERIC -> {
                        radioSystemGeneric.isChecked = true
                    }
                    RPG_SYSTEM_D20 -> {
                        radioSystemDND5E.isChecked = true
                    }
                    RPG_SYSTEM_SAVAGEWORLDS -> {
                        radioSystemSWADE.isChecked = true
                    }
                }
                val gameEffects = prefs?.getGameEffects()
                val gameplayBool = gameEffects!![SPELL_EFFECTS_GAMEPLAY] ?: true
                val roleplayBool = gameEffects!![SPELL_EFFECTS_ROLEPLAY] ?: true
                checkBoxGamePlayEffects.isChecked = gameplayBool
                checkBoxRolePlayEffects.isChecked = roleplayBool

                val targets = prefs?.getTargets()
                val targetCasterBool = targets!![TARGET_CASTER] ?: true
                val targetNearestAllyBool = targets!![TARGET_NEAREST_ALLY] ?: true
                val targetNearestEnemyBool = targets!![TARGET_NEAREST_ENEMY] ?: true
                val targetNearestCreatureBool = targets!![TARGET_NEAREST_CREATURE] ?: true
                checkBoxTargetCaster.isChecked = targetCasterBool
                checkBoxTargetNearestAlly.isChecked = targetNearestAllyBool
                checkBoxTargetNearestEnemy.isChecked = targetNearestEnemyBool
                checkBoxTargetNearestCreature.isChecked = targetNearestCreatureBool

                mTutorialHighlighter.visibility = GONE
            }
            else -> {
                //set defaults
                mPreferencesManager?.setTargets(caster = true, nearestAlly = true, nearestEnemy = true, nearestCreature = true)
                mPreferencesManager?.setDamagePreferences(null)
                mPreferencesManager?.setGameEffects(gamePlay = true, rolePlay = true)
                Log.d(TAG, "[SystemIssue] updatePreferences - selectSystem - GENERIC")
                //mPreferencesManager?.selectSystem(RPG_SYSTEM_GENERIC)
                mIsTutorial = true

                //mTutorialHighlighter.visibility = VISIBLE
            }
        }

        mPresenter?.loadDatabase(this)
    }

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            val checked = view.isChecked

            val system = mPreferencesManager?.getSystem()

            when (view.id) {
                R.id.radioSystemDND5E ->
                    if (checked) {
                        when (system) {
                            RPG_SYSTEM_D20 -> {
                            } //Do nothing
                            else -> {
                                Log.d(TAG, "[SystemIssue] onRadioButtonClicked - selectSystem DND5E")
                                mPreferencesManager?.selectSystem(RPG_SYSTEM_D20)
                                val id = mCurrentSpellEffect?.getId() ?: -1
                                mPresenter?.retrieveSpellEffectById(id)
                            }
                        }
                    }
                R.id.radioSystemSWADE ->
                    if (checked) {
                        when (system) {
                            RPG_SYSTEM_SAVAGEWORLDS -> {
                            } //Do nothing
                            else -> {
                                Log.d(TAG, "[SystemIssue] onRadioButtonClicked - selectSystem SWADE")
                                mPreferencesManager?.selectSystem(RPG_SYSTEM_SAVAGEWORLDS)
                                val id = mCurrentSpellEffect?.getId() ?: -1
                                mPresenter?.retrieveSpellEffectById(id)
                            }
                        }
                    }
            }
        }
    }

    fun onFABClick(view: View) {
        //TODO Compare with other version
        val frag = supportFragmentManager.fragments?.get(mViewPager?.currentItem)
        onShowToastMessage("Click")

        frag?.mSpellEffectAdditionalInfoContainer?.visibility = VISIBLE
        frag?.mSpellEffectAdditionalInfoContainer?.isEnabled = true
        val measuredW = frag?.mSpellEffectAdditionalInfoContainer?.measuredWidth ?: 0
        val measuredH = frag?.mSpellEffectAdditionalInfoContainer?.measuredHeight ?: 0
        val width = (measuredW * 0.5).roundToInt()
        val height = (measuredH * 0.5).roundToInt()
        Log.d(TAG, "onFABClick - width = $width")
        Log.d(TAG, "onFABClick - height = $height")
        val shape = frag?.mSpellEffectAdditionalInfoContainer
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && shape != null) {
            val anim = ViewAnimationUtils.createCircularReveal(
                shape,
                width,
                height,
                0f,
                Math.hypot(shape.getWidth().toDouble(), shape.getHeight().toDouble()).toFloat()
            )
            anim.duration = 500
            anim.interpolator = AccelerateDecelerateInterpolator()
            anim.addListener(object : Animator.AnimatorListener {
                override fun onAnimationEnd(p0: Animator?) {
                    frag?.mSpellEffectAdditionalInfoContainer?.setOnClickListener {
                        onShowToastMessage("Container CLose")
                        val widthB = (frag?.mSpellEffectAdditionalInfoContainer.measuredWidth * 0.5).roundToInt()
                        val heightB = (frag?.mSpellEffectAdditionalInfoContainer.measuredHeight * 0.5).roundToInt()
                        Log.d(TAG, "onFABClick - width = $width")
                        Log.d(TAG, "onFABClick - height = $height")
                        val shapeB = frag?.mSpellEffectAdditionalInfoContainer
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            val anim = ViewAnimationUtils.createCircularReveal(
                                shapeB,
                                widthB,
                                heightB,
                                1000f,
                                0f
                            )
                            anim.interpolator = AccelerateDecelerateInterpolator()
                            anim.addListener(object : Animator.AnimatorListener {
                                override fun onAnimationEnd(p0: Animator?) {
                                    frag?.mSpellEffectAdditionalInfoContainer?.visibility = INVISIBLE
                                    frag?.mSpellEffectAdditionalInfoContainer?.isEnabled = false

                                }

                                override fun onAnimationStart(p0: Animator?) {
                                    //do nothing
                                }

                                override fun onAnimationRepeat(p0: Animator?) {
                                    //do nothing
                                }

                                override fun onAnimationCancel(p0: Animator?) {
                                    //do nothing
                                }
                            })
                            anim.start()
                        }
                    }
                }

                override fun onAnimationStart(p0: Animator?) {
                    //do nothing
                }

                override fun onAnimationRepeat(p0: Animator?) {
                    //do nothing
                }

                override fun onAnimationCancel(p0: Animator?) {
                    //do nothing
                }
            })
            anim.start()
        }

    }

    fun onClickCheckBox(view: View) {
        Log.d(TAG, "onClickCheckBox - view = ${view}")
        if (view is CheckBox) {
            val checked: Boolean = view.isChecked

            val gameEffects = mPreferencesManager?.getGameEffects()
            val gamePlayBool = gameEffects!![SPELL_EFFECTS_GAMEPLAY] ?: true
            val rolePlayBool = gameEffects!![SPELL_EFFECTS_ROLEPLAY] ?: true
            //checkBoxGamePlayEffects.isChecked = gameplayBool
            //checkBoxRolePlayEffects.isChecked = roleplayBool

            val defaultCheckBoxErrorMessage = resources.getString(R.string.menu_affects_error)
            val targetCheckBoxErrorMessage = resources.getString(R.string.menu_targets_error)

            val system = mPreferencesManager?.getSystem()

            when (view.id) {
                //----------------GAME PLAY AND ROLE PLAY EFFECTS----------------
                R.id.checkBoxGamePlayEffects -> {
                    if (checked) {
                        mPreferencesManager?.setGameEffects(true, rolePlayBool)
                    } else {
                        if (checkBoxRolePlayEffects.isChecked) {
                            mPreferencesManager?.setGameEffects(false, rolePlayBool)
                        } else {
                            onShowToastMessage(defaultCheckBoxErrorMessage)
                            checkBoxGamePlayEffects.isChecked = true
                        }
                    }
                }
                R.id.checkBoxRolePlayEffects -> {
                    if (checked) {
                        mPreferencesManager?.setGameEffects(gamePlayBool, true)
                    } else {
                        if (checkBoxGamePlayEffects.isChecked) {
                            mPreferencesManager?.setGameEffects(gamePlayBool, false)
                        } else {
                            onShowToastMessage(defaultCheckBoxErrorMessage)
                            checkBoxRolePlayEffects.isChecked = true

                        }
                    }
                }
                //----------------TARGETS----------------
                R.id.checkBoxTargetCaster -> {
                    if (checked) {
                        mPreferencesManager?.setTargets(caster = true, nearestAlly = null, nearestEnemy = null, nearestCreature = null)
                    } else {
                        if (checkBoxTargetNearestAlly.isChecked || checkBoxTargetNearestEnemy.isChecked || checkBoxTargetNearestCreature.isChecked) {
                            mPreferencesManager?.setTargets(caster = false, nearestAlly = null, nearestEnemy = null, nearestCreature = null)
                        } else {
                            onShowToastMessage(targetCheckBoxErrorMessage)
                            checkBoxTargetCaster.isChecked = true
                        }
                    }
                }
                R.id.checkBoxTargetNearestAlly -> {
                    if (checked) {
                        mPreferencesManager?.setTargets(caster = null, nearestAlly = true, nearestEnemy = null, nearestCreature = null)
                    } else {
                        if (checkBoxTargetCaster.isChecked || checkBoxTargetNearestEnemy.isChecked || checkBoxTargetNearestCreature.isChecked) {
                            mPreferencesManager?.setTargets(caster = null, nearestAlly = false, nearestEnemy = null, nearestCreature = null)
                        } else {
                            onShowToastMessage(targetCheckBoxErrorMessage)
                            checkBoxTargetNearestAlly.isChecked = true
                        }
                    }
                }
                R.id.checkBoxTargetNearestEnemy -> {
                    if (checked) {
                        mPreferencesManager?.setTargets(caster = null, nearestAlly = null, nearestEnemy = true, nearestCreature = null)
                    } else {
                        if (checkBoxTargetCaster.isChecked || checkBoxTargetNearestAlly.isChecked || checkBoxTargetNearestCreature.isChecked) {
                            mPreferencesManager?.setTargets(caster = null, nearestAlly = null, nearestEnemy = false, nearestCreature = null)
                        } else {
                            onShowToastMessage(targetCheckBoxErrorMessage)
                            checkBoxTargetNearestEnemy.isChecked = true
                        }
                    }
                }
                R.id.checkBoxTargetNearestCreature -> {
                    if (checked) {
                        mPreferencesManager?.setTargets(caster = null, nearestAlly = null, nearestEnemy = null, nearestCreature = true)
                    } else {
                        if (checkBoxTargetCaster.isChecked || checkBoxTargetNearestEnemy.isChecked || checkBoxTargetNearestAlly.isChecked) {
                            mPreferencesManager?.setTargets(caster = null, nearestAlly = null, nearestEnemy = null, nearestCreature = false)
                        } else {
                            onShowToastMessage(targetCheckBoxErrorMessage)
                            checkBoxTargetNearestCreature.isChecked = true
                        }
                    }
                }
            }
            Log.d(TAG, "onClickCheckBox - mPreferencesManager?.getGameEffects() = ${mPreferencesManager?.getGameEffects()}")
        }
    }

    override fun onClickSettings(showSettings: Boolean) {
        //These will need animations at some point
        when (showSettings) {
            true -> {
                mSettingsContainer.visibility = VISIBLE
            }
            else -> {
                mSettingsContainer.visibility = GONE
            }
        }
    }

    private fun initializeSettingsPopup() {
        Log.d(TAG, "initializeSettingsPopup  [${mPreferencesManager?.getCurrentLifeTime()}]")
        val casterCapitalize = checkBoxTargetCaster.text.toString().split(' ').joinToString(" ") { it.capitalize() }
        checkBoxTargetCaster.text = casterCapitalize
        val nearestAllyCapitalize = checkBoxTargetNearestAlly.text.toString().split(' ').joinToString(" ") { it.capitalize() }
        checkBoxTargetNearestAlly.text = nearestAllyCapitalize
        val nearestEnemyCapitalize = checkBoxTargetNearestEnemy.text.toString().split(' ').joinToString(" ") { it.capitalize() }
        checkBoxTargetNearestEnemy.text = nearestEnemyCapitalize
        val closeCreatureCapitalize = checkBoxTargetNearestCreature.text.toString().split(' ').joinToString(" ") { it.capitalize() }
        checkBoxTargetNearestCreature.text = closeCreatureCapitalize
    }

    override fun songVideoInit(showVideo: Boolean, song: Song?) {
//TODO Remove - Not sued
    }


    override fun onFragmentInteraction(uri: Uri) {
        Log.d(TAG, "onFragmentInteraction")
    }

    override fun toggleNetLibramInfoOverlay() {
        mNetLibramInfo.visibility = when (mNetLibramInfo.visibility) {
            VISIBLE -> GONE
            else -> VISIBLE
        }
    }

    override fun onGoToAbout(intent: Intent) {
        startActivity(intent)
    }

    private fun runTutorial() {
        runTutorialPart1()

        Glide
            .with(this)
            .load(ContextCompat.getDrawable(this, R.drawable.swipe_anim))
            .into(iTutorialCardSwipe01)

        mTutorialSkipButton.setOnClickListener {
            endTutorial()
        }
    }

    private fun runTutorialPart1() {
        mIncludeTutorialContainer.visibility = VISIBLE
        mTutorialWelcomeScreen?.visibility = VISIBLE
        mTutorialCardIntro?.visibility = GONE
        mTutorialCardSwipe?.visibility = GONE
        mTutorialHighlighter?.visibility = GONE
        mTutorialMenuButton?.visibility = GONE
        mTutorialExtraInfoButtonContainer?.visibility = GONE
        mTutorialExtraInfoPopupContainer?.visibility = GONE
        mTutorialMenuPart2.visibility = GONE
        mTutorialNetLibram.visibility = GONE
        /*val swipeListener = OnSwipeTouchListener(this)



        mIncludeTutorialContainer.setOnTouchListener(object : OnSwipeTouchListener(this) {
            override fun onSwipeLeft() {
                onShowToastMessage("This one")
            }

            override fun onSwipeRight() {
                onShowToastMessage("Right")
            }
        })*/
        mTutorialWelcomeScreen?.setOnClickListener {
            runTutorialPart2()
        }

    }

    private fun runTutorialPart2() {
        mTutorialWelcomeScreen?.visibility = GONE
        mTutorialCardIntro?.visibility = VISIBLE
        mTutorialCardSwipe?.visibility = GONE
        mTutorialHighlighter?.visibility = GONE
        mTutorialMenuButton?.visibility = GONE
        mTutorialNetLibram.visibility = GONE

        mTutorialCardIntro?.setOnClickListener {
            runTutorialPart3()
        }

    }

    private fun runTutorialPart3() {
        mTutorialWelcomeScreen?.visibility = GONE
        mTutorialCardIntro?.visibility = GONE
        mTutorialCardSwipe?.visibility = VISIBLE
        mTutorialHighlighter?.visibility = GONE
        mTutorialMenuButton?.visibility = GONE
        mTutorialNetLibram.visibility = GONE

        mIncludeTutorialContainer?.isClickable = false
        mIncludeTutorialContainer?.isFocusable = false

        mViewPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                Log.d(TAG, "onPageScrolled - position = $position")
                Log.d(TAG, "onPageScrolled - positionOffset = $positionOffset")
                Log.d(TAG, "onPageScrolled - positionOffsetPixels = $positionOffsetPixels")
                if (position == 1) {
                    mViewPager?.removeOnPageChangeListener(this)

                    mTutorialWelcomeScreen?.visibility = GONE
                    mTutorialCardIntro?.visibility = GONE
                    mTutorialCardSwipe?.visibility = GONE
                    mTutorialHighlighter?.visibility = VISIBLE
                    mTutorialMenuButton?.visibility = GONE
                    mTutorialExtraInfoButtonContainer?.visibility = INVISIBLE
                    mTutorialNetLibram.visibility = GONE

                    mTutorialRevealHelper.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            mTutorialRevealHelper.viewTreeObserver.removeOnGlobalLayoutListener(this)
                            runTutorialPart4()
                        }
                    })


                }
            }

            override fun onPageScrollStateChanged(state: Int) {
                Log.d(TAG, "onPageScrollStateChanged - state = $state")
            }

            override fun onPageSelected(position: Int) {
                Log.d(TAG, "onPageScrollStateChanged - position = $position")
            }
        })

        /*mTutorialCardSwipe.setOnTouchListener(object : OnSwipeTouchListener(this) {
            override fun onSwipeLeft() {
                onShowToastMessage("This one")
                //mViewPager?.beginFakeDrag()
                runTutorialPart4()
            }

            override fun onSwipeRight() {
                //onShowToastMessage("Right")
                //do nothing
            }
        })
*/

    }

    //TODO need to add tutorial bit for net libram

    private fun runTutorialPart4() {

        val helperView = mTutorialRevealHelper

        mIncludeTutorialContainer?.isClickable = true
        mIncludeTutorialContainer?.isFocusable = true

        val currentItem = mViewPager?.currentItem
        val fragments = mPagerAdapter?.fragments
        var currentFragment: Fragment? = null
        if (fragments != null && fragments.count() >= currentItem) {
            currentFragment = fragments[currentItem]
        }


        val w = currentFragment?.mFloatingActionButtonInfo?.width ?: 0
        val h = currentFragment?.mFloatingActionButtonInfo?.height ?: 0
        val xMargin = currentFragment?.mFloatingActionButtonInfo?.marginLeft ?: 0
        val x = (currentFragment?.mFloatingActionButtonInfo?.x?.roundToInt()) ?: 0
        val y = (currentFragment?.mFloatingActionButtonInfo?.y?.roundToInt()) ?: 0

        val tutorialWidth = (w * 1.75)
        val tutorialHeight = (h * 1.75)

        //TODO Find out why this is /4 instead of /2...
        val helperHalfWidth = helperView.measuredWidth / 4
        val helperHalfHeight = helperView.measuredHeight / 4


        val layoutParams = mTutorialMidMiddle?.layoutParams
        layoutParams?.width = tutorialWidth.roundToInt()
        layoutParams?.height = tutorialHeight.roundToInt()
        mTutorialMidMiddle.requestLayout()

        val xOffset = ((x - (w * 0.5) + xMargin) + (tutorialWidth * 0.5) + helperHalfWidth).roundToInt()
        //val xOffset = xOffsetDp.px
        val yOffset = ((y - (h * 0.5)) + (tutorialHeight * 0.5) + helperHalfHeight).roundToInt()
        //val yOffset = yOffsetDp.px

        Log.d(TAG, "runTutorialPart4 - helperView = $helperView")
        Log.d(TAG, "runTutorialPart4 - helperView.measuredWidth = ${helperView.measuredWidth}")

        Log.d(TAG, "runTutorialPart4 - xMargin = $xMargin")
        Log.d(TAG, "runTutorialPart4 - helperHalfWidth = $helperHalfWidth")
        Log.d(TAG, "runTutorialPart4 - x = $x")
        Log.d(TAG, "runTutorialPart4 - w = $w")
        Log.d(TAG, "runTutorialPart4 - (w * 0.5) = ${(w * 0.5)}")
        Log.d(TAG, "runTutorialPart4 - xOffset = $xOffset")
        Log.d(TAG, "runTutorialPart4 --------------------------------------------")
        Log.d(TAG, "runTutorialPart4 - helperHalfHeight = $helperHalfHeight")
        Log.d(TAG, "runTutorialPart4 - y = $y")
        Log.d(TAG, "runTutorialPart4 - h = $h")
        Log.d(TAG, "runTutorialPart4 - (h * 0.5) = ${(h * 0.5)}")
        Log.d(TAG, "runTutorialPart4 - yOffset = $yOffset")

        val constraintSet = ConstraintSet()
        val middle = mTutorialMidMiddle.id
        val helperId = helperView.id
        val constraintLayout: ConstraintLayout = mTutorialHighlighter

        val highlighterContainer = mTutorialHighlighter.id

        constraintSet.clone(constraintLayout)

        constraintSet.connect(helperId, ConstraintSet.TOP, highlighterContainer, ConstraintSet.TOP, yOffset)
        constraintSet.connect(helperId, ConstraintSet.LEFT, highlighterContainer, ConstraintSet.LEFT, xOffset)

        constraintSet.connect(middle, ConstraintSet.TOP, helperId, ConstraintSet.TOP, 0)
        constraintSet.connect(middle, ConstraintSet.BOTTOM, helperId, ConstraintSet.BOTTOM, 0)
        constraintSet.connect(middle, ConstraintSet.LEFT, helperId, ConstraintSet.LEFT, 0)
        constraintSet.connect(middle, ConstraintSet.RIGHT, helperId, ConstraintSet.RIGHT, 0)

        constraintSet.applyTo(constraintLayout)

        val textParams = mTutorialExtraInfoButtonLayout.layoutParams as? FrameLayout.LayoutParams
        val displayWidth = Resources.getSystem().displayMetrics.widthPixels
        val horizOffset = displayWidth - xOffset
        val vertOffset = yOffset + h
        textParams?.setMargins(0, vertOffset, horizOffset, 0)
        Log.d(TAG, "runTutorialPart4 - textParams = $textParams")

        mTutorialExtraInfoButtonLayout.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
            override fun onLayoutChange(
                view: View, left: Int, top: Int, right: Int, bottom: Int,
                oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int
            ) {

                view.removeOnLayoutChangeListener(this)

                mTutorialExtraInfoButtonContainer?.visibility = VISIBLE

            }
        })

        mTutorialExtraInfoButtonLayout.requestLayout()

        mTutorialMidMiddle?.setOnClickListener {

            mTutorialWelcomeScreen?.visibility = GONE
            mTutorialCardIntro?.visibility = GONE
            mTutorialCardSwipe?.visibility = GONE
            mTutorialHighlighter?.visibility = VISIBLE
            mTutorialMenuButton?.visibility = VISIBLE
            mTutorialExtraInfoButtonContainer?.visibility = GONE
            mTutorialNetLibram.visibility = GONE

            mTutorialRevealHelper.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    mTutorialRevealHelper.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    currentFragment?.mFloatingActionButtonInfo?.performClick()
                    runTutorialPart5()
                }
            })


        }

    }

    private fun runTutorialPart5() {


        mTutorialWelcomeScreen?.visibility = GONE
        mTutorialCardIntro?.visibility = GONE
        mTutorialCardSwipe?.visibility = GONE
        mTutorialHighlighter?.visibility = GONE
        mTutorialMenuButton?.visibility = GONE
        mTutorialExtraInfoButtonContainer?.visibility = GONE
        mTutorialExtraInfoPopupContainer?.visibility = VISIBLE
        mTutorialNetLibram.visibility = GONE

        mTutorialExtraInfoPopupContainer.setOnClickListener {

            mTutorialWelcomeScreen?.visibility = GONE
            mTutorialCardIntro?.visibility = GONE
            mTutorialCardSwipe?.visibility = GONE
            mTutorialHighlighter?.visibility = VISIBLE
            mTutorialMenuButton?.visibility = VISIBLE
            mTutorialExtraInfoButtonContainer?.visibility = GONE
            mTutorialExtraInfoPopupContainer?.visibility = GONE
            mTutorialNetLibram.visibility = GONE


            val currentItem = mViewPager?.currentItem
            val fragments = mPagerAdapter?.fragments
            var currentFragment: Fragment? = null
            if (fragments != null && fragments.count() >= currentItem) {
                currentFragment = fragments[currentItem]
            }

            currentFragment?.mSpellEffectAdditionalInfoContainer?.performClick()

            runTutorialPart6()

        }

    }

    private fun runTutorialPart6() {

        val helperView = mTutorialRevealHelper

        mIncludeTutorialContainer?.isClickable = true
        mIncludeTutorialContainer?.isFocusable = true

        val w = mMenuTab.measuredWidth
        val h = mMenuTab.measuredHeight
        val xMargin = mMenuTabImage.marginLeft
        val x = (mMenuTab?.x?.roundToInt()) ?: 0
        val y = (mMenuTab?.y?.roundToInt()) ?: 0

        val tutorialWidth = (w * 2.0)
        val tutorialHeight = (h * 1.75)

        val helperHalfWidth = helperView.measuredWidth / 4
        val helperHalfHeight = helperView.measuredHeight / 4

        val layoutParams = mTutorialMidMiddle?.layoutParams
        layoutParams?.width = tutorialWidth.roundToInt()
        layoutParams?.height = tutorialHeight.roundToInt()
        mTutorialMidMiddle.requestLayout()

        val xOffset = ((x - (w * 0.5) + xMargin) + (tutorialWidth * 0.5) + helperHalfWidth).roundToInt()
        //val xOffset = xOffsetDp.px
        val yOffset = ((y - (h * 0.5)) + (tutorialHeight * 0.5) + helperHalfHeight).roundToInt()
        //val yOffset = yOffsetDp.px

        Log.d(TAG, "runTutorialPart5 - xMargin = $xMargin")
        Log.d(TAG, "runTutorialPart5 - h = $h")
        Log.d(TAG, "runTutorialPart5 - w = $w")
        Log.d(TAG, "runTutorialPart5 - x = $x")
        Log.d(TAG, "runTutorialPart5 - y = $y")
        Log.d(TAG, "runTutorialPart5 - xOffset = $xOffset")
        Log.d(TAG, "runTutorialPart5 - yOffset = $yOffset")
        Log.d(TAG, "runTutorialPart5 - helperHalfWidth = $helperHalfWidth")
        Log.d(TAG, "runTutorialPart5 - helperHalfHeight = $helperHalfHeight")

        val constraintSet = ConstraintSet()
        val middle = mTutorialMidMiddle.id
        val helperId = helperView.id
        val constraintLayout: ConstraintLayout = mTutorialHighlighter

        val highlighterContainer = mTutorialHighlighter.id

        constraintSet.clone(constraintLayout)

        constraintSet.connect(helperId, ConstraintSet.TOP, highlighterContainer, ConstraintSet.TOP, yOffset)
        constraintSet.connect(helperId, ConstraintSet.LEFT, highlighterContainer, ConstraintSet.LEFT, xOffset)

        constraintSet.connect(middle, ConstraintSet.TOP, helperId, ConstraintSet.TOP, 0)
        constraintSet.connect(middle, ConstraintSet.BOTTOM, helperId, ConstraintSet.BOTTOM, 0)
        constraintSet.connect(middle, ConstraintSet.LEFT, helperId, ConstraintSet.LEFT, 0)
        constraintSet.connect(middle, ConstraintSet.RIGHT, helperId, ConstraintSet.RIGHT, 0)

        constraintSet.applyTo(constraintLayout)

        val textParams = mTutorialMenuButtonLayout.layoutParams as? FrameLayout.LayoutParams
        textParams?.setMargins(xOffset, yOffset * 2, 0, 0)
        Log.d(TAG, "runTutorialPart5 - textParams = $textParams")
        mTutorialMenuButtonLayout.requestLayout()

        mTutorialMidMiddle?.setOnClickListener {
            mMenuTab?.performClick()
            mTutorialWelcomeScreen?.visibility = GONE
            mTutorialCardIntro?.visibility = GONE
            mTutorialCardSwipe?.visibility = GONE
            mTutorialHighlighter?.visibility = GONE
            mTutorialMenuButton?.visibility = GONE
            mTutorialExtraInfoButtonContainer?.visibility = GONE
            mTutorialExtraInfoPopupContainer?.visibility = GONE
            mTutorialNetLibram.visibility = GONE
            mTutorialMenuPart2.visibility = VISIBLE
            runTutorialPart7()
        }

    }

    private fun runTutorialPart7() {

        val helperView = mTutorialRevealHelper

        mIncludeTutorialContainer?.isClickable = true
        mIncludeTutorialContainer?.isFocusable = true

        val w = mMenuTab.measuredWidth
        val h = mMenuTab.measuredHeight
        val xMargin = mMenuTabImage.marginLeft
        val x = (mMenuTab?.x?.roundToInt()) ?: 0
        val y = (mMenuTab?.y?.roundToInt()) ?: 0

        val tutorialWidth = (w * 2.0)
        val tutorialHeight = (h * 1.75)

        val helperHalfWidth = helperView.measuredWidth / 4
        val helperHalfHeight = helperView.measuredHeight / 4

        val layoutParams = mTutorialMidMiddle?.layoutParams
        layoutParams?.width = tutorialWidth.roundToInt()
        layoutParams?.height = tutorialHeight.roundToInt()
        mTutorialMidMiddle.requestLayout()

        val xOffset = ((x - (w * 0.5) + xMargin) + (tutorialWidth * 0.5) + helperHalfWidth).roundToInt()
        val yOffset = ((y - (h * 0.5)) + (tutorialHeight * 0.5) + helperHalfHeight).roundToInt()


        val constraintSet = ConstraintSet()
        val middle = mTutorialMidMiddle.id
        val helperId = helperView.id
        val constraintLayout: ConstraintLayout = mTutorialHighlighter

        val highlighterContainer = mTutorialHighlighter.id

        constraintSet.clone(constraintLayout)

        constraintSet.connect(helperId, ConstraintSet.TOP, highlighterContainer, ConstraintSet.TOP, yOffset)
        constraintSet.connect(helperId, ConstraintSet.LEFT, highlighterContainer, ConstraintSet.LEFT, xOffset)

        constraintSet.connect(middle, ConstraintSet.TOP, helperId, ConstraintSet.TOP, 0)
        constraintSet.connect(middle, ConstraintSet.BOTTOM, helperId, ConstraintSet.BOTTOM, 0)
        constraintSet.connect(middle, ConstraintSet.LEFT, helperId, ConstraintSet.LEFT, 0)
        constraintSet.connect(middle, ConstraintSet.RIGHT, helperId, ConstraintSet.RIGHT, 0)

        constraintSet.applyTo(constraintLayout)

        val textParams = mTutorialMenuButtonLayout.layoutParams as? FrameLayout.LayoutParams
        textParams?.setMargins(xOffset, yOffset * 2, 0, 0)
        mTutorialMenuButtonLayout.requestLayout()

        mTutorialMenuPart2?.setOnClickListener {
            mMenuTab?.performClick()
            runTutorialNetLibram()
        }

    }

    private fun runTutorialNetLibram(){
        Log.d(TAG, "runTutorialNetLibram")
        mTutorialWelcomeScreen?.visibility = GONE
        mTutorialCardIntro?.visibility = GONE
        mTutorialCardSwipe?.visibility = GONE
        mTutorialHighlighter?.visibility = GONE
        mTutorialNetLibram.visibility = VISIBLE

        tTutorialNetLibramTextLink.setOnClickListener {
            openNetLibramLink()
        }

        mTutorialNetLibram?.setOnClickListener {
            endTutorial()
        }

    }

    private fun endTutorial() {
        Log.d(TAG, "endTutorial")
        mTutorialWelcomeScreen?.visibility = GONE
        mTutorialCardIntro?.visibility = GONE
        mTutorialCardSwipe?.visibility = GONE
        mTutorialHighlighter?.visibility = GONE
        mIncludeTutorialContainer.visibility = GONE
        mTutorialNetLibram.visibility = GONE
        mPreferencesManager?.setHasBeenOnboarded(1)
    }

    private fun openNetLibramLink(){
        val urlString = getString(R.string.net_libram_url)
        val uri = Uri.parse(urlString)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }


}


