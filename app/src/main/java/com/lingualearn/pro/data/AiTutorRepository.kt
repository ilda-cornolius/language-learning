package com.lingualearn.pro.data

import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend

object AiTutorRepository {
    private val model by lazy {
        Firebase.ai(backend = GenerativeBackend.googleAI())
            .generativeModel(modelName = "gemini-3.6-flash")
    }

    suspend fun reply(
        course: LanguageCourse,
        conversation: List<Pair<Boolean, String>>,
        userMessage: String,
    ): String {
        val recentConversation = conversation
            .takeLast(10)
            .joinToString("\n") { (fromUser, text) ->
                "${if (fromUser) "Student" else "Tutor"}: $text"
            }
        val prompt = """
            You are a friendly, accurate ${course.name} language tutor for a ${course.level.lowercase()} learner.
            Help the student learn through short, natural conversation.
            Correct mistakes gently, explain grammar clearly in English, and include ${course.name} examples.
            Keep replies concise (usually 2-5 sentences). Ask one useful follow-up question when appropriate.
            Never claim the student's answer is correct when it is not.

            Conversation so far:
            $recentConversation
            Student: $userMessage

            Reply as the tutor:
        """.trimIndent()

        return model.generateContent(prompt).text
            ?.trim()
            ?.takeIf { it.isNotEmpty() }
            ?: error("The AI tutor returned an empty response")
    }
}
