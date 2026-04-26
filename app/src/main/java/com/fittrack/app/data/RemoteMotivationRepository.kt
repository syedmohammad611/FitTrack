package com.fittrack.app.data

import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class RemoteMotivationRepository {
    fun fetchMotivation(): String {
        val connection = (URL(ENDPOINT).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 7000
            readTimeout = 7000
        }

        return try {
            val response = connection.inputStream.bufferedReader().use { it.readText() }
            val quote = JSONObject(response).optString("quote", "").trim()
            val author = JSONObject(response).optString("author", "Unknown").trim()
            if (quote.isBlank()) {
                DEFAULT_TEXT
            } else {
                "\"$quote\" - $author"
            }
        } catch (_: Exception) {
            DEFAULT_TEXT
        } finally {
            connection.disconnect()
        }
    }

    companion object {
        private const val ENDPOINT = "https://dummyjson.com/quotes/random"
        private const val DEFAULT_TEXT = "Consistency beats intensity. Keep showing up."
    }
}
