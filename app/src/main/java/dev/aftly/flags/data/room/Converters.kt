package dev.aftly.flags.data.room

import androidx.room.TypeConverter
import dev.aftly.flags.model.FlagCategory
import dev.aftly.flags.model.FlagCategoryBase
import dev.aftly.flags.model.FlagResources
import dev.aftly.flags.model.TimeMode
import kotlinx.serialization.json.Json

class Converters {
    /* For FlagResources */
    @TypeConverter
    fun fromFlagResources(value: FlagResources): String =
        Json.encodeToString(value = value)
    @TypeConverter
    fun toFlagResources(value: String): FlagResources =
        Json.decodeFromString(string = value)


    /* For List<FlagSuperCategory> as List<FlagCategoryBase> */
    @TypeConverter
    fun fromFlagSuperCategoryList(value: List<FlagCategoryBase>): String =
        Json.encodeToString(value = value)
    @TypeConverter
    fun toFlagSuperCategoryList(value: String): List<FlagCategoryBase> =
        Json.decodeFromString(string = value)


    /* For List<FlagCategory> */
    @TypeConverter
    fun fromFlagCategoryList(value: List<FlagCategory>): String =
        Json.encodeToString(value = value)
    @TypeConverter
    fun toFlagCategoryList(value: String): List<FlagCategory> =
        Json.decodeFromString(string = value)


    /* For List<FlagResources> */
    @TypeConverter
    fun fromFlagResourcesList(value: List<FlagResources>): String =
        Json.encodeToString(value = value)
    @TypeConverter
    fun toFlagResourcesList(value: String): List<FlagResources> =
        Json.decodeFromString(string = value)


    /* For TimeMode */
    @TypeConverter
    fun fromTimeMode(value: TimeMode): String =
        Json.encodeToString(value = value)
    @TypeConverter
    fun toTimeMode(value: String): TimeMode =
        Json.decodeFromString(string = value)
}