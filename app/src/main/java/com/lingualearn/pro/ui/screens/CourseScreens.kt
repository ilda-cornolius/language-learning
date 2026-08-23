package com.lingualearn.pro.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.lingualearn.pro.data.LanguageCourse
import com.lingualearn.pro.data.PreferencesStore
import com.lingualearn.pro.data.ProgressState
import com.lingualearn.pro.data.SampleContent
import com.lingualearn.pro.ui.Destination
import com.lingualearn.pro.ui.components.AeroButton
import com.lingualearn.pro.ui.components.Badge
import com.lingualearn.pro.ui.components.BodyText
import com.lingualearn.pro.ui.components.CardTitle
import com.lingualearn.pro.ui.components.GlassCard
import com.lingualearn.pro.ui.components.ProfileAvatar
import com.lingualearn.pro.ui.components.selectionSpotlight
import com.lingualearn.pro.ui.theme.TextMuted
import com.lingualearn.pro.ui.theme.TextPrimary
import com.lingualearn.pro.ui.widgets.DailyProgressWidget
import java.util.Calendar
import kotlin.math.absoluteValue

@Composable
fun CourseDashboardScreen(
    course: LanguageCourse,
    progress: ProgressState,
    displayName: String,
    photoUrl: String?,
    preferencesStore: PreferencesStore,
    onNavigate: (Destination) -> Unit = {},
    onFlashcardSessionComplete: (reviews: Int) -> Unit = {},
) {
    val nextUp = remember(course.id, progress.lessonsCompleted, progress.totalXp) {
        nextUpItems(course, progress)
    }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ProfileAvatar(
                name = displayName,
                photoUrl = photoUrl,
                size = 52.dp,
            )
            Column(
                Modifier.weight(0.9f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = displayName,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                BodyText("${course.flag}  ${course.name}", color = TextMuted)
            }
            DailyProgressWidget(
                progress = progress,
                modifier = Modifier.weight(1.1f),
                framed = false,
            )
        }

        FlashcardStudyModule(
            course = course,
            preferencesStore = preferencesStore,
            onEmptyAction = { onNavigate(Destination.Flashcards) },
            onEmptyActionLabel = "Open deck",
            onSessionComplete = onFlashcardSessionComplete,
            framed = false,
        )

        GlassCard(Modifier.fillMaxWidth(), color = Color(0x24101C2E)) {
            Box {
                Box(
                    Modifier
                        .matchParentSize()
                        .background(
                            Brush.verticalGradient(
                                0f to Color(0x14A5F3FC),
                                0.22f to Color(0x107DD3FC),
                                0.62f to Color(0x18081C2E),
                                1f to Color(0x66050A12),
                            ),
                        ),
                )
                Canvas(Modifier.matchParentSize()) {
                    val w = size.width
                    val h = size.height
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0x18FFFFFF), Color.Transparent),
                            center = Offset(w * 0.88f, h * 0.08f),
                            radius = w * 0.26f,
                        ),
                        radius = w * 0.26f,
                        center = Offset(w * 0.88f, h * 0.08f),
                    )
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0x147DD3FC), Color.Transparent),
                            center = Offset(w * 0.12f, h * 0.92f),
                            radius = w * 0.2f,
                        ),
                        radius = w * 0.2f,
                        center = Offset(w * 0.12f, h * 0.92f),
                    )
                }
                Column {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color(0x22FFFFFF),
                                        Color(0x1414B8A6),
                                        Color(0x22050A12),
                                    ),
                                ),
                            )
                            .padding(vertical = 5.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "Next up",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                shadow = Shadow(Color(0x447DD3FC), Offset(0f, 1f), 8f),
                            ),
                            color = Color(0xDEE8F4FF),
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                    Column(
                        Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        nextUp.forEachIndexed { index, item ->
                            NextUpAeroTile(
                                item = item,
                                orbTint = nextUpOrbTints[index % nextUpOrbTints.size],
                                onClick = { onNavigate(item.destination) },
                            )
                        }
                    }
                }
            }
        }
    }
}

private data class NextUpItem(
    val eyebrow: String,
    val title: String,
    val subtitle: String,
    val destination: Destination,
)

private val nextUpOrbTints = listOf(
    Color(0xFF7DD3FC),
    Color(0xFF5EEAD4),
    Color(0xFF93C5FD),
)

@Composable
private fun NextUpAeroTile(
    item: NextUpItem,
    orbTint: Color,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(12.dp)

    Box(
        Modifier
            .fillMaxWidth()
            .clip(shape)
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
            .background(
                Brush.verticalGradient(
                    0f to Color.White.copy(alpha = 0.08f),
                    0.42f to Color.White.copy(alpha = 0.03f),
                    1f to orbTint.copy(alpha = 0.06f),
                ),
            )
            .border(
                1.dp,
                Brush.verticalGradient(
                    0f to Color.White.copy(alpha = 0.22f),
                    0.55f to Color.White.copy(alpha = 0.08f),
                    1f to orbTint.copy(alpha = 0.16f),
                ),
                shape,
            )
            .padding(horizontal = 10.dp, vertical = 7.dp),
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AeroOrb(tint = orbTint, modifier = Modifier.size(22.dp))
            Column(
                Modifier
                    .weight(1f)
                    .padding(start = 10.dp, end = 6.dp),
            ) {
                Text(
                    text = item.eyebrow,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0x99A8D8EA),
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        shadow = Shadow(Color(0x337DD3FC), Offset(0f, 1f), 6f),
                    ),
                    color = Color(0xE6E8F4FF),
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.42f),
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

@Composable
private fun AeroOrb(tint: Color, modifier: Modifier = Modifier) {
    Canvas(modifier.size(22.dp)) {
        val c = Offset(size.width * 0.38f, size.height * 0.32f)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.55f),
                    tint.copy(alpha = 0.7f),
                    tint.copy(alpha = 0.22f),
                ),
                center = c,
                radius = size.minDimension * 0.78f,
            ),
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color.White.copy(alpha = 0.45f), Color.Transparent),
                center = c,
                radius = size.minDimension * 0.3f,
            ),
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.18f),
            style = Stroke(width = 1.2.dp.toPx()),
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
    val activities = listOf(
        NextUpItem(
            "Activity",
            "Conversation",
            "Talk through a short real-life scenario.",
            Destination.Conversation,
        ),
        NextUpItem(
            "Activity",
            "Listening",
            "Train your ear with a short clip.",
            Destination.Listening,
        ),
        NextUpItem(
            "Activity",
            "Challenge",
            "A timed round to earn XP.",
            Destination.Challenges,
        ),
    )
    val day = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
    val activityItem = activities[(day + course.id.hashCode().absoluteValue) % activities.size]
    return listOf(continueItem, drillItem, activityItem)
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
