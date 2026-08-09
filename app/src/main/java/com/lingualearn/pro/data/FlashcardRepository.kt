package com.lingualearn.pro.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object FlashcardRepository {
    @Volatile
    private var helper: FlashcardsDatabase? = null

    private fun database(context: Context): FlashcardsDatabase =
        helper ?: synchronized(this) {
            helper ?: FlashcardsDatabase(context.applicationContext).also { helper = it }
        }

    suspend fun ensureDefaultDeck(context: Context, languageId: String, seed: Boolean = true): FlashcardDeck =
        withContext(Dispatchers.IO) {
            val existing = decksForLanguageSync(context, languageId).firstOrNull()
            if (existing != null) {
                if (seed && cardsInDeckSync(context, existing.id).isEmpty()) {
                    seedDeck(context, languageId, existing.id)
                }
                return@withContext existing
            }
            val courseName = SampleContent.courseById(languageId).name
            val now = System.currentTimeMillis()
            val values = ContentValues().apply {
                put("language_id", languageId)
                put("name", "$courseName Deck")
                put("created_at", now)
            }
            val id = database(context).writableDatabase.insert("decks", null, values)
            val deck = FlashcardDeck(id, languageId, "$courseName Deck", now)
            if (seed) seedDeck(context, languageId, id)
            deck
        }

    suspend fun decksForLanguage(context: Context, languageId: String): List<FlashcardDeck> =
        withContext(Dispatchers.IO) { decksForLanguageSync(context, languageId) }

    suspend fun cardsInDeck(context: Context, deckId: Long): List<Flashcard> =
        withContext(Dispatchers.IO) { cardsInDeckSync(context, deckId) }

    suspend fun dueCards(context: Context, languageId: String): List<Flashcard> {
        ensureDefaultDeck(context, languageId, seed = true)
        return withContext(Dispatchers.IO) {
            val deckIds = decksForLanguageSync(context, languageId).map { it.id }
            if (deckIds.isEmpty()) return@withContext emptyList()
            val now = System.currentTimeMillis()
            val placeholders = deckIds.joinToString(",") { "?" }
            val args = deckIds.map { it.toString() } + now.toString()
            database(context).readableDatabase.query(
                "cards",
                CARD_COLUMNS,
                "deck_id IN ($placeholders) AND due_at <= ?",
                args.toTypedArray(),
                null,
                null,
                "due_at ASC",
            ).use { cursor -> readCards(cursor) }
        }
    }

    suspend fun countDue(context: Context, languageId: String): Int =
        dueCards(context, languageId).size

    suspend fun countCards(context: Context, languageId: String): Int =
        withContext(Dispatchers.IO) {
            val deckIds = decksForLanguageSync(context, languageId).map { it.id }
            if (deckIds.isEmpty()) return@withContext 0
            val placeholders = deckIds.joinToString(",") { "?" }
            database(context).readableDatabase.rawQuery(
                "SELECT COUNT(*) FROM cards WHERE deck_id IN ($placeholders)",
                deckIds.map { it.toString() }.toTypedArray(),
            ).use { cursor ->
                if (cursor.moveToFirst()) cursor.getInt(0) else 0
            }
        }

    suspend fun addCard(
        context: Context,
        deckId: Long,
        front: String,
        back: String,
        extra: String = "",
    ): Long = withContext(Dispatchers.IO) {
        addCardSync(context, deckId, front, back, extra)
    }

    suspend fun addCards(
        context: Context,
        deckId: Long,
        cards: List<Triple<String, String, String>>,
    ): Int = withContext(Dispatchers.IO) {
        var added = 0
        val existing = cardsInDeckSync(context, deckId)
            .map { it.front.trim().lowercase() }
            .toMutableSet()
        cards.forEach { (front, back, extra) ->
            val key = front.trim().lowercase()
            if (key.isBlank() || back.isBlank() || key in existing) return@forEach
            if (addCardSync(context, deckId, front, back, extra) > 0) {
                existing.add(key)
                added++
            }
        }
        added
    }

    suspend fun reviewCard(context: Context, cardId: Long, rating: FlashcardRating): Flashcard? =
        withContext(Dispatchers.IO) {
            val card = cardByIdSync(context, cardId) ?: return@withContext null
            val updated = FlashcardScheduler.schedule(card, rating)
            val values = ContentValues().apply {
                put("ease", updated.ease)
                put("interval_days", updated.intervalDays)
                put("repetitions", updated.repetitions)
                put("lapses", updated.lapses)
                put("due_at", updated.dueAt)
            }
            database(context).writableDatabase.update(
                "cards",
                values,
                "id = ?",
                arrayOf(cardId.toString()),
            )
            updated
        }

    suspend fun importFromSavedWords(context: Context, languageId: String): Int {
        val deck = ensureDefaultDeck(context, languageId, seed = false)
        return seedDeck(context, languageId, deck.id)
    }

    suspend fun deleteCard(context: Context, cardId: Long): Boolean =
        withContext(Dispatchers.IO) {
            database(context).writableDatabase.delete(
                "cards",
                "id = ?",
                arrayOf(cardId.toString()),
            ) > 0
        }

    private suspend fun seedDeck(context: Context, languageId: String, deckId: Long): Int {
        val existingFronts = withContext(Dispatchers.IO) {
            cardsInDeckSync(context, deckId).map { it.front.trim().lowercase() }.toMutableSet()
        }
        var added = 0
        val now = System.currentTimeMillis()

        suspend fun tryAdd(front: String, back: String, extra: String = "") {
            val key = front.trim().lowercase()
            if (key.isBlank() || back.isBlank() || key in existingFronts) return
            val id = withContext(Dispatchers.IO) {
                val values = ContentValues().apply {
                    put("deck_id", deckId)
                    put("front", front.trim())
                    put("back", back.trim())
                    put("extra", extra.trim())
                    put("ease", FlashcardScheduler.DEFAULT_EASE)
                    put("interval_days", 0)
                    put("repetitions", 0)
                    put("lapses", 0)
                    put("due_at", now)
                    put("created_at", now)
                }
                database(context).writableDatabase.insert("cards", null, values)
            }
            if (id > 0) {
                existingFronts.add(key)
                added++
            }
        }

        SavedWordRepository.wordsForLanguage(context, languageId).forEach { word ->
            tryAdd(word.word, word.meaning, word.exampleSentence)
        }
        SampleContent.dictionaryWords(languageId).forEach { word ->
            tryAdd(word.term, word.meaning)
        }
        return added
    }

    private fun addCardSync(
        context: Context,
        deckId: Long,
        front: String,
        back: String,
        extra: String,
    ): Long {
        val trimmedFront = front.trim()
        val trimmedBack = back.trim()
        if (trimmedFront.isEmpty() || trimmedBack.isEmpty()) return -1L
        val exists = cardsInDeckSync(context, deckId).any {
            it.front.equals(trimmedFront, ignoreCase = true)
        }
        if (exists) return -1L
        val now = System.currentTimeMillis()
        val values = ContentValues().apply {
            put("deck_id", deckId)
            put("front", trimmedFront)
            put("back", trimmedBack)
            put("extra", extra.trim())
            put("ease", FlashcardScheduler.DEFAULT_EASE)
            put("interval_days", 0)
            put("repetitions", 0)
            put("lapses", 0)
            put("due_at", now)
            put("created_at", now)
        }
        return database(context).writableDatabase.insert("cards", null, values)
    }

    private fun decksForLanguageSync(context: Context, languageId: String): List<FlashcardDeck> {
        return database(context).readableDatabase.query(
            "decks",
            arrayOf("id", "language_id", "name", "created_at"),
            "language_id = ?",
            arrayOf(languageId),
            null,
            null,
            "created_at ASC",
        ).use { cursor ->
            buildList {
                while (cursor.moveToNext()) {
                    add(
                        FlashcardDeck(
                            id = cursor.getLong(0),
                            languageId = cursor.getString(1),
                            name = cursor.getString(2),
                            createdAt = cursor.getLong(3),
                        )
                    )
                }
            }
        }
    }

    private fun cardsInDeckSync(context: Context, deckId: Long): List<Flashcard> {
        return database(context).readableDatabase.query(
            "cards",
            CARD_COLUMNS,
            "deck_id = ?",
            arrayOf(deckId.toString()),
            null,
            null,
            "created_at DESC",
        ).use { cursor -> readCards(cursor) }
    }

    private fun cardByIdSync(context: Context, cardId: Long): Flashcard? {
        return database(context).readableDatabase.query(
            "cards",
            CARD_COLUMNS,
            "id = ?",
            arrayOf(cardId.toString()),
            null,
            null,
            null,
        ).use { cursor ->
            if (cursor.moveToFirst()) readCard(cursor) else null
        }
    }

    private fun readCards(cursor: android.database.Cursor): List<Flashcard> = buildList {
        while (cursor.moveToNext()) add(readCard(cursor))
    }

    private fun readCard(cursor: android.database.Cursor): Flashcard = Flashcard(
        id = cursor.getLong(0),
        deckId = cursor.getLong(1),
        front = cursor.getString(2),
        back = cursor.getString(3),
        extra = cursor.getString(4).orEmpty(),
        ease = cursor.getDouble(5),
        intervalDays = cursor.getInt(6),
        repetitions = cursor.getInt(7),
        lapses = cursor.getInt(8),
        dueAt = cursor.getLong(9),
        createdAt = cursor.getLong(10),
    )

    private val CARD_COLUMNS = arrayOf(
        "id", "deck_id", "front", "back", "extra",
        "ease", "interval_days", "repetitions", "lapses", "due_at", "created_at",
    )
}

private class FlashcardsDatabase(context: Context) :
    SQLiteOpenHelper(context, "lingualearn_flashcards.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE decks (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                language_id TEXT NOT NULL,
                name TEXT NOT NULL,
                created_at INTEGER NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE cards (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                deck_id INTEGER NOT NULL,
                front TEXT NOT NULL,
                back TEXT NOT NULL,
                extra TEXT NOT NULL DEFAULT '',
                ease REAL NOT NULL DEFAULT 2.5,
                interval_days INTEGER NOT NULL DEFAULT 0,
                repetitions INTEGER NOT NULL DEFAULT 0,
                lapses INTEGER NOT NULL DEFAULT 0,
                due_at INTEGER NOT NULL,
                created_at INTEGER NOT NULL,
                FOREIGN KEY(deck_id) REFERENCES decks(id) ON DELETE CASCADE
            )
            """.trimIndent()
        )
        db.execSQL("CREATE INDEX idx_cards_deck_due ON cards(deck_id, due_at)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) = Unit
}
