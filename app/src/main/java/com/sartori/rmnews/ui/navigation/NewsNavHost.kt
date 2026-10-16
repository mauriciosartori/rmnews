package com.sartori.rmnews.ui.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalFocusManager
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.sartori.rmnews.ui.detail.DetailScreen
import com.sartori.rmnews.ui.main.MainScreen
import com.sartori.rmnews.ui.main.MainScreenUiState

/** Owns navigation; screens receive data and callbacks instead of a NavController. */
@Composable
fun NewsNavHost(
    uiState: MainScreenUiState,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onRetry: () -> Unit,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    onArticleOpened: (String) -> Unit,
    onFullArticleClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val isDetail = backStackEntry?.destination?.hasRoute<DetailRoute>() == true
    var isSearchVisible by rememberSaveable { mutableStateOf(searchQuery.isNotBlank()) }
    val focusManager = LocalFocusManager.current

    Column(modifier = modifier) {
        NewsTopBar(
            isDetail = isDetail,
            isSearchVisible = isSearchVisible,
            onBack = { navController.popBackStack() },
            onToggleSearch = {
                if (isSearchVisible) focusManager.clearFocus()
                isSearchVisible = !isSearchVisible
            }
        )
        NavHost(
            navController = navController,
            startDestination = MainRoute,
            modifier = Modifier.weight(1f).fillMaxWidth()
        ) {
            composable<MainRoute> {
                MainScreen(
                    isSearchVisible = isSearchVisible,
                    uiState = uiState,
                    searchQuery = searchQuery,
                    onSearchQueryChanged = onSearchQueryChanged,
                    onRetry = onRetry,
                    onRefresh = onRefresh,
                    onLoadMore = onLoadMore,
                    onArticleClick = { article ->
                        focusManager.clearFocus()
                        onArticleOpened(article.title)
                        navController.navigate(article.toDetailRoute()) {
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable<DetailRoute> { entry ->
                val route = entry.toRoute<DetailRoute>()
                DetailScreen(
                    title = route.title,
                    url = route.url,
                    sourceName = route.sourceName,
                    formattedPublishedDate = route.formattedPublishedDate,
                    imageUrl = route.imageUrl,
                    description = route.description,
                    onReadFullArticle = { onFullArticleClicked(route.title) }
                )
            }
        }
    }
}
