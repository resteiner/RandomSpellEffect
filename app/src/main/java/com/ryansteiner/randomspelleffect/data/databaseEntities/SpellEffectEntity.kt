package com.ryansteiner.randomspelleffect.data.databaseEntities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class SpellEffectEntity {

    @PrimaryKey
    var id: Int = 0

    @ColumnInfo (name = "name")
    var name:  String? = null

    @ColumnInfo (name = "description")
    var description:  String? = null

    @ColumnInfo (name = "type")
    var type:  Int? = null

    @ColumnInfo (name = "target")
    var target:  Int? = null

}