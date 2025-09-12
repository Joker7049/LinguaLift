package org.example.project.util

import io.ktor.client.HttpClient

actual suspend fun saveImageFromUrl(client: HttpClient, url: String, word: String): String? {
    // TODO: Implement file saving for JVM
    println("JVM file saving not implemented yet.")
    return null
}