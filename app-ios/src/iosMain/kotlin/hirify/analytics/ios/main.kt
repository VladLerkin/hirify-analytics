package hirify.analytics.ios

import androidx.compose.ui.window.ComposeUIViewController
import hirify.analytics.core.di.initKoin as koinInit
import hirify.analytics.core.di.platformModule
import hirify.analytics.ui.App
import hirify.analytics.ui.di.uiModule

fun MainViewController() = ComposeUIViewController { App() }

fun setupKoin() {
    KoinHelper.start()
}

object KoinHelper {
    private var isStarted = false

    fun start() {
        if (!isStarted) {
            koinInit(
                additionalModules = listOf(uiModule, platformModule)
            )
            isStarted = true
        }
    }
}
