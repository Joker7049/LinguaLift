package org.example.project.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.shreyaspatil.ai.client.generativeai.GenerativeModel
import dev.shreyaspatil.ai.client.generativeai.type.content
import dev.shreyaspatil.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.project.database.AiMemoryQueries
import org.example.project.database.VocabularyQueries
import org.example.project.getGeminiApiKey

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false
)

class ChatViewModel(
    private val vocabularyQueries: VocabularyQueries,
    private val aiMemoryQueries: AiMemoryQueries
) : ViewModel() {
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    // 1. Define the model as a property of the class
    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.0-flash",
        apiKey = getGeminiApiKey(),
        generationConfig = generationConfig {
            temperature = 0.7f
        }
    )

    private val chat by lazy {
        // 2. Use the class property here
        generativeModel.startChat(history = getInitialHistory())
    }

    private fun getInitialHistory(): List<dev.shreyaspatil.ai.client.generativeai.type.Content> {
        val systemInstruction = """
            You are Gemi, a friendly and patient English language tutor.
            Your goal is to help the user practice their English by having a natural conversation.
            Keep your responses concise.
        """.trimIndent()

        val memorySummary = aiMemoryQueries.getMemory().executeAsOneOrNull() ?: "No summary yet."
        val randomWords = vocabularyQueries.getRandomWords().executeAsList()
        val wordListString = randomWords.joinToString(", ") { it.word }

        val longTermMemory = """
            [USER'S LONG-TERM MEMORY]
            $memorySummary

            [PRACTICE VOCABULARY]
            Please try to naturally use these words in the conversation: $wordListString
        """.trimIndent()

        return listOf(
            content(role = "user") { text(systemInstruction) },
            content(role = "model") { text("Understood. I will act as Gemi, a friendly English tutor.") },
            content(role = "user") { text(longTermMemory) },
            content(role = "model") { text("Okay, I have reviewed the user's memory and vocabulary. I am ready to chat.") }
        )
    }

    fun sendMessage(userInput: String) {
        val userMessage = ChatMessage(text = userInput, isFromUser = true)
        val loadingMessage = ChatMessage(text = "typing...", isFromUser = false)

        val currentMessages = _uiState.value.messages
        _uiState.value = _uiState.value.copy(
            messages = currentMessages + userMessage + loadingMessage,
            isLoading = true
        )

        viewModelScope.launch(Dispatchers.Default) {
            try {
                val response = chat.sendMessage(userInput)
                val aiResponse = response.text ?: "Sorry, I couldn't process that."
                val aiMessage = ChatMessage(text = aiResponse, isFromUser = false)

                _uiState.value = _uiState.value.copy(
                    messages = _uiState.value.messages.dropLast(1) + aiMessage,
                    isLoading = false
                )

            } catch (e: Exception) {
                val errorMessage = ChatMessage(text = "Error: ${e.message}", isFromUser = false)
                _uiState.value = _uiState.value.copy(
                    messages = _uiState.value.messages.dropLast(1) + errorMessage,
                    isLoading = false
                )
            }
        }
    }

    fun summarizeAndSaveMemory() {
        viewModelScope.launch(Dispatchers.Default) {
            if (uiState.value.messages.isEmpty()) return@launch // Don't save if there's no conversation

            val chatHistory = uiState.value.messages.joinToString("\n") { 
                if (it.isFromUser) "User: ${it.text}" else "Gemi: ${it.text}"
            }
            val oldMemory = aiMemoryQueries.getMemory().executeAsOneOrNull() ?: "No summary yet."

            val prompt = """
                You are an expert in memory consolidation. Read through this chat history and the user's old memory summary.
                Create a new, single-paragraph summary of the user's key struggles, corrected mistakes, and learned concepts from the latest chat.
                Combine this with the most important points from the old memory. The new summary should be concise and useful for another AI tutor to read to quickly understand the user's progress and weak points.

                [OLD MEMORY SUMMARY]
                $oldMemory

                [LATEST CHAT HISTORY]
                $chatHistory

                Please provide only the new, updated single-paragraph summary.
            """.trimIndent()

            try {
                // 3. Use the class property here too
                val newSummary = generativeModel.generateContent(prompt).text ?: return@launch
                aiMemoryQueries.updateMemory(newSummary)
                println("[ViewModel] Memory updated successfully.")
            } catch (e: Exception) {
                println("[ViewModel] Error updating memory: ${e.message}")
            }
        }
    }
}
