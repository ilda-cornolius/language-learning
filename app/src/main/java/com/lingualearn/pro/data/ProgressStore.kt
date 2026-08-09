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
    val courseXp: Map<String, Int> = emptyMap(),
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

    /** Days toward Perfect Week (streak capped at 7). */
    val perfectWeekProgress: Int get() = currentStreak.coerceAtMost(7)

    companion object {
        const val XP_PER_LEVEL = 500
        const val PERFECT_WEEK_ID = "perfect-week"
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
     * When [courseId] is set, the same XP is also credited to that course.
     */
    fun awardOnce(
        awardId: String,
        xp: Int,
        lessonCompleted: Boolean = false,
        courseId: String? = null,
    ): Boolean {
        val awards = preferences.getStringSet(KEY_AWARDS, emptySet()).orEmpty().toMutableSet()
        if (!awards.add(awardId)) return false
        val dates = if (lessonCompleted) state.completionDates + todayKey() else state.completionDates
        val courseXp = if (courseId != null) {
            state.courseXp + (courseId to ((state.courseXp[courseId] ?: 0) + xp))
        } else {
            state.courseXp
        }
        val newState = state.copy(
            totalXp = state.totalXp + xp,
            lessonsCompleted = state.lessonsCompleted + if (lessonCompleted) 1 else 0,
            completionDates = dates,
            courseXp = courseXp,
        )
        preferences.edit()
            .putStringSet(KEY_AWARDS, awards)
            .putInt(KEY_XP, newState.totalXp)
            .putInt(KEY_LESSONS, newState.lessonsCompleted)
            .putStringSet(KEY_DATES, dates)
            .putString(KEY_COURSE_XP, encodeScores(courseXp))
            .apply()
        state = newState
        if (lessonCompleted) maybeAwardPerfectWeek()
        return true
    }

    /** Marks today as an active practice day for streak / Perfect Week. */
    fun markDayActive() {
        val today = todayKey()
        if (today in state.completionDates) {
            maybeAwardPerfectWeek()
            return
        }
        val dates = state.completionDates + today
        preferences.edit().putStringSet(KEY_DATES, dates).apply()
        state = state.copy(completionDates = dates)
        maybeAwardPerfectWeek()
    }

    /** Progress percent for a course: 100 XP → 10%, capped at 100. */
    fun courseProgress(courseId: String): Int =
        ((state.courseXp[courseId] ?: 0) / 10).coerceAtMost(100)

    fun completeChallenge(
        challengeId: String,
        xp: Int,
        score: Int,
        scoreKey: String = challengeId,
        courseId: String? = null,
    ) {
        updateBestScore(scoreKey, score)
        val completed = state.completedChallengeIds + challengeId
        if (completed != state.completedChallengeIds) {
            preferences.edit().putStringSet(KEY_CHALLENGES, completed).apply()
            state = state.copy(completedChallengeIds = completed)
        }
        awardOnce("challenge:$challengeId", xp, courseId = courseId)
        markDayActive()
    }

    fun updateBestScore(key: String, score: Int) {
        if (score <= (state.bestScores[key] ?: -1)) return
        val scores = state.bestScores + (key to score)
        preferences.edit().putString(KEY_BEST_SCORES, encodeScores(scores)).apply()
        state = state.copy(bestScores = scores)
    }

    private fun maybeAwardPerfectWeek() {
        if (state.perfectWeekProgress < 7) return
        if (ProgressState.PERFECT_WEEK_ID in state.completedChallengeIds) return
        completeChallenge(ProgressState.PERFECT_WEEK_ID, 1000, 7)
    }

    private fun load() = ProgressState(
        totalXp = preferences.getInt(KEY_XP, 0),
        lessonsCompleted = preferences.getInt(KEY_LESSONS, 0),
        completionDates = preferences.getStringSet(KEY_DATES, emptySet()).orEmpty().toSet(),
        completedChallengeIds = preferences.getStringSet(KEY_CHALLENGES, emptySet()).orEmpty().toSet(),
        bestScores = decodeScores(preferences.getString(KEY_BEST_SCORES, "").orEmpty()),
        courseXp = decodeScores(preferences.getString(KEY_COURSE_XP, "").orEmpty()),
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
        private const val KEY_COURSE_XP = "course_xp"

        fun todayKey(): String = dateKey(System.currentTimeMillis())
    }
}

private fun dateKey(timeMillis: Long): String =
    SimpleDateFormat("yyyy-MM-dd", Locale.US).format(timeMillis)
