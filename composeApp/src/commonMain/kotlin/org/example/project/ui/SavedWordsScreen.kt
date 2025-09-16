package org.example.project.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import org.example.project.VocabularyViewModel
import org.example.project.ui.common.StandardTopAppBar


@Composable
expect fun branchTopRightPainter(): Painter

@Composable
expect fun branchBottomLeftPainter(): Painter

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SavedWordsScreen(
    onBackClick: () -> Unit, modifier: Modifier = Modifier, viewModel: VocabularyViewModel
) {
    val savedWords by viewModel.savedWords.collectAsState()

    Box(
        modifier = modifier.fillMaxSize().background(
                brush = Brush.linearGradient(
                    listOf(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.secondaryContainer
                    )
                )
            )
    ) {
        Image(
            painter = branchTopRightPainter(),
            contentDescription = null,
            modifier = Modifier.size(1000.dp) // adjust size as needed
                .align(Alignment.TopEnd).offset(y = (-150).dp)

        )

        Image(
            painter = branchBottomLeftPainter(),
            contentDescription = null,
            modifier = Modifier.size(1000.dp).align(Alignment.BottomStart).offset(y = 150.dp)

        )
        Scaffold(
            topBar = {
                StandardTopAppBar(
                    title = "Saved Words", onBackClick = onBackClick
                )
            }, containerColor = Color.Transparent
        ) { paddingValues ->
            if (savedWords.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("You haven't saved any words yet.")
                }
            } else {
                val pagerState = rememberPagerState(pageCount = { savedWords.size })
                HorizontalPager(
                    state = pagerState, modifier = Modifier.fillMaxSize().padding(paddingValues),
                ) { page ->
                    Flashcard(
                        vocabularyWord = savedWords[page], onDeleteClick = {
                            savedWords[page].id?.let { id ->
                                viewModel.deleteWord(id)
                            }
                        },
                        modifier = Modifier.padding(16.dp),
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}
