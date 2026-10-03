package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.model.AppThemePreset

fun createDarkColorScheme(preset: AppThemePreset) = darkColorScheme(
    primary = preset.primaryColor,
    onPrimary = Color.White,
    primaryContainer = preset.primaryColor.copy(alpha = 0.2f),
    onPrimaryContainer = Color.White,
    secondary = preset.secondaryColor,
    onSecondary = Color.Black,
    secondaryContainer = preset.secondaryColor.copy(alpha = 0.2f),
    onSecondaryContainer = Color.White,
    tertiary = preset.tertiaryColor,
    onTertiary = Color.White,
    background = preset.darkBackground,
    onBackground = DarkTextPrimary,
    surface = preset.darkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = preset.darkSurfaceElevated,
    onSurfaceVariant = DarkTextSecondary,
    outline = Color(0xFF2C2C35),
    error = CrimsonDanger,
    onError = Color.White
)

fun createLightColorScheme(preset: AppThemePreset) = lightColorScheme(
    primary = preset.primaryColor,
    onPrimary = Color.White,
    primaryContainer = preset.primaryColor.copy(alpha = 0.15f),
    onPrimaryContainer = Color(0xFF18181B),
    secondary = preset.secondaryColor,
    onSecondary = Color.Black,
    secondaryContainer = preset.secondaryColor.copy(alpha = 0.15f),
    onSecondaryContainer = Color(0xFF18181B),
    tertiary = preset.tertiaryColor,
    onTertiary = Color.White,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceElevated,
    onSurfaceVariant = LightTextSecondary,
    outline = Color(0xFFD4D4D8),
    error = CrimsonDanger,
    onError = Color.White
)

@Composable
fun InffitTheme(
    darkTheme: Boolean = true,
    themePreset: AppThemePreset = AppThemePreset.ONYX_FLAME,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        createDarkColorScheme(themePreset)
    } else {
        createLightColorScheme(themePreset)
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    InffitTheme(darkTheme = darkTheme, themePreset = AppThemePreset.ONYX_FLAME, content = content)
}
