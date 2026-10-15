package com.sartori.rmnews.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sartori.rmnews.R
import com.sartori.rmnews.domain.model.Article
import com.sartori.rmnews.ui.theme.RMNewsTheme

@Composable
fun MainScreen(
    uiState: MainScreenUiState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineMedium
        )

        when (uiState) {
            MainScreenUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is MainScreenUiState.Success -> {
                NewsList(uiState.news)
            }

            is MainScreenUiState.Error -> {
                Text(uiState.message)
            }

        }
    }
}

@Composable
fun NewsList(articles: List<Article>) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        itemsIndexed(
            items = articles,
            key = { _, article -> article.url }
        ) { index, article ->
            if (index > 0) HorizontalDivider()
            ArticleRow(article = article)
        }
    }
}

@Composable
fun ArticleRow(article: Article) {
    Column() {
        Text(article.title)
        Text(article.sourceName ?: "N/A")
        Box(contentAlignment = Alignment.CenterEnd) {
            Text(article.publishedAt ?: "N/A")
        }
    }

}

@Preview(showBackground = true)
@Composable
private fun MainScreenPreview() {
    RMNewsTheme {
        MainScreen(
            uiState = MainScreenUiState.Loading
        )
    }
}
