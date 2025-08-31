import SwiftUI
import ComposeApp

@main
struct iOSApp: App {
    init(){
        DatabaseInitializerKt.doInitDatabase()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}