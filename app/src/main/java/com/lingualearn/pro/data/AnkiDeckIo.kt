package com.lingualearn.pro.data

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Anki text-format import/export (CSV/TSV / plain .txt).
 *
 * Preferred Anki path: File → Export → Notes in Plain Text (tab-separated).
 * .apkg (zipped collection) is not supported here — use plain-text export instead.
 */
object AnkiDeckIo {
    data class ImportedNote(
        val front: String,
        val back: String,
        val extra: String = "",
    )

    fun parseText(raw: String): List<ImportedNote> {
        val text = raw.removePrefix("\uFEFF")
        return text.lineSequence()
            .map { it.trimEnd('\r') }
            .filter { line ->
                val trimmed = line.trim()
                trimmed.isNotEmpty() && !trimmed.startsWith("#")
            }
            .mapNotNull { line -> parseLine(line) }
            .toList()
    }

    private fun parseLine(line: String): ImportedNote? {
        val fields = when {
            line.contains('\t') -> line.split('\t')
            else -> splitCsv(line)
        }.map { it.trim().trim('"') }
        if (fields.size < 2) return null
        val front = fields[0]
        val back = fields[1]
        if (front.isEmpty() || back.isEmpty()) return null
        val extra = fields.getOrNull(2).orEmpty()
        return ImportedNote(front, back, extra)
    }

    private fun splitCsv(line: String): List<String> {
        val result = mutableListOf<String>()
        val current = StringBuilder()
        var inQuotes = false
        var i = 0
        while (i < line.length) {
            val c = line[i]
            when {
                c == '"' -> {
                    if (inQuotes && i + 1 < line.length && line[i + 1] == '"') {
                        current.append('"')
                        i++
                    } else {
                        inQuotes = !inQuotes
                    }
                }
                c == ',' && !inQuotes -> {
                    result.add(current.toString())
                    current.clear()
                }
                else -> current.append(c)
            }
            i++
        }
        result.add(current.toString())
        return result
    }

    fun exportTsv(cards: List<Flashcard>): String = buildString {
        cards.forEach { card ->
            append(card.front.replace('\t', ' ').replace('\n', ' '))
            append('\t')
            append(card.back.replace('\t', ' ').replace('\n', ' '))
            if (card.extra.isNotBlank()) {
                append('\t')
                append(card.extra.replace('\t', ' ').replace('\n', ' '))
            }
            append('\n')
        }
    }

    suspend fun readUri(context: Context, uri: Uri): List<ImportedNote> =
        withContext(Dispatchers.IO) {
            val stream = context.contentResolver.openInputStream(uri)
                ?: return@withContext emptyList()
            stream.use { input ->
                val text = BufferedReader(InputStreamReader(input, Charsets.UTF_8)).readText()
                parseText(text)
            }
        }

    suspend fun writeUri(context: Context, uri: Uri, content: String): Boolean =
        withContext(Dispatchers.IO) {
            runCatching {
                context.contentResolver.openOutputStream(uri)?.use { out ->
                    out.write(content.toByteArray(Charsets.UTF_8))
                } != null
            }.getOrDefault(false)
        }
}
