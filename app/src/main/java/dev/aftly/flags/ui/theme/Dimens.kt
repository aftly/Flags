package dev.aftly.flags.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// To imitate verbose dimensionResource(R.dimen.padding_small) from dimens.xml
object Dimens {
    val extraSmall4: Dp = 4.dp
    val extraSmall6: Dp = 6.dp
    val small8: Dp = 8.dp
    val small10: Dp = 10.dp
    val medium16: Dp = 16.dp
    val large24: Dp = 24.dp
    val extraLarge32: Dp = 32.dp

    val marginHorizontal24: Dp = 24.dp
    val canNavigateBack0: Dp = 0.dp
    val listItemHeight48: Dp = 48.dp
    val listItemHeight64: Dp = 64.dp
    val filterButtonRowHeight30: Dp = 30.dp
    val bottomSpacer30: Dp = 30.dp
    val bottomSpacer80: Dp = 80.dp
}



// TODO: Experiment with MaterialTheme like padding:
/**** Shape.kt ****/


/** From MaterialTheme object declaration **/
/*
val shapes: Shapes
@Composable @ReadOnlyComposable get() = LocalShapes.current
 */


/** relevant snippets from MaterialTheme() function declaration: **/
/*
@Composable
fun MaterialTheme(
    colorScheme: ColorScheme = MaterialTheme.colorScheme,
    shapes: Shapes = MaterialTheme.shapes,
    typography: Typography = MaterialTheme.typography,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalColorScheme provides colorScheme,
        LocalShapes provides shapes,
        LocalTypography provides typography,
    ) {
        ProvideTextStyle(value = typography.bodyLarge, content = content)
    }
}
 */


/** From Shapes.kt in androidx.compose.material3: **/
// internal val LocalShapes = staticCompositionLocalOf { Shapes() }


/** Shapes() function and ShapeDefaults object: **/
/*
@Immutable
class Shapes(
    // Shapes None and Full are omitted as None is a RectangleShape and Full is a CircleShape.
    val extraSmall: CornerBasedShape = ShapeDefaults.ExtraSmall,
    val small: CornerBasedShape = ShapeDefaults.Small,
    val medium: CornerBasedShape = ShapeDefaults.Medium,
    val large: CornerBasedShape = ShapeDefaults.Large,
    val extraLarge: CornerBasedShape = ShapeDefaults.ExtraLarge,
) {
    /** Returns a copy of this Shapes, optionally overriding some of the values. */
    fun copy(
        extraSmall: CornerBasedShape = this.extraSmall,
        small: CornerBasedShape = this.small,
        medium: CornerBasedShape = this.medium,
        large: CornerBasedShape = this.large,
        extraLarge: CornerBasedShape = this.extraLarge,
    ): Shapes =
        Shapes(
            extraSmall = extraSmall,
            small = small,
            medium = medium,
            large = large,
            extraLarge = extraLarge,
        )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Shapes) return false
        if (extraSmall != other.extraSmall) return false
        if (small != other.small) return false
        if (medium != other.medium) return false
        if (large != other.large) return false
        if (extraLarge != other.extraLarge) return false
        return true
    }

    override fun hashCode(): Int {
        var result = extraSmall.hashCode()
        result = 31 * result + small.hashCode()
        result = 31 * result + medium.hashCode()
        result = 31 * result + large.hashCode()
        result = 31 * result + extraLarge.hashCode()
        return result
    }

    override fun toString(): String {
        return "Shapes(" +
            "extraSmall=$extraSmall, " +
            "small=$small, " +
            "medium=$medium, " +
            "large=$large, " +
            "extraLarge=$extraLarge)"
    }
}
 */

/** Contains the default values used by Shapes **/
/*
object ShapeDefaults {
    val ExtraSmall: CornerBasedShape = ShapeTokens.CornerExtraSmall
    val Small: CornerBasedShape = ShapeTokens.CornerSmall
    val Medium: CornerBasedShape = ShapeTokens.CornerMedium
    val Large: CornerBasedShape = ShapeTokens.CornerLarge
    val ExtraLarge: CornerBasedShape = ShapeTokens.CornerExtraLarge
}
 */