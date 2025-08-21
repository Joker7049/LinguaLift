package org.example.project.navigation

sealed class Screen(val route: String) {
    object HomeScreen : Screen("home_screen")
    object SimplificationScreen : Screen("simplification_screen")
    object FixMyEnglishScreen : Screen("fix_my_english_screen")
}