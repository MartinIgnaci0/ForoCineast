package com.example.forocineast.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Definimos el esquema de colores oscuros
private val DarkScheme = darkColorScheme(
    primary = CineRed,
    background = CineDark,
    surface = CineSurface,
    onPrimary = Color.White,
    onBackground = WhiteText,
    onSurface = WhiteText,
    secondary = CineGold
)

@Composable
fun ForoCineastTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkScheme,
        typography = Typography,
        content = content
    )
}