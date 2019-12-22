package com.ryansteiner.randomspelleffect.views.activities

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.ColorFilter
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.ryansteiner.randomspelleffect.R
import com.ryansteiner.randomspelleffect.data.*
import com.ryansteiner.randomspelleffect.data.models.FullCard
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.ryansteiner.randomspelleffect.utils.MyMathUtils
import com.ryansteiner.randomspelleffect.utils.PreferencesManager
import kotlin.math.roundToInt

class MainCardFragment(private val c: Context)// Required empty public constructor
    : Fragment() {

    private val TAG = "MainCardFragment"

    private val mContext: Context = c
    private var mFullCard: FullCard? = null
    private var mFullText: String? = null
    private var mCallback: MainActivity? = null
    private var mSystem: Int? = null
    private var mView: View? = null
    private var mDamagePreferences: List<Int>? = null
    private var mYouTubePlayerView: YouTubePlayerView? = null
    private var mYouTubePlayer: YouTubePlayer? = null
    private var mPreferencesManager: PreferencesManager? = null


    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPreferencesManager = PreferencesManager(mContext)
        Log.d(TAG, "onCreate  [${mPreferencesManager?.getCurrentLifeTime()}]")
        if (arguments != null) {
            mFullCard = arguments!!.getSerializable(EXTRA_FULL_CARD_SERIALIZABLE) as? FullCard
            mFullText = mFullCard?.getMainText()
        }
    }

    private fun init(v: View) {
        Log.d(TAG, "init  [${mPreferencesManager?.getCurrentLifeTime()}]")
        mView = v
        val mCardContainer: FrameLayout = v.findViewById(R.id.mCardContainer)
        val mFloatingActionButton: FloatingActionButton = v.findViewById((R.id.mFloatingActionButtonInfo))
        val mNetLibramLabel: LinearLayout = v.findViewById((R.id.mNetLibramLabel))
        val mYouTubeVideoReloadButton: LinearLayout = v.findViewById((R.id.mYouTubeVideoReloadButton))
        val mSpellEffectAdditionalInfoBackground: LinearLayout = v.findViewById((R.id.mSpellEffectAdditionalInfoBackground))
        val mSpellEffectInfoDiceRollSeparator: View = v.findViewById((R.id.mSpellEffectInfoDiceRollSeparator))

        val tMainCardText: TextView = v.findViewById(R.id.tMainCardText)
        val tSpellEffectInfoTitle: TextView = v.findViewById((R.id.tSpellEffectInfoTitle))
        val tSpellEffectInfoDescription: TextView = v.findViewById((R.id.tSpellEffectInfoDescription))
        val tSpellEffectInfoPageInfo: TextView = v.findViewById((R.id.tSpellEffectInfoPageInfo))

        val tSpellEffectInfoDiceRoll: TextView = v.findViewById(R.id.tSpellEffectInfoDiceRoll)
        val tSpellEffectInfoRollMultiplierX: TextView = v.findViewById(R.id.tSpellEffectInfoRollMultiplierX)
        val tSpellEffectInfoRollMultiplierNumber: TextView = v.findViewById(R.id.tSpellEffectInfoRollMultiplierNumber)
        val tSpellEffectInfoRollMultiplierEquals: TextView = v.findViewById(R.id.tSpellEffectInfoRollMultiplierEquals)
        val tSpellInfoRollMultiplierResult: TextView = v.findViewById(R.id.tSpellInfoRollMultiplierResult)

        val textViewList = listOf(
            tSpellEffectInfoTitle,
            tSpellEffectInfoDescription,
            tSpellEffectInfoPageInfo,
            tSpellEffectInfoDiceRoll,
            tSpellEffectInfoRollMultiplierX,
            tSpellEffectInfoRollMultiplierNumber,
            tSpellEffectInfoRollMultiplierEquals,
            tSpellInfoRollMultiplierResult
        )

        if (mFullText == null) {
            mCardContainer.visibility = INVISIBLE
        } else {
            tMainCardText?.text = mFullText

            mFloatingActionButton?.setOnClickListener {
                extraInfoButtonClicked()
            }
            mNetLibramLabel?.setOnClickListener {
                mCallback?.toggleNetLibramInfoOverlay()
            }

            mYouTubeVideoReloadButton.setOnClickListener {
                val song = mFullCard?.getSong()
                val videoId = song?.mUrl ?: ""
                val startSeconds = song?.mStartAt ?: 0
                val startSecondsFloat = startSeconds.toFloat()
                mYouTubePlayer?.loadVideo(videoId, startSecondsFloat)
                mYouTubePlayer?.pause()
            }

            val spellEffect = mFullCard?.getSpellEffect()
            val spell = mFullCard?.getSpell()
            val gameplayModifier = mFullCard?.getGameplayModifier()

            if (spell != null && mSystem != null) {
                if (!spell?.mTitle.isNullOrBlank()) {
                    tSpellEffectInfoTitle.text = spell?.mTitle
                }
                if (!spell?.mTitle.isNullOrBlank()) {
                    val damageOptions = mutableListOf<String>()
                    if (mDamagePreferences != null && mDamagePreferences!!.count() > 0) {
                        if (mDamagePreferences!!.contains(DAMAGE_INT_LOW)) {
                            damageOptions.add(DAMAGE_STRING_LOW)
                        }
                        if (mDamagePreferences!!.contains(DAMAGE_INT_MED)) {
                            damageOptions.add(DAMAGE_STRING_MED)
                        }
                        if (mDamagePreferences!!.contains(DAMAGE_INT_HIGH)) {
                            damageOptions.add(DAMAGE_STRING_HIGH)
                        }
                    } else {
                        damageOptions.add(DAMAGE_STRING_LOW)
                        damageOptions.add(DAMAGE_STRING_MED)
                        damageOptions.add(DAMAGE_STRING_HIGH)
                    }
                    val selectedDamageLevel = damageOptions.random()

                    var selectedSpellDescriptionWithDamageLevel = ""
                    var selectedSpellDice = ""
                    var selectedSpellPageNumber = ""

                    when (mSystem) {
                        RPG_SYSTEM_D20 -> {
                            val safeSelectedSpell = spell!!.mDND5EDescriptions!![selectedDamageLevel]
                            val safeMedSpell = spell!!.mDND5EDescriptions!![DAMAGE_STRING_MED]
                            val safeSelectedDice = spell!!.mDND5EDice!![selectedDamageLevel]
                            val safeMedDice = spell!!.mDND5EDice!![DAMAGE_STRING_MED]
                            selectedSpellDescriptionWithDamageLevel = when {
                                !safeSelectedSpell.isNullOrBlank() -> safeSelectedSpell
                                !safeMedSpell.isNullOrBlank() -> safeMedSpell
                                else -> "ERROR with selectedSpell"
                            }
                            selectedSpellDice = when {
                                !safeSelectedDice.isNullOrBlank() -> safeSelectedDice
                                !safeMedDice.isNullOrBlank() -> safeMedDice
                                else -> "ERROR with selectedDice"
                            }
                            selectedSpellPageNumber = spell!!.mDND5EPageNumber ?: ""
                        }
                        RPG_SYSTEM_SAVAGEWORLDS -> {
                            val safeSelectedSpell = spell!!.mSWADEDescriptions!![selectedDamageLevel]
                            val safeMedSpell = spell!!.mSWADEDescriptions!![DAMAGE_STRING_MED]
                            val safeSelectedDice = spell!!.mSWADEDice!![selectedDamageLevel]
                            val safeMedDice = spell!!.mSWADEDice!![DAMAGE_STRING_MED]
                            selectedSpellDescriptionWithDamageLevel = when {
                                !safeSelectedSpell.isNullOrBlank() -> safeSelectedSpell
                                !safeMedSpell.isNullOrBlank() -> safeMedSpell
                                else -> "ERROR with selectedSpell"
                            }
                            selectedSpellDice = when {
                                !safeSelectedDice.isNullOrBlank() -> safeSelectedDice
                                !safeMedDice.isNullOrBlank() -> safeMedDice
                                else -> "ERROR with selectedDice"
                            }
                            selectedSpellPageNumber = spell!!.mSWADEPageNumber ?: ""
                        }
                        //TODO Need to add generics to spells
                        else -> {
                            selectedSpellDescriptionWithDamageLevel = "NO SYSTEM"
                            selectedSpellDice = "NO SYSTEM"
                            selectedSpellPageNumber = "NO SYSTEM"
                        }
                    }

                    tSpellEffectInfoDescription.text = selectedSpellDescriptionWithDamageLevel
                    tSpellEffectInfoPageInfo.text = selectedSpellPageNumber
                    updateDiceRoll(v, selectedSpellDice)
                }
            } else if (gameplayModifier != null) {
                //TODO convert to getDamageOptions from prefs
                val damageOptions = mutableListOf<String>()
                if (mDamagePreferences != null && mDamagePreferences!!.count() > 0) {
                    if (mDamagePreferences!!.contains(DAMAGE_INT_LOW)) {
                        damageOptions.add(DAMAGE_STRING_LOW)
                    }
                    if (mDamagePreferences!!.contains(DAMAGE_INT_MED)) {
                        damageOptions.add(DAMAGE_STRING_MED)
                    }
                    if (mDamagePreferences!!.contains(DAMAGE_INT_HIGH)) {
                        damageOptions.add(DAMAGE_STRING_HIGH)
                    }
                } else {
                    damageOptions.add(DAMAGE_STRING_LOW)
                    damageOptions.add(DAMAGE_STRING_MED)
                    damageOptions.add(DAMAGE_STRING_HIGH)
                }
                val selectedDamageLevel = damageOptions.random()

                var selectedModifierWithSeverity = ""
                var selectedModifierPageInfo = ""
                var selectedModifierName = ""

                //TODO convert these to gets from GameplayModifier class
                when (mSystem) {
                    RPG_SYSTEM_D20 -> {
                        val safeSelected = gameplayModifier!!.mDND5EDescriptions!![selectedDamageLevel]
                        val safeMed = gameplayModifier!!.mDND5EDescriptions!![DAMAGE_STRING_MED]
                        selectedModifierWithSeverity = when {
                            !safeSelected.isNullOrBlank() -> safeSelected
                            !safeMed.isNullOrBlank() -> safeMed
                            else -> "ERROR with DND5E/Severity"
                        }
                        selectedModifierPageInfo = gameplayModifier!!.mDND5EPageInfo ?: ""
                        selectedModifierName = gameplayModifier!!.mDND5EName ?: "ERROR with DND5E/Name"
                    }
                    RPG_SYSTEM_SAVAGEWORLDS -> {
                        val safeSelected = gameplayModifier?.mSWADEDescriptions!![selectedDamageLevel]
                        val safeMed = gameplayModifier!!.mSWADEDescriptions!![DAMAGE_STRING_MED]
                        selectedModifierWithSeverity = when {
                            !safeSelected.isNullOrBlank() -> safeSelected
                            !safeMed.isNullOrBlank() -> safeMed
                            else -> "ERROR with SWADE/Severity"
                        }
                        selectedModifierPageInfo = gameplayModifier!!.mSWADEPageInfo ?: ""
                        selectedModifierName = gameplayModifier!!.mSWADEName ?: "ERROR with SWADE/Name"
                    }
                    else -> {
                        val safeSelected = gameplayModifier!!.mGenericDescriptions!![selectedDamageLevel]
                        val safeMed = gameplayModifier!!.mGenericDescriptions!![DAMAGE_STRING_MED]
                        selectedModifierWithSeverity = when {
                            !safeSelected.isNullOrBlank() -> safeSelected
                            !safeMed.isNullOrBlank() -> safeMed
                            else -> "ERROR with Generic/Severity"
                        }
                        selectedModifierName = gameplayModifier!!.mGenericName ?: "ERROR with Generic/Name"
                    }
                }
                tSpellEffectInfoTitle.text = selectedModifierName.split(' ').joinToString(" ") { it.capitalize() }
                tSpellEffectInfoDescription.text = selectedModifierWithSeverity.capitalize()
                if (selectedModifierPageInfo.isNullOrBlank()) {
                    tSpellEffectInfoPageInfo.visibility = GONE
                } else {
                    tSpellEffectInfoPageInfo.text = selectedModifierPageInfo
                    tSpellEffectInfoPageInfo.visibility = VISIBLE
                }
                updateDiceRoll(v, null)
            } else {
                mFloatingActionButton?.hide()
            }
            mNetLibramLabel.visibility = when {
                spellEffect?.mIsNetLibram == true -> VISIBLE
                else -> GONE
            }

            val neutralCardBackList = listOf(
                R.drawable.tarot_card_back_3,
                R.drawable.tarot_card_back_4,
                R.drawable.tarot_card_back_5,
                R.drawable.tarot_card_back_6
            )

            val colorIdList = listOf(
                R.color.colorGreenEmerald,
                R.color.colorBlueCadet,
                R.color.colorGreenCadmium,
                R.color.colorOrangeTerraCotta

            )

            val alphaList = listOf(
                COLOR_ALPHA_GREEN_EMERALD,
                COLOR_ALPHA_BLUE_CADET,
                COLOR_ALPHA_GREEN_CADMIUM,
                COLOR_ALPHA_ORANGE_TERRA_COTTA

            )

            var randomSelection = when {
                neutralCardBackList.count() > colorIdList.count() -> (1..colorIdList.count()).random()
                else -> (1..neutralCardBackList.count()).random()
            }

            randomSelection--


            val randomCardBack = neutralCardBackList[randomSelection]
            val randomColor = colorIdList[randomSelection]
            val randomAlpha = alphaList[randomSelection]

            val mCardContainer: FrameLayout = v.findViewById(R.id.mCardContainer)
            mCardContainer?.background = when (spellEffect?.mHowBadIsIt) {
                SEVERITY_NEUTRAL -> getDrawable(mContext, randomCardBack)
                SEVERITY_GOOD_LOW -> getDrawable(mContext, randomCardBack)
                SEVERITY_GOOD_MID -> getDrawable(mContext, R.drawable.tarot_card_back_2)
                SEVERITY_GOOD_HIGH -> getDrawable(mContext, R.drawable.tarot_card_back_1)
                SEVERITY_GOOD_NEAR_ENEMIES -> getDrawable(mContext, randomCardBack)
                SEVERITY_GOOD_NEAR_ALLIES -> getDrawable(mContext, randomCardBack)
                SEVERITY_BAD_LOW -> getDrawable(mContext, randomCardBack)
                SEVERITY_BAD_MID -> getDrawable(mContext, R.drawable.tarot_card_back_7)
                SEVERITY_BAD_YIKES -> getDrawable(mContext, R.drawable.tarot_card_back_8)
                else -> getDrawable(mContext, randomCardBack)
            }

            val iCardImage: ImageView = v.findViewById(R.id.iCardImage)

            /*val drawableList = listOf(
                ContextCompat.getDrawable(mContext, R.drawable.tarot_card_image_001),
                ContextCompat.getDrawable(mContext, R.drawable.tarot_card_image_002),
                ContextCompat.getDrawable(mContext, R.drawable.tarot_card_image_003),
                ContextCompat.getDrawable(mContext, R.drawable.tarot_card_image_004),
                ContextCompat.getDrawable(mContext, R.drawable.tarot_card_image_005),
                ContextCompat.getDrawable(mContext, R.drawable.tarot_card_image_006),
                ContextCompat.getDrawable(mContext, R.drawable.tarot_card_image_007),
                ContextCompat.getDrawable(mContext, R.drawable.tarot_card_image_008),
                ContextCompat.getDrawable(mContext, R.drawable.tarot_card_image_009),
                ContextCompat.getDrawable(mContext, R.drawable.tarot_card_image_010),
                ContextCompat.getDrawable(mContext, R.drawable.tarot_card_image_011),
                ContextCompat.getDrawable(mContext, R.drawable.tarot_card_image_012),
                ContextCompat.getDrawable(mContext, R.drawable.tarot_card_image_013),
                ContextCompat.getDrawable(mContext, R.drawable.tarot_card_image_014),
                ContextCompat.getDrawable(mContext, R.drawable.tarot_card_image_015),
                ContextCompat.getDrawable(mContext, R.drawable.tarot_card_image_016),
                ContextCompat.getDrawable(mContext, R.drawable.tarot_card_image_017),
                ContextCompat.getDrawable(mContext, R.drawable.tarot_card_image_018),
                ContextCompat.getDrawable(mContext, R.drawable.tarot_card_image_019),
                ContextCompat.getDrawable(mContext, R.drawable.tarot_card_image_020),
                ContextCompat.getDrawable(mContext, R.drawable.tarot_card_image_021),
                ContextCompat.getDrawable(mContext, R.drawable.tarot_card_image_022),
                ContextCompat.getDrawable(mContext, R.drawable.tarot_card_image_023),
                ContextCompat.getDrawable(mContext, R.drawable.tarot_card_image_024),
                ContextCompat.getDrawable(mContext, R.drawable.tarot_card_image_025),
                ContextCompat.getDrawable(mContext, R.drawable.tarot_card_image_026),
                ContextCompat.getDrawable(mContext, R.drawable.tarot_card_image_027),
                ContextCompat.getDrawable(mContext, R.drawable.tarot_card_image_028),
                ContextCompat.getDrawable(mContext, R.drawable.tarot_card_image_029),
                ContextCompat.getDrawable(mContext, R.drawable.tarot_card_image_030),
                ContextCompat.getDrawable(mContext, R.drawable.tarot_card_image_031),
                ContextCompat.getDrawable(mContext, R.drawable.tarot_card_image_032),
                ContextCompat.getDrawable(mContext, R.drawable.tarot_card_image_033),
                ContextCompat.getDrawable(mContext, R.drawable.tarot_card_image_034),
                ContextCompat.getDrawable(mContext, R.drawable.tarot_card_image_035),
                ContextCompat.getDrawable(mContext, R.drawable.tarot_card_image_036),
                ContextCompat.getDrawable(mContext, R.drawable.tarot_card_image_037),
                ContextCompat.getDrawable(mContext, R.drawable.tarot_card_image_038),
                ContextCompat.getDrawable(mContext, R.drawable.tarot_card_image_039),
                ContextCompat.getDrawable(mContext, R.drawable.tarot_card_image_040),
                ContextCompat.getDrawable(mContext, R.drawable.tarot_card_image_041),
                ContextCompat.getDrawable(mContext, R.drawable.tarot_card_image_042)
            )

            val previousImages = mPreferencesManager?.getPreviousCardImages()
            var selectedDrawable: Drawable? = null
            while (selectedDrawable == null) {
                val randomNumber = (0..(drawableList.count() - 1)).random()
                if (previousImages != null && previousImages!!.count() > 0) {
                    if (!previousImages.contains(randomNumber)) {
                        selectedDrawable = drawableList[randomNumber]
                    }
                } else {
                    selectedDrawable = drawableList[randomNumber]
                }
            }
            Log.d(TAG, "init - selectedDrawable = $selectedDrawable")*/
            var backgroundImageId = spellEffect?.mBackgroundImageId ?: -1
            if (backgroundImageId < 0) {
                Log.d(TAG, "init - spellEffect?.mBackgroundImageId was null = ${spellEffect?.mBackgroundImageId}")
                val count = mPreferencesManager?.getListOfCardBackgroundImagesCount() ?: 1
                val random = (0..count).random()
                backgroundImageId = random
            }
            val selectedDrawable = mPreferencesManager?.getCardBackgroundImagesAtIndex(backgroundImageId)

            iCardImage.setImageDrawable(selectedDrawable)

            val tint = when (spellEffect?.mHowBadIsIt) {
                SEVERITY_NEUTRAL -> ContextCompat.getColor(mContext, randomColor)
                SEVERITY_GOOD_LOW -> ContextCompat.getColor(mContext, randomColor)
                SEVERITY_GOOD_MID -> ContextCompat.getColor(mContext, R.color.colorBlueOxford)
                SEVERITY_GOOD_HIGH -> ContextCompat.getColor(mContext, R.color.colorPurpleDeepKoamaru)
                SEVERITY_GOOD_NEAR_ENEMIES -> ContextCompat.getColor(mContext, randomColor)
                SEVERITY_GOOD_NEAR_ALLIES -> ContextCompat.getColor(mContext, randomColor)
                SEVERITY_BAD_LOW -> ContextCompat.getColor(mContext, randomColor)
                SEVERITY_BAD_MID -> ContextCompat.getColor(mContext, R.color.colorBlackEerie)
                SEVERITY_BAD_YIKES -> ContextCompat.getColor(mContext, R.color.colorRedCardinal)
                else -> ContextCompat.getColor(mContext, randomColor)
            }

            Log.d(TAG, "init - tint = $tint")
            Log.d(TAG, "init - R.color.colorBlackEerie = ${R.color.colorBlackEerie}")
            val fabIcon = when (tint) {
                ContextCompat.getColor(mContext, R.color.colorGreenEmerald) -> R.drawable.ic_feather_info_white
                ContextCompat.getColor(mContext, R.color.colorBlueCadet) -> R.drawable.ic_feather_info_white
                ContextCompat.getColor(mContext, R.color.colorGreenCadmium) -> R.drawable.ic_feather_info_white
                ContextCompat.getColor(mContext, R.color.colorOrangeTerraCotta) -> R.drawable.ic_feather_info_black
                ContextCompat.getColor(mContext, R.color.colorBlueOxford) -> R.drawable.ic_feather_info_white
                ContextCompat.getColor(mContext, R.color.colorPurpleDeepKoamaru) -> R.drawable.ic_feather_info_white
                ContextCompat.getColor(mContext, R.color.colorBlackEerie) -> R.drawable.ic_feather_info_white
                ContextCompat.getColor(mContext, R.color.colorRedCardinal) -> R.drawable.ic_feather_info_white
                else -> R.drawable.ic_feather_info_black
            }

            ImageViewCompat.setImageTintList(iCardImage, ColorStateList.valueOf(tint))

            iCardImage?.alpha = when (spellEffect?.mHowBadIsIt) {
                SEVERITY_NEUTRAL -> randomAlpha
                SEVERITY_GOOD_LOW -> randomAlpha
                SEVERITY_GOOD_MID -> COLOR_ALPHA_BLUE_OXFORD
                SEVERITY_GOOD_HIGH -> COLOR_ALPHA_PURPLE_DEEP_KOAMARU
                SEVERITY_GOOD_NEAR_ENEMIES -> randomAlpha
                SEVERITY_GOOD_NEAR_ALLIES -> randomAlpha
                SEVERITY_BAD_LOW -> randomAlpha
                SEVERITY_BAD_MID -> COLOR_ALPHA_BLACK_EERIE
                SEVERITY_BAD_YIKES -> COLOR_ALPHA_RED_CARDINAL
                else -> randomAlpha
            }

            mFloatingActionButton.backgroundTintList = ColorStateList.valueOf(tint)
            mFloatingActionButton.setImageResource(fabIcon)

            mSpellEffectAdditionalInfoBackground.background = when (tint) {
                ContextCompat.getColor(mContext, R.color.colorGreenEmerald) -> ContextCompat.getDrawable(mContext, R.drawable.info_background_green_emerald)
                ContextCompat.getColor(mContext, R.color.colorBlueCadet) -> ContextCompat.getDrawable(mContext, R.drawable.info_background_blue_cadet)
                ContextCompat.getColor(mContext, R.color.colorGreenCadmium) -> ContextCompat.getDrawable(mContext, R.drawable.info_background_green_cadmium)
                ContextCompat.getColor(mContext, R.color.colorOrangeTerraCotta) -> ContextCompat.getDrawable(mContext, R.drawable.info_background_orange_terra_cotta)
                ContextCompat.getColor(mContext, R.color.colorBlueOxford) -> ContextCompat.getDrawable(mContext, R.drawable.info_background_blue_oxford)
                ContextCompat.getColor(mContext, R.color.colorPurpleDeepKoamaru) -> ContextCompat.getDrawable(mContext, R.drawable.info_background_purple_deep_koamaru)
                ContextCompat.getColor(mContext, R.color.colorBlackEerie) -> ContextCompat.getDrawable(mContext, R.drawable.info_background_black_eerie)
                ContextCompat.getColor(mContext, R.color.colorRedCardinal) -> ContextCompat.getDrawable(mContext, R.drawable.info_background_red_cardinal)
                else -> ContextCompat.getDrawable(mContext, R.drawable.info_background_black_eerie)
            }

            var fontColor = when (tint) {
                ContextCompat.getColor(mContext, R.color.colorBlackEerie) -> ContextCompat.getColor(mContext, R.color.colorWhitePaper)
                ContextCompat.getColor(mContext, R.color.colorPurpleDeepKoamaru) -> ContextCompat.getColor(mContext, R.color.colorWhitePaper)
                ContextCompat.getColor(mContext, R.color.colorBlueOxford) -> ContextCompat.getColor(mContext, R.color.colorWhitePaper)
                ContextCompat.getColor(mContext, R.color.colorRedCardinal) -> ContextCompat.getColor(mContext, R.color.colorWhitePaper)
                ContextCompat.getColor(mContext, R.color.colorBlueCadet) -> ContextCompat.getColor(mContext, R.color.colorWhitePaper)
                ContextCompat.getColor(mContext, R.color.colorGreenEmerald) -> ContextCompat.getColor(mContext, R.color.colorWhitePaper)
                ContextCompat.getColor(mContext, R.color.colorGreenCadmium) -> ContextCompat.getColor(mContext, R.color.colorWhitePaper)
                else -> ContextCompat.getColor(mContext, R.color.colorBlackInk)
            }

            textViewList.forEach {
                it.setTextColor(fontColor)
            }

            mSpellEffectInfoDiceRollSeparator?.setBackgroundColor(fontColor)

            Log.d(TAG, "init")
            initYouTubeView(v)

        }
    }

    private fun initYouTubeView(v: View) {
        Log.d(TAG, "initYouTubeView  [${mPreferencesManager?.getCurrentLifeTime()}]")
        val song = mFullCard?.getSong()
        val vYouTubeVideoView: YouTubePlayerView = v.findViewById((R.id.vYouTubeVideoView))
        val mYoutubeVideoContainer: FrameLayout = v.findViewById((R.id.mYoutubeVideoContainer))
        Log.d(TAG, "initYouTubeView - song = $song")

        if (song != null) {
            val videoId = song?.mUrl ?: ""
            val startSeconds = song?.mStartAt ?: 0
            val startSecondsFloat = startSeconds.toFloat()
            if (mYouTubePlayerView == null) {
                mYouTubePlayerView = vYouTubeVideoView

                lifecycle.addObserver(vYouTubeVideoView)

                val uiController = mYouTubePlayerView?.getPlayerUiController()
                uiController?.showFullscreenButton(false)

                mYouTubePlayerView?.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                    override fun onReady(youTubePlayer: YouTubePlayer) {
                        if (!videoId.isNullOrBlank()) {
                            youTubePlayer.loadVideo(videoId, startSecondsFloat)
                            youTubePlayer.pause()
                        }
                        mYouTubePlayer = youTubePlayer
                    }
                })
            } else {
                mYouTubePlayer?.loadVideo(videoId, startSecondsFloat)
                mYouTubePlayer?.pause()
            }
            mYoutubeVideoContainer.visibility = VISIBLE
        } else {
            mYoutubeVideoContainer.visibility = GONE
        }
    }

    private fun updateDiceRoll(v: View?, selectedSpellDice: String?) {
        Log.d(TAG, "updateDiceRoll  [${mPreferencesManager?.getCurrentLifeTime()}]")
        if (v != null) {
            val tSpellEffectInfoRollMultiplierX: TextView = v.findViewById(R.id.tSpellEffectInfoRollMultiplierX)
            val tSpellEffectInfoRollMultiplierNumber: TextView = v.findViewById(R.id.tSpellEffectInfoRollMultiplierNumber)
            val tSpellEffectInfoRollMultiplierEquals: TextView = v.findViewById(R.id.tSpellEffectInfoRollMultiplierEquals)
            val tSpellInfoRollMultiplierResult: TextView = v.findViewById(R.id.tSpellInfoRollMultiplierResult)
            val tSpellEffectInfoDiceRoll: TextView = v.findViewById(R.id.tSpellEffectInfoDiceRoll)
            val mSpellEffectInfoDiceRollSeparator: View = v.findViewById(R.id.mSpellEffectInfoDiceRollSeparator)
            val mSpellEffectInfoDiceRollsContainer: FrameLayout = v.findViewById(R.id.mSpellEffectInfoDiceRollsContainer)
            val tDiceRollImage1: TextView = v.findViewById(R.id.tDiceRollImage1)
            val tDiceRollImage2: TextView = v.findViewById(R.id.tDiceRollImage2)
            val tDiceRollImage3: TextView = v.findViewById(R.id.tDiceRollImage3)
            val tDiceRollImage4: TextView = v.findViewById(R.id.tDiceRollImage4)
            val tDiceRollImage5: TextView = v.findViewById(R.id.tDiceRollImage5)
            val tDiceRollImage6: TextView = v.findViewById(R.id.tDiceRollImage6)
            val tDiceRollImage7: TextView = v.findViewById(R.id.tDiceRollImage7)
            val tDiceRollImage8: TextView = v.findViewById(R.id.tDiceRollImage8)
            val tDiceRollImage9: TextView = v.findViewById(R.id.tDiceRollImage9)
            val tDiceRollImage10: TextView = v.findViewById(R.id.tDiceRollImage10)
            val tDiceRollImage11: TextView = v.findViewById(R.id.tDiceRollImage11)
            val tDiceRollImage12: TextView = v.findViewById(R.id.tDiceRollImage12)
            val tDiceRollImage13: TextView = v.findViewById(R.id.tDiceRollImage13)
            val tDiceRollImage14: TextView = v.findViewById(R.id.tDiceRollImage14)
            val tDiceRollImage15: TextView = v.findViewById(R.id.tDiceRollImage15)
            val tDiceRollImage16: TextView = v.findViewById(R.id.tDiceRollImage16)
            val tDiceRollImage17: TextView = v.findViewById(R.id.tDiceRollImage17)
            val tDiceRollImage18: TextView = v.findViewById(R.id.tDiceRollImage18)
            val tDiceRollImage19: TextView = v.findViewById(R.id.tDiceRollImage19)
            val tDiceRollImage20: TextView = v.findViewById(R.id.tDiceRollImage20)
            val diceRollImages = listOf<TextView>(
                tDiceRollImage1, tDiceRollImage2, tDiceRollImage3, tDiceRollImage4, tDiceRollImage5,
                tDiceRollImage6, tDiceRollImage7, tDiceRollImage8, tDiceRollImage9, tDiceRollImage10,
                tDiceRollImage11, tDiceRollImage12, tDiceRollImage13, tDiceRollImage14, tDiceRollImage15,
                tDiceRollImage16, tDiceRollImage17, tDiceRollImage18, tDiceRollImage19, tDiceRollImage20
            )

            if (!selectedSpellDice.isNullOrBlank()) {
                diceRollImages.forEach {
                    it.text = ""
                }

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
                                4 -> {
                                    resources.getDrawable(R.drawable.dice_silhouette_d4)
                                }
                                6 -> {
                                    resources.getDrawable(R.drawable.dice_silhouette_d6)
                                }
                                8 -> {
                                    resources.getDrawable(R.drawable.dice_silhouette_d8)
                                }
                                10 -> {
                                    resources.getDrawable(R.drawable.dice_silhouette_d10)
                                }
                                12 -> {
                                    resources.getDrawable(R.drawable.dice_silhouette_d12)
                                }
                                20 -> {
                                    resources.getDrawable(R.drawable.dice_silhouette_d20)
                                }
                                else -> {
                                    resources.getDrawable(R.drawable.dice_silhouette_d6)
                                }
                            }
                        }
                    }
                    if (multiplier != null && multiplier > 0) {
                        roll *= multiplier
                    }
                }
                var hideDiceImageContainer = true
                diceRollImages.forEach {
                    it.visibility = when {
                        it.text.isNullOrBlank() -> GONE
                        else -> {
                            hideDiceImageContainer = false
                            VISIBLE
                        }
                    }
                }

                mSpellEffectInfoDiceRollsContainer.visibility = when (hideDiceImageContainer) {
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
                        tSpellEffectInfoRollMultiplierX.visibility = VISIBLE
                        tSpellEffectInfoRollMultiplierNumber.visibility = VISIBLE

                    }
                    else -> {
                        tSpellEffectInfoRollMultiplierX.visibility = GONE
                        tSpellEffectInfoRollMultiplierNumber.visibility = GONE

                    }
                }

                diceRollFullText = sb.toString()

                tSpellEffectInfoDiceRoll.text = diceRollFullText

                tSpellEffectInfoDiceRoll.visibility = when {
                    diceRollFullText.isNullOrBlank() -> GONE
                    else -> VISIBLE
                }


            } else {
                mSpellEffectInfoDiceRollsContainer.visibility = GONE
                tSpellEffectInfoDiceRoll.visibility = GONE
                mSpellEffectInfoDiceRollSeparator.visibility = GONE
                tSpellEffectInfoRollMultiplierX.visibility = GONE
                tSpellEffectInfoRollMultiplierNumber.visibility = GONE
                tSpellEffectInfoRollMultiplierEquals.visibility = GONE
                tSpellInfoRollMultiplierResult.visibility = GONE
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView  [Pre-inflate - ${mPreferencesManager?.getCurrentLifeTime()}]")
        val v = inflater.inflate(R.layout.fragment_main_card, container, false)
        Log.d(TAG, "onCreateView  [Post-inflate ${mPreferencesManager?.getCurrentLifeTime()}]")
        init(v)
        return v
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        Toast.makeText(c, "Is it me, or a button has just been pressed?", Toast.LENGTH_SHORT).show()
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "onAttach  [${mPreferencesManager?.getCurrentLifeTime()}]")
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    override fun onDestroy() {
        super.onDestroy()
        mYouTubePlayerView?.release()
    }

    fun setCallback(callback: MainActivity) {
        mCallback = callback

    }

    fun setSystem(system: Int?) {
        mSystem = system
    }

    fun setDamagePrefs(damagePrefs: List<Int>?) {
        if (damagePrefs != null) {
            mDamagePreferences = damagePrefs
        }
    }

    private fun extraInfoButtonClicked() {
        val mSpellEffectAdditionalInfoContainer: LinearLayout? =
            mView?.findViewById(R.id.mSpellEffectAdditionalInfoContainer)
        val mFloatingActionButton: FloatingActionButton? =
            mView?.findViewById((R.id.mFloatingActionButtonInfo))


        val displayMetrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        val screenHeight = displayMetrics.heightPixels
        val screenWidth = displayMetrics.widthPixels
        val midPointX = screenWidth * 0.5f
        val midPointY = screenHeight * 0.5f
        val fabStartingX = mFloatingActionButton?.x ?: 0f
        val fabStartingY = mFloatingActionButton?.y ?: 0f
        val fabWidth = mFloatingActionButton?.measuredWidth ?: 0
        val fabHeight = mFloatingActionButton?.measuredHeight ?: 0
        val translationStartX = 0f
        val translationStartY = 0f
        val fabLocationIntArray = IntArray(2)
        mFloatingActionButton?.getLocationOnScreen(fabLocationIntArray)
        val paddingX = screenWidth - fabLocationIntArray.first()
        val paddingY = screenHeight - fabLocationIntArray.last()
        val translationEndX = -(midPointX - paddingX + (fabWidth * 0.5)).toFloat()
        val translationEndY = -(midPointY - paddingY - (fabHeight * 0.25)).toFloat()

        val fabAnimDuration = 100L

        val transAnimationX = ObjectAnimator.ofFloat(
            mFloatingActionButton,
            "translationX",
            translationStartX,
            translationEndX
        )
        transAnimationX.duration = fabAnimDuration
        transAnimationX.interpolator = AccelerateInterpolator()
        transAnimationX.addListener(object : Animator.AnimatorListener {
            override fun onAnimationEnd(p0: Animator?) {
                mFloatingActionButton?.hide()
                mSpellEffectAdditionalInfoContainer?.visibility = View.VISIBLE
                mSpellEffectAdditionalInfoContainer?.isEnabled = true
                val measuredW = mSpellEffectAdditionalInfoContainer?.measuredWidth ?: 0
                val measuredH = mSpellEffectAdditionalInfoContainer?.measuredHeight ?: 0
                val infoContainerWidth = (measuredW * 0.5).roundToInt()
                val infoContainerHeight = (measuredH * 0.5).roundToInt()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && mSpellEffectAdditionalInfoContainer != null) {
                    val anim = ViewAnimationUtils.createCircularReveal(
                        mSpellEffectAdditionalInfoContainer,
                        infoContainerWidth,
                        infoContainerHeight,
                        0f,
                        Math.hypot(
                            mSpellEffectAdditionalInfoContainer.getWidth().toDouble(),
                            mSpellEffectAdditionalInfoContainer.getHeight().toDouble()
                        ).toFloat()
                    )
                    anim.duration = 300
                    anim.interpolator = AccelerateDecelerateInterpolator()
                    anim.addListener(object : Animator.AnimatorListener {
                        override fun onAnimationEnd(p0: Animator?) {
                            mSpellEffectAdditionalInfoContainer?.setOnClickListener {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    val anim = ViewAnimationUtils.createCircularReveal(
                                        mSpellEffectAdditionalInfoContainer,
                                        infoContainerWidth,
                                        infoContainerHeight,
                                        1000f,
                                        0f
                                    )
                                    anim.interpolator = AccelerateDecelerateInterpolator()
                                    anim.addListener(object : Animator.AnimatorListener {
                                        override fun onAnimationEnd(p0: Animator?) {
                                            mFloatingActionButton?.show()
                                            mSpellEffectAdditionalInfoContainer?.visibility =
                                                View.INVISIBLE
                                            mSpellEffectAdditionalInfoContainer?.isEnabled = false

                                            val transAnimationX = ObjectAnimator.ofFloat(
                                                mFloatingActionButton,
                                                "translationX",
                                                translationEndX,
                                                translationStartX
                                            )
                                            transAnimationX.duration = fabAnimDuration
                                            transAnimationX.interpolator = AccelerateInterpolator()
                                            transAnimationX.start()
                                            val transAnimationY = ObjectAnimator.ofFloat(
                                                mFloatingActionButton,
                                                "translationY",
                                                translationEndY,
                                                translationStartY
                                            )
                                            transAnimationY.duration = fabAnimDuration
                                            transAnimationY.interpolator = AccelerateInterpolator()
                                            transAnimationY.start()

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
                } else {
                    //TODO Show the extra info without animation
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
        transAnimationX.start()
        val transAnimationY = ObjectAnimator.ofFloat(
            mFloatingActionButton,
            "translationY",
            translationStartY,
            translationEndY
        )
        transAnimationY.duration = fabAnimDuration
        transAnimationY.interpolator = AccelerateInterpolator()
        transAnimationY.start()
        //val transAnimationY = ObjectAnimator.ofFloat(mFloatingActionButton, "translationY", fabStartingY, midPointY)
        //transAnimationY.duration = fabAnimDuration
        //transAnimationY.start()

    }

    /*fun getFAB(): FloatingActionButton? {
        Log.d(TAG, "getFAB")
        return mCallback?.findViewById(R.id.mFloatingActionButtonInfo)
    }*/


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val TEXT1 = "text1"
        private val TEXT2 = "text2"

        fun newInstance(c: Context, card: FullCard): MainCardFragment {
            val fragment = MainCardFragment(c)
            val args = Bundle()
            //args.putInt(EXTRA_SPELL_EFFECT_ID_INT, id)
            args.putSerializable(EXTRA_FULL_CARD_SERIALIZABLE, card)
            fragment.arguments = args
            return fragment
        }
    }
}


class MainCardPageViewAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private val TAG = "MainCardPageViewAdapter"
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