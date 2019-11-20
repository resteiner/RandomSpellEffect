package com.ryansteiner.randomspelleffect.data.models


/**
 * Created by Ryan Steiner on 2019/11/07.
 */


class SpellEffect(

    var mId: Int = -1,
    var mDescription: String? = null,
    var mType: Int? = null, // 0 = Fluff only, 1 = Gameplay only, 2 = Fluff+Gameplay
    var mTarget: Int? = null,
    var mHasGameplayImpact: Int? = null,
    var mTags: String? = null,
    var mHowBadIsIt: Int? = null,
    //0 - Neutral
    //1 - Very beneficial
    //2 - Solidly beneficial
    //3 - Mildly beneficial
    //4 - Good Near Enemies, but probably bad near allies
    //5 - Good Near Allies, but probably bad near enemies
    //6 - Slightly bad
    //7 - Solidly bad
    //8 - Catastrophically bad
    var mRequiresCaster: Int? = null,
    var mRequiresSpecificSpell: String? = null,
    var mUsesImage: String? = null,

    val colors: List<String> = listOf(
        "snow white", "pearlescent white", "ivory",
        "ink black", "midnight black", "obsidian black",
        "stone gray",
        "blood red", "strawberry red", "deep burgandy", "crimson red", "glowing ember red", "ruby red",
        "cobalt blue", "turquoise", "sky blue", "periwinkle blue", "deep indigo",
        "emerald green", "moss green", "olive green",
        "pumpkin orange", "sunset orange",
        "canary yellow", "mustard yellow", "butter yellow", "banana yellow",
        "salmon pink", "flamingo pink", "neon pink", "carnation pink",
        "lavender", "eggplant purple", "amethyst purple",
        "reflective chrome", "shining silver", "brilliant gold",
        "candy cane stripes", "zebra stripes", "rainbow stripes", "tiger stripes",
        "ladybug patches", "salt and pepper patches",
        "leopard spots",
        "every color of the spectrum swirling together"
        )
)