package dev.aftly.flags.data

import dev.aftly.flags.R
import dev.aftly.flags.model.FlagCategory
import dev.aftly.flags.model.FlagResources
import dev.aftly.flags.model.FlagSuperCategory


data object DataSource {
    val superCategoryList = listOf(
        FlagSuperCategory.All,
        FlagSuperCategory.SovereignCountry,
        FlagSuperCategory.AutonomousRegion,
        FlagSuperCategory.Regional,
        FlagSuperCategory.International,
        FlagSuperCategory.Cultural,
        FlagSuperCategory.Historical,
        FlagSuperCategory.Political
    )

    val historicalSuperCategoryExceptions = listOf(
        FlagSuperCategory.SovereignCountry,
        FlagSuperCategory.AutonomousRegion,
        FlagSuperCategory.Regional,
        FlagSuperCategory.International
    )

    val nullFlag = FlagResources(
        id = 0,
        image = 0,
        imagePreview = 0,
        flagOf = 0,
        flagOfOfficial = 0,
        flagOfAlternate = null,
        isFlagOfThe = false,
        isFlagOfOfficialThe = false,
        sovereignState = null,
        associatedState = null,
        categories = emptyList(),
    )

    val flagsMap = mapOf(
        "abkhazia" to FlagResources(
            id = 1,
            image = R.drawable.flag_of_the_republic_of_abkhazia,
            imagePreview = R.drawable.flag_of_the_republic_of_abkhazia,
            flagOf = R.string.abkhazia,
            flagOfOfficial = R.string.abkhazia_official,
            flagOfAlternate = listOf(
                R.string.abkhazia_alt_1,
                R.string.abkhazia_alt_2,
                R.string.abkhazia_alt_3,
                R.string.abkhazia_alt_4,
                R.string.abkhazia_alt_5,
                R.string.abkhazia_alt_6,
                R.string.abkhazia_alt_7,
                R.string.abkhazia_alt_8
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = "georgia",
            associatedState = null,
            categories = listOf(FlagCategory.AUTONOMOUS_REGION),
        ),

        "adjara" to FlagResources(
            id = 2,
            image = R.drawable.flag_of_adjara,
            imagePreview = R.drawable.flag_of_adjara_preview,
            flagOf = R.string.adjara,
            flagOfOfficial = R.string.adjara_official,
            flagOfAlternate = listOf(
                R.string.adjara_alt_1,
                R.string.adjara_alt_2,
                R.string.adjara_alt_3,
                R.string.adjara_alt_4,
                R.string.adjara_alt_5,
                R.string.adjara_alt_6,
                R.string.adjara_alt_7,
                R.string.adjara_alt_8,
                R.string.adjara_alt_9
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = "georgia",
            associatedState = null,
            categories = listOf(
                FlagCategory.AUTONOMOUS_REGION,
                FlagCategory.REPUBLIC_UNIT,
                FlagCategory.DEVOLVED_GOVERNMENT
            ),
        ),

        "adygea" to FlagResources(
            id = 3,
            image = R.drawable.circassian_flag,
            imagePreview = R.drawable.circassian_flag,
            flagOf = R.string.adygea,
            flagOfOfficial = R.string.adygea_official,
            flagOfAlternate = listOf(
                R.string.adygea_alt_1,
                R.string.adygea_alt_2,
                R.string.adygea_alt_3,
                R.string.adygea_alt_4,
                R.string.adygea_alt_5,
                R.string.adygea_alt_6,
                R.string.adygea_alt_7
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = "russia",
            associatedState = null,
            categories = listOf(
                FlagCategory.ETHNIC,
                FlagCategory.REPUBLIC_UNIT
            ),
        ),

        "afghanistan" to FlagResources(
            id = 4,
            image = R.drawable.flag_of_the_taliban,
            imagePreview = R.drawable.flag_of_the_taliban,
            flagOf = R.string.afghanistan,
            flagOfOfficial = R.string.afghanistan_official,
            flagOfAlternate = listOf(
                R.string.afghanistan_alt_1,
                R.string.afghanistan_alt_2,
                R.string.afghanistan_alt_3,
                R.string.afghanistan_alt_4,
                R.string.afghanistan_alt_5,
                R.string.afghanistan_alt_6,
                R.string.afghanistan_alt_7
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.THEOCRACY
            ),
        ),

        "africanUnion" to FlagResources(
            id = 5,
            image = R.drawable.flag_of_the_african_union,
            imagePreview = R.drawable.flag_of_the_african_union_preview,
            flagOf = R.string.african_union,
            flagOfOfficial = R.string.african_union_official,
            flagOfAlternate = listOf(
                R.string.african_union_alt_1,
                R.string.african_union_alt_2,
                R.string.african_union_alt_3,
                R.string.african_union_alt_4,
                R.string.african_union_alt_5
            ),
            isFlagOfThe = true,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.INTERNATIONAL_ORGANIZATION,
                FlagCategory.DIRECTORIAL,
                FlagCategory.CONFEDERATION
            ),
        ),

        "aland" to FlagResources(
            id = 6,
            image = R.drawable.flag_of__land,
            imagePreview = R.drawable.flag_of__land,
            flagOf = R.string.aland,
            flagOfOfficial = R.string.aland_official,
            flagOfAlternate = listOf(
                R.string.aland_alt_1,
                R.string.aland_alt_2,
                R.string.aland_alt_3,
                R.string.aland_alt_4,
                R.string.aland_alt_5,
                R.string.aland_alt_6,
                R.string.aland_alt_7,
                R.string.aland_alt_8,
                R.string.aland_alt_9
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = "finland",
            associatedState = null,
            categories = listOf(
                FlagCategory.AUTONOMOUS_REGION,
                FlagCategory.REGION
            ),
        ),

        "albania" to FlagResources(
            id = 7,
            image = R.drawable.flag_of_albania,
            imagePreview = R.drawable.flag_of_albania,
            flagOf = R.string.albania,
            flagOfOfficial = R.string.albania_official,
            flagOfAlternate = listOf(
                R.string.albania_alt_1,
                R.string.albania_alt_2,
                R.string.albania_alt_3,
                R.string.albania_alt_4,
                R.string.albania_alt_5,
                R.string.albania_alt_6,
                R.string.albania_alt_7,
                R.string.albania_alt_8,
                R.string.albania_alt_9
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "algeria" to FlagResources(
            id = 8,
            image = R.drawable.flag_of_algeria,
            imagePreview = R.drawable.flag_of_algeria,
            flagOf = R.string.algeria,
            flagOfOfficial = R.string.algeria_official,
            flagOfAlternate = listOf(
                R.string.algeria_alt_1,
                R.string.algeria_alt_2,
                R.string.algeria_alt_3,
                R.string.algeria_alt_4,
                R.string.algeria_alt_5
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.SEMI_PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "americanSamoa" to FlagResources(
            id = 9,
            image = R.drawable.flag_of_american_samoa,
            imagePreview = R.drawable.flag_of_american_samoa,
            flagOf = R.string.american_samoa,
            flagOfOfficial = R.string.american_samoa_official,
            flagOfAlternate = listOf(
                R.string.american_samoa_alt_1,
                R.string.american_samoa_alt_2,
                R.string.american_samoa_alt_3,
                R.string.american_samoa_alt_4,
                R.string.american_samoa_alt_5,
                R.string.american_samoa_alt_6,
                R.string.american_samoa_alt_7,
                R.string.american_samoa_alt_8,
                R.string.american_samoa_alt_9,
                R.string.american_samoa_alt_10,
                R.string.american_samoa_alt_11,
                R.string.american_samoa_alt_12,
                R.string.american_samoa_alt_13
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = "unitedStates",
            associatedState = null,
            categories = listOf(
                FlagCategory.AUTONOMOUS_REGION,
                FlagCategory.TERRITORY
            ),
        ),

        "andalusia" to FlagResources(
            id = 10,
            image = R.drawable.flag_of_andaluc_a,
            imagePreview = R.drawable.flag_of_andaluc_a_preview,
            flagOf = R.string.andalusia,
            flagOfOfficial = R.string.andalusia_official,
            flagOfAlternate = listOf(
                R.string.andalusia_alt_1,
                R.string.andalusia_alt_2
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = "spain",
            associatedState = null,
            categories = listOf(
                FlagCategory.AUTONOMOUS_REGION,
                FlagCategory.COMMUNITY,
                FlagCategory.DEVOLVED_GOVERNMENT
            ),
        ),

        "andorra" to FlagResources(
            id = 11,
            image = R.drawable.flag_of_andorra,
            imagePreview = R.drawable.flag_of_andorra_preview,
            flagOf = R.string.andorra,
            flagOfOfficial = R.string.andorra_official,
            flagOfAlternate = listOf(
                R.string.andorra_alt_1,
                R.string.andorra_alt_2,
                R.string.andorra_alt_3,
                R.string.andorra_alt_4
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.MONARCHY
            ),
        ),

        "angola" to FlagResources(
            id = 12,
            image = R.drawable.flag_of_angola,
            imagePreview = R.drawable.flag_of_angola,
            flagOf = R.string.angola,
            flagOfOfficial = R.string.angola_official,
            flagOfAlternate = listOf(
                R.string.angola_alt_1,
                R.string.angola_alt_2,
                R.string.angola_alt_3,
                R.string.angola_alt_4
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "anguilla" to FlagResources(
            id = 13,
            image = R.drawable.flag_of_anguilla,
            imagePreview = R.drawable.flag_of_anguilla,
            flagOf = R.string.anguilla,
            flagOfOfficial = R.string.anguilla_official,
            flagOfAlternate = listOf(
                R.string.anguilla_alt_1,
                R.string.anguilla_alt_2,
                R.string.anguilla_alt_3,
                R.string.anguilla_alt_4
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = "unitedKingdom",
            associatedState = null,
            categories = listOf(
                FlagCategory.AUTONOMOUS_REGION,
                FlagCategory.TERRITORY
            ),
        ),

        "antiguaAndBarbuda" to FlagResources(
            id = 14,
            image = R.drawable.flag_of_antigua_and_barbuda,
            imagePreview = R.drawable.flag_of_antigua_and_barbuda,
            flagOf = R.string.antigua_and_barbuda,
            flagOfOfficial = R.string.antigua_and_barbuda_official,
            flagOfAlternate = listOf(
                R.string.antigua_and_barbuda_alt_1,
                R.string.antigua_and_barbuda_alt_2,
                R.string.antigua_and_barbuda_alt_3,
                R.string.antigua_and_barbuda_alt_4,
                R.string.antigua_and_barbuda_alt_5
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.MONARCHY
            ),
        ),

        "arabLeague" to FlagResources(
            id = 15,
            image = R.drawable.flag_of_the_arab_league,
            imagePreview = R.drawable.flag_of_the_arab_league,
            flagOf = R.string.arab_league,
            flagOfOfficial = R.string.arab_league_official,
            flagOfAlternate = listOf(R.string.arab_league_alt),
            isFlagOfThe = true,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.INTERNATIONAL_ORGANIZATION,
                FlagCategory.DIRECTORIAL,
                FlagCategory.CONFEDERATION
            ),
        ),

        "aragon" to FlagResources(
            id = 16,
            image = R.drawable.flag_of_aragon,
            imagePreview = R.drawable.flag_of_aragon_preview,
            flagOf = R.string.aragon,
            flagOfOfficial = R.string.aragon_official,
            flagOfAlternate = null,
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = "spain",
            associatedState = null,
            categories = listOf(
                FlagCategory.AUTONOMOUS_REGION,
                FlagCategory.COMMUNITY,
                FlagCategory.DEVOLVED_GOVERNMENT
            ),
        ),

        "argentina" to FlagResources(
            id = 17,
            image = R.drawable.flag_of_argentina,
            imagePreview = R.drawable.flag_of_argentina,
            flagOf = R.string.argentina,
            flagOfOfficial = R.string.argentina_official,
            flagOfAlternate = listOf(
                R.string.argentina_alt_1,
                R.string.argentina_alt_2,
                R.string.argentina_alt_3
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.FEDERAL,
                FlagCategory.PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "armenia" to FlagResources(
            id = 18,
            image = R.drawable.flag_of_armenia,
            imagePreview = R.drawable.flag_of_armenia,
            flagOf = R.string.armenia,
            flagOfOfficial = R.string.armenia_official,
            flagOfAlternate = listOf(
                R.string.armenia_alt_1,
                R.string.armenia_alt_2,
                R.string.armenia_alt_3,
                R.string.armenia_alt_4,
                R.string.armenia_alt_5
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "artsakh" to FlagResources(
            id = 19,
            image = R.drawable.flag_of_artsakh,
            imagePreview = R.drawable.flag_of_artsakh,
            flagOf = R.string.artsakh,
            flagOfOfficial = R.string.artsakh_official,
            flagOfAlternate = listOf(
                R.string.artsakh_alt_1,
                R.string.artsakh_alt_2,
                R.string.artsakh_alt_3
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = "azerbaijan",
            associatedState = null,
            categories = listOf(
                FlagCategory.HISTORICAL,
                FlagCategory.AUTONOMOUS_REGION
            ),
        ),

        "aruba" to FlagResources(
            id = 20,
            image = R.drawable.flag_of_aruba,
            imagePreview = R.drawable.flag_of_aruba,
            flagOf = R.string.aruba,
            flagOfOfficial = R.string.aruba_official,
            flagOfAlternate = listOf(
                R.string.aruba_alt_1,
                R.string.aruba_alt_2,
                R.string.aruba_alt_3,
                R.string.aruba_alt_4,
                R.string.aruba_alt_5
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = "netherlands",
            associatedState = null,
            categories = listOf(
                FlagCategory.AUTONOMOUS_REGION,
                FlagCategory.COUNTRY
            ),
        ),

        "ascensionIsland" to FlagResources(
            id = 21,
            image = R.drawable.flag_of_ascension_island,
            imagePreview = R.drawable.flag_of_ascension_island_preview,
            flagOf = R.string.ascension_island,
            flagOfOfficial = R.string.ascension_island_official,
            flagOfAlternate = listOf(
                R.string.ascension_island_alt_1,
                R.string.ascension_island_alt_2,
                R.string.ascension_island_alt_3
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = "unitedKingdom",
            associatedState = null,
            categories = listOf(FlagCategory.TERRITORY),
        ),

        "asturias" to FlagResources(
            id = 22,
            image = R.drawable.flag_of_asturias,
            imagePreview = R.drawable.flag_of_asturias,
            flagOf = R.string.asturias,
            flagOfOfficial = R.string.asturias_official,
            flagOfAlternate = listOf(
                R.string.asturias_alt_1,
                R.string.asturias_alt_2,
                R.string.asturias_alt_3
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = "spain",
            associatedState = null,
            categories = listOf(
                FlagCategory.AUTONOMOUS_REGION,
                FlagCategory.COMMUNITY,
                FlagCategory.DEVOLVED_GOVERNMENT
            ),
        ),

        "australia" to FlagResources(
            id = 23,
            image = R.drawable.flag_of_australia__converted_,
            imagePreview = R.drawable.flag_of_australia__converted__preview,
            flagOf = R.string.australia,
            flagOfOfficial = R.string.australia_official,
            flagOfAlternate = listOf(
                R.string.australia_alt_1,
                R.string.australia_alt_2,
                R.string.australia_alt_3,
                R.string.australia_alt_4,
                R.string.australia_alt_5,
                R.string.australia_alt_6,
                R.string.australia_alt_7,
                R.string.australia_alt_8,
                R.string.australia_alt_9,
                R.string.australia_alt_10,
                R.string.australia_alt_11,
                R.string.australia_alt_12,
                R.string.australia_alt_13
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.FEDERAL,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.MONARCHY
            ),
        ),

        "austria" to FlagResources(
            id = 24,
            image = R.drawable.flag_of_austria,
            imagePreview = R.drawable.flag_of_austria,
            flagOf = R.string.austria,
            flagOfOfficial = R.string.austria_official,
            flagOfAlternate = listOf(
                R.string.austria_alt_1,
                R.string.austria_alt_2,
                R.string.austria_alt_3
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.FEDERAL,
                FlagCategory.SEMI_PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "azawad" to FlagResources(
            id = 25,
            image = R.drawable.mnla_flag,
            imagePreview = R.drawable.mnla_flag,
            flagOf = R.string.azawad,
            flagOfOfficial = R.string.azawad_official,
            flagOfAlternate = null,
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = "mali",
            associatedState = null,
            categories = listOf(
                FlagCategory.HISTORICAL,
                FlagCategory.AUTONOMOUS_REGION,
                FlagCategory.REGION,
                FlagCategory.THEOCRATIC
            ),
        ),

        "azerbaijan" to FlagResources(
            id = 26,
            image = R.drawable.flag_of_azerbaijan,
            imagePreview = R.drawable.flag_of_azerbaijan,
            flagOf = R.string.azerbaijan,
            flagOfOfficial = R.string.azerbaijan_official,
            flagOfAlternate = listOf(
                R.string.azerbaijan_alt_1,
                R.string.azerbaijan_alt_2,
                R.string.azerbaijan_alt_3,
                R.string.azerbaijan_alt_4,
                R.string.azerbaijan_alt_5
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.SEMI_PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "azores" to FlagResources(
            id = 27,
            image = R.drawable.flag_of_the_azores,
            imagePreview = R.drawable.flag_of_the_azores_preview,
            flagOf = R.string.azores,
            flagOfOfficial = R.string.azores_official,
            flagOfAlternate = listOf(
                R.string.azores_alt_1,
                R.string.azores_alt_2,
                R.string.azores_alt_3,
                R.string.azores_alt_4,
                R.string.azores_alt_5,
                R.string.azores_alt_6,
                R.string.azores_alt_7,
                R.string.azores_alt_8,
                R.string.azores_alt_9
            ),
            isFlagOfThe = true,
            isFlagOfOfficialThe = true,
            sovereignState = "portugal",
            associatedState = null,
            categories = listOf(FlagCategory.AUTONOMOUS_REGION),
        ),

        "bahrain" to FlagResources(
            id = 28,
            image = R.drawable.flag_of_bahrain,
            imagePreview = R.drawable.flag_of_bahrain,
            flagOf = R.string.bahrain,
            flagOfOfficial = R.string.bahrain_official,
            flagOfAlternate = listOf(
                R.string.bahrain_alt_1,
                R.string.bahrain_alt_2,
                R.string.bahrain_alt_3,
                R.string.bahrain_alt_4,
                R.string.bahrain_alt_5,
                R.string.bahrain_alt_6,
                R.string.bahrain_alt_7,
                R.string.bahrain_alt_8
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.MONARCHY
            ),
        ),

        "balearicIslands" to FlagResources(
            id = 29,
            image = R.drawable.flag_of_the_balearic_islands,
            imagePreview = R.drawable.flag_of_the_balearic_islands,
            flagOf = R.string.balearic_islands,
            flagOfOfficial = R.string.balearic_islands_official,
            flagOfAlternate = listOf(
                R.string.balearic_islands_alt_1,
                R.string.balearic_islands_alt_2
            ),
            isFlagOfThe = true,
            isFlagOfOfficialThe = true,
            sovereignState = "spain",
            associatedState = null,
            categories = listOf(
                FlagCategory.AUTONOMOUS_REGION,
                FlagCategory.COMMUNITY,
                FlagCategory.DEVOLVED_GOVERNMENT
            ),
        ),

        "bangladesh" to FlagResources(
            id = 30,
            image = R.drawable.flag_of_bangladesh,
            imagePreview = R.drawable.flag_of_bangladesh_preview,
            flagOf = R.string.bangladesh,
            flagOfOfficial = R.string.bangladesh_official,
            flagOfAlternate = listOf(
                R.string.bangladesh_alt_1,
                R.string.bangladesh_alt_2,
                R.string.bangladesh_alt_3,
                R.string.bangladesh_alt_4,
                R.string.bangladesh_alt_5,
                R.string.bangladesh_alt_6
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.PROVISIONAL_GOVERNMENT
            ),
        ),

        "barbados" to FlagResources(
            id = 31,
            image = R.drawable.flag_of_barbados,
            imagePreview = R.drawable.flag_of_barbados,
            flagOf = R.string.barbados,
            flagOfOfficial = R.string.barbados_official,
            flagOfAlternate = listOf(
                R.string.barbados_alt_1,
                R.string.barbados_alt_2,
                R.string.barbados_alt_3,
                R.string.barbados_alt_4,
                R.string.barbados_alt_5
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "basqueCountry" to FlagResources(
            id = 32,
            image = R.drawable.flag_of_the_basque_country,
            imagePreview = R.drawable.flag_of_the_basque_country,
            flagOf = R.string.basque_country,
            flagOfOfficial = R.string.basque_country_official,
            flagOfAlternate = listOf(
                R.string.basque_country_alt_1,
                R.string.basque_country_alt_2,
                R.string.basque_country_alt_3,
                R.string.basque_country_alt_4,
                R.string.basque_country_alt_5,
                R.string.basque_country_alt_6,
                R.string.basque_country_alt_7,
                R.string.basque_country_alt_8,
                R.string.basque_country_alt_9
            ),
            isFlagOfThe = true,
            isFlagOfOfficialThe = true,
            sovereignState = "spain",
            associatedState = null,
            categories = listOf(
                FlagCategory.AUTONOMOUS_REGION,
                FlagCategory.COMMUNITY,
                FlagCategory.DEVOLVED_GOVERNMENT
            ),
        ),

        "belarus" to FlagResources(
            id = 33,
            image = R.drawable.flag_of_belarus,
            imagePreview = R.drawable.flag_of_belarus,
            flagOf = R.string.belarus,
            flagOfOfficial = R.string.belarus_official,
            flagOfAlternate = listOf(
                R.string.belarus_alt_1,
                R.string.belarus_alt_2,
                R.string.belarus_alt_3,
                R.string.belarus_alt_4,
                R.string.belarus_alt_5,
                R.string.belarus_alt_6,
                R.string.belarus_alt_7,
                R.string.belarus_alt_8,
                R.string.belarus_alt_9,
                R.string.belarus_alt_10,
                R.string.belarus_alt_11,
                R.string.belarus_alt_12,
                R.string.belarus_alt_13
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.SEMI_PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "belgium" to FlagResources(
            id = 34,
            image = R.drawable.flag_of_belgium,
            imagePreview = R.drawable.flag_of_belgium,
            flagOf = R.string.belgium,
            flagOfOfficial = R.string.belgium_official,
            flagOfAlternate = listOf(
                R.string.belgium_alt_1,
                R.string.belgium_alt_2,
                R.string.belgium_alt_3,
                R.string.belgium_alt_4,
                R.string.belgium_alt_5
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.FEDERAL,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.MONARCHY
            ),
        ),

        "belize" to FlagResources(
            id = 35,
            image = R.drawable.flag_of_belize,
            imagePreview = R.drawable.flag_of_belize_preview,
            flagOf = R.string.belize,
            flagOfOfficial = R.string.belize_official,
            flagOfAlternate = listOf(R.string.belize_alt),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.MONARCHY
            ),
        ),

        "benin" to FlagResources(
            id = 36,
            image = R.drawable.flag_of_benin,
            imagePreview = R.drawable.flag_of_benin,
            flagOf = R.string.benin,
            flagOfOfficial = R.string.benin_official,
            flagOfAlternate = listOf(
                R.string.benin_alt_1,
                R.string.benin_alt_2,
                R.string.benin_alt_3,
                R.string.benin_alt_4,
                R.string.benin_alt_5
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "bermuda" to FlagResources(
            id = 37,
            image = R.drawable.flag_of_bermuda,
            imagePreview = R.drawable.flag_of_bermuda,
            flagOf = R.string.bermuda,
            flagOfOfficial = R.string.bermuda_official,
            flagOfAlternate = listOf(
                R.string.bermuda_alt_1,
                R.string.bermuda_alt_2,
                R.string.bermuda_alt_3,
                R.string.bermuda_alt_4,
                R.string.bermuda_alt_5,
                R.string.bermuda_alt_6,
                R.string.bermuda_alt_7,
                R.string.bermuda_alt_8,
                R.string.bermuda_alt_9,
                R.string.bermuda_alt_10,
                R.string.bermuda_alt_11,
                R.string.bermuda_alt_12,
                R.string.bermuda_alt_13,
                R.string.bermuda_alt_14,
                R.string.bermuda_alt_15,
                R.string.bermuda_alt_16,
                R.string.bermuda_alt_17,
                R.string.bermuda_alt_18,
                R.string.bermuda_alt_19,
                R.string.bermuda_alt_20,
                R.string.bermuda_alt_21,
                R.string.bermuda_alt_22
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = "unitedKingdom",
            associatedState = null,
            categories = listOf(
                FlagCategory.AUTONOMOUS_REGION,
                FlagCategory.TERRITORY
            ),
        ),

        "bhutan" to FlagResources(
            id = 38,
            image = R.drawable.flag_of_bhutan,
            imagePreview = R.drawable.flag_of_bhutan,
            flagOf = R.string.bhutan,
            flagOfOfficial = R.string.bhutan_official,
            flagOfAlternate = listOf(
                R.string.bhutan_alt_1,
                R.string.bhutan_alt_2,
                R.string.bhutan_alt_3
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.MONARCHY
            ),
        ),

        "bolivia" to FlagResources(
            id = 39,
            image = R.drawable.bandera_de_bolivia__estado_,
            imagePreview = R.drawable.bandera_de_bolivia__estado__preview,
            flagOf = R.string.bolivia,
            flagOfOfficial = R.string.bolivia_official,
            flagOfAlternate = listOf(
                R.string.bolivia_alt_1,
                R.string.bolivia_alt_2,
                R.string.bolivia_alt_3,
                R.string.bolivia_alt_4,
                R.string.bolivia_alt_5,
                R.string.bolivia_alt_6
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "bonaire" to FlagResources(
            id = 40,
            image = R.drawable.flag_of_bonaire,
            imagePreview = R.drawable.flag_of_bonaire,
            flagOf = R.string.bonaire,
            flagOfOfficial = R.string.bonaire_official,
            flagOfAlternate = listOf(R.string.bonaire_alt),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = "netherlands",
            associatedState = null,
            categories = listOf(FlagCategory.MUNICIPALITY),
        ),

        "bosniaAndHerzegovina" to FlagResources(
            id = 41,
            image = R.drawable.flag_of_bosnia_and_herzegovina,
            imagePreview = R.drawable.flag_of_bosnia_and_herzegovina,
            flagOf = R.string.bosnia_and_herzegovina,
            flagOfOfficial = R.string.bosnia_and_herzegovina_official,
            flagOfAlternate = listOf(
                R.string.bosnia_and_herzegovina_alt_1,
                R.string.bosnia_and_herzegovina_alt_2,
                R.string.bosnia_and_herzegovina_alt_3,
                R.string.bosnia_and_herzegovina_alt_4,
                R.string.bosnia_and_herzegovina_alt_5,
                R.string.bosnia_and_herzegovina_alt_6,
                R.string.bosnia_and_herzegovina_alt_7,
                R.string.bosnia_and_herzegovina_alt_8,
                R.string.bosnia_and_herzegovina_alt_9
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.FEDERAL,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC,
                FlagCategory.SEMI_PRESIDENTIAL
            ),
        ),

        "botswana" to FlagResources(
            id = 42,
            image = R.drawable.flag_of_botswana,
            imagePreview = R.drawable.flag_of_botswana,
            flagOf = R.string.botswana,
            flagOfOfficial = R.string.botswana_official,
            flagOfAlternate = listOf(
                R.string.botswana_alt_1,
                R.string.botswana_alt_2,
                R.string.botswana_alt_3,
                R.string.botswana_alt_4
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "bougainville" to FlagResources(
            id = 43,
            image = R.drawable.flag_of_bougainville,
            imagePreview = R.drawable.flag_of_bougainville,
            flagOf = R.string.bougainville,
            flagOfOfficial = R.string.bougainville_official,
            flagOfAlternate = listOf(
                R.string.bougainville_alt_1,
                R.string.bougainville_alt_2,
                R.string.bougainville_alt_3,
                R.string.bougainville_alt_4,
                R.string.bougainville_alt_5
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = "papuaNewGuinea",
            associatedState = null,
            categories = listOf(FlagCategory.AUTONOMOUS_REGION),
        ),

        "brazil" to FlagResources(
            id = 44,
            image = R.drawable.flag_of_brazil,
            imagePreview = R.drawable.flag_of_brazil_preview,
            flagOf = R.string.brazil,
            flagOfOfficial = R.string.brazil_official,
            flagOfAlternate = listOf(
                R.string.brazil_alt_1,
                R.string.brazil_alt_2,
                R.string.brazil_alt_3
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.FEDERAL,
                FlagCategory.PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "britishIndianOceanTerritory" to FlagResources(
            id = 45,
            image = R.drawable.flag_of_the_commissioner_of_the_british_indian_ocean_territory,
            imagePreview = R.drawable.flag_of_the_commissioner_of_the_british_indian_ocean_territory_preview,
            flagOf = R.string.british_indian_ocean_territory,
            flagOfOfficial = R.string.british_indian_ocean_territory_official,
            flagOfAlternate = listOf(
                R.string.british_indian_ocean_territory_alt_1,
                R.string.british_indian_ocean_territory_alt_2,
                R.string.british_indian_ocean_territory_alt_3,
                R.string.british_indian_ocean_territory_alt_4
            ),
            isFlagOfThe = true,
            isFlagOfOfficialThe = true,
            sovereignState = "unitedKingdom",
            associatedState = null,
            categories = listOf(FlagCategory.TERRITORY),
        ),

        "britishVirginIslands" to FlagResources(
            id = 46,
            image = R.drawable.flag_of_the_british_virgin_islands,
            imagePreview = R.drawable.flag_of_the_british_virgin_islands,
            flagOf = R.string.british_virgin_islands,
            flagOfOfficial = R.string.british_virgin_islands_official,
            flagOfAlternate = listOf(
                R.string.british_virgin_islands_alt_1,
                R.string.british_virgin_islands_alt_2,
                R.string.british_virgin_islands_alt_3,
                R.string.british_virgin_islands_alt_4,
                R.string.british_virgin_islands_alt_5,
                R.string.british_virgin_islands_alt_6
            ),
            isFlagOfThe = true,
            isFlagOfOfficialThe = true,
            sovereignState = "unitedKingdom",
            associatedState = null,
            categories = listOf(
                FlagCategory.AUTONOMOUS_REGION,
                FlagCategory.TERRITORY
            ),
        ),

        "brunei" to FlagResources(
            id = 47,
            image = R.drawable.flag_of_brunei,
            imagePreview = R.drawable.flag_of_brunei,
            flagOf = R.string.brunei,
            flagOfOfficial = R.string.brunei_official,
            flagOfAlternate = listOf(
                R.string.brunei_alt_1,
                R.string.brunei_alt_2,
                R.string.brunei_alt_3,
                R.string.brunei_alt_4,
                R.string.brunei_alt_5,
                R.string.brunei_alt_6
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.THEOCRATIC,
                FlagCategory.MONARCHY
            ),
        ),

        "bulgaria" to FlagResources(
            id = 48,
            image = R.drawable.flag_of_bulgaria,
            imagePreview = R.drawable.flag_of_bulgaria,
            flagOf = R.string.bulgaria,
            flagOfOfficial = R.string.bulgaria_official,
            flagOfAlternate = listOf(
                R.string.bulgaria_alt_1,
                R.string.bulgaria_alt_2,
                R.string.bulgaria_alt_3,
                R.string.bulgaria_alt_4
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "burkinaFaso" to FlagResources(
            id = 49,
            image = R.drawable.flag_of_burkina_faso,
            imagePreview = R.drawable.flag_of_burkina_faso,
            flagOf = R.string.burkina_faso,
            flagOfOfficial = R.string.burkina_faso_official,
            flagOfAlternate = listOf(
                R.string.burkina_faso_alt_1,
                R.string.burkina_faso_alt_2,
                R.string.burkina_faso_alt_3
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.SEMI_PRESIDENTIAL,
                FlagCategory.MILITARY_JUNTA
            ),
        ),

        "burundi" to FlagResources(
            id = 50,
            image = R.drawable.flag_of_burundi,
            imagePreview = R.drawable.flag_of_burundi,
            flagOf = R.string.burundi,
            flagOfOfficial = R.string.burundi_official,
            flagOfAlternate = listOf(
                R.string.burundi_alt_1,
                R.string.burundi_alt_2,
                R.string.burundi_alt_3,
                R.string.burundi_alt_4
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "cambodia" to FlagResources(
            id = 51,
            image = R.drawable.flag_of_cambodia,
            imagePreview = R.drawable.flag_of_cambodia,
            flagOf = R.string.cambodia,
            flagOfOfficial = R.string.cambodia_official,
            flagOfAlternate = listOf(
                R.string.cambodia_alt_1,
                R.string.cambodia_alt_2,
                R.string.cambodia_alt_3,
                R.string.cambodia_alt_4,
                R.string.cambodia_alt_5,
                R.string.cambodia_alt_6,
                R.string.cambodia_alt_7,
                R.string.cambodia_alt_8
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.MONARCHY
            ),
        ),

        "cameroon" to FlagResources(
            id = 52,
            image = R.drawable.flag_of_cameroon,
            imagePreview = R.drawable.flag_of_cameroon,
            flagOf = R.string.cameroon,
            flagOfOfficial = R.string.cameroon_official,
            flagOfAlternate = listOf(
                R.string.cameroon_alt_1,
                R.string.cameroon_alt_2,
                R.string.cameroon_alt_3,
                R.string.cameroon_alt_4
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "canada" to FlagResources(
            id = 53,
            image = R.drawable.flag_of_canada__pantone_,
            imagePreview = R.drawable.flag_of_canada__pantone_,
            flagOf = R.string.canada,
            flagOfOfficial = R.string.canada_official,
            flagOfAlternate = listOf(
                R.string.canada_alt_1,
                R.string.canada_alt_2,
                R.string.canada_alt_3,
                R.string.canada_alt_4
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.FEDERAL,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.MONARCHY
            ),
        ),

        "canaryIslands" to FlagResources(
            id = 54,
            image = R.drawable.flag_of_the_canary_islands__simple_,
            imagePreview = R.drawable.flag_of_the_canary_islands__simple_,
            flagOf = R.string.canary_islands,
            flagOfOfficial = R.string.canary_islands_official,
            flagOfAlternate = listOf(
                R.string.canary_islands_alt_1,
                R.string.canary_islands_alt_2,
                R.string.canary_islands_alt_3,
                R.string.canary_islands_alt_4,
                R.string.canary_islands_alt_5,
                R.string.canary_islands_alt_6
            ),
            isFlagOfThe = true,
            isFlagOfOfficialThe = true,
            sovereignState = "spain",
            associatedState = null,
            categories = listOf(
                FlagCategory.AUTONOMOUS_REGION,
                FlagCategory.COMMUNITY,
                FlagCategory.DEVOLVED_GOVERNMENT
            ),
        ),

        "cantabria" to FlagResources(
            id = 55,
            image = R.drawable.flag_of_cantabria__official_,
            imagePreview = R.drawable.flag_of_cantabria__official__preview,
            flagOf = R.string.cantabria,
            flagOfOfficial = R.string.cantabria_official,
            flagOfAlternate = listOf(R.string.cantabria_alt),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = "spain",
            associatedState = null,
            categories = listOf(
                FlagCategory.AUTONOMOUS_REGION,
                FlagCategory.COMMUNITY,
                FlagCategory.DEVOLVED_GOVERNMENT
            ),
        ),

        "capeVerde" to FlagResources(
            id = 56,
            image = R.drawable.flag_of_cape_verde,
            imagePreview = R.drawable.flag_of_cape_verde,
            flagOf = R.string.cape_verde,
            flagOfOfficial = R.string.cape_verde_official,
            flagOfAlternate = listOf(
                R.string.cape_verde_alt_1,
                R.string.cape_verde_alt_2,
                R.string.cape_verde_alt_3,
                R.string.cape_verde_alt_4,
                R.string.cape_verde_alt_5,
                R.string.cape_verde_alt_6,
                R.string.cape_verde_alt_7,
                R.string.cape_verde_alt_8
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.SEMI_PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "castileAndLeon" to FlagResources(
            id = 57,
            image = R.drawable.flag_of_castile_and_le_n,
            imagePreview = R.drawable.flag_of_castile_and_le_n,
            flagOf = R.string.castile_and_leon,
            flagOfOfficial = R.string.castile_and_leon_official,
            flagOfAlternate = listOf(
                R.string.castile_and_leon_alt_1,
                R.string.castile_and_leon_alt_2,
                R.string.castile_and_leon_alt_3
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = "spain",
            associatedState = null,
            categories = listOf(
                FlagCategory.AUTONOMOUS_REGION,
                FlagCategory.COMMUNITY,
                FlagCategory.DEVOLVED_GOVERNMENT
            ),
        ),

        "castillaLaMancha" to FlagResources(
            id = 58,
            image = R.drawable.flag_of_castile_la_mancha,
            imagePreview = R.drawable.flag_of_castile_la_mancha_preview,
            flagOf = R.string.castilla_la_mancha,
            flagOfOfficial = R.string.castilla_la_mancha_official,
            flagOfAlternate = listOf(
                R.string.castilla_la_mancha_alt_1,
                R.string.castilla_la_mancha_alt_2,
                R.string.castilla_la_mancha_alt_3,
                R.string.castilla_la_mancha_alt_4
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = "spain",
            associatedState = null,
            categories = listOf(
                FlagCategory.AUTONOMOUS_REGION,
                FlagCategory.COMMUNITY,
                FlagCategory.DEVOLVED_GOVERNMENT
            ),
        ),

        "catalonia" to FlagResources(
            id = 59,
            image = R.drawable.flag_of_catalonia,
            imagePreview = R.drawable.flag_of_catalonia,
            flagOf = R.string.catalonia,
            flagOfOfficial = R.string.catalonia_official,
            flagOfAlternate = listOf(
                R.string.catalonia_alt_1,
                R.string.catalonia_alt_2,
                R.string.catalonia_alt_3,
                R.string.catalonia_alt_4,
                R.string.catalonia_alt_5,
                R.string.catalonia_alt_6,
                R.string.catalonia_alt_7,
                R.string.catalonia_alt_8
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = "spain",
            associatedState = null,
            categories = listOf(
                FlagCategory.AUTONOMOUS_REGION,
                FlagCategory.COMMUNITY,
                FlagCategory.DEVOLVED_GOVERNMENT
            ),
        ),

        "caymanIslands" to FlagResources(
            id = 60,
            image = R.drawable.flag_of_the_cayman_islands,
            imagePreview = R.drawable.flag_of_the_cayman_islands_preview,
            flagOf = R.string.cayman_islands,
            flagOfOfficial = R.string.cayman_islands_official,
            flagOfAlternate = listOf(
                R.string.cayman_islands_alt_1,
                R.string.cayman_islands_alt_2,
                R.string.cayman_islands_alt_3,
                R.string.cayman_islands_alt_4
            ),
            isFlagOfThe = true,
            isFlagOfOfficialThe = true,
            sovereignState = "unitedKingdom",
            associatedState = null,
            categories = listOf(
                FlagCategory.AUTONOMOUS_REGION,
                FlagCategory.TERRITORY
            ),
        ),

        "centralAfricanRepublic" to FlagResources(
            id = 61,
            image = R.drawable.flag_of_the_central_african_republic,
            imagePreview = R.drawable.flag_of_the_central_african_republic,
            flagOf = R.string.central_african_republic,
            flagOfOfficial = R.string.central_african_republic_official,
            flagOfAlternate = listOf(
                R.string.central_african_republic_alt_1,
                R.string.central_african_republic_alt_2,
                R.string.central_african_republic_alt_3,
                R.string.central_african_republic_alt_4,
                R.string.central_african_republic_alt_5
            ),
            isFlagOfThe = true,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "ceuta" to FlagResources(
            id = 62,
            image = R.drawable.flag_of_ceuta,
            imagePreview = R.drawable.flag_of_ceuta_preview,
            flagOf = R.string.ceuta,
            flagOfOfficial = R.string.ceuta_official,
            flagOfAlternate = listOf(R.string.ceuta_alt),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = "spain",
            associatedState = null,
            categories = listOf(
                FlagCategory.AUTONOMOUS_REGION,
                FlagCategory.CITY,
                FlagCategory.DEVOLVED_GOVERNMENT
            ),
        ),

        "chad" to FlagResources(
            id = 63,
            image = R.drawable.flag_of_chad,
            imagePreview = R.drawable.flag_of_chad,
            flagOf = R.string.chad,
            flagOfOfficial = R.string.chad_official,
            flagOfAlternate = listOf(
                R.string.chad_alt_1,
                R.string.chad_alt_2,
                R.string.chad_alt_3,
                R.string.chad_alt_4,
                R.string.chad_alt_5
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.SEMI_PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "chile" to FlagResources(
            id = 64,
            image = R.drawable.flag_of_chile,
            imagePreview = R.drawable.flag_of_chile,
            flagOf = R.string.chile,
            flagOfOfficial = R.string.chile_official,
            flagOfAlternate = listOf(
                R.string.chile_alt_1,
                R.string.chile_alt_2,
                R.string.chile_alt_3,
                R.string.chile_alt_4,
                R.string.chile_alt_5
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "china" to FlagResources(
            id = 65,
            image = R.drawable.flag_of_the_people_s_republic_of_china,
            imagePreview = R.drawable.flag_of_the_people_s_republic_of_china,
            flagOf = R.string.china,
            flagOfOfficial = R.string.china_official,
            flagOfAlternate = listOf(
                R.string.china_alt_1,
                R.string.china_alt_2,
                R.string.china_alt_3,
                R.string.china_alt_4,
                R.string.china_alt_5,
                R.string.china_alt_6,
                R.string.china_alt_7,
                R.string.china_alt_8,
                R.string.china_alt_9,
                R.string.china_alt_10,
                R.string.china_alt_11,
                R.string.china_alt_12,
                R.string.china_alt_13,
                R.string.china_alt_14,
                R.string.china_alt_15
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.DUAL_EXECUTIVE,
                FlagCategory.SOCIALIST,
                FlagCategory.ONE_PARTY
            ),
        ),

        "christmasIsland" to FlagResources(
            id = 66,
            image = R.drawable.flag_of_christmas_island,
            imagePreview = R.drawable.flag_of_christmas_island,
            flagOf = R.string.christmas_island,
            flagOfOfficial = R.string.christmas_island_official,
            flagOfAlternate = listOf(
                R.string.christmas_island_alt_1,
                R.string.christmas_island_alt_2,
                R.string.christmas_island_alt_3,
                R.string.christmas_island_alt_4
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = "australia",
            associatedState = null,
            categories = listOf(FlagCategory.TERRITORY),
        ),

        "chuuk" to FlagResources(
            id = 67,
            image = R.drawable.flag_of_chuuk,
            imagePreview = R.drawable.flag_of_chuuk,
            flagOf = R.string.chuuk,
            flagOfOfficial = R.string.chuuk_official,
            flagOfAlternate = listOf(
                R.string.chuuk_alt_1,
                R.string.chuuk_alt_2,
                R.string.chuuk_alt_3
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = "micronesia",
            associatedState = null,
            categories = listOf(FlagCategory.STATE),
        ),

        "cocosKeelingIslands" to FlagResources(
            id = 68,
            image = R.drawable.flag_of_the_cocos__keeling__islands,
            imagePreview = R.drawable.flag_of_the_cocos__keeling__islands,
            flagOf = R.string.cocos_keeling_islands,
            flagOfOfficial = R.string.cocos_keeling_islands_official,
            flagOfAlternate = listOf(
                R.string.cocos_keeling_islands_alt_1,
                R.string.cocos_keeling_islands_alt_2,
                R.string.cocos_keeling_islands_alt_3,
                R.string.cocos_keeling_islands_alt_4
            ),
            isFlagOfThe = true,
            isFlagOfOfficialThe = true,
            sovereignState = "australia",
            associatedState = null,
            categories = listOf(FlagCategory.TERRITORY),
        ),

        "colombia" to FlagResources(
            id = 69,
            image = R.drawable.flag_of_colombia,
            imagePreview = R.drawable.flag_of_colombia,
            flagOf = R.string.colombia,
            flagOfOfficial = R.string.colombia_official,
            flagOfAlternate = listOf(
                R.string.colombia_alt_1,
                R.string.colombia_alt_2,
                R.string.colombia_alt_3,
                R.string.colombia_alt_4,
                R.string.colombia_alt_5,
                R.string.colombia_alt_6,
                R.string.colombia_alt_7
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "comoros" to FlagResources(
            id = 70,
            image = R.drawable.flag_of_the_comoros,
            imagePreview = R.drawable.flag_of_the_comoros,
            flagOf = R.string.comoros,
            flagOfOfficial = R.string.comoros_official,
            flagOfAlternate = listOf(
                R.string.comoros_alt_1,
                R.string.comoros_alt_2,
                R.string.comoros_alt_3,
                R.string.comoros_alt_4,
                R.string.comoros_alt_5,
                R.string.comoros_alt_6,
                R.string.comoros_alt_7,
                R.string.comoros_alt_8,
                R.string.comoros_alt_9,
                R.string.comoros_alt_10,
                R.string.comoros_alt_11
            ),
            isFlagOfThe = true,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "cookIslands" to FlagResources(
            id = 71,
            image = R.drawable.flag_of_the_cook_islands,
            imagePreview = R.drawable.flag_of_the_cook_islands,
            flagOf = R.string.cook_islands,
            flagOfOfficial = R.string.cook_islands_official,
            flagOfAlternate = listOf(
                R.string.cook_islands_alt_1,
                R.string.cook_islands_alt_2,
                R.string.cook_islands_alt_3,
                R.string.cook_islands_alt_4,
                R.string.cook_islands_alt_5
            ),
            isFlagOfThe = true,
            isFlagOfOfficialThe = true,
            sovereignState = "newZealand",
            associatedState = "newZealand",
            categories = listOf(
                FlagCategory.AUTONOMOUS_REGION,
                FlagCategory.REGION
            ),
        ),

        "cornwall" to FlagResources(
            id = 72,
            image = R.drawable.flag_of_cornwall,
            imagePreview = R.drawable.flag_of_cornwall,
            flagOf = R.string.cornwall,
            flagOfOfficial = R.string.cornwall_official,
            flagOfAlternate = listOf(R.string.cornwall_alt),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = "unitedKingdom",
            associatedState = null,
            categories = listOf(
                FlagCategory.ETHNIC,
                FlagCategory.COUNTY
            ),
        ),

        "costaRica" to FlagResources(
            id = 73,
            image = R.drawable.flag_of_costa_rica,
            imagePreview = R.drawable.flag_of_costa_rica,
            flagOf = R.string.costa_rica,
            flagOfOfficial = R.string.costa_rica_official,
            flagOfAlternate = listOf(
                R.string.costa_rica_alt_1,
                R.string.costa_rica_alt_2,
                R.string.costa_rica_alt_3,
                R.string.costa_rica_alt_4,
                R.string.costa_rica_alt_5
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "crimea" to FlagResources(
            id = 74,
            image = R.drawable.flag_of_crimea,
            imagePreview = R.drawable.flag_of_crimea,
            flagOf = R.string.crimea,
            flagOfOfficial = R.string.crimea_official,
            flagOfAlternate = listOf(
                R.string.crimea_alt_1,
                R.string.crimea_alt_2,
                R.string.crimea_alt_3,
                R.string.crimea_alt_4,
                R.string.crimea_alt_5,
                R.string.crimea_alt_6,
                R.string.crimea_alt_7,
                R.string.crimea_alt_8,
                R.string.crimea_alt_9,
                R.string.crimea_alt_10,
                R.string.crimea_alt_11
            ),
            isFlagOfThe = true,
            isFlagOfOfficialThe = true,
            sovereignState = "ukraine",
            associatedState = null,
            categories = listOf(FlagCategory.AUTONOMOUS_REGION),
        ),

        "croatia" to FlagResources(
            id = 75,
            image = R.drawable.flag_of_croatia,
            imagePreview = R.drawable.flag_of_croatia_preview,
            flagOf = R.string.croatia,
            flagOfOfficial = R.string.croatia_official,
            flagOfAlternate = listOf(
                R.string.croatia_alt_1,
                R.string.croatia_alt_2,
                R.string.croatia_alt_3,
                R.string.croatia_alt_4,
                R.string.croatia_alt_5
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "cuba" to FlagResources(
            id = 76,
            image = R.drawable.flag_of_cuba,
            imagePreview = R.drawable.flag_of_cuba,
            flagOf = R.string.cuba,
            flagOfOfficial = R.string.cuba_official,
            flagOfAlternate = listOf(
                R.string.cuba_alt_1,
                R.string.cuba_alt_2,
                R.string.cuba_alt_3
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.SOCIALIST,
                FlagCategory.ONE_PARTY
            ),
        ),

        "curacao" to FlagResources(
            id = 77,
            image = R.drawable.flag_of_cura_ao,
            imagePreview = R.drawable.flag_of_cura_ao,
            flagOf = R.string.curacao,
            flagOfOfficial = R.string.curacao_official,
            flagOfAlternate = listOf(
                R.string.curacao_alt_1,
                R.string.curacao_alt_2,
                R.string.curacao_alt_3,
                R.string.curacao_alt_4,
                R.string.curacao_alt_5,
                R.string.curacao_alt_6,
                R.string.curacao_alt_7,
                R.string.curacao_alt_8,
                R.string.curacao_alt_9,
                R.string.curacao_alt_10,
                R.string.curacao_alt_11,
                R.string.curacao_alt_12
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = "netherlands",
            associatedState = null,
            categories = listOf(
                FlagCategory.AUTONOMOUS_REGION,
                FlagCategory.COUNTRY
            ),
        ),

        "cyprus" to FlagResources(
            id = 78,
            image = R.drawable.flag_of_cyprus,
            imagePreview = R.drawable.flag_of_cyprus,
            flagOf = R.string.cyprus,
            flagOfOfficial = R.string.cyprus_official,
            flagOfAlternate = listOf(
                R.string.cyprus_alt_1,
                R.string.cyprus_alt_2,
                R.string.cyprus_alt_3,
                R.string.cyprus_alt_4,
                R.string.cyprus_alt_5
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "czechRepublic" to FlagResources(
            id = 79,
            image = R.drawable.flag_of_the_czech_republic,
            imagePreview = R.drawable.flag_of_the_czech_republic,
            flagOf = R.string.czech_republic,
            flagOfOfficial = R.string.czech_republic_official,
            flagOfAlternate = listOf(
                R.string.czech_republic_alt_1,
                R.string.czech_republic_alt_2,
                R.string.czech_republic_alt_3,
                R.string.czech_republic_alt_4
            ),
            isFlagOfThe = true,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "democraticRepublicOfTheCongo" to FlagResources(
            id = 80,
            image = R.drawable.flag_of_the_democratic_republic_of_the_congo,
            imagePreview = R.drawable.flag_of_the_democratic_republic_of_the_congo,
            flagOf = R.string.democratic_republic_of_the_congo,
            flagOfOfficial = R.string.democratic_republic_of_the_congo_official,
            flagOfAlternate = listOf(
                R.string.democratic_republic_of_the_congo_alt_1,
                R.string.democratic_republic_of_the_congo_alt_2,
                R.string.democratic_republic_of_the_congo_alt_3,
                R.string.democratic_republic_of_the_congo_alt_4,
                R.string.democratic_republic_of_the_congo_alt_5,
                R.string.democratic_republic_of_the_congo_alt_6,
                R.string.democratic_republic_of_the_congo_alt_7,
                R.string.democratic_republic_of_the_congo_alt_8,
                R.string.democratic_republic_of_the_congo_alt_9,
                R.string.democratic_republic_of_the_congo_alt_10,
                R.string.democratic_republic_of_the_congo_alt_11,
                R.string.democratic_republic_of_the_congo_alt_12,
                R.string.democratic_republic_of_the_congo_alt_13,
                R.string.democratic_republic_of_the_congo_alt_14
            ),
            isFlagOfThe = true,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.SEMI_PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "denmark" to FlagResources(
            id = 81,
            image = R.drawable.flag_of_denmark,
            imagePreview = R.drawable.flag_of_denmark_preview,
            flagOf = R.string.denmark,
            flagOfOfficial = R.string.denmark_official,
            flagOfAlternate = listOf(
                R.string.denmark_alt_1,
                R.string.denmark_alt_2,
                R.string.denmark_alt_3,
                R.string.denmark_alt_4,
                R.string.denmark_alt_5,
                R.string.denmark_alt_6,
                R.string.denmark_alt_7,
                R.string.denmark_alt_8,
                R.string.denmark_alt_9
            ),
            isFlagOfThe = true,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.MONARCHY
            ),
        ),

        "djibouti" to FlagResources(
            id = 82,
            image = R.drawable.flag_of_djibouti,
            imagePreview = R.drawable.flag_of_djibouti,
            flagOf = R.string.djibouti,
            flagOfOfficial = R.string.djibouti_official,
            flagOfAlternate = listOf(
                R.string.djibouti_alt_1,
                R.string.djibouti_alt_2,
                R.string.djibouti_alt_3,
                R.string.djibouti_alt_4,
                R.string.djibouti_alt_5
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "dominica" to FlagResources(
            id = 83,
            image = R.drawable.flag_of_dominica,
            imagePreview = R.drawable.flag_of_dominica_preview,
            flagOf = R.string.dominica,
            flagOfOfficial = R.string.dominica_official,
            flagOfAlternate = listOf(
                R.string.dominica_alt_1,
                R.string.dominica_alt_2,
                R.string.dominica_alt_3,
                R.string.dominica_alt_4,
                R.string.dominica_alt_5,
                R.string.dominica_alt_6,
                R.string.dominica_alt_7,
                R.string.dominica_alt_8,
                R.string.dominica_alt_9
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "dominicanRepublic" to FlagResources(
            id = 84,
            image = R.drawable.flag_of_the_dominican_republic,
            imagePreview = R.drawable.flag_of_the_dominican_republic_preview,
            flagOf = R.string.dominican_republic,
            flagOfOfficial = R.string.dominican_republic_official,
            flagOfAlternate = listOf(
                R.string.dominican_republic_alt_1,
                R.string.dominican_republic_alt_2,
                R.string.dominican_republic_alt_3,
                R.string.dominican_republic_alt_4,
                R.string.dominican_republic_alt_5,
                R.string.dominican_republic_alt_6,
                R.string.dominican_republic_alt_7,
                R.string.dominican_republic_alt_8
            ),
            isFlagOfThe = true,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "donetskOblast" to FlagResources(
            id = 85,
            image = R.drawable.flag_of_donetsk_oblast,
            imagePreview = R.drawable.flag_of_donetsk_oblast_preview,
            flagOf = R.string.donetsk_oblast,
            flagOfOfficial = R.string.donetsk_oblast_official,
            flagOfAlternate = listOf(
                R.string.donetsk_oblast_alt_1,
                R.string.donetsk_oblast_alt_2,
                R.string.donetsk_oblast_alt_3
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = "ukraine",
            associatedState = null,
            categories = listOf(FlagCategory.OBLAST),
        ),

        "donetskPeoplesRepublic" to FlagResources(
            id = 86,
            image = R.drawable.flag_of_donetsk_people_s_republic,
            imagePreview = R.drawable.flag_of_donetsk_people_s_republic_preview,
            flagOf = R.string.donetsk_peoples_republic,
            flagOfOfficial = R.string.donetsk_peoples_republic_official,
            flagOfAlternate = listOf(
                R.string.donetsk_peoples_republic_alt_1,
                R.string.donetsk_peoples_republic_alt_2,
                R.string.donetsk_peoples_republic_alt_3
            ),
            isFlagOfThe = true,
            isFlagOfOfficialThe = true,
            sovereignState = "ukraine",
            associatedState = null,
            categories = listOf(
                FlagCategory.AUTONOMOUS_REGION,
                FlagCategory.REPUBLIC_UNIT
            ),
        ),

        "eastTimor" to FlagResources(
            id = 87,
            image = R.drawable.flag_of_east_timor,
            imagePreview = R.drawable.flag_of_east_timor,
            flagOf = R.string.east_timor,
            flagOfOfficial = R.string.east_timor_official,
            flagOfAlternate = listOf(
                R.string.east_timor_alt_1,
                R.string.east_timor_alt_2,
                R.string.east_timor_alt_3
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.SEMI_PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "eastTurkestan" to FlagResources(
            id = 88,
            image = R.drawable.kokbayraq_flag,
            imagePreview = R.drawable.kokbayraq_flag,
            flagOf = R.string.east_turkestan,
            flagOfOfficial = R.string.east_turkestan_official,
            flagOfAlternate = listOf(
                R.string.east_turkestan_alt_1,
                R.string.east_turkestan_alt_2,
                R.string.east_turkestan_alt_3,
                R.string.east_turkestan_alt_4
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = "china",
            associatedState = null,
            categories = listOf(
                FlagCategory.ETHNIC,
                FlagCategory.POLITICAL,
                FlagCategory.REGION
            ),
        ),

        "easterIsland" to FlagResources(
            id = 89,
            image = R.drawable.flag_of_rapa_nui__chile,
            imagePreview = R.drawable.flag_of_rapa_nui__chile,
            flagOf = R.string.easter_island,
            flagOfOfficial = R.string.easter_island_official,
            flagOfAlternate = listOf(
                R.string.easter_island_alt_1,
                R.string.easter_island_alt_2,
                R.string.easter_island_alt_3,
                R.string.easter_island_alt_4,
                R.string.easter_island_alt_5,
                R.string.easter_island_alt_6
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = "chile",
            associatedState = null,
            categories = listOf(FlagCategory.PROVINCE),
        ),

        "ecuador" to FlagResources(
            id = 90,
            image = R.drawable.flag_of_ecuador,
            imagePreview = R.drawable.flag_of_ecuador_preview,
            flagOf = R.string.ecuador,
            flagOfOfficial = R.string.ecuador_official,
            flagOfAlternate = listOf(
                R.string.ecuador_alt_1,
                R.string.ecuador_alt_2,
                R.string.ecuador_alt_3
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "egypt" to FlagResources(
            id = 91,
            image = R.drawable.flag_of_egypt,
            imagePreview = R.drawable.flag_of_egypt_preview,
            flagOf = R.string.egypt,
            flagOfOfficial = R.string.egypt_official,
            flagOfAlternate = listOf(
                R.string.egypt_alt_1,
                R.string.egypt_alt_2,
                R.string.egypt_alt_3,
                R.string.egypt_alt_4,
                R.string.egypt_alt_5,
                R.string.egypt_alt_6
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.SEMI_PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "elSalvador" to FlagResources(
            id = 92,
            image = R.drawable.flag_of_el_salvador,
            imagePreview = R.drawable.flag_of_el_salvador_preview,
            flagOf = R.string.el_salvador,
            flagOfOfficial = R.string.el_salvador_official,
            flagOfAlternate = listOf(
                R.string.el_salvador_alt_1,
                R.string.el_salvador_alt_2,
                R.string.el_salvador_alt_3,
                R.string.el_salvador_alt_4,
                R.string.el_salvador_alt_5
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "england" to FlagResources(
            id = 93,
            image = R.drawable.flag_of_england,
            imagePreview = R.drawable.flag_of_england,
            flagOf = R.string.england,
            flagOfOfficial = R.string.england_official,
            flagOfAlternate = listOf(
                R.string.england_alt_1,
                R.string.england_alt_2,
                R.string.england_alt_3
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = "unitedKingdom",
            associatedState = null,
            categories = listOf(FlagCategory.COUNTRY),
        ),

        "equatorialGuinea" to FlagResources(
            id = 94,
            image = R.drawable.flag_of_equatorial_guinea,
            imagePreview = R.drawable.flag_of_equatorial_guinea,
            flagOf = R.string.equatorial_guinea,
            flagOfOfficial = R.string.equatorial_guinea_official,
            flagOfAlternate = listOf(
                R.string.equatorial_guinea_alt_1,
                R.string.equatorial_guinea_alt_2,
                R.string.equatorial_guinea_alt_3
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "eritrea" to FlagResources(
            id = 95,
            image = R.drawable.flag_of_eritrea,
            imagePreview = R.drawable.flag_of_eritrea,
            flagOf = R.string.eritrea,
            flagOfOfficial = R.string.eritrea_official,
            flagOfAlternate = listOf(
                R.string.eritrea_alt_1,
                R.string.eritrea_alt_2,
                R.string.eritrea_alt_3,
                R.string.eritrea_alt_4,
                R.string.eritrea_alt_5,
                R.string.eritrea_alt_6
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PRESIDENTIAL,
                FlagCategory.ONE_PARTY
            ),
        ),

        "estonia" to FlagResources(
            id = 96,
            image = R.drawable.flag_of_estonia,
            imagePreview = R.drawable.flag_of_estonia,
            flagOf = R.string.estonia,
            flagOfOfficial = R.string.estonia_official,
            flagOfAlternate = listOf(
                R.string.estonia_alt_1,
                R.string.estonia_alt_2,
                R.string.estonia_alt_3,
                R.string.estonia_alt_4,
                R.string.estonia_alt_5,
                R.string.estonia_alt_6
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "eswatini" to FlagResources(
            id = 97,
            image = R.drawable.flag_of_eswatini,
            imagePreview = R.drawable.flag_of_eswatini_preview,
            flagOf = R.string.eswatini,
            flagOfOfficial = R.string.eswatini_official,
            flagOfAlternate = listOf(
                R.string.eswatini_alt_1,
                R.string.eswatini_alt_2,
                R.string.eswatini_alt_3,
                R.string.eswatini_alt_4,
                R.string.eswatini_alt_5,
                R.string.eswatini_alt_6,
                R.string.eswatini_alt_7
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.MONARCHY
            ),
        ),

        "ethiopia" to FlagResources(
            id = 98,
            image = R.drawable.flag_of_ethiopia,
            imagePreview = R.drawable.flag_of_ethiopia,
            flagOf = R.string.ethiopia,
            flagOfOfficial = R.string.ethiopia_official,
            flagOfAlternate = listOf(
                R.string.ethiopia_alt_1,
                R.string.ethiopia_alt_2,
                R.string.ethiopia_alt_3
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.FEDERAL,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "europeanUnion" to FlagResources(
            id = 99,
            image = R.drawable.flag_of_europe,
            imagePreview = R.drawable.flag_of_europe_preview,
            flagOf = R.string.european_union,
            flagOfOfficial = R.string.european_union_official,
            flagOfAlternate = listOf(
                R.string.european_union_alt_1,
                R.string.european_union_alt_2,
                R.string.european_union_alt_3
            ),
            isFlagOfThe = true,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SUPRANATIONAL_UNION,
                FlagCategory.INTERNATIONAL_ORGANIZATION,
                FlagCategory.DIRECTORIAL,
                FlagCategory.CONFEDERATION
            ),
        ),

        "extremadura" to FlagResources(
            id = 100,
            image = R.drawable.flag_of_extremadura__spain__with_coat_of_arms_,
            imagePreview = R.drawable.flag_of_extremadura__spain__with_coat_of_arms__preview,
            flagOf = R.string.extremadura,
            flagOfOfficial = R.string.extremadura_official,
            flagOfAlternate = listOf(R.string.extremadura_alt),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = "spain",
            associatedState = null,
            categories = listOf(
                FlagCategory.AUTONOMOUS_REGION,
                FlagCategory.COMMUNITY,
                FlagCategory.DEVOLVED_GOVERNMENT
            ),
        ),

        "falklandIslands" to FlagResources(
            id = 101,
            image = R.drawable.flag_of_the_falkland_islands,
            imagePreview = R.drawable.flag_of_the_falkland_islands_preview,
            flagOf = R.string.falkland_islands,
            flagOfOfficial = R.string.falkland_islands_official,
            flagOfAlternate = listOf(
                R.string.falkland_islands_alt_1,
                R.string.falkland_islands_alt_2,
                R.string.falkland_islands_alt_3,
                R.string.falkland_islands_alt_4,
                R.string.falkland_islands_alt_5,
                R.string.falkland_islands_alt_6
            ),
            isFlagOfThe = true,
            isFlagOfOfficialThe = true,
            sovereignState = "unitedKingdom",
            associatedState = null,
            categories = listOf(
                FlagCategory.AUTONOMOUS_REGION,
                FlagCategory.TERRITORY
            ),
        ),

        "faroeIslands" to FlagResources(
            id = 102,
            image = R.drawable.flag_of_the_faroe_islands,
            imagePreview = R.drawable.flag_of_the_faroe_islands,
            flagOf = R.string.faroe_islands,
            flagOfOfficial = R.string.faroe_islands_official,
            flagOfAlternate = listOf(
                R.string.faroe_islands_alt_1,
                R.string.faroe_islands_alt_2,
                R.string.faroe_islands_alt_3,
                R.string.faroe_islands_alt_4,
                R.string.faroe_islands_alt_5,
                R.string.faroe_islands_alt_6,
                R.string.faroe_islands_alt_7,
                R.string.faroe_islands_alt_8,
                R.string.faroe_islands_alt_9,
                R.string.faroe_islands_alt_10,
                R.string.faroe_islands_alt_11,
                R.string.faroe_islands_alt_12,
                R.string.faroe_islands_alt_13,
                R.string.faroe_islands_alt_14,
                R.string.faroe_islands_alt_15,
                R.string.faroe_islands_alt_16,
                R.string.faroe_islands_alt_17,
                R.string.faroe_islands_alt_18,
                R.string.faroe_islands_alt_19,
                R.string.faroe_islands_alt_20,
                R.string.faroe_islands_alt_21,
                R.string.faroe_islands_alt_22,
                R.string.faroe_islands_alt_23,
                R.string.faroe_islands_alt_24,
                R.string.faroe_islands_alt_25,
                R.string.faroe_islands_alt_26,
                R.string.faroe_islands_alt_27,
                R.string.faroe_islands_alt_28,
                R.string.faroe_islands_alt_29,
                R.string.faroe_islands_alt_30,
                R.string.faroe_islands_alt_31,
                R.string.faroe_islands_alt_32,
                R.string.faroe_islands_alt_33,
                R.string.faroe_islands_alt_34,
                R.string.faroe_islands_alt_35,
                R.string.faroe_islands_alt_36
            ),
            isFlagOfThe = true,
            isFlagOfOfficialThe = true,
            sovereignState = "denmark",
            associatedState = null,
            categories = listOf(
                FlagCategory.AUTONOMOUS_REGION,
                FlagCategory.TERRITORY,
                FlagCategory.DEVOLVED_GOVERNMENT
            ),
        ),

        "fiji" to FlagResources(
            id = 103,
            image = R.drawable.flag_of_fiji,
            imagePreview = R.drawable.flag_of_fiji_preview,
            flagOf = R.string.fiji,
            flagOfOfficial = R.string.fiji_official,
            flagOfAlternate = listOf(
                R.string.fiji_alt_1,
                R.string.fiji_alt_2,
                R.string.fiji_alt_3,
                R.string.fiji_alt_4,
                R.string.fiji_alt_5,
                R.string.fiji_alt_6,
                R.string.fiji_alt_7,
                R.string.fiji_alt_8
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "finland" to FlagResources(
            id = 104,
            image = R.drawable.flag_of_finland,
            imagePreview = R.drawable.flag_of_finland,
            flagOf = R.string.finland,
            flagOfOfficial = R.string.finland_official,
            flagOfAlternate = listOf(
                R.string.finland_alt_1,
                R.string.finland_alt_2,
                R.string.finland_alt_3,
                R.string.finland_alt_4,
                R.string.finland_alt_5,
                R.string.finland_alt_6,
                R.string.finland_alt_7,
                R.string.finland_alt_8,
                R.string.finland_alt_9
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "france" to FlagResources(
            id = 105,
            image = R.drawable.flag_of_france,
            imagePreview = R.drawable.flag_of_france,
            flagOf = R.string.france,
            flagOfOfficial = R.string.france_official,
            flagOfAlternate = listOf(
                R.string.france_alt_1,
                R.string.france_alt_2,
                R.string.france_alt_3
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.SEMI_PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "frenchGuiana" to FlagResources(
            id = 106,
            image = R.drawable.flag_of_french_guiana,
            imagePreview = R.drawable.flag_of_french_guiana,
            flagOf = R.string.french_guiana,
            flagOfOfficial = R.string.french_guiana_official,
            flagOfAlternate = listOf(
                R.string.french_guiana_alt_1,
                R.string.french_guiana_alt_2,
                R.string.french_guiana_alt_3,
                R.string.french_guiana_alt_4,
                R.string.french_guiana_alt_5,
                R.string.french_guiana_alt_6
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = "france",
            associatedState = null,
            categories = listOf(FlagCategory.REGION),
        ),

        "frenchPolynesia" to FlagResources(
            id = 107,
            image = R.drawable.flag_of_french_polynesia,
            imagePreview = R.drawable.flag_of_french_polynesia,
            flagOf = R.string.french_polynesia,
            flagOfOfficial = R.string.french_polynesia_official,
            flagOfAlternate = listOf(
                R.string.french_polynesia_alt_1,
                R.string.french_polynesia_alt_2,
                R.string.french_polynesia_alt_3,
                R.string.french_polynesia_alt_4,
                R.string.french_polynesia_alt_5,
                R.string.french_polynesia_alt_6
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = "france",
            associatedState = null,
            categories = listOf(
                FlagCategory.AUTONOMOUS_REGION,
                FlagCategory.COUNTRY
            ),
        ),

        "frenchSouthernAndAntarcticLands" to FlagResources(
            id = 108,
            image = R.drawable.flag_of_the_french_southern_and_antarctic_lands,
            imagePreview = R.drawable.flag_of_the_french_southern_and_antarctic_lands,
            flagOf = R.string.french_southern_and_antarctic_lands,
            flagOfOfficial = R.string.french_southern_and_antarctic_lands_official,
            flagOfAlternate = listOf(
                R.string.french_southern_and_antarctic_lands_alt_1,
                R.string.french_southern_and_antarctic_lands_alt_2,
                R.string.french_southern_and_antarctic_lands_alt_3
            ),
            isFlagOfThe = true,
            isFlagOfOfficialThe = true,
            sovereignState = "france",
            associatedState = null,
            categories = listOf(FlagCategory.TERRITORY),
        ),

        "gabon" to FlagResources(
            id = 109,
            image = R.drawable.flag_of_gabon,
            imagePreview = R.drawable.flag_of_gabon,
            flagOf = R.string.gabon,
            flagOfOfficial = R.string.gabon_official,
            flagOfAlternate = listOf(
                R.string.gabon_alt_1,
                R.string.gabon_alt_2,
                R.string.gabon_alt_3,
                R.string.gabon_alt_4,
                R.string.gabon_alt_5,
                R.string.gabon_alt_6
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PRESIDENTIAL,
                FlagCategory.MILITARY_JUNTA
            ),
        ),

        "gagauzia" to FlagResources(
            id = 110,
            image = R.drawable.flag_of_gagauzia,
            imagePreview = R.drawable.flag_of_gagauzia_preview,
            flagOf = R.string.gagauzia,
            flagOfOfficial = R.string.gagauzia_official,
            flagOfAlternate = listOf(
                R.string.gagauzia_alt_1,
                R.string.gagauzia_alt_2
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = "moldova",
            associatedState = null,
            categories = listOf(
                FlagCategory.ETHNIC,
                FlagCategory.AUTONOMOUS_REGION,
                FlagCategory.TERRITORY,
                FlagCategory.DEVOLVED_GOVERNMENT
            ),
        ),

        "galiciaSpain" to FlagResources(
            id = 111,
            image = R.drawable.flag_of_galicia,
            imagePreview = R.drawable.flag_of_galicia_preview,
            flagOf = R.string.galicia_spain,
            flagOfOfficial = R.string.galicia_spain_official,
            flagOfAlternate = listOf(
                R.string.galicia_spain_alt_1,
                R.string.galicia_spain_alt_2
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = "spain",
            associatedState = null,
            categories = listOf(
                FlagCategory.AUTONOMOUS_REGION,
                FlagCategory.COMMUNITY,
                FlagCategory.DEVOLVED_GOVERNMENT
            ),
        ),

        "georgia" to FlagResources(
            id = 112,
            image = R.drawable.flag_of_georgia,
            imagePreview = R.drawable.flag_of_georgia,
            flagOf = R.string.georgia,
            flagOfOfficial = R.string.georgia_official,
            flagOfAlternate = listOf(
                R.string.georgia_alt_1,
                R.string.georgia_alt_2,
                R.string.georgia_alt_3
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "germany" to FlagResources(
            id = 113,
            image = R.drawable.flag_of_germany,
            imagePreview = R.drawable.flag_of_germany,
            flagOf = R.string.germany,
            flagOfOfficial = R.string.germany_official,
            flagOfAlternate = listOf(
                R.string.germany_alt_1,
                R.string.germany_alt_2,
                R.string.germany_alt_3,
                R.string.germany_alt_4
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.FEDERAL,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "ghana" to FlagResources(
            id = 114,
            image = R.drawable.flag_of_ghana,
            imagePreview = R.drawable.flag_of_ghana,
            flagOf = R.string.ghana,
            flagOfOfficial = R.string.ghana_official,
            flagOfAlternate = listOf(
                R.string.ghana_alt_1,
                R.string.ghana_alt_2,
                R.string.ghana_alt_3
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "gibraltar" to FlagResources(
            id = 115,
            image = R.drawable.flag_of_gibraltar,
            imagePreview = R.drawable.flag_of_gibraltar,
            flagOf = R.string.gibraltar,
            flagOfOfficial = R.string.gibraltar_official,
            flagOfAlternate = listOf(
                R.string.gibraltar_alt_1,
                R.string.gibraltar_alt_2,
                R.string.gibraltar_alt_3,
                R.string.gibraltar_alt_4
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = "unitedKingdom",
            associatedState = null,
            categories = listOf(
                FlagCategory.AUTONOMOUS_REGION,
                FlagCategory.TERRITORY
            ),
        ),

        "greece" to FlagResources(
            id = 116,
            image = R.drawable.flag_of_greece,
            imagePreview = R.drawable.flag_of_greece,
            flagOf = R.string.greece,
            flagOfOfficial = R.string.greece_official,
            flagOfAlternate = listOf(
                R.string.greece_alt_1,
                R.string.greece_alt_2,
                R.string.greece_alt_3,
                R.string.greece_alt_4,
                R.string.greece_alt_5,
                R.string.greece_alt_6,
                R.string.greece_alt_7
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "greenland" to FlagResources(
            id = 117,
            image = R.drawable.flag_of_greenland,
            imagePreview = R.drawable.flag_of_greenland,
            flagOf = R.string.greenland,
            flagOfOfficial = R.string.greenland_official,
            flagOfAlternate = listOf(
                R.string.greenland_alt_1,
                R.string.greenland_alt_2,
                R.string.greenland_alt_3
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = "denmark",
            associatedState = null,
            categories = listOf(
                FlagCategory.AUTONOMOUS_REGION,
                FlagCategory.TERRITORY,
                FlagCategory.DEVOLVED_GOVERNMENT
            ),
        ),

        "grenada" to FlagResources(
            id = 118,
            image = R.drawable.flag_of_grenada,
            imagePreview = R.drawable.flag_of_grenada,
            flagOf = R.string.grenada,
            flagOfOfficial = R.string.grenada_official,
            flagOfAlternate = listOf(
                R.string.grenada_alt_1,
                R.string.grenada_alt_2
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.MONARCHY
            ),
        ),

        "guadeloupe" to FlagResources(
            id = 119,
            image = R.drawable.unofficial_flag_of_guadeloupe__local_,
            imagePreview = R.drawable.unofficial_flag_of_guadeloupe__local_,
            flagOf = R.string.guadeloupe,
            flagOfOfficial = R.string.guadeloupe_official,
            flagOfAlternate = listOf(
                R.string.guadeloupe_alt_1,
                R.string.guadeloupe_alt_2,
                R.string.guadeloupe_alt_3,
                R.string.guadeloupe_alt_4,
                R.string.guadeloupe_alt_5,
                R.string.guadeloupe_alt_6
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = "france",
            associatedState = null,
            categories = listOf(FlagCategory.REGION),
        ),

        "guam" to FlagResources(
            id = 120,
            image = R.drawable.flag_of_guam,
            imagePreview = R.drawable.flag_of_guam,
            flagOf = R.string.guam,
            flagOfOfficial = R.string.guam_official,
            flagOfAlternate = listOf(
                R.string.guam_alt_1,
                R.string.guam_alt_2,
                R.string.guam_alt_3,
                R.string.guam_alt_4,
                R.string.guam_alt_5,
                R.string.guam_alt_6,
                R.string.guam_alt_7,
                R.string.guam_alt_8,
                R.string.guam_alt_9,
                R.string.guam_alt_10,
                R.string.guam_alt_11
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = "unitedStates",
            associatedState = null,
            categories = listOf(
                FlagCategory.AUTONOMOUS_REGION,
                FlagCategory.TERRITORY
            ),
        ),

        "guatemala" to FlagResources(
            id = 121,
            image = R.drawable.flag_of_guatemala,
            imagePreview = R.drawable.flag_of_guatemala_preview,
            flagOf = R.string.guatemala,
            flagOfOfficial = R.string.guatemala_official,
            flagOfAlternate = listOf(
                R.string.guatemala_alt_1,
                R.string.guatemala_alt_2,
                R.string.guatemala_alt_3
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "guernsey" to FlagResources(
            id = 122,
            image = R.drawable.flag_of_guernsey,
            imagePreview = R.drawable.flag_of_guernsey,
            flagOf = R.string.guernsey,
            flagOfOfficial = R.string.guernsey_official,
            flagOfAlternate = listOf(
                R.string.guernsey_alt_1,
                R.string.guernsey_alt_2,
                R.string.guernsey_alt_3,
                R.string.guernsey_alt_4,
                R.string.guernsey_alt_5,
                R.string.guernsey_alt_6,
                R.string.guernsey_alt_7,
                R.string.guernsey_alt_8,
                R.string.guernsey_alt_9,
                R.string.guernsey_alt_10,
                R.string.guernsey_alt_11
            ),
            isFlagOfThe = true,
            isFlagOfOfficialThe = true,
            sovereignState = "unitedKingdom",
            associatedState = null,
            categories = listOf(
                FlagCategory.AUTONOMOUS_REGION,
                FlagCategory.TERRITORY
            ),
        ),

        "guinea" to FlagResources(
            id = 123,
            image = R.drawable.flag_of_guinea,
            imagePreview = R.drawable.flag_of_guinea,
            flagOf = R.string.guinea,
            flagOfOfficial = R.string.guinea_official,
            flagOfAlternate = listOf(
                R.string.guinea_alt_1,
                R.string.guinea_alt_2,
                R.string.guinea_alt_3,
                R.string.guinea_alt_4
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PRESIDENTIAL,
                FlagCategory.MILITARY_JUNTA
            ),
        ),

        "guineaBissau" to FlagResources(
            id = 124,
            image = R.drawable.flag_of_guinea_bissau,
            imagePreview = R.drawable.flag_of_guinea_bissau,
            flagOf = R.string.guinea_bissau,
            flagOfOfficial = R.string.guinea_bissau_official,
            flagOfAlternate = listOf(
                R.string.guinea_bissau_alt_1,
                R.string.guinea_bissau_alt_2,
                R.string.guinea_bissau_alt_3
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.SEMI_PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "guyana" to FlagResources(
            id = 125,
            image = R.drawable.flag_of_guyana,
            imagePreview = R.drawable.flag_of_guyana,
            flagOf = R.string.guyana,
            flagOfOfficial = R.string.guyana_official,
            flagOfAlternate = listOf(
                R.string.guyana_alt_1,
                R.string.guyana_alt_2,
                R.string.guyana_alt_3,
                R.string.guyana_alt_4
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "haiti" to FlagResources(
            id = 126,
            image = R.drawable.flag_of_haiti,
            imagePreview = R.drawable.flag_of_haiti_preview,
            flagOf = R.string.haiti,
            flagOfOfficial = R.string.haiti_official,
            flagOfAlternate = listOf(
                R.string.haiti_alt_1,
                R.string.haiti_alt_2,
                R.string.haiti_alt_3,
                R.string.haiti_alt_4,
                R.string.haiti_alt_5,
                R.string.haiti_alt_6,
                R.string.haiti_alt_7
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.SEMI_PRESIDENTIAL,
                FlagCategory.PROVISIONAL_GOVERNMENT
            ),
        ),

        "honduras" to FlagResources(
            id = 127,
            image = R.drawable.flag_of_honduras,
            imagePreview = R.drawable.flag_of_honduras,
            flagOf = R.string.honduras,
            flagOfOfficial = R.string.honduras_official,
            flagOfAlternate = listOf(
                R.string.honduras_alt_1,
                R.string.honduras_alt_2,
                R.string.honduras_alt_3,
                R.string.honduras_alt_4,
                R.string.honduras_alt_5,
                R.string.honduras_alt_6
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "hongKong" to FlagResources(
            id = 128,
            image = R.drawable.flag_of_hong_kong,
            imagePreview = R.drawable.flag_of_hong_kong,
            flagOf = R.string.hong_kong,
            flagOfOfficial = R.string.hong_kong_official,
            flagOfAlternate = listOf(
                R.string.hong_kong_alt_1,
                R.string.hong_kong_alt_2,
                R.string.hong_kong_alt_3,
                R.string.hong_kong_alt_4,
                R.string.hong_kong_alt_5,
                R.string.hong_kong_alt_6,
                R.string.hong_kong_alt_7,
                R.string.hong_kong_alt_8,
                R.string.hong_kong_alt_9,
                R.string.hong_kong_alt_10,
                R.string.hong_kong_alt_11
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = "china",
            associatedState = null,
            categories = listOf(FlagCategory.REGION),
        ),

        "hungary" to FlagResources(
            id = 129,
            image = R.drawable.flag_of_hungary,
            imagePreview = R.drawable.flag_of_hungary,
            flagOf = R.string.hungary,
            flagOfOfficial = R.string.hungary_official,
            flagOfAlternate = listOf(
                R.string.hungary_alt_1,
                R.string.hungary_alt_2,
                R.string.hungary_alt_3
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "iceland" to FlagResources(
            id = 130,
            image = R.drawable.flag_of_iceland,
            imagePreview = R.drawable.flag_of_iceland,
            flagOf = R.string.iceland,
            flagOfOfficial = R.string.iceland_official,
            flagOfAlternate = listOf(
                R.string.iceland_alt_1,
                R.string.iceland_alt_2
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "india" to FlagResources(
            id = 131,
            image = R.drawable.flag_of_india,
            imagePreview = R.drawable.flag_of_india,
            flagOf = R.string.india,
            flagOfOfficial = R.string.india_official,
            flagOfAlternate = listOf(
                R.string.india_alt_1,
                R.string.india_alt_2,
                R.string.india_alt_3,
                R.string.india_alt_4,
                R.string.india_alt_5,
                R.string.india_alt_6,
                R.string.india_alt_7,
                R.string.india_alt_8,
                R.string.india_alt_9,
                R.string.india_alt_10
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.FEDERAL,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "indonesia" to FlagResources(
            id = 132,
            image = R.drawable.flag_of_indonesia,
            imagePreview = R.drawable.flag_of_indonesia,
            flagOf = R.string.indonesia,
            flagOfOfficial = R.string.indonesia_official,
            flagOfAlternate = listOf(
                R.string.indonesia_alt_1,
                R.string.indonesia_alt_2,
                R.string.indonesia_alt_3,
                R.string.indonesia_alt_4,
                R.string.indonesia_alt_5
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "iran" to FlagResources(
            id = 133,
            image = R.drawable.flag_of_iran__official_,
            imagePreview = R.drawable.flag_of_iran__official__preview,
            flagOf = R.string.iran,
            flagOfOfficial = R.string.iran_official,
            flagOfAlternate = listOf(
                R.string.iran_alt_1,
                R.string.iran_alt_2,
                R.string.iran_alt_3,
                R.string.iran_alt_4,
                R.string.iran_alt_5,
                R.string.iran_alt_6
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.DUAL_EXECUTIVE,
                FlagCategory.THEOCRACY
            ),
        ),

        "iraq" to FlagResources(
            id = 134,
            image = R.drawable.flag_of_iraq,
            imagePreview = R.drawable.flag_of_iraq,
            flagOf = R.string.iraq,
            flagOfOfficial = R.string.iraq_official,
            flagOfAlternate = listOf(
                R.string.iraq_alt_1,
                R.string.iraq_alt_2,
                R.string.iraq_alt_3
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.FEDERAL,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "ireland" to FlagResources(
            id = 135,
            image = R.drawable.flag_of_ireland,
            imagePreview = R.drawable.flag_of_ireland,
            flagOf = R.string.ireland,
            flagOfOfficial = R.string.ireland_official,
            flagOfAlternate = listOf(
                R.string.ireland_alt_1,
                R.string.ireland_alt_2,
                R.string.ireland_alt_3,
                R.string.ireland_alt_4,
                R.string.ireland_alt_5
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "isleOfMan" to FlagResources(
            id = 136,
            image = R.drawable.flag_of_the_isle_of_mann,
            imagePreview = R.drawable.flag_of_the_isle_of_mann,
            flagOf = R.string.isle_of_man,
            flagOfOfficial = R.string.isle_of_man_official,
            flagOfAlternate = listOf(
                R.string.isle_of_man_alt_1,
                R.string.isle_of_man_alt_2,
                R.string.isle_of_man_alt_3,
                R.string.isle_of_man_alt_4,
                R.string.isle_of_man_alt_5,
                R.string.isle_of_man_alt_6,
                R.string.isle_of_man_alt_7,
                R.string.isle_of_man_alt_8,
                R.string.isle_of_man_alt_9
            ),
            isFlagOfThe = true,
            isFlagOfOfficialThe = true,
            sovereignState = "unitedKingdom",
            associatedState = null,
            categories = listOf(
                FlagCategory.AUTONOMOUS_REGION,
                FlagCategory.TERRITORY
            ),
        ),

        "israel" to FlagResources(
            id = 137,
            image = R.drawable.flag_of_israel,
            imagePreview = R.drawable.flag_of_israel_preview,
            flagOf = R.string.israel,
            flagOfOfficial = R.string.israel_official,
            flagOfAlternate = listOf(
                R.string.israel_alt_1,
                R.string.israel_alt_2,
                R.string.israel_alt_3,
                R.string.israel_alt_4,
                R.string.israel_alt_5
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "italy" to FlagResources(
            id = 138,
            image = R.drawable.flag_of_italy,
            imagePreview = R.drawable.flag_of_italy,
            flagOf = R.string.italy,
            flagOfOfficial = R.string.italy_official,
            flagOfAlternate = listOf(
                R.string.italy_alt_1,
                R.string.italy_alt_2,
                R.string.italy_alt_3,
                R.string.italy_alt_4,
                R.string.italy_alt_5,
                R.string.italy_alt_6,
                R.string.italy_alt_7
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "ivoryCoast" to FlagResources(
            id = 139,
            image = R.drawable.flag_of_c_te_d_ivoire,
            imagePreview = R.drawable.flag_of_c_te_d_ivoire,
            flagOf = R.string.ivory_coast,
            flagOfOfficial = R.string.ivory_coast_official,
            flagOfAlternate = listOf(
                R.string.ivory_coast_alt_1,
                R.string.ivory_coast_alt_2,
                R.string.ivory_coast_alt_3,
                R.string.ivory_coast_alt_4,
                R.string.ivory_coast_alt_5,
                R.string.ivory_coast_alt_6,
                R.string.ivory_coast_alt_7,
                R.string.ivory_coast_alt_8,
                R.string.ivory_coast_alt_9,
                R.string.ivory_coast_alt_10,
                R.string.ivory_coast_alt_11,
                R.string.ivory_coast_alt_12,
                R.string.ivory_coast_alt_13,
                R.string.ivory_coast_alt_14
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "jamaica" to FlagResources(
            id = 140,
            image = R.drawable.flag_of_jamaica,
            imagePreview = R.drawable.flag_of_jamaica,
            flagOf = R.string.jamaica,
            flagOfOfficial = R.string.jamaica_official,
            flagOfAlternate = listOf(
                R.string.jamaica_alt_1,
                R.string.jamaica_alt_2,
                R.string.jamaica_alt_3,
                R.string.jamaica_alt_4
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.MONARCHY
            ),
        ),

        "japan" to FlagResources(
            id = 141,
            image = R.drawable.flag_of_japan,
            imagePreview = R.drawable.flag_of_japan,
            flagOf = R.string.japan,
            flagOfOfficial = R.string.japan_official,
            flagOfAlternate = listOf(
                R.string.japan_alt_1,
                R.string.japan_alt_2,
                R.string.japan_alt_3,
                R.string.japan_alt_4,
                R.string.japan_alt_5,
                R.string.japan_alt_6,
                R.string.japan_alt_7,
                R.string.japan_alt_8,
                R.string.japan_alt_9,
                R.string.japan_alt_10,
                R.string.japan_alt_11,
                R.string.japan_alt_12,
                R.string.japan_alt_13,
                R.string.japan_alt_14,
                R.string.japan_alt_15,
                R.string.japan_alt_16
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.MONARCHY
            ),
        ),

        "jersey" to FlagResources(
            id = 142,
            image = R.drawable.flag_of_jersey,
            imagePreview = R.drawable.flag_of_jersey_preview,
            flagOf = R.string.jersey,
            flagOfOfficial = R.string.jersey_official,
            flagOfAlternate = listOf(
                R.string.jersey_alt_1,
                R.string.jersey_alt_2,
                R.string.jersey_alt_3,
                R.string.jersey_alt_4,
                R.string.jersey_alt_5,
                R.string.jersey_alt_6,
                R.string.jersey_alt_7,
                R.string.jersey_alt_8,
                R.string.jersey_alt_9,
                R.string.jersey_alt_10,
                R.string.jersey_alt_11,
                R.string.jersey_alt_12,
                R.string.jersey_alt_13,
                R.string.jersey_alt_14,
                R.string.jersey_alt_15
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = "unitedKingdom",
            associatedState = null,
            categories = listOf(
                FlagCategory.AUTONOMOUS_REGION,
                FlagCategory.TERRITORY
            ),
        ),

        "jordan" to FlagResources(
            id = 143,
            image = R.drawable.flag_of_jordan,
            imagePreview = R.drawable.flag_of_jordan,
            flagOf = R.string.jordan,
            flagOfOfficial = R.string.jordan_official,
            flagOfAlternate = listOf(
                R.string.jordan_alt_1,
                R.string.jordan_alt_2,
                R.string.jordan_alt_3,
                R.string.jordan_alt_4,
                R.string.jordan_alt_5,
                R.string.jordan_alt_6,
                R.string.jordan_alt_7,
                R.string.jordan_alt_8,
                R.string.jordan_alt_9,
                R.string.jordan_alt_10
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.MONARCHY
            ),
        ),

        "kanakAndSocialistNationalLiberationFront" to FlagResources(
            id = 144,
            image = R.drawable.flag_of_flnks,
            imagePreview = R.drawable.flag_of_flnks,
            flagOf = R.string.kanak_and_socialist_national_liberation_front,
            flagOfOfficial = R.string.kanak_and_socialist_national_liberation_front_official,
            flagOfAlternate = listOf(R.string.kanak_and_socialist_national_liberation_front_alt),
            isFlagOfThe = true,
            isFlagOfOfficialThe = true,
            sovereignState = "france",
            associatedState = null,
            categories = listOf(FlagCategory.POLITICAL),
        ),

        "kazakhstan" to FlagResources(
            id = 145,
            image = R.drawable.flag_of_kazakhstan,
            imagePreview = R.drawable.flag_of_kazakhstan,
            flagOf = R.string.kazakhstan,
            flagOfOfficial = R.string.kazakhstan_official,
            flagOfAlternate = listOf(
                R.string.kazakhstan_alt_1,
                R.string.kazakhstan_alt_2,
                R.string.kazakhstan_alt_3,
                R.string.kazakhstan_alt_4,
                R.string.kazakhstan_alt_5
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.SEMI_PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "kenya" to FlagResources(
            id = 146,
            image = R.drawable.flag_of_kenya,
            imagePreview = R.drawable.flag_of_kenya,
            flagOf = R.string.kenya,
            flagOfOfficial = R.string.kenya_official,
            flagOfAlternate = listOf(
                R.string.kenya_alt_1,
                R.string.kenya_alt_2,
                R.string.kenya_alt_3,
                R.string.kenya_alt_4
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "kiribati" to FlagResources(
            id = 147,
            image = R.drawable.flag_of_kiribati,
            imagePreview = R.drawable.flag_of_kiribati_preview,
            flagOf = R.string.kiribati,
            flagOfOfficial = R.string.kiribati_official,
            flagOfAlternate = listOf(
                R.string.kiribati_alt_1,
                R.string.kiribati_alt_2,
                R.string.kiribati_alt_3
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "knightsTemplar" to FlagResources(
            id = 148,
            image = R.drawable.bandeira_templ_ria,
            imagePreview = R.drawable.bandeira_templ_ria,
            flagOf = R.string.knights_templar,
            flagOfOfficial = R.string.knights_templar_official,
            flagOfAlternate = listOf(
                R.string.knights_templar_alt_1,
                R.string.knights_templar_alt_2,
                R.string.knights_templar_alt_3,
                R.string.knights_templar_alt_4
            ),
            isFlagOfThe = true,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.HISTORICAL,
                FlagCategory.RELIGIOUS
            ),
        ),

        "kosovo" to FlagResources(
            id = 149,
            image = R.drawable.flag_of_kosovo,
            imagePreview = R.drawable.flag_of_kosovo,
            flagOf = R.string.kosovo,
            flagOfOfficial = R.string.kosovo_official,
            flagOfAlternate = listOf(
                R.string.kosovo_alt_1,
                R.string.kosovo_alt_2,
                R.string.kosovo_alt_3,
                R.string.kosovo_alt_4,
                R.string.kosovo_alt_5,
                R.string.kosovo_alt_6,
                R.string.kosovo_alt_7,
                R.string.kosovo_alt_8
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "kosrae" to FlagResources(
            id = 150,
            image = R.drawable.flag_of_kosrae,
            imagePreview = R.drawable.flag_of_kosrae,
            flagOf = R.string.kosrae,
            flagOfOfficial = R.string.kosrae_official,
            flagOfAlternate = null,
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = "micronesia",
            associatedState = null,
            categories = listOf(FlagCategory.STATE),
        ),

        "kurdistan" to FlagResources(
            id = 151,
            image = R.drawable.flag_of_kurdistan,
            imagePreview = R.drawable.flag_of_kurdistan,
            flagOf = R.string.kurdistan,
            flagOfOfficial = R.string.kurdistan_official,
            flagOfAlternate = listOf(
                R.string.kurdistan_alt_1,
                R.string.kurdistan_alt_2,
                R.string.kurdistan_alt_3,
                R.string.kurdistan_alt_4
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = "iraq",
            associatedState = null,
            categories = listOf(FlagCategory.AUTONOMOUS_REGION),
        ),

        "kuwait" to FlagResources(
            id = 152,
            image = R.drawable.flag_of_kuwait,
            imagePreview = R.drawable.flag_of_kuwait,
            flagOf = R.string.kuwait,
            flagOfOfficial = R.string.kuwait_official,
            flagOfAlternate = listOf(
                R.string.kuwait_alt_1,
                R.string.kuwait_alt_2,
                R.string.kuwait_alt_3
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PRESIDENTIAL,
                FlagCategory.MONARCHY
            ),
        ),

        "kyrgyzstan" to FlagResources(
            id = 153,
            image = R.drawable.flag_of_kyrgyzstan,
            imagePreview = R.drawable.flag_of_kyrgyzstan,
            flagOf = R.string.kyrgyzstan,
            flagOfOfficial = R.string.kyrgyzstan_official,
            flagOfAlternate = listOf(
                R.string.kyrgyzstan_alt_1,
                R.string.kyrgyzstan_alt_2,
                R.string.kyrgyzstan_alt_3
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "lgbtq" to FlagResources(
            id = 154,
            image = R.drawable.gay_pride_flag,
            imagePreview = R.drawable.gay_pride_flag,
            flagOf = R.string.lgbtq,
            flagOfOfficial = R.string.lgbtq_official,
            flagOfAlternate = listOf(
                R.string.lgbtq_alt_1,
                R.string.lgbtq_alt_2,
                R.string.lgbtq_alt_3,
                R.string.lgbtq_alt_4,
                R.string.lgbtq_alt_5,
                R.string.lgbtq_alt_6,
                R.string.lgbtq_alt_7,
                R.string.lgbtq_alt_8,
                R.string.lgbtq_alt_9,
                R.string.lgbtq_alt_10,
                R.string.lgbtq_alt_11,
                R.string.lgbtq_alt_12,
                R.string.lgbtq_alt_13,
                R.string.lgbtq_alt_14,
                R.string.lgbtq_alt_15,
                R.string.lgbtq_alt_16,
                R.string.lgbtq_alt_17,
                R.string.lgbtq_alt_18,
                R.string.lgbtq_alt_19,
                R.string.lgbtq_alt_20,
                R.string.lgbtq_alt_21,
                R.string.lgbtq_alt_22,
                R.string.lgbtq_alt_23
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = null,
            associatedState = null,
            categories = listOf(FlagCategory.SOCIAL),
        ),

        "laRioja" to FlagResources(
            id = 155,
            image = R.drawable.flag_of_la_rioja__with_coat_of_arms_,
            imagePreview = R.drawable.flag_of_la_rioja__with_coat_of_arms__preview,
            flagOf = R.string.la_rioja,
            flagOfOfficial = R.string.la_rioja_official,
            flagOfAlternate = listOf(
                R.string.la_rioja_alt_1,
                R.string.la_rioja_alt_2
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = "spain",
            associatedState = null,
            categories = listOf(
                FlagCategory.AUTONOMOUS_REGION,
                FlagCategory.COMMUNITY,
                FlagCategory.DEVOLVED_GOVERNMENT
            ),
        ),

        "laos" to FlagResources(
            id = 156,
            image = R.drawable.flag_of_laos,
            imagePreview = R.drawable.flag_of_laos,
            flagOf = R.string.laos,
            flagOfOfficial = R.string.laos_official,
            flagOfAlternate = listOf(
                R.string.laos_alt_1,
                R.string.laos_alt_2,
                R.string.laos_alt_3,
                R.string.laos_alt_4
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.SOCIALIST,
                FlagCategory.ONE_PARTY
            ),
        ),

        "latvia" to FlagResources(
            id = 157,
            image = R.drawable.flag_of_latvia,
            imagePreview = R.drawable.flag_of_latvia,
            flagOf = R.string.latvia,
            flagOfOfficial = R.string.latvia_official,
            flagOfAlternate = listOf(
                R.string.latvia_alt_1,
                R.string.latvia_alt_2,
                R.string.latvia_alt_3,
                R.string.latvia_alt_4,
                R.string.latvia_alt_5,
                R.string.latvia_alt_6,
                R.string.latvia_alt_7,
                R.string.latvia_alt_8,
                R.string.latvia_alt_9,
                R.string.latvia_alt_10,
                R.string.latvia_alt_11
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "lebanon" to FlagResources(
            id = 158,
            image = R.drawable.flag_of_lebanon,
            imagePreview = R.drawable.flag_of_lebanon,
            flagOf = R.string.lebanon,
            flagOfOfficial = R.string.lebanon_official,
            flagOfAlternate = listOf(
                R.string.lebanon_alt_1,
                R.string.lebanon_alt_2,
                R.string.lebanon_alt_3,
                R.string.lebanon_alt_4,
                R.string.lebanon_alt_5,
                R.string.lebanon_alt_6,
                R.string.lebanon_alt_7
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "lesotho" to FlagResources(
            id = 159,
            image = R.drawable.flag_of_lesotho,
            imagePreview = R.drawable.flag_of_lesotho,
            flagOf = R.string.lesotho,
            flagOfOfficial = R.string.lesotho_official,
            flagOfAlternate = listOf(
                R.string.lesotho_alt_1,
                R.string.lesotho_alt_2,
                R.string.lesotho_alt_3,
                R.string.lesotho_alt_4
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.MONARCHY
            ),
        ),

        "liberia" to FlagResources(
            id = 160,
            image = R.drawable.flag_of_liberia,
            imagePreview = R.drawable.flag_of_liberia,
            flagOf = R.string.liberia,
            flagOfOfficial = R.string.liberia_official,
            flagOfAlternate = listOf(
                R.string.liberia_alt_1,
                R.string.liberia_alt_2,
                R.string.liberia_alt_3,
                R.string.liberia_alt_4
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "libya" to FlagResources(
            id = 161,
            image = R.drawable.flag_of_libya,
            imagePreview = R.drawable.flag_of_libya,
            flagOf = R.string.libya,
            flagOfOfficial = R.string.libya_official,
            flagOfAlternate = listOf(
                R.string.libya_alt_1,
                R.string.libya_alt_2,
                R.string.libya_alt_3
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.THEOCRATIC,
                FlagCategory.PROVISIONAL_GOVERNMENT
            ),
        ),

        "liechtenstein" to FlagResources(
            id = 162,
            image = R.drawable.flag_of_liechtenstein,
            imagePreview = R.drawable.flag_of_liechtenstein,
            flagOf = R.string.liechtenstein,
            flagOfOfficial = R.string.liechtenstein_official,
            flagOfAlternate = listOf(
                R.string.liechtenstein_alt_1,
                R.string.liechtenstein_alt_2,
                R.string.liechtenstein_alt_3,
                R.string.liechtenstein_alt_4,
                R.string.liechtenstein_alt_5
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.MONARCHY
            ),
        ),

        "lithuania" to FlagResources(
            id = 163,
            image = R.drawable.flag_of_lithuania,
            imagePreview = R.drawable.flag_of_lithuania,
            flagOf = R.string.lithuania,
            flagOfOfficial = R.string.lithuania_official,
            flagOfAlternate = listOf(
                R.string.lithuania_alt_1,
                R.string.lithuania_alt_2,
                R.string.lithuania_alt_3,
                R.string.lithuania_alt_4,
                R.string.lithuania_alt_5
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.SEMI_PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "luhanskOblast" to FlagResources(
            id = 164,
            image = R.drawable.flag_of_luhansk_oblast,
            imagePreview = R.drawable.flag_of_luhansk_oblast_preview,
            flagOf = R.string.luhansk_oblast,
            flagOfOfficial = R.string.luhansk_oblast_official,
            flagOfAlternate = listOf(
                R.string.luhansk_oblast_alt_1,
                R.string.luhansk_oblast_alt_2,
                R.string.luhansk_oblast_alt_3,
                R.string.luhansk_oblast_alt_4,
                R.string.luhansk_oblast_alt_5
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = "ukraine",
            associatedState = null,
            categories = listOf(FlagCategory.OBLAST),
        ),

        "luhanskPeoplesRepublic" to FlagResources(
            id = 165,
            image = R.drawable.flag_of_the_luhansk_people_s_republic,
            imagePreview = R.drawable.flag_of_the_luhansk_people_s_republic,
            flagOf = R.string.luhansk_peoples_republic,
            flagOfOfficial = R.string.luhansk_peoples_republic_official,
            flagOfAlternate = listOf(
                R.string.luhansk_peoples_republic_alt_1,
                R.string.luhansk_peoples_republic_alt_2,
                R.string.luhansk_peoples_republic_alt_3,
                R.string.luhansk_peoples_republic_alt_4,
                R.string.luhansk_peoples_republic_alt_5
            ),
            isFlagOfThe = true,
            isFlagOfOfficialThe = true,
            sovereignState = "ukraine",
            associatedState = null,
            categories = listOf(
                FlagCategory.AUTONOMOUS_REGION,
                FlagCategory.REPUBLIC_UNIT
            ),
        ),

        "luxembourg" to FlagResources(
            id = 166,
            image = R.drawable.flag_of_luxembourg,
            imagePreview = R.drawable.flag_of_luxembourg_preview,
            flagOf = R.string.luxembourg,
            flagOfOfficial = R.string.luxembourg_official,
            flagOfAlternate = listOf(
                R.string.luxembourg_alt_1,
                R.string.luxembourg_alt_2,
                R.string.luxembourg_alt_3,
                R.string.luxembourg_alt_4
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.MONARCHY
            ),
        ),

        "macau" to FlagResources(
            id = 167,
            image = R.drawable.flag_of_macau,
            imagePreview = R.drawable.flag_of_macau_preview,
            flagOf = R.string.macau,
            flagOfOfficial = R.string.macau_official,
            flagOfAlternate = listOf(
                R.string.macau_alt_1,
                R.string.macau_alt_2,
                R.string.macau_alt_3,
                R.string.macau_alt_4,
                R.string.macau_alt_5,
                R.string.macau_alt_6,
                R.string.macau_alt_7,
                R.string.macau_alt_8,
                R.string.macau_alt_9,
                R.string.macau_alt_10,
                R.string.macau_alt_11,
                R.string.macau_alt_12
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = "china",
            associatedState = null,
            categories = listOf(FlagCategory.REGION),
        ),

        "madagascar" to FlagResources(
            id = 168,
            image = R.drawable.flag_of_madagascar,
            imagePreview = R.drawable.flag_of_madagascar,
            flagOf = R.string.madagascar,
            flagOfOfficial = R.string.madagascar_official,
            flagOfAlternate = listOf(
                R.string.madagascar_alt_1,
                R.string.madagascar_alt_2,
                R.string.madagascar_alt_3,
                R.string.madagascar_alt_4,
                R.string.madagascar_alt_5,
                R.string.madagascar_alt_6
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.SEMI_PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "madeira" to FlagResources(
            id = 169,
            image = R.drawable.flag_of_madeira,
            imagePreview = R.drawable.flag_of_madeira,
            flagOf = R.string.madeira,
            flagOfOfficial = R.string.madeira_official,
            flagOfAlternate = listOf(
                R.string.madeira_alt_1,
                R.string.madeira_alt_2,
                R.string.madeira_alt_3
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = "portugal",
            associatedState = null,
            categories = listOf(FlagCategory.AUTONOMOUS_REGION),
        ),

        "madridCommunity" to FlagResources(
            id = 170,
            image = R.drawable.flag_of_the_community_of_madrid,
            imagePreview = R.drawable.flag_of_the_community_of_madrid_preview,
            flagOf = R.string.madrid_community,
            flagOfOfficial = R.string.madrid_community_official,
            flagOfAlternate = listOf(
                R.string.madrid_community_alt_1,
                R.string.madrid_community_alt_2,
                R.string.madrid_community_alt_3
            ),
            isFlagOfThe = true,
            isFlagOfOfficialThe = true,
            sovereignState = "spain",
            associatedState = null,
            categories = listOf(
                FlagCategory.AUTONOMOUS_REGION,
                FlagCategory.COMMUNITY,
                FlagCategory.DEVOLVED_GOVERNMENT
            ),
        ),

        "malawi" to FlagResources(
            id = 171,
            image = R.drawable.flag_of_malawi,
            imagePreview = R.drawable.flag_of_malawi,
            flagOf = R.string.malawi,
            flagOfOfficial = R.string.malawi_official,
            flagOfAlternate = listOf(
                R.string.malawi_alt_1,
                R.string.malawi_alt_2,
                R.string.malawi_alt_3
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "malaysia" to FlagResources(
            id = 172,
            image = R.drawable.flag_of_malaysia,
            imagePreview = R.drawable.flag_of_malaysia_preview,
            flagOf = R.string.malaysia,
            flagOfOfficial = R.string.malaysia_official,
            flagOfAlternate = listOf(
                R.string.malaysia_alt_1,
                R.string.malaysia_alt_2,
                R.string.malaysia_alt_3,
                R.string.malaysia_alt_4,
                R.string.malaysia_alt_5
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.FEDERAL,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.THEOCRATIC,
                FlagCategory.MONARCHY
            ),
        ),

        "maldives" to FlagResources(
            id = 173,
            image = R.drawable.flag_of_maldives,
            imagePreview = R.drawable.flag_of_maldives_preview,
            flagOf = R.string.maldives,
            flagOfOfficial = R.string.maldives_official,
            flagOfAlternate = listOf(
                R.string.maldives_alt_1,
                R.string.maldives_alt_2,
                R.string.maldives_alt_3
            ),
            isFlagOfThe = true,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.THEOCRATIC,
                FlagCategory.REPUBLIC
            ),
        ),

        "mali" to FlagResources(
            id = 174,
            image = R.drawable.flag_of_mali,
            imagePreview = R.drawable.flag_of_mali,
            flagOf = R.string.mali,
            flagOfOfficial = R.string.mali_official,
            flagOfAlternate = listOf(
                R.string.mali_alt_1,
                R.string.mali_alt_2,
                R.string.mali_alt_3
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PRESIDENTIAL,
                FlagCategory.MILITARY_JUNTA
            ),
        ),

        "malta" to FlagResources(
            id = 175,
            image = R.drawable.flag_of_malta,
            imagePreview = R.drawable.flag_of_malta,
            flagOf = R.string.malta,
            flagOfOfficial = R.string.malta_official,
            flagOfAlternate = listOf(
                R.string.malta_alt_1,
                R.string.malta_alt_2,
                R.string.malta_alt_3
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "marshallIslands" to FlagResources(
            id = 176,
            image = R.drawable.flag_of_the_marshall_islands,
            imagePreview = R.drawable.flag_of_the_marshall_islands,
            flagOf = R.string.marshall_islands,
            flagOfOfficial = R.string.marshall_islands_official,
            flagOfAlternate = listOf(
                R.string.marshall_islands_alt_1,
                R.string.marshall_islands_alt_2,
                R.string.marshall_islands_alt_3
            ),
            isFlagOfThe = true,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = "unitedStates",
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.FREE_ASSOCIATION,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "martinique" to FlagResources(
            id = 177,
            image = R.drawable.flag_of_martinique,
            imagePreview = R.drawable.flag_of_martinique,
            flagOf = R.string.martinique,
            flagOfOfficial = R.string.martinique_official,
            flagOfAlternate = listOf(
                R.string.martinique_alt_1,
                R.string.martinique_alt_2,
                R.string.martinique_alt_3,
                R.string.martinique_alt_4,
                R.string.martinique_alt_5,
                R.string.martinique_alt_6,
                R.string.martinique_alt_7,
                R.string.martinique_alt_8,
                R.string.martinique_alt_9,
                R.string.martinique_alt_10
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = "france",
            associatedState = null,
            categories = listOf(FlagCategory.REGION),
        ),

        "mauritania" to FlagResources(
            id = 178,
            image = R.drawable.flag_of_mauritania,
            imagePreview = R.drawable.flag_of_mauritania,
            flagOf = R.string.mauritania,
            flagOfOfficial = R.string.mauritania_official,
            flagOfAlternate = listOf(
                R.string.mauritania_alt_1,
                R.string.mauritania_alt_2,
                R.string.mauritania_alt_3,
                R.string.mauritania_alt_4,
                R.string.mauritania_alt_5,
                R.string.mauritania_alt_6,
                R.string.mauritania_alt_7,
                R.string.mauritania_alt_8,
                R.string.mauritania_alt_9,
                R.string.mauritania_alt_10
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.SEMI_PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.THEOCRATIC,
                FlagCategory.REPUBLIC
            ),
        ),

        "mauritius" to FlagResources(
            id = 179,
            image = R.drawable.flag_of_mauritius,
            imagePreview = R.drawable.flag_of_mauritius,
            flagOf = R.string.mauritius,
            flagOfOfficial = R.string.mauritius_official,
            flagOfAlternate = listOf(
                R.string.mauritius_alt_1,
                R.string.mauritius_alt_2,
                R.string.mauritius_alt_3,
                R.string.mauritius_alt_4,
                R.string.mauritius_alt_5
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "mayotte" to FlagResources(
            id = 180,
            image = R.drawable.flag_of_mayotte__local_,
            imagePreview = R.drawable.flag_of_mayotte__local__preview,
            flagOf = R.string.mayotte,
            flagOfOfficial = R.string.mayotte_official,
            flagOfAlternate = listOf(
                R.string.mayotte_alt_1,
                R.string.mayotte_alt_2,
                R.string.mayotte_alt_3
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = "france",
            associatedState = null,
            categories = listOf(FlagCategory.REGION),
        ),

        "melilla" to FlagResources(
            id = 181,
            image = R.drawable.flag_of_melilla,
            imagePreview = R.drawable.flag_of_melilla_preview,
            flagOf = R.string.melilla,
            flagOfOfficial = R.string.melilla_official,
            flagOfAlternate = listOf(R.string.melilla_alt),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = "spain",
            associatedState = null,
            categories = listOf(
                FlagCategory.AUTONOMOUS_REGION,
                FlagCategory.CITY,
                FlagCategory.DEVOLVED_GOVERNMENT
            ),
        ),

        "mexico" to FlagResources(
            id = 182,
            image = R.drawable.flag_of_mexico,
            imagePreview = R.drawable.flag_of_mexico_preview,
            flagOf = R.string.mexico,
            flagOfOfficial = R.string.mexico_official,
            flagOfAlternate = listOf(
                R.string.mexico_alt_1,
                R.string.mexico_alt_2,
                R.string.mexico_alt_3,
                R.string.mexico_alt_4,
                R.string.mexico_alt_5,
                R.string.mexico_alt_6,
                R.string.mexico_alt_7,
                R.string.mexico_alt_8
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.FEDERAL,
                FlagCategory.PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "micronesia" to FlagResources(
            id = 183,
            image = R.drawable.flag_of_the_federated_states_of_micronesia,
            imagePreview = R.drawable.flag_of_the_federated_states_of_micronesia,
            flagOf = R.string.micronesia,
            flagOfOfficial = R.string.micronesia_official,
            flagOfAlternate = listOf(
                R.string.micronesia_alt_1,
                R.string.micronesia_alt_2,
                R.string.micronesia_alt_3,
                R.string.micronesia_alt_4,
                R.string.micronesia_alt_5,
                R.string.micronesia_alt_6
            ),
            isFlagOfThe = true,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = "unitedStates",
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.FREE_ASSOCIATION,
                FlagCategory.FEDERAL,
                FlagCategory.PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "moldova" to FlagResources(
            id = 184,
            image = R.drawable.flag_of_moldova,
            imagePreview = R.drawable.flag_of_moldova,
            flagOf = R.string.moldova,
            flagOfOfficial = R.string.moldova_official,
            flagOfAlternate = listOf(
                R.string.moldova_alt_1,
                R.string.moldova_alt_2,
                R.string.moldova_alt_3,
                R.string.moldova_alt_4,
                R.string.moldova_alt_5
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "monaco" to FlagResources(
            id = 185,
            image = R.drawable.flag_of_monaco,
            imagePreview = R.drawable.flag_of_monaco,
            flagOf = R.string.monaco,
            flagOfOfficial = R.string.monaco_official,
            flagOfAlternate = listOf(
                R.string.monaco_alt_1,
                R.string.monaco_alt_2,
                R.string.monaco_alt_3,
                R.string.monaco_alt_4,
                R.string.monaco_alt_5,
                R.string.monaco_alt_6,
                R.string.monaco_alt_7,
                R.string.monaco_alt_8,
                R.string.monaco_alt_9,
                R.string.monaco_alt_10
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.MONARCHY
            ),
        ),

        "mongolia" to FlagResources(
            id = 186,
            image = R.drawable.flag_of_mongolia,
            imagePreview = R.drawable.flag_of_mongolia,
            flagOf = R.string.mongolia,
            flagOfOfficial = R.string.mongolia_official,
            flagOfAlternate = listOf(
                R.string.mongolia_alt_1,
                R.string.mongolia_alt_2
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.SEMI_PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "montenegro" to FlagResources(
            id = 187,
            image = R.drawable.flag_of_montenegro,
            imagePreview = R.drawable.flag_of_montenegro_preview,
            flagOf = R.string.montenegro,
            flagOfOfficial = R.string.montenegro_official,
            flagOfAlternate = listOf(
                R.string.montenegro_alt_1,
                R.string.montenegro_alt_2,
                R.string.montenegro_alt_3
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "montserrat" to FlagResources(
            id = 188,
            image = R.drawable.flag_of_montserrat,
            imagePreview = R.drawable.flag_of_montserrat,
            flagOf = R.string.montserrat,
            flagOfOfficial = R.string.montserrat_official,
            flagOfAlternate = listOf(
                R.string.montserrat_alt_1,
                R.string.montserrat_alt_2,
                R.string.montserrat_alt_3
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = "unitedKingdom",
            associatedState = null,
            categories = listOf(FlagCategory.TERRITORY),
        ),

        "morocco" to FlagResources(
            id = 189,
            image = R.drawable.flag_of_morocco,
            imagePreview = R.drawable.flag_of_morocco,
            flagOf = R.string.morocco,
            flagOfOfficial = R.string.morocco_official,
            flagOfAlternate = listOf(
                R.string.morocco_alt_1,
                R.string.morocco_alt_2,
                R.string.morocco_alt_3,
                R.string.morocco_alt_4,
                R.string.morocco_alt_5,
                R.string.morocco_alt_6,
                R.string.morocco_alt_7,
                R.string.morocco_alt_8
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.MONARCHY
            ),
        ),

        "mozambique" to FlagResources(
            id = 190,
            image = R.drawable.flag_of_mozambique,
            imagePreview = R.drawable.flag_of_mozambique,
            flagOf = R.string.mozambique,
            flagOfOfficial = R.string.mozambique_official,
            flagOfAlternate = listOf(
                R.string.mozambique_alt_1,
                R.string.mozambique_alt_2,
                R.string.mozambique_alt_3,
                R.string.mozambique_alt_4,
                R.string.mozambique_alt_5,
                R.string.mozambique_alt_6
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.SEMI_PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "murciaCommunity" to FlagResources(
            id = 191,
            image = R.drawable.flag_of_the_region_of_murcia,
            imagePreview = R.drawable.flag_of_the_region_of_murcia,
            flagOf = R.string.murcia_community,
            flagOfOfficial = R.string.murcia_community_official,
            flagOfAlternate = listOf(
                R.string.murcia_community_alt_1,
                R.string.murcia_community_alt_2,
                R.string.murcia_community_alt_3,
                R.string.murcia_community_alt_4,
                R.string.murcia_community_alt_5,
                R.string.murcia_community_alt_6,
                R.string.murcia_community_alt_7,
                R.string.murcia_community_alt_8,
                R.string.murcia_community_alt_9
            ),
            isFlagOfThe = true,
            isFlagOfOfficialThe = true,
            sovereignState = "spain",
            associatedState = null,
            categories = listOf(
                FlagCategory.AUTONOMOUS_REGION,
                FlagCategory.COMMUNITY,
                FlagCategory.DEVOLVED_GOVERNMENT
            ),
        ),

        "myanmar" to FlagResources(
            id = 192,
            image = R.drawable.flag_of_myanmar,
            imagePreview = R.drawable.flag_of_myanmar,
            flagOf = R.string.myanmar,
            flagOfOfficial = R.string.myanmar_official,
            flagOfAlternate = listOf(
                R.string.myanmar_alt_1,
                R.string.myanmar_alt_2,
                R.string.myanmar_alt_3,
                R.string.myanmar_alt_4,
                R.string.myanmar_alt_5,
                R.string.myanmar_alt_6
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.MILITARY_JUNTA
            ),
        ),

        "nato" to FlagResources(
            id = 193,
            image = R.drawable.flag_of_nato,
            imagePreview = R.drawable.flag_of_nato,
            flagOf = R.string.nato,
            flagOfOfficial = R.string.nato_official,
            flagOfAlternate = listOf(
                R.string.nato_alt_1,
                R.string.nato_alt_2,
                R.string.nato_alt_3,
                R.string.nato_alt_4
            ),
            isFlagOfThe = true,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.DIRECTORIAL,
                FlagCategory.INTERNATIONAL_ORGANIZATION
            ),
        ),

        "namibia" to FlagResources(
            id = 194,
            image = R.drawable.flag_of_namibia,
            imagePreview = R.drawable.flag_of_namibia,
            flagOf = R.string.namibia,
            flagOfOfficial = R.string.namibia_official,
            flagOfAlternate = listOf(
                R.string.namibia_alt_1,
                R.string.namibia_alt_2,
                R.string.namibia_alt_3
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.SEMI_PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "nauru" to FlagResources(
            id = 195,
            image = R.drawable.flag_of_nauru,
            imagePreview = R.drawable.flag_of_nauru,
            flagOf = R.string.nauru,
            flagOfOfficial = R.string.nauru_official,
            flagOfAlternate = listOf(
                R.string.nauru_alt_1,
                R.string.nauru_alt_2,
                R.string.nauru_alt_3,
                R.string.nauru_alt_4
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "navarre" to FlagResources(
            id = 196,
            image = R.drawable.bandera_de_navarra,
            imagePreview = R.drawable.bandera_de_navarra_preview,
            flagOf = R.string.navarre,
            flagOfOfficial = R.string.navarre_official,
            flagOfAlternate = listOf(
                R.string.navarre_alt_1,
                R.string.navarre_alt_2,
                R.string.navarre_alt_3,
                R.string.navarre_alt_4
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = "spain",
            associatedState = null,
            categories = listOf(
                FlagCategory.AUTONOMOUS_REGION,
                FlagCategory.COMMUNITY,
                FlagCategory.DEVOLVED_GOVERNMENT
            ),
        ),

        "naziGermany" to FlagResources(
            id = 197,
            image = R.drawable.flag_of_germany__19351945_,
            imagePreview = R.drawable.flag_of_germany__19351945_,
            flagOf = R.string.nazi_germany,
            flagOfOfficial = R.string.nazi_germany_official,
            flagOfAlternate = listOf(
                R.string.nazi_germany_alt_1,
                R.string.nazi_germany_alt_2,
                R.string.nazi_germany_alt_3,
                R.string.nazi_germany_alt_4,
                R.string.nazi_germany_alt_5
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.HISTORICAL,
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.FASCIST,
                FlagCategory.ONE_PARTY
            ),
        ),

        "nepal" to FlagResources(
            id = 198,
            image = R.drawable.flag_of_nepal,
            imagePreview = R.drawable.flag_of_nepal,
            flagOf = R.string.nepal,
            flagOfOfficial = R.string.nepal_official,
            flagOfAlternate = listOf(
                R.string.nepal_alt_1,
                R.string.nepal_alt_2,
                R.string.nepal_alt_3,
                R.string.nepal_alt_4,
                R.string.nepal_alt_5,
                R.string.nepal_alt_6,
                R.string.nepal_alt_7,
                R.string.nepal_alt_8,
                R.string.nepal_alt_9,
                R.string.nepal_alt_10,
                R.string.nepal_alt_11,
                R.string.nepal_alt_12,
                R.string.nepal_alt_13,
                R.string.nepal_alt_14,
                R.string.nepal_alt_15,
                R.string.nepal_alt_16,
                R.string.nepal_alt_17,
                R.string.nepal_alt_18,
                R.string.nepal_alt_19,
                R.string.nepal_alt_20,
                R.string.nepal_alt_21,
                R.string.nepal_alt_22,
                R.string.nepal_alt_23,
                R.string.nepal_alt_24,
                R.string.nepal_alt_25,
                R.string.nepal_alt_26,
                R.string.nepal_alt_27,
                R.string.nepal_alt_28,
                R.string.nepal_alt_29,
                R.string.nepal_alt_30,
                R.string.nepal_alt_31,
                R.string.nepal_alt_32,
                R.string.nepal_alt_33
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.FEDERAL,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "netherlands" to FlagResources(
            id = 199,
            image = R.drawable.flag_of_the_netherlands,
            imagePreview = R.drawable.flag_of_the_netherlands,
            flagOf = R.string.netherlands,
            flagOfOfficial = R.string.netherlands_official,
            flagOfAlternate = listOf(
                R.string.netherlands_alt_1,
                R.string.netherlands_alt_2,
                R.string.netherlands_alt_3,
                R.string.netherlands_alt_4
            ),
            isFlagOfThe = true,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.MONARCHY
            ),
        ),

        "newCaledonia" to FlagResources(
            id = 200,
            image = R.drawable.flag_of_flnks,
            imagePreview = R.drawable.flag_of_flnks,
            flagOf = R.string.new_caledonia,
            flagOfOfficial = R.string.new_caledonia_official,
            flagOfAlternate = listOf(
                R.string.new_caledonia_alt_1,
                R.string.new_caledonia_alt_2,
                R.string.new_caledonia_alt_3,
                R.string.new_caledonia_alt_4,
                R.string.new_caledonia_alt_5,
                R.string.new_caledonia_alt_6
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = "france",
            associatedState = null,
            categories = listOf(
                FlagCategory.AUTONOMOUS_REGION,
                FlagCategory.COLLECTIVITY
            ),
        ),

        "newZealand" to FlagResources(
            id = 201,
            image = R.drawable.flag_of_new_zealand,
            imagePreview = R.drawable.flag_of_new_zealand,
            flagOf = R.string.new_zealand,
            flagOfOfficial = R.string.new_zealand_official,
            flagOfAlternate = listOf(
                R.string.new_zealand_alt_1,
                R.string.new_zealand_alt_2,
                R.string.new_zealand_alt_3,
                R.string.new_zealand_alt_4,
                R.string.new_zealand_alt_5,
                R.string.new_zealand_alt_6,
                R.string.new_zealand_alt_7,
                R.string.new_zealand_alt_8,
                R.string.new_zealand_alt_9,
                R.string.new_zealand_alt_10
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.MONARCHY
            ),
        ),

        "nicaragua" to FlagResources(
            id = 202,
            image = R.drawable.flag_of_nicaragua,
            imagePreview = R.drawable.flag_of_nicaragua,
            flagOf = R.string.nicaragua,
            flagOfOfficial = R.string.nicaragua_official,
            flagOfAlternate = listOf(
                R.string.nicaragua_alt_1,
                R.string.nicaragua_alt_2,
                R.string.nicaragua_alt_3,
                R.string.nicaragua_alt_4,
                R.string.nicaragua_alt_5
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "niger" to FlagResources(
            id = 203,
            image = R.drawable.flag_of_niger,
            imagePreview = R.drawable.flag_of_niger,
            flagOf = R.string.niger,
            flagOfOfficial = R.string.niger_official,
            flagOfAlternate = listOf(
                R.string.niger_alt_1,
                R.string.niger_alt_2,
                R.string.niger_alt_3,
                R.string.niger_alt_4,
                R.string.niger_alt_5
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.SEMI_PRESIDENTIAL,
                FlagCategory.MILITARY_JUNTA
            ),
        ),

        "nigeria" to FlagResources(
            id = 204,
            image = R.drawable.flag_of_nigeria,
            imagePreview = R.drawable.flag_of_nigeria,
            flagOf = R.string.nigeria,
            flagOfOfficial = R.string.nigeria_official,
            flagOfAlternate = listOf(
                R.string.nigeria_alt_1,
                R.string.nigeria_alt_2,
                R.string.nigeria_alt_3,
                R.string.nigeria_alt_4,
                R.string.nigeria_alt_5,
                R.string.nigeria_alt_6
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.FEDERAL,
                FlagCategory.PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "niue" to FlagResources(
            id = 205,
            image = R.drawable.flag_of_niue,
            imagePreview = R.drawable.flag_of_niue,
            flagOf = R.string.niue,
            flagOfOfficial = R.string.niue_official,
            flagOfAlternate = listOf(
                R.string.niue_alt_1,
                R.string.niue_alt_2
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = "newZealand",
            associatedState = "newZealand",
            categories = listOf(
                FlagCategory.AUTONOMOUS_REGION,
                FlagCategory.REGION
            ),
        ),

        "norfolkIsland" to FlagResources(
            id = 206,
            image = R.drawable.flag_of_norfolk_island,
            imagePreview = R.drawable.flag_of_norfolk_island,
            flagOf = R.string.norfolk_island,
            flagOfOfficial = R.string.norfolk_island_official,
            flagOfAlternate = listOf(
                R.string.norfolk_island_alt_1,
                R.string.norfolk_island_alt_2,
                R.string.norfolk_island_alt_3,
                R.string.norfolk_island_alt_4
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = "australia",
            associatedState = null,
            categories = listOf(FlagCategory.TERRITORY),
        ),

        "northKorea" to FlagResources(
            id = 207,
            image = R.drawable.flag_of_north_korea,
            imagePreview = R.drawable.flag_of_north_korea,
            flagOf = R.string.north_korea,
            flagOfOfficial = R.string.north_korea_official,
            flagOfAlternate = listOf(
                R.string.north_korea_alt_1,
                R.string.north_korea_alt_2,
                R.string.north_korea_alt_3,
                R.string.north_korea_alt_4,
                R.string.north_korea_alt_5,
                R.string.north_korea_alt_6
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.DUAL_EXECUTIVE,
                FlagCategory.SOCIALIST,
                FlagCategory.ONE_PARTY
            ),
        ),

        "northMacedonia" to FlagResources(
            id = 208,
            image = R.drawable.flag_of_north_macedonia,
            imagePreview = R.drawable.flag_of_north_macedonia,
            flagOf = R.string.north_macedonia,
            flagOfOfficial = R.string.north_macedonia_official,
            flagOfAlternate = listOf(
                R.string.north_macedonia_alt_1,
                R.string.north_macedonia_alt_2,
                R.string.north_macedonia_alt_3,
                R.string.north_macedonia_alt_4,
                R.string.north_macedonia_alt_5,
                R.string.north_macedonia_alt_6,
                R.string.north_macedonia_alt_7,
                R.string.north_macedonia_alt_8
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "northernCyprus" to FlagResources(
            id = 209,
            image = R.drawable.flag_of_the_turkish_republic_of_northern_cyprus,
            imagePreview = R.drawable.flag_of_the_turkish_republic_of_northern_cyprus,
            flagOf = R.string.northern_cyprus,
            flagOfOfficial = R.string.northern_cyprus_official,
            flagOfAlternate = listOf(
                R.string.northern_cyprus_alt_1,
                R.string.northern_cyprus_alt_2,
                R.string.northern_cyprus_alt_3,
                R.string.northern_cyprus_alt_4,
                R.string.northern_cyprus_alt_5,
                R.string.northern_cyprus_alt_6,
                R.string.northern_cyprus_alt_7,
                R.string.northern_cyprus_alt_8,
                R.string.northern_cyprus_alt_9,
                R.string.northern_cyprus_alt_10,
                R.string.northern_cyprus_alt_11,
                R.string.northern_cyprus_alt_12,
                R.string.northern_cyprus_alt_13
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = "cyprus",
            associatedState = null,
            categories = listOf(FlagCategory.AUTONOMOUS_REGION),
        ),

        "northernIreland" to FlagResources(
            id = 210,
            image = R.drawable.saint_patrick_s_saltire,
            imagePreview = R.drawable.saint_patrick_s_saltire,
            flagOf = R.string.northern_ireland,
            flagOfOfficial = R.string.northern_ireland_official,
            flagOfAlternate = listOf(
                R.string.northern_ireland_alt_1,
                R.string.northern_ireland_alt_2,
                R.string.northern_ireland_alt_3,
                R.string.northern_ireland_alt_4,
                R.string.northern_ireland_alt_5,
                R.string.northern_ireland_alt_6,
                R.string.northern_ireland_alt_7,
                R.string.northern_ireland_alt_8
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = "unitedKingdom",
            associatedState = null,
            categories = listOf(
                FlagCategory.COUNTRY,
                FlagCategory.DEVOLVED_GOVERNMENT
            ),
        ),

        "northernMarianaIslands" to FlagResources(
            id = 211,
            image = R.drawable.flag_of_the_northern_mariana_islands,
            imagePreview = R.drawable.flag_of_the_northern_mariana_islands_preview,
            flagOf = R.string.northern_mariana_islands,
            flagOfOfficial = R.string.northern_mariana_islands_official,
            flagOfAlternate = listOf(
                R.string.northern_mariana_islands_alt_1,
                R.string.northern_mariana_islands_alt_2,
                R.string.northern_mariana_islands_alt_3,
                R.string.northern_mariana_islands_alt_4,
                R.string.northern_mariana_islands_alt_5,
                R.string.northern_mariana_islands_alt_6,
                R.string.northern_mariana_islands_alt_7,
                R.string.northern_mariana_islands_alt_8,
                R.string.northern_mariana_islands_alt_9,
                R.string.northern_mariana_islands_alt_10,
                R.string.northern_mariana_islands_alt_11
            ),
            isFlagOfThe = true,
            isFlagOfOfficialThe = true,
            sovereignState = "unitedStates",
            associatedState = null,
            categories = listOf(
                FlagCategory.AUTONOMOUS_REGION,
                FlagCategory.TERRITORY
            ),
        ),

        "norway" to FlagResources(
            id = 212,
            image = R.drawable.flag_of_norway,
            imagePreview = R.drawable.flag_of_norway_preview,
            flagOf = R.string.norway,
            flagOfOfficial = R.string.norway_official,
            flagOfAlternate = listOf(R.string.norway_alt),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.MONARCHY
            ),
        ),

        "novorossiyaConfederation" to FlagResources(
            id = 213,
            image = R.drawable.war_flag_of_novorussia,
            imagePreview = R.drawable.war_flag_of_novorussia,
            flagOf = R.string.novorossiya_confederation,
            flagOfOfficial = R.string.novorossiya_confederation_official,
            flagOfAlternate = listOf(
                R.string.novorossiya_confederation_alt_1,
                R.string.novorossiya_confederation_alt_2,
                R.string.novorossiya_confederation_alt_3,
                R.string.novorossiya_confederation_alt_4,
                R.string.novorossiya_confederation_alt_5
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = "ukraine",
            associatedState = null,
            categories = listOf(
                FlagCategory.HISTORICAL,
                FlagCategory.POLITICAL
            ),
        ),

        "oman" to FlagResources(
            id = 214,
            image = R.drawable.flag_of_oman,
            imagePreview = R.drawable.flag_of_oman_preview,
            flagOf = R.string.oman,
            flagOfOfficial = R.string.oman_official,
            flagOfAlternate = listOf(
                R.string.oman_alt_1,
                R.string.oman_alt_2,
                R.string.oman_alt_3,
                R.string.oman_alt_4,
                R.string.oman_alt_5
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.THEOCRATIC,
                FlagCategory.MONARCHY
            ),
        ),

        "orderOfTheHolySepulchre" to FlagResources(
            id = 215,
            image = R.drawable.drapeau_du_saint_s_pulcre,
            imagePreview = R.drawable.drapeau_du_saint_s_pulcre,
            flagOf = R.string.order_of_the_holy_sepulchre,
            flagOfOfficial = R.string.order_of_the_holy_sepulchre_official,
            flagOfAlternate = listOf(
                R.string.order_of_the_holy_sepulchre_alt_1,
                R.string.order_of_the_holy_sepulchre_alt_2,
                R.string.order_of_the_holy_sepulchre_alt_3,
                R.string.order_of_the_holy_sepulchre_alt_4
            ),
            isFlagOfThe = true,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(FlagCategory.RELIGIOUS),
        ),

        "pakistan" to FlagResources(
            id = 216,
            image = R.drawable.flag_of_pakistan,
            imagePreview = R.drawable.flag_of_pakistan,
            flagOf = R.string.pakistan,
            flagOfOfficial = R.string.pakistan_official,
            flagOfAlternate = listOf(
                R.string.pakistan_alt_1,
                R.string.pakistan_alt_2,
                R.string.pakistan_alt_3,
                R.string.pakistan_alt_4
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.FEDERAL,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.THEOCRATIC,
                FlagCategory.REPUBLIC
            ),
        ),

        "palau" to FlagResources(
            id = 217,
            image = R.drawable.flag_of_palau,
            imagePreview = R.drawable.flag_of_palau,
            flagOf = R.string.palau,
            flagOfOfficial = R.string.palau_official,
            flagOfAlternate = listOf(
                R.string.palau_alt_1,
                R.string.palau_alt_2,
                R.string.palau_alt_3,
                R.string.palau_alt_4,
                R.string.palau_alt_5,
                R.string.palau_alt_6,
                R.string.palau_alt_7,
                R.string.palau_alt_8
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = "unitedStates",
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.FREE_ASSOCIATION,
                FlagCategory.UNITARY,
                FlagCategory.PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "palestine" to FlagResources(
            id = 218,
            image = R.drawable.flag_of_palestine,
            imagePreview = R.drawable.flag_of_palestine,
            flagOf = R.string.palestine,
            flagOfOfficial = R.string.palestine_official,
            flagOfAlternate = listOf(
                R.string.palestine_alt_1,
                R.string.palestine_alt_2,
                R.string.palestine_alt_3,
                R.string.palestine_alt_4
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.SEMI_PRESIDENTIAL,
                FlagCategory.PROVISIONAL_GOVERNMENT
            ),
        ),

        "panama" to FlagResources(
            id = 219,
            image = R.drawable.flag_of_panama,
            imagePreview = R.drawable.flag_of_panama,
            flagOf = R.string.panama,
            flagOfOfficial = R.string.panama_official,
            flagOfAlternate = listOf(
                R.string.panama_alt_1,
                R.string.panama_alt_2
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "papuaNewGuinea" to FlagResources(
            id = 220,
            image = R.drawable.flag_of_papua_new_guinea,
            imagePreview = R.drawable.flag_of_papua_new_guinea,
            flagOf = R.string.papua_new_guinea,
            flagOfOfficial = R.string.papua_new_guinea_official,
            flagOfAlternate = listOf(
                R.string.papua_new_guinea_alt_1,
                R.string.papua_new_guinea_alt_2,
                R.string.papua_new_guinea_alt_3
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.MONARCHY
            ),
        ),

        "paraguay" to FlagResources(
            id = 221,
            image = R.drawable.flag_of_paraguay,
            imagePreview = R.drawable.flag_of_paraguay,
            flagOf = R.string.paraguay,
            flagOfOfficial = R.string.paraguay_official,
            flagOfAlternate = listOf(
                R.string.paraguay_alt_1,
                R.string.paraguay_alt_2,
                R.string.paraguay_alt_3,
                R.string.paraguay_alt_4
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "peoplesUnionForTheLiberationOfGuadeloupe" to FlagResources(
            id = 222,
            image = R.drawable.flag_of_guadeloupe__uplg_,
            imagePreview = R.drawable.flag_of_guadeloupe__uplg_,
            flagOf = R.string.peoples_union_for_the_liberation_of_guadeloupe,
            flagOfOfficial = R.string.peoples_union_for_the_liberation_of_guadeloupe_official,
            flagOfAlternate = listOf(
                R.string.peoples_union_for_the_liberation_of_guadeloupe_alt_1,
                R.string.peoples_union_for_the_liberation_of_guadeloupe_alt_2
            ),
            isFlagOfThe = true,
            isFlagOfOfficialThe = false,
            sovereignState = "france",
            associatedState = null,
            categories = listOf(FlagCategory.POLITICAL),
        ),

        "peru" to FlagResources(
            id = 223,
            image = R.drawable.flag_of_peru,
            imagePreview = R.drawable.flag_of_peru,
            flagOf = R.string.peru,
            flagOfOfficial = R.string.peru_official,
            flagOfAlternate = listOf(
                R.string.peru_alt_1,
                R.string.peru_alt_2,
                R.string.peru_alt_3,
                R.string.peru_alt_4,
                R.string.peru_alt_5
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.SEMI_PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "philippines" to FlagResources(
            id = 224,
            image = R.drawable.flag_of_the_philippines,
            imagePreview = R.drawable.flag_of_the_philippines,
            flagOf = R.string.philippines,
            flagOfOfficial = R.string.philippines_official,
            flagOfAlternate = listOf(
                R.string.philippines_alt_1,
                R.string.philippines_alt_2,
                R.string.philippines_alt_3,
                R.string.philippines_alt_4,
                R.string.philippines_alt_5,
                R.string.philippines_alt_6,
                R.string.philippines_alt_7,
                R.string.philippines_alt_8
            ),
            isFlagOfThe = true,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "pitcairnIslands" to FlagResources(
            id = 225,
            image = R.drawable.flag_of_the_pitcairn_islands,
            imagePreview = R.drawable.flag_of_the_pitcairn_islands,
            flagOf = R.string.pitcairn_islands,
            flagOfOfficial = R.string.pitcairn_islands_official,
            flagOfAlternate = listOf(
                R.string.pitcairn_islands_alt_1,
                R.string.pitcairn_islands_alt_2,
                R.string.pitcairn_islands_alt_3,
                R.string.pitcairn_islands_alt_4
            ),
            isFlagOfThe = true,
            isFlagOfOfficialThe = false,
            sovereignState = "unitedKingdom",
            associatedState = null,
            categories = listOf(FlagCategory.TERRITORY),
        ),

        "pohnpei" to FlagResources(
            id = 226,
            image = R.drawable.flag_of_pohnpei,
            imagePreview = R.drawable.flag_of_pohnpei,
            flagOf = R.string.pohnpei,
            flagOfOfficial = R.string.pohnpei_official,
            flagOfAlternate = listOf(
                R.string.pohnpei_alt_1,
                R.string.pohnpei_alt_2
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = "micronesia",
            associatedState = null,
            categories = listOf(FlagCategory.STATE),
        ),

        "poland" to FlagResources(
            id = 227,
            image = R.drawable.flag_of_poland,
            imagePreview = R.drawable.flag_of_poland,
            flagOf = R.string.poland,
            flagOfOfficial = R.string.poland_official,
            flagOfAlternate = listOf(R.string.poland_alt),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.SEMI_PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "portugal" to FlagResources(
            id = 228,
            image = R.drawable.flag_of_portugal__alternate_,
            imagePreview = R.drawable.flag_of_portugal__alternate_,
            flagOf = R.string.portugal,
            flagOfOfficial = R.string.portugal_official,
            flagOfAlternate = listOf(
                R.string.portugal_alt_1,
                R.string.portugal_alt_2,
                R.string.portugal_alt_3
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.SEMI_PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "puertoRico" to FlagResources(
            id = 229,
            image = R.drawable.flag_of_puerto_rico,
            imagePreview = R.drawable.flag_of_puerto_rico,
            flagOf = R.string.puerto_rico,
            flagOfOfficial = R.string.puerto_rico_official,
            flagOfAlternate = listOf(
                R.string.puerto_rico_alt_1,
                R.string.puerto_rico_alt_2,
                R.string.puerto_rico_alt_3,
                R.string.puerto_rico_alt_4,
                R.string.puerto_rico_alt_5,
                R.string.puerto_rico_alt_6,
                R.string.puerto_rico_alt_7,
                R.string.puerto_rico_alt_8,
                R.string.puerto_rico_alt_9,
                R.string.puerto_rico_alt_10,
                R.string.puerto_rico_alt_11,
                R.string.puerto_rico_alt_12,
                R.string.puerto_rico_alt_13,
                R.string.puerto_rico_alt_14,
                R.string.puerto_rico_alt_15
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = "unitedStates",
            associatedState = null,
            categories = listOf(
                FlagCategory.AUTONOMOUS_REGION,
                FlagCategory.TERRITORY
            ),
        ),

        "puntland" to FlagResources(
            id = 230,
            image = R.drawable.flag_of_puntland,
            imagePreview = R.drawable.flag_of_puntland,
            flagOf = R.string.puntland,
            flagOfOfficial = R.string.puntland_official,
            flagOfAlternate = listOf(
                R.string.puntland_alt_1,
                R.string.puntland_alt_2,
                R.string.puntland_alt_3,
                R.string.puntland_alt_4,
                R.string.puntland_alt_5
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = "somalia",
            associatedState = null,
            categories = listOf(
                FlagCategory.AUTONOMOUS_REGION,
                FlagCategory.STATE
            ),
        ),

        "qatar" to FlagResources(
            id = 231,
            image = R.drawable.flag_of_qatar,
            imagePreview = R.drawable.flag_of_qatar,
            flagOf = R.string.qatar,
            flagOfOfficial = R.string.qatar_official,
            flagOfAlternate = listOf(
                R.string.qatar_alt_1,
                R.string.qatar_alt_2,
                R.string.qatar_alt_3
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.MONARCHY
            ),
        ),

        "quebec" to FlagResources(
            id = 232,
            image = R.drawable.flag_of_quebec,
            imagePreview = R.drawable.flag_of_quebec,
            flagOf = R.string.quebec,
            flagOfOfficial = R.string.quebec_official,
            flagOfAlternate = listOf(
                R.string.quebec_alt_1,
                R.string.quebec_alt_2,
                R.string.quebec_alt_3,
                R.string.quebec_alt_4,
                R.string.quebec_alt_5,
                R.string.quebec_alt_6,
                R.string.quebec_alt_7,
                R.string.quebec_alt_8
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = "canada",
            associatedState = null,
            categories = listOf(FlagCategory.PROVINCE),
        ),

        "republicOfTheCongo" to FlagResources(
            id = 233,
            image = R.drawable.flag_of_the_republic_of_the_congo,
            imagePreview = R.drawable.flag_of_the_republic_of_the_congo,
            flagOf = R.string.republic_of_the_congo,
            flagOfOfficial = R.string.republic_of_the_congo_official,
            flagOfAlternate = listOf(
                R.string.republic_of_the_congo_alt_1,
                R.string.republic_of_the_congo_alt_2,
                R.string.republic_of_the_congo_alt_3,
                R.string.republic_of_the_congo_alt_4,
                R.string.republic_of_the_congo_alt_5,
                R.string.republic_of_the_congo_alt_6,
                R.string.republic_of_the_congo_alt_7,
                R.string.republic_of_the_congo_alt_8,
                R.string.republic_of_the_congo_alt_9,
                R.string.republic_of_the_congo_alt_10,
                R.string.republic_of_the_congo_alt_11
            ),
            isFlagOfThe = true,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.SEMI_PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "reunion" to FlagResources(
            id = 234,
            image = R.drawable.proposed_flag_of_r_union__var_,
            imagePreview = R.drawable.proposed_flag_of_r_union__var_,
            flagOf = R.string.reunion,
            flagOfOfficial = R.string.reunion_official,
            flagOfAlternate = listOf(
                R.string.reunion_alt_1,
                R.string.reunion_alt_2,
                R.string.reunion_alt_3,
                R.string.reunion_alt_4,
                R.string.reunion_alt_5,
                R.string.reunion_alt_6,
                R.string.reunion_alt_7,
                R.string.reunion_alt_8,
                R.string.reunion_alt_9,
                R.string.reunion_alt_10,
                R.string.reunion_alt_11
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = "france",
            associatedState = null,
            categories = listOf(FlagCategory.REGION),
        ),

        "romania" to FlagResources(
            id = 235,
            image = R.drawable.flag_of_romania,
            imagePreview = R.drawable.flag_of_romania,
            flagOf = R.string.romania,
            flagOfOfficial = R.string.romania_official,
            flagOfAlternate = listOf(
                R.string.romania_alt_1,
                R.string.romania_alt_2,
                R.string.romania_alt_3,
                R.string.romania_alt_4,
                R.string.romania_alt_5
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.SEMI_PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "russia" to FlagResources(
            id = 236,
            image = R.drawable.flag_of_russia,
            imagePreview = R.drawable.flag_of_russia,
            flagOf = R.string.russia,
            flagOfOfficial = R.string.russia_official,
            flagOfAlternate = listOf(
                R.string.russia_alt_1,
                R.string.russia_alt_2,
                R.string.russia_alt_3,
                R.string.russia_alt_4,
                R.string.russia_alt_5,
                R.string.russia_alt_6,
                R.string.russia_alt_7
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.FEDERAL,
                FlagCategory.SEMI_PRESIDENTIAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "rwanda" to FlagResources(
            id = 237,
            image = R.drawable.flag_of_rwanda,
            imagePreview = R.drawable.flag_of_rwanda,
            flagOf = R.string.rwanda,
            flagOfOfficial = R.string.rwanda_official,
            flagOfAlternate = listOf(
                R.string.rwanda_alt_1,
                R.string.rwanda_alt_2,
                R.string.rwanda_alt_3
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "saba" to FlagResources(
            id = 238,
            image = R.drawable.flag_of_saba,
            imagePreview = R.drawable.flag_of_saba,
            flagOf = R.string.saba,
            flagOfOfficial = R.string.saba_official,
            flagOfAlternate = null,
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = "netherlands",
            associatedState = null,
            categories = listOf(FlagCategory.MUNICIPALITY),
        ),

        "sahrawiArabDemocraticRepublic" to FlagResources(
            id = 239,
            image = R.drawable.flag_of_the_sahrawi_arab_democratic_republic,
            imagePreview = R.drawable.flag_of_the_sahrawi_arab_democratic_republic,
            flagOf = R.string.sahrawi_arab_democratic_republic,
            flagOfOfficial = R.string.sahrawi_arab_democratic_republic_official,
            flagOfAlternate = listOf(
                R.string.sahrawi_arab_democratic_republic_alt_1,
                R.string.sahrawi_arab_democratic_republic_alt_2,
                R.string.sahrawi_arab_democratic_republic_alt_3
            ),
            isFlagOfThe = true,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PRESIDENTIAL,
                FlagCategory.ONE_PARTY
            ),
        ),

        "saintBarthelemy" to FlagResources(
            id = 240,
            image = R.drawable.flag_of_saint_barth_lemy__local_,
            imagePreview = R.drawable.flag_of_saint_barth_lemy__local_,
            flagOf = R.string.saint_barthelemy,
            flagOfOfficial = R.string.saint_barthelemy_official,
            flagOfAlternate = listOf(
                R.string.saint_barthelemy_alt_1,
                R.string.saint_barthelemy_alt_2,
                R.string.saint_barthelemy_alt_3,
                R.string.saint_barthelemy_alt_4,
                R.string.saint_barthelemy_alt_5,
                R.string.saint_barthelemy_alt_6,
                R.string.saint_barthelemy_alt_7,
                R.string.saint_barthelemy_alt_8,
                R.string.saint_barthelemy_alt_9,
                R.string.saint_barthelemy_alt_10,
                R.string.saint_barthelemy_alt_11,
                R.string.saint_barthelemy_alt_12,
                R.string.saint_barthelemy_alt_13,
                R.string.saint_barthelemy_alt_14,
                R.string.saint_barthelemy_alt_15,
                R.string.saint_barthelemy_alt_16,
                R.string.saint_barthelemy_alt_17,
                R.string.saint_barthelemy_alt_18,
                R.string.saint_barthelemy_alt_19,
                R.string.saint_barthelemy_alt_20,
                R.string.saint_barthelemy_alt_21,
                R.string.saint_barthelemy_alt_22,
                R.string.saint_barthelemy_alt_23,
                R.string.saint_barthelemy_alt_24,
                R.string.saint_barthelemy_alt_25,
                R.string.saint_barthelemy_alt_26,
                R.string.saint_barthelemy_alt_27,
                R.string.saint_barthelemy_alt_28
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = "france",
            associatedState = null,
            categories = listOf(
                FlagCategory.AUTONOMOUS_REGION,
                FlagCategory.COLLECTIVITY
            ),
        ),

        "saintHelena" to FlagResources(
            id = 241,
            image = R.drawable.flag_of_saint_helena,
            imagePreview = R.drawable.flag_of_saint_helena_preview,
            flagOf = R.string.saint_helena,
            flagOfOfficial = R.string.saint_helena_official,
            flagOfAlternate = listOf(
                R.string.saint_helena_alt_1,
                R.string.saint_helena_alt_2,
                R.string.saint_helena_alt_3,
                R.string.saint_helena_alt_4
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = "unitedKingdom",
            associatedState = null,
            categories = listOf(FlagCategory.TERRITORY),
        ),

        "saintKittsAndNevis" to FlagResources(
            id = 242,
            image = R.drawable.flag_of_saint_kitts_and_nevis,
            imagePreview = R.drawable.flag_of_saint_kitts_and_nevis,
            flagOf = R.string.saint_kitts_and_nevis,
            flagOfOfficial = R.string.saint_kitts_and_nevis_official,
            flagOfAlternate = listOf(
                R.string.saint_kitts_and_nevis_alt_1,
                R.string.saint_kitts_and_nevis_alt_2,
                R.string.saint_kitts_and_nevis_alt_3,
                R.string.saint_kitts_and_nevis_alt_4,
                R.string.saint_kitts_and_nevis_alt_5,
                R.string.saint_kitts_and_nevis_alt_6,
                R.string.saint_kitts_and_nevis_alt_7,
                R.string.saint_kitts_and_nevis_alt_8,
                R.string.saint_kitts_and_nevis_alt_9
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.FEDERAL,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.MONARCHY
            ),
        ),

        "saintLucia" to FlagResources(
            id = 243,
            image = R.drawable.flag_of_saint_lucia,
            imagePreview = R.drawable.flag_of_saint_lucia,
            flagOf = R.string.saint_lucia,
            flagOfOfficial = R.string.saint_lucia_official,
            flagOfAlternate = listOf(
                R.string.saint_lucia_alt_1,
                R.string.saint_lucia_alt_2,
                R.string.saint_lucia_alt_3,
                R.string.saint_lucia_alt_4,
                R.string.saint_lucia_alt_5,
                R.string.saint_lucia_alt_6
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.MONARCHY
            ),
        ),

        "saintPierreAndMiquelon" to FlagResources(
            id = 244,
            image = R.drawable.flag_of_saint_pierre_and_miquelon,
            imagePreview = R.drawable.flag_of_saint_pierre_and_miquelon_preview,
            flagOf = R.string.saint_pierre_and_miquelon,
            flagOfOfficial = R.string.saint_pierre_and_miquelon_official,
            flagOfAlternate = listOf(
                R.string.saint_pierre_and_miquelon_alt_1,
                R.string.saint_pierre_and_miquelon_alt_2,
                R.string.saint_pierre_and_miquelon_alt_3,
                R.string.saint_pierre_and_miquelon_alt_4,
                R.string.saint_pierre_and_miquelon_alt_5,
                R.string.saint_pierre_and_miquelon_alt_6,
                R.string.saint_pierre_and_miquelon_alt_7,
                R.string.saint_pierre_and_miquelon_alt_8,
                R.string.saint_pierre_and_miquelon_alt_9,
                R.string.saint_pierre_and_miquelon_alt_10,
                R.string.saint_pierre_and_miquelon_alt_11,
                R.string.saint_pierre_and_miquelon_alt_12,
                R.string.saint_pierre_and_miquelon_alt_13,
                R.string.saint_pierre_and_miquelon_alt_14,
                R.string.saint_pierre_and_miquelon_alt_15
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = "france",
            associatedState = null,
            categories = listOf(
                FlagCategory.AUTONOMOUS_REGION,
                FlagCategory.COLLECTIVITY
            ),
        ),

        "saintVincentAndTheGrenadines" to FlagResources(
            id = 245,
            image = R.drawable.flag_of_saint_vincent_and_the_grenadines,
            imagePreview = R.drawable.flag_of_saint_vincent_and_the_grenadines,
            flagOf = R.string.saint_vincent_and_the_grenadines,
            flagOfOfficial = R.string.saint_vincent_and_the_grenadines_official,
            flagOfAlternate = listOf(
                R.string.saint_vincent_and_the_grenadines_alt_1,
                R.string.saint_vincent_and_the_grenadines_alt_2,
                R.string.saint_vincent_and_the_grenadines_alt_3,
                R.string.saint_vincent_and_the_grenadines_alt_4,
                R.string.saint_vincent_and_the_grenadines_alt_5,
                R.string.saint_vincent_and_the_grenadines_alt_6,
                R.string.saint_vincent_and_the_grenadines_alt_7
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.MONARCHY
            ),
        ),

        "sakha" to FlagResources(
            id = 246,
            image = R.drawable.flag_of_sakha,
            imagePreview = R.drawable.flag_of_sakha_preview,
            flagOf = R.string.sakha,
            flagOfOfficial = R.string.sakha_official,
            flagOfAlternate = listOf(
                R.string.sakha_alt_1,
                R.string.sakha_alt_2,
                R.string.sakha_alt_3,
                R.string.sakha_alt_4,
                R.string.sakha_alt_5
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = "russia",
            associatedState = null,
            categories = listOf(
                FlagCategory.ETHNIC,
                FlagCategory.REPUBLIC_UNIT
            ),
        ),

        "samoa" to FlagResources(
            id = 247,
            image = R.drawable.flag_of_samoa,
            imagePreview = R.drawable.flag_of_samoa,
            flagOf = R.string.samoa,
            flagOfOfficial = R.string.samoa_official,
            flagOfAlternate = listOf(
                R.string.samoa_alt_1,
                R.string.samoa_alt_2,
                R.string.samoa_alt_3,
                R.string.samoa_alt_4
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "sanMarino" to FlagResources(
            id = 248,
            image = R.drawable.flag_of_san_marino,
            imagePreview = R.drawable.flag_of_san_marino_preview,
            flagOf = R.string.san_marino,
            flagOfOfficial = R.string.san_marino_official,
            flagOfAlternate = listOf(
                R.string.san_marino_alt_1,
                R.string.san_marino_alt_2,
                R.string.san_marino_alt_3
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "saoTomeAndPrincipe" to FlagResources(
            id = 249,
            image = R.drawable.flag_of_s_o_tom__and_pr_ncipe,
            imagePreview = R.drawable.flag_of_s_o_tom__and_pr_ncipe,
            flagOf = R.string.sao_tome_and_principe,
            flagOfOfficial = R.string.sao_tome_and_principe_official,
            flagOfAlternate = listOf(
                R.string.sao_tome_and_principe_alt_1,
                R.string.sao_tome_and_principe_alt_2,
                R.string.sao_tome_and_principe_alt_3,
                R.string.sao_tome_and_principe_alt_4,
                R.string.sao_tome_and_principe_alt_5,
                R.string.sao_tome_and_principe_alt_6
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.SEMI_PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "sapmi" to FlagResources(
            id = 250,
            image = R.drawable.sami_flag,
            imagePreview = R.drawable.sami_flag,
            flagOf = R.string.sapmi,
            flagOfOfficial = R.string.sapmi_official,
            flagOfAlternate = listOf(
                R.string.sapmi_alt_1,
                R.string.sapmi_alt_2
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.ETHNIC,
                FlagCategory.REGIONAL
            ),
        ),

        "sardinia" to FlagResources(
            id = 251,
            image = R.drawable.flag_of_sardinia,
            imagePreview = R.drawable.flag_of_sardinia,
            flagOf = R.string.sardinia,
            flagOfOfficial = R.string.sardinia_official,
            flagOfAlternate = listOf(
                R.string.sardinia_alt_1,
                R.string.sardinia_alt_2,
                R.string.sardinia_alt_3,
                R.string.sardinia_alt_4,
                R.string.sardinia_alt_5,
                R.string.sardinia_alt_6
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = "italy",
            associatedState = null,
            categories = listOf(
                FlagCategory.AUTONOMOUS_REGION,
                FlagCategory.REGION,
                FlagCategory.DEVOLVED_GOVERNMENT
            ),
        ),

        "saudiArabia" to FlagResources(
            id = 252,
            image = R.drawable.flag_of_saudi_arabia,
            imagePreview = R.drawable.flag_of_saudi_arabia,
            flagOf = R.string.saudi_arabia,
            flagOfOfficial = R.string.saudi_arabia_official,
            flagOfAlternate = listOf(
                R.string.saudi_arabia_alt_1,
                R.string.saudi_arabia_alt_2,
                R.string.saudi_arabia_alt_3,
                R.string.saudi_arabia_alt_4,
                R.string.saudi_arabia_alt_5,
                R.string.saudi_arabia_alt_6,
                R.string.saudi_arabia_alt_7,
                R.string.saudi_arabia_alt_8,
                R.string.saudi_arabia_alt_9
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.THEOCRATIC,
                FlagCategory.MONARCHY
            ),
        ),

        "scotland" to FlagResources(
            id = 253,
            image = R.drawable.flag_of_scotland,
            imagePreview = R.drawable.flag_of_scotland,
            flagOf = R.string.scotland,
            flagOfOfficial = R.string.scotland_official,
            flagOfAlternate = listOf(
                R.string.scotland_alt_1,
                R.string.scotland_alt_2,
                R.string.scotland_alt_3,
                R.string.scotland_alt_4,
                R.string.scotland_alt_5,
                R.string.scotland_alt_6,
                R.string.scotland_alt_7,
                R.string.scotland_alt_8
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = "unitedKingdom",
            associatedState = null,
            categories = listOf(
                FlagCategory.COUNTRY,
                FlagCategory.DEVOLVED_GOVERNMENT
            ),
        ),

        "senegal" to FlagResources(
            id = 254,
            image = R.drawable.flag_of_senegal,
            imagePreview = R.drawable.flag_of_senegal,
            flagOf = R.string.senegal,
            flagOfOfficial = R.string.senegal_official,
            flagOfAlternate = listOf(
                R.string.senegal_alt_1,
                R.string.senegal_alt_2,
                R.string.senegal_alt_3,
                R.string.senegal_alt_4
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "serbia" to FlagResources(
            id = 255,
            image = R.drawable.flag_of_serbia,
            imagePreview = R.drawable.flag_of_serbia_preview,
            flagOf = R.string.serbia,
            flagOfOfficial = R.string.serbia_official,
            flagOfAlternate = listOf(
                R.string.serbia_alt_1,
                R.string.serbia_alt_2,
                R.string.serbia_alt_3,
                R.string.serbia_alt_4,
                R.string.serbia_alt_5
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "seychelles" to FlagResources(
            id = 256,
            image = R.drawable.flag_of_seychelles,
            imagePreview = R.drawable.flag_of_seychelles,
            flagOf = R.string.seychelles,
            flagOfOfficial = R.string.seychelles_official,
            flagOfAlternate = listOf(
                R.string.seychelles_alt_1,
                R.string.seychelles_alt_2,
                R.string.seychelles_alt_3,
                R.string.seychelles_alt_4
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "sierraLeone" to FlagResources(
            id = 257,
            image = R.drawable.flag_of_sierra_leone,
            imagePreview = R.drawable.flag_of_sierra_leone,
            flagOf = R.string.sierra_leone,
            flagOfOfficial = R.string.sierra_leone_official,
            flagOfAlternate = listOf(
                R.string.sierra_leone_alt_1,
                R.string.sierra_leone_alt_2,
                R.string.sierra_leone_alt_3
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "singapore" to FlagResources(
            id = 258,
            image = R.drawable.flag_of_singapore,
            imagePreview = R.drawable.flag_of_singapore,
            flagOf = R.string.singapore,
            flagOfOfficial = R.string.singapore_official,
            flagOfAlternate = listOf(
                R.string.singapore_alt_1,
                R.string.singapore_alt_2,
                R.string.singapore_alt_3,
                R.string.singapore_alt_4,
                R.string.singapore_alt_5,
                R.string.singapore_alt_6,
                R.string.singapore_alt_7,
                R.string.singapore_alt_8,
                R.string.singapore_alt_9
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "sintEustatius" to FlagResources(
            id = 259,
            image = R.drawable.flag_of_sint_eustatius,
            imagePreview = R.drawable.flag_of_sint_eustatius,
            flagOf = R.string.sint_eustatius,
            flagOfOfficial = R.string.sint_eustatius_official,
            flagOfAlternate = listOf(
                R.string.sint_eustatius_alt_1,
                R.string.sint_eustatius_alt_2,
                R.string.sint_eustatius_alt_3,
                R.string.sint_eustatius_alt_4
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = "netherlands",
            associatedState = null,
            categories = listOf(FlagCategory.MUNICIPALITY),
        ),

        "sintMaarten" to FlagResources(
            id = 260,
            image = R.drawable.flag_of_sint_maarten,
            imagePreview = R.drawable.flag_of_sint_maarten,
            flagOf = R.string.sint_maarten,
            flagOfOfficial = R.string.sint_maarten_official,
            flagOfAlternate = listOf(
                R.string.sint_maarten_alt_1,
                R.string.sint_maarten_alt_2,
                R.string.sint_maarten_alt_3,
                R.string.sint_maarten_alt_4,
                R.string.sint_maarten_alt_5,
                R.string.sint_maarten_alt_6,
                R.string.sint_maarten_alt_7,
                R.string.sint_maarten_alt_8,
                R.string.sint_maarten_alt_9
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = "netherlands",
            associatedState = null,
            categories = listOf(
                FlagCategory.AUTONOMOUS_REGION,
                FlagCategory.COUNTRY
            ),
        ),

        "slovakia" to FlagResources(
            id = 261,
            image = R.drawable.flag_of_slovakia,
            imagePreview = R.drawable.flag_of_slovakia,
            flagOf = R.string.slovakia,
            flagOfOfficial = R.string.slovakia_official,
            flagOfAlternate = listOf(
                R.string.slovakia_alt_1,
                R.string.slovakia_alt_2,
                R.string.slovakia_alt_3
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "slovenia" to FlagResources(
            id = 262,
            image = R.drawable.flag_of_slovenia,
            imagePreview = R.drawable.flag_of_slovenia,
            flagOf = R.string.slovenia,
            flagOfOfficial = R.string.slovenia_official,
            flagOfAlternate = listOf(
                R.string.slovenia_alt_1,
                R.string.slovenia_alt_2,
                R.string.slovenia_alt_3,
                R.string.slovenia_alt_4,
                R.string.slovenia_alt_5,
                R.string.slovenia_alt_6,
                R.string.slovenia_alt_7
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "solomonIslands" to FlagResources(
            id = 263,
            image = R.drawable.flag_of_the_solomon_islands,
            imagePreview = R.drawable.flag_of_the_solomon_islands,
            flagOf = R.string.solomon_islands,
            flagOfOfficial = R.string.solomon_islands_official,
            flagOfAlternate = listOf(
                R.string.solomon_islands_alt_1,
                R.string.solomon_islands_alt_2
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.MONARCHY
            ),
        ),

        "somalia" to FlagResources(
            id = 264,
            image = R.drawable.flag_of_somalia,
            imagePreview = R.drawable.flag_of_somalia,
            flagOf = R.string.somalia,
            flagOfOfficial = R.string.somalia_official,
            flagOfAlternate = listOf(R.string.somalia_alt),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.FEDERAL,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "somaliland" to FlagResources(
            id = 265,
            image = R.drawable.flag_of_somaliland,
            imagePreview = R.drawable.flag_of_somaliland,
            flagOf = R.string.somaliland,
            flagOfOfficial = R.string.somaliland_official,
            flagOfAlternate = listOf(R.string.somaliland_alt),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = "somalia",
            associatedState = null,
            categories = listOf(FlagCategory.AUTONOMOUS_REGION),
        ),

        "southAfrica" to FlagResources(
            id = 266,
            image = R.drawable.flag_of_south_africa,
            imagePreview = R.drawable.flag_of_south_africa,
            flagOf = R.string.south_africa,
            flagOfOfficial = R.string.south_africa_official,
            flagOfAlternate = listOf(
                R.string.south_africa_alt_1,
                R.string.south_africa_alt_2,
                R.string.south_africa_alt_3,
                R.string.south_africa_alt_4,
                R.string.south_africa_alt_5,
                R.string.south_africa_alt_6
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "southGeorgiaAndTheSouthSandwichIslands" to FlagResources(
            id = 267,
            image = R.drawable.flag_of_south_georgia_and_the_south_sandwich_islands,
            imagePreview = R.drawable.flag_of_south_georgia_and_the_south_sandwich_islands_preview,
            flagOf = R.string.south_georgia_and_the_south_sandwich_islands,
            flagOfOfficial = R.string.south_georgia_and_the_south_sandwich_islands_official,
            flagOfAlternate = listOf(
                R.string.south_georgia_and_the_south_sandwich_islands_alt_1,
                R.string.south_georgia_and_the_south_sandwich_islands_alt_2,
                R.string.south_georgia_and_the_south_sandwich_islands_alt_3,
                R.string.south_georgia_and_the_south_sandwich_islands_alt_4,
                R.string.south_georgia_and_the_south_sandwich_islands_alt_5
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = "unitedKingdom",
            associatedState = null,
            categories = listOf(FlagCategory.TERRITORY),
        ),

        "southKorea" to FlagResources(
            id = 268,
            image = R.drawable.flag_of_south_korea,
            imagePreview = R.drawable.flag_of_south_korea,
            flagOf = R.string.south_korea,
            flagOfOfficial = R.string.south_korea_official,
            flagOfAlternate = listOf(
                R.string.south_korea_alt_1,
                R.string.south_korea_alt_2,
                R.string.south_korea_alt_3,
                R.string.south_korea_alt_4,
                R.string.south_korea_alt_5,
                R.string.south_korea_alt_6,
                R.string.south_korea_alt_7,
                R.string.south_korea_alt_8,
                R.string.south_korea_alt_9,
                R.string.south_korea_alt_10,
                R.string.south_korea_alt_11,
                R.string.south_korea_alt_12,
                R.string.south_korea_alt_13,
                R.string.south_korea_alt_14,
                R.string.south_korea_alt_15
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "southOssetia" to FlagResources(
            id = 269,
            image = R.drawable.flag_of_south_ossetia,
            imagePreview = R.drawable.flag_of_south_ossetia,
            flagOf = R.string.south_ossetia,
            flagOfOfficial = R.string.south_ossetia_official,
            flagOfAlternate = listOf(
                R.string.south_ossetia_alt_1,
                R.string.south_ossetia_alt_2,
                R.string.south_ossetia_alt_3,
                R.string.south_ossetia_alt_4,
                R.string.south_ossetia_alt_5,
                R.string.south_ossetia_alt_6,
                R.string.south_ossetia_alt_7,
                R.string.south_ossetia_alt_8,
                R.string.south_ossetia_alt_9,
                R.string.south_ossetia_alt_10,
                R.string.south_ossetia_alt_11,
                R.string.south_ossetia_alt_12
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = "georgia",
            associatedState = null,
            categories = listOf(FlagCategory.AUTONOMOUS_REGION),
        ),

        "southSudan" to FlagResources(
            id = 270,
            image = R.drawable.flag_of_south_sudan,
            imagePreview = R.drawable.flag_of_south_sudan,
            flagOf = R.string.south_sudan,
            flagOfOfficial = R.string.south_sudan_official,
            flagOfAlternate = listOf(
                R.string.south_sudan_alt_1,
                R.string.south_sudan_alt_2,
                R.string.south_sudan_alt_3,
                R.string.south_sudan_alt_4
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.FEDERAL,
                FlagCategory.PRESIDENTIAL,
                FlagCategory.PROVISIONAL_GOVERNMENT
            ),
        ),

        "sovietUnion" to FlagResources(
            id = 271,
            image = R.drawable.flag_of_the_soviet_union,
            imagePreview = R.drawable.flag_of_the_soviet_union,
            flagOf = R.string.soviet_union,
            flagOfOfficial = R.string.soviet_union_official,
            flagOfAlternate = listOf(
                R.string.soviet_union_alt_1,
                R.string.soviet_union_alt_2,
                R.string.soviet_union_alt_3,
                R.string.soviet_union_alt_4,
                R.string.soviet_union_alt_5,
                R.string.soviet_union_alt_6,
                R.string.soviet_union_alt_7,
                R.string.soviet_union_alt_8,
                R.string.soviet_union_alt_9,
                R.string.soviet_union_alt_10,
                R.string.soviet_union_alt_11,
                R.string.soviet_union_alt_12
            ),
            isFlagOfThe = true,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.HISTORICAL,
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.FEDERAL,
                FlagCategory.DIRECTORIAL,
                FlagCategory.SOCIALIST,
                FlagCategory.ONE_PARTY
            ),
        ),

        "spain" to FlagResources(
            id = 272,
            image = R.drawable.flag_of_spain,
            imagePreview = R.drawable.flag_of_spain_preview,
            flagOf = R.string.spain,
            flagOfOfficial = R.string.spain_official,
            flagOfAlternate = listOf(R.string.spain_alt),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.MONARCHY
            ),
        ),

        "sriLanka" to FlagResources(
            id = 273,
            image = R.drawable.flag_of_sri_lanka,
            imagePreview = R.drawable.flag_of_sri_lanka,
            flagOf = R.string.sri_lanka,
            flagOfOfficial = R.string.sri_lanka_official,
            flagOfAlternate = listOf(
                R.string.sri_lanka_alt_1,
                R.string.sri_lanka_alt_2,
                R.string.sri_lanka_alt_3,
                R.string.sri_lanka_alt_4,
                R.string.sri_lanka_alt_5,
                R.string.sri_lanka_alt_6,
                R.string.sri_lanka_alt_7,
                R.string.sri_lanka_alt_8,
                R.string.sri_lanka_alt_9,
                R.string.sri_lanka_alt_10,
                R.string.sri_lanka_alt_11,
                R.string.sri_lanka_alt_12,
                R.string.sri_lanka_alt_13,
                R.string.sri_lanka_alt_14
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.SEMI_PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "sudan" to FlagResources(
            id = 274,
            image = R.drawable.flag_of_sudan,
            imagePreview = R.drawable.flag_of_sudan,
            flagOf = R.string.sudan,
            flagOfOfficial = R.string.sudan_official,
            flagOfAlternate = listOf(
                R.string.sudan_alt_1,
                R.string.sudan_alt_2,
                R.string.sudan_alt_3,
                R.string.sudan_alt_4,
                R.string.sudan_alt_5,
                R.string.sudan_alt_6,
                R.string.sudan_alt_7,
                R.string.sudan_alt_8,
                R.string.sudan_alt_9,
                R.string.sudan_alt_10
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.FEDERAL,
                FlagCategory.PRESIDENTIAL,
                FlagCategory.MILITARY_JUNTA
            ),
        ),

        "sultanateOfZanzibar" to FlagResources(
            id = 275,
            image = R.drawable.flag_of_the_sultanate_of_zanzibar__1963_,
            imagePreview = R.drawable.flag_of_the_sultanate_of_zanzibar__1963_,
            flagOf = R.string.sultanate_of_zanzibar,
            flagOfOfficial = R.string.sultanate_of_zanzibar_official,
            flagOfAlternate = listOf(R.string.sultanate_of_zanzibar_alt),
            isFlagOfThe = true,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.HISTORICAL,
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.THEOCRATIC,
                FlagCategory.MONARCHY
            ),
        ),

        "suriname" to FlagResources(
            id = 276,
            image = R.drawable.flag_of_suriname,
            imagePreview = R.drawable.flag_of_suriname,
            flagOf = R.string.suriname,
            flagOfOfficial = R.string.suriname_official,
            flagOfAlternate = listOf(
                R.string.suriname_alt_1,
                R.string.suriname_alt_2,
                R.string.suriname_alt_3,
                R.string.suriname_alt_4,
                R.string.suriname_alt_5,
                R.string.suriname_alt_6,
                R.string.suriname_alt_7
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "sweden" to FlagResources(
            id = 277,
            image = R.drawable.flag_of_sweden,
            imagePreview = R.drawable.flag_of_sweden,
            flagOf = R.string.sweden,
            flagOfOfficial = R.string.sweden_official,
            flagOfAlternate = listOf(
                R.string.sweden_alt_1,
                R.string.sweden_alt_2,
                R.string.sweden_alt_3,
                R.string.sweden_alt_4,
                R.string.sweden_alt_5,
                R.string.sweden_alt_6
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.MONARCHY
            ),
        ),

        "switzerland" to FlagResources(
            id = 278,
            image = R.drawable.flag_of_switzerland__pantone_,
            imagePreview = R.drawable.flag_of_switzerland__pantone_,
            flagOf = R.string.switzerland,
            flagOfOfficial = R.string.switzerland_official,
            flagOfAlternate = listOf(
                R.string.switzerland_alt_1,
                R.string.switzerland_alt_2,
                R.string.switzerland_alt_3
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.FEDERAL,
                FlagCategory.DIRECTORIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "syria" to FlagResources(
            id = 279,
            image = R.drawable.flag_of_the_syrian_revolution,
            imagePreview = R.drawable.flag_of_the_syrian_revolution,
            flagOf = R.string.syria,
            flagOfOfficial = R.string.syria_official,
            flagOfAlternate = listOf(
                R.string.syria_alt_1,
                R.string.syria_alt_2,
                R.string.syria_alt_3,
                R.string.syria_alt_4,
                R.string.syria_alt_5
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PROVISIONAL_GOVERNMENT
            ),
        ),

        "taiwan" to FlagResources(
            id = 280,
            image = R.drawable.flag_of_the_republic_of_china,
            imagePreview = R.drawable.flag_of_the_republic_of_china,
            flagOf = R.string.taiwan,
            flagOfOfficial = R.string.taiwan_official,
            flagOfAlternate = listOf(
                R.string.taiwan_alt_1,
                R.string.taiwan_alt_2,
                R.string.taiwan_alt_3,
                R.string.taiwan_alt_4,
                R.string.taiwan_alt_5,
                R.string.taiwan_alt_6,
                R.string.taiwan_alt_7,
                R.string.taiwan_alt_8,
                R.string.taiwan_alt_9,
                R.string.taiwan_alt_10,
                R.string.taiwan_alt_11,
                R.string.taiwan_alt_12,
                R.string.taiwan_alt_13,
                R.string.taiwan_alt_14,
                R.string.taiwan_alt_15,
                R.string.taiwan_alt_16,
                R.string.taiwan_alt_17,
                R.string.taiwan_alt_18,
                R.string.taiwan_alt_19,
                R.string.taiwan_alt_20,
                R.string.taiwan_alt_21
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.SEMI_PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "tajikistan" to FlagResources(
            id = 281,
            image = R.drawable.flag_of_tajikistan,
            imagePreview = R.drawable.flag_of_tajikistan,
            flagOf = R.string.tajikistan,
            flagOfOfficial = R.string.tajikistan_official,
            flagOfAlternate = listOf(
                R.string.tajikistan_alt_1,
                R.string.tajikistan_alt_2,
                R.string.tajikistan_alt_3
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "tanzania" to FlagResources(
            id = 282,
            image = R.drawable.flag_of_tanzania,
            imagePreview = R.drawable.flag_of_tanzania,
            flagOf = R.string.tanzania,
            flagOfOfficial = R.string.tanzania_official,
            flagOfAlternate = listOf(
                R.string.tanzania_alt_1,
                R.string.tanzania_alt_2,
                R.string.tanzania_alt_3,
                R.string.tanzania_alt_4,
                R.string.tanzania_alt_5
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "teutonicOrder" to FlagResources(
            id = 283,
            image = R.drawable.flag_of_the_state_of_the_teutonic_order,
            imagePreview = R.drawable.flag_of_the_state_of_the_teutonic_order,
            flagOf = R.string.teutonic_order,
            flagOfOfficial = R.string.teutonic_order_official,
            flagOfAlternate = listOf(
                R.string.teutonic_order_alt_1,
                R.string.teutonic_order_alt_2,
                R.string.teutonic_order_alt_3,
                R.string.teutonic_order_alt_4,
                R.string.teutonic_order_alt_5
            ),
            isFlagOfThe = true,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(FlagCategory.RELIGIOUS),
        ),

        "thailand" to FlagResources(
            id = 284,
            image = R.drawable.flag_of_thailand,
            imagePreview = R.drawable.flag_of_thailand,
            flagOf = R.string.thailand,
            flagOfOfficial = R.string.thailand_official,
            flagOfAlternate = listOf(
                R.string.thailand_alt_1,
                R.string.thailand_alt_2,
                R.string.thailand_alt_3,
                R.string.thailand_alt_4,
                R.string.thailand_alt_5
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.MONARCHY
            ),
        ),

        "theBahamas" to FlagResources(
            id = 285,
            image = R.drawable.flag_of_the_bahamas,
            imagePreview = R.drawable.flag_of_the_bahamas,
            flagOf = R.string.the_bahamas,
            flagOfOfficial = R.string.the_bahamas_official,
            flagOfAlternate = listOf(
                R.string.the_bahamas_alt_1,
                R.string.the_bahamas_alt_2,
                R.string.the_bahamas_alt_3,
                R.string.the_bahamas_alt_4,
                R.string.the_bahamas_alt_5,
                R.string.the_bahamas_alt_6
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.MONARCHY
            ),
        ),

        "theGambia" to FlagResources(
            id = 286,
            image = R.drawable.flag_of_the_gambia,
            imagePreview = R.drawable.flag_of_the_gambia,
            flagOf = R.string.the_gambia,
            flagOfOfficial = R.string.the_gambia_official,
            flagOfAlternate = listOf(
                R.string.the_gambia_alt_1,
                R.string.the_gambia_alt_2,
                R.string.the_gambia_alt_3,
                R.string.the_gambia_alt_4,
                R.string.the_gambia_alt_5,
                R.string.the_gambia_alt_6
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "tibet" to FlagResources(
            id = 287,
            image = R.drawable.flag_of_tibet,
            imagePreview = R.drawable.flag_of_tibet_preview,
            flagOf = R.string.tibet,
            flagOfOfficial = R.string.tibet_official,
            flagOfAlternate = listOf(
                R.string.tibet_alt_1,
                R.string.tibet_alt_2,
                R.string.tibet_alt_3,
                R.string.tibet_alt_4,
                R.string.tibet_alt_5,
                R.string.tibet_alt_6,
                R.string.tibet_alt_7,
                R.string.tibet_alt_8,
                R.string.tibet_alt_9,
                R.string.tibet_alt_10
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = "china",
            associatedState = null,
            categories = listOf(
                FlagCategory.ETHNIC,
                FlagCategory.REGION
            ),
        ),

        "togo" to FlagResources(
            id = 288,
            image = R.drawable.flag_of_togo,
            imagePreview = R.drawable.flag_of_togo,
            flagOf = R.string.togo,
            flagOfOfficial = R.string.togo_official,
            flagOfAlternate = listOf(
                R.string.togo_alt_1,
                R.string.togo_alt_2,
                R.string.togo_alt_3
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "tokelau" to FlagResources(
            id = 289,
            image = R.drawable.flag_of_tokelau,
            imagePreview = R.drawable.flag_of_tokelau,
            flagOf = R.string.tokelau,
            flagOfOfficial = R.string.tokelau_official,
            flagOfAlternate = listOf(
                R.string.tokelau_alt_1,
                R.string.tokelau_alt_2,
                R.string.tokelau_alt_3
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = "newZealand",
            associatedState = null,
            categories = listOf(
                FlagCategory.TERRITORY,
                FlagCategory.DEVOLVED_GOVERNMENT
            ),
        ),

        "tonga" to FlagResources(
            id = 290,
            image = R.drawable.flag_of_tonga,
            imagePreview = R.drawable.flag_of_tonga,
            flagOf = R.string.tonga,
            flagOfOfficial = R.string.tonga_official,
            flagOfAlternate = listOf(
                R.string.tonga_alt_1,
                R.string.tonga_alt_2,
                R.string.tonga_alt_3,
                R.string.tonga_alt_4,
                R.string.tonga_alt_5
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.MONARCHY
            ),
        ),

        "transgender" to FlagResources(
            id = 291,
            image = R.drawable.transgender_pride_flag,
            imagePreview = R.drawable.transgender_pride_flag,
            flagOf = R.string.transgender,
            flagOfOfficial = R.string.transgender_official,
            flagOfAlternate = listOf(
                R.string.transgender_alt_1,
                R.string.transgender_alt_2,
                R.string.transgender_alt_3,
                R.string.transgender_alt_4,
                R.string.transgender_alt_5,
                R.string.transgender_alt_6,
                R.string.transgender_alt_7,
                R.string.transgender_alt_8,
                R.string.transgender_alt_9,
                R.string.transgender_alt_10,
                R.string.transgender_alt_11,
                R.string.transgender_alt_12,
                R.string.transgender_alt_13,
                R.string.transgender_alt_14,
                R.string.transgender_alt_15,
                R.string.transgender_alt_16,
                R.string.transgender_alt_17,
                R.string.transgender_alt_18,
                R.string.transgender_alt_19
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = null,
            associatedState = null,
            categories = listOf(FlagCategory.SOCIAL),
        ),

        "transnistria" to FlagResources(
            id = 292,
            image = R.drawable.flag_of_transnistria__state_,
            imagePreview = R.drawable.flag_of_transnistria__state_,
            flagOf = R.string.transnistria,
            flagOfOfficial = R.string.transnistria_official,
            flagOfAlternate = listOf(
                R.string.transnistria_alt_1,
                R.string.transnistria_alt_2,
                R.string.transnistria_alt_3,
                R.string.transnistria_alt_4,
                R.string.transnistria_alt_5,
                R.string.transnistria_alt_6,
                R.string.transnistria_alt_7,
                R.string.transnistria_alt_8,
                R.string.transnistria_alt_9,
                R.string.transnistria_alt_10,
                R.string.transnistria_alt_11
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = "moldova",
            associatedState = null,
            categories = listOf(FlagCategory.AUTONOMOUS_REGION),
        ),

        "trinidadAndTobago" to FlagResources(
            id = 293,
            image = R.drawable.flag_of_trinidad_and_tobago,
            imagePreview = R.drawable.flag_of_trinidad_and_tobago,
            flagOf = R.string.trinidad_and_tobago,
            flagOfOfficial = R.string.trinidad_and_tobago_official,
            flagOfAlternate = listOf(
                R.string.trinidad_and_tobago_alt_1,
                R.string.trinidad_and_tobago_alt_2,
                R.string.trinidad_and_tobago_alt_3,
                R.string.trinidad_and_tobago_alt_4,
                R.string.trinidad_and_tobago_alt_5
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "tristanDaCunha" to FlagResources(
            id = 294,
            image = R.drawable.flag_of_tristan_da_cunha,
            imagePreview = R.drawable.flag_of_tristan_da_cunha_preview,
            flagOf = R.string.tristan_da_cunha,
            flagOfOfficial = R.string.tristan_da_cunha_official,
            flagOfAlternate = listOf(
                R.string.tristan_da_cunha_alt_1,
                R.string.tristan_da_cunha_alt_2,
                R.string.tristan_da_cunha_alt_3,
                R.string.tristan_da_cunha_alt_4
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = "unitedKingdom",
            associatedState = null,
            categories = listOf(FlagCategory.TERRITORY),
        ),

        "tunisia" to FlagResources(
            id = 295,
            image = R.drawable.flag_of_tunisia,
            imagePreview = R.drawable.flag_of_tunisia,
            flagOf = R.string.tunisia,
            flagOfOfficial = R.string.tunisia_official,
            flagOfAlternate = listOf(
                R.string.tunisia_alt_1,
                R.string.tunisia_alt_2,
                R.string.tunisia_alt_3,
                R.string.tunisia_alt_4
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "turkey" to FlagResources(
            id = 296,
            image = R.drawable.flag_of_turkey,
            imagePreview = R.drawable.flag_of_turkey,
            flagOf = R.string.turkey,
            flagOfOfficial = R.string.turkey_official,
            flagOfAlternate = listOf(
                R.string.turkey_alt_1,
                R.string.turkey_alt_2,
                R.string.turkey_alt_3,
                R.string.turkey_alt_4,
                R.string.turkey_alt_5,
                R.string.turkey_alt_6
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "turkmenistan" to FlagResources(
            id = 297,
            image = R.drawable.flag_of_turkmenistan,
            imagePreview = R.drawable.flag_of_turkmenistan_preview,
            flagOf = R.string.turkmenistan,
            flagOfOfficial = R.string.turkmenistan_official,
            flagOfAlternate = listOf(
                R.string.turkmenistan_alt_1,
                R.string.turkmenistan_alt_2,
                R.string.turkmenistan_alt_3
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "turksAndCaicosIslands" to FlagResources(
            id = 298,
            image = R.drawable.flag_of_the_turks_and_caicos_islands,
            imagePreview = R.drawable.flag_of_the_turks_and_caicos_islands,
            flagOf = R.string.turks_and_caicos_islands,
            flagOfOfficial = R.string.turks_and_caicos_islands_official,
            flagOfAlternate = listOf(
                R.string.turks_and_caicos_islands_alt_1,
                R.string.turks_and_caicos_islands_alt_2,
                R.string.turks_and_caicos_islands_alt_3,
                R.string.turks_and_caicos_islands_alt_4
            ),
            isFlagOfThe = true,
            isFlagOfOfficialThe = true,
            sovereignState = "unitedKingdom",
            associatedState = null,
            categories = listOf(
                FlagCategory.AUTONOMOUS_REGION,
                FlagCategory.TERRITORY
            ),
        ),

        "tuvalu" to FlagResources(
            id = 299,
            image = R.drawable.flag_of_tuvalu,
            imagePreview = R.drawable.flag_of_tuvalu,
            flagOf = R.string.tuvalu,
            flagOfOfficial = R.string.tuvalu_official,
            flagOfAlternate = listOf(
                R.string.tuvalu_alt_1,
                R.string.tuvalu_alt_2,
                R.string.tuvalu_alt_3
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.MONARCHY
            ),
        ),

        "uganda" to FlagResources(
            id = 300,
            image = R.drawable.flag_of_uganda,
            imagePreview = R.drawable.flag_of_uganda,
            flagOf = R.string.uganda,
            flagOfOfficial = R.string.uganda_official,
            flagOfAlternate = listOf(
                R.string.uganda_alt_1,
                R.string.uganda_alt_2,
                R.string.uganda_alt_3,
                R.string.uganda_alt_4,
                R.string.uganda_alt_5,
                R.string.uganda_alt_6
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "ukraine" to FlagResources(
            id = 301,
            image = R.drawable.flag_of_ukraine,
            imagePreview = R.drawable.flag_of_ukraine,
            flagOf = R.string.ukraine,
            flagOfOfficial = R.string.ukraine_official,
            flagOfAlternate = listOf(
                R.string.ukraine_alt_1,
                R.string.ukraine_alt_2,
                R.string.ukraine_alt_3,
                R.string.ukraine_alt_4,
                R.string.ukraine_alt_5,
                R.string.ukraine_alt_6
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.SEMI_PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "unitedArabEmirates" to FlagResources(
            id = 302,
            image = R.drawable.flag_of_the_united_arab_emirates,
            imagePreview = R.drawable.flag_of_the_united_arab_emirates,
            flagOf = R.string.united_arab_emirates,
            flagOfOfficial = R.string.united_arab_emirates_official,
            flagOfAlternate = listOf(
                R.string.united_arab_emirates_alt_1,
                R.string.united_arab_emirates_alt_2,
                R.string.united_arab_emirates_alt_3,
                R.string.united_arab_emirates_alt_4,
                R.string.united_arab_emirates_alt_5,
                R.string.united_arab_emirates_alt_6,
                R.string.united_arab_emirates_alt_7,
                R.string.united_arab_emirates_alt_8,
                R.string.united_arab_emirates_alt_9
            ),
            isFlagOfThe = true,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.FEDERAL,
                FlagCategory.PRESIDENTIAL,
                FlagCategory.MONARCHY
            ),
        ),

        "unitedKingdom" to FlagResources(
            id = 303,
            image = R.drawable.flag_of_the_united_kingdom__1_2_,
            imagePreview = R.drawable.flag_of_the_united_kingdom__1_2_,
            flagOf = R.string.united_kingdom,
            flagOfOfficial = R.string.united_kingdom_official,
            flagOfAlternate = listOf(
                R.string.united_kingdom_alt_1,
                R.string.united_kingdom_alt_2,
                R.string.united_kingdom_alt_3,
                R.string.united_kingdom_alt_4,
                R.string.united_kingdom_alt_5,
                R.string.united_kingdom_alt_6,
                R.string.united_kingdom_alt_7,
                R.string.united_kingdom_alt_8,
                R.string.united_kingdom_alt_9,
                R.string.united_kingdom_alt_10,
                R.string.united_kingdom_alt_11,
                R.string.united_kingdom_alt_12,
                R.string.united_kingdom_alt_13,
                R.string.united_kingdom_alt_14,
                R.string.united_kingdom_alt_15
            ),
            isFlagOfThe = true,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.MONARCHY
            ),
        ),

        "unitedNations" to FlagResources(
            id = 304,
            image = R.drawable.flag_of_the_united_nations,
            imagePreview = R.drawable.flag_of_the_united_nations,
            flagOf = R.string.united_nations,
            flagOfOfficial = R.string.united_nations_official,
            flagOfAlternate = listOf(
                R.string.united_nations_alt_1,
                R.string.united_nations_alt_2,
                R.string.united_nations_alt_3,
                R.string.united_nations_alt_4,
                R.string.united_nations_alt_5,
                R.string.united_nations_alt_6,
                R.string.united_nations_alt_7
            ),
            isFlagOfThe = true,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.DIRECTORIAL,
                FlagCategory.INTERNATIONAL_ORGANIZATION
            ),
        ),

        "unitedStates" to FlagResources(
            id = 305,
            image = R.drawable.flag_of_the_united_states__dos_eca_color_standard_,
            imagePreview = R.drawable.flag_of_the_united_states__dos_eca_color_standard_,
            flagOf = R.string.united_states,
            flagOfOfficial = R.string.united_states_official,
            flagOfAlternate = listOf(
                R.string.united_states_alt_1,
                R.string.united_states_alt_2,
                R.string.united_states_alt_3,
                R.string.united_states_alt_4,
                R.string.united_states_alt_5,
                R.string.united_states_alt_6,
                R.string.united_states_alt_7,
                R.string.united_states_alt_8,
                R.string.united_states_alt_9,
                R.string.united_states_alt_10,
                R.string.united_states_alt_11,
                R.string.united_states_alt_12,
                R.string.united_states_alt_13,
                R.string.united_states_alt_14,
                R.string.united_states_alt_15,
                R.string.united_states_alt_16,
                R.string.united_states_alt_17,
                R.string.united_states_alt_18,
                R.string.united_states_alt_19,
                R.string.united_states_alt_20,
                R.string.united_states_alt_21,
                R.string.united_states_alt_22,
                R.string.united_states_alt_23,
                R.string.united_states_alt_24,
                R.string.united_states_alt_25
            ),
            isFlagOfThe = true,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.FEDERAL,
                FlagCategory.PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "unitedStatesVirginIslands" to FlagResources(
            id = 306,
            image = R.drawable.flag_of_the_united_states_virgin_islands,
            imagePreview = R.drawable.flag_of_the_united_states_virgin_islands,
            flagOf = R.string.united_states_virgin_islands,
            flagOfOfficial = R.string.united_states_virgin_islands_official,
            flagOfAlternate = listOf(
                R.string.united_states_virgin_islands_alt_1,
                R.string.united_states_virgin_islands_alt_2,
                R.string.united_states_virgin_islands_alt_3,
                R.string.united_states_virgin_islands_alt_4,
                R.string.united_states_virgin_islands_alt_5,
                R.string.united_states_virgin_islands_alt_6,
                R.string.united_states_virgin_islands_alt_7,
                R.string.united_states_virgin_islands_alt_8,
                R.string.united_states_virgin_islands_alt_9
            ),
            isFlagOfThe = true,
            isFlagOfOfficialThe = true,
            sovereignState = "unitedStates",
            associatedState = null,
            categories = listOf(
                FlagCategory.AUTONOMOUS_REGION,
                FlagCategory.TERRITORY
            ),
        ),

        "uruguay" to FlagResources(
            id = 307,
            image = R.drawable.flag_of_uruguay,
            imagePreview = R.drawable.flag_of_uruguay_preview,
            flagOf = R.string.uruguay,
            flagOfOfficial = R.string.uruguay_official,
            flagOfAlternate = listOf(
                R.string.uruguay_alt_1,
                R.string.uruguay_alt_2,
                R.string.uruguay_alt_3,
                R.string.uruguay_alt_4
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "uzbekistan" to FlagResources(
            id = 308,
            image = R.drawable.flag_of_uzbekistan,
            imagePreview = R.drawable.flag_of_uzbekistan_preview,
            flagOf = R.string.uzbekistan,
            flagOfOfficial = R.string.uzbekistan_official,
            flagOfAlternate = listOf(R.string.uzbekistan_alt),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.SEMI_PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "valencianCommunity" to FlagResources(
            id = 309,
            image = R.drawable.flag_of_the_valencian_community__2x3_,
            imagePreview = R.drawable.flag_of_the_valencian_community__2x3_,
            flagOf = R.string.valencian_community,
            flagOfOfficial = R.string.valencian_community_official,
            flagOfAlternate = listOf(
                R.string.valencian_community_alt_1,
                R.string.valencian_community_alt_2,
                R.string.valencian_community_alt_3,
                R.string.valencian_community_alt_4,
                R.string.valencian_community_alt_5,
                R.string.valencian_community_alt_6
            ),
            isFlagOfThe = true,
            isFlagOfOfficialThe = true,
            sovereignState = "spain",
            associatedState = null,
            categories = listOf(
                FlagCategory.AUTONOMOUS_REGION,
                FlagCategory.COMMUNITY,
                FlagCategory.DEVOLVED_GOVERNMENT
            ),
        ),

        "vanuatu" to FlagResources(
            id = 310,
            image = R.drawable.flag_of_vanuatu,
            imagePreview = R.drawable.flag_of_vanuatu,
            flagOf = R.string.vanuatu,
            flagOfOfficial = R.string.vanuatu_official,
            flagOfAlternate = listOf(
                R.string.vanuatu_alt_1,
                R.string.vanuatu_alt_2,
                R.string.vanuatu_alt_3,
                R.string.vanuatu_alt_4,
                R.string.vanuatu_alt_5
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "vaticanCity" to FlagResources(
            id = 311,
            image = R.drawable.flag_of_vatican_city__2023present_,
            imagePreview = R.drawable.flag_of_vatican_city__2023present__preview,
            flagOf = R.string.vatican_city,
            flagOfOfficial = R.string.vatican_city_official,
            flagOfAlternate = listOf(
                R.string.vatican_city_alt_1,
                R.string.vatican_city_alt_2,
                R.string.vatican_city_alt_3,
                R.string.vatican_city_alt_4,
                R.string.vatican_city_alt_5,
                R.string.vatican_city_alt_6,
                R.string.vatican_city_alt_7,
                R.string.vatican_city_alt_8,
                R.string.vatican_city_alt_9,
                R.string.vatican_city_alt_10,
                R.string.vatican_city_alt_11
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.MONARCHY,
                FlagCategory.THEOCRACY
            ),
        ),

        "venezuela" to FlagResources(
            id = 312,
            image = R.drawable.flag_of_venezuela,
            imagePreview = R.drawable.flag_of_venezuela,
            flagOf = R.string.venezuela,
            flagOfOfficial = R.string.venezuela_official,
            flagOfAlternate = listOf(
                R.string.venezuela_alt_1,
                R.string.venezuela_alt_2
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.FEDERAL,
                FlagCategory.PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "vietnam" to FlagResources(
            id = 313,
            image = R.drawable.flag_of_vietnam,
            imagePreview = R.drawable.flag_of_vietnam,
            flagOf = R.string.vietnam,
            flagOfOfficial = R.string.vietnam_official,
            flagOfAlternate = listOf(
                R.string.vietnam_alt_1,
                R.string.vietnam_alt_2,
                R.string.vietnam_alt_3,
                R.string.vietnam_alt_4,
                R.string.vietnam_alt_5,
                R.string.vietnam_alt_6,
                R.string.vietnam_alt_7
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.SOCIALIST,
                FlagCategory.ONE_PARTY
            ),
        ),

        "vojvodina" to FlagResources(
            id = 314,
            image = R.drawable.flag_of_vojvodina,
            imagePreview = R.drawable.flag_of_vojvodina,
            flagOf = R.string.vojvodina,
            flagOfOfficial = R.string.vojvodina_official,
            flagOfAlternate = listOf(
                R.string.vojvodina_alt_1,
                R.string.vojvodina_alt_2
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = "serbia",
            associatedState = null,
            categories = listOf(
                FlagCategory.AUTONOMOUS_REGION,
                FlagCategory.PROVINCE,
                FlagCategory.DEVOLVED_GOVERNMENT
            ),
        ),

        "wales" to FlagResources(
            id = 315,
            image = R.drawable.flag_of_wales,
            imagePreview = R.drawable.flag_of_wales,
            flagOf = R.string.wales,
            flagOfOfficial = R.string.wales_official,
            flagOfAlternate = listOf(
                R.string.wales_alt_1,
                R.string.wales_alt_2,
                R.string.wales_alt_3,
                R.string.wales_alt_4
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = "unitedKingdom",
            associatedState = null,
            categories = listOf(
                FlagCategory.COUNTRY,
                FlagCategory.DEVOLVED_GOVERNMENT
            ),
        ),

        "wallisAndFutuna" to FlagResources(
            id = 316,
            image = R.drawable.flag_of_wallis_and_futuna,
            imagePreview = R.drawable.flag_of_wallis_and_futuna,
            flagOf = R.string.wallis_and_futuna,
            flagOfOfficial = R.string.wallis_and_futuna_official,
            flagOfAlternate = listOf(
                R.string.wallis_and_futuna_alt_1,
                R.string.wallis_and_futuna_alt_2
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = "france",
            associatedState = null,
            categories = listOf(
                FlagCategory.AUTONOMOUS_REGION,
                FlagCategory.COLLECTIVITY
            ),
        ),

        "washingtonDc" to FlagResources(
            id = 317,
            image = R.drawable.flag_of_washington__d_c,
            imagePreview = R.drawable.flag_of_washington__d_c,
            flagOf = R.string.washington_dc,
            flagOfOfficial = R.string.washington_dc_official,
            flagOfAlternate = listOf(
                R.string.washington_dc_alt_1,
                R.string.washington_dc_alt_2,
                R.string.washington_dc_alt_3,
                R.string.washington_dc_alt_4
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = "unitedStates",
            associatedState = null,
            categories = listOf(FlagCategory.DISTRICT),
        ),

        "yap" to FlagResources(
            id = 318,
            image = R.drawable.flag_of_yap,
            imagePreview = R.drawable.flag_of_yap,
            flagOf = R.string.yap,
            flagOfOfficial = R.string.yap_official,
            flagOfAlternate = listOf(R.string.yap_alt),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = "micronesia",
            associatedState = null,
            categories = listOf(FlagCategory.STATE),
        ),

        "yemen" to FlagResources(
            id = 319,
            image = R.drawable.flag_of_yemen,
            imagePreview = R.drawable.flag_of_yemen,
            flagOf = R.string.yemen,
            flagOfOfficial = R.string.yemen_official,
            flagOfAlternate = listOf(
                R.string.yemen_alt_1,
                R.string.yemen_alt_2,
                R.string.yemen_alt_3,
                R.string.yemen_alt_4,
                R.string.yemen_alt_5
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PARLIAMENTARY,
                FlagCategory.THEOCRATIC,
                FlagCategory.PROVISIONAL_GOVERNMENT
            ),
        ),

        "zambia" to FlagResources(
            id = 320,
            image = R.drawable.flag_of_zambia,
            imagePreview = R.drawable.flag_of_zambia,
            flagOf = R.string.zambia,
            flagOfOfficial = R.string.zambia_official,
            flagOfAlternate = listOf(
                R.string.zambia_alt_1,
                R.string.zambia_alt_2,
                R.string.zambia_alt_3,
                R.string.zambia_alt_4
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        ),

        "zanzibar" to FlagResources(
            id = 321,
            image = R.drawable.flag_of_zanzibar,
            imagePreview = R.drawable.flag_of_zanzibar_preview,
            flagOf = R.string.zanzibar,
            flagOfOfficial = R.string.zanzibar_official,
            flagOfAlternate = listOf(R.string.zanzibar_alt),
            isFlagOfThe = false,
            isFlagOfOfficialThe = false,
            sovereignState = "tanzania",
            associatedState = null,
            categories = listOf(FlagCategory.AUTONOMOUS_REGION),
        ),

        "zimbabwe" to FlagResources(
            id = 322,
            image = R.drawable.flag_of_zimbabwe,
            imagePreview = R.drawable.flag_of_zimbabwe,
            flagOf = R.string.zimbabwe,
            flagOfOfficial = R.string.zimbabwe_official,
            flagOfAlternate = listOf(
                R.string.zimbabwe_alt_1,
                R.string.zimbabwe_alt_2,
                R.string.zimbabwe_alt_3,
                R.string.zimbabwe_alt_4,
                R.string.zimbabwe_alt_5
            ),
            isFlagOfThe = false,
            isFlagOfOfficialThe = true,
            sovereignState = null,
            associatedState = null,
            categories = listOf(
                FlagCategory.SOVEREIGN_STATE,
                FlagCategory.UNITARY,
                FlagCategory.PRESIDENTIAL,
                FlagCategory.CONSTITUTIONAL,
                FlagCategory.REPUBLIC
            ),
        )
    )

    val allFlagsList = flagsMap.values.toList()
}