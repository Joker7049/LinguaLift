package org.example.project

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.kamel.core.config.KamelConfig
import io.kamel.core.config.takeFrom
import io.kamel.image.config.Default
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