package com.lingualearn.pro.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class PhotoOfTheDay(
    val imageUrl: String,
    val caption: String,
)

/**
 * Loads Bing's daily wallpaper — a free photo-of-the-day feed that needs no API key.
 * Falls back to a Spain Unsplash still if Bing is unreachable.
 */
object PhotoOfTheDayRepository {

    private const val BING_FEED = "https://www.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1"
    private const val FALLBACK_IMAGE =
        "https://images.unsplash.com/photo-1558642452-9d2a7deb7f62?auto=format&fit=crop&w=800&q=80"
    private const val FALLBACK_CAPTION = "Photo of the day: España"

    suspend fun load(): PhotoOfTheDay = withContext(Dispatchers.IO) {
        runCatching { fetchBing() }.getOrElse {
            PhotoOfTheDay(FALLBACK_IMAGE, FALLBACK_CAPTION)
        }
    }

    private fun fetchBing(): PhotoOfTheDay {
        val connection = (URL(BING_FEED).openConnection() as HttpURLConnection).apply {
            connectTimeout = 8_000
            readTimeout = 8_000
            requestMethod = "GET"
            setRequestProperty("User-Agent", "LinguaLearnPro/2.3.1")
        }
        try {
            val body = connection.inputStream.bufferedReader().use { it.readText() }
            val image = JSONObject(body).getJSONArray("images").getJSONObject(0)
            val path = image.getString("url")
            val title = image.optString("title")
                .ifBlank { image.optString("copyright") }
                .ifBlank { "Photo of the day" }
            return PhotoOfTheDay(
                imageUrl = "https://www.bing.com$path",
                caption = title,
            )
        } finally {
            connection.disconnect()
        }
    }
}
