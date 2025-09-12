package org.example.project.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun LocalImage(
    path: String,
    contentDescription: String,
    modifier: Modifier
) {
    // TODO: Implement iOS image loading from local path
    Box(modifier)
}