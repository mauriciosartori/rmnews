package com.sartori.rmnews.domain.model

data class HeadlinesPage(
    val articles: List<Article>,
    val totalResults: Int
)
