package com.lingualearn.pro.ui.screens

import android.content.Context
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.lingualearn.pro.R
import com.lingualearn.pro.data.CloudWordRepository
import com.lingualearn.pro.data.DictionaryLookupRepository
import com.lingualearn.pro.data.LanguageCourse
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.max
import com.lingualearn.pro.data.PostComment
import com.lingualearn.pro.data.SampleContent
import com.lingualearn.pro.data.SavedWordRepository
import com.lingualearn.pro.data.SocialPost
import com.lingualearn.pro.data.SocialStore
import com.lingualearn.pro.ui.components.AeroButton
import com.lingualearn.pro.ui.components.BodyText
import com.lingualearn.pro.ui.components.CardTitle
import com.lingualearn.pro.ui.components.GlassCard
import com.lingualearn.pro.ui.components.GlassTextField
import com.lingualearn.pro.ui.components.GlassTile
import com.lingualearn.pro.ui.components.InitialsAvatar
import com.lingualearn.pro.ui.components.PhraseWithReading
import com.lingualearn.pro.ui.theme.TextMuted
import com.lingualearn.pro.ui.theme.TextPrimary
import com.lingualearn.pro.ui.theme.VistaAccent
import com.lingualearn.pro.ui.theme.VistaBlue
import com.lingualearn.pro.ui.theme.VistaGreen
import com.lingualearn.pro.ui.theme.VistaTeal
import kotlinx.coroutines.launch

@Composable
fun InstagramScreen(course: LanguageCourse) {
    val context = LocalContext.current
    val socialStore = remember(context) { SocialStore(context.applicationContext) }
    val feed = remember(course.id) { SampleContent.socialFeed(course.id) }
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        GlassCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                CardTitle("Connect with ${course.name} Speakers")
                feed.follows.forEach { suggestion ->
                    var following by remember(suggestion.handle) {
                        mutableStateOf(socialStore.isFollowing(suggestion.handle))
                    }
                    GlassTile(Modifier.fillMaxWidth()) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            InitialsAvatar(suggestion.handle, 44.dp)
                            Column(
                                Modifier
                                    .weight(1f)
                                    .padding(start = 12.dp)
                            ) {
                                Text(
                                    text = suggestion.handle,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Medium,
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                                Text(
                                    text = suggestion.role,
                                    color = TextMuted,
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }
                            AeroButton(
                                text = if (following) "Following" else "Follow",
                                color = if (following) VistaGreen else VistaAccent,
                                onClick = {
                                    following = !following
                                    socialStore.setFollowing(suggestion.handle, following)
                                },
                            )
                        }
                    }
                }
            }
        }

        GlassCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                CardTitle("Language Learning Posts")
                feed.posts.forEach { post -> PostCard(post, socialStore) }
            }
        }
    }
}

@Composable
private fun PostCard(post: SocialPost, socialStore: SocialStore) {
    var liked by remember(post.id) { mutableStateOf(socialStore.isLiked(post.id)) }
    var commentsOpen by remember { mutableStateOf(false) }
    val comments = remember(post.id) {
        mutableStateListOf<PostComment>().apply {
            addAll(socialStore.commentsFor(post.id, post.comments))
        }
    }
    var draft by remember { mutableStateOf("") }
    val likeCount = post.likes + if (liked) 1 else 0

    GlassTile(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                InitialsAvatar(post.author, 32.dp)
                Text(
                    text = post.author,
                    color = TextPrimary,
                    fontWeight = FontWeight.Medium,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 8.dp),
                )
            }
            Text(
                text = post.body,
                color = TextPrimary,
                style = MaterialTheme.typography.bodyMedium,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                CounterAction(
                    icon = { tint ->
                        Icon(
                            imageVector = if (liked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (liked) Color(0xFFEF4444) else tint,
                            modifier = Modifier.size(16.dp),
                        )
                    },
                    label = "$likeCount likes",
                    onClick = {
                        liked = !liked
                        socialStore.setLiked(post.id, liked)
                    },
                )
                CounterAction(
                    icon = { tint ->
                        Icon(
                            Icons.Filled.ChatBubbleOutline,
                            contentDescription = "Comments",
                            tint = tint,
                            modifier = Modifier.size(16.dp),
                        )
                    },
                    label = "${comments.size} comments",
                    onClick = { commentsOpen = !commentsOpen },
                )
            }

            if (commentsOpen) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    comments.forEach { comment ->
                        GlassTile(Modifier.fillMaxWidth(), color = Color(0x14FFFFFF)) {
                            Column(Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    InitialsAvatar(comment.author, 24.dp)
                                    Text(
                                        text = comment.author,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Medium,
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(start = 8.dp),
                                    )
                                }
                                BodyText(comment.body)
                            }
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        InitialsAvatar("you", 24.dp)
                        Spacer(Modifier.size(8.dp))
                        GlassTextField(
                            value = draft,
                            onValueChange = { draft = it },
                            placeholder = "Add a comment...",
                            modifier = Modifier.weight(1f),
                        )
                        Spacer(Modifier.size(8.dp))
                        AeroButton(
                            text = "Post",
                            onClick = {
                                if (draft.isNotBlank()) {
                                    comments.add(PostComment("@you", draft.trim()))
                                    socialStore.saveComments(post.id, comments.toList())
                                    draft = ""
                                }
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CounterAction(
    icon: @Composable (Color) -> Unit,
    label: String,
    onClick: () -> Unit,
) {
    GlassTile(color = Color.Transparent, onClick = onClick) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 4.dp),
        ) {
            icon(TextMuted)
            Text(
                text = label,
                color = TextMuted,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 6.dp),
            )
        }
    }
}

