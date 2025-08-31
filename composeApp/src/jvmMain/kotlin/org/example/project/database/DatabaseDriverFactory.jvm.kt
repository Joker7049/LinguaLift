package org.example.project.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import java.io.File

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        val userHome = System.getProperty("user.home")
        val dbFile = File(userHome, ".lingua_lift/AppDatabase.db")

        // Ensure the directory exists
        dbFile.parentFile.mkdirs()

        val driver: SqlDriver = JdbcSqliteDriver("jdbc:sqlite:${dbFile.absolutePath}")

        // If the database file doesn't exist, create the schema
        if (!dbFile.exists()) {
            AppDatabase.Schema.create(driver)
        }

        return driver
    }
}