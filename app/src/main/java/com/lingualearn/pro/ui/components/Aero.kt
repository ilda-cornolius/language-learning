package com.lingualearn.pro.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.ui.text.TextStyle
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.lingualearn.pro.ui.theme.GlassBorder
import com.lingualearn.pro.ui.theme.GlassPanel
import com.lingualearn.pro.ui.theme.GlassTile as GlassTileColor
import com.lingualearn.pro.ui.theme.TextMuted
import com.lingualearn.pro.ui.theme.TextPrimary
import com.lingualearn.pro.ui.theme.TextSecondary
import com.lingualearn.pro.ui.theme.VistaAccent
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

/** Shared blur source for frosted Aero panels. Null when outside [AeroBackground]. */
val LocalHazeState = compositionLocalOf<HazeState?> { null }
val LocalDarkMode = compositionLocalOf { true }

/**
 * App wallpaper: night Lumina field in dark mode, Vista Aero gradient otherwise.
 * Also hosts the Haze blur source so glass panels can frost this backdrop.
 */
@Composable
fun AeroBackground(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val hazeState = rememberHazeState()
    val darkMode = LocalDarkMode.current
    CompositionLocalProvider(LocalHazeState provides hazeState) {
        Box(modifier = modifier.fillMaxSize()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .hazeSource(state = hazeState)
            ) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(
                            if (darkMode) {
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFF05070D),
                                        Color(0xFF0B1524),
                                        Color(0xFF071018),
                                    ),
                                )
                            } else {
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF38BDF8),
                                        Color(0xFF34D399),
                                        Color(0xFF2563EB),
                                    ),
                                )
                            }
                        )
                )
                Canvas(Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    fun ribbon(startY: Float, amplitude: Float, thickness: Float, color: Color) {
                        val path = Path().apply {
                            moveTo(-w * 0.1f, startY)
                            cubicTo(
                                w * 0.25f, startY - amplitude,
                                w * 0.65f, startY + amplitude,
                                w * 1.1f, startY - amplitude * 0.4f,
                            )
                            lineTo(w * 1.1f, startY - amplitude * 0.4f + thickness)
                            cubicTo(
                                w * 0.65f, startY + amplitude + thickness,
                                w * 0.25f, startY - amplitude + thickness,
                                -w * 0.1f, startY + thickness,
                            )
                            close()
                        }
                        drawPath(path, color)
                    }

                    if (darkMode) {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0x3378C8E8), Color.Transparent),
                                center = Offset(w * 0.5f, h * 0.18f),
                                radius = w * 0.85f,
                            ),
                            radius = w * 0.85f,
                            center = Offset(w * 0.5f, h * 0.18f),
                        )
                        ribbon(h * 0.28f, h * 0.10f, h * 0.06f, Color(0x14FFFFFF))
                        ribbon(h * 0.62f, h * 0.12f, h * 0.05f, Color(0x1478C8E8))
                        drawRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color(0x66000000)),
                            ),
                            size = Size(w, h),
                        )
                    } else {
                        ribbon(h * 0.22f, h * 0.14f, h * 0.10f, Color(0x33FFFFFF))
                        ribbon(h * 0.48f, h * 0.18f, h * 0.07f, Color(0x2600E5FF))
                        ribbon(h * 0.74f, h * 0.12f, h * 0.12f, Color(0x1FE6D54A))

                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0x40FFFFFF), Color.Transparent),
                                center = Offset(w * 0.3f, h * 0.15f),
                                radius = w * 0.7f,
                            ),
                            radius = w * 0.7f,
                            center = Offset(w * 0.3f, h * 0.15f),
                        )
                        drawRect(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0x1AFFFFFF), Color.Transparent, Color(0x3300E5FF)),
                            ),
                            size = Size(w, h),
                        )
                    }
                }
            }
            content()
        }
    }
}

