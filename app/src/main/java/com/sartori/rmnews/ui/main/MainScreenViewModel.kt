package com.sartori.rmnews.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sartori.rmnews.domain.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val newsRepository: NewsRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<MainScreenUiState>(MainScreenUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        loadNewsFeed()
    }

    fun loadNewsFeed() {
        if (loadJob?.isActive == true) return

        val previousContent = _uiState.value as? MainScreenUiState.Content
        _uiState.value = previousContent?.copy(
            isRefreshing = true,
            refreshFailed = false
        ) ?: MainScreenUiState.Loading
        loadJob = viewModelScope.launch {
            try {
                val articles = newsRepository.getTopHeadlines().articles
                _uiState.update {
                    MainScreenUiState.Content(articles)
                }
            } catch (e: CancellationException) {
                if (previousContent != null) {
                    _uiState.value = previousContent.copy(isRefreshing = false)
                }
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    previousContent?.copy(
                        isRefreshing = false,
                        refreshFailed = true
                    ) ?: MainScreenUiState.Error(
                        "Sorry, we can't display news at this time. Try again later."
                    )
                }
            }

        }
    }

}
