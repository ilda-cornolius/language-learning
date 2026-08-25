package com.lingualearn.pro.ui.screens

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lingualearn.pro.data.AiTutorRepository
import com.lingualearn.pro.data.ConversationScenario
import com.lingualearn.pro.data.LanguageCourse
import com.lingualearn.pro.data.PreferencesStore
import com.lingualearn.pro.data.SampleContent
import com.lingualearn.pro.data.SavedWordRepository
import com.lingualearn.pro.data.SoundEffects
import com.lingualearn.pro.ui.components.AeroButton
import com.lingualearn.pro.ui.components.AeroProgressBar
import com.lingualearn.pro.ui.components.BodyText
import com.lingualearn.pro.ui.components.CardTitle
import com.lingualearn.pro.ui.components.GlassCard
import com.lingualearn.pro.ui.components.GlassTile
import com.lingualearn.pro.ui.components.GlassTextField
import com.lingualearn.pro.ui.components.PhraseWithReading
import com.lingualearn.pro.ui.theme.TextMuted
import com.lingualearn.pro.ui.theme.TextPrimary
import com.lingualearn.pro.ui.theme.VistaAccent
import com.lingualearn.pro.ui.theme.VistaBlue
import com.lingualearn.pro.ui.theme.VistaGreen
import com.lingualearn.pro.ui.theme.VistaTeal
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun VocabularyScreen(
    course: LanguageCourse,
    preferencesStore: PreferencesStore,
    onComplete: () -> Unit = {},
) {
    val context = LocalContext.current
    val pack = SampleContent.activityPack(course.id)
    val practice = SampleContent.practicePack(course.id)
    val scope = rememberCoroutineScope()
    var revealed by remember(course.id) { mutableStateOf(setOf<String>()) }
    var savedTerms by remember(course.id) { mutableStateOf(setOf<String>()) }
    var awarded by remember(course.id) { mutableStateOf(false) }
    var saveMessage by remember(course.id) { mutableStateOf("") }
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }

    DisposableEffect(context, practice.localeTag, preferencesStore.voiceSpeed) {
        lateinit var engine: TextToSpeech
        engine = TextToSpeech(context) {
            if (it == TextToSpeech.SUCCESS) {
                engine.language = Locale.forLanguageTag(practice.localeTag)
                engine.setSpeechRate(preferencesStore.speechRate())
                tts = engine
            }
        }
        onDispose {
            engine.stop()
            engine.shutdown()
        }
    }

    LaunchedEffect(revealed, pack.vocabulary) {
        if (!awarded && pack.vocabulary.isNotEmpty() && revealed.size >= pack.vocabulary.size) {
            awarded = true
            onComplete()
        }
    }

    GlassCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            BodyText(
                if (pack.vocabulary.any { !it.reading.isNullOrBlank() }) {
                    val label = SampleContent.readingLabel(course.id) ?: "reading"
                    "Hover or tap a word for $label. Tap the emoji or label to flip."
                } else {
                    "Tap a card to flip between ${course.name} and English."
                },
            )
            if (saveMessage.isNotBlank()) BodyText(saveMessage, color = VistaGreen)
            pack.vocabulary.chunked(2).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    row.forEach { word ->
                        val isRevealed = word.term in revealed
                        val isSaved = word.term in savedTerms
                        val hasReading = !word.reading.isNullOrBlank()
                        fun flipCard() {
                            revealed = if (isRevealed) revealed - word.term else revealed + word.term
                        }
                        GlassTile(
                            modifier = Modifier.weight(1f),
                            onClick = if (hasReading) null else ({ flipCard() }),
                        ) {
                            Column(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                Text(
                                    word.emoji ?: "",
                                    style = MaterialTheme.typography.headlineSmall,
                                    modifier = if (hasReading) Modifier.clickable { flipCard() } else Modifier,
                                )
                                if (isRevealed) {
                                    Text(
                                        text = word.meaning,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Medium,
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = if (hasReading) Modifier.clickable { flipCard() } else Modifier,
                                    )
                                } else {
                                    PhraseWithReading(
                                        phrase = word.term,
                                        reading = word.reading,
                                        phraseColor = course.accent,
                                        textAlign = TextAlign.Center,
                                    )
                                }
                                Text(
                                    text = if (isRevealed) "english" else pack.nativeLabel,
                                    color = TextMuted,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = if (hasReading) Modifier.clickable { flipCard() } else Modifier,
                                )
                                AeroButton(
                                    text = "Listen",
                                    color = course.accent,
                                    onClick = {
                                        tts?.setSpeechRate(preferencesStore.speechRate())
                                        tts?.speak(
                                            word.term,
                                            TextToSpeech.QUEUE_FLUSH,
                                            null,
                                            "vocab-${word.term}",
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.AutoMirrored.Filled.VolumeUp,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp),
                                        )
                                    },
                                )
                                AeroButton(
                                    text = if (isSaved) "Saved" else "Save",
                                    color = if (isSaved) VistaGreen else Color(0xFF526777),
                                    onClick = {
                                        if (isSaved) return@AeroButton
                                        scope.launch {
                                            SavedWordRepository.save(
                                                context,
                                                course.id,
                                                word.term,
                                                word.meaning,
                                                "",
                                            )
                                            savedTerms = savedTerms + word.term
                                            saveMessage = "Saved “${word.term}”"
                                            SoundEffects.playSuccess(context, preferencesStore)
                                        }
                                    },
                                )
                            }
                        }
                    }
                    if (row.size == 1) Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun ConversationScreen(
    course: LanguageCourse,
    onComplete: () -> Unit = {},
) {
    val pack = SampleContent.activityPack(course.id)
    val scenarios = remember(course.id) { SampleContent.conversationScenarios(course.id) }
    var active by remember(course.id) { mutableStateOf<ConversationScenario?>(null) }
    val messages = remember { mutableStateListOf<ChatMessage>() }
    var draft by remember(course.id) { mutableStateOf("") }
    var isReplying by remember(course.id) { mutableStateOf(false) }
    var awarded by remember(course.id) { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(messages.size, isReplying) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        BodyText("${course.flag}  ${pack.conversationNote} — an AI partner plays the other role.")
        val scenario = active
        if (scenario == null) {
            scenarios.forEach { item ->
                ScenarioCard(
                    title = item.title,
                    description = item.description,
                    tint = when (item.id) {
                        "restaurant" -> VistaGreen
                        "travel" -> VistaBlue
                        else -> VistaTeal
                    },
                    onStart = {
                        active = item
                        messages.clear()
                        draft = ""
                        awarded = false
                        isReplying = true
                        scope.launch {
                            val opening = runCatching {
                                AiTutorRepository.startRolePlay(course, item)
                            }.getOrElse {
                                Log.e("AiTutor", "Conversation start failed", it)
                                "${pack.tutorGreeting}\nLet's role-play: ${item.title}."
                            }
                            messages.add(ChatMessage(opening, fromUser = false))
                            isReplying = false
                        }
                    },
                )
            }
        } else {
            GlassCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    CardTitle(scenario.title)
                    BodyText("Reply in ${course.name} or English. The AI partner stays in the scene.", color = TextMuted)
                    GlassTile(
                        Modifier
                            .fillMaxWidth()
                            .heightIn(min = 220.dp, max = 340.dp),
                    ) {
                        Column(
                            Modifier
                                .padding(12.dp)
                                .verticalScroll(scrollState),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            messages.forEach { message ->
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = if (message.fromUser) Arrangement.End else Arrangement.Start,
                                ) {
                                    GlassTile(
                                        color = if (message.fromUser) course.accent.copy(alpha = 0.35f)
                                        else VistaTeal.copy(alpha = 0.3f),
                                        shape = RoundedCornerShape(12.dp),
                                    ) {
                                        Text(
                                            text = message.text,
                                            color = TextPrimary,
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier = Modifier.padding(12.dp),
                                        )
                                    }
                                }
                            }
                            if (isReplying) {
                                GlassTile(
                                    color = VistaTeal.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(12.dp),
                                ) {
                                    Text(
                                        text = "Your partner is speaking…",
                                        color = TextMuted,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(12.dp),
                                    )
                                }
                            }
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        GlassTextField(
                            value = draft,
                            onValueChange = { draft = it },
                            placeholder = "Type your reply…",
                            modifier = Modifier.weight(1f),
                        )
                        Spacer(Modifier.size(8.dp))
                        AeroButton(
                            text = if (isReplying) "…" else "Send",
                            color = course.accent,
                            onClick = {
                                if (draft.isBlank() || isReplying) return@AeroButton
                                val reply = draft.trim()
                                val history = messages.map { it.fromUser to it.text }
                                messages.add(ChatMessage(reply, fromUser = true))
                                draft = ""
                                isReplying = true
                                scope.launch {
                                    val response = runCatching {
                                        AiTutorRepository.rolePlay(
                                            course = course,
                                            scenario = scenario,
                                            conversation = history,
                                            userMessage = reply,
                                        )
                                    }.getOrElse {
                                        Log.e("AiTutor", "Conversation reply failed", it)
                                        "I couldn't reach the AI partner. Try again in a moment."
                                    }
                                    messages.add(ChatMessage(response, fromUser = false))
                                    isReplying = false
                                    if (!awarded) {
                                        awarded = true
                                        onComplete()
                                    }
                                }
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.AutoMirrored.Filled.Send,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .padding(end = 8.dp)
                                        .size(16.dp),
                                )
                            },
                        )
                    }
                    AeroButton(
                        text = "Back to scenarios",
                        color = Color(0xFF526777),
                        onClick = {
                            active = null
                            messages.clear()
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun ScenarioCard(
    title: String,
    description: String,
    tint: Color,
    onStart: () -> Unit,
) {
    GlassCard(Modifier.fillMaxWidth(), color = tint.copy(alpha = 0.25f)) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            CardTitle(title)
            BodyText(description)
            AeroButton(text = "Start", color = tint, onClick = onStart)
        }
    }
}

@Composable
fun ListeningScreen(
    course: LanguageCourse,
    preferencesStore: PreferencesStore,
    onComplete: () -> Unit = {},
) {
    val context = LocalContext.current
    val pack = SampleContent.activityPack(course.id)
    val practice = SampleContent.practicePack(course.id)
    val phrases = remember(course.id) {
        pack.listeningPhrases.ifEmpty { practice.pronunciationPhrases }.take(5)
    }
    var playing by remember(course.id) { mutableStateOf(false) }
    var phraseIndex by remember(course.id) { mutableIntStateOf(0) }
    var finished by remember(course.id) { mutableStateOf(false) }
    var revealed by remember(course.id) { mutableStateOf(false) }
    var forceShowTranslation by remember(course.id) { mutableStateOf(false) }
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    val mainHandler = remember { Handler(Looper.getMainLooper()) }
    val transition = rememberInfiniteTransition(label = "pulse")
    val pulse by transition.animateFloat(
        initialValue = 1f,
        targetValue = if (playing) 1.08f else 1f,
        animationSpec = infiniteRepeatable(tween(900, easing = LinearEasing), RepeatMode.Reverse),
        label = "pulseScale",
    )
    val progress = if (phrases.isEmpty()) 0f else phraseIndex / phrases.size.toFloat()
    val currentPhrase = phrases.getOrNull(phraseIndex.coerceAtMost(phrases.lastIndex.coerceAtLeast(0)))
    val showTranscript = forceShowTranslation || (!playing && (revealed || finished))

    fun speakFrom(index: Int) {
        val engine = tts ?: return
        if (index >= phrases.size) {
            playing = false
            finished = true
            revealed = true
            onComplete()
            return
        }
        phraseIndex = index
        playing = true
        finished = false
        revealed = false
        forceShowTranslation = false
        engine.setSpeechRate(preferencesStore.speechRate())
        val params = Bundle()
        engine.speak(phrases[index].phrase, TextToSpeech.QUEUE_FLUSH, params, "listen-$index")
    }

    DisposableEffect(context, practice.localeTag, preferencesStore.voiceSpeed) {
        lateinit var engine: TextToSpeech
        engine = TextToSpeech(context) {
            if (it == TextToSpeech.SUCCESS) {
                engine.language = Locale.forLanguageTag(practice.localeTag)
                engine.setSpeechRate(preferencesStore.speechRate())
                engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) = Unit
                    override fun onError(utteranceId: String?) {
                        mainHandler.post {
                            playing = false
                            revealed = true
                        }
                    }
                    override fun onDone(utteranceId: String?) {
                        mainHandler.post {
                            if (!playing) return@post
                            revealed = true
                            val next = phraseIndex + 1
                            if (next >= phrases.size) {
                                playing = false
                                finished = true
                                phraseIndex = phrases.size
                                onComplete()
                            } else {
                                // Brief pause so the learner can read before the next clip.
                                playing = false
                            }
                        }
                    }
                })
                tts = engine
            }
        }
        onDispose {
            engine.stop()
            engine.shutdown()
        }
    }

    GlassCard(Modifier.fillMaxWidth()) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                Modifier
                    .size(128.dp)
                    .scale(pulse)
                    .background(course.accent.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = if (playing) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = if (playing) "Pause" else "Play",
                    tint = Color.White,
                    modifier = Modifier.size(52.dp),
                )
            }
            CardTitle(pack.listeningTitle)
            BodyText(pack.listeningDescription)
            when {
                showTranscript && currentPhrase != null && !finished -> {
                    PhraseWithReading(
                        phrase = currentPhrase.phrase,
                        reading = currentPhrase.reading,
                        phraseColor = TextPrimary,
                        textAlign = TextAlign.Center,
                    )
                    BodyText(text = currentPhrase.translation, color = TextMuted)
                }
                finished -> BodyText("Listening complete!", color = VistaGreen)
                playing -> BodyText("Listening…", color = TextMuted)
                else -> BodyText("Transcript hidden until the clip finishes.", color = TextMuted)
            }
            AeroProgressBar(progress = progress.coerceIn(0f, 1f), color = course.accent)
            BodyText(
                text = if (phrases.isEmpty()) "0 phrases" else "${phraseIndex.coerceAtMost(phrases.size)} / ${phrases.size}",
                color = TextMuted,
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    AeroButton(
                        text = when {
                            playing -> "Pause Listening"
                            finished -> "Play again"
                            revealed && !finished -> "Next phrase"
                            else -> "Start Listening"
                        },
                        color = course.accent,
                        onClick = {
                            when {
                                playing -> {
                                    playing = false
                                    revealed = true
                                    tts?.stop()
                                }
                                finished -> {
                                    phraseIndex = 0
                                    revealed = false
                                    forceShowTranslation = false
                                    speakFrom(0)
                                }
                                revealed && !finished -> {
                                    val next = phraseIndex + 1
                                    if (next >= phrases.size) {
                                        finished = true
                                        phraseIndex = phrases.size
                                        onComplete()
                                    } else {
                                        speakFrom(next)
                                    }
                                }
                                else -> speakFrom(phraseIndex.coerceAtMost(phrases.lastIndex))
                            }
                        },
                    )
                    if (phrases.isNotEmpty() && (playing || revealed || finished)) {
                        AeroButton(
                            text = "Replay",
                            color = Color(0xFF526777),
                            onClick = {
                                val index = phraseIndex.coerceIn(0, phrases.lastIndex)
                                speakFrom(index)
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Filled.Replay,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                )
                            },
                        )
                    }
                }
                if (!finished && currentPhrase != null) {
                    AeroButton(
                        text = if (forceShowTranslation || revealed) "Hide translation" else "Show translation",
                        color = Color(0xFF526777),
                        onClick = { forceShowTranslation = !forceShowTranslation },
                    )
                }
            }
        }
    }
}