/** Theater spotlight behind a pressed or selected control. */
@Composable
fun Modifier.selectionSpotlight(
    selected: Boolean = false,
    interactionSource: MutableInteractionSource,
    color: Color = Color(0xCCF4FBFF),
): Modifier {
    val pressed by interactionSource.collectIsPressedAsState()
    val intensity by animateFloatAsState(
        targetValue = when {
            pressed -> 1f
            selected -> 0.58f
            else -> 0f
        },
        animationSpec = tween(durationMillis = if (pressed) 90 else 340),
        label = "selection-spotlight",
    )
    return drawWithContent {
        drawContent()
        if (intensity <= 0.01f) return@drawWithContent
        val center = Offset(size.width * 0.5f, size.height * 0.08f)
        val radius = size.maxDimension * (0.72f + 0.30f * intensity)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.55f * intensity),
                    color.copy(alpha = 0.34f * intensity),
                    Color.Transparent,
                ),
                center = center,
                radius = radius,
            ),
            center = center,
            radius = radius,
            blendMode = BlendMode.Screen,
        )
    }
}

/** Frosted panel — real backdrop blur via Haze when available, tinted scrim otherwise. */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    color: Color = GlassPanel,
    onClick: (() -> Unit)? = null,
    selected: Boolean = false,
    spotlightColor: Color = Color(0xCCF4FBFF),
    content: @Composable () -> Unit,
) {
    val hazeState = LocalHazeState.current
    val interactionSource = remember { MutableInteractionSource() }
    // Only frost translucent panels; solid colours (calendar orange, etc.) stay opaque.
    val frosted = hazeState != null && color.alpha in 0.08f..0.92f

    Box(
        modifier = modifier
            .clip(shape)
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClick,
                    )
                } else {
                    Modifier
                },
            )
            .selectionSpotlight(selected, interactionSource, spotlightColor)
            .then(
                if (frosted) {
                    Modifier.hazeEffect(
                        state = hazeState!!,
                        style = HazeStyle(
                            backgroundColor = color,
                            tints = listOf(HazeTint(color)),
                            blurRadius = 18.dp,
                            noiseFactor = 0.08f,
                        ),
                    )
                } else {
                    Modifier.background(color, shape)
                }
            )
            .border(1.dp, GlassBorder.copy(alpha = 0.35f), shape),
    ) {
        content()
    }
}

/** Lighter inner tile, equivalent of `bg-white/10 rounded-lg`. */
@Composable
fun GlassTile(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(12.dp),
    color: Color = GlassTileColor,
    onClick: (() -> Unit)? = null,
    selected: Boolean = false,
    content: @Composable () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    Surface(
        modifier = modifier
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClick,
                    )
                } else {
                    Modifier
                },
            )
            .selectionSpotlight(selected, interactionSource),
        shape = shape,
        color = color,
        contentColor = TextPrimary,
        content = content,
    )
}

/** Shared ice-glass shell used by pills and Next up actions. */
@Composable
fun AeroGlassPanel(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(percent = 50),
    tint: Color = Color(0xFF7DD3FC),
    content: @Composable BoxScope.() -> Unit,
) {
    val darkMode = LocalDarkMode.current
    val hazeState = LocalHazeState.current
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val press by animateFloatAsState(
        targetValue = if (pressed) 1f else 0f,
        animationSpec = tween(90),
        label = "aero-glass-press",
    )
    val ice = if (darkMode) Color(0x147DD3FC) else Color(0x28F0FDFF)
    val frosted = hazeState != null && ice.alpha in 0.04f..0.92f

    Box(
        modifier
            .graphicsLayer {
                alpha = if (enabled) 1f else 0.42f
                val s = 1f - 0.03f * press
                scaleX = s
                scaleY = s
            }
            .drawBehind {
                val radius = CornerRadius(size.height / 2f, size.height / 2f)
                drawRoundRect(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF67E8F9).copy(alpha = (0.10f + 0.10f * press) * (if (darkMode) 1f else 0.4f)),
                            tint.copy(alpha = 0.06f + 0.06f * press),
                            Color.Transparent,
                        ),
                        center = Offset(size.width / 2f, size.height * 0.35f),
                        radius = size.maxDimension * 0.85f,
                    ),
                    cornerRadius = radius,
                )
            }
            .clip(shape)
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        enabled = enabled,
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClick,
                    )
                } else {
                    Modifier
                },
            )
            .selectionSpotlight(
                selected = false,
                interactionSource = interactionSource,
                color = Color(0xCCE0F9FF),
            )
            .then(
                if (frosted) {
                    Modifier.hazeEffect(
                        state = hazeState!!,
                        style = HazeStyle(
                            backgroundColor = ice,
                            tints = listOf(
                                HazeTint(ice),
                                HazeTint(Color(0x227DD3FC)),
                                HazeTint(tint.copy(alpha = 0.06f)),
                            ),
                            blurRadius = 16.dp,
                            noiseFactor = 0.02f,
                        ),
                    )
                } else {
                    Modifier.background(ice, shape)
                },
            )
            .border(
                1.dp,
                Brush.verticalGradient(
                    0f to Color.White.copy(alpha = 0.42f),
                    0.45f to tint.copy(alpha = 0.38f),
                    1f to Color.White.copy(alpha = 0.10f),
                ),
                shape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        0.00f to Color.White.copy(alpha = if (darkMode) 0.10f else 0.22f),
                        0.28f to Color(0x147DD3FC),
                        0.70f to tint.copy(alpha = 0.08f),
                        1.00f to Color.White.copy(alpha = 0.03f),
                    ),
                ),
        )
        Box(
            Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .fillMaxHeight(0.46f)
                .padding(start = 5.dp, end = 5.dp, top = 2.dp)
                .clip(shape)
                .background(
                    Brush.verticalGradient(
                        0f to Color.White.copy(alpha = 0.22f),
                        0.45f to Color.White.copy(alpha = 0.06f),
                        1f to Color.Transparent,
                    ),
                ),
        )
        Box(
            Modifier
                .matchParentSize()
                .padding(1.dp)
                .border(1.dp, Color.White.copy(alpha = 0.12f), shape),
        )
        content()
    }
}

