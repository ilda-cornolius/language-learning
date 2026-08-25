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
    val localeTag: String = "en-US",
    val optional: Boolean = false,
    val nativeLabel: String = "",
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
    val reading: String? = null,
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
    val reading: String? = null,
)

data class ExerciseQuestion(
    val prompt: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
)

data class CourseLesson(
    val id: String,
    val number: Int,
    val title: String,
    val subtitle: String,
    val level: String,
    /** Opens the full grammar lesson when true; otherwise the daily lesson intro. */
    val opensGrammarLesson: Boolean,
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

data class ConversationTurn(
    val prompt: String,
    val translation: String,
    val options: List<String>,
    val correctIndex: Int,
)

data class ConversationScenario(
    val id: String,
    val title: String,
    val description: String,
    val turns: List<ConversationTurn>,
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
            localeTag = "es-ES",
            nativeLabel = "español",
        ),
        LanguageCourse(
            id = "french",
            name = "French",
            flag = "\uD83C\uDDEB\uD83C\uDDF7",
            progress = 42,
            level = "Beginner",
            accent = VistaBlue,
            recentLessons = listOf("Basic Greetings", "Numbers 1-20", "Colors and Shapes"),
            localeTag = "fr-FR",
            nativeLabel = "français",
        ),
        LanguageCourse(
            id = "japanese",
            name = "Japanese",
            flag = "\uD83C\uDDEF\uD83C\uDDF5",
            progress = 23,
            level = "Beginner",
            accent = VistaGreen,
            recentLessons = listOf("Hiragana Practice", "Basic Introductions", "Common Phrases"),
            localeTag = "ja-JP",
            nativeLabel = "日本語",
        ),
    )

    val optionalCourses: List<LanguageCourse>
        get() = LanguageCatalog.optionalCourses

    val allCourses: List<LanguageCourse>
        get() = courses + optionalCourses

    fun visibleCourses(selectedIds: Set<String>): List<LanguageCourse> {
        val selected = allCourses.filter { it.id in selectedIds }
        return selected.ifEmpty { courses }
    }

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
        val listeningPhrases: List<GrammarExample>,
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
            listeningPhrases = listOf(
                GrammarExample("Bonjour, je voudrais un café.", "Hello, I would like a coffee."),
                GrammarExample("Où est la gare, s’il vous plaît ?", "Where is the train station, please?"),
                GrammarExample("J’ai passé une bonne journée.", "I had a good day."),
                GrammarExample("Parlez-vous anglais ?", "Do you speak English?"),
                GrammarExample("Merci beaucoup !", "Thank you very much!"),
            ),
            writingPrompt = "Describe your ideal vacation in French (40 words minimum)",
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
            listeningPhrases = listOf(
                GrammarExample("こんにちは。お元気ですか。", "Hello. How are you?"),
                GrammarExample("駅はどこですか。", "Where is the station?"),
                GrammarExample("コーヒーをお願いします。", "Coffee, please."),
                GrammarExample("わたしは日本語を勉強しています。", "I am studying Japanese."),
                GrammarExample("ありがとうございます。", "Thank you."),
            ),
            writingPrompt = "Describe your ideal vacation in Japanese (40 words minimum)",
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
        "spanish" -> spanishActivityPack()
        else -> LanguageCatalog.activityPack(courseId) ?: spanishActivityPack()
    }

    private fun spanishActivityPack() = ActivityPack(
        vocabulary = vocabularyBuilder,
        nativeLabel = "español",
        listeningTitle = "Spanish News Podcast",
        listeningDescription = "Listen to current events in Spanish",
        listeningPhrases = listOf(
            GrammarExample("Buenos días, ¿cómo está usted?", "Good morning, how are you?"),
            GrammarExample("¿Dónde está la estación?", "Where is the station?"),
            GrammarExample("Me gustaría un café, por favor.", "I would like a coffee, please."),
            GrammarExample("Ayer fui al cine con mis amigos.", "Yesterday I went to the movies with my friends."),
            GrammarExample("Muchas gracias por su ayuda.", "Thank you very much for your help."),
        ),
        writingPrompt = "Describe your ideal vacation in Spanish (40 words minimum)",
        writingPlaceholder = "Escribe aquí...",
        writingSuccess = "¡Bien hecho! Your tutor will review this entry.",
        tutorGreeting = "¡Hola! I'm your AI Spanish tutor. How can I help you today?",
        tutorPlaceholder = "Ask me anything about Spanish...",
        tutorReplies = spanishTutorReplies,
        conversationNote = "Practice these scenarios in Spanish",
    )

    fun conversationScenarios(courseId: String): List<ConversationScenario> = when (courseId) {
        "french" -> listOf(
            ConversationScenario(
                id = "restaurant",
                title = "Restaurant Conversation",
                description = "Practice ordering food and drinks in French",
                turns = listOf(
                    ConversationTurn(
                        "Bonjour ! Que désirez-vous ?",
                        "Hello! What would you like?",
                        listOf("Je voudrais un café.", "Je suis fatigué.", "Où est la gare ?"),
                        0,
                    ),
                    ConversationTurn(
                        "Et pour manger ?",
                        "And to eat?",
                        listOf("Un sandwich, s’il vous plaît.", "Bonsoir.", "Merci, au revoir."),
                        0,
                    ),
                    ConversationTurn(
                        "Ce sera tout ?",
                        "Will that be all?",
                        listOf("Oui, l’addition s’il vous plaît.", "Je parle anglais.", "Deux plus deux."),
                        0,
                    ),
                ),
            ),
            ConversationScenario(
                id = "travel",
                title = "Travel Scenarios",
                description = "Airport, hotel, and transportation in French",
                turns = listOf(
                    ConversationTurn(
                        "Où allez-vous ?",
                        "Where are you going?",
                        listOf("À l’hôtel, s’il vous plaît.", "J’aime le fromage.", "Bonne nuit."),
                        0,
                    ),
                    ConversationTurn(
                        "Avez-vous une réservation ?",
                        "Do you have a reservation?",
                        listOf("Oui, au nom de Dupont.", "Je voudrais du pain.", "C’est rouge."),
                        0,
                    ),
                    ConversationTurn(
                        "Voici votre clé.",
                        "Here is your key.",
                        listOf("Merci beaucoup !", "Où est mon chien ?", "Trois cafés."),
                        0,
                    ),
                ),
            ),
            ConversationScenario(
                id = "small-talk",
                title = "Small Talk",
                description = "Weather, weekends, and introductions in French",
                turns = listOf(
                    ConversationTurn(
                        "Comment allez-vous ?",
                        "How are you?",
                        listOf("Je vais bien, merci.", "Un billet pour Paris.", "L’addition."),
                        0,
                    ),
                    ConversationTurn(
                        "Que faites-vous ce week-end ?",
                        "What are you doing this weekend?",
                        listOf("Je vais au cinéma.", "Je voudrais du thé.", "Où est la banque ?"),
                        0,
                    ),
                    ConversationTurn(
                        "Quel temps fait-il ?",
                        "How’s the weather?",
                        listOf("Il fait beau aujourd’hui.", "Deux chambres.", "Le menu, s’il vous plaît."),
                        0,
                    ),
                ),
            ),
        )
        "japanese" -> listOf(
            ConversationScenario(
                id = "restaurant",
                title = "Restaurant Conversation",
                description = "Practice ordering food and drinks in Japanese",
                turns = listOf(
                    ConversationTurn(
                        "いらっしゃいませ。ご注文は？",
                        "Welcome. What would you like to order?",
                        listOf("コーヒーをお願いします。", "駅はどこですか。", "おはようございます。"),
                        0,
                    ),
                    ConversationTurn(
                        "お食事はいかがですか？",
                        "How about a meal?",
                        listOf("ラーメンをお願いします。", "ありがとう。", "さようなら。"),
                        0,
                    ),
                    ConversationTurn(
                        "以上でよろしいですか？",
                        "Is that all?",
                        listOf("はい、お会計をお願いします。", "本を読みます。", "犬が好きです。"),
                        0,
                    ),
                ),
            ),
            ConversationScenario(
                id = "travel",
                title = "Travel Scenarios",
                description = "Airport, hotel, and transportation in Japanese",
                turns = listOf(
                    ConversationTurn(
                        "どちらへ行きますか？",
                        "Where are you going?",
                        listOf("ホテルへ行きます。", "水をください。", "おはよう。"),
                        0,
                    ),
                    ConversationTurn(
                        "ご予約はありますか？",
                        "Do you have a reservation?",
                        listOf("はい、田中です。", "猫がいます。", "赤です。"),
                        0,
                    ),
                    ConversationTurn(
                        "これが鍵です。",
                        "Here is the key.",
                        listOf("ありがとうございます。", "映画を見ます。", "三です。"),
                        0,
                    ),
                ),
            ),
            ConversationScenario(
                id = "small-talk",
                title = "Small Talk",
                description = "Weather, weekends, and introductions in Japanese",
                turns = listOf(
                    ConversationTurn(
                        "お元気ですか？",
                        "How are you?",
                        listOf("元気です。", "コーヒーです。", "駅です。"),
                        0,
                    ),
                    ConversationTurn(
                        "今週末は何をしますか？",
                        "What will you do this weekend?",
                        listOf("映画を見ます。", "お会計を。", "水です。"),
                        0,
                    ),
                    ConversationTurn(
                        "天気はどうですか？",
                        "How’s the weather?",
                        listOf("いい天気です。", "ラーメンを。", "鍵です。"),
                        0,
                    ),
                ),
            ),
        )
        "spanish" -> listOf(
            ConversationScenario(
                id = "restaurant",
                title = "Restaurant Conversation",
                description = "Practice ordering food and drinks in Spanish",
                turns = listOf(
                    ConversationTurn(
                        "¡Buenas tardes! ¿Qué desea?",
                        "Good afternoon! What would you like?",
                        listOf("Quisiera un café, por favor.", "Estoy cansado.", "¿Dónde está la estación?"),
                        0,
                    ),
                    ConversationTurn(
                        "¿Y para comer?",
                        "And to eat?",
                        listOf("Un bocadillo, por favor.", "Buenas noches.", "Gracias, adiós."),
                        0,
                    ),
                    ConversationTurn(
                        "¿Algo más?",
                        "Anything else?",
                        listOf("La cuenta, por favor.", "Hablo inglés.", "Dos más dos."),
                        0,
                    ),
                ),
            ),
            ConversationScenario(
                id = "travel",
                title = "Travel Scenarios",
                description = "Airport, hotel, and transportation in Spanish",
                turns = listOf(
                    ConversationTurn(
                        "¿Adónde va?",
                        "Where are you going?",
                        listOf("Al hotel, por favor.", "Me gusta el queso.", "Buenas noches."),
                        0,
                    ),
                    ConversationTurn(
                        "¿Tiene una reserva?",
                        "Do you have a reservation?",
                        listOf("Sí, a nombre de García.", "Quiero pan.", "Es rojo."),
                        0,
                    ),
                    ConversationTurn(
                        "Aquí tiene su llave.",
                        "Here is your key.",
                        listOf("¡Muchas gracias!", "¿Dónde está mi perro?", "Tres cafés."),
                        0,
                    ),
                ),
            ),
            ConversationScenario(
                id = "small-talk",
                title = "Small Talk",
                description = "Weather, weekends, and introductions in Spanish",
                turns = listOf(
                    ConversationTurn(
                        "¿Cómo estás?",
                        "How are you?",
                        listOf("Estoy bien, gracias.", "Un billete a Madrid.", "La cuenta."),
                        0,
                    ),
                    ConversationTurn(
                        "¿Qué haces este fin de semana?",
                        "What are you doing this weekend?",
                        listOf("Voy al cine.", "Quiero té.", "¿Dónde está el banco?"),
                        0,
                    ),
                    ConversationTurn(
                        "¿Qué tiempo hace?",
                        "How’s the weather?",
                        listOf("Hace buen tiempo hoy.", "Dos habitaciones.", "El menú, por favor."),
                        0,
                    ),
                ),
            ),
        )
        else -> genericConversationScenarios(courseId)
    }

    private fun genericConversationScenarios(courseId: String): List<ConversationScenario> {
        val course = courseById(courseId)
        return listOf(
            ConversationScenario(
                id = "restaurant",
                title = "Restaurant Conversation",
                description = "Practice ordering food and drinks in ${course.name}",
                turns = emptyList(),
            ),
            ConversationScenario(
                id = "travel",
                title = "Travel Scenarios",
                description = "Airport, hotel, and transportation in ${course.name}",
                turns = emptyList(),
            ),
            ConversationScenario(
                id = "small-talk",
                title = "Small Talk",
                description = "Weather, weekends, and introductions in ${course.name}",
                turns = emptyList(),
            ),
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
        "spanish" -> DailyGrammarLesson(
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
        else -> LanguageCatalog.dailyGrammarLesson(courseId) ?: dailyGrammarLesson("spanish")
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
            "spanish" -> listOf(
                GrammarExample("Hola, ¿cómo estás?", "Hello, how are you?"),
                GrammarExample("Ayer fui al cine con mis amigos.", "Yesterday I went to the movies with my friends."),
                GrammarExample("Me gustaría un café, por favor.", "I would like a coffee, please."),
            )
            else -> LanguageCatalog.pronunciationPhrases(courseId) ?: listOf(
                GrammarExample("Hola, ¿cómo estás?", "Hello, how are you?"),
                GrammarExample("Ayer fui al cine con mis amigos.", "Yesterday I went to the movies with my friends."),
                GrammarExample("Me gustaría un café, por favor.", "I would like a coffee, please."),
            )
        }
        return PracticePack(
            reviewWords = activity.vocabulary.take(6),
            grammarQuestions = drillQuestions(courseId, lesson.questions),
            pronunciationPhrases = phrases,
            localeTag = when (courseId) {
                "french" -> "fr-FR"
                "japanese" -> "ja-JP"
                "spanish" -> "es-ES"
                else -> LanguageCatalog.localeTag(courseId) ?: courseById(courseId).localeTag
            },
        )
    }

    private fun drillQuestions(
        courseId: String,
        lessonQuestions: List<ExerciseQuestion>,
    ): List<ExerciseQuestion> {
        val extras = when (courseId) {
            "french" -> listOf(
                ExerciseQuestion(
                    "Choose the correct article: ___ pomme",
                    listOf("une", "un", "des"),
                    0,
                    "Pomme is feminine singular, so une.",
                ),
                ExerciseQuestion(
                    "Complete: Nous ___ français.",
                    listOf("parlons", "parlez", "parle"),
                    0,
                    "Nous takes the -ons ending: parlons.",
                ),
            )
            "japanese" -> listOf(
                ExerciseQuestion(
                    "Which particle marks the topic?",
                    listOf("は (wa)", "を (o)", "に (ni)"),
                    0,
                    "は marks the topic of the sentence.",
                ),
                ExerciseQuestion(
                    "Complete: 水___飲みます。",
                    listOf("を", "は", "が"),
                    0,
                    "を marks the direct object.",
                ),
            )
            "spanish" -> listOf(
                ExerciseQuestion(
                    "Choose the correct article: ___ casa",
                    listOf("la", "el", "los"),
                    0,
                    "Casa is feminine singular, so la.",
                ),
                ExerciseQuestion(
                    "Complete: Nosotros ___ español.",
                    listOf("hablamos", "hablan", "hablo"),
                    0,
                    "Nosotros takes -amos in the present: hablamos.",
                ),
            )
            else -> emptyList()
        }
        return (extras + lessonQuestions).distinctBy { it.prompt }
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
            "spanish" -> listOf(
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
            else -> LanguageCatalog.dictionaryWords(courseId) ?: listOf(
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
        allCourses.firstOrNull { it.id == id } ?: courses.first()

    fun readingOf(courseId: String, text: String): String? = LanguageCatalog.readingOf(courseId, text)

    fun readingLabel(courseId: String): String? = LanguageCatalog.readingLabel(courseId)

    fun courseLessons(courseId: String): List<CourseLesson> {
        val grammar = dailyGrammarLesson(courseId)
        val course = courseById(courseId)
        val extras = when (courseId) {
            "french" -> listOf(
                CourseLesson("fr-greetings", 1, "Basic Greetings", "Say hello and introduce yourself politely", "Beginner", false),
                CourseLesson("fr-numbers", 2, "Numbers 1–20", "Count and use numbers in everyday phrases", "Beginner", false),
                CourseLesson("fr-colors", 3, "Colors and Shapes", "Describe objects with common adjectives", "Beginner", false),
            )
            "japanese" -> listOf(
                CourseLesson("jp-hiragana", 1, "Hiragana Practice", "Read and write basic hiragana characters", "Beginner", false),
                CourseLesson("jp-intro", 2, "Basic Introductions", "Introduce yourself with です sentences", "Beginner", false),
                CourseLesson("jp-phrases", 3, "Common Phrases", "Useful everyday Japanese expressions", "Beginner", false),
            )
            "spanish" -> listOf(
                CourseLesson("es-everyday", 1, "Everyday Conversations", "Practice natural Spanish chat for daily life", "Intermediate", false),
                CourseLesson("es-restaurant", 2, "Restaurant Vocabulary", "Order food and drinks with confidence", "Intermediate", false),
                CourseLesson("es-travel", 3, "Travel Phrases", "Navigate airports, hotels, and transport", "Intermediate", false),
            )
            else -> listOf(
                CourseLesson("${courseId}-greetings", 1, "Basic Greetings", "Say hello and introduce yourself politely", "Beginner", false),
                CourseLesson("${courseId}-numbers", 2, "Numbers 1–10", "Count and use numbers in everyday phrases", "Beginner", false),
                CourseLesson("${courseId}-phrases", 3, "Everyday Phrases", "Useful everyday expressions", "Beginner", false),
            )
        }
        return listOf(
            CourseLesson(
                id = "grammar-${course.id}",
                number = grammar.number,
                title = grammar.title,
                subtitle = grammar.subtitle,
                level = grammar.level,
                opensGrammarLesson = true,
            ),
        ) + extras
    }

    data class SocialFeed(
        val follows: List<SuggestedFollow>,
        val posts: List<SocialPost>,
    )

    fun socialFeed(courseId: String): SocialFeed = when (courseId) {
        "french" -> SocialFeed(
            follows = listOf(
                SuggestedFollow("@claire_paris", "Native Speaker"),
                SuggestedFollow("@french_daily", "Learning Tips"),
                SuggestedFollow("@cafe_chat", "Language Exchange"),
            ),
            posts = listOf(
                SocialPost(
                    id = 101,
                    author = "@claire_paris",
                    body = "\"Mot du jour: 'flâner' — to stroll without a goal. Perfect for Paris weekends.\"",
                    likes = 31,
                    comments = listOf(
                        PostComment("@learner_sam", "I love this word! Merci Claire."),
                        PostComment("@bonjour_ben", "Adding this to my notebook."),
                    ),
                ),
                SocialPost(
                    id = 102,
                    author = "@french_daily",
                    body = "\"Tip: listen to French podcasts at 0.8x speed, then bump to normal.\"",
                    likes = 48,
                    comments = listOf(
                        PostComment("@polyglot_mia", "This helped my listening a lot!"),
                    ),
                ),
            ),
        )
        "japanese" -> SocialFeed(
            follows = listOf(
                SuggestedFollow("@yuki_tokyo", "Native Speaker"),
                SuggestedFollow("@nihongo_tips", "Learning Tips"),
                SuggestedFollow("@kanji_club", "Study Group"),
            ),
            posts = listOf(
                SocialPost(
                    id = 201,
                    author = "@yuki_tokyo",
                    body = "\"今日の単語: がんばる — to do your best. みんな、がんばって！\"",
                    likes = 56,
                    comments = listOf(
                        PostComment("@hiro_learner", "Timing is perfect before my quiz."),
                        PostComment("@sakura_san", "Short and useful. Thanks!"),
                    ),
                ),
                SocialPost(
                    id = 202,
                    author = "@nihongo_tips",
                    body = "\"Practice tip: shadow anime lines with Japanese subtitles, then without.\"",
                    likes = 39,
                    comments = listOf(
                        PostComment("@anime_alex", "Doing this with Shirokuma Cafe."),
                    ),
                ),
            ),
        )
        else -> SocialFeed(
            follows = suggestedFollows,
            posts = posts,
        )
    }

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

    /** Canned replies for the AI tutor so the screen is usable without a backend. */
    val spanishTutorReplies = listOf(
        "¡Buena pregunta! In Spanish, 'ser' describes permanent traits and 'estar' describes states or locations.",
        "Try saying it out loud: \"Fui al cine con mis amigos.\" The preterite 'fui' works for both 'ir' and 'ser'.",
        "Un consejo: learn verbs in chunks, like 'me gustaría' (I would like), instead of memorising full tables.",
        "¡Muy bien! Want me to quiz you on the vocabulary from Lesson 12?",
    )
}
