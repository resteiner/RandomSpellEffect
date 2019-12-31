package com.ryansteiner.randomspelleffect.views.activities

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.View
import com.ryansteiner.randomspelleffect.R
import com.ryansteiner.randomspelleffect.data.*
import com.ryansteiner.randomspelleffect.utils.*
import android.widget.CheckBox
import kotlinx.android.synthetic.main.include_net_libram_info_page.*
import android.content.Intent
import android.net.Uri
import androidx.viewpager.widget.ViewPager
import android.util.DisplayMetrics
import kotlinx.android.synthetic.main.include_side_menu.*
import android.animation.ObjectAnimator
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.drawable.AnimatedVectorDrawable
import android.view.View.*
import android.view.animation.AccelerateDecelerateInterpolator
import kotlinx.android.synthetic.main.fragment_main_card.*
import android.view.ViewAnimationUtils
import android.os.Build
import android.os.Handler
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import com.ryansteiner.randomspelleffect.contracts.TutorialContract
import com.ryansteiner.randomspelleffect.data.models.*
import com.ryansteiner.randomspelleffect.presenters.TutorialPresenter
import kotlinx.android.synthetic.main.activity_tutorial.*
import kotlin.math.roundToInt


/**
 * Created by Ryan Steiner on 2019/12/30.
 */

