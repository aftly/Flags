package dev.aftly.flags.model

sealed interface RelatedFlagsContent {
    val menu: RelatedFlagsMenu
    val groups: List<RelatedFlagGroup>

    fun getIds(): List<Int> = groups.flatMap { group ->
        when (group) {
            is RelatedFlagGroup.Single -> listOf(group.flag.id)
            is RelatedFlagGroup.Multiple -> group.flags.map { it.id }
            is RelatedFlagGroup.AdminUnits -> group.flags.map { it.id }
        }
    }.distinct()

    data class Political(
        val sovereign: RelatedFlagGroup.Single?,
        val adminUnits: List<RelatedFlagGroup.AdminUnits>?,
        val externalTerritories: RelatedFlagGroup.Multiple?,
        val associatedStates: RelatedFlagGroup.Multiple?,
        val internationalOrgs: RelatedFlagGroup.Multiple?,
    ) : RelatedFlagsContent {
        override val menu = RelatedFlagsMenu.POLITICAL

        override val groups = buildList {
            sovereign?.let { add(it) }
            adminUnits?.let {
                it.forEach { group -> add(group) }
            }
            externalTerritories?.let { add(it) }
            associatedStates?.let { add(it) }
            internationalOrgs?.let { add(it) }
        }
    }

    data class Chronological(
        val latestEntity: RelatedFlagGroup.Single?,
        val previousEntities: RelatedFlagGroup.Multiple?,
        val historicalFlags: RelatedFlagGroup.Multiple?,
        val previousEntitiesOfSovereign: RelatedFlagGroup.Multiple?,
        val dependentsOfLatest: RelatedFlagGroup.Multiple?,
    ) : RelatedFlagsContent {
        override val menu = RelatedFlagsMenu.CHRONOLOGICAL

        override val groups = buildList {
            latestEntity?.let { add(it) }
            previousEntities?.let { add(it) }
            historicalFlags?.let { add(it) }
            previousEntitiesOfSovereign?.let { add(it) }
            dependentsOfLatest?.let { add(it) }
        }
    }
}