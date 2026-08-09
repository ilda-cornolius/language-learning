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

data class GrammarExample(
    val phrase: String,
    val translation: String,
)

data class ExerciseQuestion(
    val prompt: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
)

data class DailyGrammarLesson(
    val number: Int,
    val title: String,
    val subtitle: String,
    val level: String,
    val concept: String,
    val rule: String,
    val examples: List<GrammarExample>,
    val questions: List<ExerciseQuestion>,
)

data class PracticePack(
    val reviewWords: List<VocabWord>,
    val grammarQuestions: List<ExerciseQuestion>,
    val pronunciationPhrases: List<GrammarExample>,
    val localeTag: String,
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

    fun dailyGrammarLesson(courseId: String): DailyGrammarLesson = when (courseId) {
        "french" -> DailyGrammarLesson(
            number = 8,
            title = "Using avoir in the present tense",
            subtitle = "Learn how to describe what you have and talk about age in French",
            level = "Beginner",
            concept = "The verb avoir means “to have.” French also uses it in expressions where English uses “to be,” such as age.",
            rule = "Conjugate avoir to match the subject: j’ai, tu as, il/elle a, nous avons, vous avez, ils/elles ont.",
            examples = listOf(
                GrammarExample("J’ai un livre.", "I have a book."),
                GrammarExample("Elle a vingt ans.", "She is twenty years old."),
                GrammarExample("Nous avons deux chiens.", "We have two dogs."),
            ),
            questions = listOf(
                ExerciseQuestion(
                    "Choose the correct form: Je ___ une voiture.",
                    listOf("ai", "as", "a"),
                    0,
                    "Je uses j’ai: J’ai une voiture.",
                ),
                ExerciseQuestion(
                    "Choose the correct form: Nous ___ un cours aujourd’hui.",
                    listOf("avez", "avons", "ont"),
                    1,
                    "Nous uses avons.",
                ),
                ExerciseQuestion(
                    "How do you say “She is eighteen years old”?",
                    listOf("Elle est dix-huit ans.", "Elle a dix-huit ans.", "Elle ont dix-huit ans."),
                    1,
                    "French expresses age with avoir: Elle a dix-huit ans.",
                ),
            ),
        )
        "japanese" -> DailyGrammarLesson(
            number = 6,
            title = "Building sentences with は",
            subtitle = "Learn how the topic particle は organizes a basic Japanese sentence",
            level = "Beginner",
            concept = "The particle は (pronounced wa) marks what the sentence is about. It follows the topic.",
            rule = "Use: topic + は + information + です. The particle is written は but pronounced “wa.”",
            examples = listOf(
                GrammarExample("わたしは学生です。", "I am a student."),
                GrammarExample("これは本です。", "This is a book."),
                GrammarExample("田中さんは先生です。", "Mr./Ms. Tanaka is a teacher."),
            ),
            questions = listOf(
                ExerciseQuestion(
                    "Choose the topic particle: わたし ___ 学生です。",
                    listOf("を", "は", "に"),
                    1,
                    "は marks わたし as the topic.",
                ),
                ExerciseQuestion(
                    "What is the spoken pronunciation of は when it is a particle?",
                    listOf("ha", "wa", "ba"),
                    1,
                    "The topic particle は is pronounced “wa.”",
                ),
                ExerciseQuestion(
                    "Choose the best translation: これは本です。",
                    listOf("This is a book.", "That is a teacher.", "I read a book."),
                    0,
                    "これ means “this,” and 本 means “book.”",
                ),
            ),
        )
        else -> DailyGrammarLesson(
            number = 12,
            title = "Talking about completed actions",
            subtitle = "Learn how to use the Spanish preterite for actions completed in the past",
            level = "Intermediate",
            concept = "The preterite describes actions that started and finished at a specific time in the past.",
            rule = "Regular -ar verbs use é, aste, ó, amos, aron. Regular -er/-ir verbs use í, iste, ió, imos, ieron.",
            examples = listOf(
                GrammarExample("Ayer hablé con Ana.", "Yesterday I spoke with Ana."),
                GrammarExample("Comimos en un restaurante.", "We ate at a restaurant."),
                GrammarExample("Ellos vivieron en Madrid.", "They lived in Madrid."),
            ),
            questions = listOf(
                ExerciseQuestion(
                    "Complete the sentence: Ayer yo ___ con María.",
                    listOf("hablo", "hablé", "hablaré"),
                    1,
                    "Hablé is the first-person preterite form of hablar.",
                ),
                ExerciseQuestion(
                    "Choose the completed past action.",
                    listOf("Comemos ahora.", "Comeremos mañana.", "Comimos anoche."),
                    2,
                    "Anoche signals a completed past event, so use comimos.",
                ),
                ExerciseQuestion(
                    "Complete: Ellos ___ la película ayer.",
                    listOf("vieron", "ven", "verán"),
                    0,
                    "Vieron is the third-person plural preterite of ver.",
                ),
            ),
        )
    }

    fun practicePack(courseId: String): PracticePack {
        val lesson = dailyGrammarLesson(courseId)
        val activity = activityPack(courseId)
        val phrases = when (courseId) {
            "french" -> listOf(
                GrammarExample("Bonjour, comment allez-vous ?", "Hello, how are you?"),
                GrammarExample("J’ai passé une bonne journée.", "I had a good day."),
                GrammarExample("Je voudrais un café, s’il vous plaît.", "I would like a coffee, please."),
            )
            "japanese" -> listOf(
                GrammarExample("こんにちは。お元気ですか。", "Hello. How are you?"),
                GrammarExample("わたしは日本語を勉強しています。", "I am studying Japanese."),
                GrammarExample("コーヒーをお願いします。", "Coffee, please."),
            )
            else -> listOf(
                GrammarExample("Hola, ¿cómo estás?", "Hello, how are you?"),
                GrammarExample("Ayer fui al cine con mis amigos.", "Yesterday I went to the movies with my friends."),
                GrammarExample("Me gustaría un café, por favor.", "I would like a coffee, please."),
            )
        }
        return PracticePack(
            reviewWords = activity.vocabulary.take(6),
            grammarQuestions = lesson.questions,
            pronunciationPhrases = phrases,
            localeTag = when (courseId) {
                "french" -> "fr-FR"
                "japanese" -> "ja-JP"
                else -> "es-ES"
            },
        )
    }

    fun dictionaryWords(courseId: String): List<VocabWord> {
        val common = when (courseId) {
            "french" -> listOf(
                VocabWord("bonjour", "hello"),
                VocabWord("merci", "thank you"),
                VocabWord("un", "one"),
                VocabWord("deux", "two"),
                VocabWord("trois", "three"),
                VocabWord("ami", "friend"),
                VocabWord("école", "school"),
                VocabWord("manger", "to eat"),
                VocabWord("parler", "to speak"),
                VocabWord("aujourd’hui", "today"),
            )
            "japanese" -> listOf(
                VocabWord("こんにちは", "hello"),
                VocabWord("ありがとう", "thank you"),
                VocabWord("いち", "one"),
                VocabWord("に", "two"),
                VocabWord("さん", "three"),
                VocabWord("ともだち", "friend"),
                VocabWord("がっこう", "school"),
                VocabWord("たべる", "to eat"),
                VocabWord("はなす", "to speak"),
                VocabWord("きょう", "today"),
            )
            else -> listOf(
                VocabWord("hola", "hello"),
                VocabWord("gracias", "thank you"),
                VocabWord("uno", "one"),
                VocabWord("dos", "two"),
                VocabWord("tres", "three"),
                VocabWord("amigo", "friend"),
                VocabWord("escuela", "school"),
                VocabWord("comer", "to eat"),
                VocabWord("hablar", "to speak"),
                VocabWord("hoy", "today"),
            )
        }
        return (activityPack(courseId).vocabulary + common)
            .distinctBy { it.term.lowercase() }
            .sortedBy { it.term.lowercase() }
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
