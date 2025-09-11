package org.example.project.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import lingualift.composeapp.generated.resources.Res
import lingualift.composeapp.generated.resources.Res.getUri
import lingualift.composeapp.generated.resources.branch_bottom_left
import lingualift.composeapp.generated.resources.branch_top_right
import org.example.project.VocabularyWord
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalResourceApi::class)
@Composable
fun Flashcard(
    vocabularyWord: VocabularyWord,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isFlipped by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "flashcardRotation"
    )

    Box(modifier.fillMaxSize()) {
        /* ---------- top-left branch ---------- */
        KamelImage(
            resource = { asyncPainterResource("resource:drawable/branch_bottom_left.svg") },
            contentDescription = null,
            onLoading = { Box(Modifier.size(80.dp)) },   // placeholder
            onFailure = { error ->                      // <-- will print the reason
                println("Kamel top-right failed: ${error.message}")
            },
            modifier = Modifier
                .size(80.dp)
                .padding(16.dp)
                .align(Alignment.TopStart)
        )

        /* ---------- bottom-right branch ---------- */
        KamelImage(
            resource = { asyncPainterResource("resource:drawable/branch_top_right.svg") },
            contentDescription = null,
            onLoading = { Box(Modifier.size(80.dp)) },
            onFailure = { error ->
                println("Kamel bottom-left failed: ${error.message}")
            },
            modifier = Modifier
                .size(80.dp)
                .padding(16.dp)
                .align(Alignment.BottomEnd)
        )

        /* ---------- flipping card (unchanged) ---------- */
        Card(
            modifier = modifier
                .fillMaxWidth(0.8f)
                .aspectRatio(0.7f)
                .shadow(8.dp, RoundedCornerShape(16.dp))
                .graphicsLayer {
                    rotationY = rotation
                    cameraDistance = 12 * density
                },
            shape = RoundedCornerShape(16.dp),
            onClick = { isFlipped = !isFlipped },
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box(Modifier.fillMaxSize()) {
                if (rotation <= 90f) {
                    FlashcardFront(vocabularyWord, onDeleteClick)
                } else {
                    Box(Modifier.graphicsLayer { rotationY = 180f }) {
                        FlashcardBack(vocabularyWord, onDeleteClick)
                    }
                }
            }
        }
    }
}

@Composable
fun FlashcardFront(vocabularyWord: VocabularyWord, onDeleteClick: () -> Unit) {
    val (word, phonetic) = remember(vocabularyWord.word) {
        val match = Regex("(.+?) /(.+?)/").find(vocabularyWord.word)
        if (match != null && match.groupValues.size == 3) {
            match.groupValues[1].trim() to "/${match.groupValues[2]}/"
        } else {
            vocabularyWord.word to null
        }
    }

    val (partOfSpeech, definition) = remember(vocabularyWord.explanation) {
        val match = Regex("\\((.*?)\\)\\s+(.*)").find(vocabularyWord.explanation)
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
    ) {
        // Main content area
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = word,
                style = MaterialTheme.typography.displaySmall,
                fontFamily = FontFamily.Serif
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                phonetic?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                partOfSpeech?.let {
                    Text(
                        text = "($it)",
                        style = MaterialTheme.typography.bodyLarge,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Divider(modifier = Modifier.fillMaxWidth(0.5f))
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = definition,
                style = MaterialTheme.typography.titleMedium,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }

        // Action bar at the bottom
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
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

@Composable
fun FlashcardBack(vocabularyWord: VocabularyWord, onDeleteClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Main content area
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = vocabularyWord.persianEquivalent,
                style = MaterialTheme.typography.displaySmall,
                fontFamily = FontFamily.Serif
            )
            Spacer(modifier = Modifier.height(24.dp))
            Divider(modifier = Modifier.fillMaxWidth(0.5f))
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Example:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "\"${vocabularyWord.example}\"",
                style = MaterialTheme.typography.bodyLarge,
                fontStyle = FontStyle.Italic,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }

        // Action bar at the bottom
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
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



@Composable
private fun branchTopRight()  = painterResource(Res.drawable.branch_top_right)
@Composable
private fun branchBottomLeft()= painterResource(Res.drawable.branch_bottom_left)