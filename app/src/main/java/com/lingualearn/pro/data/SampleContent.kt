package com.lingualearn.pro.data

import androidx.compose.ui.graphics.Color
import com.lingualearn.pro.ui.theme.VistaAccent
import com.lingualearn.pro.ui.theme.VistaBlue
import com.lingualearn.pro.ui.theme.VistaGreen

data class LanguageCourse(
    val id: String,
    val name: String,
    val flag: String,
    val progress: Int,
    val level: String,
    val accent: Color,
    val recentLessons: List<String>,
)

data class DialogueLine(
    val speaker: String,
    val speakerColor: Color,
    val phrase: String,
    val translation: String,
)

data class VocabWord(
    val term: String,
    val meaning: String,
    val emoji: String? = null,
)

data class SocialPost(
    val id: Int,
    val author: String,
    val body: String,
    val likes: Int,
    val comments: List<PostComment>,
)

data class PostComment(val author: String, val body: String)

data class SuggestedFollow(val handle: String, val role: String)

data class Challenge(
    val title: String,
    val description: String,
    val reward: String,
    val actionable: Boolean,
    val progressLabel: String? = null,
)

object SampleContent {

    val courses = listOf(
        LanguageCourse(
            id = "spanish",
            name = "Spanish",
            flag = "\uD83C\uDDEA\uD83C\uDDF8",
            progress = 85,
            level = "Intermediate",
            accent = VistaAccent,
            recentLessons = listOf("Everyday Conversations", "Past Tense Practice", "Restaurant Vocabulary"),
        ),
        LanguageCourse(
            id = "french",
            name = "French",
            flag = "\uD83C\uDDEB\uD83C\uDDF7",
            progress = 42,
            level = "Beginner",
            accent = VistaBlue,
            recentLessons = listOf("Basic Greetings", "Numbers 1-20", "Colors and Shapes"),
        ),
        LanguageCourse(
            id = "japanese",
            name = "Japanese",
            flag = "\uD83C\uDDEF\uD83C\uDDF5",
            progress = 23,
            level = "Beginner",
            accent = VistaGreen,
            recentLessons = listOf("Hiragana Practice", "Basic Introductions", "Common Phrases"),
        ),
    )

    val dialogue = listOf(
        DialogueLine("A", VistaGreen, "¿Cómo estuvo tu fin de semana?", "How was your weekend?"),
        DialogueLine("B", Color(0xFF2ABBC4), "Fue muy divertido. Fui al cine con mis amigos.", "It was very fun. I went to the movies with my friends."),
        DialogueLine("A", VistaGreen, "¿Qué película vieron?", "What movie did you watch?"),
    )

    val lessonVocabulary = listOf(
        VocabWord("fin de semana", "weekend"),
        VocabWord("divertido", "fun"),
        VocabWord("el cine", "the movie theater"),
        VocabWord("película", "movie"),
    )

    val vocabularyBuilder = listOf(
        VocabWord("manzana", "apple", "\uD83C\uDF4E"),
        VocabWord("coche", "car", "\uD83D\uDE97"),
        VocabWord("casa", "house", "\uD83C\uDFE0"),
        VocabWord("perro", "dog", "\uD83D\uDC15"),
        VocabWord("playa", "beach", "\uD83C\uDFD6\uFE0F"),
        VocabWord("libro", "book", "\uD83D\uDCDA"),
    )

    private val frenchVocabulary = listOf(
        VocabWord("pomme", "apple", "\uD83C\uDF4E"),
        VocabWord("voiture", "car", "\uD83D\uDE97"),
        VocabWord("maison", "house", "\uD83C\uDFE0"),
        VocabWord("chien", "dog", "\uD83D\uDC15"),
        VocabWord("plage", "beach", "\uD83C\uDFD6\uFE0F"),
        VocabWord("livre", "book", "\uD83D\uDCDA"),
    )

    private val japaneseVocabulary = listOf(
        VocabWord("りんご", "apple", "\uD83C\uDF4E"),
        VocabWord("くるま", "car", "\uD83D\uDE97"),
        VocabWord("いえ", "house", "\uD83C\uDFE0"),
        VocabWord("いぬ", "dog", "\uD83D\uDC15"),
        VocabWord("うみ", "beach", "\uD83C\uDFD6\uFE0F"),
        VocabWord("ほん", "book", "\uD83D\uDCDA"),
    )

    data class ActivityPack(
        val vocabulary: List<VocabWord>,
        val nativeLabel: String,
        val listeningTitle: String,
        val listeningDescription: String,
        val writingPrompt: String,
        val writingPlaceholder: String,
        val writingSuccess: String,
        val tutorGreeting: String,
        val tutorPlaceholder: String,
        val tutorReplies: List<String>,
        val conversationNote: String,
    )

