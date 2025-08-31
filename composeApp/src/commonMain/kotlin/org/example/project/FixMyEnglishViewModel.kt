package org.example.project

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.shreyaspatil.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class FixMyEnglishUiState(
    val textToCorrect: String = "",
    val correctedText: String = "",
    val explanation: String = "",
    val alternatives: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class FixMyEnglishViewModel(): ViewModel() {
    private val _uiState = MutableStateFlow(FixMyEnglishUiState())
    val uiState: StateFlow<FixMyEnglishUiState> = _uiState.asStateFlow()

    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.0-flash",
        apiKey = getGeminiApiKey()
    )

    fun onTextChange(text: String){
        _uiState.value = _uiState.value.copy(textToCorrect = text)
    }

    fun correctText(){
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                val prompt = """
                   You are an English teacher. Please correct the following text and provide the output in the following format, using these exact
   headings:

                   [CORRECTED]
                   The corrected version of the text.

                   [EXPLANATION]
                   A simple explanation of the mistakes.

                   [ALTERNATIVES]
                   A list of 2-3 alternative ways to say it, separated by newlines.

                   Here is the text to correct:
                   "${_uiState.value.textToCorrect}"
               """.trimIndent()

                val response = generativeModel.generateContent(prompt).text ?: ""

                val correctedText = response.substringAfter("[CORRECTED]").substringBefore("[EXPLANATION]").trim()
                val explanation = response.substringAfter("[EXPLANATION]").substringBefore("[ALTERNATIVES]").trim()
                val alternatives = response.substringAfter("[ALTERNATIVES]").trim().lines()

                _uiState.value = _uiState.value.copy(
                    correctedText = correctedText,
                    explanation = explanation,
                    alternatives = alternatives,
                    isLoading = false,
                    error = null
                )

            }catch (e: Exception){
                _uiState.value = _uiState.value.copy(error = "Error: ${e.message}", isLoading = false)
            }
        }
    }
}

