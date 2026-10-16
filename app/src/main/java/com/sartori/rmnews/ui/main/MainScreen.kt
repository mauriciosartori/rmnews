package com.sartori.rmnews.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sartori.rmnews.domain.model.Article
import com.sartori.rmnews.ui.main.components.EmptySearchResults
import com.sartori.rmnews.ui.main.components.NewsError
import com.sartori.rmnews.ui.main.components.NewsFeedContent
import com.sartori.rmnews.ui.main.components.NewsLoading
import com.sartori.rmnews.ui.main.components.NewsSearchField
import com.sartori.rmnews.ui.theme.RMNewsTheme

/** Arranges the search field and the current feed state. */
@Composable
fun MainScreen(
    uiState: MainScreenUiState,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onRetry: () -> Unit,
    onRefresh: () -> Unit,
    onArticleClick: (Article) -> Unit,
    onLoadMore: () -> Unit = {},
    modifier: Modifier = Modifier,
    isSearchVisible: Boolean = false
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AnimatedVisibility(
            visible = isSearchVisible,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            NewsSearchField(
                query = searchQuery,
                onQueryChanged = onSearchQueryChanged,
                modifier = Modifier.fillMaxWidth()
            )
        }
        when (uiState) {
            MainScreenUiState.Loading -> NewsLoading(modifier = Modifier.fillMaxSize())
            is MainScreenUiState.Content -> {
                if (uiState.news.isEmpty() && !uiState.canLoadMore && searchQuery.isNotBlank()) {
                    EmptySearchResults(
                        query = searchQuery.trim(),
                        onClearSearch = { onSearchQueryChanged("") },
                        modifier = Modifier.weight(1f).fillMaxWidth()
                    )
                } else {
                    NewsFeedContent(
                        content = uiState,
                        onRefresh = onRefresh,
                        onArticleClick = onArticleClick,
                        onLoadMore = onLoadMore,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            is MainScreenUiState.Error -> NewsError(
                message = uiState.message,
                onRetry = onRetry,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MainScreenPreview() {
    RMNewsTheme {
        MainScreen(
            uiState = MainScreenUiState.Loading,
            searchQuery = "",
            onSearchQueryChanged = {},
            onRetry = {},
            onRefresh = {},
            onArticleClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MainScreenErrorPreview() {
    RMNewsTheme {
        MainScreen(
            uiState = MainScreenUiState.Error("Unable to load news."),
            searchQuery = "",
            onSearchQueryChanged = {},
            onRetry = {},
            onRefresh = {},
            onArticleClick = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
private fun MainScreenEmptySearchPreview() {
    RMNewsTheme {
        MainScreen(
            uiState = MainScreenUiState.Content(emptyList()),
            searchQuery = "mexico",
            onSearchQueryChanged = {},
            onRetry = {},
            onRefresh = {},
            onArticleClick = {},
            modifier = Modifier.fillMaxSize()
        )
    }
}
