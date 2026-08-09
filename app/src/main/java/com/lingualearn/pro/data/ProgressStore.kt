package com.lingualearn.pro.data

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class ProgressState(
    val totalXp: Int = 0,
    val lessonsCompleted: Int = 0,
    val completionDates: Set<String> = emptySet(),
    val completedChallengeIds: Set<String> = emptySet(),
    val bestScores: Map<String, Int> = emptyMap(),
) {
    val level: Int get() = totalXp / XP_PER_LEVEL + 1
    val xpIntoLevel: Int get() = totalXp % XP_PER_LEVEL
    val xpToNextLevel: Int get() = XP_PER_LEVEL - xpIntoLevel
    val levelProgress: Float get() = xpIntoLevel / XP_PER_LEVEL.toFloat()
    val currentStreak: Int get() {
        if (completionDates.isEmpty()) return 0
        val calendar = Calendar.getInstance()
        val today = dateKey(calendar.timeInMillis)
        if (today !in completionDates) {
            calendar.add(Calendar.DAY_OF_YEAR, -1)
            if (dateKey(calendar.timeInMillis) !in completionDates) return 0
        }
        var streak = 0
        while (dateKey(calendar.timeInMillis) in completionDates) {
            streak++
            calendar.add(Calendar.DAY_OF_YEAR, -1)
        }
        return streak
    }

    companion object {
        const val XP_PER_LEVEL = 500
    }
}

class ProgressStore(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences(
        "lingualearn_progress",
        Context.MODE_PRIVATE,
    )

    var state by mutableStateOf(load())
        private set

    /**
     * Awards XP once for a stable ID. The ID is persisted before observable
     * state changes, so recomposition and navigation can never duplicate it.
     */
    fun awardOnce(awardId: String, xp: Int, lessonCompleted: Boolean = false): Boolean {
        val awards = preferences.getStringSet(KEY_AWARDS, emptySet()).orEmpty().toMutableSet()
        if (!awards.add(awardId)) return false
        val dates = state.completionDates + todayKey()
        val newState = state.copy(
            totalXp = state.totalXp + xp,
            lessonsCompleted = state.lessonsCompleted + if (lessonCompleted) 1 else 0,
            completionDates = dates,
        )
        preferences.edit()
            .putStringSet(KEY_AWARDS, awards)
            .putInt(KEY_XP, newState.totalXp)
            .putInt(KEY_LESSONS, newState.lessonsCompleted)
            .putStringSet(KEY_DATES, dates)
            .apply()
        state = newState
        return true
    }

    fun completeChallenge(challengeId: String, xp: Int, score: Int, scoreKey: String = challengeId) {
        updateBestScore(scoreKey, score)
        val completed = state.completedChallengeIds + challengeId
        if (completed != state.completedChallengeIds) {
            preferences.edit().putStringSet(KEY_CHALLENGES, completed).apply()
            state = state.copy(completedChallengeIds = completed)
        }
        awardOnce("challenge:$challengeId", xp)
    }

    fun updateBestScore(key: String, score: Int) {
        if (score <= (state.bestScores[key] ?: -1)) return
        val scores = state.bestScores + (key to score)
        preferences.edit().putString(KEY_BEST_SCORES, encodeScores(scores)).apply()
        state = state.copy(bestScores = scores)
    }

    private fun load() = ProgressState(
        totalXp = preferences.getInt(KEY_XP, 0),
        lessonsCompleted = preferences.getInt(KEY_LESSONS, 0),
        completionDates = preferences.getStringSet(KEY_DATES, emptySet()).orEmpty().toSet(),
        completedChallengeIds = preferences.getStringSet(KEY_CHALLENGES, emptySet()).orEmpty().toSet(),
        bestScores = decodeScores(preferences.getString(KEY_BEST_SCORES, "").orEmpty()),
    )

    private fun encodeScores(scores: Map<String, Int>) =
        scores.entries.joinToString("|") { "${it.key}=${it.value}" }

    private fun decodeScores(value: String): Map<String, Int> = value
        .split("|")
        .mapNotNull { entry ->
            val separator = entry.lastIndexOf('=')
            if (separator <= 0) null
            else entry.substring(separator + 1).toIntOrNull()?.let { entry.substring(0, separator) to it }
        }
        .toMap()

    companion object {
        private const val KEY_XP = "total_xp"
        private const val KEY_LESSONS = "lessons_completed"
        private const val KEY_DATES = "completion_dates"
        private const val KEY_AWARDS = "award_ids"
        private const val KEY_CHALLENGES = "completed_challenges"
        private const val KEY_BEST_SCORES = "best_scores"

        fun todayKey(): String = dateKey(System.currentTimeMillis())
    }
}

private fun dateKey(timeMillis: Long): String =
    SimpleDateFormat("yyyy-MM-dd", Locale.US).format(timeMillis)
