package com.ryansteiner.randomspelleffect.data

const val SHARED_PREFERENCES_NAME = "SHARED_PREFERENCES_NAME"

const val DB_NAME = "spelleffectsdatabase.db"
const val DB_SPELLEFFECT_TABLE_NAME = "SpellEffects"
const val DB_CREATURE_TABLE_NAME = "Creatures"
const val TABLE_COL_ID = "id"
const val TABLE_COL_NAME = "name"
const val TABLE_COL_DESCRIPTION = "description"
const val TABLE_COL_TYPE = "type"
const val TABLE_COL_TARGET = "target"
const val TABLE_COL_HASGAMEPLAYIMPACT = "hasGameplayImpact"
const val TABLE_COL_TAGS = "tags"
const val TABLE_COL_HOWBADISIT = "howBadIsIt"
const val TABLE_COL_USESIMAGE = "usesImage"
const val TABLE_COL_DND5E_PAGE = "pageDND5E"
const val TABLE_COL_SWADE_PAGE = "pageSWADE"

const val RPG_SYSTEM_ID = "RPG_SYSTEM_ID"
const val RPG_DAMAGE_PREFERENCES = "RPG_DAMAGE_PREFERENCES"
const val RPG_SYSTEM_D20 = 10001
const val RPG_SYSTEM_SAVAGEWORLDS = 10002

const val DEFAULT_DAMAGE_PREFERENCES = "1,2,3"
const val DAMAGE_INT_LOW = 1
const val DAMAGE_INT_MED = 2
const val DAMAGE_INT_HIGH = 3
const val DAMAGE_STRING_LOW = "low"
const val DAMAGE_STRING_MED = "med"
const val DAMAGE_STRING_HIGH = "high"

const val SEVERITY_NEUTRAL = 0
const val SEVERITY_GOOD_HIGH = 1
const val SEVERITY_GOOD_MID = 2
const val SEVERITY_GOOD_LOW = 3
const val SEVERITY_GOOD_NEAR_ENEMIES = 4
const val SEVERITY_GOOD_NEAR_ALLIES = 5
const val SEVERITY_BAD_LOW = 6
const val SEVERITY_BAD_MID = 7
const val SEVERITY_BAD_YIKES = 8

const val SPELL_EFFECTS_GAMEPLAY = "SPELL_EFFECTS_GAMEPLAY"
const val SPELL_EFFECTS_ROLEPLAY = "SPELL_EFFECTS_ROLEPLAY"