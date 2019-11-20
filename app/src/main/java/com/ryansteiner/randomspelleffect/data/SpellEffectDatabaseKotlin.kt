package com.ryansteiner.randomspelleffect.data

import androidx.room.Database
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ryansteiner.randomspelleffect.data.databaseDAOs.SpellEffectDao
import com.ryansteiner.randomspelleffect.data.databaseEntities.SpellEffectEntity

@Database(entities = [(SpellEffectEntity::class)], version = 1)
abstract class AppDatabase : SpellEffectDatabase() {
    abstract fun getDao(): SpellEffectDao

    companion object {
        @JvmField
        val MIGRATION_1_2 : Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
            }
        }

    }
}