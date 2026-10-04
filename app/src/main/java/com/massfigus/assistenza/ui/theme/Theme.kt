package com.massfigus.assistenza.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Blu petrolio da officina + giallo ambra "di sicurezza"
private val Petrolio = Color(0xFF17394F)
private val Ambra = Color(0xFFF2A900)

private val Chiaro = lightColorScheme(
    primary = Petrolio,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFCFE2EE),
    onPrimaryContainer = Color(0xFF0A2232),
    secondary = Color(0xFF8A6100),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFE7A8),
    onSecondaryContainer = Color(0xFF2B1D00),
    tertiary = Ambra,
    background = Color(0xFFF3F5F7),
    surface = Color.White,
    surfaceVariant = Color(0xFFE3E8EC),
    onSurfaceVariant = Color(0xFF4A5560)
)

private val Scuro = darkColorScheme(
    primary = Color(0xFF9CCBE8),
    onPrimary = Color(0xFF0A2232),
    primaryContainer = Petrolio,
    onPrimaryContainer = Color(0xFFCFE2EE),
    secondary = Ambra,
    onSecondary = Color(0xFF2B1D00),
    secondaryContainer = Color(0xFF5E4300),
    onSecondaryContainer = Color(0xFFFFE7A8),
    tertiary = Ambra
)

@Composable
fun AssistenzaTheme(scuro: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (scuro) Scuro else Chiaro, content = content)
}
