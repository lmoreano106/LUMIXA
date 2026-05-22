package com.lumixa.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = LumixaBlue,
    onPrimary = LumixaTextPrimaryDark,
    secondary = LumixaMint,
    onSecondary = LumixaTextPrimaryDark,
    background = LumixaBackgroundDark,
    onBackground = LumixaTextPrimaryDark,
    surface = LumixaCardDark,
    onSurface = LumixaTextPrimaryDark,
    tertiary = LumixaMint,
    onTertiary = LumixaTextPrimaryDark
)

private val LightColorScheme = lightColorScheme(
    primary = LumixaBlue,
    onPrimary = LumixaTextPrimaryDark,
    secondary = LumixaMint,
    onSecondary = LumixaTextPrimaryDark,
    background = LumixaSurfaceLight,
    onBackground = LumixaTextPrimaryLight,
    surface = LumixaCardLight,
    onSurface = LumixaTextPrimaryLight,
    tertiary = LumixaNavy,
    onTertiary = LumixaCardLight
)

@Composable
fun LUMIXATheme(
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
