package com.fittrack.app.data

import com.fittrack.app.models.Quote
import com.fittrack.app.models.QuotesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface QuoteApiService {
    /**
     * Fetch a list of quotes from the DummyJSON API
     * @param limit Number of quotes to fetch (default: 10)
     * @param skip Number of quotes to skip for pagination
     */
    @GET("https://dummyjson.com/quotes")
    suspend fun getQuotes(
        @Query("limit") limit: Int = 10,
        @Query("skip") skip: Int = 0
    ): QuotesResponse

    /**
     * Fetch a random single quote
     */
    @GET("https://dummyjson.com/quotes/random")
    suspend fun getRandomQuote(): Quote
}

