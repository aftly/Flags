package dev.aftly.flags.data.room

import androidx.room.TypeConverter
import dev.aftly.flags.model.FlagCategory
import dev.aftly.flags.model.FlagCategoryBase
import dev.aftly.flags.model.FlagResources
import dev.aftly.flags.model.TimeMode
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {
    /* For List<FlagSuperCategory> as List<FlagCategoryBase> */
    @TypeConverter
    fun fromFlagSuperCategoryList(value: List<FlagCategoryBase>): String =
        Json.encodeToString(value)

    @TypeConverter
    fun toFlagSuperCategoryList(value: String): List<FlagCategoryBase> =
        Json.decodeFromString(value)


    /* For List<FlagCategory> */
    @TypeConverter
    fun fromFlagCategoryList(value: List<FlagCategory>): String = Json.encodeToString(value)

    @TypeConverter
    fun toFlagCategoryList(value: String): List<FlagCategory> = Json.decodeFromString(value)


    /* For List<FlagResources> */
    @TypeConverter
    fun fromFlagResourcesList(value: List<FlagResources>): String = Json.encodeToString(value)

    @TypeConverter
    fun toFlagResourcesList(value: String): List<FlagResources> = Json.decodeFromString(value)


    /* For TimeMode */
    @TypeConverter
    fun fromTimeMode(value: TimeMode): String = Json.encodeToString(value)

    @TypeConverter
    fun toTimeMode(value: String): TimeMode = Json.decodeFromString(value)
}