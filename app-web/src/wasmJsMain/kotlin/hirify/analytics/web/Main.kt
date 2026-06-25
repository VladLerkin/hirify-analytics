package hirify.analytics.web

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import hirify.analytics.core.di.initKoin
import hirify.analytics.core.di.platformModule
import hirify.analytics.ui.App
import hirify.analytics.ui.di.uiModule
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    initKoin(
        additionalModules = listOf(uiModule, platformModule)
    )
    ComposeViewport(document.body!!) {
        App()
    }
}
