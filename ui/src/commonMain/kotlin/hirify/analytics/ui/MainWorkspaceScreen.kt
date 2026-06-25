package hirify.analytics.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.core.lifecycle.LifecycleEffect
import org.koin.compose.koinInject

class MainWorkspaceScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinInject<MainViewModel>()
        val state by viewModel.state.collectAsState()
        
        LifecycleEffect(
            onStarted = {
                viewModel.reload()
            }
        )

        // Host the existing main screen
        MainScreen()
    }
}
