package com.ryansteiner.randomspelleffect.data.databaseDAOs;


import androidx.room.*;

import com.ryansteiner.randomspelleffect.data.databaseEntities.SpellEffectEntity;

import java.util.List;

import static com.ryansteiner.randomspelleffect.data.ConstantsKt.*;

@Dao
public interface SpellEffectDao {
    //@Query("SELECT * FROM user "+ "1")
    //List<SpellEffectEntity> getAll();


    /*
     * Insert the object in database
     * @param note, object to be inserted
     */
    @Insert
    void insert(SpellEffectEntity spellEffectEntity);

    /*
     * update the object in database
     * @param note, object to be updated
     */
    @Update
    void update(SpellEffectEntity repos);

    /*
     * delete the object from database
     * @param note, object to be deleted
     */
    @Delete
    void delete(SpellEffectEntity spellEffectEntity);

    /*
     * delete list of objects from database
     * @param note, array of objects to be deleted
     */
    @Delete
    void delete(SpellEffectEntity... spellEffectEntity);      // Note... is varargs, here note is an array

}