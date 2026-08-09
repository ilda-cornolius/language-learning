package com.lingualearn.pro.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lingualearn.pro.data.SampleContent
import com.lingualearn.pro.data.ProgressState
import com.lingualearn.pro.ui.components.AeroProgressBar
import com.lingualearn.pro.ui.components.BodyText
import com.lingualearn.pro.ui.components.CardTitle
import com.lingualearn.pro.ui.components.GlassCard
import com.lingualearn.pro.ui.components.GlassTile
import com.lingualearn.pro.ui.components.InitialsAvatar
import com.lingualearn.pro.ui.theme.TextMuted
import com.lingualearn.pro.ui.theme.TextPrimary
import com.lingualearn.pro.ui.theme.VistaAccent
import com.lingualearn.pro.ui.theme.VistaGreen
import com.lingualearn.pro.ui.theme.VistaTeal

@Composable
fun ProfileScreen(progress: ProgressState) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        GlassCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    InitialsAvatar("Maria Rodriguez", 72.dp)
                    Column(Modifier.padding(start = 16.dp)) {
                        Text(
                            text = "Maria Rodriguez",
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimary,
                        )
                        BodyText("Spanish Learner since 2023")
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatTile("Total XP", progress.totalXp.toString(), Modifier.weight(1f))
                    StatTile("Lessons Completed", progress.lessonsCompleted.toString(), Modifier.weight(1f))
                }
            }
        }

        GlassCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    CardTitle("Level ${progress.level}")
                    BodyText("${progress.xpIntoLevel} / ${ProgressState.XP_PER_LEVEL} XP")
                }
                AeroProgressBar(progress.levelProgress, VistaGreen, height = 10.dp)
                BodyText("${progress.xpToNextLevel} XP to the next level", color = TextMuted)
            }
        }

        GlassCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                CardTitle("Language Progress")
                SampleContent.courses.forEach { course ->
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            BodyText("${course.flag}  ${course.name}")
                            BodyText("${course.progress}%")
                        }
                        AeroProgressBar(course.progress / 100f, course.accent, height = 8.dp)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatTile(label: String, value: String, modifier: Modifier = Modifier) {
    GlassTile(modifier) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = label,
                color = VistaAccent,
                fontWeight = FontWeight.Medium,
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = value,
                color = TextPrimary,
                style = MaterialTheme.typography.headlineSmall,
            )
        }
    }
}

@Composable
fun PreferencesScreen() {
    var voiceSpeed by remember { mutableStateOf("Normal") }
    var dailyReminders by remember { mutableStateOf(true) }
    var soundEffects by remember { mutableStateOf(true) }
    var offlineMode by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        GlassCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                CardTitle("Audio Settings")
                BodyText("Voice Speed", color = TextMuted)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Slow", "Normal", "Fast").forEach { speed ->
                        val selected = speed == voiceSpeed
                        GlassTile(
                            color = if (selected) VistaAccent else Color(0x1AFFFFFF),
                            onClick = { voiceSpeed = speed },
                        ) {
                            Text(
                                text = speed,
                                color = TextPrimary,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            )
                        }
                    }
                }
                ToggleRow("Sound effects", soundEffects) { soundEffects = it }
            }
        }

        GlassCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                CardTitle("Notifications")
                ToggleRow("Daily reminders", dailyReminders) { dailyReminders = it }
                ToggleRow("Download lessons for offline use", offlineMode) { offlineMode = it }
            }
        }
    }
}

@Composable
private fun ToggleRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BodyText(label)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = VistaGreen,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0x33FFFFFF),
                uncheckedBorderColor = Color(0x33FFFFFF),
                checkedBorderColor = VistaTeal,
            ),
        )
    }
}
