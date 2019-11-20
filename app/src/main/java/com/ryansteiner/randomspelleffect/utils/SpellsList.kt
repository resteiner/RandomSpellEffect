package com.ryansteiner.randomspelleffect.utils

import android.content.Context
import com.ryansteiner.randomspelleffect.data.*
import com.ryansteiner.randomspelleffect.data.models.Spell

class SpellsList(context: Context) {
    val mContext: Context = context
    var mSpellList: List<Spell>? = null

    init {
        var mutList = mutableListOf<Spell>()

        val fireballDND5EDescs: MutableMap<String, String> = mutableMapOf()
        val fireballDND5EDice: MutableMap<String, String> = mutableMapOf()
        val fireballDND5EDescLow = "Use Fireball as a 3rd-level spell with its radius and damage reduced by 1/2."
        val fireballDND5EDiceLow = "8d6*0.5"
        val fireballDND5EDescMed = "Use Fireball as a 3rd-level spell."
        val fireballDND5EDiceMed = "8d6"
        val fireballDND5EDescHigh = "Use Fireball as a 9th-level spell."
        val fireballDND5EDiceHigh = "14d6"
        val fireballDND5EPageNumber = "p. 241 of the Player's Handbook"
        fireballDND5EDescs[DAMAGE_STRING_LOW] = fireballDND5EDescLow
        fireballDND5EDescs[DAMAGE_STRING_MED] = fireballDND5EDescMed
        fireballDND5EDescs[DAMAGE_STRING_HIGH] = fireballDND5EDescHigh
        fireballDND5EDice[DAMAGE_STRING_LOW] = fireballDND5EDiceLow
        fireballDND5EDice[DAMAGE_STRING_MED] = fireballDND5EDiceMed
        fireballDND5EDice[DAMAGE_STRING_HIGH] = fireballDND5EDiceHigh

        val fireballSWADEDescs: MutableMap<String, String> = mutableMapOf()
        val fireballSWADEDice: MutableMap<String, String> = mutableMapOf()
        val fireballSWADEDescLow = "Use the Blast Power with Fire trappings, reduce its area to a Small Blast Template and reduce its damage by 1/2."
        val fireballSWADEDiceLow = "3d6*0.5"
        val fireballSWADEDescMed = "Use the Blast Power with Fire trappings."
        val fireballSWADEDiceMed = "3d6"
        val fireballSWADEDesceHigh = "Use the Blast Power with Fire trappings, include the DAMAGE Blast modifier and the LINGERING DAMAGE Power Modifier (p. 152), increase its area to a Large Blast Template and treat it as though it hit with a raise."
        val fireballSWADEDiceHigh = "4d6,2d6 fire damage on the victim's next turn."
        val fireballSWADEPageNumber = "p. 156 of the Savage Worlds Adventure Edition Core Rule Book"
        fireballSWADEDescs[DAMAGE_STRING_LOW] = fireballSWADEDescLow
        fireballSWADEDescs[DAMAGE_STRING_MED] = fireballSWADEDescMed
        fireballSWADEDescs[DAMAGE_STRING_HIGH] = fireballSWADEDesceHigh
        fireballSWADEDice[DAMAGE_STRING_LOW] = fireballSWADEDiceLow
        fireballSWADEDice[DAMAGE_STRING_MED] = fireballSWADEDiceMed
        fireballSWADEDice[DAMAGE_STRING_HIGH] = fireballSWADEDiceHigh

        val fireball = Spell(
            1,
            "Fireball",
            "A swirling ball of fire",
            fireballDND5EDescs,
            fireballDND5EDice,
            fireballDND5EPageNumber,
            fireballSWADEDescs,
            fireballSWADEDice,
            fireballSWADEPageNumber

        )

        mutList.add(fireball)


        val insectPlagueDND5EDescs: MutableMap<String, String> = mutableMapOf()
        val insectPlagueDND5EDice: MutableMap<String, String> = mutableMapOf()
        val insectPlagueDND5EDescLow = "Use Insect Plague as a 5th-level spell with its radius, duration, and damage reduced by 1/2."
        val insectPlagueDND5EDiceLow = "4d10*0.5"
        val insectPlagueDND5EDescMed = "Use Insect Plague as a 5th-level spell."
        val insectPlagueDND5EDiceMed = "4d10"
        val insectPlagueDND5EDescHigh = "Use Insect Plague as a 9th-level spell."
        val insectPlagueDND5EDiceHigh = "8d10"
        val insectPlagueDND5EPageNumber = "p. 254 of the Player's Handbook"
        insectPlagueDND5EDescs[DAMAGE_STRING_LOW] = insectPlagueDND5EDescLow
        insectPlagueDND5EDescs[DAMAGE_STRING_MED] = insectPlagueDND5EDescMed
        insectPlagueDND5EDescs[DAMAGE_STRING_HIGH] = insectPlagueDND5EDescHigh
        insectPlagueDND5EDice[DAMAGE_STRING_LOW] = insectPlagueDND5EDiceLow
        insectPlagueDND5EDice[DAMAGE_STRING_MED] = insectPlagueDND5EDiceMed
        insectPlagueDND5EDice[DAMAGE_STRING_HIGH] = insectPlagueDND5EDiceHigh

        val insectPlagueSWADEDescs: MutableMap<String, String> = mutableMapOf()
        val insectPlagueSWADEDice: MutableMap<String, String> = mutableMapOf()
        val insectPlagueSWADEDescLow = "Use the Blast Power with Fire trappings, reduce its area to a Small Blast Template and reduce its damage by 1/2."
        val insectPlagueSWADEDiceLow = "3d6*0.5"
        val insectPlagueSWADEDescMed = "Use the Blast Power with Fire trappings."
        val insectPlagueSWADEDiceMed = "3d6"
        val insectPlagueSWADEDesceHigh = "Use the Blast Power with Fire trappings, include the DAMAGE Blast modifier and the LINGERING DAMAGE Power Modifier (p. 152), increase its area to a Large Blast Template and treat it as though it hit with a raise."
        val insectPlagueSWADEDiceHigh = "4d6,2d6 fire damage on the victim's next turn."
        val insectPlagueSWADEPageNumber = "p. 156 of the Savage Worlds Adventure Edition Core Rule Book"
        insectPlagueSWADEDescs[DAMAGE_STRING_LOW] = insectPlagueSWADEDescLow
        insectPlagueSWADEDescs[DAMAGE_STRING_MED] = insectPlagueSWADEDescMed
        insectPlagueSWADEDescs[DAMAGE_STRING_HIGH] = insectPlagueSWADEDesceHigh
        insectPlagueSWADEDice[DAMAGE_STRING_LOW] = insectPlagueSWADEDiceLow
        insectPlagueSWADEDice[DAMAGE_STRING_MED] = insectPlagueSWADEDiceMed
        insectPlagueSWADEDice[DAMAGE_STRING_HIGH] = insectPlagueSWADEDiceHigh

        val insectPlague = Spell(
            1,
            "Insect Plague",
            "A swirling ball of fire",
            insectPlagueDND5EDescs,
            insectPlagueDND5EDice,
            insectPlagueDND5EPageNumber,
            insectPlagueSWADEDescs,
            insectPlagueSWADEDice,
            insectPlagueSWADEPageNumber

        )

        mutList.add(insectPlague)

        mSpellList = mutList.toList()
    }

    fun getRandomSpell(): Spell? {
        return if (mSpellList != null && mSpellList!!.count() > 0) {
            mSpellList!!.random()
        } else {
            null
        }
    }

    fun getRandomSpellOfSpecificPowerLevel(systemId: Int, powerLevel: String) : Pair<Spell?, String?>? {
        val spell = getRandomSpell()
        return when (systemId) {
            RPG_SYSTEM_D20 -> {
                Pair(spell, spell!!.mDND5EDescriptions!![powerLevel])
            }
            RPG_SYSTEM_SAVAGEWORLDS -> {
                Pair(spell, spell!!.mSWADEDescriptions!![powerLevel])
            }
            else -> {
                null
            }
        }

    }

}
