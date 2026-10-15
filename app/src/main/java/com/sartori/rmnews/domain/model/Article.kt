package com.sartori.rmnews.domain.model

data class Article(
    val title: String,
    val url: String,
    val sourceName: String?,
    val publishedAt: String?,
    val description: String?
)
