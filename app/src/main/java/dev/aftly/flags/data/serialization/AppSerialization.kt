package dev.aftly.flags.data.serialization

import dev.aftly.flags.model.FlagCategory
import dev.aftly.flags.model.FlagCategoryType
import dev.aftly.flags.model.FlagSuperCategory
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

object AppSerialization {
    private val flagSerializersModule = SerializersModule {
        polymorphic(FlagSuperCategory::class) {
            subclass(FlagSuperCategory.All::class)
            subclass(FlagSuperCategory.SovereignCountry::class)
            subclass(FlagSuperCategory.AutonomousRegion::class)
            subclass(FlagSuperCategory.Regional::class)
            subclass(FlagSuperCategory.International::class)
            subclass(FlagSuperCategory.Historical::class)
            subclass(FlagSuperCategory.Political::class)
            subclass(FlagSuperCategory.Cultural::class)
            subclass(FlagSuperCategory.TerritorialDistributionOfAuthority::class)
            subclass(FlagSuperCategory.ExecutiveStructure::class)
            subclass(FlagSuperCategory.LegalConstraint::class)
            subclass(FlagSuperCategory.PowerDerivation::class)
            subclass(FlagSuperCategory.RegimeType::class)
            subclass(FlagSuperCategory.IdeologicalOrientation::class)
            subclass(FlagSuperCategory.NonAdministrative::class)
        }

        polymorphic(FlagCategoryType::class) {
            subclass(FlagSuperCategory::class)
            subclass(FlagCategory::class)
        }
    }

    val json = Json {
        serializersModule = flagSerializersModule
        classDiscriminator = "type"
        ignoreUnknownKeys = false
    }
}