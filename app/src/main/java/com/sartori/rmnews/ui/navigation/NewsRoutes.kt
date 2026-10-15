package com.sartori.rmnews.ui.navigation

import com.sartori.rmnews.domain.model.Article
import kotlinx.serialization.Serializable

@Serializable
data object MainRoute

/** Small article snapshot retained as navigation arguments for detail restoration. */
@Serializable
data class DetailRoute(
    val title: String,
    val url: String,
    val sourceName: String? = null,
    val publishedAt: String? = null
)

fun Article.toDetailRoute(): DetailRoute = DetailRoute(
    title = title,
    url = url,
    sourceName = sourceName,
    publishedAt = publishedAt
)
