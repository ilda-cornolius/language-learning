package com.lingualearn.pro.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lingualearn.pro.data.SampleContent
import com.lingualearn.pro.ui.components.AeroButton
import com.lingualearn.pro.ui.components.AeroProgressBar
import com.lingualearn.pro.ui.components.BodyText
import com.lingualearn.pro.ui.components.CardTitle
import com.lingualearn.pro.ui.components.GlassCard
import com.lingualearn.pro.ui.components.GlassTile
import com.lingualearn.pro.ui.components.GlassTextField
import com.lingualearn.pro.ui.theme.TextMuted
import com.lingualearn.pro.ui.theme.TextPrimary
import com.lingualearn.pro.ui.theme.VistaAccent
import com.lingualearn.pro.ui.theme.VistaBlue
import com.lingualearn.pro.ui.theme.VistaGreen
import com.lingualearn.pro.ui.theme.VistaTeal

@Composable
fun VocabularyScreen() {
    var revealed by remember { mutableStateOf(setOf<String>()) }

    GlassCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            BodyText("Tap a card to flip between Spanish and English.")
            SampleContent.vocabularyBuilder.chunked(2).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    row.forEach { word ->
                        val isRevealed = word.term in revealed
                        GlassTile(
                            modifier = Modifier.weight(1f),
                            onClick = {
                                revealed = if (isRevealed) revealed - word.term else revealed + word.term
                            },
                        ) {
                            Column(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                Text(word.emoji ?: "", style = MaterialTheme.typography.headlineSmall)
                                Text(
                                    text = if (isRevealed) word.meaning else word.term,
                                    color = if (isRevealed) TextPrimary else VistaAccent,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                                Text(
                                    text = if (isRevealed) "english" else "español",
                                    color = TextMuted,
                                    style = MaterialTheme.typography.bodySmall,
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
fun ConversationScreen() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        ScenarioCard(
            title = "Restaurant Conversation",
            description = "Practice ordering food and drinks",
            tint = VistaGreen,
        )
        ScenarioCard(
            title = "Travel Scenarios",
            description = "Airport, hotel, and transportation",
            tint = VistaBlue,
        )
        ScenarioCard(
            title = "Small Talk",
            description = "Weather, weekends, and introductions",
            tint = VistaTeal,
        )
    }
}

@Composable
private fun ScenarioCard(title: String, description: String, tint: Color) {
    GlassCard(Modifier.fillMaxWidth(), color = tint.copy(alpha = 0.25f)) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            CardTitle(title)
            BodyText(description)
        }
    }
}

@Composable
fun ListeningScreen() {
    var playing by remember { mutableStateOf(false) }
    val transition = rememberInfiniteTransition(label = "pulse")
    val pulse by transition.animateFloat(
        initialValue = 1f,
        targetValue = if (playing) 1.08f else 1f,
        animationSpec = infiniteRepeatable(tween(900, easing = LinearEasing), RepeatMode.Reverse),
        label = "pulseScale",
    )

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
                    .background(VistaTeal.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = if (playing) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = if (playing) "Pause" else "Play",
                    tint = Color.White,
                    modifier = Modifier.size(52.dp),
                )
            }
            CardTitle("Spanish News Podcast")
            BodyText("Listen to current events in Spanish")
            AeroProgressBar(progress = if (playing) 0.35f else 0f, color = VistaTeal)
            AeroButton(
                text = if (playing) "Pause Listening" else "Start Listening",
                onClick = { playing = !playing },
            )
        }
    }
}

@Composable
fun WritingScreen() {
    var text by rememberSaveable { mutableStateOf("") }
    var submitted by remember { mutableStateOf(false) }
    val words = text.split(Regex("\\s+")).count { it.isNotBlank() }

    GlassCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            CardTitle("Today's Writing Prompt")
            BodyText("Describe your ideal vacation in Spanish (100 words minimum)")
            GlassTextField(
                value = text,
                onValueChange = {
                    text = it
                    submitted = false
                },
                placeholder = "Escribe aquí...",
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
                Text(
                    text = "$words / 100 words",
                    color = if (words >= 100) VistaGreen else TextMuted,
                    style = MaterialTheme.typography.bodySmall,
                )
                AeroButton(
                    text = if (submitted) "Submitted" else "Submit",
                    color = if (submitted) VistaGreen else VistaAccent,
                    onClick = { if (text.isNotBlank()) submitted = true },
                )
            }
            if (submitted) {
                BodyText("¡Bien hecho! Your tutor will review this entry.", color = VistaGreen)
            }
        }
    }
}

@Composable
fun AssistantScreen() {
    val messages = remember {
        mutableStateListOf(
            ChatMessage("¡Hola! I'm your AI Spanish tutor. How can I help you today?", fromUser = false)
        )
    }
    var draft by rememberSaveable { mutableStateOf("") }
    var replyIndex by remember { mutableIntStateOf(0) }
    val scrollState = rememberScrollState()

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
                                color = if (message.fromUser) VistaAccent.copy(alpha = 0.35f)
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
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                GlassTextField(
                    value = draft,
                    onValueChange = { draft = it },
                    placeholder = "Ask me anything about Spanish...",
                    modifier = Modifier.weight(1f),
                )
                Spacer(Modifier.size(8.dp))
                AeroButton(
                    text = "Send",
                    onClick = {
                        if (draft.isNotBlank()) {
                            messages.add(ChatMessage(draft, fromUser = true))
                            messages.add(
                                ChatMessage(
                                    SampleContent.tutorReplies[replyIndex % SampleContent.tutorReplies.size],
                                    fromUser = false,
                                )
                            )
                            replyIndex++
                            draft = ""
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
