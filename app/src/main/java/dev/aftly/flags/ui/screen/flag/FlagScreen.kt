package dev.aftly.flags.ui.screen.flag

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.aftly.flags.R
import dev.aftly.flags.data.DataSource
import dev.aftly.flags.model.FlagResources
import dev.aftly.flags.navigation.Screen
import dev.aftly.flags.ui.component.StaticTopAppBar
import dev.aftly.flags.ui.theme.Dimens
import dev.aftly.flags.ui.theme.FlagsTheme


@Composable
fun FlagScreen(
    viewModel: FlagViewModel = viewModel(),
    navArgFlagId: String?,
    currentScreen: Screen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    onNavigateError: () -> Unit,
) {
    viewModel.initialiseUiState(navArgFlagId)
    val uiState by viewModel.uiState.collectAsState()
    // TODO: Give null flag properties and show to user?
    if (uiState.flag == DataSource.nullFlag) onNavigateError()

    /* Update flag description string when language configuration changes */
    val currentLocale = LocalContext.current.resources.configuration.locales[0]
    LaunchedEffect(currentLocale) { viewModel.updateDescriptionString() }

    FlagScaffold(
        currentScreen = currentScreen,
        canNavigateBack = canNavigateBack,
        navigateUp = navigateUp,
        flag = uiState.flag,
        description = uiState.description,
        boldWordPositions = uiState.descriptionBoldWordIndexes,
    )

}


@Composable
private fun FlagScaffold(
    modifier: Modifier = Modifier,
    currentScreen: Screen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    flag: FlagResources,
    description: List<String>,
    boldWordPositions: List<Int>,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            StaticTopAppBar(
                currentScreen = currentScreen,
                canNavigateBack = canNavigateBack,
                onNavigateUp = navigateUp,
                onAction = { },
            )
        }
    ) { scaffoldPadding ->
        FlagContent(
            modifier = Modifier.padding(scaffoldPadding),
            flag = flag,
            description = description,
            boldWordPositions = boldWordPositions,
        )
    }
}


@Composable
private fun FlagContent(
    modifier: Modifier = Modifier,
    flag: FlagResources,
    description: List<String>,
    boldWordPositions: List<Int>,
) {
    Column(
        modifier = modifier.fillMaxSize()
            .padding(horizontal = Dimens.medium16),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        /* Determine name title properties */
        val prefix = when (flag.isFlagOfOfficialThe) {
            true -> stringResource(R.string.string_the_capitalized) +
                    stringResource(R.string.string_whitespace)
            false -> null
        }
        val name = stringResource(flag.flagOfOfficial)
        val nameLength = prefix?.let { it.length + name.length } ?: name.length

        val nameStyle = if (nameLength < 36) {
            if (nameLength <= 12) MaterialTheme.typography.displayLarge
            else MaterialTheme.typography.displayMedium
        } else {
            MaterialTheme.typography.displaySmall
        }

        /* Build annotated string from name properties, making name bold */
        val annotatedName = buildAnnotatedString {
            if (prefix != null) append(text = prefix)
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append(text = name) }
        }

        /* Build annotated string from description string list, making flag names bold */
        val annotatedDescription = buildAnnotatedString {
            for ((index, string) in description.withIndex()) {
                if (index in boldWordPositions) {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(text = string)
                    }
                } else append(text = string)
            }
        }

        Text(
            text = annotatedName,
            style = nameStyle,
            textAlign = TextAlign.Center,
        )

        Surface(
            modifier = Modifier.wrapContentSize()
                .padding(vertical = Dimens.extraLarge32)
                .shadow(Dimens.extraSmall4),
        ) {
            Image(
                painter = painterResource(flag.image),
                contentDescription = null,
                contentScale = ContentScale.Fit,
            )
        }

        Text(
            text = annotatedDescription,
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            lineHeight = 28.sp,
            style = TextStyle(fontStyle = FontStyle.Italic),
        )

        Spacer(modifier = Modifier.height(Dimens.bottomSpacer80))
    }
}


// Preview screen in Android Studio
@Preview(
    showBackground = true,
    showSystemUi = true)
@Composable
fun FlagScreenPreview() {
    FlagsTheme(
        //darkTheme = true
    ) {
        FlagScreen(
            navArgFlagId = "russia",
            currentScreen = Screen.Flag,
            canNavigateBack = true,
            navigateUp = { },
            onNavigateError = { }
        )
    }
}