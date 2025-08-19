package org.example.project

actual fun getGeminiApiKey(): String {
    return BuildConfig.GEMINI_API_KEY
}
