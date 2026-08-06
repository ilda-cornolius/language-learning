package com.lingualearn.pro.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val VistaGreen = Color(0xFF6DC067)
val VistaTeal = Color(0xFF2ABBC4)
val VistaBlue = Color(0xFF1E6BA8)
val VistaYellow = Color(0xFFE6D54A)
val VistaAccent = Color(0xFFFF6B1A)

val GlassPanel = Color(0x33000000)
val GlassPanelStrong = Color(0x4D000000)
val GlassTile = Color(0x1AFFFFFF)
val GlassTileStrong = Color(0x33FFFFFF)
val GlassBorder = Color(0x33FFFFFF)

/** Text tints matching the mockup's white/80, white/60 utility classes. */
val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xCCFFFFFF)
val TextMuted = Color(0x99FFFFFF)

private val AeroColorScheme = darkColorScheme(
    primary = VistaAccent,
    onPrimary = Color.White,
    secondary = VistaTeal,
    onSecondary = Color.White,
    tertiary = VistaGreen,
    background = VistaBlue,
    onBackground = Color.White,
    surface = GlassPanel,
    onSurface = Color.White,
    outline = GlassBorder,
)

/**
 * The mockup asks for Segoe UI / Frutiger, neither of which ships with Android.
 * SansSerif is the closest humanist default across OEM devices.
 */
private val AeroTypography = Typography(
    headlineSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 30.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
        lineHeight = 26.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 22.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 12.sp,
        lineHeight = 17.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp,
        letterSpacing = 0.8.sp,
    ),
)

@Composable
fun LinguaLearnTheme(
    @Suppress("UNUSED_PARAMETER") darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    // The Aero glass look is built on a fixed bright gradient, so the palette
    // stays the same in light and dark system themes.
    MaterialTheme(
        colorScheme = AeroColorScheme,
        typography = AeroTypography,
        content = content,
    )
}
