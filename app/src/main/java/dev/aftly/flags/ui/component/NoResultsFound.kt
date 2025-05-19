package dev.aftly.flags.ui.component

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import dev.aftly.flags.R
import dev.aftly.flags.navigation.Screen
import dev.aftly.flags.ui.theme.Dimens

@Composable
fun NoResultsFound(
    modifier: Modifier = Modifier,
    isSearch: Boolean = false,
    isGame: Boolean = false,
) {
    val categoryScreen = listOf(
        Screen.List,
        Screen.Game
    )

    @StringRes val description1 = when (isSearch) {
        true -> R.string.flag_search_no_results_description
        false -> R.string.list_flags_no_results_description
    }
    @StringRes val description2 = when (isSearch) {
        true -> R.string.flag_search_no_results_description_2
        false -> null
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.flag_search_no_results_title),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineSmall,
        )

        Text(
            text = stringResource(description1),
            modifier = Modifier.padding(vertical = Dimens.extraSmall6),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
        )

        description2?.let { stringResId ->
            Text(
                text = stringResource(stringResId),
                textAlign = TextAlign.Center,
                fontStyle = FontStyle.Italic,
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        if (!isGame) {
            Spacer(modifier = Modifier.height(Dimens.bottomSpacer80))
        }
    }
}