@Composable
fun WritingScreen(
    course: LanguageCourse,
    preferencesStore: PreferencesStore? = null,
    onComplete: () -> Unit = {},
) {
    val context = LocalContext.current
    val prefs = preferencesStore ?: remember(context) { PreferencesStore(context.applicationContext) }
    val pack = SampleContent.activityPack(course.id)
    val scope = rememberCoroutineScope()
    var text by rememberSaveable(course.id) { mutableStateOf("") }
    var submitted by remember(course.id) { mutableStateOf(false) }
    var feedback by remember(course.id) { mutableStateOf("") }
    var submitting by remember(course.id) { mutableStateOf(false) }
    val isJapanese = course.id == "japanese"
    val count = if (isJapanese) {
        text.replace(Regex("\\s"), "").length
    } else {
        text.split(Regex("\\s+")).count { it.isNotBlank() }
    }
    val goal = if (isJapanese) 80 else 40
    val unitLabel = if (isJapanese) "characters" else "words"
    val meetsGoal = count >= goal

    GlassCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            CardTitle("Today's Writing Prompt")
            BodyText(
                if (isJapanese) {
                    pack.writingPrompt.replace("40 words minimum", "about 80 characters")
                } else {
                    pack.writingPrompt
                },
            )
            GlassTextField(
                value = text,
                onValueChange = {
                    text = it
                    submitted = false
                    feedback = ""
                },
                placeholder = pack.writingPlaceholder,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                singleLine = false,
            )
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = "$count / $goal $unitLabel",
                        color = if (meetsGoal) VistaGreen else TextMuted,
                        style = MaterialTheme.typography.bodySmall,
                    )
                    if (!meetsGoal) {
                        BodyText("Write at least $goal $unitLabel to submit.", color = TextMuted)
                    }
                }
                AeroButton(
                    text = when {
                        submitting -> "Reviewing…"
                        submitted -> "Submitted"
                        !meetsGoal -> "Need $goal $unitLabel"
                        else -> "Submit"
                    },
                    color = if (submitted) VistaGreen else if (!meetsGoal) Color(0xFF526777) else course.accent,
                    onClick = {
                        if (meetsGoal && !submitting && !submitted) {
                            submitting = true
                            scope.launch {
                                feedback = runCatching {
                                    AiTutorRepository.reply(
                                        course = course,
                                        conversation = emptyList(),
                                        userMessage = "Please give short writing feedback (2-3 sentences) on this ${course.name} draft:\n\n$text",
                                    )
                                }.getOrElse {
                                    Log.e("AiTutor", "Writing feedback failed", it)
                                    "${pack.writingSuccess} Tip: keep practicing everyday phrases and check verb endings."
                                }
                                submitted = true
                                submitting = false
                                SoundEffects.playSuccess(context, prefs)
                                onComplete()
                            }
                        }
                    },
                )
            }
            if (submitted && feedback.isNotBlank()) {
                BodyText(feedback, color = VistaGreen)
            }
        }
    }
}

