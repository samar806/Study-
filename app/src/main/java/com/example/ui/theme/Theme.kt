package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Immutable
data class ExtendedColors(
    val canvasBackground: Color,
    val cardBackground: Color,
    val cardBorder: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val isDark: Boolean,
    val primaryGradient: Brush = PrimaryGradient,
    val backgroundGradient: Brush
)

val LocalExtendedColors = staticCompositionLocalOf {
    ExtendedColors(
        canvasBackground = NavyDark,
        cardBackground = CardWhite,
        cardBorder = CardBorderLight,
        textPrimary = TextOnWhitePrimary,
        textSecondary = TextOnWhiteSecondary,
        textMuted = TextMuted,
        isDark = true,
        backgroundGradient = NavyBackgroundGradient
    )
}

private val DarkColorScheme = darkColorScheme(
    primary = VioletAccent,
    secondary = BlueAccent,
    tertiary = VioletAccent,
    background = NavyDark,
    surface = CardWhite,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = TextOnNavyPrimary,
    onSurface = TextOnWhitePrimary,
    error = ErrorRed
)

private val LightColorScheme = lightColorScheme(
    primary = VioletAccent,
    secondary = BlueAccent,
    tertiary = VioletAccent,
    background = LightCanvas,
    surface = CardWhite,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = TextOnWhitePrimary,
    onSurface = TextOnWhitePrimary,
    error = ErrorRed
)

@Composable
fun AIMCQTeacherTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val extendedColors = if (darkTheme) {
        ExtendedColors(
            canvasBackground = NavyDark,
            cardBackground = CardWhite,
            cardBorder = CardBorderLight.copy(alpha = 0.8f),
            textPrimary = TextOnWhitePrimary,
            textSecondary = TextOnWhiteSecondary,
            textMuted = TextMuted,
            isDark = true,
            backgroundGradient = NavyBackgroundGradient
        )
    } else {
        ExtendedColors(
            canvasBackground = LightCanvas,
            cardBackground = CardWhite,
            cardBorder = CardBorderLight,
            textPrimary = TextOnWhitePrimary,
            textSecondary = TextOnWhiteSecondary,
            textMuted = TextMuted,
            isDark = false,
            backgroundGradient = LightBackgroundGradient
        )
    }

    CompositionLocalProvider(LocalExtendedColors provides extendedColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    AIMCQTeacherTheme(darkTheme = darkTheme, content = content)
}

object AppTheme {
    val colors: ExtendedColors
        @Composable
        get() = LocalExtendedColors.current
}
