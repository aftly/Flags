package dev.aftly.flags.model

sealed interface RelatedFlagsContent {
    val groups: List<RelatedFlagGroup?>

    data class Political(
        val sovereign: RelatedFlagGroup.Single?,
        val firstLevelAdminUnits: RelatedFlagGroup.Multiple?,
        val autonomousTerritories: RelatedFlagGroup.Multiple?,
        val associatedStates: RelatedFlagGroup.Multiple?,
        val internationalOrgs: RelatedFlagGroup.Multiple?,
        val secondLevelAdminUnits: RelatedFlagGroup.Multiple?,
    ) : RelatedFlagsContent {
        override val groups = listOf(
            sovereign,
            firstLevelAdminUnits,
            autonomousTerritories,
            associatedStates,
            internationalOrgs,
            secondLevelAdminUnits
        )
    }

    data class Chronological(
        val latestEntity: RelatedFlagGroup.Single?,
        val previousEntities: RelatedFlagGroup.Multiple?,
        val dependentsOfLatest: RelatedFlagGroup.Multiple?,
    ) : RelatedFlagsContent {
        override val groups = listOf(latestEntity, previousEntities, dependentsOfLatest)
    }
}