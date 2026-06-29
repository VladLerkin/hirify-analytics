package hirify.analytics.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import hirify.analytics.core.ai.AiConfig
import hirify.analytics.core.ai.AiPresets
import hirify.analytics.core.ai.AiSettingsStorage
import hirify.analytics.core.ai.AiClientFactory
import hirify.analytics.core.ai.sendPromptSafe
import hirify.analytics.core.ai.AiResult
import org.koin.compose.koinInject
import kotlinx.coroutines.launch
import hirify.analytics.core.ai.agent.AgentService
import hirify.analytics.ui.i18n.LocalAppStrings
import hirify.analytics.ui.i18n.LocalLanguageUpdater

class AiSettingsScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val storage = koinInject<AiSettingsStorage>()
        val aiClientFactory = koinInject<AiClientFactory>()
        val initialConfig = remember { storage.loadConfig() }
        
        val presets = AiPresets.getAllPresets()
        val initialPresetIndex = presets.indexOfFirst { (_, preset) -> 
            preset.model == initialConfig.model && preset.provider == initialConfig.provider
        }.let { if (it >= 0) it else 0 }
        
        var selectedPresetIndex by remember { mutableStateOf(initialPresetIndex) }
        var provider by remember { mutableStateOf(initialConfig.provider) }
        @Suppress("DEPRECATION")
        var apiKey by remember { mutableStateOf(initialConfig.apiKey) }
        var model by remember { mutableStateOf(initialConfig.model) }
        var baseUrl by remember { mutableStateOf(initialConfig.baseUrl) }
        var temperature by remember { mutableStateOf(initialConfig.temperature.toString()) }
        var maxTokens by remember { mutableStateOf(initialConfig.maxTokens.toString()) }
        var language by remember { mutableStateOf(initialConfig.language) }
        var transcriptionProvider by remember { mutableStateOf(initialConfig.transcriptionProvider) }
        @Suppress("DEPRECATION")
        var googleApiKey by remember { mutableStateOf(initialConfig.googleApiKey) }
        
        var openAiKey by remember { mutableStateOf(initialConfig.openaiApiKey) }
        var googleKey by remember { mutableStateOf(initialConfig.googleAiApiKey) }
        var yandexKey by remember { mutableStateOf(initialConfig.yandexApiKey) }
        var yandexFolderId by remember { mutableStateOf(initialConfig.yandexFolderId) }
        var hirifyKey by remember { mutableStateOf(initialConfig.hirifyApiKey) }
        var interfaceLanguage by remember { mutableStateOf(initialConfig.interfaceLanguage) }
        val scope = rememberCoroutineScope()
        
        val strings = LocalAppStrings.current
        val updateLanguage = LocalLanguageUpdater.current
        
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(strings.aiSettingsTitle) },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = strings.back)
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            @Suppress("DEPRECATION")
                            val config = AiConfig(
                                provider = provider,
                                apiKey = apiKey,
                                model = model,
                                baseUrl = baseUrl,
                                temperature = temperature.toDoubleOrNull() ?: 0.7,
                                maxTokens = maxTokens.toIntOrNull() ?: 4000,
                                language = language,
                                transcriptionProvider = transcriptionProvider,
                                googleApiKey = googleApiKey,
                                openaiApiKey = openAiKey,
                                googleAiApiKey = googleKey,
                                yandexApiKey = yandexKey,
                                yandexFolderId = yandexFolderId,
                                hirifyApiKey = hirifyKey,
                                interfaceLanguage = interfaceLanguage
                            )
                            storage.saveConfig(config)
                            navigator.pop()
                        }) {
                            Icon(Icons.Default.Check, contentDescription = strings.save)
                        }
                    }
                )
            }
        ) { paddingValues ->
            Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.TopCenter) {
                Column(
                    modifier = Modifier
                        .widthIn(max = 600.dp)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    Text(
                        text = strings.hirifyKeyWarning,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    ApiKeyTextField(
                        value = hirifyKey,
                        onValueChange = { hirifyKey = it },
                        label = strings.hirifyKeyLabel,
                        placeholder = "hrf_...",
                        supportingText = strings.hirifyKeySupportingText,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                    )
                    
                    HorizontalDivider(modifier = Modifier.padding(bottom = 16.dp))
                    
                    Text(
                        text = strings.modelSelectionWarning,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    Text(
                        text = strings.presetsLabel,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    var presetsExpanded by remember { mutableStateOf(false) }
                    
                    Box(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                        OutlinedButton(
                            onClick = { presetsExpanded = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(presets.getOrNull(selectedPresetIndex)?.first ?: strings.selectPreset)
                        }
                        DropdownMenu(
                            expanded = presetsExpanded,
                            onDismissRequest = { presetsExpanded = false },
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            presets.forEachIndexed { index, (name, preset) ->
                                DropdownMenuItem(
                                    text = { Text(name) },
                                    onClick = {
                                        selectedPresetIndex = index
                                        provider = preset.provider
                                        model = preset.model
                                        baseUrl = preset.baseUrl
                                        temperature = preset.temperature.toString()
                                        maxTokens = preset.maxTokens.toString()
                                        presetsExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    
                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                    
                    when (provider) {
                        "OPENAI" -> {
                            ApiKeyTextField(
                                value = openAiKey,
                                onValueChange = { openAiKey = it },
                                label = strings.openaiApiKeyLabel,
                                placeholder = "sk-...",
                                supportingText = strings.keyStoredInMemory,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        "GOOGLE" -> {
                            ApiKeyTextField(
                                value = googleKey,
                                onValueChange = { googleKey = it },
                                label = strings.googleApiKeyLabel,
                                placeholder = "AIza...",
                                supportingText = strings.keyStoredInMemory,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        "YANDEX" -> {
                            ApiKeyTextField(
                                value = yandexKey,
                                onValueChange = { yandexKey = it },
                                label = strings.yandexApiKeyLabel,
                                placeholder = "AQVN...",
                                supportingText = strings.keyStoredInMemory,
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = yandexFolderId,
                                onValueChange = { yandexFolderId = it },
                                label = { Text(strings.yandexFolderIdLabel) },
                                placeholder = { Text("default or b1g...") },
                                supportingText = { Text(strings.yandexFolderIdSupportingText) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp),
                                singleLine = true
                            )
                        }
                        "OLLAMA", "CUSTOM" -> {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 12.dp)) {
                                val ollamaCommand = "ollama run ${if (model.isNotBlank()) model else "qwen2.5:7b"}"
                                Text(
                                    text = strings.apiKeyNotRequired + ollamaCommand,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.weight(1f)
                                )
                                val clipboardManager = LocalClipboardManager.current
                                IconButton(onClick = {
                                    clipboardManager.setText(AnnotatedString(ollamaCommand))
                                }) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy Command", modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                    
                    if (provider == "OLLAMA" || provider == "CUSTOM") {
                        OutlinedTextField(
                            value = baseUrl,
                            onValueChange = { baseUrl = it },
                            label = { Text(strings.baseUrlLabel) },
                            placeholder = { 
                                Text(
                                    when (provider) {
                                        "OLLAMA" -> "http://localhost:11434"
                                        else -> "https://your-api.com/v1"
                                    }
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            singleLine = true
                        )
                    }
                    
                    OutlinedTextField(
                        value = model,
                        onValueChange = { model = it },
                        label = { Text(strings.modelLabel) },
                        placeholder = { Text("gpt-4o-mini") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        singleLine = true
                    )
                    
                    OutlinedTextField(
                        value = language,
                        onValueChange = { language = it },
                        label = { Text(strings.transcriptionLanguageLabel) },
                        placeholder = { Text("ka, ru, en, etc.") },
                        supportingText = { Text(strings.transcriptionLanguageSupportingText) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        singleLine = true
                    )
                    
                    Text(
                        text = strings.speechRecognitionProviderLabel,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                    )
                    
                    var transcriptionExpanded by remember { mutableStateOf(false) }
                    val transcriptionProviders = listOf(
                        "OPENAI_WHISPER" to strings.openaiWhisperProvider,
                        "VOSK_LOCAL" to strings.voskLocalProvider,
                        "GOOGLE_SPEECH" to strings.googleSpeechProvider,
                        "YANDEX_SPEECHKIT" to strings.yandexSpeechKitProvider
                    )
                    
                    Box(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                        OutlinedButton(
                            onClick = { transcriptionExpanded = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(transcriptionProviders.find { it.first == transcriptionProvider }?.second ?: strings.selectProvider)
                        }
                        DropdownMenu(
                            expanded = transcriptionExpanded,
                            onDismissRequest = { transcriptionExpanded = false },
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            transcriptionProviders.forEach { (id, name) ->
                                DropdownMenuItem(
                                    text = { Text(name) },
                                    onClick = {
                                        transcriptionProvider = id
                                        transcriptionExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    
                    // Vosk Download Manager
                    if (transcriptionProvider == "VOSK_LOCAL") {
                        val scope = rememberCoroutineScope()
                        val voskManager = remember { hirify.analytics.core.ai.VoskRecognizerManager() }
                        val currentLang = if (language.isBlank()) "ru" else language
                        var isDownloaded by remember(currentLang) { mutableStateOf(voskManager.isModelDownloaded(currentLang)) }
                        var downloadProgress by remember { mutableStateOf(-1f) }
                        var downloadError by remember { mutableStateOf<String?>(null) }
                        
                        if (isDownloaded) {
                            Text(
                                text = "✓ ${strings.modelDownloaded}",
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        } else {
                            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                                Text(
                                    text = strings.voskRequiresModel,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                
                                if (downloadProgress >= 0f && downloadProgress <= 1f) {
                                    LinearProgressIndicator(
                                        progress = { downloadProgress },
                                        modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)
                                    )
                                    Text("${(downloadProgress * 100).toInt()}% downloaded", style = MaterialTheme.typography.bodySmall)
                                } else {
                                    Button(onClick = {
                                        scope.launch {
                                            try {
                                                downloadProgress = 0f
                                                downloadError = null
                                                voskManager.downloadModel(currentLang) { progress ->
                                                    downloadProgress = progress
                                                }
                                                isDownloaded = true
                                                downloadProgress = -1f
                                            } catch (e: Exception) {
                                                downloadError = e.message ?: "Unknown error"
                                                downloadProgress = -1f
                                            }
                                        }
                                    }) {
                                        Text(strings.downloadModel)
                                    }
                                }
                                
                                if (downloadError != null) {
                                    Text(
                                        text = "${strings.error}: $downloadError",
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    if (transcriptionProvider == "OPENAI_WHISPER") {
                        ApiKeyTextField(
                            value = openAiKey,
                            onValueChange = { openAiKey = it },
                            label = strings.openaiWhisperApiKeyLabel,
                            placeholder = "sk-...",
                            supportingText = strings.openaiWhisperApiKeySupportingText,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    if (transcriptionProvider == "GOOGLE_SPEECH") {
                        ApiKeyTextField(
                            value = googleKey,
                            onValueChange = { googleKey = it },
                            label = strings.googleSpeechApiKeyLabel,
                            placeholder = "AIza...",
                            supportingText = strings.googleSpeechApiKeySupportingText,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    if (transcriptionProvider == "YANDEX_SPEECHKIT") {
                        ApiKeyTextField(
                            value = yandexKey,
                            onValueChange = { yandexKey = it },
                            label = strings.yandexSpeechKitApiKeyLabel,
                            placeholder = "AQVN...",
                            supportingText = strings.yandexSpeechKitApiKeySupportingText,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }


                    Text(
                        text = strings.advancedSettings,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                    )
                    
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                        Text(strings.interfaceLanguage, modifier = Modifier.weight(1f))
                        var langExpanded by remember { mutableStateOf(false) }
                        Box {
                            OutlinedButton(onClick = { langExpanded = true }) {
                                Text(if (interfaceLanguage == "ru") "Русский" else "English")
                            }
                            DropdownMenu(expanded = langExpanded, onDismissRequest = { langExpanded = false }) {
                                DropdownMenuItem(text = { Text("Русский") }, onClick = { interfaceLanguage = "ru"; updateLanguage("ru"); langExpanded = false })
                                DropdownMenuItem(text = { Text("English") }, onClick = { interfaceLanguage = "en"; updateLanguage("en"); langExpanded = false })
                            }
                        }
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 48.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = temperature,
                            onValueChange = { temperature = it },
                            label = { Text(strings.temperatureLabel) },
                            placeholder = { Text("0.7") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        
                        OutlinedTextField(
                            value = maxTokens,
                            onValueChange = { maxTokens = it },
                            label = { Text(strings.maxTokensLabel) },
                            placeholder = { Text("4000") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    var testConnectionResult by remember { mutableStateOf<String?>(null) }
                    var isTestingConnection by remember { mutableStateOf(false) }
                    var testConnectionSuccess by remember { mutableStateOf(false) }
                    
                    Button(
                        onClick = {
                            if (isTestingConnection) return@Button
                            isTestingConnection = true
                            testConnectionResult = strings.testingConnection
                            
                            scope.launch {
                                try {
                                    @Suppress("DEPRECATION")
                                    val currentConfig = AiConfig(
                                        provider = provider,
                                        apiKey = apiKey,
                                        model = model,
                                        baseUrl = baseUrl,
                                        temperature = temperature.toDoubleOrNull() ?: 0.7,
                                        maxTokens = maxTokens.toIntOrNull() ?: 4000,
                                        language = language,
                                        transcriptionProvider = transcriptionProvider,
                                        googleApiKey = googleApiKey,
                                        openaiApiKey = openAiKey,
                                        googleAiApiKey = googleKey,
                                        yandexApiKey = yandexKey,
                                        yandexFolderId = yandexFolderId,
                                        hirifyApiKey = hirifyKey,
                                        interfaceLanguage = interfaceLanguage
                                    )
                                    val client = aiClientFactory.createClient(currentConfig)
                                    val result = client.sendPromptSafe("Hello, are you there?", currentConfig)
                                    
                                    when (result) {
                                        is AiResult.Success -> {
                                            testConnectionSuccess = true
                                            testConnectionResult = "${strings.connectionSuccessful}\nResponse: ${result.text.take(100)}"
                                        }
                                        is AiResult.Error -> {
                                            testConnectionSuccess = false
                                            testConnectionResult = "${strings.connectionFailed}:\n${result.message}"
                                        }
                                    }
                                } catch (e: Exception) {
                                    testConnectionSuccess = false
                                    testConnectionResult = "${strings.error}: ${e.message}"
                                } finally {
                                    isTestingConnection = false
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    ) {
                        if (isTestingConnection) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp).padding(end = 8.dp), color = MaterialTheme.colorScheme.onSecondaryContainer, strokeWidth = 2.dp)
                        }
                        Text(strings.testAiConnection)
                    }
                    
                    if (testConnectionResult != null) {
                        Surface(
                            color = if (testConnectionSuccess) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer,
                            shape = MaterialTheme.shapes.small,
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                        ) {
                            Text(
                                text = testConnectionResult!!,
                                color = if (testConnectionSuccess) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
