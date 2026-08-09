package com.lingualearn.pro.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.lingualearn.pro.data.CloudWordRepository
import com.lingualearn.pro.data.DictionaryLookupRepository
import com.lingualearn.pro.data.ExerciseQuestion
import com.lingualearn.pro.data.LanguageCourse
import com.lingualearn.pro.data.SampleContent
import com.lingualearn.pro.data.SavedWord
import com.lingualearn.pro.data.SavedWordRepository
import com.lingualearn.pro.data.VocabWord
import com.lingualearn.pro.ui.components.AeroButton
import com.lingualearn.pro.ui.components.Badge
import com.lingualearn.pro.ui.components.BodyText
import com.lingualearn.pro.ui.components.CardTitle
import com.lingualearn.pro.ui.components.GlassCard
import com.lingualearn.pro.ui.components.GlassTextField
import com.lingualearn.pro.ui.components.GlassTile
import com.lingualearn.pro.ui.theme.GlassTileStrong
import com.lingualearn.pro.ui.theme.TextMuted
import com.lingualearn.pro.ui.theme.TextPrimary
import com.lingualearn.pro.ui.theme.VistaGreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.max

@Composable
fun DailyGrammarLessonScreen(course: LanguageCourse, onStart: () -> Unit) {
    val lesson = SampleContent.dailyGrammarLesson(course.id)
    GlassCard(Modifier.fillMaxWidth()) {
        Column(
            Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                Modifier.size(80.dp).background(course.accent.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.School, null, tint = Color.White, modifier = Modifier.size(36.dp))
            }
            Badge("Lesson ${lesson.number} · ${lesson.level}", course.accent.copy(alpha = 0.7f))
            CardTitle(lesson.title)
            BodyText(lesson.subtitle)
            GlassTile(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Today’s grammar focus", color = course.accent, fontWeight = FontWeight.Bold)
                    BodyText(lesson.concept)
                    BodyText("${lesson.examples.size} examples · ${lesson.questions.size} questions", color = TextMuted)
                }
            }
            AeroButton("Start Lesson", onClick = onStart, color = course.accent)
        }
    }
}

@Composable
fun GrammarLessonScreen(course: LanguageCourse, onComplete: () -> Unit) {
    val lesson = SampleContent.dailyGrammarLesson(course.id)
    GlassCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Badge("Lesson ${lesson.number} · ${lesson.level}", course.accent.copy(alpha = 0.7f))
            CardTitle(lesson.title)
            BodyText(lesson.concept)
            GlassTile(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text("Grammar rule", color = course.accent, fontWeight = FontWeight.Bold)
                    BodyText(lesson.rule)
                }
            }
            lesson.examples.forEach {
                GlassTile(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        Text(it.phrase, color = TextPrimary, fontWeight = FontWeight.Bold)
                        BodyText(it.translation, color = TextMuted)
                    }
                }
            }
        }
    }
    QuizCard(course, lesson.questions, "Lesson complete") { _, _ -> onComplete() }
}

@Composable
fun PracticeHubScreen(
    onQuickReview: () -> Unit,
    onGrammarDrills: () -> Unit,
    onPronunciationLab: () -> Unit,
    onDictationNotebook: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        PracticeEntry("Quick Review", "Practice words you've learned recently", "Start Review", Color(0xFF2ABBC4), onQuickReview)
        PracticeEntry("Grammar Drills", "Focus on verb conjugations and sentence structure", "Start Drills", Color(0xFF2563EB), onGrammarDrills)
        PracticeEntry("Pronunciation Lab", "Repeat after the speaker and compare your accent", "Start Lab", VistaGreen, onPronunciationLab)
        PracticeEntry("Dictation Notebook", "Hear a word, write it down, look up its meaning, and save it", "Open Notebook", Color(0xFFFF6B1A), onDictationNotebook)
    }
}

@Composable
private fun PracticeEntry(title: String, subtitle: String, button: String, color: Color, onClick: () -> Unit) {
    GlassCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            CardTitle(title)
            BodyText(subtitle)
            AeroButton(button, onClick = onClick, color = color)
        }
    }
}

