package com.ryansteiner.randomspelleffect.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.ryansteiner.randomspelleffect.data.databaseDAOs.SpellEffectDao;
import com.ryansteiner.randomspelleffect.data.databaseEntities.SpellEffectEntity;

import static com.ryansteiner.randomspelleffect.data.ConstantsKt.DB_NAME;

@Database(entities = { SpellEffectEntity.class }, version = 1)
public abstract class SpellEffectDatabase extends RoomDatabase {

    public abstract SpellEffectDao getSpellEffectDao();





    private static SpellEffectDatabase spellDB;

    public static SpellEffectDatabase getInstance(Context context) {
        if (null == spellDB) {
            spellDB = buildDatabaseInstance(context);
        }
        return spellDB;
    }

    private static SpellEffectDatabase buildDatabaseInstance(Context context) {
        return Room.databaseBuilder(context,
                SpellEffectDatabase.class,
                DB_NAME)
                .allowMainThreadQueries().build(); //TODO this should not be done on a live app
    }

    public void cleanUp(){
        spellDB = null;
    }

}