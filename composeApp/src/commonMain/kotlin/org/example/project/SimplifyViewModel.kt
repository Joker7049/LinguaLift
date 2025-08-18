package org.example.project

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.shreyaspatil.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SimplifyViewModel(): ViewModel() {
    private val _uiState = MutableStateFlow(SimplifyUiState())
    val uiState: StateFlow<SimplifyUiState> = _uiState.asStateFlow()

    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.0-flash",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    fun onTextChange(text: String) {
        _uiState.value = _uiState.value.copy(textToSimplify = text)
    }

    fun simplifyText(){
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                val prompt = "Simplify the following text: ${uiState.value.textToSimplify}"
                val simplifiedText = generativeModel.generateContent(prompt).text ?: ""
                _uiState.value = _uiState.value.copy(simplifiedText = simplifiedText, isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
            }
        }
    }
}

data class SimplifyUiState(
    val textToSimplify: String = "",
    val simplifiedText: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)