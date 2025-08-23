package dev.aftly.flags.model

import kotlinx.serialization.Serializable

@Serializable
data class FlagResources(
    val id: Int, // For LazyColumn's items() key param
    val wikipediaUrlPath: StringResSource, // So that other locales can use a different wikipedia
    val image: DrawableResName,
    val imagePreview: DrawableResName, // Smaller image for lists (if relevant)
    val fromYear: Int?, // Year flag was adopted
    val toYear: Int?, // Year flag was unadopted
    val flagOf: StringResSource, // flagOf<> params are for common, official & alt names (of entity)
    val flagOfLiteral: StringResSource, // flagOf without descriptors
    val flagOfOfficial: StringResSource,
    val flagOfAlternate: List<StringResSource>, // List of StringResIds
    val isFlagOfThe: BooleanSource, // <>The params are for if name is preceded by "the"
    val isFlagOfOfficialThe: BooleanSource,
    val internationalOrganisations: List<String>,
    val associatedState: String?, // Like below but for states in a Compact of Free Association
    val sovereignState: String?, // If applicable, flagsMap key string of it's sovereign entity
    val parentUnit: String?, // For relate sub-units with a parent (eg. US county -> US state)
    val latestEntities: List<String>, // To relate unique historical entities with latest proceeding entities
    val previousFlagOf: String?, // To relate non-unique historical entities with it's latest entity
    val categories: List<FlagCategory>,
)