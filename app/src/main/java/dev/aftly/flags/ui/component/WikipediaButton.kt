package dev.aftly.flags.ui.component

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import dev.aftly.flags.R
import dev.aftly.flags.ui.theme.Dimens

@Composable
fun WikipediaButton(
    modifier: Modifier = Modifier,
    context: Context,
    wikiLink: String,
) {
    Button(
        onClick = {
            viewWikipedia(
                context = context,
                linkToView = wikiLink,
            )
        },
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.wikipedia_button),
            )
            Icon(
                imageVector = Icons.AutoMirrored.Default.OpenInNew,
                contentDescription = null,
                modifier = Modifier.padding(start = Dimens.small8)
            )
        }
    }
}

private fun viewWikipedia(
    context: Context,
    linkToView: String,
) {
    val intent = Intent(Intent.ACTION_VIEW, linkToView.toUri())
    try {
        context.startActivity(intent)
    } catch (_: ActivityNotFoundException) {} // TODO: param "e" when catch Content
    // TODO: Pop up dialog indicating failure
}