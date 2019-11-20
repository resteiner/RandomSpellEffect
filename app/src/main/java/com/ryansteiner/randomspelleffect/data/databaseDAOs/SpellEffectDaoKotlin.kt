package com.ryansteiner.randomspelleffect.data.databaseDAOs

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ryansteiner.randomspelleffect.data.databaseEntities.SpellEffectEntity

@Dao
interface SpellEffectDaoKotlin
{
    @Insert
    fun saveSpellEffects(spell: SpellEffectEntity)

    @Query(value = "Select * from SpellEffectEntity")
    fun getAllSpellEffects() : List<SpellEffectEntity>
}