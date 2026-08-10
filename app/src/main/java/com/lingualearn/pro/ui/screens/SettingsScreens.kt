package com.lingualearn.pro.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.lingualearn.pro.R
import com.lingualearn.pro.data.CloudWordRepository
import com.lingualearn.pro.data.DailyReminderScheduler
import com.lingualearn.pro.data.PreferencesStore
import com.lingualearn.pro.data.ProgressState
import com.lingualearn.pro.data.ProgressStore
import com.lingualearn.pro.data.SampleContent
import com.lingualearn.pro.ui.components.AeroButton
import com.lingualearn.pro.ui.components.AeroProgressBar
import com.lingualearn.pro.ui.components.BodyText
import com.lingualearn.pro.ui.components.CardTitle
import com.lingualearn.pro.ui.components.GlassCard
import com.lingualearn.pro.ui.components.GlassTextField
import com.lingualearn.pro.ui.components.GlassTile
import com.lingualearn.pro.ui.components.ProfileAvatar
import com.lingualearn.pro.ui.theme.TextMuted
import com.lingualearn.pro.ui.theme.TextPrimary
import com.lingualearn.pro.ui.theme.VistaAccent
import com.lingualearn.pro.ui.theme.VistaBlue
import com.lingualearn.pro.ui.theme.VistaGreen
import com.lingualearn.pro.ui.theme.VistaTeal

@Composable
fun ProfileScreen(
    progress: ProgressState,
    progressStore: ProgressStore,
    preferencesStore: PreferencesStore,
    courseName: String,
) {
    val context = LocalContext.current
    var editing by remember { mutableStateOf(false) }
    var draftName by remember(preferencesStore.displayName) {
        mutableStateOf(preferencesStore.displayName)
    }
    val googleName = CloudWordRepository.userDisplayName
    val googleEmail = CloudWordRepository.userEmail
    val googlePhotoUrl = CloudWordRepository.userPhotoUrl
    val avatarName = googleName ?: preferencesStore.displayName
    val googleSignInClient = remember(context) {
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .requestProfile()
            .build()
        GoogleSignIn.getClient(context, options)
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        GlassCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ProfileAvatar(
                        name = avatarName,
                        photoUrl = googlePhotoUrl,
                        size = 72.dp,
                    )
                    Column(Modifier.padding(start = 16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        if (editing) {
                            GlassTextField(
                                value = draftName,
                                onValueChange = { draftName = it },
                                placeholder = "Display name",
                                modifier = Modifier.fillMaxWidth(),
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                AeroButton(
                                    text = "Save",
                                    color = VistaGreen,
                                    onClick = {
                                        preferencesStore.updateDisplayName(draftName)
                                        editing = false
                                    },
                                )
                                AeroButton(
                                    text = "Cancel",
                                    color = Color(0xFF526777),
                                    onClick = {
                                        draftName = preferencesStore.displayName
                                        editing = false
                                    },
                                )
                            }
                        } else {
                            Text(
                                text = avatarName,
                                style = MaterialTheme.typography.titleLarge,
                                color = TextPrimary,
                            )
                            googleEmail?.let {
                                BodyText(it, color = TextMuted)
                            }
                            BodyText("$courseName Learner since ${preferencesStore.learnerSinceYear}")
                            AeroButton(
                                text = "Edit name",
                                color = VistaAccent,
                                onClick = { editing = true },
                            )
                        }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatTile("Total XP", progress.totalXp.toString(), Modifier.weight(1f))
                    StatTile("Lessons Completed", progress.lessonsCompleted.toString(), Modifier.weight(1f))
                }
                AeroButton(
                    text = "Sign out",
                    color = VistaBlue,
                    onClick = {
                        googleSignInClient.signOut().addOnCompleteListener {
                            CloudWordRepository.signOut()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
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
                    val pct = progressStore.courseProgress(course.id)
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            BodyText("${course.flag}  ${course.name}")
                            BodyText("$pct%")
                        }
                        AeroProgressBar(pct / 100f, course.accent, height = 8.dp)
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
fun PreferencesScreen(preferencesStore: PreferencesStore) {
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) {
            preferencesStore.updateDailyReminders(true)
            DailyReminderScheduler.schedule(context)
        } else {
            preferencesStore.updateDailyReminders(false)
            DailyReminderScheduler.cancel(context)
        }
    }

    fun setReminders(enabled: Boolean) {
        if (enabled) {
            if (Build.VERSION.SDK_INT >= 33) {
                val granted = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS,
                ) == PackageManager.PERMISSION_GRANTED
                if (!granted) {
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    return
                }
            }
            preferencesStore.updateDailyReminders(true)
            DailyReminderScheduler.schedule(context)
        } else {
            preferencesStore.updateDailyReminders(false)
            DailyReminderScheduler.cancel(context)
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        GlassCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                CardTitle("Audio Settings")
                BodyText("Voice Speed", color = TextMuted)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PreferencesStore.SPEEDS.forEach { speed ->
                        val selected = speed == preferencesStore.voiceSpeed
                        GlassTile(
                            color = if (selected) VistaAccent else Color(0x1AFFFFFF),
                            onClick = { preferencesStore.updateVoiceSpeed(speed) },
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
                ToggleRow("Sound effects", preferencesStore.soundEffects) {
                    preferencesStore.updateSoundEffects(it)
                }
            }
        }

        GlassCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                CardTitle("Notifications")
                ToggleRow("Daily reminders", preferencesStore.dailyReminders) { setReminders(it) }
                ToggleRow("Download lessons for offline use", preferencesStore.offlineMode) {
                    preferencesStore.updateOfflineMode(it)
                }
                if (preferencesStore.offlineMode) {
                    BodyText(
                        "Offline packs ready for Spanish/French/Japanese",
                        color = VistaGreen,
                    )
                }
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
