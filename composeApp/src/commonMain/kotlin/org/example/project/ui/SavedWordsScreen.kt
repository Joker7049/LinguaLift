package org.example.project.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.example.project.VocabularyViewModel
import org.example.project.VocabularyWord
import org.example.project.database.getDatabase

@Composable
fun SavedWordsScreen(
    modifier: Modifier = Modifier,
    viewModel: VocabularyViewModel
) {
    // We use the same ViewModel, as it holds the logic for both extracting and managing words.
    val vocabularyQueries = getDatabase().vocabularyQueries
    val savedWords by viewModel.savedWords.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Saved Vocabulary",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (savedWords.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("You haven't saved any words yet.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(savedWords) { word ->
                    SavedWordCard(
                        vocabularyWord = word,
                        onDeleteClick = {
                            // The id from the database cannot be null here
                            word.id?.let { id ->
                                viewModel.deleteWord(id)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SavedWordCard(
    vocabularyWord: VocabularyWord,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = vocabularyWord.word,
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = vocabularyWord.explanation,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Persian: ${vocabularyWord.persianEquivalent}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Example: \"${vocabularyWord.example}\"",
                style = MaterialTheme.typography.bodySmall,
                fontStyle = FontStyle.Italic
            )

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
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