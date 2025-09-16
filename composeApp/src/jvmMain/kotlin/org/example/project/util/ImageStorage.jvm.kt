package org.example.project.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

actual suspend fun copyImageToInternalStorage(uriString: String, word: String): String? {
    return withContext(Dispatchers.IO) {
        try {
            // 1. On desktop, the uriString is already a direct file path
            val inputStream = File(uriString).inputStream()

            // 2. Get the user's home directory to create our app's folder
            val userHome = System.getProperty("user.home")
            val directory = File(userHome, ".lingua_lift/word_images")
            if (!directory.exists()) {
                directory.mkdirs()
            }

            // 3. Create a unique file name and copy the data
            // Sanitize the word to make it safe for filenames
            val safeWord = word.replace(Regex("[^a-zA-Z0-9._-]"), "_")

            val timestamp = System.currentTimeMillis()
            val file = File(directory, "${safeWord}_image_$timestamp.jpg")

            inputStream.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }

            // 4. Return the new, permanent path
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}