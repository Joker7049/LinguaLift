package org.example.project.database

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlCursor
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import java.io.File

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        val userHome = System.getProperty("user.home")
        val dbFile = File(userHome, ".lingua_lift/AppDatabase.db")
        dbFile.parentFile.mkdirs()

        val driver = JdbcSqliteDriver("jdbc:sqlite:${dbFile.absolutePath}")

        // Get the current version from the database
        val currentVersion = driver.executeQuery(
            identifier = null,
            sql = "PRAGMA user_version;",
            mapper = { cursor ->
                if (cursor.next().value) {
                    QueryResult.Value(cursor.getLong(0)?.toInt() ?: 0)
                } else {
                    QueryResult.Value(0)
                }
            },
            parameters = 0,
            binders = null
        ).value ?: 0

        if (currentVersion == 0) {
            // This is a new database, create the schema
            AppDatabase.Schema.create(driver)
            driver.execute(null, "PRAGMA user_version = ${AppDatabase.Schema.version}", 0)
        } else {
            // Here is where you would handle migrations if the schema version changes
            if (AppDatabase.Schema.version > currentVersion) {
                AppDatabase.Schema.migrate(driver, currentVersion.toLong(), AppDatabase.Schema.version.toLong())
                driver.execute(null, "PRAGMA user_version = ${AppDatabase.Schema.version}", 0)
            }
        }
        return driver
    }
}