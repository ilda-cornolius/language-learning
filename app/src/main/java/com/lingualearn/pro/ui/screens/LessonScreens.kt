package com.lingualearn.pro.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.lingualearn.pro.data.DialogueLine
import com.lingualearn.pro.data.SampleContent
import com.lingualearn.pro.ui.components.AeroButton
import com.lingualearn.pro.ui.components.Badge
import com.lingualearn.pro.ui.components.BodyText
import com.lingualearn.pro.ui.components.CardTitle
import com.lingualearn.pro.ui.components.GlassCard
import com.lingualearn.pro.ui.components.GlassTile
import com.lingualearn.pro.ui.theme.GlassTileStrong
import com.lingualearn.pro.ui.theme.TextMuted
import com.lingualearn.pro.ui.theme.TextPrimary
import com.lingualearn.pro.ui.theme.TextSecondary
import com.lingualearn.pro.ui.theme.VistaAccent
import com.lingualearn.pro.ui.theme.VistaBlue
import com.lingualearn.pro.ui.theme.VistaGreen
import com.lingualearn.pro.ui.theme.VistaTeal

@Composable
fun LessonScreen(onCheck: () -> Unit) {
    var answer by rememberSaveable { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        GlassCard {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Dialogue Practice",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                    )
                    Badge("Intermediate", VistaBlue.copy(alpha = 0.7f))
                }

                SampleContent.dialogue.forEach { line -> DialogueRow(line) }

                AnswerRow(
                    value = answer,
                    onValueChange = { answer = it },
                    hint = "Hint: Tell what movie you saw",
                )

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        Modifier
                            .clickable(onClick = {})
                            .padding(horizontal = 4.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = TextPrimary,
                            modifier = Modifier.size(16.dp),
                        )
                        Text(
                            text = "Previous",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary,
                            modifier = Modifier.padding(start = 8.dp),
                        )
                    }
                    AeroButton(
                        text = "Check",
                        onClick = onCheck,
                        trailingIcon = {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.padding(start = 8.dp).size(16.dp),
                            )
                        },
                    )
                }
            }
        }

        GlassCard {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Key Vocabulary",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                )
                SampleContent.lessonVocabulary.chunked(2).forEach { pair ->
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        pair.forEach { word ->
                            GlassTile(Modifier.weight(1f)) {
                                Column(Modifier.padding(12.dp)) {
                                    Row(
                                        Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Text(
                                            text = word.term,
                                            color = VistaAccent,
                                            fontWeight = FontWeight.Medium,
                                            style = MaterialTheme.typography.bodyMedium,
                                        )
                                        SpeakerButton()
                                    }
                                    BodyText(word.meaning)
                                }
                            }
                        }
                        if (pair.size == 1) Box(Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun DialogueRow(line: DialogueLine) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Box(
            Modifier
                .size(40.dp)
                .background(line.speakerColor.copy(alpha = 0.7f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(line.speaker, color = Color.White, fontWeight = FontWeight.Medium)
        }
        SpeechBubble(
            tint = line.speakerColor.copy(alpha = 0.22f),
            modifier = Modifier
                .padding(start = 10.dp)
                .weight(1f),
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Text(
                    text = line.phrase,
                    color = TextPrimary,
                    fontWeight = FontWeight.Medium,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f),
                )
                SpeakerButton()
            }
            BodyText(line.translation, color = TextMuted)
        }
    }
}

@Composable
private fun AnswerRow(value: String, onValueChange: (String) -> Unit, hint: String) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Box(
            Modifier
                .size(40.dp)
                .background(VistaTeal.copy(alpha = 0.7f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text("B", color = Color.White, fontWeight = FontWeight.Medium)
        }
        SpeechBubble(
            tint = VistaTeal.copy(alpha = 0.22f),
            modifier = Modifier
                .padding(start = 10.dp)
                .weight(1f),
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary),
                cursorBrush = SolidColor(VistaAccent),
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { inner ->
                    Column {
                        if (value.isEmpty()) {
                            BodyText("Type your answer...", color = TextMuted)
                        }
                        inner()
                        Box(
                            Modifier
                                .padding(top = 6.dp)
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Color(0x66FFFFFF))
                        )
                    }
                },
            )
            Text(
                text = hint,
                style = MaterialTheme.typography.bodySmall,
                fontStyle = FontStyle.Italic,
                color = TextMuted,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}

/** Chat-style bubble sitting next to the speaker avatar. */
@Composable
private fun SpeechBubble(
    tint: Color,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier) {
        Column(
            Modifier
                .fillMaxWidth()
                .clip(
                    RoundedCornerShape(
                        topStart = 4.dp,
                        topEnd = 18.dp,
                        bottomStart = 18.dp,
                        bottomEnd = 18.dp,
                    )
                )
                .background(GlassTileStrong)
                .background(tint)
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            content()
        }
    }
}

@Composable
fun SpeakerButton(onClick: () -> Unit = {}) {
    IconButton(onClick = onClick, modifier = Modifier.size(24.dp)) {
        Icon(
            Icons.AutoMirrored.Filled.VolumeUp,
            contentDescription = "Play audio",
            tint = TextSecondary,
            modifier = Modifier.size(18.dp),
        )
    }
}

@Composable
fun DailyLessonScreen(onStart: () -> Unit) {
    GlassCard(Modifier.fillMaxWidth()) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                Modifier
                    .size(80.dp)
                    .background(VistaAccent.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Filled.School,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(36.dp),
                )
            }
            CardTitle("Lesson 12: Everyday Conversations")
            BodyText("Learn how to have basic conversations in Spanish")
            AeroButton("Start Lesson", onClick = onStart)
        }
    }
}

@Composable
fun PracticeScreen() {
    Column(
        Modifier.padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        PracticeCard(
            title = "Quick Review",
            description = "Practice words you've learned recently",
            buttonText = "Start Review",
            buttonColor = VistaTeal,
        )
        PracticeCard(
            title = "Grammar Drills",
            description = "Focus on verb conjugations and sentence structure",
            buttonText = "Start Drills",
            buttonColor = VistaBlue,
        )
        PracticeCard(
            title = "Pronunciation Lab",
            description = "Repeat after the speaker and compare your accent",
            buttonText = "Start Lab",
            buttonColor = VistaGreen,
        )
    }
}

@Composable
private fun PracticeCard(title: String, description: String, buttonText: String, buttonColor: Color) {
    var started by remember { mutableStateOf(false) }
    GlassCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            CardTitle(title)
            BodyText(if (started) "In progress — keep going!" else description)
            AeroButton(
                text = if (started) "Continue" else buttonText,
                color = buttonColor,
                onClick = { started = true },
            )
        }
    }
}

@Composable
fun ChallengesScreen() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SampleContent.challenges.forEach { challenge ->
            var accepted by remember { mutableStateOf(false) }
            GlassCard(Modifier.fillMaxWidth()) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        CardTitle(challenge.title)
                        BodyText(challenge.description)
                    }
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = challenge.reward,
                            color = VistaAccent,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                        )
                        if (challenge.actionable) {
                            AeroButton(
                                text = if (accepted) "Accepted" else "Accept",
                                color = if (accepted) VistaGreen else VistaAccent,
                                onClick = { accepted = true },
                            )
                        }
                        challenge.progressLabel?.let {
                            Text(it, style = MaterialTheme.typography.bodySmall, color = TextMuted)
                        }
                    }
                }
            }
        }
    }
}
