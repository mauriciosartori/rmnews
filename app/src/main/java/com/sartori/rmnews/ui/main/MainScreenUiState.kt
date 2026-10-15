package com.sartori.rmnews.ui.main

import com.sartori.rmnews.domain.model.Article

/**
 * UI state exposed by [MainScreenViewModel] and rendered by [MainScreen].
 * Loading state controls the screen's progress indicator.
 */
sealed interface MainScreenUiState {
    data object Loading : MainScreenUiState

    data class Success(val news: List<Article>) : MainScreenUiState

    data class Error(val message: String) : MainScreenUiState
}

