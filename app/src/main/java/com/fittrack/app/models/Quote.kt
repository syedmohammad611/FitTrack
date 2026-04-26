package com.fittrack.app.models

import com.google.gson.annotations.SerializedName

data class Quote(
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("quote")
    val quote: String = "",

    @SerializedName("author")
    val author: String = "Unknown",

    @SerializedName("category")
    val category: String? = null
)

data class QuotesResponse(
    @SerializedName("quotes")
    val quotes: List<Quote> = emptyList(),

    @SerializedName("total")
    val total: Int = 0,

    @SerializedName("skip")
    val skip: Int = 0,

    @SerializedName("limit")
    val limit: Int = 0
)

