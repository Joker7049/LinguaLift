package org.example.project.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import org.example.project.VocabularyViewModel
import org.example.project.VocabularyWord
import org.example.project.ui.common.ImagePicker
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.example.project.ui.common.LocalImage
import org.example.project.util.copyImageToInternalStorage


@OptIn(ExperimentalMaterial3Api::class, ExperimentalResourceApi::class)
@Composable
fun Flashcard(
    vocabularyWord: VocabularyWord,
    onDeleteClick: () -> Unit,
    viewModel: VocabularyViewModel, // Add this
    modifier: Modifier = Modifier
)
{
    var isFlipped by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "flashcardRotation"
    )

    Card(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer{
                rotationY = rotation
                cameraDistance = 12 * density
            },
        shape = RoundedCornerShape(16.dp),
        onClick = { isFlipped = !isFlipped },
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(Modifier.fillMaxSize()) {
            if (rotation <= 90f) {
                FlashcardFront(vocabularyWord, viewModel, onDeleteClick)
            } else {
                Box(Modifier.graphicsLayer { rotationY = 180f }) {
                    FlashcardBack(vocabularyWord, onDeleteClick)
                }
            }
        }
    }
}


@Composable
fun FlashcardFront(
    vocabularyWord: VocabularyWord,
    viewModel: VocabularyViewModel,
    onDeleteClick: () -> Unit
) {
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
    var showImagePicker by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    ImagePicker(
        show = showImagePicker,
        onImageSelected = {
            showImagePicker = false
            it?.let { uriString ->
                vocabularyWord.id?.let { id ->
                    scope.launch {
                        val localPath = copyImageToInternalStorage(uriString, vocabularyWord.word)
                        println("[ViewModel] Local path: $localPath")
                        if (localPath != null) {
                            viewModel.updateWordImagePath(id, localPath)
                        }
                    }
                }
            }
        }
    )

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
            vocabularyWord.imagePath?.let { path ->
                LocalImage(
                    path = path,
                    contentDescription = "Image for ${vocabularyWord.word}",
                    modifier = Modifier.size(240.dp).padding(bottom = 16.dp)
                )
            }
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
            IconButton(onClick = { showImagePicker = true }) {
                Icon(
                    imageVector = Icons.Default.AddPhotoAlternate,
                    contentDescription = "Upload Image"
                )
            }
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