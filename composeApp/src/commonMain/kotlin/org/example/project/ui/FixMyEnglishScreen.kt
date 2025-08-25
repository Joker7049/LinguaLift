package org.example.project.ui


import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import org.example.project.FixMyEnglishViewModel

import androidx.compose.material3.Button

@Composable
fun FixMyEnglishScreen(
    modifier: Modifier = Modifier,
) {
    val viewModel = remember { FixMyEnglishViewModel() }
    val uiState by viewModel.uiState.collectAsState()
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Fix My English",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = uiState.textToCorrect,
            onValueChange = viewModel::onTextChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            label = { Text("Enter text to correct") },
            placeholder = { Text("e.g., I is happy") },
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = viewModel::correctText,
            enabled = uiState.textToCorrect.isNotBlank() && !uiState.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Correct My English",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text("Correct My English")
        }

        Spacer(modifier = Modifier.height(24.dp))

        AnimatedContent(
            targetState = uiState,
            transitionSpec = {
                fadeIn(animationSpec = tween(durationMillis = 300)) togetherWith fadeOut(
                    animationSpec = tween(durationMillis = 300)
                )
            }
        ) { targetState ->
            when {
                targetState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.size(48.dp))
                }

                targetState.error != null -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                    ) {
                        Text(
                            text = "Error: ${targetState.error}",
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }

                targetState.correctedText.isNotBlank() -> {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Corrected Version:",
                                style = MaterialTheme.typography.titleMedium,
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = targetState.correctedText,
                                style = MaterialTheme.typography.bodyLarge
                            )

                            // Explanation Section
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Explanation:",
                                style = MaterialTheme.typography.titleMedium,
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = targetState.explanation,
                                style = MaterialTheme.typography.bodyLarge
                            )

                            // Alternatives Section
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Alternatives:",
                                style = MaterialTheme.typography.titleMedium,
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            targetState.alternatives.forEach { alternative ->
                                Text(
                                    text = "• $alternative",
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }
                        }
                    }
                }

            }
        }
    }

}