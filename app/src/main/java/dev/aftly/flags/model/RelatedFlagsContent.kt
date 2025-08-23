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
        val internationalOrgMembers: RelatedFlagGroup.Multiple?,
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
            internationalOrgMembers?.let { add(it) }
        }.sortedWith { p1, p2 ->
            when {
                p1 is RelatedFlagGroup.AdminUnits && p2 !is RelatedFlagGroup
                    .AdminUnits && p1.unitLevel > 1 -> 1
                p2 is RelatedFlagGroup.AdminUnits && p1 !is RelatedFlagGroup
                    .AdminUnits && p2.unitLevel > 1 -> -1
                p1 is RelatedFlagGroup.AdminUnits && p2 is RelatedFlagGroup
                    .AdminUnits && p1.unitLevel > p2.unitLevel -> 1
                p2 is RelatedFlagGroup.AdminUnits && p1 is RelatedFlagGroup
                    .AdminUnits && p2.unitLevel > p1.unitLevel -> -1
                else -> 0
            }
        }
    }

    data class Chronological(
        val latestEntities: RelatedFlagGroup.Multiple?,
        val previousEntities: RelatedFlagGroup.Multiple?,
        val historicalFlags: RelatedFlagGroup.Multiple?,
        val previousEntitiesOfSovereign: RelatedFlagGroup.Multiple?,
        val dependentsOfLatest: RelatedFlagGroup.Multiple?,
    ) : RelatedFlagsContent {
        override val menu = RelatedFlagsMenu.CHRONOLOGICAL

        override val groups = buildList {
            latestEntities?.let { add(it) }
            previousEntities?.let { add(it) }
            historicalFlags?.let { add(it) }
            previousEntitiesOfSovereign?.let { add(it) }
            dependentsOfLatest?.let { add(it) }
        }
    }
}