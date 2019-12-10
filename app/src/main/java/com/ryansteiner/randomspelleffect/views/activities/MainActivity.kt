package com.ryansteiner.randomspelleffect.views.activities

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.View
import com.ryansteiner.randomspelleffect.R
import com.ryansteiner.randomspelleffect.contracts.MainContract
import com.ryansteiner.randomspelleffect.data.*
import com.ryansteiner.randomspelleffect.presenters.MainPresenter
import com.ryansteiner.randomspelleffect.utils.*
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.CheckBox
import android.widget.RadioButton
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
import android.view.View.*
import android.view.animation.AccelerateDecelerateInterpolator
import kotlinx.android.synthetic.main.fragment_main_card.*
import android.view.ViewAnimationUtils
import android.os.Build
import com.ryansteiner.randomspelleffect.data.models.*
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
    private var mPreferencesManager: PreferencesManager? = null
    private val mSpellsList = SpellsList(this)
    private var mCurrentSpellEffect: SpellEffect? = null
    private var mScreenWidth = 0
    private var mScreenHeight = 0
    private var mMenuStartingLocation = 0f
    private var mMenuClosedLocation = 0f
    private var mMenuIsClosed = true
    private var mMenuIsAnimating = false
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
        setContentView(R.layout.activity_main)


        mPresenter = MainPresenter(this)
        mPresenter?.bindView(this)

        mViewPager = findViewById(R.id.mMainCardPager)

            //mPreferencesManager = PreferencesManager(this)
        mPresenter?.getPreferences()
        mPresenter?.loadDatabase(this)
        //val spellsList = SpellsList(this)
        //mPresenter?.updateSpellList(spellsList)

        //mPresenter?.updateSpellList(mSpellsList)

        mPresenter?.initializeView()

    }

    override fun onLoadedDatabase() {
        mPresenter?.getSpellEffects()
    }

    override fun onGetSpellEffects(spellEffects: List<SpellEffect>?) {

        Log.d(TAG, "onGetSpellEffects - spellEffects = $spellEffects")
        val system = mPreferencesManager?.getSystem() ?: -1
        val fullCards: MutableList<FullCard?> = mutableListOf()
        if (spellEffects != null && spellEffects.count() > 0) {
            for (i in 0 until spellEffects.count()) {
                val fullCard = FullCard()
                val spellEffect = spellEffects[i]
                val desc = spellEffect?.mDescription
                val parsedResult: ParseSpellEffectStringResult? = mPresenter?.parseSpellStringForVariables(desc, system)
                val cardText =  parsedResult?.mFullString ?: "ERROR Parsing Spell Pair in onGetSpellEffects"
                fullCard.setSpellEffect(spellEffect)
                fullCard.setMainText(cardText)
                fullCard.setSpell(parsedResult?.mSpell)
                fullCard.setGameplayModifier(parsedResult?.mGameplayModifier)
                fullCard.setSong(parsedResult?.mSong)
                fullCards.add(fullCard)
            }
        }

        setupViewPager(fullCards.toList())
    }



    private fun setupViewPager(fullCards: List<FullCard?>?) {
        if (fullCards != null && fullCards.count() > 0) {

            val system = mPreferencesManager?.getSystem() ?: -1
            val damagePrefs = mPreferencesManager?.getDamagePreferences()
            mViewPager = findViewById(R.id.mMainCardPager)
            mViewPager.pageMargin = 120
            mViewPager.setPageTransformer(true, TiltAnglePageTransformer())
            val pagerAdapter = MainCardPageViewAdapter(supportFragmentManager)
            fullCards.forEach {
                if (it != null) {
                    val spellEffect = it.getSpellEffect()
                    val title = spellEffect?.mId.toString()
                    val id = spellEffect?.mId ?: -1
                    Log.d(TAG, "setupViewPager - it(full card) = ${it}")
                    val fragment = MainCardFragment.newInstance(this, it)
                    fragment.setCallback(this)
                    fragment.setSystem(system)
                    fragment.setDamagePrefs(damagePrefs)
                    pagerAdapter.addFragment(fragment, title)
                }

            }
            Log.d(TAG, "setupViewPager - pagerAdapter = ${pagerAdapter}")
            mViewPager.adapter = pagerAdapter
        }
        val count = mViewPager.adapter?.count
        Log.d(TAG, "setupViewPager - count = ${count}")

        mPreviousPageButton.visibility = GONE

        mViewPager?.addOnPageChangeListener(object: ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {
                Log.d(TAG, "onPageScrollStateChanged - state = $state")
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                Log.d(TAG, "onPageScrolled - position = $position")
                val max = mViewPager?.adapter?.count ?: -1
                Log.d(TAG, "onPageScrolled - max = $max")
                when {
                    position == max - 2 -> {
                        mNextPageButton.visibility = GONE
                        mPreviousPageButton.visibility = VISIBLE
                    }
                    position == 0 -> {
                        mNextPageButton.visibility = VISIBLE
                        mPreviousPageButton.visibility = GONE
                    }
                    else -> {
                        mNextPageButton.visibility = VISIBLE
                        mPreviousPageButton.visibility = VISIBLE
                    }
                }


            }

            override fun onPageSelected(position: Int) {
                Log.d(TAG, "onPageSelected - position = $position")


            }

        })
    }

    override fun onInitializedView() {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        mScreenHeight = height
        val width = displayMetrics.widthPixels
        mScreenWidth = width

        val menuStartingLocation = mSideMenu.translationX
        Log.d(TAG, "onInitializedView - menuStartingLocation = $menuStartingLocation")
        mMenuStartingLocation = menuStartingLocation
        val menuClosedLocation = menuStartingLocation - (width * 0.8f)
        Log.d(TAG, "onInitializedView - menuStartingLocation = $menuClosedLocation")
        mMenuClosedLocation = menuClosedLocation
        //mSideMenu.translationX = menuClosedLocation

        mMenuIsAnimating = true
        val transAnimation = ObjectAnimator.ofFloat(mSideMenu, "translationX", mMenuStartingLocation, mMenuClosedLocation)
        transAnimation.setDuration(1)
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
        /*val animation = TranslateAnimation(0f, mMenuClosedLocation, 0f, 0f)
        animation.duration = 1
        animation.fillAfter = true
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {

                //mSideMenu.translationX = mMenuClosedLocation
                mMenuIsAnimating = false
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })
        mSideMenu.startAnimation(animation)*/

        //mSpellEffectAdditionalInfoContainer?.visibility = INVISIBLE
        //mSpellEffectAdditionalInfoContainer?.isEnabled = false

        mSettingsContainer.visibility = GONE

        mNetLibramInfo.visibility = GONE

        initializeSettingsPopup()
        setupClickListeners()
    }

    private fun setupClickListeners() {
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
            val uri = Uri.parse("https://www.facebook.com/The-Net-Libram-of-Random-Magical-Effects-106043207533296")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
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
                }
                else -> {
                    toggleSideMenu(true)
                }
            }
        }

        tSideMenuSettingsButton.setOnClickListener {
            toggleSideMenu(false)
            mPresenter?.clickSettings(true)
        }
        tSideMenuAboutButton.setOnClickListener {
            onShowToastMessage("About")
            toggleSideMenu(false)
        }

    }

    private fun toggleSideMenu(shouldOpen: Boolean) {
        if (!mMenuIsAnimating) {
            mMenuIsAnimating = true
            when (mMenuIsClosed) {
                true -> {
                    val transAnimation = ObjectAnimator.ofFloat(mSideMenu, "translationX", mMenuClosedLocation, mMenuStartingLocation)
                    transAnimation.setDuration(250)
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
                    transAnimation.setDuration(250)
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

        mCurrentSpellEffect = spellEffect

        //TODO Move to Frag vColorLayer.visibility = GONE
        //TODO Move to Frag vPatternLayer.visibility = GONE

        var spellEffectText: String? = ""
        spellEffectText = when {
            spellEffect.mId == null -> "spellEffect was null"
            spellEffect.mId < 0 -> "spellEffect was below 0"
            else -> spellEffect.mDescription
        }



        //Find an image to use with iCardCenterImage
        /* Probably cut for first release
        if (!spellEffect.mUsesImage.isNullOrBlank()) {

            iCardCenterImage.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
            val width = iCardCenterImage.measuredWidth
            val height = iCardCenterImage.measuredHeight

            Log.d(TAG, "onGeneratedSingleSpellEffect - width = ${width}")
            Log.d(TAG, "onGeneratedSingleSpellEffect - height = ${height}")

            val fullResourceString = spellEffect.mUsesImage
            val resource = resources.getIdentifier(fullResourceString, "drawable", this.packageName)
            Glide.with(this)
                .load(resource)
                .placeholder(R.drawable.card_image_placeholder)
                .error(R.drawable.card_image_placeholder)
                .apply(RequestOptions.bitmapTransform(CropTransformation(width, height, CropTransformation.CropType.TOP)))
                .into(iCardCenterImage)
        }



        iCardCenterImage.visibility = when {
            spellEffect.mUsesImage.isNullOrBlank() -> GONE
            else -> VISIBLE
        }
         */

        val system = mPreferencesManager?.getSystem() ?: -1
        val damagePrefs = mPreferencesManager?.getDamagePreferences()

        mPresenter?.updateDamagePreferences(damagePrefs)

        //val spellPair = mPresenter?.parseSpellStringForVariables(spellEffectText, system)
        //spellEffectText = spellPair?.first ?: "ERROR parsing string in onGeneratedSingleSpellEffect"
        //Log.d(TAG, "onGeneratedSingleSpellEffect - spellEffectText 1 = ${spellEffectText}")

        //TODO Move to Frag val borderContainers = listOf<FrameLayout>(fBorderLeft, fBorderRight, fBorderTop, fBorderBottom)

        /*TODO Move to Frag borderContainers.forEach{
            val resourceInt = mBorderColors[spellEffect.mHowBadIsIt] ?: -1
            val resourceColor = ContextCompat.getColor(this, resourceInt)
            it.setBackgroundColor(resourceColor)


        }*/
        spellEffectText = spellEffectText?.capitalize()

        val finalSpellEffectText = spellEffectText ?: "Something went very wrong."

        //TODO Move to Frag mMainActivityText?.text = finalSpellEffectText

        Log.d(TAG, "onGeneratedSingleSpellEffect - spellEffect?.mIsNetLibram = ${spellEffect?.mIsNetLibram}")
        /*TODO Move to Frag tNetLibramMoreInfo.visibility = when {
            spellEffect?.mIsNetLibram == true -> VISIBLE
            else -> GONE
        }*/
    }

    override fun updateColorLayer(colorId: Int?, visibility: Boolean) {
        if (colorId != null) {
            //TODO Move to Frag vColorLayer.setBackgroundColor(colorId!!)
        }

        /*TODO Move to Frag vColorLayer.visibility = when (visibility) {
            true -> VISIBLE
            else -> GONE
        }*/
    }

    override fun updatePatternLayer(patternId: Int?, visibility: Boolean) {
        if (patternId != null) {
            //TODO Move to Frag vPatternLayer.background = ContextCompat.getDrawable(this, patternId)
        }

        /*TODO Move to Frag vPatternLayer.visibility = when (visibility) {
            true -> VISIBLE
            else -> GONE
        }*/
    }

    override fun updateDiceRoll(selectedSpellDice: String?) {
        /*TODO Move to Frag val diceRollImages = listOf<TextView>(
            tDiceRollImage1, tDiceRollImage2, tDiceRollImage3, tDiceRollImage4, tDiceRollImage5,
            tDiceRollImage6, tDiceRollImage7, tDiceRollImage8, tDiceRollImage9, tDiceRollImage10,
            tDiceRollImage11, tDiceRollImage12, tDiceRollImage13, tDiceRollImage14, tDiceRollImage15,
            tDiceRollImage16, tDiceRollImage17, tDiceRollImage18, tDiceRollImage19, tDiceRollImage20
        )
        mSpellInfoDiceRollsContainer.visibility = GONE
        diceRollImages.forEach() {
            it.text = ""
        }
        tSpellInfoRollMultiplierEquals.visibility = VISIBLE
        tSpellInfoRollMultiplierX.visibility = GONE
        tSpellInfoRollMultiplierNumber.visibility = GONE

        if (!selectedSpellDice.isNullOrBlank()) {
            var workingString = selectedSpellDice
            var numberOfDice: Int? = null
            var typeOfDie: Int? = null
            var multiplier: Float? = null
            var onGoingEffects: String = ""

            val splitStringsComma = workingString.split(",")
            if (splitStringsComma.count() > 1) {
                onGoingEffects = splitStringsComma.last()
                workingString = splitStringsComma.first()
            }

            val splitStringMultiplier = workingString.split("*")
            if (splitStringMultiplier.count() > 1) {
                workingString = splitStringMultiplier.first()
                multiplier = splitStringMultiplier.last().toFloatOrNull()
            }

            var roll = 0f
            val splitStringDieType = workingString.split("d")
            if (splitStringDieType.count() > 1) {
                numberOfDice = splitStringDieType.first().toIntOrNull()
                typeOfDie = splitStringDieType.last().toIntOrNull()


                for (i in 0 until numberOfDice!!) {
                    val random = (1..typeOfDie!!).random()
                    roll += random
                    if (i < diceRollImages.count()) {
                        val img = diceRollImages[i]
                        img.text = random.toString()
                        img.background = when (typeOfDie) {
                            4 -> {resources.getDrawable(R.drawable.dice_silhouette_d4)}
                            6 -> {resources.getDrawable(R.drawable.dice_silhouette_d6)}
                            8 -> {resources.getDrawable(R.drawable.dice_silhouette_d6)}
                            10 -> {resources.getDrawable(R.drawable.dice_silhouette_d10)}
                            12 -> {resources.getDrawable(R.drawable.dice_silhouette_d6)}
                            20 -> {resources.getDrawable(R.drawable.dice_silhouette_d6)}
                            else -> {resources.getDrawable(R.drawable.dice_silhouette_d6)}
                        }
                    }
                }
                if (multiplier != null && multiplier > 0) {
                    roll *= multiplier
                }
            }
            var hideDiceImageContainer = true
            diceRollImages.forEach() {
                it.visibility = when {
                    it.text.isNullOrBlank() -> GONE
                    else -> {
                        hideDiceImageContainer = false
                        VISIBLE
                    }
                }
            }

            mSpellInfoDiceRollsContainer.visibility = when (hideDiceImageContainer) {
                true -> GONE
                else -> VISIBLE
            }

            var diceRollFullText = ""
            val sb = StringBuilder()
            sb.append(numberOfDice)
            sb.append("d")
            sb.append(typeOfDie)
            if (multiplier != null && multiplier > 0) {
                sb.append(" x ")
                val fraction = MyMathUtils().convertDecimalToFraction(multiplier.toDouble())
                sb.append(fraction)
            }
            if (roll > 0) {
                val roundedRoll = roll.roundToInt()
                tSpellInfoRollMultiplierResult.text = roundedRoll.toString()
                tSpellInfoRollMultiplierResult.visibility = VISIBLE
            } else {
                tSpellInfoRollMultiplierResult.visibility = GONE
            }
            when {
                (multiplier != null && multiplier > 0) -> {
                    tSpellInfoRollMultiplierX.visibility = VISIBLE
                    tSpellInfoRollMultiplierNumber.visibility = VISIBLE

                }
                else -> {
                    tSpellInfoRollMultiplierX.visibility = GONE
                    tSpellInfoRollMultiplierNumber.visibility = GONE

                }
            }

            diceRollFullText = sb.toString()

            tSpellDiceRoll.text = diceRollFullText

            tSpellDiceRoll.visibility = when {
                diceRollFullText.isNullOrBlank() -> GONE
                else -> VISIBLE
            }

        }*/

    }

    override fun updateSpellInfoContainer(visibility: Boolean, spellText: String?, selectedSpellDescriptionWithDamageLevel: String?, selectedSpellPageNumber: String?) {
        //TODO Move to Frag  tSpellTitle.text = spellText
        //TODO Move to Frag tSpellDescription.text = selectedSpellDescriptionWithDamageLevel
        val spellPageFullText = "$spellText can be found on $selectedSpellPageNumber"
        //TODO Move to Frag tSpellPage.text = spellPageFullText

        /*TODO Move to Frag  when (visibility) {
            true -> mSpellInfoContainer.visibility = VISIBLE
            else -> mSpellInfoContainer.visibility = GONE
        }*/
    }

    override fun test() {
        //TODO Move to Frag mMainActivityText?.text = "spellEffect was null"
    }

    override fun updateDebugText(systemText: String?){
        tDebugSystemText.text = systemText
    }

    override fun updatePreferences(prefs: PreferencesManager?) {
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
    }

    fun onFABClick(view: View) {
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




        /*mFloatingActionButtonInfo.animate()
            //.scaleX(0.0f)
            //.scaleY(0.0f)
            .rotation(720f)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .setDuration(1000)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    onShowToastMessage("Animation Ended")
                    mFloatingActionButtonInfo.rotation = 0f
                }
            })*/
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
            Log.d(TAG, "onClickCheckBox - mPreferencesManager?.getTargets() = ${mPreferencesManager?.getTargets()}")
        }
    }

    override fun onClickSettings(showSettings: Boolean) {
        //These will need animations at some point
        when (showSettings) {
            true -> {mSettingsContainer.visibility = VISIBLE}
            else -> {mSettingsContainer.visibility = GONE}
        }
    }

    private fun initializeSettingsPopup() {
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

        val videoId = song?.mUrl ?: ""
        val startSeconds = 0f
/*
        if (mYouTubePlayerView == null) {
            val youTubePlayerView: YouTubePlayerView = findViewById(R.id.vYouTubeVideoView)
            mYouTubePlayerView = youTubePlayerView

            lifecycle.addObserver(youTubePlayerView)

            val uiController = youTubePlayerView.getPlayerUiController()
            uiController.showFullscreenButton(false)




            youTubePlayerView.addYouTubePlayerListener(object: AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    if (!videoId.isNullOrBlank()) {
                        youTubePlayer.loadVideo(videoId, startSeconds)
                        youTubePlayer.pause()
                    }
                    mYouTubePlayer = youTubePlayer
                }
            })
        } else {
            mYouTubePlayer?.loadVideo(videoId, startSeconds)
            mYouTubePlayer?.pause()

        }

        when (showVideo) {
            true -> {



                //TODO fragment vYouTubeVideoView.visibility = VISIBLE
            }
            else -> {
                //TODO fragment vvYouTubeVideoView.visibility = GONE
                }
        }
        */
    }

    /*override fun onDestroy() {
        super.onDestroy()
        mYouTubePlayerView?.release()
    }*/

    override fun onFragmentInteraction(uri: Uri) {
        Log.d(TAG, "onFragmentInteraction")
    }

    override fun toggleNetLibramInfoOverlay() {
        mNetLibramInfo.visibility = when (mNetLibramInfo.visibility) {
            VISIBLE -> GONE
            else -> VISIBLE
        }
    }

}


