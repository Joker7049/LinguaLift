package org.example.project.util

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUUID

@OptIn(ExperimentalForeignApi::class)
actual suspend fun copyImageToInternalStorage(uriString: String, word: String): String? {
    val fileManager = NSFileManager.defaultManager
    val documentsPath = fileManager.URLsForDirectory(
        platform.Foundation.NSDocumentDirectory,
        platform.Foundation.NSUserDomainMask
    ).first() as NSURL

    val imagesDir = documentsPath.URLByAppendingPathComponent("word_images")
    if (!fileManager.fileExistsAtPath(imagesDir?.path!!)) {
        fileManager.createDirectoryAtURL(imagesDir, true, null, null)
    }

    val sourceUrl = NSURL.fileURLWithPath(uriString)
    
    // Create a unique file name using a UUID
    val fileName = "${NSUUID().UUIDString()}.jpg"
    val destinationUrl = imagesDir.URLByAppendingPathComponent(fileName)

    return try {
        fileManager.copyItemAtURL(sourceUrl, destinationUrl!!, null)
        destinationUrl?.path
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}