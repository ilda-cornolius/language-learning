package com.lingualearn.pro.ui.widgets

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.lingualearn.pro.data.PhotoOfTheDay
import com.lingualearn.pro.data.PhotoOfTheDayRepository
import com.lingualearn.pro.data.ProgressState
import com.lingualearn.pro.ui.components.AeroProgressBar
import com.lingualearn.pro.ui.components.GlassCard
import com.lingualearn.pro.ui.theme.TextPrimary
import com.lingualearn.pro.ui.theme.TextSecondary
import com.lingualearn.pro.ui.theme.VistaAccent
import com.lingualearn.pro.ui.theme.VistaBlue
import com.lingualearn.pro.ui.theme.VistaGreen
import kotlinx.coroutines.delay
import java.util.Calendar
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CalendarWidget(modifier: Modifier = Modifier) {
    val now = remember { Calendar.getInstance() }
    val months = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December",
    )
    val days = listOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")

    GlassCard(modifier.fillMaxWidth(), color = VistaAccent) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(Brush.horizontalGradient(listOf(Color(0xFFC2410C), VistaAccent)))
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "${months[now.get(Calendar.MONTH)]} ${now.get(Calendar.YEAR)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White,
                )
            }
            Column(
                Modifier.padding(vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = now.get(Calendar.DAY_OF_MONTH).toString(),
                    style = MaterialTheme.typography.headlineSmall.copy(fontSize = MaterialTheme.typography.headlineSmall.fontSize * 1.8f),
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
                Text(
                    text = days[now.get(Calendar.DAY_OF_WEEK) - 1],
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xE6FFFFFF),
                )
            }
        }
    }
}

@Composable
fun DailyProgressWidget(progress: ProgressState, modifier: Modifier = Modifier) {
    GlassCard(modifier.fillMaxWidth()) {
        Column(
            Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = "Daily Progress",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
            )
            ProgressRow(
                "Level ${progress.level}",
                "${progress.xpIntoLevel}/${ProgressState.XP_PER_LEVEL} XP",
                progress.levelProgress,
                VistaGreen,
            )
            ProgressRow(
                "Lessons",
                "${progress.lessonsCompleted} completed",
                (progress.lessonsCompleted.coerceAtMost(10) / 10f),
                VistaBlue,
            )
        }
    }
}

@Composable
private fun ProgressRow(label: String, value: String, progress: Float, color: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            Text(value, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
        AeroProgressBar(progress, color, height = 8.dp)
    }
}

/** Analog clock that ticks with the device time, like the Vista sidebar gadget. */
@Composable
fun ClockWidget(modifier: Modifier = Modifier) {
    var time by remember { mutableStateOf(Calendar.getInstance()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            time = Calendar.getInstance()
        }
    }

    val hour = time.get(Calendar.HOUR)
    val minute = time.get(Calendar.MINUTE)
    val second = time.get(Calendar.SECOND)

    GlassCard(
        modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        shape = RoundedCornerShape(percent = 50),
    ) {
        Canvas(Modifier.fillMaxSize().padding(12.dp)) {
            val radius = size.minDimension / 2f
            val center = Offset(size.width / 2f, size.height / 2f)

            drawCircle(Color(0x4DFFFFFF), radius = radius, center = center, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4.dp.toPx()))

            fun hand(fraction: Float, length: Float, width: Float, color: Color) {
                val angle = Math.toRadians((fraction * 360f - 90f).toDouble())
                drawLine(
                    color = color,
                    start = center,
                    end = Offset(
                        center.x + (cos(angle) * radius * length).toFloat(),
                        center.y + (sin(angle) * radius * length).toFloat(),
                    ),
                    strokeWidth = width,
                    cap = StrokeCap.Round,
                )
            }

            hand((hour + minute / 60f) / 12f, 0.5f, 5.dp.toPx(), Color.White)
            hand((minute + second / 60f) / 60f, 0.72f, 3.dp.toPx(), Color.White)
            hand(second / 60f, 0.78f, 1.5.dp.toPx(), VistaAccent)
            drawCircle(Color.White, radius = 5.dp.toPx(), center = center)
        }
    }
}

@Composable
fun DestinationWidget(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var photo by remember { mutableStateOf(PhotoOfTheDayRepository.fallback()) }
    LaunchedEffect(Unit) {
        photo = PhotoOfTheDayRepository.load(context)
    }

    GlassCard(modifier.fillMaxWidth()) {
        Column {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(Color(0x33000000)),
                contentAlignment = Alignment.Center,
            ) {
                val current = photo
                val request = remember(current.model) {
                    ImageRequest.Builder(context)
                        .data(current.model)
                        .crossfade(true)
                        .build()
                }
                SubcomposeAsyncImage(
                    model = request,
                    contentDescription = current.caption,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    loading = {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(28.dp),
                                color = Color.White,
                                strokeWidth = 2.dp,
                            )
                        }
                    },
                    error = {
                        SubcomposeAsyncImage(
                            model = "file:///android_asset/photo_of_the_day_fallback.jpg",
                            contentDescription = current.caption,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                        )
                    },
                )
            }
            Text(
                text = photo.caption,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(10.dp),
            )
        }
    }
}
