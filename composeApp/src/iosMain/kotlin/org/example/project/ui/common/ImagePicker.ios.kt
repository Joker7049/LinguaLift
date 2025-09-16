package org.example.project.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.Foundation.NSItemProviderReadingProtocol
import platform.Foundation.NSURL
import platform.PhotosUI.PHPickerConfiguration
import platform.PhotosUI.PHPickerResult
import platform.PhotosUI.PHPickerViewController
import platform.PhotosUI.PHPickerViewControllerDelegateProtocol
import platform.UIKit.UIApplication
import platform.darwin.NSObject




@Composable
actual fun ImagePicker(
    show: Boolean,
    onImageSelected: (String?) -> Unit
) {
    val picker = remember {
        PHPicker(onImageSelected)
    }
    if (show) {
        picker.present()
    }
}

private class PHPicker(
    private val onImageSelected: (String?) -> Unit
) : NSObject(), PHPickerViewControllerDelegateProtocol {

    fun present() {
        val configuration = PHPickerConfiguration()
        configuration.setFilter(null) // null filter means all assets, we can refine this
        val picker = PHPickerViewController(configuration)
        picker.delegate = this

        val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
        rootViewController?.presentViewController(picker, animated = true, completion = null)
    }

    override fun picker(picker: PHPickerViewController, didFinishPicking: List<*>) {
        picker.dismissViewControllerAnimated(true, null)

        val result = didFinishPicking.firstOrNull() as? PHPickerResult
        val provider = result?.itemProvider

        // Use the correct Kotlin ::class syntax
        if (provider?.canLoadObjectOfClass(NSURL as NSItemProviderReadingProtocol) == true) {
            provider.loadObjectOfClass(NSURL as NSItemProviderReadingProtocol) { url, _ ->
                val nsUrl = url as? NSURL
                onImageSelected(nsUrl?.path)
            }
        } else {
            onImageSelected(null)
        }
    }
}