@Composable
fun GoogleToolsScreen(
    course: LanguageCourse,
    onVoiceComplete: () -> Unit = {},
) {
    val context = LocalContext.current
    var source by remember { mutableStateOf("") }
    var translation by remember { mutableStateOf("") }
    var translationStatus by remember { mutableStateOf("") }
    var translating by remember { mutableStateOf(false) }
    var cloudStatus by remember { mutableStateOf("") }
    var pendingSave by remember { mutableStateOf<Pair<String, String>?>(null) }
    var docsStatus by remember { mutableStateOf("") }
    var listening by remember { mutableStateOf(false) }
    var heard by remember { mutableStateOf("") }
    var voiceFeedback by remember { mutableStateOf("Practice pronunciation with Google Voice") }
    val practicePhrase = remember(course.id) {
        SampleContent.practicePack(course.id).pronunciationPhrases.first()
    }
    val pack = remember(course.id) { SampleContent.practicePack(course.id) }
    val scope = rememberCoroutineScope()
    val uriHandler = LocalUriHandler.current
    val googleSignInClient = remember(context) {
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, options)
    }
    val languageCode = when (course.id) {
        "french" -> "fr"
        "japanese" -> "ja"
        else -> "es"
    }

    val voiceLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        listening = false
        val text = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
        if (text == null) {
            voiceFeedback = "I couldn't hear that. Try again."
        } else {
            heard = text
            val score = googleVoiceScore(practicePhrase.phrase, text)
            voiceFeedback = if (score >= 70) {
                onVoiceComplete()
                "Nice! $score% match."
            } else {
                "Good try — $score% match. Aim for 70%+."
            }
        }
    }

    fun startVoicePractice() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, pack.localeTag)
            putExtra(RecognizerIntent.EXTRA_PROMPT, practicePhrase.phrase)
        }
        listening = true
        runCatching { voiceLauncher.launch(intent) }
            .onFailure {
                listening = false
                voiceFeedback = "Speech recognition is unavailable."
            }
    }

    val micPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (it) startVoicePractice() else voiceFeedback = "Microphone permission is required."
    }

    LaunchedEffect(Unit) {
        if (CloudWordRepository.isSignedIn) {
            runCatching { syncLocalWordsToCloud(context) }
                .onSuccess { count ->
                    if (count > 0) cloudStatus = "Synced $count learned words to your online deck."
                }
        }
    }

    fun saveTranslationOnline(word: String, meaning: String) {
        scope.launch {
            cloudStatus = "Saving to your online deck…"
            runCatching {
                CloudWordRepository.saveWord(
                    languageId = course.id,
                    languageName = course.name,
                    word = word,
                    meaning = meaning,
                    source = "quick_translate",
                )
            }.onSuccess {
                cloudStatus = "Saved to your online ${course.name} deck."
            }.onFailure {
                cloudStatus = "Could not save online: ${it.message ?: "unknown Firebase error"}"
            }
        }
    }

    val signInLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val account = runCatching {
            GoogleSignIn.getSignedInAccountFromIntent(result.data)
                .getResult(ApiException::class.java)
        }.getOrNull()
        val idToken = account?.idToken
        if (idToken == null) {
            cloudStatus = "Google sign-in was cancelled or unsuccessful."
        } else {
            scope.launch {
                cloudStatus = "Signing in…"
                runCatching { CloudWordRepository.signInWithGoogle(idToken) }
                    .onSuccess {
                        cloudStatus = "Syncing learned words…"
                        runCatching { syncLocalWordsToCloud(context) }
                        val saved = pendingSave
                        pendingSave = null
                        if (saved != null) saveTranslationOnline(saved.first, saved.second)
                    }
                    .onFailure {
                        cloudStatus = "Firebase sign-in failed: ${it.message ?: "unknown error"}"
                    }
            }
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        GlassCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                CardTitle("Quick Translate")
                BodyText("From English", color = TextMuted)
                GlassTextField(
                    value = source,
                    onValueChange = { source = it },
                    placeholder = "Enter text to translate...",
                    singleLine = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(96.dp),
                )
                BodyText("To ${course.name}", color = TextMuted)
                GlassTile(
                    Modifier
                        .fillMaxWidth()
                        .height(96.dp),
                    color = Color(0x0DFFFFFF),
                ) {
                    Text(
                        text = translation.ifEmpty { "Translation will appear here..." },
                        color = if (translation.isEmpty()) TextMuted else TextPrimary,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(12.dp),
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    AeroButton(
                        text = if (translating) "Translating…" else "Translate",
                        onClick = {
                            if (source.isBlank()) {
                                translation = ""
                                translationStatus = "Enter some English text first."
                            } else if (!translating) {
                                scope.launch {
                                    translating = true
                                    translationStatus = ""
                                    val local = offlineTranslate(source, course.id)
                                    val online = if (local == null) {
                                        DictionaryLookupRepository.translate(
                                            text = source,
                                            sourceLanguage = "en",
                                            targetLanguage = languageCode,
                                        )
                                    } else {
                                        null
                                    }
                                    translation = online ?: local.orEmpty()
                                    translationStatus = when {
                                        online != null -> "Translated online"
                                        local != null -> "Offline translation"
                                        else -> "Translation unavailable. Check your connection and try again."
                                    }
                                    translating = false
                                }
                            }
                        },
                    )
                    AeroButton(
                        text = "Save",
                        color = VistaGreen,
                        onClick = {
                            if (source.isBlank() || translation.isBlank()) {
                                cloudStatus = "Translate a word or phrase before saving it."
                            } else if (CloudWordRepository.isSignedIn) {
                                saveTranslationOnline(source, translation)
                            } else {
                                pendingSave = source to translation
                                cloudStatus = "Sign in with Google to save your deck online."
                                signInLauncher.launch(googleSignInClient.signInIntent)
                            }
                        },
                    )
                }
                if (translationStatus.isNotBlank()) {
                    BodyText(translationStatus, color = TextMuted)
                }
                if (cloudStatus.isNotBlank()) {
                    BodyText(cloudStatus, color = TextMuted)
                }
                CloudWordRepository.userLabel?.let {
                    BodyText("Online deck: $it", color = TextMuted)
                }
            }
        }

        GlassCard(Modifier.fillMaxWidth()) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                CardTitle("Voice Search Practice")
                BodyText("Say:", color = TextMuted)
                PhraseWithReading(
                    phrase = practicePhrase.phrase,
                    reading = practicePhrase.reading,
                )
                Box(
                    Modifier
                        .size(80.dp)
                        .background(VistaTeal.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Filled.Mic,
                        contentDescription = null,
                        tint = if (listening) VistaAccent else Color.White,
                        modifier = Modifier.size(34.dp),
                    )
                }
                BodyText(
                    text = if (listening) "Listening…" else voiceFeedback,
                    modifier = Modifier.fillMaxWidth(),
                )
                if (heard.isNotBlank()) {
                    BodyText("I heard: “$heard”", color = TextMuted)
                }
                AeroButton(
                    text = if (listening) "Listening…" else "Start Voice Practice",
                    color = VistaTeal,
                    onClick = {
                        if (listening) return@AeroButton
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) ==
                            PackageManager.PERMISSION_GRANTED
                        ) {
                            startVoicePractice()
                        } else {
                            micPermission.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    },
                )
            }
        }

        GlassCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                CardTitle("Google Docs & Drive")
                BodyText("Opens Google Docs or Drive in your browser for ${course.name} notes")
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AeroButton(
                        "Open Google Docs",
                        onClick = {
                            runCatching { uriHandler.openUri("https://docs.new") }
                                .onSuccess { docsStatus = "Opening Google Docs in your browser…" }
                                .onFailure { docsStatus = "No browser or Google Docs app is available." }
                        },
                        color = VistaBlue,
                    )
                    AeroButton(
                        "Open Google Drive",
                        onClick = {
                            runCatching {
                                uriHandler.openUri("https://drive.google.com/drive/shared-with-me")
                            }
                                .onSuccess { docsStatus = "Opening Google Drive in your browser…" }
                                .onFailure { docsStatus = "No browser or Google Drive app is available." }
                        },
                        color = VistaGreen,
                    )
                }
                if (docsStatus.isNotBlank()) BodyText(docsStatus, color = TextMuted)
            }
        }
    }
}

