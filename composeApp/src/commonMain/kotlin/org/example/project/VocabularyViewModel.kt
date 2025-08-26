package org.example.project

import org.example.project.database.VocabularyQueries
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import dev.shreyaspatil.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.example.project.database.Vocabulary

@Serializable
data class VocabularyWord(
    val id: Long? = null,
    val word: String = "",
    val explanation: String = "",
    val persianEquivalent: String = "",
    val example: String = ""
)

@Serializable
data class VocabularyUiState(
    val textToExtract: String = "",
    val extractedWords: List<VocabularyWord> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class VocabularyViewModel(private val vocabularyQueries: VocabularyQueries) : ViewModel() {

    val savedWords: StateFlow<List<VocabularyWord>> = 
        vocabularyQueries.getAllWords()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { vocabularyList: List<Vocabulary> ->
                vocabularyList.map { dbVocabulary: Vocabulary ->
                    VocabularyWord(
                        id = dbVocabulary.id,
                        word = dbVocabulary.word,
                        explanation = dbVocabulary.explanation,
                        persianEquivalent = dbVocabulary.persianEquivalent,
                        example = dbVocabulary.example
                    )
                }
            }
            .onEach { words ->
                println("[ViewModel] Saved words list updated. Count: ${words.size}")
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    private val _uiState = MutableStateFlow(VocabularyUiState())
    val uiState: StateFlow<VocabularyUiState> = _uiState

    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.0-flash", apiKey = getGeminiApiKey()
    )

    fun onTextChange(text: String) {
        _uiState.value = _uiState.value.copy(textToExtract = text)
    }

    fun extractWords() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)

                val prompt = """
       You are an expert linguist and English teacher.
       From the following text, extract the most important vocabulary words that a language learner should know.
       For each word, provide an explanations in very simple terms, the Persian equivalent, and an example sentence. If a verb is in the past or past participle form, mention it in the explanation,
       add additional info inside parenthesis next to each word, if it's a noun say if it's countable or not, and if it's a verb say if it's transitive or intransitive. Also always type the IPA pronunciation symbols for the word in word section.
       Return the result as a single, valid JSON array, where each object has the following keys: "word", "explanation", "persianEquivalent", "example".
       Do not include any other text or explanations outside of the JSON array.

       Here is the text:
       "${_uiState.value.textToExtract}"
   """.trimIndent()
                val response = generativeModel.generateContent(prompt).text ?: ""
                val cleanedResponse = response.removeSurrounding("```json", "```").trim()
                val extractedWords = Json.decodeFromString<List<VocabularyWord>>(cleanedResponse)

                _uiState.value =
                    _uiState.value.copy(extractedWords = extractedWords, isLoading = false)
            } catch (e: Exception) {
                _uiState.value =
                    uiState.value.copy(error = "Error: ${e.message}", isLoading = false)
            }
        }
    }

    fun saveWord(word: VocabularyWord) {
        viewModelScope.launch(Dispatchers.Default) {
            println("[ViewModel] Attempting to save word: ${word.word}")
            vocabularyQueries.insertWord(
                word = word.word,
                explanation = word.explanation,
                persianEquivalent = word.persianEquivalent,
                example = word.example
            )
            println("[ViewModel] Finished saving word: ${word.word}")
        }
    }

    fun deleteWord(id: Long) {
        viewModelScope.launch(Dispatchers.Default) {
            vocabularyQueries.deleteWordById(id)
        }
    }
}