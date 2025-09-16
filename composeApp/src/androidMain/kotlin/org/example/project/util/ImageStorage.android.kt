package org.example.project.util

import android.annotation.SuppressLint
import android.content.Context
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

@SuppressLint("StaticFieldLeak")
object AndroidContext {
    lateinit var instance: Context
}

actual suspend fun copyImageToInternalStorage(uriString: String, word: String): String? =
    withContext(Dispatchers.IO) {
        val context = AndroidContext.instance

        // Sanitize the word to make it safe for filenames
        val safeWord = word.replace(Regex("[^a-zA-Z0-9._-]"), "_")

        // Prepare destination directory
        val directory = File(context.filesDir, "word_images").apply {
            if (!exists() && !mkdirs()) {
                // Failed to create directory
                return@withContext null
            }
        }

        // Prepare destination file
        val timestamp = System.currentTimeMillis()
        val file = File(directory, "${safeWord}_image_$timestamp.jpg")

        try {
            context.contentResolver.openInputStream(uriString.toUri())?.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            } ?: return@withContext null // inputStream was null

            return@withContext file.absolutePath
        } catch (e: Exception) {
            // Log full error for debugging
            e.printStackTrace()
            return@withContext null
        }
    }
