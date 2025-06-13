package dev.aftly.flags.model

import androidx.annotation.StringRes
import dev.aftly.flags.R
import kotlinx.serialization.Serializable


@Serializable
sealed class FlagSuperCategory(
    @StringRes val title: Int,
    @StringRes val gameCategory: Int?,
    val subCategories: List<FlagCategoryType>,
) : FlagCategoryType {
    data object All : FlagSuperCategory(
        title = R.string.category_super_all,
        gameCategory = R.string.category_super_all,
        subCategories = FlagCategory.entries.toList(),
    )

    data object SovereignCountry : FlagSuperCategory(
        title = R.string.category_super_sovereign_country,
        gameCategory = R.string.category_super_sovereign_country_game_category,
        subCategories = listOf(FlagCategory.SOVEREIGN_STATE),
    )

    data object AutonomousRegion : FlagSuperCategory(
        title = R.string.category_super_autonomous_region,
        gameCategory = R.string.category_super_autonomous_region_game_category,
        subCategories = listOf(
            FlagCategory.FREE_ASSOCIATION,
            FlagCategory.AUTONOMOUS_REGION,
            FlagCategory.DEVOLVED_GOVERNMENT
            //FlagCategory.INDIGENOUS_TERRITORY
        ),
    )

    data object Regional : FlagSuperCategory(
        title = R.string.category_super_regional,
        gameCategory = R.string.category_super_regional,
        subCategories = listOf(
            //FlagCategory.CANTON,
            FlagCategory.COLLECTIVITY,
            FlagCategory.COMMUNITY,
            FlagCategory.COUNTRY,
            FlagCategory.COUNTY,
            FlagCategory.CITY,
            FlagCategory.DISTRICT,
            //FlagCategory.EMIRATE,
            //FlagCategory.ENTITY,
            //FlagCategory.GOVERNORATE,
            //FlagCategory.ISLAND,
            //FlagCategory.KRAI,
            //FlagCategory.MEMBER_STATE,
            FlagCategory.MUNICIPALITY,
            FlagCategory.OBLAST,
            //FlagCategory.OKRUG,
            FlagCategory.PROVINCE,
            FlagCategory.REGION,
            FlagCategory.REGIONAL,
            FlagCategory.REPUBLIC_UNIT,
            FlagCategory.STATE,
            FlagCategory.TERRITORY
        ),
    )

    data object International : FlagSuperCategory(
        title = R.string.category_super_international,
        gameCategory = R.string.category_super_international,
        subCategories = listOf(
            //FlagCategory.SUPRANATIONAL_UNION,
            FlagCategory.INTERNATIONAL_ORGANIZATION
        ),
    )

    data object Historical : FlagSuperCategory(
        title = R.string.category_super_historical,
        gameCategory = R.string.category_super_historical,
        subCategories = listOf(FlagCategory.HISTORICAL),
    )

    data object Political : FlagSuperCategory(
        title = R.string.category_super_political,
        gameCategory = null,
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

    data object Cultural : FlagSuperCategory(
        title = R.string.category_super_cultural,
        gameCategory = R.string.category_super_cultural,
        subCategories = listOf(
            FlagCategory.ETHNIC,
            FlagCategory.SOCIAL,
            FlagCategory.POLITICAL,
            FlagCategory.RELIGIOUS,
            FlagCategory.REGIONAL
        ),
    )

    /* Below FlagSuperCategory-s for use as subCategories for above FlagSuperCategory-s */

    data object TerritorialDistributionOfAuthority : FlagSuperCategory(
        title = R.string.category_sub_territorial_power_distribution,
        gameCategory = null,
        subCategories = listOf(
            FlagCategory.FEDERAL,
            FlagCategory.UNITARY,
            FlagCategory.CONFEDERATION
        ),
    )

    data object ExecutiveStructure : FlagSuperCategory(
        title = R.string.category_sub_executive_structure,
        gameCategory = null,
        subCategories = listOf(
            FlagCategory.DIRECTORIAL,
            FlagCategory.PARLIAMENTARY,
            FlagCategory.SEMI_PRESIDENTIAL,
            FlagCategory.DUAL_EXECUTIVE,
            FlagCategory.PRESIDENTIAL
        ),
    )

    data object LegalConstraint : FlagSuperCategory(
        title = R.string.category_sub_legal_constraint,
        gameCategory = null,
        subCategories = listOf(
            FlagCategory.CONSTITUTIONAL,
            FlagCategory.NOMINAL_EXTRA_CONSTITUTIONAL
        ),
    )

    data object PowerDerivation : FlagSuperCategory(
        title = R.string.category_sub_power_derivation,
        gameCategory = null,
        subCategories = listOf(
            FlagCategory.REPUBLIC,
            FlagCategory.MONARCHY,
            FlagCategory.ONE_PARTY,
            FlagCategory.THEOCRACY,
            FlagCategory.MILITARY_JUNTA,
            FlagCategory.PROVISIONAL_GOVERNMENT
        ),
    )

    private data object RegimeType : FlagSuperCategory(
        title = R.string.category_sub_regime_type,
        gameCategory = null,
        subCategories = listOf(
            FlagCategory.DEMOCRACY,
            FlagCategory.AUTHORITARIAN,
            FlagCategory.TOTALITARIAN,
            FlagCategory.DICTATORSHIP
        ),
    )

    data object IdeologicalOrientation : FlagSuperCategory(
        title = R.string.category_sub_ideological_orientation,
        gameCategory = null,
        subCategories = listOf(
            //FlagCategory.LIBERAL,
            //FlagCategory.ILLIBERAL,
            FlagCategory.THEOCRATIC,
            FlagCategory.SOCIALIST,
            FlagCategory.FASCIST
        ),
    )

    data object NonAdministrative : FlagSuperCategory(
        title = R.string.category_non_administrative_political,
        gameCategory = null,
        subCategories = listOf(FlagCategory.POLITICAL),
    )
}