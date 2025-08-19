package org.example.project

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.example.project.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    AppTheme {
        val viewModel = remember { SimplifyViewModel() }
        val uiState by viewModel.uiState.collectAsState()

        Column(modifier = Modifier.padding(16.dp)) {
            SimplifyInputTextField(
                text = uiState.textToSimplify,
                onTextChange = viewModel::onTextChange
            )
            SimplifyButton(
                onClick = viewModel::simplifyText,
                isLoading = uiState.isLoading
            )
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.error != null) {
                Text(text = "Error: ${uiState.error}")
            } else {
                SimplifiedText(text = uiState.simplifiedText)
            }
        }
    }
}

@Composable
fun SimplifyInputTextField(
    modifier: Modifier = Modifier,
    text: String,
    onTextChange: (String) -> Unit
) {
    TextField(
        value = text,
        onValueChange = onTextChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text("Paste text to simplify") }
    )
}

@Composable
fun SimplifyButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    isLoading: Boolean
) {
    Button(
        onClick = onClick,
        modifier = modifier.padding(8.dp),
        enabled = !isLoading
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Text("Simplify")
        }
    }
}

@Composable
fun SimplifiedText(
    modifier: Modifier = Modifier,
    text: String
) {
    Text(
        text = text,
        modifier = modifier.padding(16.dp)
    )
}