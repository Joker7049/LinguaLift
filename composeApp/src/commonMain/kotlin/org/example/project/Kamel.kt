package org.example.project

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import io.kamel.core.config.KamelConfig
import io.kamel.core.config.takeFrom
import io.kamel.image.config.Default
import io.kamel.image.config.LocalKamelConfig
import io.kamel.image.config.imageBitmapDecoder
import io.kamel.image.config.imageVectorDecoder
import io.kamel.image.config.svgDecoder

val desktopConfig = KamelConfig {
    takeFrom(KamelConfig.Default)
    imageBitmapDecoder()
    imageVectorDecoder()
    svgDecoder()
}

@Composable
fun KamelProvider(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalKamelConfig provides desktopConfig) {
        content()
    }
}
