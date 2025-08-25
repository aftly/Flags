package dev.aftly.flags.model

data class FlagScreenContent(
    val flag: FlagView,
    val descriptionResIds: List<Int>,
    val descriptionClickableFlags: List<FlagView>,
    val descriptionClickableWordIndexes: List<Int>,
    val descriptionBoldWordIndexes: List<Int>,
    val descriptionLightWordIndexes: List<Int>,
    val isAnimated: Boolean,
)