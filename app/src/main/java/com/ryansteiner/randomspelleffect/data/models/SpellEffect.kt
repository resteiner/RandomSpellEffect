package com.ryansteiner.randomspelleffect.data.models

import java.io.Serializable


/**
 * Created by Ryan Steiner on 2019/11/07.
 */


class SpellEffect : Serializable {

    private var mId: Int = -1
    private var mDescription: String? = null
    private var mType: Int? = null // 0 = Fluff only, 1 = Gameplay only, 2 = Fluff+Gameplay
    private var mTarget: Int? = null
    private var mHasGameplayImpact: Int? = null
    private var mTags: String? = null
    private var mHowBadIsIt: Int? = null
    //0 - Neutral
    //1 - Very beneficial
    //2 - Solidly beneficial
    //3 - Mildly beneficial
    //4 - Good Near Enemies, but probably bad near allies
    //5 - Good Near Allies, but probably bad near enemies
    //6 - Slightly bad
    //7 - Solidly bad
    //8 - Catastrophically bad
    private var mRequiresCaster: Int? = null
    private var mRequiresSpecificSpell: String? = null
    private var mUsesImage: String? = null
    private var mIsNetLibram: Boolean? = null
    private var mBackgroundImageId: Int? = null
    private var mRequiresSpellType: String? = null

    fun setAllVariables(id: Int?, desc: String?, type: Int?, target: Int?, hasGameplayImpact: Int?, tags: String?, howBad: Int?,
                        requiresCaster: Int?, requiresSpell: String?, usesImage: String?, isNetLibram: Boolean?, backgroundImage: Int?, requiresSpellType: String?
                        ) {
        if (id != null) {
            setId(id)
        }
        setDescription(desc)
        setType(type)
        setTarget(target)
        setHasGameplayImpact(hasGameplayImpact)
        setTags(tags)
        setHowBadIsIt(howBad)
        setRequiresCaster(requiresCaster)
        setRequiresSpecificSpell(requiresSpell)
        setUsesImage(usesImage)
        setIsNetLibram(isNetLibram)
        setBackgroundImageId(backgroundImage)
        setRequiresSpellType(requiresSpellType)
    }

    fun setId(id: Int?) {
        if (id != null) {
            mId = id
        }
    }

    fun setDescription(desc: String?) {
        mDescription = desc
    }

    fun setType(type: Int?) {
        mType = type
    }

    fun setTarget(target: Int?) {
        mTarget = target
    }

    fun setHasGameplayImpact(hasGameplayImpact: Int?) {
        mHasGameplayImpact = hasGameplayImpact
    }

    fun setTags(tags: String?) {
        mTags = tags
    }

    fun setHowBadIsIt(howBad: Int?) {
        mHowBadIsIt = howBad
    }

    fun setRequiresCaster(requiresCaster: Int?) {
        mRequiresCaster = requiresCaster
    }

    fun setRequiresSpecificSpell(requiresSpell: String?) {
        mRequiresSpecificSpell = requiresSpell
    }

    fun setUsesImage(usesImage: String?) {
        mUsesImage = usesImage
    }

    fun setIsNetLibram(isNetLibram: Boolean?) {
        mIsNetLibram = isNetLibram
    }

    fun setBackgroundImageId(backgroundImage: Int?) {
        mBackgroundImageId = backgroundImage
    }

    fun setRequiresSpellType(requiresSpellType: String?) {
        mRequiresSpellType = requiresSpellType
    }

    fun getId(): Int? {
        return mId
    }

    fun getIdAsString(): String {
        return mId.toString()
    }

    fun getDescription(): String? {
        return mDescription
    }

    fun getType(): Int? {
        return mType
    }

    fun getTarget(): Int? {
        return mTarget
    }

    fun getHasGameplayImpact(): Int? {
        return mHasGameplayImpact
    }

    fun getTags(): String? {
        return mTags
    }

    fun getHowBadIsIt(): Int? {
        return mHowBadIsIt
    }

    fun getRequiresCaster(): Int? {
        return mRequiresCaster
    }

    fun getRequiresSpecificSpell(): String? {
        return mRequiresSpecificSpell
    }

    fun getUsesImage(): String? {
        return mUsesImage
    }

    fun getIsNetLibram(): Boolean? {
        return mIsNetLibram
    }

    fun getBackgroundImageId(): Int? {
        return mBackgroundImageId
    }

    fun getRequiresSpellType(): String? {
        return mRequiresSpellType
    }

}