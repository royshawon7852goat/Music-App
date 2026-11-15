package com.pulse.music.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PulseRedAccent,
    onPrimary = PulseTextPrimary,
    primaryContainer = PulseCardDark,
    onPrimaryContainer = PulseTextPrimary,
    secondary = PulseProgressBar,
    onSecondary = PulseTextPrimary,
    background = PulseBlack,
    onBackground = PulseTextPrimary,
    surface = PulseSecondaryDark,
    onSurface = PulseTextPrimary,
    surfaceVariant = PulseCardDark,
    onSurfaceVariant = PulseTextSecondary,
    outline = PulseBorderDark
)

private val LightColorScheme = lightColorScheme(
    primary = PulseRedAccent,
    onPrimary = Color.White,
    primaryContainer = PulseLightCard,
    onPrimaryContainer = PulseLightTextPrimary,
    secondary = PulseProgressBar,
    onSecondary = Color.White,
    background = PulseLightBg,
    onBackground = PulseLightTextPrimary,
    surface = PulseLightSurface,
    onSurface = PulseLightTextPrimary,
    surfaceVariant = PulseLightCard,
    onSurfaceVariant = PulseLightTextSecondary,
    outline = Color(0xFFD0D0D0)
)

@Composable
fun PulseTheme(
    darkTheme: Boolean = true, // Default to dark mode for Nothing Phone 2a AMOLED experience
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
