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
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.graphics.Color
import hirify.analytics.core.analytics.VacancyFilter

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
    var showMenu by remember { mutableStateOf(false) }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isPortrait = maxHeight > maxWidth

        LaunchedEffect(isPortrait) {
            isSidebarVisible = !isPortrait
        }

        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                TopAppBar(
                    title = { Text("hirify analytics", maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold) },
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
                                val buttonText = if (isRecording) {
                                    if (isPortrait) "Stop" else "Stop Recording"
                                } else {
                                    if (isPortrait) "Mic" else "Voice Dictation"
                                }
                                Text(buttonText, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                            }
                        }
                        val uriHandler = LocalUriHandler.current

                        if (!isPortrait) {
                            IconButton(onClick = { uriHandler.openUri((state.seriesList.getOrNull(state.activeSeriesIndex)?.filter ?: VacancyFilter()).toHirifyWebUrl()) }) {
                                Icon(Icons.Filled.OpenInBrowser, contentDescription = "Open in browser")
                            }
                            IconButton(onClick = { navigator.push(AboutScreen()) }) {
                                Icon(Icons.Filled.Info, contentDescription = "About")
                            }
                            IconButton(onClick = { navigator.push(AiSettingsScreen()) }) {
                                Icon(androidx.compose.material.icons.Icons.Filled.Settings, contentDescription = "Settings")
                            }
                        } else {
                            Box {
                                IconButton(onClick = { showMenu = true }) {
                                    Text("⋮", style = MaterialTheme.typography.titleLarge, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                                }
                                DropdownMenu(
                                    expanded = showMenu,
                                    onDismissRequest = { showMenu = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Open in browser") },
                                        onClick = { 
                                            showMenu = false
                                            uriHandler.openUri((state.seriesList.getOrNull(state.activeSeriesIndex)?.filter ?: VacancyFilter()).toHirifyWebUrl()) 
                                        },
                                        leadingIcon = { Icon(Icons.Filled.OpenInBrowser, contentDescription = null) }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("About") },
                                        onClick = { 
                                            showMenu = false
                                            navigator.push(AboutScreen()) 
                                        },
                                        leadingIcon = { Icon(Icons.Filled.Info, contentDescription = null) }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Settings") },
                                        onClick = { 
                                            showMenu = false
                                            navigator.push(AiSettingsScreen()) 
                                        },
                                        leadingIcon = { Icon(androidx.compose.material.icons.Icons.Filled.Settings, contentDescription = null) }
                                    )
                                }
                            }
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
                    val activeSeries = state.seriesList.getOrNull(state.activeSeriesIndex)
                    if (activeSeries != null) {
                        LeftSidebar(
                            filter = activeSeries.filter,
                            onFilterChanged = { viewModel.updateFilter(it) }
                        )
                    }
                }
            }

            Surface(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp,
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Series Tabs
                    ScrollableTabRow(
                        selectedTabIndex = state.activeSeriesIndex,
                        edgePadding = 8.dp,
                        containerColor = MaterialTheme.colorScheme.surface,
                        divider = {},
                        indicator = {}
                    ) {
                        state.seriesList.forEachIndexed { index, series ->
                            val color = hirify.analytics.ui.render.chartColors[index % hirify.analytics.ui.render.chartColors.size]
                            val isSelected = state.activeSeriesIndex == index
                            
                            Surface(
                                shape = MaterialTheme.shapes.small,
                                color = if (isSelected) color.copy(alpha = 0.1f) else Color.Transparent,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) color else MaterialTheme.colorScheme.outlineVariant),
                                modifier = Modifier.padding(4.dp).height(32.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 12.dp).clickable {
                                        if (isSelected && isPortrait) {
                                            isSidebarVisible = true
                                        } else {
                                            viewModel.selectSeries(index)
                                        }
                                    }
                                ) {
                                    Box(modifier = Modifier.size(8.dp).background(color, androidx.compose.foundation.shape.CircleShape))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = series.filter.toShortLabel(),
                                        color = if (isSelected) color else MaterialTheme.colorScheme.onSurface,
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                    if (series.isLoading) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        CircularProgressIndicator(modifier = Modifier.size(12.dp), color = color, strokeWidth = 2.dp)
                                    }
                                    if (state.seriesList.size > 1) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        IconButton(onClick = { viewModel.removeSeries(index) }, modifier = Modifier.size(16.dp)) {
                                            Icon(Icons.Default.Close, contentDescription = "Remove", tint = if (isSelected) color else MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                }
                            }
                        }
                        
                        if (state.seriesList.size < 5) {
                            IconButton(onClick = { viewModel.addSeries() }, modifier = Modifier.padding(4.dp)) {
                                Icon(Icons.Default.Add, contentDescription = "Add Series")
                            }
                        }
                    }
                    
                    Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                        val activeSeries = state.seriesList.getOrNull(state.activeSeriesIndex)
                        if (activeSeries?.error != null) {
                            Text(text = activeSeries.error!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
                        } else {
                            ChartRenderer(
                                seriesList = state.seriesList,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
        }
    }
}

fun VacancyFilter.toShortLabel(): String {
    val parts = mutableListOf<String>()
    if (!skills.isNullOrEmpty()) parts.add(skills!!.split(",").first().trim())
    if (!specializations.isNullOrEmpty()) parts.add(specializations!!.split(",").first().trim())
    if (!grade.isNullOrEmpty()) {
        val gradeValue = grade!!.split(",").first().trim()
        val mappedGrade = when (gradeValue) {
            "trainee" -> "Стажер"
            "junior" -> "Джуниор"
            "middle" -> "Мидл"
            "senior" -> "Сеньор"
            "lead" -> "Лид"
            "head" -> "Head"
            "director" -> "Директор"
            "c_level" -> "C-level"
            else -> gradeValue
        }
        parts.add(mappedGrade)
    }
    
    if (parts.isEmpty()) return "Все вакансии"
    val fullLabel = parts.joinToString(", ")
    return if (fullLabel.length > 20) fullLabel.take(17) + "..." else fullLabel
}
