package hirify.analytics.desktop

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import hirify.analytics.core.di.initKoin
import hirify.analytics.core.di.platformModule
import hirify.analytics.ui.App
import hirify.analytics.ui.di.uiModule

fun main() {
    initKoin(
        additionalModules = listOf(uiModule, platformModule)
    )
    application {
        Window(onCloseRequest = ::exitApplication, title = "Hirify Analytics") {
            App()
        }
    }
}
