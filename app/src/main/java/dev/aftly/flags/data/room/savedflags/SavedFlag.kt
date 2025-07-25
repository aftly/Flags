package dev.aftly.flags.data.room.savedflags

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_flags")
data class SavedFlag(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "flag_key")
    val flagKey: String,
)
