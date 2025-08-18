# LinguaLift Project

## Project Overview

This is a Kotlin Multiplatform (KMP) project named **LinguaLift** that targets Android, iOS, and Desktop (JVM). The project is set up to share code between these platforms, with a focus on using Jetpack Compose for the UI.

The project is structured into two main modules:

*   `composeApp`: This is the shared module containing the common code for all platforms. It includes the UI written in Jetpack Compose and the business logic.
*   `iosApp`: This is the entry point for the iOS application. It includes the necessary SwiftUI code to host the Compose-based UI.

The project uses Gradle for dependency management and building.

## Building and Running

### Android

To run the Android application, you can use the following Gradle command:

```bash
./gradlew :composeApp:installDebug
```

This will build and install the debug version of the app on a connected Android device or emulator.

### iOS

To run the iOS application, you will need to open the `iosApp/iosApp.xcodeproj` file in Xcode and run the app from there.

### Desktop

To run the desktop application, you can use the following Gradle command:

```bash
./gradlew :composeApp:run
```

This will build and run the desktop application on your local machine.

## Development Conventions

*   **Shared Code:** All shared code should be placed in the `composeApp/src/commonMain/kotlin` directory.
*   **Platform-Specific Code:** Platform-specific code should be placed in the corresponding source sets, such as `composeApp/src/androidMain/kotlin` for Android and `composeApp/src/iosMain/kotlin` for iOS.
*   **UI:** The UI is written using Jetpack Compose in the `composeApp` module.
*   **Dependencies:** Dependencies are managed in the `build.gradle.kts` files. The `gradle/libs.versions.toml` file is used for version management.
