package org.example.project.util

import android.annotation.SuppressLint
import android.content.Context
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream


@SuppressLint("StaticFieldLeak")
object AndroidContext {
    lateinit var instance: Context
}

actual suspend fun saveImageFromUrl(client: HttpClient, url: String, word: String): String? {
    return withContext(Dispatchers.IO) {
        try {
            val imageBytes = client.get(url).body<ByteArray>()

            val directory = File(AndroidContext.instance.filesDir, "word_images")
            if (!directory.exists()) {
                directory.mkdirs()
            }
            val file = File(directory, "${word}_image.jpg")

            FileOutputStream(file).use {
                it.write(imageBytes)
            }
            file.absolutePath

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

