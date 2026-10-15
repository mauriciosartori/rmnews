package com.sartori.rmnews.ui.main

import com.sartori.rmnews.domain.model.Article

/**
 * UI state exposed by [MainScreenViewModel] and rendered by [MainScreen].
 * Initial loading replaces content; refresh state keeps the current news visible.
 */
sealed interface MainScreenUiState {
    data object Loading : MainScreenUiState

    data class Content(
        val news: List<Article>,
        val isRefreshing: Boolean = false,
        val refreshFailed: Boolean = false
    ) : MainScreenUiState

    data class Error(val message: String) : MainScreenUiState
}

