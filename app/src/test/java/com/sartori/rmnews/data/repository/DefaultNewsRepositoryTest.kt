package com.sartori.rmnews.data.repository

import com.sartori.rmnews.data.remote.NewsApi
import com.sartori.rmnews.data.remote.dto.ArticleDto
import com.sartori.rmnews.data.remote.dto.HeadlinesResponseDto
import com.sartori.rmnews.data.remote.dto.SourceDto
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.IOException

class DefaultNewsRepositoryTest {
    @Test
    fun mapsUsableArticlesAndPreservesServerTotal() = runBlocking {
        val api = object : NewsApi {
            override suspend fun getTopHeadlines(
                apiKey: String, country: String, page: Int, pageSize: Int
            ): HeadlinesResponseDto {
                assertEquals("test-key", apiKey)
                assertEquals(2, page)
                assertEquals(20, pageSize)
                return HeadlinesResponseDto(
                    status = "ok",
                    totalResults = 42,
                    articles = listOf(
                        ArticleDto(title = "Headline", url = "https://example.com", source = SourceDto(name = "Source")),
                        ArticleDto(title = null, url = "https://example.com"),
                        ArticleDto(title = "Missing URL", url = " ")
                    )
                )
            }
        }
        val result = DefaultNewsRepository(api, "test-key").getTopHeadlines(page = 2)
        assertEquals(42, result.totalResults)
        assertEquals(1, result.articles.size)
        assertEquals("Headline", result.articles.single().title)
        assertEquals("Source", result.articles.single().sourceName)
    }

    @Test(expected = IOException::class)
    fun propagatesNetworkFailure() {
        runBlocking {
            val api = object : NewsApi {
                override suspend fun getTopHeadlines(
                    apiKey: String, country: String, page: Int, pageSize: Int
                ): HeadlinesResponseDto = throw IOException("No connection")
            }
            DefaultNewsRepository(api, "test-key").getTopHeadlines()
        }
    }
}
