package org.example.project.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import org.example.project.VocabularyWord
import org.example.project.ui.theme.flashcard_gradient_end
import org.example.project.ui.theme.flashcard_gradient_start

@Composable
fun Flashcard(
    vocabularyWord: VocabularyWord,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isFlipped by remember { mutableStateOf(false) }

    // 1. Rename the state variable to "rotation"
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "flashcardRotation"
    )

    Card(
        modifier = modifier
            .shadow(8.dp, RoundedCornerShape(16.dp))
            .graphicsLayer {
                // 2. Use our "rotation" state variable here
                this.rotationY = rotation
                cameraDistance = 12 * density
            },
        shape = RoundedCornerShape(16.dp),
        onClick = { isFlipped = !isFlipped },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceBright
        )
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    listOf<Color>(
                        flashcard_gradient_start,
                        flashcard_gradient_end
                    )
                )
            )
        ) {
            // 3. Use our "rotation" state variable for the check
            if (rotation <= 90f) {
                FlashcardFront(vocabularyWord)
            } else {
                // 4. This line now works, because there is no conflict.
                // It correctly sets the rotationY property of this specific graphicsLayer.
                Box(modifier = Modifier.graphicsLayer { rotationY = 180f }) {
                    FlashcardBack(vocabularyWord)
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete word",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
@Composable
fun FlashcardFront(vocabularyWord: VocabularyWord) {
    val (word, phonetic) = remember(vocabularyWord.word) {
        val match = Regex("(.+?) \\((.+?)\\)").find(vocabularyWord.word)
        if (match != null && match.groupValues.size == 3) {
            match.groupValues[1] to match.groupValues[2]
        } else {
            vocabularyWord.word to null
        }
    }

    val (partOfSpeech, definition) = remember(vocabularyWord.explanation) {
        val match = Regex("\\((.*?)\\) (.*)").find(vocabularyWord.explanation)
        if (match != null && match.groupValues.size == 3) {
            match.groupValues[1] to match.groupValues[2]
        } else {
            null to vocabularyWord.explanation
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = word,
            style = MaterialTheme.typography.headlineLarge
        )
        phonetic?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = FontStyle.Italic
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        partOfSpeech?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        Text(
            text = definition,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun FlashcardBack(vocabularyWord: VocabularyWord) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = vocabularyWord.persianEquivalent,
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Example:",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "\"${vocabularyWord.example}\"",
            style = MaterialTheme.typography.bodyLarge,
            fontStyle = FontStyle.Italic
        )
    }
}
