package com.sartori.rmnews.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = NewsLightGreen,
    onPrimary = Color(0xFF15351D),
    primaryContainer = Color(0xFF2C4D35),
    onPrimaryContainer = NewsSage,
    secondary = Color(0xFFB5CCBF),
    onSecondary = Color(0xFF20382C),
    secondaryContainer = Color(0xFF374F43),
    onSecondaryContainer = NewsSage,
    tertiary = NewsMustard,
    onTertiary = Color(0xFF392B00),
    tertiaryContainer = Color(0xFF554200),
    onTertiaryContainer = Color(0xFFF8E1A4),
    background = NewsPetrol,
    onBackground = Color(0xFFE5EEE6),
    surface = NewsPetrol,
    onSurface = Color(0xFFE5EEE6),
    surfaceVariant = Color(0xFF3F5350),
    onSurfaceVariant = Color(0xFFBDCDC4),
    surfaceContainerLowest = Color(0xFF0B2026),
    surfaceContainerLow = NewsDarkCard,
    surfaceContainer = Color(0xFF244145),
    surfaceContainerHigh = Color(0xFF2C494D),
    surfaceContainerHighest = Color(0xFF365357),
    surfaceDim = NewsPetrol,
    surfaceBright = Color(0xFF365357),
    surfaceTint = NewsLightGreen,
    outline = Color(0xFF8FA39A),
    outlineVariant = Color(0xFF3F5350),
    inverseSurface = Color(0xFFE5EEE6),
    inverseOnSurface = NewsInk,
    inversePrimary = NewsForest
)

private val LightColorScheme = lightColorScheme(
    primary = NewsForest,
    onPrimary = Color.White,
    primaryContainer = NewsSage,
    onPrimaryContainer = Color(0xFF142E1B),
    secondary = Color(0xFF506758),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFDCE8DD),
    onSecondaryContainer = Color(0xFF172C20),
    tertiary = Color(0xFF735800),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFF8E1A4),
    onTertiaryContainer = Color(0xFF241A00),
    background = NewsCream,
    onBackground = NewsInk,
    surface = NewsCream,
    onSurface = NewsInk,
    surfaceVariant = Color(0xFFE0E5DA),
    onSurfaceVariant = NewsMuted,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFFFFBF5),
    surfaceContainer = Color(0xFFF0E8DC),
    surfaceContainerHigh = Color(0xFFE9E3D5),
    surfaceContainerHighest = Color(0xFFE1DECE),
    surfaceDim = Color(0xFFDDD9CD),
    surfaceBright = Color(0xFFFFFBF5),
    surfaceTint = NewsForest,
    outline = Color(0xFF788579),
    outlineVariant = Color(0xFFC7CEC0),
    inverseSurface = NewsInk,
    inverseOnSurface = NewsCream,
    inversePrimary = NewsLightGreen
)

@Composable
fun RMNewsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Keep the news palette by default instead of using wallpaper colors.
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}