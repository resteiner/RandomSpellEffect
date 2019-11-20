package com.ryansteiner.randomspelleffect.views.activities

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ryansteiner.randomspelleffect.R
import com.ryansteiner.randomspelleffect.contracts.MainContract
import com.ryansteiner.randomspelleffect.data.*
import com.ryansteiner.randomspelleffect.data.models.SpellEffect
import com.ryansteiner.randomspelleffect.presenters.MainPresenter
import com.ryansteiner.randomspelleffect.utils.*
import jp.wasabeef.glide.transformations.CropTransformation
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.roundToInt
import android.view.View.MeasureSpec



/**
 * Created by Ryan Steiner on 2019/11/06.
 */

class MainActivity : BaseActivity(), MainContract.View {

    private val TAG = "MainActivity"

    private var mPresenter: MainPresenter? = null
    private var mDatabase: SQLiteDatabase? = null
    private var mPreferencesManager: PreferencesManager? = null
    private val mSpellsList = SpellsList(this)
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


            //mPreferencesManager = PreferencesManager(this)
        mPresenter?.getPreferences()

        mPresenter?.updateSpellList(mSpellsList)

        mPresenter?.initializeView()
        mPresenter?.loadDatabase(this)

    }

    override fun onLoadedDatabase(database: SQLiteDatabase) {
        mDatabase = database
        mPresenter?.generateSingleSpellEffect()
    }

    override fun onInitializedView() {
        mMainActivityText?.text = "View Initialized"
        Glide.with(this)
            .load(R.drawable.card_image_placeholder)
            .placeholder(R.drawable.card_image_placeholder)
            .error(R.drawable.card_image_placeholder)
            .centerCrop()
            .into(iCardCenterImage)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        mGenerateNewSpellEffectButton?.setOnClickListener {
            mPresenter?.generateSingleSpellEffect()
        }
    }

    override fun onGeneratedSingleSpellEffect(spellEffect: SpellEffect) {
        Log.d(TAG, "onGeneratedSingleSpellEffect - spellEffect.mDescription = ${spellEffect.mDescription}")

        vColorLayer.visibility = GONE
        vPatternLayer.visibility = GONE

        var spellEffectText: String? = ""
        spellEffectText = when {
            spellEffect.mId == null -> "spellEffect was null"
            spellEffect.mId < 0 -> "spellEffect was below 0"
            else -> spellEffect.mDescription
        }



        //Find an image to use with iCardCenterImage
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


        Log.d(TAG, "onGeneratedSingleSpellEffect - spellEffect.mUsesImage = ${spellEffect.mUsesImage}")
        iCardCenterImage.visibility = when {
            spellEffect.mUsesImage.isNullOrBlank() -> GONE
            else -> VISIBLE
        }

        val system = mPreferencesManager?.getSystem() ?: -1
        val damagePrefs = mPreferencesManager?.getDamagePreferences()

        mPresenter?.updateDamagePreferences(damagePrefs)


        spellEffectText = mPresenter?.parseSpellStringForVariables(spellEffectText, system)
        Log.d(TAG, "onGeneratedSingleSpellEffect - spellEffectText 1 = ${spellEffectText}")




        val borderContainers = listOf<FrameLayout>(fBorderLeft, fBorderRight, fBorderTop, fBorderBottom)

        borderContainers.forEach{
            val resourceInt = mBorderColors[spellEffect.mHowBadIsIt] ?: -1
            val resourceColor = ContextCompat.getColor(this, resourceInt)
            it.setBackgroundColor(resourceColor)


        }
        spellEffectText = spellEffectText?.capitalize()
        Log.d(TAG, "onGeneratedSingleSpellEffect - spellEffectText 2 = ${spellEffectText}")

        val finalSpellEffectText = spellEffectText ?: "Something went very wrong."

        mMainActivityText?.text = finalSpellEffectText
    }

    override fun updateColorLayer(colorId: Int?, visibility: Boolean) {
        if (colorId != null) {
            vColorLayer.setBackgroundColor(colorId!!)
        }

        vColorLayer.visibility = when (visibility) {
            true -> VISIBLE
            else -> GONE
        }
    }

    override fun updatePatternLayer(patternId: Int?, visibility: Boolean) {
        if (patternId != null) {
            vPatternLayer.background = ContextCompat.getDrawable(this, patternId)
        }

        vPatternLayer.visibility = when (visibility) {
            true -> VISIBLE
            else -> GONE
        }
    }

    override fun updateDiceRoll(selectedSpellDice: String?) {
        val diceRollImages = listOf<TextView>(
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

        }

    }

    override fun updateSpellInfoContainer(visibility: Boolean, spellText: String?, selectedSpellDescriptionWithDamageLevel: String?, selectedSpellPageNumber: String?) {
        tSpellTitle.text = spellText
        tSpellDescription.text = selectedSpellDescriptionWithDamageLevel
        val spellPageFullText = "$spellText can be found on $selectedSpellPageNumber"
        tSpellPage.text = spellPageFullText

        when (visibility) {
            true -> mSpellInfoContainer.visibility = VISIBLE
            else -> mSpellInfoContainer.visibility = GONE
        }
    }

    override fun test() {
        mMainActivityText?.text = "spellEffect was null"
    }

    override fun updateDebugText(systemText: String?){
        tDebugSystemText.text = systemText
    }

    override fun updatePreferences(prefs: PreferencesManager?) {
        mPreferencesManager = prefs
    }




}

