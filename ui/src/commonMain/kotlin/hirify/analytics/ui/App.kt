package hirify.analytics.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
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
            Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                Navigator(MainWorkspaceScreen()) { navigator ->
                    SlideTransition(navigator)
                }
            }
        }
    }
}