    fun activityPack(courseId: String): ActivityPack = when (courseId) {
        "french" -> ActivityPack(
            vocabulary = frenchVocabulary,
            nativeLabel = "français",
            listeningTitle = "French Café Podcast",
            listeningDescription = "Listen to everyday conversations in French",
            writingPrompt = "Describe your ideal vacation in French (100 words minimum)",
            writingPlaceholder = "Écrivez ici...",
            writingSuccess = "Très bien! Your tutor will review this entry.",
            tutorGreeting = "Bonjour! I'm your AI French tutor. How can I help you today?",
            tutorPlaceholder = "Ask me anything about French...",
            tutorReplies = listOf(
                "Bonne question! In French, 'être' is used for identity and 'avoir' often forms compound tenses.",
                "Essayez à voix haute: \"J'ai passé le week-end avec mes amis.\"",
                "Astuce: learn chunks like \"je voudrais\" (I would like) instead of full conjugation tables.",
                "Très bien! Want a quick quiz on greetings and numbers?",
            ),
            conversationNote = "Practice these scenarios in French",
        )
        "japanese" -> ActivityPack(
            vocabulary = japaneseVocabulary,
            nativeLabel = "日本語",
            listeningTitle = "Japanese Daily Podcast",
            listeningDescription = "Listen to beginner-friendly Japanese clips",
            writingPrompt = "Describe your ideal vacation in Japanese (as much as you can)",
            writingPlaceholder = "ここに書いてください...",
            writingSuccess = "よくできました! Your tutor will review this entry.",
            tutorGreeting = "こんにちは! I'm your AI Japanese tutor. How can I help you today?",
            tutorPlaceholder = "Ask me anything about Japanese...",
            tutorReplies = listOf(
                "いい質問です! Particles like は and が mark the topic and subject differently.",
                "Try saying it out loud: 「友達と映画を見ました。」",
                "Tip: learn phrases in chunks, like 「〜たいです」 (I want to ~).",
                "よくできました! Want to practice hiragana next?",
            ),
            conversationNote = "Practice these scenarios in Japanese",
        )
        else -> ActivityPack(
            vocabulary = vocabularyBuilder,
            nativeLabel = "español",
            listeningTitle = "Spanish News Podcast",
            listeningDescription = "Listen to current events in Spanish",
            writingPrompt = "Describe your ideal vacation in Spanish (100 words minimum)",
            writingPlaceholder = "Escribe aquí...",
            writingSuccess = "¡Bien hecho! Your tutor will review this entry.",
            tutorGreeting = "¡Hola! I'm your AI Spanish tutor. How can I help you today?",
            tutorPlaceholder = "Ask me anything about Spanish...",
            tutorReplies = spanishTutorReplies,
            conversationNote = "Practice these scenarios in Spanish",
        )
    }

    fun courseById(id: String): LanguageCourse =
        courses.firstOrNull { it.id == id } ?: courses.first()

    val suggestedFollows = listOf(
        SuggestedFollow("@maria_spanish", "Native Speaker"),
        SuggestedFollow("@carlos_madrid", "Language Exchange"),
        SuggestedFollow("@spanish_daily", "Learning Tips"),
    )

    val posts = listOf(
        SocialPost(
            id = 1,
            author = "@maria_spanish",
            body = "\"¡Buenos días! Today's word: 'madrugada' - early morning before dawn \uD83C\uDF05\"",
            likes = 24,
            comments = listOf(
                PostComment("@learner_alex", "Great word! I never knew this one. Gracias! \uD83D\uDE0A"),
                PostComment("@spanish_lover", "Perfect timing! I was just wondering about this word yesterday \uD83D\uDE4C"),
            ),
        ),
        SocialPost(
            id = 2,
            author = "@carlos_madrid",
            body = "\"Practice tip: Watch Spanish Netflix with Spanish subtitles! \uD83D\uDCFA\"",
            likes = 42,
            comments = listOf(
                PostComment("@netflix_fan", "This is exactly what I do! Elite and Money Heist are perfect for learning \uD83C\uDFAC"),
                PostComment("@beginner_sara", "Should I start with English subtitles first? I'm still a beginner \uD83E\uDD14"),
                PostComment("@carlos_madrid", "@beginner_sara Yes! Start with English subs, then switch to Spanish when you feel ready \uD83D\uDC4D"),
            ),
        ),
    )

    val challenges = listOf(
        Challenge(
            title = "Speed Round Challenge",
            description = "Translate 20 words in under 2 minutes",
            reward = "500 XP",
            actionable = true,
        ),
        Challenge(
            title = "Perfect Week",
            description = "Complete lessons 7 days in a row",
            reward = "1000 XP",
            actionable = false,
            progressLabel = "4/7 days",
        ),
    )

    /** Canned replies for the AI tutor so the screen is usable without a backend. */
    val spanishTutorReplies = listOf(
        "¡Buena pregunta! In Spanish, 'ser' describes permanent traits and 'estar' describes states or locations.",
        "Try saying it out loud: \"Fui al cine con mis amigos.\" The preterite 'fui' works for both 'ir' and 'ser'.",
        "Un consejo: learn verbs in chunks, like 'me gustaría' (I would like), instead of memorising full tables.",
        "¡Muy bien! Want me to quiz you on the vocabulary from Lesson 12?",
    )
}
