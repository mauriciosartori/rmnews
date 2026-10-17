package com.sartori.rmnews.ui.detail

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
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
    imageUrl: String? = null,
    description: String? = null,
    onReadFullArticle: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrimFadeHeight = with(LocalDensity.current) { 32.dp.toPx() }
    var linkFailed by remember(url) { mutableStateOf(false) }
    val publicationDate = formattedPublishedDate
        ?: stringResource(R.string.date_unavailable)

    Column(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val placeholder = painterResource(R.drawable.ic_article_placeholder)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            ) {
                // The image fills the larger of the default photo area and the full title.
                Spacer(Modifier.fillMaxWidth().aspectRatio(16f / 9f))
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    placeholder = placeholder,
                    error = placeholder,
                    fallback = placeholder,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )
                Box(
                    modifier = Modifier.matchParentSize().background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.15f))
                        )
                    )
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .padding(top = 48.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.25f),
                                    Color.Black.copy(alpha = 0.45f)
                                ),
                                endY = scrimFadeHeight
                            )
                        )
                        .padding(start = 16.dp, end = 16.dp, top = 32.dp, bottom = 16.dp)
                        .semantics { heading() }
                )
            }

            Text(
                text = stringResource(R.string.article_published, publicationDate),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            description?.trim()?.takeIf { it.isNotEmpty() }?.let { articleDescription ->
                Text(
                    text = articleDescription,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Text(
                text = stringResource(
                    R.string.article_source,
                    sourceName?.takeIf { it.isNotBlank() } ?: stringResource(R.string.source_unavailable)
                ),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End,
                style = MaterialTheme.typography.titleMedium
            )
        }
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (linkFailed) {
                Text(
                    text = stringResource(R.string.article_link_error),
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
                )
            }
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
            description = "Researchers are exploring new ways to make renewable energy more accessible and efficient."
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
            formattedPublishedDate = null
        )
    }
}
