package com.sartori.rmnews.data.repository

import com.sartori.rmnews.data.mapper.formatPublicationDate
import com.sartori.rmnews.domain.repository.NewsRepository
import com.sartori.rmnews.data.remote.NewsApi
import com.sartori.rmnews.data.remote.dto.ArticleDto
import com.sartori.rmnews.domain.model.Article
import com.sartori.rmnews.domain.model.HeadlinesPage
import java.io.IOException
import javax.inject.Inject
import javax.inject.Named

/** Retrieves headlines and maps API data to models used by the app. */
class DefaultNewsRepository @Inject constructor(
    private val newsApi: NewsApi,
    @param:Named("newsApiKey") private val apiKey: String
) : NewsRepository {
    /**
     * Loads a page starting at 1, omitting articles without a title or URL.
     * Network and HTTP failures propagate to the caller for UI error handling.
     */
    override suspend fun getTopHeadlines(
        page: Int,
        pageSize: Int,
        query: String?
    ): HeadlinesPage {
        require(page >= 1) { "Page must start at 1." }
        require(pageSize in 1..100) { "Page size must be between 1 and 100." }
        check(apiKey.isNotBlank()) { "Set NEWS_API_KEY in local.properties." }

        val response = newsApi.getTopHeadlines(
            apiKey = apiKey,
            page = page,
            pageSize = pageSize,
            query = query?.trim()?.takeIf { it.isNotEmpty() }
        )
        if (response.status != "ok") {
            throw IOException("NewsAPI returned an unsuccessful response.")
        }
        return HeadlinesPage(
            articles = response.articles.mapNotNull { it.toArticleOrNull() },
            totalResults = response.totalResults
        )
    }
}

private fun ArticleDto.toArticleOrNull(): Article? {
    val title = title?.takeIf { it.isNotBlank() } ?: return null
    val url = url?.takeIf { it.isNotBlank() } ?: return null
    return Article(
        title = title,
        url = url,
        sourceName = source?.name,
        publishedAt = publishedAt,
        description = description,
        formattedPublishedDate = formatPublicationDate(publishedAt)
    )
}
