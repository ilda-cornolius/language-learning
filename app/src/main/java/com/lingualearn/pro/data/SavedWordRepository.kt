package com.lingualearn.pro.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

data class SavedWord(
    val id: Long,
    val languageId: String,
    val word: String,
    val meaning: String,
    val exampleSentence: String,
    val createdAt: Long,
)

object SavedWordRepository {
    @Volatile
    private var helper: SavedWordsDatabase? = null

    private fun database(context: Context): SavedWordsDatabase =
        helper ?: synchronized(this) {
            helper ?: SavedWordsDatabase(context.applicationContext).also { helper = it }
        }

    suspend fun save(
        context: Context,
        languageId: String,
        word: String,
        meaning: String,
        exampleSentence: String,
    ) = withContext(Dispatchers.IO) {
        val values = ContentValues().apply {
            put("language_id", languageId)
            put("word", word.trim())
            put("meaning", meaning.trim())
            put("example_sentence", exampleSentence.trim())
            put("created_at", System.currentTimeMillis())
        }
        database(context).writableDatabase.insertWithOnConflict(
            "saved_words",
            null,
            values,
            SQLiteDatabase.CONFLICT_REPLACE,
        )
    }

    suspend fun wordsForLanguage(context: Context, languageId: String): List<SavedWord> =
        withContext(Dispatchers.IO) {
            database(context).readableDatabase.query(
                "saved_words",
                arrayOf("id", "language_id", "word", "meaning", "example_sentence", "created_at"),
                "language_id = ?",
                arrayOf(languageId),
                null,
                null,
                "created_at DESC",
            ).use { cursor ->
                buildList {
                    while (cursor.moveToNext()) {
                        add(
                            SavedWord(
                                id = cursor.getLong(0),
                                languageId = cursor.getString(1),
                                word = cursor.getString(2),
                                meaning = cursor.getString(3),
                                exampleSentence = cursor.getString(4),
                                createdAt = cursor.getLong(5),
                            )
                        )
                    }
                }
            }
        }

    suspend fun allWords(context: Context): List<SavedWord> = withContext(Dispatchers.IO) {
        database(context).readableDatabase.query(
            "saved_words",
            arrayOf("id", "language_id", "word", "meaning", "example_sentence", "created_at"),
            null,
            null,
            null,
            null,
            "created_at DESC",
        ).use { cursor ->
            buildList {
                while (cursor.moveToNext()) {
                    add(
                        SavedWord(
                            id = cursor.getLong(0),
                            languageId = cursor.getString(1),
                            word = cursor.getString(2),
                            meaning = cursor.getString(3),
                            exampleSentence = cursor.getString(4),
                            createdAt = cursor.getLong(5),
                        )
                    )
                }
            }
        }
    }
}

object DictionaryLookupRepository {
    suspend fun lookup(word: String, sourceLanguage: String): String? =
        translate(word, sourceLanguage, "en")

    suspend fun translate(text: String, sourceLanguage: String, targetLanguage: String): String? =
        withContext(Dispatchers.IO) {
            runCatching {
                val encoded = URLEncoder.encode(text.trim(), Charsets.UTF_8.name())
                val url =
                    "https://api.mymemory.translated.net/get?q=$encoded&langpair=$sourceLanguage%7C$targetLanguage"
                val connection = (URL(url).openConnection() as HttpURLConnection).apply {
                    connectTimeout = 4_000
                    readTimeout = 4_000
                    requestMethod = "GET"
                    setRequestProperty("Accept", "application/json")
                }
                try {
                    check(connection.responseCode in 200..299)
                    val body = connection.inputStream.bufferedReader().use { it.readText() }
                    JSONObject(body)
                        .getJSONObject("responseData")
                        .optString("translatedText")
                        .takeIf { it.isNotBlank() && !it.equals(text, ignoreCase = true) }
                } finally {
                    connection.disconnect()
                }
            }.getOrNull()
        }
}

private class SavedWordsDatabase(context: Context) :
    SQLiteOpenHelper(context, "lingualearn_words.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE saved_words (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                language_id TEXT NOT NULL,
                word TEXT NOT NULL,
                meaning TEXT NOT NULL,
                example_sentence TEXT NOT NULL DEFAULT '',
                created_at INTEGER NOT NULL,
                UNIQUE(language_id, word)
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) = Unit
}
