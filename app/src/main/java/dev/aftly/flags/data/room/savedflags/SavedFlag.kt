package dev.aftly.flags.data.room.savedflags

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.aftly.flags.model.FlagResources
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "saved_flags")
data class SavedFlag(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val flag: FlagResources,
)