private fun googleVoiceScore(target: String, spoken: String): Int {
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

/**
 * Tiny offline phrasebook so the translate card responds without a network call;
 * anything unknown is echoed back unchanged.
 */
private val phrasebooks = mapOf(
    "spanish" to mapOf(
        "hi" to "hola",
        "hey" to "hola",
        "hello" to "hola",
        "good morning" to "buenos días",
        "thank you" to "gracias",
        "how are you" to "¿cómo estás?",
        "goodbye" to "adiós",
        "please" to "por favor",
        "weekend" to "fin de semana",
        "movie" to "película",
        "friend" to "amigo",
        "school" to "escuela",
        "yes" to "sí",
        "no" to "no",
        "good" to "bueno",
        "bad" to "malo",
        "morning" to "mañana",
        "night" to "noche",
        "water" to "agua",
        "food" to "comida",
        "where" to "dónde",
        "what" to "qué",
        "when" to "cuándo",
        "why" to "por qué",
        "who" to "quién",
        "i" to "yo",
        "you" to "tú",
        "love" to "amor",
    ),
    "french" to mapOf(
        "hi" to "salut",
        "hey" to "salut",
        "hello" to "bonjour",
        "good morning" to "bonjour",
        "thank you" to "merci",
        "how are you" to "comment allez-vous ?",
        "goodbye" to "au revoir",
        "please" to "s’il vous plaît",
        "weekend" to "week-end",
        "movie" to "film",
        "friend" to "ami",
        "school" to "école",
        "yes" to "oui",
        "no" to "non",
        "good" to "bon",
        "bad" to "mauvais",
        "morning" to "matin",
        "night" to "nuit",
        "water" to "eau",
        "food" to "nourriture",
    ),
    "japanese" to mapOf(
        "hi" to "こんにちは",
        "hey" to "やあ",
        "hello" to "こんにちは",
        "good morning" to "おはようございます",
        "thank you" to "ありがとう",
        "how are you" to "お元気ですか",
        "goodbye" to "さようなら",
        "please" to "お願いします",
        "weekend" to "週末",
        "movie" to "映画",
        "friend" to "友達",
        "school" to "学校",
        "yes" to "はい",
        "no" to "いいえ",
        "good" to "良い",
        "bad" to "悪い",
        "morning" to "朝",
        "night" to "夜",
        "water" to "水",
        "food" to "食べ物",
    ),
)

private fun offlineTranslate(input: String, courseId: String): String? {
    val trimmed = input.trim().lowercase().trimEnd('.', '?', '!')
    if (trimmed.isEmpty()) return null
    val phrasebook = phrasebooks[courseId] ?: phrasebooks.getValue("spanish")
    phrasebook[trimmed]?.let { return it }
    val words = trimmed.split(" ")
    val translated = words.map { phrasebook[it] ?: it }
    return translated.joinToString(" ").takeIf { translated != words }
}

private suspend fun syncLocalWordsToCloud(context: Context): Int {
    val words = SavedWordRepository.allWords(context)
    words.forEach { saved ->
        val course = SampleContent.courseById(saved.languageId)
        CloudWordRepository.saveWord(
            languageId = saved.languageId,
            languageName = course.name,
            word = saved.word,
            meaning = saved.meaning,
            exampleSentence = saved.exampleSentence,
            source = "dictation_notebook",
        )
    }
    if (words.isNotEmpty()) CloudWordRepository.markSynced()
    return words.size
}
