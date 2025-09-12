package org.example.project

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.example.project.database.DatabaseDriverFactory
import org.example.project.database.createDatabase

fun main() = application {
    createDatabase(DatabaseDriverFactory())


    Window(
        onCloseRequest = ::exitApplication,
        title = "LinguaLift",
    ) {
        App()
    }
}