@Composable
fun QuickReviewScreen(course: LanguageCourse, onBack: () -> Unit, onComplete: (Int, Int) -> Unit) {
    val context = LocalContext.current
    val base = SampleContent.practicePack(course.id).reviewWords
    var saved by remember(course.id) { mutableStateOf<List<SavedWord>>(emptyList()) }
    LaunchedEffect(course.id) { saved = SavedWordRepository.wordsForLanguage(context, course.id) }
    val words = remember(base, saved) {
        (saved.map { VocabWord(it.word, it.meaning) } + base).distinctBy { it.term.lowercase() }
    }
    var index by rememberSaveable(course.id) { mutableStateOf(0) }
    var selected by rememberSaveable(course.id, index) { mutableStateOf<String?>(null) }
    var checked by rememberSaveable(course.id, index) { mutableStateOf(false) }
    var score by rememberSaveable(course.id) { mutableStateOf(0) }
    var finished by rememberSaveable(course.id) { mutableStateOf(false) }

    ExerciseHeader("Quick Review", "Practice words you've learned recently", onBack, true)
    GlassCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (finished) {
                ResultSummary("Review complete", score, words.size, onBack)
            } else {
                val word = words[index.coerceAtMost(words.lastIndex)]
                val options = remember(index, words) {
                    listOf(word.term, words[(index + 1) % words.size].term, words[(index + 2) % words.size].term)
                        .distinct().sorted()
                }
                Badge("${index + 1}/${words.size}", course.accent.copy(alpha = 0.65f))
                BodyText("Which word means…", color = TextMuted)
                CardTitle(word.meaning)
                options.forEach { option ->
                    AnswerOption(
                        option,
                        option == selected,
                        checked && option == word.term,
                        checked && option == selected && option != word.term,
                        course.accent,
                    ) { if (!checked) selected = option }
                }
                if (checked) BodyText(if (selected == word.term) "Correct!" else "Answer: ${word.term}")
                AeroButton(
                    if (!checked) "Check answer" else if (index == words.lastIndex) "See results" else "Next word",
                    color = course.accent,
                    onClick = {
                        when {
                            !checked && selected != null -> {
                                checked = true
                                if (selected == word.term) score++
                            }
                            checked && index == words.lastIndex -> {
                                finished = true
                                onComplete(score, words.size)
                            }
                            checked -> {
                                index++
                                selected = null
                                checked = false
                            }
                        }
                    },
                )
            }
        }
    }
}

@Composable
fun GrammarDrillsScreen(
    course: LanguageCourse,
    onBack: () -> Unit,
    onComplete: (Int, Int) -> Unit,
) {
    ExerciseHeader("Grammar Drills", "Focus on verb conjugations and sentence structure", onBack)
    QuizCard(course, SampleContent.practicePack(course.id).grammarQuestions, "Drills complete") { score, total ->
        onComplete(score, total)
    }
}

@Composable
private fun QuizCard(
    course: LanguageCourse,
    questions: List<ExerciseQuestion>,
    resultTitle: String,
    onComplete: (Int, Int) -> Unit,
) {
    var index by rememberSaveable(course.id, resultTitle) { mutableStateOf(0) }
    var selected by rememberSaveable(course.id, resultTitle, index) { mutableStateOf<Int?>(null) }
    var checked by rememberSaveable(course.id, resultTitle, index) { mutableStateOf(false) }
    var score by rememberSaveable(course.id, resultTitle) { mutableStateOf(0) }
    var finished by rememberSaveable(course.id, resultTitle) { mutableStateOf(false) }
    GlassCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (finished) {
                ResultSummary(resultTitle, score, questions.size) { onComplete(score, questions.size) }
            } else {
                val question = questions[index]
                Badge("${index + 1}/${questions.size}", course.accent.copy(alpha = 0.65f))
                CardTitle(question.prompt)
                question.options.forEachIndexed { optionIndex, option ->
                    AnswerOption(
                        option,
                        selected == optionIndex,
                        checked && question.correctIndex == optionIndex,
                        checked && selected == optionIndex && question.correctIndex != optionIndex,
                        course.accent,
                    ) { if (!checked) selected = optionIndex }
                }
                if (checked) BodyText(question.explanation)
                AeroButton(
                    if (!checked) "Check answer" else if (index == questions.lastIndex) "Finish" else "Next question",
                    color = course.accent,
                    onClick = {
                        when {
                            !checked && selected != null -> {
                                checked = true
                                if (selected == question.correctIndex) score++
                            }
                            checked && index == questions.lastIndex -> finished = true
                            checked -> {
                                index++
                                selected = null
                                checked = false
                            }
                        }
                    },
                )
            }
        }
    }
}

