package com.lingualearn.pro.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

enum class Destination(
    val route: String,
    val title: String,
    val shortLabel: String,
    val icon: ImageVector?,
) {
    Lesson("lesson", "Spanish Lesson 12: Everyday Conversations", "Lesson", Icons.Filled.School),
    Spanish("spanish", "Spanish Learning Dashboard", "Spanish", null),
    French("french", "French Learning Dashboard", "French", null),
    Japanese("japanese", "Japanese Learning Dashboard", "Japanese", null),
    Vocabulary("vocabulary", "Vocabulary Builder", "Vocabulary", Icons.Filled.Book),
    Conversation("conversation", "Conversation Practice", "Conversation", Icons.AutoMirrored.Filled.Chat),
    Listening("listening", "Listening Exercises", "Listening", Icons.Filled.Headphones),
    Writing("writing", "Writing Practice", "Writing", Icons.Filled.Edit),
    Assistant("assistant", "AI Language Assistant", "AI Tutor", Icons.Filled.SmartToy),
    Instagram("instagram", "Instagram Language Learning", "Instagram", Icons.Filled.PhotoCamera),
    Google("google", "Google Language Tools", "Google", Icons.Filled.Search),
    Profile("profile", "User Profile", "Profile", Icons.Filled.Person),
    Preferences("preferences", "Preferences", "Preferences", Icons.Filled.Settings),
    DailyLesson("daily-lesson", "Today's Spanish Lesson", "Daily Lesson", Icons.Filled.School),
    Practice("practice", "Practice Exercises", "Practice", Icons.Filled.FitnessCenter),
    QuickReview("quick-review", "Quick Review", "Quick Review", Icons.Filled.Book),
    GrammarDrills("grammar-drills", "Grammar Drills", "Grammar Drills", Icons.Filled.School),
    PronunciationLab("pronunciation-lab", "Pronunciation Lab", "Pronunciation Lab", Icons.Filled.Headphones),
    DictationNotebook("dictation-notebook", "Dictation Notebook", "Dictation Notebook", Icons.Filled.Edit),
    Challenges("challenges", "Weekly Challenges", "Challenges", Icons.Filled.EmojiEvents);

    companion object {
        val activities = listOf(Vocabulary, Conversation, Listening, Writing, Assistant)
        val social = listOf(Instagram, Google)
        val settings = listOf(Profile, Preferences)
        val toolbar = listOf(DailyLesson, Practice, Challenges)

        fun forCourse(courseId: String): Destination = when (courseId) {
            "french" -> French
            "japanese" -> Japanese
            else -> Spanish
        }

        fun courseIdFor(destination: Destination): String? = when (destination) {
            Spanish -> "spanish"
            French -> "french"
            Japanese -> "japanese"
            else -> null
        }

        fun titleFor(destination: Destination, languageName: String): String = when (destination) {
            Vocabulary -> "$languageName Vocabulary Builder"
            Conversation -> "$languageName Conversation Practice"
            Listening -> "$languageName Listening Exercises"
            Writing -> "$languageName Writing Practice"
            Assistant -> "$languageName AI Tutor"
            DailyLesson -> "Today's $languageName Lesson"
            Practice -> "$languageName Practice Exercises"
            QuickReview -> "$languageName Quick Review"
            GrammarDrills -> "$languageName Grammar Drills"
            PronunciationLab -> "$languageName Pronunciation Lab"
            DictationNotebook -> "$languageName Dictation Notebook"
            Challenges -> "$languageName Weekly Challenges"
            Lesson -> "$languageName Grammar Lesson"
            else -> destination.title
        }
    }
}
