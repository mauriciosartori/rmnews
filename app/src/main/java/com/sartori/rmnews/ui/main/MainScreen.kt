package com.sartori.rmnews.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil3.compose.AsyncImage
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sartori.rmnews.R
import com.sartori.rmnews.domain.model.Article
import com.sartori.rmnews.ui.theme.RMNewsTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    uiState: MainScreenUiState,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onRetry: () -> Unit,
    onRefresh: () -> Unit,
    onArticleClick: (Article) -> Unit,
    onLoadMore: () -> Unit = {},
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

        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChanged,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.search_headlines)) },
            singleLine = true,
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    TextButton(onClick = { onSearchQueryChanged("") }) {
                        Text(stringResource(R.string.clear_search))
                    }
                }
            }
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

            is MainScreenUiState.Content -> {
                if (uiState.news.isEmpty() && !uiState.canLoadMore && searchQuery.isNotBlank()) {
                    EmptySearchResults(
                        query = searchQuery.trim(),
                        onClearSearch = { onSearchQueryChanged("") },
                        modifier = Modifier.weight(1f).fillMaxWidth()
                    )
                } else {
                    Column(modifier = Modifier.weight(1f)) {
                        if (uiState.refreshFailed) {
                            Text(
                                text = stringResource(R.string.refresh_failed),
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        PullToRefreshBox(
                            isRefreshing = uiState.isRefreshing,
                            onRefresh = onRefresh,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            NewsList(uiState, onArticleClick, onLoadMore)
                        }
                    }
                }
            }

            is MainScreenUiState.Error -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(
                        16.dp,
                        Alignment.CenterVertically
                    ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(uiState.message)
                    Button(onClick = onRetry) {
                        Text(stringResource(R.string.retry))
                    }
                }
            }

        }
    }
}

@Composable
private fun EmptySearchResults(
    query: String,
    onClearSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.no_search_results, query),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(R.string.no_search_results_hint),
            textAlign = TextAlign.Center
        )
        Button(onClick = onClearSearch) {
            Text(stringResource(R.string.clear_search_action))
        }
    }
}

@Composable
fun NewsList(
    content: MainScreenUiState.Content,
    onArticleClick: (Article) -> Unit,
    onLoadMore: () -> Unit
) {
    val articles = content.news
    val listState = rememberLazyListState()
    LaunchedEffect(listState, content, onLoadMore) {
        if (!content.canLoadMore || content.isRefreshing || content.isLoadingMore || content.loadMoreFailed) {
            return@LaunchedEffect
        }
        snapshotFlow {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            lastVisible >= (articles.size - 3).coerceAtLeast(0)
        }.distinctUntilChanged().collect { nearEnd ->
            if (nearEnd) onLoadMore()
        }
    }
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (articles.isEmpty() && !content.canLoadMore) {
            item { Text(stringResource(R.string.no_news)) }
        }
        itemsIndexed(
            items = articles,
            key = { _, article -> article.url }
        ) { index, article ->
            if (index > 0) HorizontalDivider()
            ArticleRow(article = article, onClick = { onArticleClick(article) })
        }
        if (content.canLoadMore) {
            item(key = "pagination_footer") {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (content.loadMoreFailed) {
                        Text(stringResource(R.string.load_more_failed))
                        Button(onClick = onLoadMore) { Text(stringResource(R.string.retry)) }
                    } else if (content.isLoadingMore) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

@Composable
fun ArticleRow(article: Article, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        val placeholder = painterResource(R.drawable.ic_article_placeholder)
        AsyncImage(
            model = article.imageUrl,
            contentDescription = null,
            placeholder = placeholder,
            error = placeholder,
            fallback = placeholder,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(88.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(article.title, style = MaterialTheme.typography.titleMedium)
            Text(
                article.sourceName ?: stringResource(R.string.source_unavailable),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                article.formattedPublishedDate ?: stringResource(R.string.date_unavailable),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun ArticleRowPreview() {
    RMNewsTheme {
        ArticleRow(
            article = Article(
                title = "A new discovery brings fresh possibilities for renewable energy",
                url = "https://example.com/article",
                sourceName = "Example News",
                publishedAt = null,
                description = null,
                formattedPublishedDate = "Oct 16, 2026, 9:00 AM"
            ),
            onClick = {}
        )
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
