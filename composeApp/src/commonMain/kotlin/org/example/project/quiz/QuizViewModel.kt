package org.example.project.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.shreyaspatil.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.example.project.database.VocabularyQueries
import org.example.project.getGeminiApiKey

@Serializable
data class QuizUiState(
    // States for fetching the quiz
    val isLoading: Boolean = false,
    val error: String? = null,
    val quiz: Quiz? = null,

    // New states for managing the game
    val currentQuestionIndex: Int = 0,
    val selectedAnswer: String? = null,
    val isAnswerSubmitted: Boolean = false,
    val score: Int = 0,
    val isQuizFinished: Boolean = false
)

class QuizViewModel(private val vocabularyQueries: VocabularyQueries) : ViewModel() {
    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.0-flash", apiKey = getGeminiApiKey()
    )

    fun onAnswerSelected(answer: String) {
        _uiState.update { it.copy(selectedAnswer = answer) }
    }

    fun submitAnswer() {
        val currentState = _uiState.value
        val currentQuestion =
            currentState.quiz?.questions?.get(currentState.currentQuestionIndex) ?: return

        if (currentState.selectedAnswer == currentQuestion.correctAnswer) {
            // Correct answer
            _uiState.update { it.copy(isAnswerSubmitted = true, score = it.score + 1) }
        } else {
            // Incorrect answer
            _uiState.update { it.copy(isAnswerSubmitted = true) }
        }
    }

    fun nextQuestion() {
        val currentState = _uiState.value
        val totalQuestions = currentState.quiz?.questions?.size ?: 0

        if (currentState.currentQuestionIndex < totalQuestions - 1) {
            // Move to the next question
            _uiState.update {
                it.copy(
                    currentQuestionIndex = it.currentQuestionIndex + 1,
                    isAnswerSubmitted = false,
                    selectedAnswer = null
                )
            }
        } else {
            // Quiz is finished
            _uiState.update { it.copy(isQuizFinished = true) }
        }
    }


    fun generateQuiz() {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                _uiState.value = QuizUiState(isLoading = true)
                val savedWords = vocabularyQueries.getAllWords().executeAsList()
                if (savedWords.size < 5) { // Need at least 5 words for a good quiz
                    _uiState.value =
                        QuizUiState(error = "You need to save at least 5 vocabulary words to generate a quiz!")
                    return@launch
                }
                val wordListString = savedWords.joinToString(", ") { it.word }
                val prompt = """
                    You are an expert English teacher designing a quiz.
                    Based on the following list of vocabulary words, please generate a quiz with 5 multiple-choice questions.
                    The questions should be a mix of "Definition Matching", "Word Matching", and "Fill-in-the-Blank" styles.
                    Ensure the options provided are relevant and create a good challenge.

                    The user's saved vocabulary words are:
                    $wordListString

                    Please return the result as a single, valid JSON object that matches this exact structure:
                    {
                      "questions": [
                        {
                          "questionText": "The text of the question",
                          "options": ["option1", "option2", "option3", "option4"],
                          "correctAnswer": "the correct option"
                        }
                      ]
                    }

                    Do not include any other text, explanations, or markdown formatting in your response.
                """.trimIndent()

                val response = generativeModel.generateContent(prompt).text ?: ""
                val cleanedResponse = response.removeSurrounding("```json", "```").trim()
                val quiz = Json.decodeFromString<Quiz>(cleanedResponse)
                _uiState.value = QuizUiState(quiz = quiz)
            } catch (e: Exception) {
                _uiState.value = QuizUiState(error = "Error: ${e.message}")
            }
        }
    }
}
