package com.sartori.rmnews.data.remote

import com.sartori.rmnews.data.remote.dto.HeadlinesResponseDto
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

/** Retrofit contract for retrieving headlines from NewsAPI's v2 API. */
interface NewsApi {
    /**
     * Retrieves a page of top headlines for the selected country.
     *
     * @param apiKey Required NewsAPI credential, sent in the X-Api-Key header.
     * @param query Optional keywords used to filter headlines.
     * @param country Two-letter country code used to filter headlines.
     * @param page Page number, starting at 1.
     * @param pageSize Number of articles requested per page; defaults to 20, with a maximum of 100.
     */
    @GET("top-headlines")
    suspend fun getTopHeadlines(
        @Header("X-Api-Key") apiKey: String,
        @Query("country") country: String = "us",
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
        @Query("q") query: String? = null
    ): HeadlinesResponseDto
}