@Composable
fun PronunciationLabScreen(course: LanguageCourse, onBack: () -> Unit) {
    val context = LocalContext.current
    val pack = SampleContent.practicePack(course.id)
    val phrases = pack.pronunciationPhrases
    var index by rememberSaveable(course.id) { mutableStateOf(0) }
    var heard by rememberSaveable(course.id, index) { mutableStateOf("") }
    var feedback by rememberSaveable(course.id, index) { mutableStateOf("Tap Listen, then repeat the phrase.") }
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    DisposableEffect(context, pack.localeTag) {
        lateinit var engine: TextToSpeech
        engine = TextToSpeech(context) {
            if (it == TextToSpeech.SUCCESS) {
                engine.language = Locale.forLanguageTag(pack.localeTag)
                tts = engine
            }
        }
        onDispose { engine.shutdown() }
    }
    val speechLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val text = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
        if (text == null) feedback = "I couldn't hear that. Try again."
        else {
            heard = text
            val score = pronunciationScore(phrases[index].phrase, text)
            feedback = if (score >= 80) "Excellent! $score% match." else "Good attempt — $score% match. Try again."
        }
    }
    fun listen() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, pack.localeTag)
        }
        runCatching { speechLauncher.launch(intent) }.onFailure { feedback = "Speech recognition is unavailable." }
    }
    val permission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (it) listen() else feedback = "Microphone permission is required."
    }
    val phrase = phrases[index]
    ExerciseHeader("Pronunciation Lab", "Repeat after the speaker and compare your accent", onBack)
    GlassCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Badge("${index + 1}/${phrases.size}", course.accent.copy(alpha = 0.65f))
            Text(phrase.phrase, style = MaterialTheme.typography.headlineSmall, color = TextPrimary)
            BodyText(phrase.translation)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AeroButton(
                    text = "Listen",
                    onClick = { tts?.speak(phrase.phrase, TextToSpeech.QUEUE_FLUSH, null, "phrase-$index") },
                    color = course.accent,
                    leadingIcon = { Icon(Icons.AutoMirrored.Filled.VolumeUp, null) },
                )
                AeroButton(
                    text = "Speak",
                    onClick = {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                            listen()
                        } else permission.launch(Manifest.permission.RECORD_AUDIO)
                    },
                    color = VistaGreen,
                    leadingIcon = { Icon(Icons.Filled.Mic, null) },
                )
            }
            BodyText(feedback)
            if (heard.isNotBlank()) BodyText("I heard: “$heard”", color = TextMuted)
            AeroButton(
                text = if (index == phrases.lastIndex) "Start over" else "Next phrase",
                onClick = {
                    index = (index + 1) % phrases.size
                    heard = ""
                    feedback = "Tap Listen, then repeat the phrase."
                },
                color = course.accent,
            )
        }
    }
}

