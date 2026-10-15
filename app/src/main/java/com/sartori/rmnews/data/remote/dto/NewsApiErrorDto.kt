package com.sartori.rmnews.data.remote.dto

import com.google.gson.annotations.SerializedName

data class NewsApiErrorDto(
    @SerializedName("status") val status: String? = null,
    @SerializedName("code") val code: String? = null,
    @SerializedName("message") val message: String? = null
)
