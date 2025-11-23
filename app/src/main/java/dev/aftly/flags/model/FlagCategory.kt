package dev.aftly.flags.model

import androidx.annotation.StringRes
import dev.aftly.flags.R
import kotlinx.serialization.Serializable


/* List of categories for use in flag list filtering and flag page descriptions */
@Serializable
enum class FlagCategory(
    @param:StringRes val title: Int,
    @param:StringRes val string: Int,
) {
    /* POLITICAL ENTITIES: */

    /* Top level (de jure and recognised) state */
    SOVEREIGN_STATE (title = R.string.category_sovereign_state_title, string = R.string.category_sovereign_state_string),

    /* Autonomous or devolved but de jure dependent territories. Having special provisions over
     * typical federated or unitary divisions.
     * Also, territories with a Compact of Free Association agreement */
    FREE_ASSOCIATION (title = R.string.category_free_association_title, string = R.string.category_free_association_string),
    AUTONOMOUS_REGION (title = R.string.category_autonomous_region_title, string = R.string.category_autonomous_region_string),
    DEVOLVED_GOVERNMENT (title = R.string.category_devolved_government_title, string = R.string.category_devolved_government_string),
    INDIGENOUS_TERRITORY (title = R.string.category_indigenous_territory_title, string = R.string.category_indigenous_territory_string),
    UNRECOGNIZED_STATE (title = R.string.category_unrecognized_state_title, string = R.string.category_unrecognized_state_string),
    ANNEXED_TERRITORY (title = R.string.category_annexed_territory_title, string = R.string.category_annexed_territory_string),
    SOVEREIGN_ENTITY (title = R.string.category_sovereign_entity_title, string = R.string.category_sovereign_entity_string),

    /* Administrative units within a sovereign state (eg. federated state or unitary region) */
    CANTON (title = R.string.category_canton_title, string = R.string.category_canton_string),
    COLLECTIVITY (title = R.string.category_collectivity_title, string = R.string.category_collectivity_string),
    COLONY(title = R.string.category_colony_title, string = R.string.category_colony_string),
    COMMUNITY (title = R.string.category_community_title, string = R.string.category_community_string),
    COMUNE (title = R.string.category_comune_title, string = R.string.category_comune_string),
    COUNCIL_AREA (title = R.string.category_council_area_title, string = R.string.category_council_area_string),
    COUNTRY (title = R.string.category_country_title, string = R.string.category_country_string),
    COUNTY (title = R.string.category_county_title, string = R.string.category_county_string),
    CITY (title = R.string.category_city_title, string = R.string.category_city_string),
    DISTRICT (title = R.string.category_district_title, string = R.string.category_district_string),
    EMIRATE (title = R.string.category_emirate_title, string = R.string.category_emirate_string),
    ENTITY (title = R.string.category_entity_title, string = R.string.category_entity_string),
    GOVERNORATE (title = R.string.category_governorate_title, string = R.string.category_governorate_string),
    ISLAND (title = R.string.category_island_title, string = R.string.category_island_string),
    KRAI (title = R.string.category_krai_title, string = R.string.category_krai_string),
    MEMBER_STATE (title = R.string.category_member_state_title, string = R.string.category_member_state_string),
    MICRONATION (title = R.string.category_micronation_title, string = R.string.category_micronation_string),
    MUNICIPALITY (title = R.string.category_municipality_title, string = R.string.category_municipality_string),
    OBLAST (title = R.string.category_oblast_title, string = R.string.category_oblast_string),
    OKRUG (title = R.string.category_okrug_title, string = R.string.category_okrug_string),
    PROVINCE (title = R.string.category_province_title, string = R.string.category_province_string),
    REGION (title = R.string.category_region_title, string = R.string.category_region_string),
    REGIONAL (title = R.string.category_regional_title, string = R.string.category_regional_string),
    REPUBLIC_UNIT (title = R.string.category_republic_unit_title, string = R.string.category_republic_unit_string),
    RIDING (title = R.string.category_riding_unit_title, string = R.string.category_riding_unit_string),
    STATE (title = R.string.category_state_title, string = R.string.category_state_string),
    TERRITORY (title = R.string.category_territory_title, string = R.string.category_territory_string),

    /* Supranational & international polities */
    SUPRANATIONAL_UNION (title = R.string.category_supranational_union_title, string = R.string.category_supranational_union_string),
    INTERNATIONAL_ORGANIZATION (title = R.string.category_international_organization_title, string = R.string.category_international_organization_string),

    /* Institutional */
    LOWER_HOUSE (title = R.string.category_lower_house_title, string = R.string.category_lower_house_string),
    UPPER_HOUSE (title = R.string.category_upper_house_title, string = R.string.category_upper_house_string),
    UNICAMERAL (title = R.string.category_unicameral_title, string = R.string.category_unicameral_string),
    PARLIAMENT (title = R.string.category_parliament_title, string = R.string.category_parliament_string),
    CONGRESS (title = R.string.category_congress_title, string = R.string.category_congress_string),
    ASSEMBLY (title = R.string.category_assembly_title, string = R.string.category_assembly_string),
    DEPARTMENT (title = R.string.category_department_title, string = R.string.category_department_string),
    MILITARY (title = R.string.category_military_title, string = R.string.category_military_string),
    POLICE (title = R.string.category_police_title, string = R.string.category_police_string),
    POLITICAL_ORGANIZATION (title = R.string.category_political_organization_title, string = R.string.category_political_organization_string),
    CHARITY (title = R.string.category_charity_title, string = R.string.category_charity_string),
    UNIVERSITY (title = R.string.category_university_title, string = R.string.category_university_string),
    VEXILLOLOGY (title = R.string.category_vexillology_title, string = R.string.category_vexillology_string),
    RELIGIOUS (title = R.string.category_religious_title, string = R.string.category_religious_string),


    /* POLITICAL CATEGORIES: */

    /* Modes of territorial distribution of power */
    UNITARY (title = R.string.category_unitary_title, string = R.string.category_unitary_string),
    FEDERAL (title = R.string.category_federal_title, string = R.string.category_federal_string),
    CONFEDERATION (title = R.string.category_confederation_title, string = R.string.category_confederation_string),

    /* Modes of executive structure */
    DIRECTORIAL (title = R.string.category_directorial_title, string = R.string.category_directorial_string),
    PARLIAMENTARY (title = R.string.category_parliamentary_title, string = R.string.category_parliamentary_string),
    SEMI_PRESIDENTIAL (title = R.string.category_semi_presidential_title, string = R.string.category_semi_presidential_string),
    DUAL_EXECUTIVE (title = R.string.category_dual_executive_title, string = R.string.category_dual_executive_string),
    PRESIDENTIAL (title = R.string.category_presidential_title, string = R.string.category_presidential_string),

    /* Legal constraint on head(s) of state & government */
    CONSTITUTIONAL (title = R.string.category_constitutional_title, string = R.string.category_constitutional_string),
    NOMINAL_EXTRA_CONSTITUTIONAL (title = R.string.category_nominal_extra_constitutional_title, string = R.string.category_nominal_extra_constitutional_string),

    /* Modes of power derivation */
    REPUBLIC (title = R.string.category_republic_title, string = R.string.category_republic_string),
    MONARCHY (title = R.string.category_monarchy_title, string = R.string.category_monarchy_string),
    ONE_PARTY (title = R.string.category_one_party_title, string = R.string.category_one_party_string),
    THEOCRACY (title = R.string.category_theocracy_title, string = R.string.category_theocracy_string),
    MILITARY_JUNTA (title = R.string.category_military_junta_title, string = R.string.category_military_junta_string),
    PROVISIONAL_GOVERNMENT (title = R.string.category_provisional_government_title, string = R.string.category_provisional_government_string),

    /* Regime type */
    DEMOCRACY(title = R.string.category_democracy_title, string = R.string.category_democracy_string),
    AUTHORITARIAN (title = R.string.category_authoritarian_title, string = R.string.category_authoritarian_string),
    TOTALITARIAN (title = R.string.category_totalitarian_title, string = R.string.category_totalitarian_string),
    DICTATORSHIP (title = R.string.category_dictatorship_title, string = R.string.category_dictatorship_string),
    OLIGARCHY (title = R.string.category_oligarchy_title, string = R.string.category_oligarchy_string),

    /* Ideological orientation */
    LIBERAL (title = R.string.category_liberal_title, string = R.string.category_liberal_string),
    THEOCRATIC (title = R.string.category_theocratic_title, string = R.string.category_theocratic_string),
    SOCIALIST (title = R.string.category_socialist_title, string = R.string.category_socialist_string),
    FASCIST (title = R.string.category_fascist_title, string = R.string.category_fascist_string),


    /* CULTURAL ENTITIES */
    POLITICAL_MOVEMENT (title = R.string.category_political_movement_title, string = R.string.category_political_movement_string),
    TRIBE (title = R.string.category_tribe_title, string = R.string.category_tribe_string),
    ETHNIC (title = R.string.category_ethnic_title, string = R.string.category_ethnic_string),
    SOCIAL (title = R.string.category_social_title, string = R.string.category_social_string),


    /* OTHER */
    HISTORICAL (title = R.string.category_historical_title, string = R.string.category_historical_string),
    COMPOSITE (title = R.string.category_composite_title, string = R.string.category_composite_string),
    MARITIME (title = R.string.category_maritime_title, string = R.string.category_maritime_string),
    MICROSTATE (title = R.string.category_microstate_title, string = R.string.category_microstate_string),
    QUASI_STATE (title = R.string.category_quasi_state_title, string = R.string.category_quasi_state_string),
    MILITANT_ORGANIZATION (title = R.string.category_militant_organization_title, string = R.string.category_militant_organization_string),
    TERRORIST_ORGANIZATION (title = R.string.category_terrorist_organization_title, string = R.string.category_terrorist_organization_string),
    INDIGENOUS (title = R.string.category_indigenous_title, string = R.string.category_indigenous_string)
}