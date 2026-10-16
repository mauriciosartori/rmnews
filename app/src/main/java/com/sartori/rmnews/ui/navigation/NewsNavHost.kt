package com.sartori.rmnews.ui.navigation

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
    onRetry: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = MainRoute, modifier = modifier) {
        composable<MainRoute> {
            MainScreen(
                uiState = uiState,
                onRetry = onRetry,
                onRefresh = onRefresh,
                onArticleClick = { article ->
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
                onBack = { navController.popBackStack() }
            )
        }
    }
}
