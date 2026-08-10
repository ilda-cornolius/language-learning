package com.lingualearn.pro.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.lingualearn.pro.data.LanguageCourse
import com.lingualearn.pro.data.ProgressState
import com.lingualearn.pro.data.ProgressStore
import com.lingualearn.pro.data.SampleContent
import com.lingualearn.pro.ui.Destination
import com.lingualearn.pro.ui.components.AeroButton
import com.lingualearn.pro.ui.components.AeroProgressBar
import com.lingualearn.pro.ui.components.Badge
import com.lingualearn.pro.ui.components.BodyText
import com.lingualearn.pro.ui.components.CardTitle
import com.lingualearn.pro.ui.components.GlassCard
import com.lingualearn.pro.ui.components.GlassTile
import com.lingualearn.pro.ui.theme.TextMuted
import com.lingualearn.pro.ui.theme.TextPrimary
import com.lingualearn.pro.ui.widgets.CalendarWidget
import com.lingualearn.pro.ui.widgets.ClockWidget
import com.lingualearn.pro.ui.widgets.DailyProgressWidget

@Composable
fun CourseDashboardScreen(
    course: LanguageCourse,
    progress: ProgressState,
    progressStore: ProgressStore,
    onNavigate: (Destination) -> Unit = {},
) {
    val coursePct = progressStore.courseProgress(course.id)
    val lessonDestinations = listOf(
        Destination.DailyLesson,
        Destination.Practice,
        Destination.Challenges,
    )
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CalendarWidget(Modifier.weight(1f))
            ClockWidget(Modifier.weight(1f))
        }
        DailyProgressWidget(progress)

        GlassCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    CardTitle("Current Level: ${course.level}")
                    Text(
                        text = course.flag,
                        style = MaterialTheme.typography.titleLarge,
                    )
                }
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    BodyText("Progress")
                    BodyText("$coursePct%")
                }
                AeroProgressBar(
                    progress = coursePct / 100f,
                    color = course.accent,
                    height = 12.dp,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AeroButton("Lessons", onClick = { onNavigate(Destination.Lessons) }, color = course.accent)
                    AeroButton("Daily Lesson", onClick = { onNavigate(Destination.DailyLesson) }, color = course.accent)
                    AeroButton("Practice", onClick = { onNavigate(Destination.Practice) }, color = course.accent)
                }
                AeroButton("Challenges", onClick = { onNavigate(Destination.Challenges) }, color = course.accent)
            }
        }

        GlassCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                CardTitle("Lessons")
                BodyText("Structured ${course.name} lessons for your level.", color = TextMuted)
                SampleContent.courseLessons(course.id).take(3).forEach { lesson ->
                    GlassTile(
                        Modifier.fillMaxWidth(),
                        onClick = {
                            onNavigate(
                                if (lesson.opensGrammarLesson) Destination.Lesson else Destination.DailyLesson,
                            )
                        },
                    ) {
                        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Lesson ${lesson.number} · ${lesson.title}",
                                color = TextPrimary,
                                style = MaterialTheme.typography.bodyLarge,
                            )
                            BodyText(lesson.subtitle, color = TextMuted)
                        }
                    }
                }
                AeroButton(
                    "View all lessons",
                    onClick = { onNavigate(Destination.Lessons) },
                    color = Color(0xFF526777),
                )
            }
        }

        GlassCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                CardTitle("Recent Lessons")
                course.recentLessons.forEachIndexed { index, title ->
                    val destination = lessonDestinations[index % lessonDestinations.size]
                    GlassTile(
                        Modifier.fillMaxWidth(),
                        onClick = { onNavigate(destination) },
                    ) {
                        BodyText(
                            text = "•  $title",
                            color = TextPrimary,
                            modifier = Modifier.padding(12.dp),
                        )
                    }
                }
            }
        }
    }
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
