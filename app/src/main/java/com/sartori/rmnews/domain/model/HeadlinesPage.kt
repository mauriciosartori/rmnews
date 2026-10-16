package com.sartori.rmnews.domain.model

data class HeadlinesPage(
    val articles: List<Article>,
    val totalResults: Int,
    // Count before filtering invalid articles, used to detect an empty API page.
    val receivedCount: Int = articles.size
)
