package com.lingualearn.pro.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.ui.text.TextStyle
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
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
/**
 * Vista-style desktop wallpaper: a blue/green gradient with translucent light
 * ribbons sweeping across it. Also hosts the Haze blur source so glass panels
 * can frost this backdrop.
 */
@Composable
fun AeroBackground(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val hazeState = rememberHazeState()
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
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF38BDF8),
                                    Color(0xFF34D399),
                                    Color(0xFF2563EB),
                                ),
                            )
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
            content()
        }
    }
}

/** Frosted panel — real backdrop blur via Haze when available, tinted scrim otherwise. */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    color: Color = GlassPanel,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val hazeState = LocalHazeState.current
    // Only frost translucent panels; solid colours (calendar orange, etc.) stay opaque.
    val frosted = hazeState != null && color.alpha in 0.08f..0.92f

    Box(
        modifier = modifier
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .clip(shape)
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
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = if (onClick != null) modifier.clickable(onClick = onClick) else modifier,
        shape = shape,
        color = color,
        contentColor = TextPrimary,
        content = content,
    )
}

/** Glossy pill button with the Aero top highlight. */
@Composable
fun AeroButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = VistaAccent,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = color,
        contentColor = Color.White,
        shadowElevation = 4.dp,
        border = BorderStroke(1.dp, Color(0x40FFFFFF)),
    ) {
        Row(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        0f to Color(0x4DFFFFFF),
                        0.45f to Color(0x0DFFFFFF),
                        1f to Color(0x14000000),
                    )
                )
                .padding(contentPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            leadingIcon?.invoke()
            Text(
                text = text,
                style = textStyle,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
            )
            trailingIcon?.invoke()
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
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(6.dp),
        color = color,
        contentColor = Color.White,
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
