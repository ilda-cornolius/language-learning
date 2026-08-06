package com.lingualearn.pro.ui.screens

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lingualearn.pro.data.PostComment
import com.lingualearn.pro.data.SampleContent
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
fun GoogleToolsScreen() {
    var source by remember { mutableStateOf("") }
    var translation by remember { mutableStateOf("") }
    var listening by remember { mutableStateOf(false) }

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
                BodyText("To Spanish", color = TextMuted)
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
                AeroButton(
                    text = "Translate",
                    onClick = { translation = offlineTranslate(source) },
                )
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
                BodyText("Create and share Spanish learning documents")
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AeroButton("Create Doc", onClick = {}, color = VistaBlue)
                    AeroButton("View Shared", onClick = {}, color = VistaGreen)
                }
            }
        }
    }
}

/**
 * Tiny offline phrasebook so the translate card responds without a network call;
 * anything unknown is echoed back unchanged.
 */
private val phrasebook = mapOf(
    "hello" to "hola",
    "good morning" to "buenos días",
    "thank you" to "gracias",
    "how are you" to "¿cómo estás?",
    "goodbye" to "adiós",
    "please" to "por favor",
    "weekend" to "fin de semana",
    "movie" to "película",
)

private fun offlineTranslate(input: String): String {
    val trimmed = input.trim().lowercase().trimEnd('.', '?', '!')
    if (trimmed.isEmpty()) return ""
    return phrasebook[trimmed] ?: trimmed.split(" ").joinToString(" ") { phrasebook[it] ?: it }
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
