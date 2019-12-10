package com.ryansteiner.randomspelleffect.views.activities

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.ryansteiner.randomspelleffect.R
import com.ryansteiner.randomspelleffect.data.*
import com.ryansteiner.randomspelleffect.data.models.FullCard
import android.widget.Toast
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.ryansteiner.randomspelleffect.utils.MyMathUtils
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

    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mFullCard = arguments!!.getSerializable(EXTRA_FULL_CARD_SERIALIZABLE) as? FullCard
            mFullText = mFullCard?.getMainText() ?: "ERROR EXTRACTING ARGS IN onCreate"
        }
    }

    private fun init(v: View) {
        mView = v
        val tMainCardText: TextView = v.findViewById(R.id.tMainCardText)
        val mFloatingActionButton: FloatingActionButton =
            v.findViewById((R.id.mFloatingActionButtonInfo))
        val tSpellEffectInfoTitle: TextView = v.findViewById((R.id.tSpellEffectInfoTitle))
        val tSpellEffectInfoDescription: TextView =
            v.findViewById((R.id.tSpellEffectInfoDescription))
        val tSpellEffectInfoPageInfo: TextView = v.findViewById((R.id.tSpellEffectInfoPageInfo))
        val mNetLibramLabel: LinearLayout = v.findViewById((R.id.mNetLibramLabel))
        val mYouTubeVideoReloadButton: LinearLayout = v.findViewById((R.id.mYouTubeVideoReloadButton))
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

            when (mSystem) {
                RPG_SYSTEM_D20 -> {
                    val safeSelected = gameplayModifier!!.mDND5EDescriptions!![selectedDamageLevel]
                    val safeMed = gameplayModifier!!.mDND5EDescriptions!![DAMAGE_STRING_MED]
                    selectedModifierWithSeverity = when {
                        !safeSelected.isNullOrBlank() -> safeSelected
                        !safeMed.isNullOrBlank() -> safeMed
                        else -> "ERROR with DND5E/Severity"
                    }
                    selectedModifierPageInfo = gameplayModifier!!.mDND5EPageInfo ?: "ERROR with DND5E/Page Number Info"
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
                    selectedModifierPageInfo = gameplayModifier!!.mSWADEPageInfo ?: "ERROR with SWADE/Page Number Info"
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
            tSpellEffectInfoTitle.text = selectedModifierName
            tSpellEffectInfoDescription.text = selectedModifierWithSeverity
            tSpellEffectInfoPageInfo.text = selectedModifierPageInfo
            updateDiceRoll(v, null)
        } else {
            mFloatingActionButton?.hide()
        }
        mNetLibramLabel.visibility = when {
            spellEffect?.mIsNetLibram == true -> VISIBLE
            else -> GONE
        }

        Log.d(TAG, "init")
        initYouTubeView(v)
    }

    private fun initYouTubeView(v: View) {
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
        val v = inflater.inflate(R.layout.fragment_main_card, container, false)
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


class MainCardPageViewAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    private val TAG = "MainCardPageViewAdapter"
    val fragments: ArrayList<Fragment> = ArrayList()
    val titles: ArrayList<String> = ArrayList()

    override fun getCount(): Int = fragments?.count()

    override fun getItem(position: Int): Fragment = fragments[position]

    fun addFragment(fragment: Fragment, title: String) {
        Log.d(TAG, "addFragment - fragment = $fragment")
        Log.d(TAG, "addFragment - title = $title")
        fragments.add(fragment)
        titles.add(title)
    }
}