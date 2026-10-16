package com.sartori.rmnews.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sartori.rmnews.domain.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val newsRepository: NewsRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<MainScreenUiState>(MainScreenUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private var loadJob: Job? = null

    init {
        loadNewsFeed()
    }

    fun onSearchQueryChanged(value: String) {
        val previousQuery = _searchQuery.value.trim()
        _searchQuery.value = value
        if (value.trim() == previousQuery) return

        // Cancel immediately, including during the debounce, to prevent stale results.
        loadJob?.cancel()
        requestNews(
            keepContent = false,
            debounceDuration = if (value.isBlank()) Duration.ZERO else 1100.milliseconds
        )
    }

    fun loadNewsFeed() {
        if (loadJob?.isActive == true) return
        requestNews(keepContent = true)
    }

    private fun requestNews(
        keepContent: Boolean,
        debounceDuration: Duration = Duration.ZERO
    ) {
        val query = _searchQuery.value.trim().takeIf { it.isNotEmpty() }
        val previousContent = if (keepContent) _uiState.value as? MainScreenUiState.Content else null
        _uiState.value = previousContent?.copy(
            isRefreshing = true,
            refreshFailed = false
        ) ?: MainScreenUiState.Loading

        loadJob = viewModelScope.launch {
            try {
                delay(debounceDuration)
                val articles = newsRepository.getTopHeadlines(page = 1, query = query).articles
                ensureActive()
                _uiState.value = MainScreenUiState.Content(articles)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                ensureActive()
                _uiState.value = previousContent?.copy(
                    isRefreshing = false,
                    refreshFailed = true
                ) ?: MainScreenUiState.Error(
                    "Sorry, we can't display news at this time. Try again later."
                )
            }
        }
    }
}
