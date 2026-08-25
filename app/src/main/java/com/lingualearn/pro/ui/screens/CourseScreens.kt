package com.lingualearn.pro.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lingualearn.pro.data.LanguageCourse
import com.lingualearn.pro.data.ProgressState
import com.lingualearn.pro.data.SampleContent
import com.lingualearn.pro.ui.Destination
import com.lingualearn.pro.ui.components.AeroButton
import com.lingualearn.pro.ui.components.AeroProgressBar
import com.lingualearn.pro.ui.components.Badge
import com.lingualearn.pro.ui.components.BodyText
import com.lingualearn.pro.ui.components.CardTitle
import com.lingualearn.pro.ui.components.CrystalCursor
import com.lingualearn.pro.ui.components.GlassCard
import com.lingualearn.pro.ui.components.ProfileAvatar
import com.lingualearn.pro.ui.components.selectionSpotlight
import com.lingualearn.pro.ui.theme.GlassTileStrong
import com.lingualearn.pro.ui.theme.TextMuted
import com.lingualearn.pro.ui.theme.TextPrimary
import com.lingualearn.pro.ui.theme.VistaAccent
import com.lingualearn.pro.ui.theme.VistaGreen
import com.lingualearn.pro.ui.theme.VistaTeal

@Composable
fun CourseDashboardScreen(
    course: LanguageCourse,
    progress: ProgressState,
    displayName: String,
    photoUrl: String?,
    onNavigate: (Destination) -> Unit = {},
) {
    val nextUp = remember(course.id, progress.lessonsCompleted, progress.totalXp) {
        nextUpItems(course, progress)
    }
    Column(verticalArrangement = Arrangement.spacedBy(22.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                ProfileAvatar(
                    name = displayName,
                    photoUrl = photoUrl,
                    size = 56.dp,
                )
                Column(
                    Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = displayName,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = "${course.flag}  ${course.name}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        AeroProgressBar(
                            progress = progress.levelProgress,
                            color = VistaGreen,
                            modifier = Modifier.weight(1f),
                            height = 6.dp,
                        )
                        Text(
                            text = "${progress.xpIntoLevel}/${ProgressState.XP_PER_LEVEL}",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted,
                        )
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                HomeStatChip(
                    icon = Icons.Filled.LocalFireDepartment,
                    label = "${progress.currentStreak} day streak",
                    tint = VistaAccent,
                )
                HomeStatChip(
                    icon = Icons.Filled.Diamond,
                    label = "${progress.totalXp} XP · Lv ${progress.level}",
                    tint = VistaTeal,
                )
            }
        }

        Column(
            modifier = Modifier.padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = "NEXT UP",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(start = 4.dp, bottom = 4.dp),
            )
            nextUp.forEach { item ->
                NextUpRow(
                    item = item,
                    onClick = { onNavigate(item.destination) },
                )
            }
        }
    }
}

@Composable
private fun HomeStatChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    tint: Color,
) {
    GlassCard(shape = CircleShape, color = GlassTileStrong) {
        Row(
            Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(14.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = TextPrimary,
                maxLines = 1,
                modifier = Modifier.padding(start = 6.dp),
            )
        }
    }
}

private data class NextUpItem(
    val eyebrow: String,
    val title: String,
    val subtitle: String,
    val destination: Destination,
)

@Composable
private fun NextUpRow(
    item: NextUpItem,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            )
            .selectionSpotlight(
                selected = false,
                interactionSource = interactionSource,
                color = Color(0x557DD3FC),
            )
            .padding(horizontal = 4.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        CrystalCursor(glow = 0.82f)
        Text(
            text = item.eyebrow.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = Color(0x99A8D8EA),
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.2.sp,
            modifier = Modifier.widthIn(min = 72.dp),
        )
        Text(
            text = item.title,
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.35f),
            modifier = Modifier.size(18.dp),
        )
    }
}

private fun nextUpItems(course: LanguageCourse, progress: ProgressState): List<NextUpItem> {
    val lessons = SampleContent.courseLessons(course.id)
    val nextLesson = lessons.getOrElse(
        progress.lessonsCompleted.coerceIn(0, (lessons.size - 1).coerceAtLeast(0)),
    ) { lessons.first() }
    val started = progress.lessonsCompleted > 0 || progress.totalXp > 0
    val continueItem = NextUpItem(
        eyebrow = if (started) "Continue" else "Start here",
        title = nextLesson.title,
        subtitle = if (started) {
            "Pick up where you left off in ${course.name}."
        } else {
            "Begin your next ${course.name} lesson."
        },
        destination = if (nextLesson.opensGrammarLesson) Destination.Lesson else Destination.DailyLesson,
    )
    val drillItem = NextUpItem(
        eyebrow = "Drill",
        title = "Grammar drills",
        subtitle = "A short round of conjugations and sentence structure.",
        destination = Destination.GrammarDrills,
    )
    val questItem = NextUpItem(
        eyebrow = "Quest",
        title = "Word Pinball",
        subtitle = "Glass-orb pinball — the biggest XP drop in Lumina.",
        destination = Destination.VocabPinball,
    )
    return listOf(continueItem, questItem, drillItem)
}

@Composable
fun LessonsScreen(
    course: LanguageCourse,
    onOpenGrammarLesson: () -> Unit,
    onOpenDailyLesson: () -> Unit,
) {
    val lessons = SampleContent.courseLessons(course.id)
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        GlassCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                CardTitle("${course.flag}  ${course.name} Lessons")
                BodyText(
                    "Browse grammar and topic lessons for ${course.name}. Complete them to earn XP and build progress.",
                    color = TextMuted,
                )
            }
        }
        lessons.forEach { lesson ->
            GlassCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Badge(
                        "Lesson ${lesson.number} · ${lesson.level}",
                        course.accent.copy(alpha = 0.7f),
                    )
                    CardTitle(lesson.title)
                    BodyText(lesson.subtitle)
                    AeroButton(
                        text = if (lesson.opensGrammarLesson) "Start grammar lesson" else "Open lesson",
                        onClick = {
                            if (lesson.opensGrammarLesson) onOpenGrammarLesson()
                            else onOpenDailyLesson()
                        },
                        color = course.accent,
                    )
                }
            }
        }
    }
}
