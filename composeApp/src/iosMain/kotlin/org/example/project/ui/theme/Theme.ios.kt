package org.example.project.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

@Composable
internal actual fun getPlatformColorScheme(darkTheme: Boolean): ColorScheme =
    if (darkTheme) darkScheme else lightScheme