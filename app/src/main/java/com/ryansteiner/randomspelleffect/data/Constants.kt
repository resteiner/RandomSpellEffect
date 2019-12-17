package com.ryansteiner.randomspelleffect.data

const val SHARED_PREFERENCES_NAME = "SHARED_PREFERENCES_NAME"

const val DB_NAME = "spelleffectsdatabase.db"
const val DB_SPELLEFFECT_TABLE_NAME = "SpellEffects"
const val DB_SPELLS_TABLE_NAME = "Spells"
const val DB_CREATURE_TABLE_NAME = "Creatures"
const val DB_GAMEPLAY_MODIFIERS_TABLE_NAME = "GameplayModifiers"
const val DB_SONGS_TABLE_NAME = "Songs"
const val TABLE_COL_ID = "id"
const val TABLE_COL_NAME = "name"
const val TABLE_COL_GENERIC_NAME = "generic_name"
const val TABLE_COL_DND5E_NAME = "dnd5e_name"
const val TABLE_COL_SWADE_NAME = "swade_name"
const val TABLE_COL_NAME_WITH_A_AN = "name_with_a_an"
const val TABLE_COL_NAME_PLURAL = "namePlural"
const val TABLE_COL_DESCRIPTION = "description"
const val TABLE_COL_GENERIC_DESCRIPTION = "generic_description"
const val TABLE_COL_GENERIC_DESCRIPTION_LOW = "generic_description_low"
const val TABLE_COL_GENERIC_DESCRIPTION_MED = "generic_description_med"
const val TABLE_COL_GENERIC_DESCRIPTION_HIGH = "generic_description_high"
const val TABLE_COL_TYPE = "type"
const val TABLE_COL_TARGET = "target"
const val TABLE_COL_HASGAMEPLAYIMPACT = "hasGameplayImpact"
const val TABLE_COL_TAGS = "tags"
const val TABLE_COL_HOWBADISIT = "howBadIsIt"
const val TABLE_COL_USESIMAGE = "usesImage"
const val TABLE_COL_ISNETLIBRAM = "isNetLibram"
const val TABLE_COL_DND5E_DESC_LOW = "dnd5e_desc_low"
const val TABLE_COL_DND5E_DESC_MEDIUM = "dnd5e_desc_medium"
const val TABLE_COL_DND5E_DESC_HIGH = "dnd5e_desc_high"
const val TABLE_COL_SWADE_DESC_LOW = "swade_desc_low"
const val TABLE_COL_SWADE_DESC_MEDIUM = "swade_desc_medium"
const val TABLE_COL_SWADE_DESC_HIGH = "swade_desc_high"
const val TABLE_COL_DND5E_DICE_LOW = "dnd5e_dice_low"
const val TABLE_COL_DND5E_DICE_MEDIUM = "dnd5e_dice_med"
const val TABLE_COL_DND5E_DICE_HIGH = "dnd5e_dice_high"
const val TABLE_COL_SWADE_DICE_LOW = "swade_dice_low"
const val TABLE_COL_SWADE_DICE_MEDIUM = "swade_dice_med"
const val TABLE_COL_SWADE_DICE_HIGH = "swade_dice_high"
const val TABLE_COL_DND5E_PAGE = "dnd5e_page_info"
const val TABLE_COL_SWADE_PAGE = "swade_page_info"
const val TABLE_COL_ARTIST = "artist"
const val TABLE_COL_URL = "url"
const val TABLE_COL_START_AT = "startAt"

const val RPG_SYSTEM_ID = "RPG_SYSTEM_ID"
const val RPG_DAMAGE_PREFERENCES = "RPG_DAMAGE_PREFERENCES"
const val RPG_SYSTEM_D20 = 10001
const val RPG_SYSTEM_SAVAGEWORLDS = 10002
const val PREVIOUSLY_VIEWED_CARDS = "PREVIOUSLY_VIEWED_CARDS"

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

const val TARGET_CASTER = "TARGET_CASTER"
const val TARGET_NEAREST_ALLY = "TARGET_NEAREST_ALLY"
const val TARGET_NEAREST_ENEMY = "TARGET_NEAREST_ENEMY"
const val TARGET_NEAREST_CREATURE = "TARGET_NEAREST_CREATURE"

const val APP_LIFECYCLE_START_TIME = "APP_LIFECYCLE_START_TIME"

const val EXTRA_FULL_CARD_SERIALIZABLE = "EXTRA_FULL_CARD_SERIALIZABLE"

const val COLOR_ALPHA_RED_CARDINAL = 0.15f
const val COLOR_ALPHA_GREEN_EMERALD = 0.15f
const val COLOR_ALPHA_GREEN_CADMIUM = 0.15f
const val COLOR_ALPHA_BLUE_CADET = 0.25f
const val COLOR_ALPHA_BLUE_OXFORD = 0.1f

const val COLOR_ALPHA_ORANGE_TERRA_COTTA = 0.25f
const val COLOR_ALPHA_PURPLE_DEEP_KOAMARU = 0.15f
const val COLOR_ALPHA_BLACK_EERIE = 0.1f

const val NUMBER_OF_CARDS_TO_LOAD = 5
const val MAX_NUMBER_OF_CARDS_TO_REMEMBER = 15