@Composable
fun DictationNotebookScreen(course: LanguageCourse, onBack: () -> Unit, onReview: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val pack = SampleContent.practicePack(course.id)
    val dictionary = SampleContent.dictionaryWords(course.id)
    var promptIndex by rememberSaveable(course.id) { mutableStateOf(0) }
    var word by rememberSaveable(course.id) { mutableStateOf("") }
    var meaning by rememberSaveable(course.id) { mutableStateOf("") }
    var example by rememberSaveable(course.id) { mutableStateOf("") }
    var message by rememberSaveable(course.id) { mutableStateOf("Tap Hear word, then write what you hear.") }
    var suggestions by remember(course.id) { mutableStateOf<List<VocabWord>>(emptyList()) }
    var saved by remember(course.id) { mutableStateOf<List<SavedWord>>(emptyList()) }
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    val prompt = pack.reviewWords[promptIndex]

    suspend fun refresh() { saved = SavedWordRepository.wordsForLanguage(context, course.id) }
    LaunchedEffect(course.id) { refresh() }
    LaunchedEffect(course.id, word) {
        val query = word.trim()
        suggestions = if (query.length < 2) emptyList() else {
            delay(250)
            val local = dictionary.filter { it.term.startsWith(query, true) }.take(5)
            if (local.isNotEmpty()) local else {
                DictionaryLookupRepository.lookup(query, pack.localeTag.substringBefore('-'))
                    ?.let { listOf(VocabWord(query, it)) }.orEmpty()
            }
        }
    }
    DisposableEffect(context, pack.localeTag) {
        lateinit var engine: TextToSpeech
        engine = TextToSpeech(context) {
            if (it == TextToSpeech.SUCCESS) {
                engine.language = Locale.forLanguageTag(pack.localeTag)
                tts = engine
            }
        }
        onDispose { engine.shutdown() }
    }

    ExerciseHeader("Dictation Notebook", "Hear a word, write it down, look up its meaning, and save it", onBack)
    GlassCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Badge("Word ${promptIndex + 1}/${pack.reviewWords.size}", course.accent.copy(alpha = 0.65f))
            CardTitle("Listen and capture")
            AeroButton(
                text = "Hear word",
                onClick = { tts?.speak(prompt.term, TextToSpeech.QUEUE_FLUSH, null, "dictation-$promptIndex") },
                color = course.accent,
                leadingIcon = { Icon(Icons.AutoMirrored.Filled.VolumeUp, null) },
            )
            Text("Word", color = TextPrimary, fontWeight = FontWeight.Bold)
            GlassTextField(word, { word = it }, "Write the word you heard", Modifier.fillMaxWidth())
            if (suggestions.isNotEmpty()) {
                GlassCard(Modifier.fillMaxWidth(), color = Color(0xE6335C72)) {
                    Column(Modifier.padding(6.dp)) {
                        BodyText("Dictionary suggestions", color = TextMuted)
                        suggestions.forEach { suggestion ->
                            GlassTile(Modifier.fillMaxWidth(), onClick = {
                                word = suggestion.term
                                meaning = suggestion.meaning
                                suggestions = emptyList()
                                message = "Added “${suggestion.term} — ${suggestion.meaning}”."
                            }) {
                                Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(suggestion.term, color = course.accent, fontWeight = FontWeight.Bold)
                                    BodyText("— ${suggestion.meaning}")
                                }
                            }
                        }
                    }
                }
            }
            AeroButton(
                text = "Look up meaning",
                onClick = {
                    val query = word.trim()
                    if (query.isEmpty()) {
                        message = "Write a word before looking it up."
                    } else {
                        scope.launch {
                            message = "Looking up “$query”…"
                            val localMeaning = dictionary
                                .firstOrNull { it.term.equals(query, ignoreCase = true) }
                                ?.meaning
                            val result = localMeaning ?: DictionaryLookupRepository.lookup(
                                query,
                                pack.localeTag.substringBefore('-'),
                            )
                            if (result != null) {
                                meaning = result
                                suggestions = emptyList()
                                message = "Found: $query — $result"
                            } else {
                                message = "No dictionary result found. You can enter the meaning yourself."
                            }
                        }
                    }
                },
                color = Color(0xFF526777),
            )
            Text("Meaning", color = TextPrimary, fontWeight = FontWeight.Bold)
            GlassTextField(meaning, { meaning = it }, "Enter the meaning", Modifier.fillMaxWidth())
            Text("Example sentence (optional)", color = TextPrimary, fontWeight = FontWeight.Bold)
            GlassTextField(example, { example = it }, "Use the word in a sentence", Modifier.fillMaxWidth(), false)
            BodyText(message, color = TextMuted)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AeroButton("Save word", color = VistaGreen, onClick = {
                    if (word.isBlank() || meaning.isBlank()) message = "Add the word and meaning first."
                    else scope.launch {
                        SavedWordRepository.save(context, course.id, word, meaning, example)
                        val cloudSaved = CloudWordRepository.isSignedIn && runCatching {
                            CloudWordRepository.saveWord(course.id, course.name, word, meaning, example, "dictation_notebook")
                        }.isSuccess
                        refresh()
                        message = if (cloudSaved) "Saved on this device and online." else {
                            "Saved on this device. Sign in from Google Tools to sync online."
                        }
                    }
                })
                AeroButton("Another word", color = course.accent, onClick = {
                    promptIndex = (promptIndex + 1) % pack.reviewWords.size
                    word = ""; meaning = ""; example = ""; suggestions = emptyList()
                })
            }
        }
    }
    GlassCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            CardTitle("Saved words · ${saved.size}")
            saved.take(5).forEach {
                GlassTile(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        Text(it.word, color = course.accent, fontWeight = FontWeight.Bold)
                        BodyText(it.meaning)
                        if (it.exampleSentence.isNotBlank()) BodyText(it.exampleSentence, color = TextMuted)
                    }
                }
            }
            if (saved.isNotEmpty()) AeroButton("Review saved words", onClick = onReview, color = course.accent)
        }
    }
}

