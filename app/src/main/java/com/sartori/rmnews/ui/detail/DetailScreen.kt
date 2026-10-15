package com.sartori.rmnews.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sartori.rmnews.R
import com.sartori.rmnews.ui.theme.RMNewsTheme

/** Detail scaffold. Article metadata and URL are ready for the screen implementation. */
@Composable
fun DetailScreen(
    title: String,
    url: String,
    sourceName: String?,
    publishedAt: String?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TextButton(onClick = onBack) {
            Text(stringResource(R.string.back))
        }
        Text(title)
        // TODO: Display source and publication date, and add the external article action.
    }
}

@Preview(showBackground = true)
@Composable
private fun DetailScreenPreview() {
    RMNewsTheme {
        DetailScreen(
            title = "Example headline",
            url = "https://example.com/article",
            sourceName = "Example News",
            publishedAt = "2026-10-15T12:00:00Z",
            onBack = {}
        )
    }
}
