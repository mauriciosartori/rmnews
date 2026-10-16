package com.sartori.rmnews.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sartori.rmnews.domain.repository.NewsRepository
import com.sartori.rmnews.domain.model.HeadlinesPage
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
        if (loadJob?.isActive == true) {
            if ((_uiState.value as? MainScreenUiState.Content)?.isLoadingMore != true) return
            loadJob?.cancel()
        }
        requestNews(keepContent = true)
    }

    fun loadNextPage() {
        val content = _uiState.value as? MainScreenUiState.Content ?: return
        if (!content.canLoadMore || content.isRefreshing || loadJob?.isActive == true) return
        val page = content.currentPage + 1
        val query = _searchQuery.value.trim().takeIf { it.isNotEmpty() }
        _uiState.value = content.copy(isLoadingMore = true, loadMoreFailed = false)
        loadJob = viewModelScope.launch {
            try {
                val result = newsRepository.getTopHeadlines(
                    page = page,
                    pageSize = PAGE_SIZE,
                    query = query
                )
                ensureActive()
                _uiState.value = content.copy(
                    news = (content.news + result.articles).distinctBy { it.url },
                    currentPage = page,
                    canLoadMore = result.hasNextPage(page),
                    isLoadingMore = false,
                    loadMoreFailed = false
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                ensureActive()
                _uiState.value = content.copy(isLoadingMore = false, loadMoreFailed = true)
            }
        }
    }

    // Use server pagination, not the deduplicated/filtered list size, to find the end.
    private fun HeadlinesPage.hasNextPage(page: Int): Boolean =
        receivedCount > 0 && page.toLong() * PAGE_SIZE < totalResults

    private fun requestNews(
        keepContent: Boolean,
        debounceDuration: Duration = Duration.ZERO
    ) {
        val query = _searchQuery.value.trim().takeIf { it.isNotEmpty() }
        val previousContent = if (keepContent) {
            (_uiState.value as? MainScreenUiState.Content)?.copy(isLoadingMore = false, loadMoreFailed = false)
        } else null
        _uiState.value = previousContent?.copy(
            isRefreshing = true,
            refreshFailed = false
        ) ?: MainScreenUiState.Loading

        loadJob = viewModelScope.launch {
            try {
                delay(debounceDuration)
                val result = newsRepository.getTopHeadlines(page = 1, pageSize = PAGE_SIZE, query = query)
                ensureActive()
                _uiState.value = MainScreenUiState.Content(
                    news = result.articles.distinctBy { it.url },
                    canLoadMore = result.hasNextPage(1)
                )
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
    private companion object {
        const val PAGE_SIZE = 10
    }
}
