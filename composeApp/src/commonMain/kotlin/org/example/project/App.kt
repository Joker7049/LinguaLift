package org.example.project

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.example.project.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import kotlinx.coroutines.launch
import org.example.project.database.getDatabase
import org.example.project.navigation.Screen
import org.example.project.ui.FixMyEnglishScreen
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
        val vocabularyViewModel = remember { VocabularyViewModel(vocabularyQueries) }

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    Text(text = "LinguaLift", modifier = Modifier.padding(16.dp))
                    Divider()
                    NavigationDrawerItem(
                        label = { Text(text = "Home") },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(Screen.HomeScreen.route)
                        }
                    )
                    NavigationDrawerItem(
                        label = { Text(text = "Simplify Text") },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(Screen.SimplificationScreen.route)
                        }
                    )
                    NavigationDrawerItem(
                        label = { Text(text = "Fix My English") },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(Screen.FixMyEnglishScreen.route)
                        }
                    )
                    NavigationDrawerItem(
                        label = { Text(text = "Vocabulary Builder") },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(Screen.VocabularyBuilderScreen.route)
                        }
                    )
                    NavigationDrawerItem(
                        label = { Text(text = "Saved Words") },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(Screen.SavedWordsScreen.route)
                        }
                    )
                }
            }
        ){
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("LinguaLift") },
                        navigationIcon = {
                            IconButton(onClick = {
                                scope.launch {
                                    drawerState.apply {
                                        if (isClosed) open() else close()
                                    }
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Menu"
                                )
                            }
                        }
                    )
                }
            ){ paddingValues ->
                NavHost(
                    navController = navController,
                    startDestination = Screen.HomeScreen.route,
                    modifier = Modifier.padding(paddingValues)
                ) {
                    composable(Screen.HomeScreen.route) {
                        Text("HomeScreen")
                    }
                    composable(Screen.SimplificationScreen.route) {
                        SimplificationScreen()
                    }
                    composable(Screen.FixMyEnglishScreen.route) {
                        FixMyEnglishScreen()
                    }
                    composable(Screen.VocabularyBuilderScreen.route){
                        VocabularyBuilderScreen(viewModel = vocabularyViewModel)
                    }
                    composable(Screen.SavedWordsScreen.route) {
                        SavedWordsScreen(viewModel = vocabularyViewModel)
                    }
                }


            }

        }
    }
}