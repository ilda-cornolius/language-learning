package com.lingualearn.pro.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withTimeoutOrNull
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

data class PhotoOfTheDay(
    /** Coil model: https URL, local File, or asset path string. */
    val model: Any,
    val caption: String,
)

/**
 * Loads Bing's daily wallpaper — a free photo-of-the-day feed that needs no API key.
 * Falls back to a bundled asset when the network or Bing is unavailable.
 */
object PhotoOfTheDayRepository {

    private const val BING_FEED = "https://www.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1"
    private const val ASSET_FALLBACK = "file:///android_asset/photo_of_the_day_fallback.jpg"
    private const val FALLBACK_CAPTION = "Photo of the day"
    private const val CACHE_NAME = "photo_of_the_day.jpg"
    private const val FETCH_TIMEOUT_MS = 5_000L
    private const val USER_AGENT =
        "Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Mobile Safari/537.36"

    fun fallback(): PhotoOfTheDay = PhotoOfTheDay(ASSET_FALLBACK, FALLBACK_CAPTION)

    /**
     * Returns the bundled photo immediately-ready fallback, or Bing when reachable
     * within [FETCH_TIMEOUT_MS]. Never hangs on bad DNS.
     */
    suspend fun load(context: Context): PhotoOfTheDay = coroutineScope {
        val app = context.applicationContext
        val remote = async(Dispatchers.IO) {
            runCatching { fetchBing(app) }.getOrNull()
        }
        withTimeoutOrNull(FETCH_TIMEOUT_MS) { remote.await() } ?: fallback()
    }

    private fun fetchBing(context: Context): PhotoOfTheDay {
        val connection = openGet(BING_FEED)
        try {
            check(connection.responseCode in 200..299) {
                "Bing feed HTTP ${connection.responseCode}"
            }
            val body = connection.inputStream.bufferedReader().use { it.readText() }
            val image = JSONObject(body).getJSONArray("images").getJSONObject(0)
            val path = image.getString("url")
            val title = image.optString("title")
                .ifBlank { image.optString("copyright") }
                .ifBlank { FALLBACK_CAPTION }
            val imageUrl = if (path.startsWith("http")) path else "https://www.bing.com$path"
            val file = downloadToCache(context, imageUrl)
            return PhotoOfTheDay(model = file, caption = title)
        } finally {
            connection.disconnect()
        }
    }

    private fun downloadToCache(context: Context, imageUrl: String): File {
        val out = File(context.cacheDir, CACHE_NAME)
        val connection = openGet(imageUrl)
        try {
            check(connection.responseCode in 200..299) {
                "Bing image HTTP ${connection.responseCode}"
            }
            connection.inputStream.use { input ->
                out.outputStream().use { output -> input.copyTo(output) }
            }
            check(out.length() > 1_000L) { "Downloaded image too small" }
            return out
        } finally {
            connection.disconnect()
        }
    }

    private fun openGet(url: String): HttpURLConnection {
        return (URL(url).openConnection() as HttpURLConnection).apply {
            connectTimeout = 4_000
            readTimeout = 4_000
            instanceFollowRedirects = true
            requestMethod = "GET"
            setRequestProperty("User-Agent", USER_AGENT)
            setRequestProperty("Accept", "*/*")
        }
    }
}
