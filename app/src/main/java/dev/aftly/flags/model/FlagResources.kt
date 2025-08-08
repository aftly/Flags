package dev.aftly.flags.model

import androidx.annotation.DrawableRes

data class FlagResources(
    val id: Int, // For LazyColumn's items() key param
    val wikipediaUrlPath: StringResSource, // So that other locales can use a different wikipedia
    @param:DrawableRes val image: Int,
    @param:DrawableRes val imagePreview: Int, // Smaller image for lists (if relevant)
    val fromYear: Int?, // Year flag was adopted
    val toYear: Int?, // Year flag was unadopted
    val flagOf: StringResSource, // flagOf<> params are for common, official & alt names (of entity)
    val flagOfOfficial: StringResSource,
    val flagOfAlternate: List<Int>?, // List of StringResIds
    val isFlagOfThe: BooleanSource, // <>The params are for if name is preceded by "the"
    val isFlagOfOfficialThe: BooleanSource,
    val associatedState: String?, // Like below but for states in a Compact of Free Association
    val sovereignState: String?, // If applicable, flagsMap key string of it's sovereign entity
    val parentEntity: String?, // For relate sub-units with a parent (eg. US county -> US state)
    val latestEntity: String?, // To relate unique historical entities with latest proceeding entity
    val previousFlagOf: String?, // To relate non-unique historical entities with it's latest entity
    val categories: List<FlagCategory>,
)