class TutorialActivity(context: Context) : BaseActivity(), TutorialContract.View,
    MainCardFragment.OnFragmentInteractionListener {

    private val TAG = "TutorialActivity"

    private lateinit var mViewPager: ViewPager
    private var mContext = context
    private var mPresenter: TutorialPresenter? = null
    private var mDatabase: SQLiteDatabase? = null
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

        mPresenter = TutorialPresenter(this)
        mPresenter?.bindView(this)

        glideAnimatedLoadingIcon(this)
        mPresenter?.loadingViewToggle(true)

        mViewPager = findViewById(R.id.mTutorialCardPager)

        initializePreferencesAndDatabase()

    }

    private fun initializePreferencesAndDatabase(){
        //mPresenter?.getPreferences()
        mPresenter?.load()
        initializeView()

    }

    override fun onLoaded() {
        mPresenter?.getSpellEffects()
    }

    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /*override fun onLoadedDatabase() {
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
                val desc = spellEffect?.mDescription
                val requiredSpellType = spellEffect?.mRequiresSpellType
                val parsedResult: ParseSpellEffectStringResult? = mPresenter?.parseSpellStringForVariables(desc, system, requiredSpellType)
                val cardText =  parsedResult?.mFullString ?: "ERROR Parsing Spell Pair in onGetSpellEffects"
                val cardId = spellEffect?.mId.toString()
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
    }*/

    private fun finishTutorial() {
        mPreferencesManager?.setHasBeenOnboarded(1)

        val intent = Intent(mContext, MainActivity::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.exitTransition = null
        }
        startActivity(intent)

    }


    override fun onGetSpellEffects(spellEffects: List<SpellEffect>?) {

        Log.d(TAG, "onGetSpellEffects  [${mPreferencesManager?.getCurrentLifeTime()}]")
        val system = mPreferencesManager?.getSystem() ?: -1
        val fullCards = mutableListOf<FullCard?>()
        val cardIds = mutableListOf<String>()



        //mPreferencesManager?.addToPreviousCardsList(cardIds)

        setupViewPager(fullCards.toList())
    }



    private fun setupViewPager(fullCards: List<FullCard?>?) {
        Log.d(TAG, "setupViewPager  [${mPreferencesManager?.getCurrentLifeTime()}]")
        Log.d(TAG, "setupViewPager  [${fullCards}]")
        val system = mPreferencesManager?.getSystem() ?: -1
        val damagePrefs = mPreferencesManager?.getDamagePreferences()
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
                        val title = spellEffect?.mId.toString()
                        val id = spellEffect?.mId ?: -1
                        Log.d(TAG, "setupViewPager - it(full card) = ${it}")
                        val fragment = MainCardFragment.newInstance(this, it)
                        //fragment.setCallback(this)
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

            //mPreviousPageButton.visibility = GONE

            mViewPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

                override fun onPageScrollStateChanged(state: Int) {
                    Log.d(TAG, "onPageScrollStateChanged - state = $state")
                    mViewPagerState = state
                }

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                    //mNextPageButton.visibility = GONE
                    //mPreviousPageButton.visibility = GONE
                }

                override fun onPageSelected(position: Int) {
                    Log.d(TAG, "onPageSelected - position = $position")
                    mViewPagerSelection = position
                    if (mViewPagerState == 2) {
                        val count = mViewPager?.adapter?.count ?: -100
                        val lastPosition = count - 1
                        Log.d(TAG, "onPageSelected - lastPosition = $lastPosition")
                        if (position == lastPosition) {
                            onLoadingViewToggle(true)
                            Handler().postDelayed({
                                mViewPager?.setCurrentItem(0, false)
                                //mPresenter?.getSpellEffects()
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
                    val title = spellEffect?.mId.toString()
                    val id = spellEffect?.mId ?: -1
                    Log.d(TAG, "setupViewPager - it(full card) = ${it}")
                    val fragment = MainCardFragment.newInstance(this, it)
                    //fragment.setCallback(this)
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


    }

    private fun initializeView() {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        mScreenHeight = height
        val width = displayMetrics.widthPixels
        mScreenWidth = width

        val menuStartingLocation = mTutorialSideMenu.translationX
        mMenuStartingLocation = menuStartingLocation
        val menuClosedLocation = menuStartingLocation - (width * 0.8f)
        mMenuClosedLocation = menuClosedLocation

        mMenuIsAnimating = true
        val transAnimation = ObjectAnimator.ofFloat(mTutorialSideMenu, "translationX", mMenuStartingLocation, mMenuClosedLocation)
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

        //mSettingsContainer.visibility = GONE
        //mNetLibramInfo.visibility = GONE

        //initializeSettingsPopup()
        setupClickListeners()
    }

    private fun setupClickListeners() {
        Log.d(TAG, "setupClickListeners  [${mPreferencesManager?.getCurrentLifeTime()}]")
        /*mGenerateNewSpellEffectButton?.setOnClickListener {
            //mPresenter?.generateSingleSpellEffect()
        }*/

        /*iSpellInfoCollapseButton?.setOnClickListener {
            //
        }*/

        /*mSettingsButton.setOnClickListener {
           // mPresenter?.clickSettings(true)
        }*/

        /*iSettingsBackButton.setOnClickListener {
           // mPresenter?.clickSettings(false)
        }*/

        tNetLibramLink.setOnClickListener {
            val uri = Uri.parse("https://www.facebook.com/The-Net-Libram-of-Random-Magical-Effects-106043207533296")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        /*iPreviousPageButton.setOnClickListener {
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
        }*/

        /*mNetLibramInfo.setOnClickListener {
            //toggleNetLibramInfoOverlay()
        }*/

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

        /*tSideMenuSettingsButton.setOnClickListener {
            toggleSideMenu(false)
            //mPresenter?.clickSettings(true)
        }
        tSideMenuAboutButton.setOnClickListener {
            onShowToastMessage("About")
            toggleSideMenu(false)
        }*/

    }

    private fun toggleSideMenu(shouldOpen: Boolean) {
        Log.d(TAG, "toggleSideMenu  [${mPreferencesManager?.getCurrentLifeTime()}]")
        if (!mMenuIsAnimating) {
            mMenuIsAnimating = true
            when (mMenuIsClosed) {
                true -> {
                    val transAnimation = ObjectAnimator.ofFloat(mTutorialSideMenu, "translationX", mMenuClosedLocation, mMenuStartingLocation)
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
                    val transAnimation = ObjectAnimator.ofFloat(mTutorialSideMenu, "translationX", mMenuStartingLocation, mMenuClosedLocation)
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

    /*override fun onGeneratedSingleSpellEffect(spellEffect: SpellEffect) {
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

    override fun updateDebugText(systemText: String?){
        Log.d(TAG, "updateDebugText  [${mPreferencesManager?.getCurrentLifeTime()}]")
        tDebugSystemText.text = systemText
    }

    override fun updatePreferences(prefs: PreferencesManager?) {
        Log.d(TAG, "updatePreferences  [${mPreferencesManager?.getCurrentLifeTime()}]")
        mPreferencesManager = prefs
        val system = prefs?.getSystem()
        when (system) {
            RPG_SYSTEM_D20 -> {radioSystemDND5E.isChecked = true}
            RPG_SYSTEM_SAVAGEWORLDS -> {radioSystemSWADE.isChecked = true}
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
    }

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            val checked = view.isChecked

            val system = mPreferencesManager?.getSystem()

            when (view.id) {
                R.id.radioSystemDND5E ->
                    if (checked) {
                        when (system) {
                            RPG_SYSTEM_D20 -> { } //Do nothing
                            else -> {
                                mPreferencesManager?.selectSystem(RPG_SYSTEM_D20)
                                val id = mCurrentSpellEffect?.mId ?: -1
                                mPresenter?.retrieveSpellEffectById(id)
                            }
                        }
                    }
                R.id.radioSystemSWADE ->
                    if (checked) {
                        when (system) {
                            RPG_SYSTEM_SAVAGEWORLDS -> { } //Do nothing
                            else -> {
                                mPreferencesManager?.selectSystem(RPG_SYSTEM_SAVAGEWORLDS)
                                val id = mCurrentSpellEffect?.mId ?: -1
                                mPresenter?.retrieveSpellEffectById(id)
                            }
                        }
                    }
            }
        }
    }*/

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


    /*override fun onClickSettings(showSettings: Boolean) {
        //These will need animations at some point
        when (showSettings) {
            true -> {mSettingsContainer.visibility = VISIBLE}
            else -> {mSettingsContainer.visibility = GONE}
        }
    }*/

    /*private fun initializeSettingsPopup() {
        Log.d(TAG, "initializeSettingsPopup  [${mPreferencesManager?.getCurrentLifeTime()}]")
        val casterCapitalize = checkBoxTargetCaster.text.toString().split(' ').joinToString(" ") { it.capitalize() }
        checkBoxTargetCaster.text = casterCapitalize
        val nearestAllyCapitalize = checkBoxTargetNearestAlly.text.toString().split(' ').joinToString(" ") { it.capitalize() }
        checkBoxTargetNearestAlly.text = nearestAllyCapitalize
        val nearestEnemyCapitalize = checkBoxTargetNearestEnemy.text.toString().split(' ').joinToString(" ") { it.capitalize() }
        checkBoxTargetNearestEnemy.text = nearestEnemyCapitalize
        val closeCreatureCapitalize = checkBoxTargetNearestCreature.text.toString().split(' ').joinToString(" ") { it.capitalize() }
        checkBoxTargetNearestCreature.text = closeCreatureCapitalize
    }*/

    /*override fun songVideoInit(showVideo: Boolean, song: Song?) {
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
    }*/

}


