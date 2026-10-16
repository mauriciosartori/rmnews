package com.sartori.rmnews.ui.main.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sartori.rmnews.R
import com.sartori.rmnews.domain.model.Article
import com.sartori.rmnews.ui.main.MainScreenUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NewsFeedContent(
    content: MainScreenUiState.Content,
    onRefresh: () -> Unit,
    onArticleClick: (Article) -> Unit,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        if (content.refreshFailed) {
            Text(
                text = stringResource(R.string.refresh_failed),
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        PullToRefreshBox(
            isRefreshing = content.isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier.fillMaxSize()
        ) {
            NewsList(content, onArticleClick, onLoadMore)
        }
    }
}
