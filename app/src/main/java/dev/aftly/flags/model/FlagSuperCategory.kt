package dev.aftly.flags.model

import androidx.annotation.StringRes
import dev.aftly.flags.R
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
sealed class FlagSuperCategory(
    @param:StringRes val title: Int,
    @param:StringRes val categoriesMenuButton: Int?,
    @param:StringRes val gameScoreCategoryPreview: Int?,
    @param:StringRes val gameScoreCategoryDetailed: Int?,
    @Polymorphic val subCategories: List<FlagCategoryBase>,
) : FlagCategoryBase() {
    fun supers(): List<FlagSuperCategory> = subCategories.filterIsInstance<FlagSuperCategory>()

    fun allSupers(categories: List<FlagSuperCategory> = supers() + this): List<FlagSuperCategory> {
        return categories.flatMap { superCategory ->
            val childSupers = superCategory.supers()

            when (childSupers.size) {
                0 -> listOf(superCategory)
                else -> listOf(superCategory) + childSupers + allSupers(categories = childSupers)
            }
        }.distinct()
    }

    fun enums(): List<FlagCategory> =
        subCategories.filterIsInstance<FlagCategoryWrapper>().map { it.enum }

    fun allEnums(categories: List<FlagCategoryBase> = subCategories): List<FlagCategory> {
        return categories.flatMap { category ->
            when (category) {
                is FlagCategoryWrapper -> listOf(category.enum)
                is FlagSuperCategory -> allEnums(category.subCategories)
            }
        }
    }

    fun firstCategoryEnumOrNull(): FlagCategory? = when (val first = subCategories.first()) {
        is FlagCategoryWrapper -> first.enum
        else -> null
    }

    @Serializable
    @SerialName(value = "all")
    data object All : FlagSuperCategory(
        title = R.string.category_super_all,
        categoriesMenuButton = R.string.category_super_all_menu_button,
        gameScoreCategoryPreview = R.string.category_super_all_score_preview,
        gameScoreCategoryDetailed = R.string.category_super_all_score_detailed,
        subCategories = FlagCategory.entries.map(::FlagCategoryWrapper),
    )

    @Serializable
    @SerialName(value = "sovereign_country")
    data object SovereignCountry : FlagSuperCategory(
        title = R.string.category_super_sovereign_country,
        categoriesMenuButton = R.string.category_super_sovereign_country_menu_button,
        gameScoreCategoryPreview = R.string.category_super_sovereign_country_score_preview,
        gameScoreCategoryDetailed = R.string.category_super_sovereign_country_score_detailed,
        subCategories = listOf(
            FlagCategoryWrapper(enum = FlagCategory.SOVEREIGN_STATE)
        ),
    )

    @Serializable
    @SerialName(value = "autonomous_region")
    data object AutonomousRegion : FlagSuperCategory(
        title = R.string.category_super_autonomous_region,
        categoriesMenuButton = R.string.category_super_autonomous_region_menu_button,
        gameScoreCategoryPreview = R.string.category_super_autonomous_region_score_preview,
        gameScoreCategoryDetailed = R.string.category_super_autonomous_region_score_detailed,
        subCategories = listOf(
            FlagCategoryWrapper(enum = FlagCategory.FREE_ASSOCIATION),
            FlagCategoryWrapper(enum = FlagCategory.AUTONOMOUS_REGION),
            FlagCategoryWrapper(enum = FlagCategory.DEVOLVED_GOVERNMENT)
            //FlagCategoryWrapper(enum = FlagCategory.INDIGENOUS_TERRITORY)
        ),
    )

    @Serializable
    @SerialName(value = "regional")
    data object Regional : FlagSuperCategory(
        title = R.string.category_super_regional,
        categoriesMenuButton = R.string.category_super_regional_menu_button,
        gameScoreCategoryPreview = R.string.category_super_regional_score_preview,
        gameScoreCategoryDetailed = R.string.category_super_regional_score_detailed,
        subCategories = listOf(
            FlagCategoryWrapper(enum = FlagCategory.CANTON),
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
            FlagCategoryWrapper(enum = FlagCategory.KRAI),
            //FlagCategoryWrapper(enum = FlagCategory.MEMBER_STATE),
            FlagCategoryWrapper(enum = FlagCategory.MUNICIPALITY),
            FlagCategoryWrapper(enum = FlagCategory.OBLAST),
            FlagCategoryWrapper(enum = FlagCategory.OKRUG),
            FlagCategoryWrapper(enum = FlagCategory.PROVINCE),
            FlagCategoryWrapper(enum = FlagCategory.REGION),
            FlagCategoryWrapper(enum = FlagCategory.REGIONAL),
            FlagCategoryWrapper(enum = FlagCategory.REPUBLIC_UNIT),
            FlagCategoryWrapper(enum = FlagCategory.STATE),
            FlagCategoryWrapper(enum = FlagCategory.TERRITORY)
        ),
    )

    @Serializable
    @SerialName(value = "international")
    data object International : FlagSuperCategory(
        title = R.string.category_super_international,
        categoriesMenuButton = R.string.category_super_international_menu_button,
        gameScoreCategoryPreview = R.string.category_super_international_score_preview,
        gameScoreCategoryDetailed = R.string.category_super_international_score_detailed,
        subCategories = listOf(
            //FlagCategoryWrapper(enum = FlagCategory.SUPRANATIONAL_UNION),
            FlagCategoryWrapper(enum = FlagCategory.INTERNATIONAL_ORGANIZATION)
        ),
    )

    @Serializable
    @SerialName(value = "institution")
    data object Institution : FlagSuperCategory(
        title = R.string.category_super_institution,
        categoriesMenuButton = R.string.category_super_institution_menu_button,
        gameScoreCategoryPreview = R.string.category_super_institution_score_preview,
        gameScoreCategoryDetailed = R.string.category_super_institution_score_detailed,
        subCategories = listOf(Legislature, Executive, Civilian),
    )

    @Serializable
    @SerialName(value = "legislature")
    data object Legislature : FlagSuperCategory(
        title = R.string.category_super_legislature,
        categoriesMenuButton = R.string.category_super_legislature_menu_button,
        gameScoreCategoryPreview = R.string.category_super_legislature_score_preview,
        gameScoreCategoryDetailed = R.string.category_super_legislature_score_detailed,
        subCategories = listOf(
            FlagCategoryWrapper(enum = FlagCategory.LOWER_HOUSE),
            FlagCategoryWrapper(enum = FlagCategory.UPPER_HOUSE),
            FlagCategoryWrapper(enum = FlagCategory.UNICAMERAL),
            FlagCategoryWrapper(enum = FlagCategory.PARLIAMENT),
            FlagCategoryWrapper(enum = FlagCategory.CONGRESS),
            FlagCategoryWrapper(enum = FlagCategory.ASSEMBLY)
        ),
    )

    data object LegislatureDivision : FlagSuperCategory(
        title = R.string.category_super_legislature_division,
        categoriesMenuButton = null,
        gameScoreCategoryPreview = null,
        gameScoreCategoryDetailed = null,
        subCategories = listOf(
            FlagCategoryWrapper(enum = FlagCategory.LOWER_HOUSE),
            FlagCategoryWrapper(enum = FlagCategory.UPPER_HOUSE),
            FlagCategoryWrapper(enum = FlagCategory.UNICAMERAL)
        )
    )
    data object LegislatureBody : FlagSuperCategory(
        title = R.string.category_super_legislature_body,
        categoriesMenuButton = null,
        gameScoreCategoryPreview = null,
        gameScoreCategoryDetailed = null,
        subCategories = listOf(
            FlagCategoryWrapper(enum = FlagCategory.PARLIAMENT),
            FlagCategoryWrapper(enum = FlagCategory.CONGRESS),
            FlagCategoryWrapper(enum = FlagCategory.ASSEMBLY)
        )
    )

    @Serializable
    @SerialName(value = "executive")
    data object Executive : FlagSuperCategory(
        title = R.string.category_super_executive,
        categoriesMenuButton = R.string.category_super_executive_menu_button,
        gameScoreCategoryPreview = R.string.category_super_executive_score_preview,
        gameScoreCategoryDetailed = R.string.category_super_executive_score_detailed,
        subCategories = listOf(
            FlagCategoryWrapper(enum = FlagCategory.DEPARTMENT),
            FlagCategoryWrapper(enum = FlagCategory.MILITARY),
            FlagCategoryWrapper(enum = FlagCategory.POLICE)
        ),
    )

    @Serializable
    @SerialName(value = "executive")
    data object Civilian : FlagSuperCategory(
        title = R.string.category_super_civilian,
        categoriesMenuButton = R.string.category_super_civilian_menu_button,
        gameScoreCategoryPreview = R.string.category_super_civilian_score_preview,
        gameScoreCategoryDetailed = R.string.category_super_civilian_score_detailed,
        subCategories = listOf(
            FlagCategoryWrapper(enum = FlagCategory.CHARITY),
            FlagCategoryWrapper(enum = FlagCategory.UNIVERSITY)
        ),
    )

    @Serializable
    @SerialName(value = "historical")
    data object Historical : FlagSuperCategory(
        title = R.string.category_super_historical,
        categoriesMenuButton = R.string.category_super_historical_menu_button,
        gameScoreCategoryPreview = R.string.category_super_historical_score_preview,
        gameScoreCategoryDetailed = R.string.category_super_historical_score_detailed,
        subCategories = listOf(
            FlagCategoryWrapper(enum = FlagCategory.HISTORICAL)
        ),
    )

    @Serializable
    @SerialName(value = "cultural")
    data object Cultural : FlagSuperCategory(
        title = R.string.category_super_cultural,
        categoriesMenuButton = R.string.category_super_cultural_menu_button,
        gameScoreCategoryPreview = R.string.category_super_cultural_score_preview,
        gameScoreCategoryDetailed = R.string.category_super_cultural_score_detailed,
        subCategories = listOf(
            FlagCategoryWrapper(enum = FlagCategory.SOCIAL),
            FlagCategoryWrapper(enum = FlagCategory.POLITICAL),
            FlagCategoryWrapper(enum = FlagCategory.RELIGIOUS),
            FlagCategoryWrapper(enum = FlagCategory.ETHNIC),
        ),
    )

    @Serializable
    @SerialName(value = "political")
    data object Political : FlagSuperCategory(
        title = R.string.category_super_political,
        categoriesMenuButton = null,
        gameScoreCategoryPreview = null,
        gameScoreCategoryDetailed = null,
        subCategories = listOf(
            TerritorialDistributionOfAuthority,
            ExecutiveStructure,
            LegalConstraint,
            PowerDerivation,
            //RegimeType,
            IdeologicalOrientation
        ),
    )


    /* Below FlagSuperCategory-s for use as subCategories for above FlagSuperCategory-s */

    @Serializable
    @SerialName(value = "territorial_authority_distribution")
    data object TerritorialDistributionOfAuthority : FlagSuperCategory(
        title = R.string.category_sub_territorial_power_distribution,
        categoriesMenuButton = null,
        gameScoreCategoryPreview = null,
        gameScoreCategoryDetailed = null,
        subCategories = listOf(
            FlagCategoryWrapper(enum = FlagCategory.FEDERAL),
            FlagCategoryWrapper(enum = FlagCategory.UNITARY),
            FlagCategoryWrapper(enum = FlagCategory.CONFEDERATION)
        ),
    )

    @Serializable
    @SerialName(value = "executive_structure")
    data object ExecutiveStructure : FlagSuperCategory(
        title = R.string.category_sub_executive_structure,
        categoriesMenuButton = null,
        gameScoreCategoryPreview = null,
        gameScoreCategoryDetailed = null,
        subCategories = listOf(
            FlagCategoryWrapper(enum = FlagCategory.DIRECTORIAL),
            FlagCategoryWrapper(enum = FlagCategory.PARLIAMENTARY),
            FlagCategoryWrapper(enum = FlagCategory.SEMI_PRESIDENTIAL),
            FlagCategoryWrapper(enum = FlagCategory.DUAL_EXECUTIVE),
            FlagCategoryWrapper(enum = FlagCategory.PRESIDENTIAL)
        ),
    )

    @Serializable
    @SerialName(value = "legal_constraint")
    data object LegalConstraint : FlagSuperCategory(
        title = R.string.category_sub_legal_constraint,
        categoriesMenuButton = null,
        gameScoreCategoryPreview = null,
        gameScoreCategoryDetailed = null,
        subCategories = listOf(
            FlagCategoryWrapper(enum = FlagCategory.CONSTITUTIONAL),
            FlagCategoryWrapper(enum = FlagCategory.NOMINAL_EXTRA_CONSTITUTIONAL)
        ),
    )

    @Serializable
    @SerialName(value = "power_derivation")
    data object PowerDerivation : FlagSuperCategory(
        title = R.string.category_sub_power_derivation,
        categoriesMenuButton = null,
        gameScoreCategoryPreview = null,
        gameScoreCategoryDetailed = null,
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
    @SerialName(value = "regime_type")
    data object RegimeType : FlagSuperCategory(
        title = R.string.category_sub_regime_type,
        categoriesMenuButton = null,
        gameScoreCategoryPreview = null,
        gameScoreCategoryDetailed = null,
        subCategories = listOf(
            FlagCategoryWrapper(enum = FlagCategory.DEMOCRACY),
            FlagCategoryWrapper(enum = FlagCategory.AUTHORITARIAN),
            FlagCategoryWrapper(enum = FlagCategory.TOTALITARIAN),
            FlagCategoryWrapper(enum = FlagCategory.DICTATORSHIP)
        ),
    )

    @Serializable
    @SerialName(value = "ideological_orientation")
    data object IdeologicalOrientation : FlagSuperCategory(
        title = R.string.category_sub_ideological_orientation,
        categoriesMenuButton = null,
        gameScoreCategoryPreview = null,
        gameScoreCategoryDetailed = null,
        subCategories = listOf(
            //FlagCategoryWrapper(enum = FlagCategory.LIBERAL),
            //FlagCategoryWrapper(enum = FlagCategory.ILLIBERAL),
            FlagCategoryWrapper(enum = FlagCategory.THEOCRATIC),
            FlagCategoryWrapper(enum = FlagCategory.SOCIALIST),
            FlagCategoryWrapper(enum = FlagCategory.FASCIST)
        ),
    )
}