package org.example.project

actual fun getGeminiApiKey(): String {
    return System.getenv("GEMINI_API_KEY") ?: "YOUR_DUMMY_API_KEY"
}
