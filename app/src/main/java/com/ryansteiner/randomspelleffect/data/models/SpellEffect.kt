package com.ryansteiner.randomspelleffect.data.models

import android.graphics.drawable.Drawable


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
    var mIsNetLibram: Boolean? = null,
    var mBackgroundImageId: Int? = null,
    var mRequiresSpellType: String? = null


)