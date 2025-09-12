package org.example.project.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import java.io.File

@Composable
actual fun LocalImage(
    path: String,
    contentDescription: String,
    modifier: Modifier
) {
    val imageFile = File(path)
    if (imageFile.exists()) {
        KamelImage(
            resource = asyncPainterResource(data = imageFile),
            contentDescription = contentDescription,
            modifier = modifier
        )
    }
}