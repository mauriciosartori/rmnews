package com.sartori.rmnews.ui.main

import com.sartori.rmnews.domain.model.Article
import com.sartori.rmnews.domain.model.HeadlinesPage
import com.sartori.rmnews.domain.repository.NewsRepository
import java.io.IOException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainScreenViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val article = Article("Headline", "https://example.com", "Source", null, null)

    @Before
    fun setUp() { Dispatchers.setMain(dispatcher) }

    @After
    fun tearDown() { Dispatchers.resetMain() }

    @Test
    fun refreshKeepsContentAndIgnoresRepeatedRequests() = runTest(dispatcher) {
        val repository = PendingRepository()
        val viewModel = MainScreenViewModel(repository)
        runCurrent()
        repository.pending.complete(HeadlinesPage(listOf(article), 1))
        runCurrent()

        repository.pending = CompletableDeferred()
        viewModel.loadNewsFeed()
        val refreshing = viewModel.uiState.value as MainScreenUiState.Content
        assertEquals(listOf(article), refreshing.news)
        assertTrue(refreshing.isRefreshing)
        viewModel.loadNewsFeed()
        runCurrent()
        assertEquals(2, repository.calls)

        repository.pending.complete(HeadlinesPage(emptyList(), 0))
        runCurrent()
        val result = viewModel.uiState.value as MainScreenUiState.Content
        assertTrue(result.news.isEmpty())
        assertFalse(result.isRefreshing)
    }

    @Test
    fun failedRefreshPreservesContentAndCanBeRetried() = runTest(dispatcher) {
        val repository = PendingRepository()
        val viewModel = MainScreenViewModel(repository)
        runCurrent()
        repository.pending.complete(HeadlinesPage(listOf(article), 1))
        runCurrent()

        repository.pending = CompletableDeferred()
        viewModel.loadNewsFeed()
        runCurrent()
        repository.pending.completeExceptionally(IOException("Offline"))
        runCurrent()
        val failed = viewModel.uiState.value as MainScreenUiState.Content
        assertEquals(listOf(article), failed.news)
        assertFalse(failed.isRefreshing)
        assertTrue(failed.refreshFailed)

        repository.pending = CompletableDeferred()
        viewModel.loadNewsFeed()
        assertFalse((viewModel.uiState.value as MainScreenUiState.Content).refreshFailed)
        runCurrent()
        repository.pending.complete(HeadlinesPage(listOf(article), 1))
        runCurrent()
        assertEquals(MainScreenUiState.Content(listOf(article)), viewModel.uiState.value)
    }

    @Test
    fun initialFailureUsesErrorAndRetryUsesLoading() = runTest(dispatcher) {
        val repository = PendingRepository()
        val viewModel = MainScreenViewModel(repository)
        runCurrent()
        repository.pending.completeExceptionally(IOException("Offline"))
        runCurrent()
        assertTrue(viewModel.uiState.value is MainScreenUiState.Error)

        repository.pending = CompletableDeferred()
        viewModel.loadNewsFeed()
        assertEquals(MainScreenUiState.Loading, viewModel.uiState.value)
        runCurrent()
        repository.pending.complete(HeadlinesPage(listOf(article), 1))
        runCurrent()
        assertEquals(MainScreenUiState.Content(listOf(article)), viewModel.uiState.value)
    }

    private class PendingRepository : NewsRepository {
        var pending = CompletableDeferred<HeadlinesPage>()
        var calls = 0
        override suspend fun getTopHeadlines(page: Int, pageSize: Int): HeadlinesPage {
            calls++
            return pending.await()
        }
    }
}