@Composable
fun AssistantScreen(
    course: LanguageCourse,
    onFirstReply: () -> Unit = {},
) {
    val pack = SampleContent.activityPack(course.id)
    val messages = remember { mutableStateListOf<ChatMessage>() }
    var replyIndex by remember(course.id) { mutableIntStateOf(0) }
    var awarded by remember(course.id) { mutableStateOf(false) }
    LaunchedEffect(course.id) {
        messages.clear()
        messages.add(ChatMessage(pack.tutorGreeting, fromUser = false))
        replyIndex = 0
        awarded = false
    }
    var draft by rememberSaveable(course.id) { mutableStateOf("") }
    var isReplying by remember(course.id) { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    LaunchedEffect(messages.size, isReplying) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    GlassCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            GlassTile(
                Modifier
                    .fillMaxWidth()
                    .heightIn(min = 220.dp, max = 320.dp)
            ) {
                Column(
                    Modifier
                        .padding(12.dp)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    messages.forEach { message ->
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = if (message.fromUser) Arrangement.End else Arrangement.Start,
                        ) {
                            GlassTile(
                                color = if (message.fromUser) course.accent.copy(alpha = 0.35f)
                                else VistaTeal.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(12.dp),
                            ) {
                                Text(
                                    text = message.text,
                                    color = TextPrimary,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(12.dp),
                                )
                            }
                        }
                    }
                    if (isReplying) {
                        GlassTile(
                            color = VistaTeal.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(12.dp),
                        ) {
                            Text(
                                text = "Thinking…",
                                color = TextMuted,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(12.dp),
                            )
                        }
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                GlassTextField(
                    value = draft,
                    onValueChange = { draft = it },
                    placeholder = pack.tutorPlaceholder,
                    modifier = Modifier.weight(1f),
                )
                Spacer(Modifier.size(8.dp))
                AeroButton(
                    text = if (isReplying) "Thinking…" else "Send",
                    color = course.accent,
                    onClick = {
                        if (draft.isNotBlank() && !isReplying) {
                            val question = draft.trim()
                            val history = messages.map { it.fromUser to it.text }
                            messages.add(ChatMessage(question, fromUser = true))
                            draft = ""
                            isReplying = true
                            scope.launch {
                                val response = runCatching {
                                    AiTutorRepository.reply(
                                        course = course,
                                        conversation = history,
                                        userMessage = question,
                                    )
                                }.getOrElse {
                                    Log.e("AiTutor", "Gemini request failed", it)
                                    val replies = pack.tutorReplies
                                    if (replies.isEmpty()) {
                                        "I couldn't reach the AI tutor right now. Please try again."
                                    } else {
                                        val reply = replies[replyIndex % replies.size]
                                        replyIndex++
                                        reply
                                    }
                                }
                                messages.add(ChatMessage(response, fromUser = false))
                                isReplying = false
                                if (!awarded) {
                                    awarded = true
                                    onFirstReply()
                                }
                            }
                        }
                    },
                    leadingIcon = {
                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(16.dp),
                        )
                    },
                )
            }
        }
    }
}

data class ChatMessage(val text: String, val fromUser: Boolean)
