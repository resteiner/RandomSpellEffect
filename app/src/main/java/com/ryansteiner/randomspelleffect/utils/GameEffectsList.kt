package com.ryansteiner.randomspelleffect.utils

import com.ryansteiner.randomspelleffect.R
import com.ryansteiner.randomspelleffect.data.models.GameEffect

/**
 * Created by Ryan Steiner on 2019/11/06.
 */

class GameEffectsList() {
    var mGameEffectMap: Map<String, GameEffect>? = null

    init {
        var mutMap = mutableMapOf<String, GameEffect>()

        //Difficult Terrain
        val gameEffectDT = GameEffect()
        gameEffectDT.mName = "Difficult Terrain"
        gameEffectDT.mDND5EName = "Difficult Terrain"
        gameEffectDT.mSWADEName = "Difficult Ground"
        gameEffectDT.mDND5EDescription = "Difficult Terrain (p. 182 of the Player's Handbook)"
        gameEffectDT.mSWADEName = "Difficult Ground (p. XXX of the SWADE Core Rule Book)"

        val stringDT = gameEffectDT.mName ?: "ERROR"

        mutMap[stringDT] = gameEffectDT

        //Hiccups
        val gEHiccup = GameEffect(
            "Hiccups",
            "Hiccups",
            "Hiccups",
            "-1 to AC, +1 to all casting times",
            "-1 to Parry, -1 to all Fighting rolls, -1 to all Casting rolls")

        val stringHic = gEHiccup.mName ?: "ERROR"
        mutMap[stringHic] = gameEffectDT

        //Vulnerability

        //Confused

        //Resistant

        //Drunk

        //High

        //Hallucinogenic

        //Hobbled

        mGameEffectMap = mutMap
    }

    fun getEffectByName(name: String): GameEffect? {
        return if (mGameEffectMap != null && mGameEffectMap!!.count() > 0) {
            val effect = mGameEffectMap!![name]
            effect
        } else {
            null
        }
    }

    fun getEffectRandomly(): GameEffect? {
        return if (mGameEffectMap != null && mGameEffectMap!!.count() > 0) {
            val random = (0..(mGameEffectMap!!.size)).random()
            val mapEntry = mGameEffectMap!!.entries.elementAt(random)
            val effect = mapEntry.component2()
            effect
        } else {
            null
        }
    }


    class InanimateObjectsSingular{
        val mAllObjects: MutableList<String> = mutableListOf(
            "a potted plant",
            "a chamber pot",
            "a wooden stool",
            "an ornate owl statue",
            "a metal thimble",
            "a handful of assorted polyhedral game dice"
        )
    }
    class InanimateObjectsPlural{
        val mAllObjects: MutableList<String> = mutableListOf(
            "potted plants",
            "chamber pots",
            "wooden stools",
            "ornate owl statues",
            "metal thimbles",
            "assorted polyhedral game dice"
        )
    }

