package dev.aftly.flags.model

sealed interface RelatedFlagsContent {
    val groups: List<RelatedFlagGroup>

    fun getIds(): List<Int> = groups.flatMap { group ->
        when (group) {
            is RelatedFlagGroup.Single -> listOf(group.flag.id)
            is RelatedFlagGroup.Multiple -> group.flags.map { it.id }
        }
    }.distinct()

    data class Political(
        val sovereign: RelatedFlagGroup.Single?,
        val firstLevelAdminUnits: List<RelatedFlagGroup.Multiple>?,
        val externalTerritories: RelatedFlagGroup.Multiple?,
        val associatedStates: RelatedFlagGroup.Multiple?,
        val internationalOrgs: RelatedFlagGroup.Multiple?,
        val secondLevelAdminUnits: List<RelatedFlagGroup.Multiple>?,
    ) : RelatedFlagsContent {
        override val groups = buildList {
            sovereign?.let { add(it) }
            firstLevelAdminUnits?.let {
                it.forEach { group -> add(group) }
            }
            externalTerritories?.let { add(it) }
            associatedStates?.let { add(it) }
            internationalOrgs?.let { add(it) }
            secondLevelAdminUnits?.let {
                it.forEach { group -> add(group) }
            }
        }
    }

    data class Chronological(
        val latestEntity: RelatedFlagGroup.Single?,
        val previousEntities: RelatedFlagGroup.Multiple?,
        val previousEntitiesOfSovereign: RelatedFlagGroup.Multiple?,
        val dependentsOfLatest: RelatedFlagGroup.Multiple?,
    ) : RelatedFlagsContent {
        override val groups = buildList {
            latestEntity?.let { add(it) }
            previousEntities?.let { add(it) }
            previousEntitiesOfSovereign?.let { add(it) }
            dependentsOfLatest?.let { add(it) }
        }
    }
}