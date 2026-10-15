package com.sartori.rmnews.data.remote.dto

import com.google.gson.annotations.SerializedName

data class HeadlinesResponseDto(
    @SerializedName("status") val status: String? = null,
    @SerializedName("totalResults") val totalResults: Int = 0,
    @SerializedName("articles") val articles: List<ArticleDto> = emptyList()
)
