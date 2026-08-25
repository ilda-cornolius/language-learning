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
            ${readingHint(course)}

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

    suspend fun startRolePlay(course: LanguageCourse, scenario: ConversationScenario): String {
        val prompt = """
            You are a native ${course.name} speaker in this role-play: ${scenario.title}.
            Setting: ${scenario.description}
            Start the conversation naturally in ${course.name} at a ${course.level.lowercase()} level.
            After the ${course.name} line, add a short English translation in parentheses.
            ${readingHint(course)}
            Keep it to 1-2 sentences. Do not mention that you are an AI.
        """.trimIndent()
        return model.generateContent(prompt).text
            ?.trim()
            ?.takeIf { it.isNotEmpty() }
            ?: error("The AI conversation returned an empty response")
    }

    suspend fun rolePlay(
        course: LanguageCourse,
        scenario: ConversationScenario,
        conversation: List<Pair<Boolean, String>>,
        userMessage: String,
    ): String {
        val recentConversation = conversation
            .takeLast(10)
            .joinToString("\n") { (fromUser, text) ->
                "${if (fromUser) "Student" else "Partner"}: $text"
            }
        val prompt = """
            You are a native ${course.name} conversation partner in this role-play: ${scenario.title}.
            Setting: ${scenario.description}
            The student is ${course.level.lowercase()}. Stay in character and keep the scene moving.
            Reply mostly in ${course.name}, then give a brief English translation in parentheses.
            ${readingHint(course)}
            If the student makes a mistake, gently correct it in one short English note after the translation.
            Keep replies concise (2-4 sentences). Ask one natural follow-up in ${course.name}.

            Conversation so far:
            $recentConversation
            Student: $userMessage

            Reply as the conversation partner:
        """.trimIndent()
        return model.generateContent(prompt).text
            ?.trim()
            ?.takeIf { it.isNotEmpty() }
            ?: error("The AI conversation returned an empty response")
    }

    private fun readingHint(course: LanguageCourse): String = when (course.id) {
        "mandarin" ->
            "Whenever you write Chinese characters, immediately add Hanyu Pinyin with tone marks in parentheses, for example 你好 (nǐ hǎo)."
        "korean" ->
            "Whenever you write Hangul, immediately add Revised Romanization in parentheses, for example 안녕하세요 (annyeonghaseyo)."
        else -> ""
    }
}