    class gameColors{
        val mAllColors: List<String> = listOf(
            "snow white", "pearlescent white", "ivory",
            "ink black", "midnight black", "obsidian black",
            "stone gray",
            "blood red", "strawberry red", "deep burgundy", "crimson red", "glowing ember red", "ruby red",
            "cobalt blue", "turquoise", "sky blue", "periwinkle blue", "deep indigo",
            "emerald green", "moss green", "olive green",
            "pumpkin orange", "sunset orange",
            "canary yellow", "mustard yellow", "butter yellow", "banana yellow",
            "salmon pink", "flamingo pink", "neon pink", "carnation pink",
            "lavender", "eggplant purple", "amethyst purple"
        )
        val mMapOfColors: Map<String, Int> = mapOf(
            Pair("snow white", R.color.colorWhiteSnow), Pair("pearlescent white", R.color.colorWhitePearlescent), Pair("ivory", R.color.colorWhiteIvory),
            Pair("ink black", R.color.colorBlackInk), Pair("midnight black", R.color.colorBlackMidnight), Pair("obsidian black", R.color.colorBlackObsidian),
            Pair("stone gray", R.color.colorGrayStone),
            Pair("blood red", R.color.colorRedBlood), Pair("strawberry red", R.color.colorRedStrawberry), Pair("deep burgundy", R.color.colorRedBurgundy), Pair("crimson red", R.color.colorRedCrimson), Pair("glowing ember red", R.color.colorRed), Pair("ruby red", R.color.colorRedRuby),
            Pair("cobalt blue", R.color.colorBlueCobalt), Pair("turquoise", R.color.colorBlueTurqoise), Pair("sky blue", R.color.colorBlueSky), Pair("periwinkle blue", R.color.colorBluePeriwinkle), Pair("deep indigo", R.color.colorBlueDeepIndigo),
            Pair("emerald green", R.color.colorGreenEmerald), Pair("moss green", R.color.colorGreenMoss), Pair("olive green", R.color.colorGreenOlive),
            Pair("pumpkin orange", R.color.colorOrangePumpkin), Pair("sunset orange", R.color.colorOrangeSunset),
            Pair("canary yellow", R.color.colorYellowCanary), Pair("mustard yellow", R.color.colorYellowMustard), Pair("butter yellow", R.color.colorYellowButter), Pair("banana yellow", R.color.colorYellowBanana),
            Pair("salmon pink", R.color.colorPinkSalmon), Pair("flamingo pink", R.color.colorPinkFlamingo), Pair("neon pink", R.color.colorPinkNeon), Pair("carnation pink", R.color.colorPinkCarnation),
            Pair("lavender", R.color.colorPurpleLavender), Pair("eggplant purple", R.color.colorPurpleEggplant), Pair("amethyst purple", R.color.colorPurpleAmethyst)
        )

        //Note: Any images not created by myself were found at publicdomainpictures.net and are available under Creative Commons CC0 Public Domain license (https://creativecommons.org/publicdomain/zero/1.0/) unless otherwise specified.

        val mMapOfPatterns: Map<String, Int> = mapOf(
            Pair("reflective chrome", R.drawable.pattern_tile_chrome),
            Pair("shining silver", R.drawable.pattern_tile_silver),
            Pair("brilliant gold", R.drawable.pattern_tile_gold),
            Pair("candy cane stripes", R.drawable.pattern_tile_candycane),
            Pair("zebra stripes", R.drawable.pattern_tile_zebra), //https://www.publicdomainpictures.net/en/view-image.php?image=129300&picture=seamless-zebra-pattern
            Pair("rainbow stripes", R.drawable.pattern_tile_rainbow), //https://opengameart.org/content/seamless-rainbow-colors - Created by n4 and released with CC0 Public Domain
            Pair("tiger stripes", R.drawable.pattern_tile_tiger), //https://www.publicdomainpictures.net/en/view-image.php?image=80625&picture=tiger-pattern-seamless
            Pair("salt and pepper patches", R.drawable.pattern_tile_saltandpepper),
            Pair("ladybug spots", R.drawable.pattern_tile_ladybug), //https://pixabay.com/illustrations/seamless-dot-polka-dot-circle-2482976/ - Pixabay License, modified by me.
            Pair("leopard spots", R.drawable.pattern_tile_leopard),
            Pair("every color of the spectrum swirling together", R.drawable.pattern_tile_spectrum_swirl) //https://publicdomainvectors.org/en/free-clipart/Wavy-background-in-rainbow-colors/78153.html - CC0

        )
    }

    class objectMaterials{
        val mAllMaterials: List<String> = listOf(
            "granite", "marble", "limestone", "sandstone", "basalt", "onyx", "alabaster",
            "brick", "concrete", "terracotta", "ceramic", "clay",
            "gold", "silver", "platinum", "copper", "tin", "bronze", "aluminum", "iron", "brass", "steel",
            "oak", "mahogany", "pine wood", "cheap plywood", "cherry wood",
            "glass",
            "plastic",
            "strawberry jelly", "tapioca pudding", "orange marmalade",
            "bone"
        )
    }

}
