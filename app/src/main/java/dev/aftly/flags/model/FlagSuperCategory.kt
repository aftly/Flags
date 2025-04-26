package dev.aftly.flags.model

import androidx.annotation.StringRes
import dev.aftly.flags.R


sealed class FlagSuperCategory(
    @StringRes val title: Int,
    val subCategories: List<Any>,
) {
    data object All : FlagSuperCategory(
        title = R.string.category_super_all,
        subCategories = FlagCategory.entries.toList(),
    )

    data object SovereignCountry : FlagSuperCategory(
        title = R.string.category_super_sovereign_country,
        subCategories = listOf(FlagCategory.SOVEREIGN_STATE),
    )

    data object AutonomousRegion : FlagSuperCategory(
        title = R.string.category_super_autonomous_region,
        subCategories = listOf(
            FlagCategory.FREE_ASSOCIATION,
            FlagCategory.AUTONOMOUS_REGION,
            FlagCategory.DEVOLVED_GOVERNMENT
            //FlagCategory.INDIGENOUS_TERRITORY
        ),
    )

    data object Regional : FlagSuperCategory(
        title = R.string.category_super_regional,
        subCategories = listOf(
            FlagCategory.AUTONOMOUS_REGION,
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
        subCategories = listOf(
            //FlagCategory.SUPRANATIONAL_UNION,
            FlagCategory.INTERNATIONAL_ORGANIZATION
        ),
    )

    data object Historical : FlagSuperCategory(
        title = R.string.category_super_historical,
        subCategories = listOf(FlagCategory.HISTORICAL),
    )

    data object Political : FlagSuperCategory(
        title = R.string.category_super_political,
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
        subCategories = listOf(
            FlagCategory.FEDERAL,
            FlagCategory.UNITARY,
            FlagCategory.CONFEDERATION
        ),
    )

    data object ExecutiveStructure : FlagSuperCategory(
        title = R.string.category_sub_executive_structure,
        subCategories = listOf(
            FlagCategory.DIRECTORIAL,
            FlagCategory.PARLIAMENTARY,
            FlagCategory.SEMI_PRESIDENTIAL,
            FlagCategory.DUAL_EXECUTIVE,
            FlagCategory.PRESIDENTIAL
        ),
    )

    private data object LegalConstraint : FlagSuperCategory(
        title = R.string.category_sub_legal_constraint,
        subCategories = listOf(
            FlagCategory.CONSTITUTIONAL,
            FlagCategory.NOMINAL_EXTRA_CONSTITUTIONAL
        ),
    )

    private data object PowerDerivation : FlagSuperCategory(
        title = R.string.category_sub_power_derivation,
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
        subCategories = listOf(
            FlagCategory.DEMOCRACY,
            FlagCategory.AUTHORITARIAN,
            FlagCategory.TOTALITARIAN,
            FlagCategory.DICTATORSHIP
        ),
    )

    data object IdeologicalOrientation : FlagSuperCategory(
        title = R.string.category_sub_ideological_orientation,
        subCategories = listOf(
            //FlagCategory.LIBERAL,
            //FlagCategory.ILLIBERAL,
            FlagCategory.THEOCRATIC,
            FlagCategory.SOCIALIST,
            FlagCategory.FASCIST
        ),
    )

    private data object NonAdministrative : FlagSuperCategory(
        title = R.string.category_non_administrative_political,
        subCategories = listOf(FlagCategory.POLITICAL),
    )
}