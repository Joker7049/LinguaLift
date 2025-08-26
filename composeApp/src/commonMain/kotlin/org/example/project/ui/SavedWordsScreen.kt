package org.example.project.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.example.project.VocabularyViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SavedWordsScreen(
    modifier: Modifier = Modifier,
    viewModel: VocabularyViewModel
) {
    val savedWords by viewModel.savedWords.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
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
            val pagerState = rememberPagerState(pageCount = { savedWords.size })
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                Flashcard(
                    vocabularyWord = savedWords[page],
                    onDeleteClick = {
                        savedWords[page].id?.let { id ->
                            viewModel.deleteWord(id)
                        }
                    },
                    modifier = Modifier.fillMaxSize().padding(16.dp)
                )
            }
        }
    }
}