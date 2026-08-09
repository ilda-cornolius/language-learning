package com.lingualearn.pro.data

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.json.JSONArray

class PreferencesStore(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences(
        "lingualearn_preferences",
        Context.MODE_PRIVATE,
    )

    var voiceSpeed by mutableStateOf(preferences.getString(KEY_VOICE_SPEED, "Normal") ?: "Normal")
        private set

    var soundEffects by mutableStateOf(preferences.getBoolean(KEY_SOUND_EFFECTS, true))
        private set

    var dailyReminders by mutableStateOf(preferences.getBoolean(KEY_DAILY_REMINDERS, true))
        private set

    var offlineMode by mutableStateOf(preferences.getBoolean(KEY_OFFLINE_MODE, false))
        private set

    var activeLanguageId by mutableStateOf(
        preferences.getString(KEY_ACTIVE_LANGUAGE, "spanish") ?: "spanish",
    )
        private set

    var displayName by mutableStateOf(
        preferences.getString(KEY_DISPLAY_NAME, "Maria Rodriguez") ?: "Maria Rodriguez",
    )
        private set

    var learnerSinceYear by mutableStateOf(preferences.getInt(KEY_LEARNER_SINCE, 2023))
        private set

    fun updateVoiceSpeed(value: String) {
        if (value !in SPEEDS) return
        preferences.edit().putString(KEY_VOICE_SPEED, value).apply()
        voiceSpeed = value
    }

    fun updateSoundEffects(enabled: Boolean) {
        preferences.edit().putBoolean(KEY_SOUND_EFFECTS, enabled).apply()
        soundEffects = enabled
    }

    fun updateDailyReminders(enabled: Boolean) {
        preferences.edit().putBoolean(KEY_DAILY_REMINDERS, enabled).apply()
        dailyReminders = enabled
    }

    fun updateOfflineMode(enabled: Boolean) {
        preferences.edit().putBoolean(KEY_OFFLINE_MODE, enabled).apply()
        offlineMode = enabled
    }

    fun updateActiveLanguageId(languageId: String) {
        preferences.edit().putString(KEY_ACTIVE_LANGUAGE, languageId).apply()
        activeLanguageId = languageId
    }

    fun updateDisplayName(name: String) {
        val trimmed = name.trim().ifBlank { "Learner" }
        preferences.edit().putString(KEY_DISPLAY_NAME, trimmed).apply()
        displayName = trimmed
    }

    fun updateLearnerSinceYear(year: Int) {
        preferences.edit().putInt(KEY_LEARNER_SINCE, year).apply()
        learnerSinceYear = year
    }

    fun speechRate(): Float = when (voiceSpeed) {
        "Slow" -> 0.7f
        "Fast" -> 1.3f
        else -> 1.0f
    }

    companion object {
        val SPEEDS = listOf("Slow", "Normal", "Fast")
        private const val KEY_VOICE_SPEED = "voice_speed"
        private const val KEY_SOUND_EFFECTS = "sound_effects"
        private const val KEY_DAILY_REMINDERS = "daily_reminders"
        private const val KEY_OFFLINE_MODE = "offline_mode"
        private const val KEY_ACTIVE_LANGUAGE = "active_language_id"
        private const val KEY_DISPLAY_NAME = "display_name"
        private const val KEY_LEARNER_SINCE = "learner_since_year"
    }
}

class SocialStore(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences(
        "lingualearn_social",
        Context.MODE_PRIVATE,
    )

    fun isFollowing(handle: String): Boolean =
        preferences.getBoolean(followKey(handle), false)

    fun setFollowing(handle: String, following: Boolean) {
        preferences.edit().putBoolean(followKey(handle), following).apply()
    }

    fun isLiked(postId: Int): Boolean =
        preferences.getBoolean(likeKey(postId), false)

    fun setLiked(postId: Int, liked: Boolean) {
        preferences.edit().putBoolean(likeKey(postId), liked).apply()
    }

    fun commentsFor(postId: Int, seed: List<PostComment>): List<PostComment> {
        val raw = preferences.getString(commentKey(postId), null) ?: return seed
        return runCatching {
            val array = JSONArray(raw)
            buildList {
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    add(PostComment(obj.getString("author"), obj.getString("body")))
                }
            }
        }.getOrDefault(seed)
    }

    fun saveComments(postId: Int, comments: List<PostComment>) {
        val array = JSONArray()
        comments.forEach { comment ->
            array.put(
                org.json.JSONObject()
                    .put("author", comment.author)
                    .put("body", comment.body),
            )
        }
        preferences.edit().putString(commentKey(postId), array.toString()).apply()
    }

    private fun followKey(handle: String) = "follow:$handle"
    private fun likeKey(postId: Int) = "like:$postId"
    private fun commentKey(postId: Int) = "comments:$postId"
}
