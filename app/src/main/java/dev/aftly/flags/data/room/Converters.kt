package dev.aftly.flags.data.room

import androidx.room.TypeConverter
import dev.aftly.flags.model.FlagCategory
import dev.aftly.flags.model.FlagCategoryBase
import dev.aftly.flags.model.TimeMode
import kotlinx.serialization.json.Json

class Converters {
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


    /* For List<String> */
    @TypeConverter
    fun fromStringList(value: List<String>): String =
        Json.encodeToString(value = value)
    @TypeConverter
    fun toStringList(value: String): List<String> =
        Json.decodeFromString(string = value)


    /* For TimeMode */
    @TypeConverter
    fun fromTimeMode(value: TimeMode): String =
        Json.encodeToString(value = value)
    @TypeConverter
    fun toTimeMode(value: String): TimeMode =
        Json.decodeFromString(string = value)
}