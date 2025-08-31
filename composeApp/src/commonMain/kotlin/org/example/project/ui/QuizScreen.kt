package org.example.project.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.example.project.quiz.QuizUiState
import org.example.project.quiz.QuizViewModel

@Composable
fun QuizScreen(
    viewModel: QuizViewModel,
    modifier: Modifier = Modifier
) {
    // 1. Run this block once when the screen first appears
    LaunchedEffect(Unit) {
        viewModel.generateQuiz()
    }

    // 2. Collect the state from the ViewModel
    val uiState by viewModel.uiState.collectAsState()

    // 3. A simple layout to center our content
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // 4. Show UI based on the current state
        when {
            uiState.isLoading -> {
                CircularProgressIndicator()
            }
            uiState.error != null -> {
                Text("Error: ${uiState.error}")
            }
            uiState.quiz != null -> {
                QuizContent(
                    uiState = uiState,
                    viewModel = viewModel
                )

            }
        }
    }
}

@Composable
fun QuizContent(
    uiState: QuizUiState,
    viewModel: QuizViewModel,
    modifier: Modifier = Modifier
) {
    val currentQuestion = uiState.quiz?.questions?.get(uiState.currentQuestionIndex)

    if (currentQuestion == null) {
        Text("Error: Could not load question.")
        return
    }

    if (uiState.isQuizFinished) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Quiz Finished!", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Your score: ${uiState.score} / ${uiState.quiz.questions.size}", style =
                MaterialTheme.typography.titleLarge)
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Question Text
        Text(
            text = "Question ${uiState.currentQuestionIndex + 1}/${uiState.quiz.questions.size}",
            style = MaterialTheme.typography.labelLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = currentQuestion.questionText,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(32.dp))

        // Answer Options
        currentQuestion.options.forEach { option ->
            val isSelected = uiState.selectedAnswer == option
            val buttonColors = if (uiState.isAnswerSubmitted) {
                // After submission, color the buttons
                when {
                    option == currentQuestion.correctAnswer -> ButtonDefaults.buttonColors(containerColor =
                        Color.Green.copy(alpha = 0.5f))
                    isSelected -> ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.5f))
                    else -> ButtonDefaults.outlinedButtonColors()
                }
            } else {
                // Before submission, just highlight the selected one
                if (isSelected) ButtonDefaults.buttonColors() else ButtonDefaults.outlinedButtonColors()
            }

            Button(
                onClick = { viewModel.onAnswerSelected(option) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                colors = buttonColors,
                enabled = !uiState.isAnswerSubmitted
            ) {
                Text(option)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Submit/Next Button
        if (uiState.isAnswerSubmitted) {
            Button(onClick = { viewModel.nextQuestion() }) {
                Text("Next")
            }
        } else {
            Button(
                onClick = { viewModel.submitAnswer() },
                enabled = uiState.selectedAnswer != null
            ) {
                Text("Submit")
            }
        }
    }
}

