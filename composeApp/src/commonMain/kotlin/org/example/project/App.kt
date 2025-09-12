package org.example.project

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.example.project.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import kotlinx.coroutines.launch
import org.example.project.data.ChatViewModel
import org.example.project.database.getDatabase
import org.example.project.navigation.Screen
import org.example.project.quiz.QuizViewModel
import org.example.project.ui.ChatScreen
import org.example.project.ui.FixMyEnglishScreen
import org.example.project.ui.HomeScreen
import org.example.project.ui.QuizScreen
import org.example.project.ui.SavedWordsScreen
import org.example.project.ui.SimplificationScreen
import org.example.project.ui.VocabularyBuilderScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App() {
    AppTheme {
        val navController = rememberNavController()
        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val scope = rememberCoroutineScope()

        val vocabularyQueries = getDatabase().vocabularyQueries
        val aiMemoryQueries = getDatabase().aiMemoryQueries
        val vocabularyViewModel = remember { VocabularyViewModel(vocabularyQueries) }
        val quizViewModel = remember { QuizViewModel(vocabularyQueries) }
        val chatViewModel = remember { ChatViewModel(vocabularyQueries, aiMemoryQueries) }

        val screens = listOf(
            Screen.HomeScreen,
            Screen.ChatScreen,
            Screen.VocabularyBuilderScreen,
            Screen.SavedWordsScreen,
            Screen.QuizScreen,
            Screen.SimplificationScreen,
            Screen.FixMyEnglishScreen
        )
        var selectedScreen by remember { mutableStateOf(screens[0]) }

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Icon(
                            imageVector = Icons.Default.Translate,
                            contentDescription = "LinguaLift Logo",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "LinguaLift",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    Divider()
                    screens.forEach { screen ->
                        NavigationDrawerItem(
                            label = { Text(text = screen.title) },
                            icon = { Icon(imageVector = screen.icon, contentDescription = screen.title) },
                            selected = screen == selectedScreen,
                            onClick = {
                                selectedScreen = screen
                                scope.launch { drawerState.close() }
                                if (screen.route == Screen.HomeScreen.route) {
                                    navController.popBackStack(Screen.HomeScreen.route, false)
                                } else {
                                    navController.navigate(screen.route)
                                }
                            }
                        )
                    }
                }
            }
        ){
            NavHost(
                navController = navController,
                startDestination = Screen.HomeScreen.route,
            ) {
                composable(Screen.HomeScreen.route) {
                    HomeScreen(
                        drawerState = drawerState,
                        onScreenSelected = { screen ->
                            navController.navigate(screen.route)
                        }
                    )
                }
                composable(Screen.SimplificationScreen.route) {
                    SimplificationScreen(onBackClick = { navController.popBackStack() })
                }
                composable(Screen.FixMyEnglishScreen.route) {
                    FixMyEnglishScreen(onBackClick = { navController.popBackStack() })
                }
                composable(Screen.VocabularyBuilderScreen.route){
                    VocabularyBuilderScreen(
                        onBackClick = { navController.popBackStack() },
                        viewModel = vocabularyViewModel
                    )
                }
                composable(Screen.SavedWordsScreen.route) {
                    SavedWordsScreen(
                        onBackClick = { navController.popBackStack() },
                        viewModel = vocabularyViewModel
                    )
                }
                composable(Screen.QuizScreen.route) {
                    QuizScreen(
                        onBackClick = { navController.popBackStack() },
                        viewModel = quizViewModel
                    )
                }
                composable(Screen.ChatScreen.route) {
                    ChatScreen(
                        onBackClick = { navController.popBackStack() },
                        viewModel = chatViewModel
                    )
                }
            }
        }

    }

}
