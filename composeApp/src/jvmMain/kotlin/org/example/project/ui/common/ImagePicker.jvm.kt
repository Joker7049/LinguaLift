package org.example.project.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import java.awt.FileDialog
import java.awt.Frame

@Composable
actual fun ImagePicker(
    show: Boolean,
    onImageSelected: (String?) -> Unit
) {
    LaunchedEffect(show) {
        if (show) {
            val fileDialog = FileDialog(null as Frame?, "Select an image", FileDialog.LOAD)
            fileDialog.isVisible = true
            val file = fileDialog.file
            val dir = fileDialog.directory
            if (file != null && dir != null) {
                onImageSelected(dir + file)
            } else {
                onImageSelected(null)
            }
        }
    }
}