package com.lingualearn.pro.ui.screens

import android.content.Context
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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.lingualearn.pro.R
import com.lingualearn.pro.data.CloudWordRepository
import com.lingualearn.pro.data.DictionaryLookupRepository
import com.lingualearn.pro.data.LanguageCourse
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lingualearn.pro.data.PostComment
import com.lingualearn.pro.data.SampleContent
import com.lingualearn.pro.data.SavedWordRepository
import com.lingualearn.pro.data.SocialPost
import com.lingualearn.pro.ui.components.AeroButton
import com.lingualearn.pro.ui.components.BodyText
import com.lingualearn.pro.ui.components.CardTitle
import com.lingualearn.pro.ui.components.GlassCard
import com.lingualearn.pro.ui.components.GlassTextField
import com.lingualearn.pro.ui.components.GlassTile
import com.lingualearn.pro.ui.components.InitialsAvatar
import com.lingualearn.pro.ui.theme.TextMuted
import com.lingualearn.pro.ui.theme.TextPrimary
import com.lingualearn.pro.ui.theme.VistaAccent
import com.lingualearn.pro.ui.theme.VistaBlue
import com.lingualearn.pro.ui.theme.VistaGreen
import com.lingualearn.pro.ui.theme.VistaTeal
import kotlinx.coroutines.launch

@Composable
fun InstagramScreen() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        GlassCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                CardTitle("Connect with Spanish Speakers")
                SampleContent.suggestedFollows.forEach { suggestion ->
                    var following by remember { mutableStateOf(false) }
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
                                onClick = { following = !following },
                            )
                        }
                    }
                }
            }
        }

        GlassCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                CardTitle("Language Learning Posts")
                SampleContent.posts.forEach { post -> PostCard(post) }
            }
        }
    }
}

@Composable
private fun PostCard(post: SocialPost) {
    var liked by remember { mutableStateOf(false) }
    var commentsOpen by remember { mutableStateOf(false) }
    val comments = remember(post.id) { mutableStateListOf<PostComment>().apply { addAll(post.comments) } }
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
                    onClick = { liked = !liked },
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
                                    comments.add(PostComment("@you", draft))
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
fun GoogleToolsScreen(course: LanguageCourse) {
    val context = LocalContext.current
    var source by remember { mutableStateOf("") }
    var translation by remember { mutableStateOf("") }
    var translationStatus by remember { mutableStateOf("") }
    var translating by remember { mutableStateOf(false) }
    var cloudStatus by remember { mutableStateOf("") }
    var pendingSave by remember { mutableStateOf<Pair<String, String>?>(null) }
    var docsStatus by remember { mutableStateOf("") }
    var listening by remember { mutableStateOf(false) }
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
                    text = if (listening) "Listening... say \"¿dónde está la estación?\""
                    else "Practice pronunciation with Google Voice",
                    modifier = Modifier.fillMaxWidth(),
                )
                AeroButton(
                    text = if (listening) "Stop" else "Start Voice Practice",
                    color = VistaTeal,
                    onClick = { listening = !listening },
                )
            }
        }

        GlassCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                CardTitle("Google Docs Integration")
                BodyText("Create and share ${course.name} learning documents")
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AeroButton(
                        "Create Doc",
                        onClick = {
                            runCatching { uriHandler.openUri("https://docs.new") }
                                .onSuccess { docsStatus = "Opening a new Google document…" }
                                .onFailure { docsStatus = "No browser or Google Docs app is available." }
                        },
                        color = VistaBlue,
                    )
                    AeroButton(
                        "View Shared",
                        onClick = {
                            runCatching {
                                uriHandler.openUri("https://drive.google.com/drive/shared-with-me")
                            }
                                .onSuccess { docsStatus = "Opening documents shared with you…" }
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
    return words.size
}

@Composable
fun CenteredHint(text: String) {
    Text(
        text = text,
        color = TextMuted,
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier.fillMaxWidth(),
    )
}
