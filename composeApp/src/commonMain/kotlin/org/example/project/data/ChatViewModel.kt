package org.example.project.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.shreyaspatil.ai.client.generativeai.GenerativeModel
import dev.shreyaspatil.ai.client.generativeai.type.content
import dev.shreyaspatil.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.project.getGeminiApiKey

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false
)

class ChatViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    // 1. Initialize the GenerativeModel
    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.0-flash",
        apiKey = getGeminiApiKey(),
        generationConfig = generationConfig {
            temperature = 0.7f
        }
    )

    // 2. Create a chat object
    private val chat = generativeModel.startChat()

    fun sendMessage(userInput: String) {
        // Add user's message and a temporary loading message
        val userMessage = ChatMessage(text = userInput, isFromUser = true)
        val loadingMessage = ChatMessage(text = "typing...", isFromUser = false)
        _uiState.value = _uiState.value.copy(
            messages = _uiState.value.messages + userMessage + loadingMessage,
            isLoading = true
        )

        viewModelScope.launch {
            try {
                // 3. Send the message to the AI
                val response = chat.sendMessage(userInput)

                // 4. Replace the loading message with the AI's response
                val aiResponse = response.text ?: "Sorry, I couldn't process that."
                val aiMessage = ChatMessage(text = aiResponse, isFromUser = false)

                _uiState.value = _uiState.value.copy(
                    messages = _uiState.value.messages.dropLast(1) + aiMessage,
                    isLoading = false
                )

            } catch (e: Exception) {
                // Handle errors
                val errorMessage = ChatMessage(text = "Error: ${e.message}", isFromUser = false)
                _uiState.value = _uiState.value.copy(
                    messages = _uiState.value.messages.dropLast(1) + errorMessage,
                    isLoading = false
                )
            }
        }
    }
}