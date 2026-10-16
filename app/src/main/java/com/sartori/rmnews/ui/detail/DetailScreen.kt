package com.sartori.rmnews.ui.detail

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil3.compose.AsyncImage
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sartori.rmnews.R
import com.sartori.rmnews.ui.theme.RMNewsTheme

/** Shows article metadata and opens the full article in an external browser. */
@Composable
fun DetailScreen(
    title: String,
    url: String,
    sourceName: String?,
    formattedPublishedDate: String?,
    onBack: () -> Unit,
    imageUrl: String? = null,
    onReadFullArticle: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var linkFailed by remember(url) { mutableStateOf(false) }
    val publicationDate = formattedPublishedDate
        ?: stringResource(R.string.date_unavailable)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TextButton(onClick = onBack) {
            Text(stringResource(R.string.back))
        }
        val placeholder = painterResource(R.drawable.ic_article_placeholder)
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            placeholder = placeholder,
            error = placeholder,
            fallback = placeholder,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.semantics { heading() }
        )
        Text(
            text = stringResource(
                R.string.article_source,
                sourceName?.takeIf { it.isNotBlank() } ?: stringResource(R.string.source_unavailable)
            ),
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = stringResource(R.string.article_published, publicationDate),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                onReadFullArticle()
                linkFailed = false
                val uri = Uri.parse(url)
                if (uri.scheme?.lowercase() !in setOf("https", "http") || uri.host.isNullOrBlank()) {
                    linkFailed = true
                } else {
                    try {
                        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                    } catch (_: ActivityNotFoundException) {
                        linkFailed = true
                    } catch (_: SecurityException) {
                        linkFailed = true
                    }
                }
            }
        ) {
            Text(stringResource(R.string.read_full_article))
        }
        if (linkFailed) {
            Text(
                text = stringResource(R.string.article_link_error),
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DetailScreenPreview() {
    RMNewsTheme {
        DetailScreen(
            title = "A new discovery brings fresh possibilities for renewable energy",
            url = "https://example.com/article",
            sourceName = "Example News",
            formattedPublishedDate = "Oct 15, 2026, 5:00 AM",
            onBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DetailScreenMissingMetadataPreview() {
    RMNewsTheme {
        DetailScreen(
            title = "Example headline",
            url = "https://example.com/article",
            sourceName = null,
            formattedPublishedDate = null,
            onBack = {}
        )
    }
}