/** Pulsing crystal pointer used by command buttons. */
@Composable
fun CrystalCursor(
    glow: Float,
    tint: Color = Color(0xFF7DD3FC),
    modifier: Modifier = Modifier,
) {
    Canvas(
        modifier
            .size(width = 10.dp, height = 14.dp)
            .graphicsLayer { alpha = glow.coerceIn(0.2f, 1f) },
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
                listOf(tint, Color.White, Color(0xFFE0F9FF)),
            ),
        )
    }
}

/** Translucent Final Fantasy command plate — crystal cursor, tracked type, thin rim. */
@Composable
fun AeroButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = VistaAccent,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    enabled: Boolean = true,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val pulse by rememberInfiniteTransition(label = "ff-command").animateFloat(
        initialValue = 0.48f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "ff-command-pulse",
    )
    val glow = when {
        !enabled -> 0.28f
        pressed -> 1f
        else -> pulse
    }
    val shape = RoundedCornerShape(3.dp)
    val label = text.uppercase()

    Box(
        modifier
            .graphicsLayer {
                alpha = if (enabled) 1f else 0.42f
                val s = 1f - 0.02f * if (pressed) 1f else 0f
                scaleX = s
                scaleY = s
            }
            .drawBehind {
                drawRoundRect(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            color.copy(alpha = 0.16f + 0.18f * glow),
                            Color.Transparent,
                        ),
                        center = Offset(size.width * 0.18f, size.height * 0.5f),
                        radius = size.maxDimension * 0.7f,
                    ),
                    cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx()),
                )
            }
            .clip(shape)
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            )
            .background(
                Brush.verticalGradient(
                    0f to Color.White.copy(alpha = 0.08f + 0.05f * glow),
                    0.55f to color.copy(alpha = 0.07f + 0.05f * glow),
                    1f to Color.White.copy(alpha = 0.03f),
                ),
            )
            .border(
                1.dp,
                Brush.verticalGradient(
                    0f to Color.White.copy(alpha = 0.38f + 0.22f * glow),
                    0.5f to color.copy(alpha = 0.42f + 0.22f * glow),
                    1f to Color.White.copy(alpha = 0.10f),
                ),
                shape,
            )
            .padding(contentPadding),
        contentAlignment = Alignment.Center,
    ) {
        CompositionLocalProvider(LocalContentColor provides Color.White.copy(alpha = 0.9f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                if (leadingIcon != null) {
                    leadingIcon()
                    Spacer(Modifier.width(8.dp))
                } else {
                    CrystalCursor(glow = glow, tint = color)
                    Spacer(Modifier.width(10.dp))
                }
                Text(
                    text = label,
                    style = textStyle.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Light,
                        letterSpacing = 2.4.sp,
                        shadow = Shadow(
                            color = color.copy(alpha = 0.28f + 0.42f * glow),
                            offset = Offset.Zero,
                            blurRadius = 12f,
                        ),
                    ),
                    color = Color.White.copy(alpha = 0.72f + 0.28f * glow),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (trailingIcon != null) {
                    Spacer(Modifier.width(8.dp))
                    trailingIcon()
                }
            }
        }
    }
}

