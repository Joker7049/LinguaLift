package org.example.project.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation.Companion.keyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
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
import androidx.lifecycle.viewmodel.compose.viewModel
import org.example.project.SimplifyViewModel


@Composable
fun SimplificationScreen(
    modifier: Modifier = Modifier,
) {
    val viewModel = remember { SimplifyViewModel() }
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
            text = "Simplify Text",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = uiState.textToSimplify,
            onValueChange = viewModel::onTextChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            label = { Text("Enter text to simplify") },
            placeholder = { Text("e.g., The feline was quiescent.") },
            trailingIcon = {
                if (uiState.textToSimplify.isNotEmpty()) {
                    IconButton(onClick = {
                        viewModel.onTextChange(
                            ""
                        )
                    }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear text"
                        )
                    }
                }
            },
            shape = MaterialTheme.shapes.large,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = viewModel::simplifyText,
            enabled = uiState.textToSimplify.isNotBlank() && !uiState.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Simplify",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text("Simplify Text")
        }

        Spacer(modifier = Modifier.height(24.dp))

        AnimatedContent(
            targetState = uiState,
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) togetherWith fadeOut(
                    animationSpec = tween(
                        300
                    )
                )
            }
        ) { targetState ->
            when {
                targetState.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(48.dp))
                    }
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

                targetState.simplifiedText.isNotBlank() -> {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Simplified Version:",
                                style = MaterialTheme.typography.titleMedium,
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = targetState.simplifiedText,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        }
    }
}