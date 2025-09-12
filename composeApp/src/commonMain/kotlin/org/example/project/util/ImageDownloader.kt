package org.example.project.util

import io.ktor.client.HttpClient

expect suspend fun saveImageFromUrl(client: HttpClient, url: String, word: String): String?