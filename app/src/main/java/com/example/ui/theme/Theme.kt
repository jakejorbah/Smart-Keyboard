package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = ElectricBlueDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF1E3A8A),
    onPrimaryContainer = Color(0xFFBFDBFE),
    secondary = ElectricCyan,
    onSecondary = Color(0xFF0F172A),
    tertiary = SoftViolet,
    background = GlassDarkBg,
    onBackground = TextPrimaryDark,
    surface = GlassDarkSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = GlassDarkSurfaceElevated,
    onSurfaceVariant = TextSecondaryDark,
    outline = GlassDarkBorder
)

private val LightColorScheme = lightColorScheme(
    primary = ElectricBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDBEAFE),
    onPrimaryContainer = Color(0xFF1E40AF),
    secondary = ElectricCyan,
    onSecondary = Color.White,
    tertiary = SoftViolet,
    background = GlassLightBg,
    onBackground = TextPrimaryLight,
    surface = GlassLightSurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = GlassLightSurfaceElevated,
    onSurfaceVariant = TextSecondaryLight,
    outline = GlassLightBorder
)

@Composable
fun CrystalKeyboardTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    CrystalKeyboardTheme(darkTheme = darkTheme, content = content)
}
