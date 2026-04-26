package com.fittrack.app.data

import com.fittrack.app.models.Quote
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RemoteQuoteRepository {
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://dummyjson.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService: QuoteApiService = retrofit.create(QuoteApiService::class.java)

    /**
     * Fetch multiple quotes from the remote API
     * @param limit Number of quotes to fetch (default: 10)
     * @param skip Number of quotes to skip for pagination (default: 0)
     * @return List of Quote objects or empty list on error
     */
    suspend fun fetchQuotes(limit: Int = 10, skip: Int = 0): List<Quote> {
        return try {
            val response = apiService.getQuotes(limit = limit, skip = skip)
            response.quotes
        } catch (e: Exception) {
            // Log error and return empty list for graceful degradation
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Fetch a single random quote
     * @return Quote object or null on error
     */
    suspend fun fetchRandomQuote(): Quote? {
        return try {
            apiService.getRandomQuote()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

