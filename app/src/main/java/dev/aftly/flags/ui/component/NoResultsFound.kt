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
import dev.aftly.flags.ui.theme.Dimens


enum class ResultsType { CATEGORIES, SEARCH, GAME_HISTORY }

@Composable
fun NoResultsFound(
    modifier: Modifier = Modifier,
    resultsType: ResultsType,
    bottomSpacer: Boolean = false,
) {
    @StringRes val title = when (resultsType) {
        ResultsType.CATEGORIES -> R.string.no_flags_found
        ResultsType.SEARCH -> R.string.no_flags_found
        ResultsType.GAME_HISTORY -> R.string.no_score_history
    }

    @StringRes val description1 = when (resultsType) {
        ResultsType.CATEGORIES -> R.string.multi_select_no_results_description
        ResultsType.SEARCH -> R.string.search_no_results_description_1
        ResultsType.GAME_HISTORY -> R.string.game_history_none_description_1
    }
    @StringRes val description2 = when (resultsType) {
        ResultsType.CATEGORIES -> null
        ResultsType.SEARCH -> R.string.search_no_results_description_2
        ResultsType.GAME_HISTORY -> R.string.game_history_none_description_2
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(title),
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

        if (bottomSpacer) {
            Spacer(modifier = Modifier.height(Dimens.bottomSpacer80))
        }
    }
}