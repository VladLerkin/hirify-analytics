package hirify.analytics.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.currentOrThrow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.ui.platform.LocalUriHandler
import hirify.analytics.ui.components.panels.LeftSidebar
import hirify.analytics.ui.render.ChartRenderer
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val viewModel = koinInject<MainViewModel>()
    val state by viewModel.state.collectAsState()
    
    val voiceProcessor = viewModel.voiceInputProcessor
    val isRecording by voiceProcessor.isRecording.collectAsState()
    val isProcessing by voiceProcessor.isProcessing.collectAsState()

    val navigator = cafe.adriel.voyager.navigator.LocalNavigator.currentOrThrow

    var isSidebarVisible by remember { mutableStateOf(true) }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isPortrait = maxHeight > maxWidth

        LaunchedEffect(isPortrait) {
            isSidebarVisible = !isPortrait
        }

        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                TopAppBar(
                    title = { Text("hirify analytics", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground
                    ),
                    navigationIcon = {
                        IconButton(onClick = { isSidebarVisible = !isSidebarVisible }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Toggle Sidebar")
                        }
                    },
                    actions = {
                        if (isProcessing) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        } else {
                            Button(
                                onClick = { viewModel.toggleVoiceInput() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isRecording) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                                    contentColor = if (isRecording) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onPrimary
                                )
                            ) {
                                Text(if (isRecording) "Stop Recording" else "Voice Dictation", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                            }
                        }
                        val uriHandler = LocalUriHandler.current
                        IconButton(onClick = { uriHandler.openUri(state.filter.toHirifyWebUrl()) }) {
                            Icon(Icons.Filled.OpenInBrowser, contentDescription = "Open in browser")
                        }
                        IconButton(onClick = { navigator.push(AboutScreen()) }) {
                            Icon(Icons.Filled.Info, contentDescription = "About")
                        }
                        IconButton(onClick = { navigator.push(AiSettingsScreen()) }) {
                            Icon(androidx.compose.material.icons.Icons.Filled.Settings, contentDescription = "Settings")
                        }
                    }
                )
            }
        ) { paddingValues ->
        Row(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AnimatedVisibility(visible = isSidebarVisible) {
                Surface(
                    modifier = Modifier.fillMaxHeight().width(320.dp),
                    shape = MaterialTheme.shapes.large,
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 2.dp,
                    shadowElevation = 1.dp
                ) {
                    LeftSidebar(
                        filter = state.filter,
                        onFilterChanged = { viewModel.updateFilter(it) }
                    )
                }
            }

            Surface(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp,
                shadowElevation = 1.dp
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    if (state.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    } else if (state.error != null) {
                        Text(text = state.error!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
                    } else {
                        ChartRenderer(
                            chartData = state.analyticsData,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
        }
    }
}
