package com.lingualearn.pro.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lingualearn.pro.data.LanguageCourse
import com.lingualearn.pro.data.SampleContent
import com.lingualearn.pro.ui.components.BodyText
import com.lingualearn.pro.ui.components.CinematicBackground
import com.lingualearn.pro.ui.components.CinematicCommandButton
import com.lingualearn.pro.ui.components.CinematicFadeIn
import com.lingualearn.pro.ui.components.selectionSpotlight
import com.lingualearn.pro.ui.theme.TextMuted
import com.lingualearn.pro.ui.theme.TextPrimary
import kotlinx.coroutines.delay

@Composable
fun LanguageOnboardingScreen(
    onFinished: (Set<String>) -> Unit,
) {
    var phase by remember { mutableIntStateOf(0) }
    var selected by remember { mutableStateOf(setOf<String>()) }
    val courses = SampleContent.allCourses

    LaunchedEffect(Unit) {
        delay(350)
        phase = 1
        delay(800)
        phase = 2
        delay(700)
        phase = 3
        delay(900)
        phase = 4
    }

    CinematicBackground {
        Column(
            Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CinematicFadeIn(visible = phase >= 1, durationMillis = 1500, rise = 14.dp) {
                Text(
                    text = "What languages are you learning?",
                    color = TextPrimary,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Light,
                    fontSize = 28.sp,
                    lineHeight = 34.sp,
                    letterSpacing = 0.8.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            CinematicFadeIn(
                visible = phase >= 2,
                durationMillis = 1300,
                rise = 10.dp,
                modifier = Modifier.padding(top = 10.dp, bottom = 18.dp),
            ) {
                Text(
                    text = "Choose one or more. You can add more later.",
                    color = TextMuted,
                    fontFamily = FontFamily.Serif,
                    fontSize = 15.sp,
                    letterSpacing = 0.6.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            CinematicFadeIn(
                visible = phase >= 3,
                durationMillis = 1200,
                rise = 16.dp,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 148.dp),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    itemsIndexed(courses, key = { _, course -> course.id }) { index, course ->
                        LanguageChoiceTile(
                            course = course,
                            selected = course.id in selected,
                            appear = phase >= 3,
                            appearDelayMs = 70 * index,
                            onClick = {
                                selected = if (course.id in selected) {
                                    selected - course.id
                                } else {
                                    selected + course.id
                                }
                            },
                        )
                    }
                }
            }
            CinematicFadeIn(
                visible = phase >= 4,
                durationMillis = 1200,
                rise = 12.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
            ) {
                val enabled = selected.isNotEmpty()
                CinematicCommandButton(
                    text = if (enabled) "Begin" else "Select a language",
                    onClick = { onFinished(selected) },
                    enabled = enabled,
                )
            }
        }
    }
}

@Composable
private fun LanguageChoiceTile(
    course: LanguageCourse,
    selected: Boolean,
    appear: Boolean,
    appearDelayMs: Int,
    onClick: () -> Unit,
) {
    CinematicFadeIn(
        visible = appear,
        delayMillis = appearDelayMs,
        durationMillis = 900,
        rise = 10.dp,
    ) {
        val interactionSource = remember { MutableInteractionSource() }
        val border = if (selected) course.accent.copy(alpha = 0.95f) else Color(0x40FFFFFF)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick,
                )
                .selectionSpotlight(
                    selected = selected,
                    interactionSource = interactionSource,
                    color = course.accent,
                )
                .border(1.dp, border, RoundedCornerShape(12.dp))
                .padding(vertical = 16.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(course.flag, fontSize = 28.sp)
            Text(
                text = course.name,
                color = TextPrimary,
                fontWeight = FontWeight.Medium,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
            if (course.nativeLabel.isNotBlank()) {
                BodyText(course.nativeLabel, color = TextMuted)
            }
        }
    }
}
