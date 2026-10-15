package com.sartori.rmnews.ui.main

/**
 * UI state exposed by [MainScreenViewModel] and rendered by [MainScreen].
 * Loading state controls the screen's progress indicator.
 */
data class MainScreenUiState(
    val isLoading: Boolean = false
)
