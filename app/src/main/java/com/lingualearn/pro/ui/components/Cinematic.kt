package com.lingualearn.pro.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

/**
 * Final Fantasy XIII–style title hold: black field, then a slow white-cyan bloom.
 */
@Composable
fun CinematicBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val bloom by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 2600, easing = LinearOutSlowInEasing),
        label = "cinematic-bloom",
    )
    val hazeState = rememberHazeState()
    CompositionLocalProvider(LocalHazeState provides hazeState) {
        Box(modifier.fillMaxSize()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .hazeSource(state = hazeState)
                    .background(Color.Black),
            ) {
                Canvas(Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val center = Offset(w * 0.5f, h * 0.38f)
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0x66F4FBFF).copy(alpha = 0.42f * bloom),
                                Color(0x3378C8E8).copy(alpha = 0.22f * bloom),
                                Color.Transparent,
                            ),
                            center = center,
                            radius = w * (0.55f + 0.25f * bloom),
                        ),
                        radius = w * (0.55f + 0.25f * bloom),
                        center = center,
                    )
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.15f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.72f),
                            ),
                        ),
                    )
                }
            }
            content()
        }
    }
}

/** Slow dissolve plus a slight rise, used throughout title and onboarding. */
@Composable
fun CinematicFadeIn(
    visible: Boolean,
    modifier: Modifier = Modifier,
    delayMillis: Int = 0,
    durationMillis: Int = 1400,
    rise: Dp = 18.dp,
    content: @Composable () -> Unit,
) {
    val density = LocalDensity.current
    val risePx = with(density) { rise.roundToPx() }
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn(
            animationSpec = tween(durationMillis, delayMillis, FastOutSlowInEasing),
        ) + slideInVertically(
            animationSpec = tween(durationMillis, delayMillis, FastOutSlowInEasing),
            initialOffsetY = { risePx },
        ),
        exit = fadeOut(animationSpec = tween(600, easing = FastOutSlowInEasing)),
    ) {
        content()
    }
}

/** Logo dissolve with a gentle scale-up, like a Square Enix title card. */
@Composable
fun CinematicLogoFade(
    visible: Boolean,
    modifier: Modifier = Modifier,
    delayMillis: Int = 0,
    content: @Composable () -> Unit,
) {
    val progress by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(
            durationMillis = 1800,
            delayMillis = delayMillis,
            easing = FastOutSlowInEasing,
        ),
        label = "cinematic-logo",
    )
    Box(
        modifier.graphicsLayer {
            alpha = progress
            val scale = 0.88f + 0.12f * progress
            scaleX = scale
            scaleY = scale
        },
    ) {
        content()
    }
}

fun Modifier.cinematicGlow(color: Color = Color(0x88FFFFFF), radius: Dp = 48.dp): Modifier =
    drawBehind {
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(color, Color.Transparent),
            ),
            radius = radius.toPx(),
        )
    }

/**
 * Square Enix title command: compact, tracked type, pulsing crystal cursor.
 */
@Composable
fun CinematicCommandButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val pulse by rememberInfiniteTransition(label = "ff-cursor").animateFloat(
        initialValue = 0.42f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "ff-cursor-pulse",
    )
    val glow = when {
        !enabled -> 0.28f
        pressed -> 1f
        else -> pulse
    }
    val label = text.uppercase()

    Column(
        modifier
            .graphicsLayer { alpha = if (enabled) 1f else 0.42f }
            .clip(RoundedCornerShape(2.dp))
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Canvas(
                Modifier
                    .size(width = 11.dp, height = 16.dp)
                    .graphicsLayer { alpha = glow },
            ) {
                val crystal = Path().apply {
                    moveTo(0f, size.height / 2f)
                    lineTo(size.width * 0.38f, 0f)
                    lineTo(size.width, size.height / 2f)
                    lineTo(size.width * 0.38f, size.height)
                    close()
                }
                drawPath(
                    crystal,
                    brush = Brush.horizontalGradient(
                        listOf(Color(0xFF7DD3FC), Color.White, Color(0xFFE0F9FF)),
                    ),
                )
            }
            Spacer(Modifier.width(12.dp))
            Text(
                text = label,
                color = Color.White.copy(alpha = 0.72f + 0.28f * glow),
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Light,
                fontSize = 15.sp,
                letterSpacing = 7.sp,
                textAlign = TextAlign.Center,
                style = TextStyle(
                    shadow = Shadow(
                        color = Color(0xAA7DD3FC).copy(alpha = 0.35f + 0.45f * glow),
                        offset = Offset(0f, 0f),
                        blurRadius = 14f,
                    ),
                ),
            )
        }
        Spacer(Modifier.height(7.dp))
        Box(
            Modifier
                .width((label.length * 11).dp.coerceIn(72.dp, 220.dp))
                .height(1.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.18f + 0.45f * glow),
                            Color.Transparent,
                        ),
                    ),
                ),
        )
    }
}