@Composable
private fun ExerciseHeader(title: String, subtitle: String, onBack: () -> Unit, backInside: Boolean = false) {
    if (backInside) {
        GlassCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                CardTitle(title); BodyText(subtitle)
                BackButton(onBack)
            }
        }
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            BackButton(onBack)
            GlassCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) { CardTitle(title); BodyText(subtitle) }
            }
        }
    }
}

@Composable
private fun BackButton(onBack: () -> Unit) {
    AeroButton("Back to Practice", onClick = onBack, color = Color(0xFF526777), leadingIcon = {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
    })
}

@Composable
private fun AnswerOption(
    text: String,
    selected: Boolean,
    correct: Boolean,
    incorrect: Boolean,
    accent: Color,
    onClick: () -> Unit,
) {
    val color = when {
        correct -> VistaGreen.copy(alpha = 0.55f)
        incorrect -> Color(0x99B63A3A)
        selected -> accent.copy(alpha = 0.4f)
        else -> GlassTileStrong
    }
    GlassTile(Modifier.fillMaxWidth(), color = color, onClick = onClick) {
        Text(text, color = TextPrimary, modifier = Modifier.padding(14.dp))
    }
}

@Composable
private fun ResultSummary(title: String, score: Int, total: Int, onDone: () -> Unit) {
    CardTitle(title)
    Text("$score / $total", style = MaterialTheme.typography.headlineMedium, color = VistaGreen)
    BodyText(if (score == total) "Perfect score!" else "Nice work. Practice again to improve.")
    AeroButton("Continue", onClick = onDone, color = VistaGreen)
}

private fun pronunciationScore(target: String, spoken: String): Int {
    fun normalize(value: String) = value.lowercase().replace(Regex("[\\p{P}\\p{Z}\\s]"), "")
    val expected = normalize(target)
    val actual = normalize(spoken)
    if (expected.isEmpty() || actual.isEmpty()) return 0
    val row = IntArray(actual.length + 1) { it }
    for (i in expected.indices) {
        var diagonal = row[0]
        row[0] = i + 1
        for (j in actual.indices) {
            val above = row[j + 1]
            row[j + 1] = minOf(row[j + 1] + 1, row[j] + 1, diagonal + if (expected[i] == actual[j]) 0 else 1)
            diagonal = above
        }
    }
    return ((1f - row[actual.length].toFloat() / max(expected.length, actual.length)) * 100)
        .toInt().coerceIn(0, 100)
}