/** Translucent input matching `bg-white/10 border border-white/20 focus:border-vista-accent`. */
@Composable
fun GlassTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
) {
    var focused by remember { mutableStateOf(false) }
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = singleLine,
        textStyle = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary),
        cursorBrush = SolidColor(VistaAccent),
        modifier = modifier.onFocusChanged { focused = it.isFocused },
        decorationBox = { inner ->
            Box(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(GlassTileColor)
                    .border(
                        width = 1.dp,
                        color = if (focused) VistaAccent else GlassBorder,
                        shape = RoundedCornerShape(10.dp),
                    )
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                contentAlignment = if (singleLine) Alignment.CenterStart else Alignment.TopStart,
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted,
                    )
                }
                inner()
            }
        },
    )
}

@Composable
fun Badge(text: String, color: Color, modifier: Modifier = Modifier) {
    val darkMode = LocalDarkMode.current
    val fill = if (darkMode) {
        color.copy(alpha = (color.alpha * 0.42f).coerceIn(0.28f, 0.5f))
    } else {
        color
    }
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(6.dp),
        color = fill,
        contentColor = Color.White,
        border = if (darkMode) BorderStroke(1.dp, Color.White.copy(alpha = 0.18f)) else null,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
        )
    }
}

@Composable
fun ScreenTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineSmall,
        color = TextPrimary,
        modifier = modifier,
    )
}

@Composable
fun CardTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = TextPrimary,
        modifier = modifier,
    )
}

@Composable
fun BodyText(text: String, modifier: Modifier = Modifier, color: Color = TextSecondary) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = color,
        modifier = modifier,
    )
}

@Composable
fun AeroProgressBar(
    progress: Float,
    color: Color,
    modifier: Modifier = Modifier,
    height: Dp = 10.dp,
) {
    Box(
        modifier
            .fillMaxWidth()
            .height(height)
            .clip(CircleShape)
            .background(Color(0x33FFFFFF))
    ) {
        Box(
            Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .height(height)
                .clip(CircleShape)
                .background(
                    Brush.verticalGradient(listOf(color.copy(alpha = 0.95f), color))
                )
        )
    }
}

/**
 * Stand-in for the mockup's remote avatar images: a tinted circle with initials,
 * derived from the handle so each user keeps a stable colour.
 */
@Composable
fun InitialsAvatar(name: String, size: Dp, modifier: Modifier = Modifier) {
    val palette = listOf(VistaAccent, Color(0xFF2ABBC4), Color(0xFF6DC067), Color(0xFF1E6BA8), Color(0xFF9C6ADE))
    val tint = palette[(name.hashCode().let { if (it < 0) -it else it }) % palette.size]
    val initials = name.trimStart('@')
        .split(' ', '_')
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercase() }

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(Brush.linearGradient(listOf(tint.copy(alpha = 0.95f), tint.copy(alpha = 0.6f)))),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = initials,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            style = if (size < 32.dp) MaterialTheme.typography.bodySmall else MaterialTheme.typography.bodyMedium,
        )
    }
}

/** Circular avatar that prefers a remote Google photo URL, then falls back to initials. */
@Composable
fun ProfileAvatar(
    name: String,
    photoUrl: String?,
    size: Dp,
    modifier: Modifier = Modifier,
) {
    if (photoUrl.isNullOrBlank()) {
        InitialsAvatar(name = name, size = size, modifier = modifier)
        return
    }
    SubcomposeAsyncImage(
        model = photoUrl,
        contentDescription = "$name profile photo",
        contentScale = ContentScale.Crop,
        modifier = modifier
            .size(size)
            .clip(CircleShape),
        loading = {
            InitialsAvatar(name = name, size = size)
        },
        error = {
            InitialsAvatar(name = name, size = size)
        },
    )
}

@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = TextMuted,
        modifier = modifier,
    )
}

@Composable
fun BulletList(items: List<String>, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        items.forEach { BodyText("•  $it") }
    }
}
