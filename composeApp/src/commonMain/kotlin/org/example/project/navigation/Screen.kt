package org.example.project.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Translate
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object HomeScreen : Screen("home_screen", "Home", Icons.Default.Home)
    object SimplificationScreen : Screen("simplification_screen", "Simplify Text", Icons.Default.Translate)
    object FixMyEnglishScreen : Screen("fix_my_english_screen", "Fix My English", Icons.Default.Edit)
    object VocabularyBuilderScreen : Screen("vocabulary_builder_screen", "Vocabulary Builder", Icons.Default.LibraryBooks)
    object SavedWordsScreen : Screen("saved_words_screen", "Saved Words", Icons.Default.Bookmark)
    object QuizScreen : Screen("quiz_screen", "Quiz", Icons.Default.Bookmark)
}