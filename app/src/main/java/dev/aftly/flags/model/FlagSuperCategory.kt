package dev.aftly.flags.model

import androidx.annotation.StringRes
import dev.aftly.flags.R
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
sealed class FlagSuperCategory(
    @StringRes val title: Int,
    @StringRes val gameCategoryPreview: Int?,
    @StringRes val gameCategoryDetailed: Int? = R.string.string_whitespace,
    @Polymorphic val subCategories: List<FlagCategoryBase>,
) : FlagCategoryBase() {
    fun enums(): List<FlagCategory> {
        return subCategories.filterIsInstance<FlagCategoryWrapper>().map { it.enum }
    }

    fun firstCategoryEnumOrNull(): FlagCategory? {
        return when (val firstCategory = subCategories.first()) {
            is FlagCategoryWrapper -> firstCategory.enum
            else -> null
        }
    }

    @Serializable
    @SerialName("all")
    data object All : FlagSuperCategory(
        title = R.string.category_super_all,
        gameCategoryPreview = R.string.category_super_all,
        gameCategoryDetailed = R.string.category_super_all_detailed,
        subCategories = FlagCategory.entries.map(::FlagCategoryWrapper),
    )

    @Serializable
    @SerialName("sovereign_country")
    data object SovereignCountry : FlagSuperCategory(
        title = R.string.category_super_sovereign_country,
        gameCategoryPreview = R.string.category_super_sovereign_country_game_category_preview,
        gameCategoryDetailed = R.string.category_super_sovereign_country_game_category_detailed,
        subCategories = listOf(
            FlagCategoryWrapper(enum = FlagCategory.SOVEREIGN_STATE)
        ),
    )

    @Serializable
    @SerialName("autonomous_region")
    data object AutonomousRegion : FlagSuperCategory(
        title = R.string.category_super_autonomous_region,
        gameCategoryPreview = R.string.category_super_autonomous_region_game_category_preview,
        gameCategoryDetailed = R.string.category_super_autonomous_region,
        subCategories = listOf(
            FlagCategoryWrapper(enum = FlagCategory.FREE_ASSOCIATION),
            FlagCategoryWrapper(enum = FlagCategory.AUTONOMOUS_REGION),
            FlagCategoryWrapper(enum = FlagCategory.DEVOLVED_GOVERNMENT)
            //FlagCategoryWrapper(enum = FlagCategory.INDIGENOUS_TERRITORY)
        ),
    )

    @Serializable
    @SerialName("regional")
    data object Regional : FlagSuperCategory(
        title = R.string.category_super_regional,
        gameCategoryPreview = R.string.category_super_regional,
        gameCategoryDetailed = R.string.category_super_regional,
        subCategories = listOf(
            //FlagCategoryWrapper(enum = FlagCategory.CANTON),
            FlagCategoryWrapper(enum = FlagCategory.COLLECTIVITY),
            FlagCategoryWrapper(enum = FlagCategory.COMMUNITY),
            FlagCategoryWrapper(enum = FlagCategory.COUNTRY),
            FlagCategoryWrapper(enum = FlagCategory.COUNTY),
            FlagCategoryWrapper(enum = FlagCategory.CITY),
            FlagCategoryWrapper(enum = FlagCategory.DISTRICT),
            //FlagCategoryWrapper(enum = FlagCategory.EMIRATE),
            //FlagCategoryWrapper(enum = FlagCategory.ENTITY),
            //FlagCategoryWrapper(enum = FlagCategory.GOVERNORATE),
            //FlagCategoryWrapper(enum = FlagCategory.ISLAND),
            //FlagCategoryWrapper(enum = FlagCategory.KRAI),
            //FlagCategoryWrapper(enum = FlagCategory.MEMBER_STATE),
            FlagCategoryWrapper(enum = FlagCategory.MUNICIPALITY),
            FlagCategoryWrapper(enum = FlagCategory.OBLAST),
            //FlagCategoryWrapper(enum = FlagCategory.OKRUG),
            FlagCategoryWrapper(enum = FlagCategory.PROVINCE),
            FlagCategoryWrapper(enum = FlagCategory.REGION),
            FlagCategoryWrapper(enum = FlagCategory.REGIONAL),
            FlagCategoryWrapper(enum = FlagCategory.REPUBLIC_UNIT),
            FlagCategoryWrapper(enum = FlagCategory.STATE),
            FlagCategoryWrapper(enum = FlagCategory.TERRITORY)
        ),
    )

    @Serializable
    @SerialName("international")
    data object International : FlagSuperCategory(
        title = R.string.category_super_international,
        gameCategoryPreview = R.string.category_super_international,
        gameCategoryDetailed = R.string.category_super_international_detailed,
        subCategories = listOf(
            //FlagCategoryWrapper(enum = FlagCategory.SUPRANATIONAL_UNION),
            FlagCategoryWrapper(enum = FlagCategory.INTERNATIONAL_ORGANIZATION)
        ),
    )

    @Serializable
    @SerialName("historical")
    data object Historical : FlagSuperCategory(
        title = R.string.category_super_historical,
        gameCategoryPreview = R.string.category_super_historical,
        gameCategoryDetailed = R.string.category_super_historical,
        subCategories = listOf(
            FlagCategoryWrapper(enum = FlagCategory.HISTORICAL)
        ),
    )

    @Serializable
    @SerialName("political")
    data object Political : FlagSuperCategory(
        title = R.string.category_super_political,
        gameCategoryPreview = null,
        gameCategoryDetailed = null,
        subCategories = listOf(
            TerritorialDistributionOfAuthority,
            ExecutiveStructure,
            LegalConstraint,
            PowerDerivation,
            //RegimeType,
            IdeologicalOrientation,
            NonAdministrative
        ),
    )

    @Serializable
    @SerialName("cultural")
    data object Cultural : FlagSuperCategory(
        title = R.string.category_super_cultural,
        gameCategoryPreview = R.string.category_super_cultural,
        gameCategoryDetailed = R.string.category_super_all_detailed,
        subCategories = listOf(
            FlagCategoryWrapper(enum = FlagCategory.ETHNIC),
            FlagCategoryWrapper(enum = FlagCategory.SOCIAL),
            FlagCategoryWrapper(enum = FlagCategory.POLITICAL),
            FlagCategoryWrapper(enum = FlagCategory.RELIGIOUS),
            FlagCategoryWrapper(enum = FlagCategory.REGIONAL)
        ),
    )

    /* Below FlagSuperCategory-s for use as subCategories for above FlagSuperCategory-s */

    @Serializable
    @SerialName("territorial_authority_distribution")
    data object TerritorialDistributionOfAuthority : FlagSuperCategory(
        title = R.string.category_sub_territorial_power_distribution,
        gameCategoryPreview = null,
        gameCategoryDetailed = null,
        subCategories = listOf(
            FlagCategoryWrapper(enum = FlagCategory.FEDERAL),
            FlagCategoryWrapper(enum = FlagCategory.UNITARY),
            FlagCategoryWrapper(enum = FlagCategory.CONFEDERATION)
        ),
    )

    @Serializable
    @SerialName("executive_structure")
    data object ExecutiveStructure : FlagSuperCategory(
        title = R.string.category_sub_executive_structure,
        gameCategoryPreview = null,
        gameCategoryDetailed = null,
        subCategories = listOf(
            FlagCategoryWrapper(enum = FlagCategory.DIRECTORIAL),
            FlagCategoryWrapper(enum = FlagCategory.PARLIAMENTARY),
            FlagCategoryWrapper(enum = FlagCategory.SEMI_PRESIDENTIAL),
            FlagCategoryWrapper(enum = FlagCategory.DUAL_EXECUTIVE),
            FlagCategoryWrapper(enum = FlagCategory.PRESIDENTIAL)
        ),
    )

    @Serializable
    @SerialName("legal_constraint")
    data object LegalConstraint : FlagSuperCategory(
        title = R.string.category_sub_legal_constraint,
        gameCategoryPreview = null,
        gameCategoryDetailed = null,
        subCategories = listOf(
            FlagCategoryWrapper(enum = FlagCategory.CONSTITUTIONAL),
            FlagCategoryWrapper(enum = FlagCategory.NOMINAL_EXTRA_CONSTITUTIONAL)
        ),
    )

    @Serializable
    @SerialName("power_derivation")
    data object PowerDerivation : FlagSuperCategory(
        title = R.string.category_sub_power_derivation,
        gameCategoryPreview = null,
        gameCategoryDetailed = null,
        subCategories = listOf(
            FlagCategoryWrapper(enum = FlagCategory.REPUBLIC),
            FlagCategoryWrapper(enum = FlagCategory.MONARCHY),
            FlagCategoryWrapper(enum = FlagCategory.ONE_PARTY),
            FlagCategoryWrapper(enum = FlagCategory.THEOCRACY),
            FlagCategoryWrapper(enum = FlagCategory.MILITARY_JUNTA),
            FlagCategoryWrapper(enum = FlagCategory.PROVISIONAL_GOVERNMENT)
        ),
    )

    @Serializable
    @SerialName("regime_type")
    data object RegimeType : FlagSuperCategory(
        title = R.string.category_sub_regime_type,
        gameCategoryPreview = null,
        gameCategoryDetailed = null,
        subCategories = listOf(
            FlagCategoryWrapper(enum = FlagCategory.DEMOCRACY),
            FlagCategoryWrapper(enum = FlagCategory.AUTHORITARIAN),
            FlagCategoryWrapper(enum = FlagCategory.TOTALITARIAN),
            FlagCategoryWrapper(enum = FlagCategory.DICTATORSHIP)
        ),
    )

    @Serializable
    @SerialName("ideological_orientation")
    data object IdeologicalOrientation : FlagSuperCategory(
        title = R.string.category_sub_ideological_orientation,
        gameCategoryPreview = null,
        gameCategoryDetailed = null,
        subCategories = listOf(
            //FlagCategoryWrapper(enum = FlagCategory.LIBERAL),
            //FlagCategoryWrapper(enum = FlagCategory.ILLIBERAL),
            FlagCategoryWrapper(enum = FlagCategory.THEOCRATIC),
            FlagCategoryWrapper(enum = FlagCategory.SOCIALIST),
            FlagCategoryWrapper(enum = FlagCategory.FASCIST)
        ),
    )

    @Serializable
    @SerialName("non_administrative_political")
    data object NonAdministrative : FlagSuperCategory(
        title = R.string.category_non_administrative_political,
        gameCategoryPreview = null,
        gameCategoryDetailed = null,
        subCategories = listOf(
            FlagCategoryWrapper(enum = FlagCategory.POLITICAL)
        ),
    )
}