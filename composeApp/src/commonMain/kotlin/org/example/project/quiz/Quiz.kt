package org.example.project.quiz


import kotlinx.serialization.Serializable

@Serializable
data class Quiz(
    val questions: List<QuizQuestion>
)

@Serializable
data class QuizQuestion(
    val questionText: String,
    val options: List<String>,
    val correctAnswer: String
)
