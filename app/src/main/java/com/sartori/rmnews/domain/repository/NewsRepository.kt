package com.sartori.rmnews.domain.repository

import com.sartori.rmnews.domain.model.HeadlinesPage

/** Provides headline pages without exposing the underlying data source. */
interface NewsRepository {
    /** Loads a page starting at 1. Failures propagate to the caller. */
    suspend fun getTopHeadlines(
        page: Int = 1,
        pageSize: Int = 20,
        query: String? = null
    ): HeadlinesPage
}
