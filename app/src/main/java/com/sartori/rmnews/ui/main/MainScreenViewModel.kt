package com.sartori.rmnews.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sartori.rmnews.domain.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val newsRepository: NewsRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<MainScreenUiState> (MainScreenUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init{
        loadNewsFeed()
    }

    fun loadNewsFeed(){
        viewModelScope.launch {
            try {
                val articles = newsRepository.getTopHeadlines().articles
                _uiState.update {
                    MainScreenUiState.Success(articles)
                }
            }catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    MainScreenUiState.Error("Sorry, We can't display news at this time. \n Try again later")
                }
            }

        }
    }

}
