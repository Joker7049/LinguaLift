package org.example.project.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.example.project.quiz.QuizUiState
import org.example.project.quiz.QuizViewModel
import org.example.project.ui.common.StandardTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    onBackClick: () -> Unit,
    viewModel: QuizViewModel,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        viewModel.generateQuiz()
    }

    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    listOf(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.secondaryContainer
                    )
                )
            )
    ) {
        Scaffold(
            topBar = {
                StandardTopAppBar(
                    title = "Daily Quiz",
                    onBackClick = onBackClick
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Box(
                modifier = modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
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
            Text("Your score: ${uiState.score} / ${uiState.quiz.questions.size}", style = MaterialTheme.typography.titleLarge)
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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

        currentQuestion.options.forEach { option ->
            val isSelected = uiState.selectedAnswer == option
            val buttonColors = if (uiState.isAnswerSubmitted) {
                when {
                    option == currentQuestion.correctAnswer -> ButtonDefaults.buttonColors(containerColor = Color.Green.copy(alpha = 0.5f))
                    isSelected -> ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.5f))
                    else -> ButtonDefaults.outlinedButtonColors()
                }
            } else {
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
