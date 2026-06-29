package hirify.analytics.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import hirify.analytics.core.di.coreModule
import hirify.analytics.ui.di.uiModule
import hirify.analytics.ui.theme.HirifyTheme
import org.koin.compose.KoinApplication



@Composable
fun App() {
    @Suppress("DEPRECATION")
    KoinApplication(application = {
        modules(coreModule, uiModule)
    }) {




        HirifyTheme {
            val storage = org.koin.compose.koinInject<hirify.analytics.core.ai.AiSettingsStorage>()
            var language by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(storage.loadConfig().interfaceLanguage) }
            val strings = androidx.compose.runtime.remember(language) { if (language == "ru") hirify.analytics.ui.i18n.RuStrings else hirify.analytics.ui.i18n.EnStrings }

            androidx.compose.runtime.CompositionLocalProvider(
                hirify.analytics.ui.i18n.LocalAppStrings provides strings,
                hirify.analytics.ui.i18n.LocalLanguageUpdater provides { newLang -> language = newLang }
            ) {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Navigator(MainWorkspaceScreen()) { navigator ->
                        SlideTransition(navigator)
                    }
                }
            }
        }
    }
}
