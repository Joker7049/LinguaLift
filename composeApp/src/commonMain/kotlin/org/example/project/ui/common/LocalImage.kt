package org.example.project.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun LocalImage(
    path: String,
    contentDescription: String,
    modifier: Modifier = Modifier
)