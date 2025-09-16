package org.example.project.ui.common

import androidx.compose.runtime.Composable

@Composable
expect fun ImagePicker(
    show: Boolean,
    onImageSelected: (String?) -> Unit
)