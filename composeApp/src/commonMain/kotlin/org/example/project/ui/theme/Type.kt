package org.example.project.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontFamily

// Default Material 3 typography values
val baseline = Typography()

val AppTypography = Typography(
    displayLarge = baseline.displayLarge.copy(fontFamily = FontFamily.Serif),
    displayMedium = baseline.displayMedium.copy(fontFamily = FontFamily.Serif),
    displaySmall = baseline.displaySmall.copy(fontFamily = FontFamily.Serif),
    headlineLarge = baseline.headlineLarge.copy(fontFamily = FontFamily.Serif),
    headlineMedium = baseline.headlineMedium.copy(fontFamily = FontFamily.Serif),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = FontFamily.Serif),
    titleLarge = baseline.titleLarge.copy(fontFamily = FontFamily.Serif),
    titleMedium = baseline.titleMedium.copy(fontFamily = FontFamily.Serif),
    titleSmall = baseline.titleSmall.copy(fontFamily = FontFamily.Serif),
    bodyLarge = baseline.bodyLarge.copy(fontFamily = FontFamily.SansSerif),
    bodyMedium = baseline.bodyMedium.copy(fontFamily = FontFamily.SansSerif),
    bodySmall = baseline.bodySmall.copy(fontFamily = FontFamily.SansSerif),
    labelLarge = baseline.labelLarge.copy(fontFamily = FontFamily.SansSerif),
    labelMedium = baseline.labelMedium.copy(fontFamily = FontFamily.SansSerif),
    labelSmall = baseline.labelSmall.copy(fontFamily = FontFamily.SansSerif),
)