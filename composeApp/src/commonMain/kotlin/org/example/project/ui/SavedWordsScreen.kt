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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedWordsScreen(
    modifier: Modifier = Modifier,
    viewModel: VocabularyViewModel
) {
    // We use the same ViewModel, as it holds the logic for both extracting and managing words.
    val vocabularyQueries = getDatabase().vocabularyQueries
    val savedWords by viewModel.savedWords.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Saved Vocabulary") }
            )
        }
    ) { paddingValues ->
        if (savedWords.isEmpty()) {
            Box(
                modifier = modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("You haven't saved any words yet.")
            }
        } else {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
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
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = vocabularyWord.word,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
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