package org.example.project.database


private var database: AppDatabase? = null

fun createDatabase(driverFactory: DatabaseDriverFactory): AppDatabase{
    val driver = driverFactory.createDriver()
    val db = AppDatabase(driver)
    database = db
    return db
}


fun getDatabase(): AppDatabase {
    return database ?: throw IllegalStateException("Database not initialized")
}