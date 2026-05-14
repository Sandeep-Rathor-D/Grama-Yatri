package com.mindmatrix.gramayatri.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val GramaColorScheme = lightColorScheme(
    primary          = GramaGreen,
    onPrimary        = GramaWhite,
    secondary        = GramaGreenLight,
    onSecondary      = GramaWhite,
    background       = GramaWhite,
    surface          = GramaWhite,
    onBackground     = GramaBlack,
    onSurface        = GramaBlack,
    error            = Color(0xFFB00020),
    tertiary         = GramaAmber
)

@Composable
fun GramaYatriTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = GramaColorScheme,
        content     = content
    )
}