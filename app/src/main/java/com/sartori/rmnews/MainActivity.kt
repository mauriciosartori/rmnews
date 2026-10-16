package com.sartori.rmnews

import android.os.Bundle
import com.sartori.rmnews.analytics.NewsAnalytics
import javax.inject.Inject
import dagger.hilt.android.AndroidEntryPoint
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sartori.rmnews.ui.navigation.NewsNavHost
import com.sartori.rmnews.ui.main.MainScreenViewModel
import com.sartori.rmnews.ui.theme.RMNewsTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var newsAnalytics: NewsAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RMNewsTheme {
                val viewModel: MainScreenViewModel = viewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NewsNavHost(
                        uiState = uiState,
                        searchQuery = searchQuery,
                        onSearchQueryChanged = viewModel::onSearchQueryChanged,
                        onRetry = viewModel::loadNewsFeed,
                        onRefresh = {
                            newsAnalytics.trackRefresh()
                            viewModel.loadNewsFeed()
                        },
                        onArticleOpened = newsAnalytics::trackArticleOpened,
                        onFullArticleClicked = newsAnalytics::trackFullArticleClicked,
                        onLoadMore = viewModel::loadNextPage,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
            }
        }
    }
}
