package com.sartori.rmnews.ui.main.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sartori.rmnews.R
import com.sartori.rmnews.domain.model.Article
import com.sartori.rmnews.ui.main.MainScreenUiState
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
internal fun NewsList(
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
