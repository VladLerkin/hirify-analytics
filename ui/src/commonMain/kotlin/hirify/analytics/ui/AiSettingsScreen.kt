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
        val scope = rememberCoroutineScope()
        
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("AI Settings") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
                                hirifyApiKey = hirifyKey
                            )
                            storage.saveConfig(config)
                            navigator.pop()
                        }) {
                            Icon(Icons.Default.Check, contentDescription = "Save")
                        }
                    }
                )
            }
        ) { paddingValues ->
            Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.TopCenter) {
                Column(
                    modifier = Modifier
                        .widthIn(max = 600.dp)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "Для работы приложения необходим ключ аналитики Hirify. Если у вас его нет, обратитесь в службу поддержки Hirify.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    ApiKeyTextField(
                        value = hirifyKey,
                        onValueChange = { hirifyKey = it },
                        label = "Hirify Analytics API Key",
                        placeholder = "hrf_...",
                        supportingText = "API key for Hirify Vacancy Analytics.",
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                    )
                    
                    HorizontalDivider(modifier = Modifier.padding(bottom = 16.dp))
                    
                    Text(
                        text = "Выберите AI-модель для распознавания голоса и автоматической настройки фильтров. Это позволяет получать аналитику по вакансиям, просто произнося свои пожелания вслух.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    Text(
                        text = "Presets:",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    var presetsExpanded by remember { mutableStateOf(false) }
                    
                    Box(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                        OutlinedButton(
                            onClick = { presetsExpanded = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(presets.getOrNull(selectedPresetIndex)?.first ?: "Select Preset")
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
                                label = "OpenAI API Key",
                                placeholder = "sk-...",
                                supportingText = "Provided key is stored in memory and masked in logs.",
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        "GOOGLE" -> {
                            ApiKeyTextField(
                                value = googleKey,
                                onValueChange = { googleKey = it },
                                label = "Google AI API Key",
                                placeholder = "AIza...",
                                supportingText = "Provided key is stored in memory and masked in logs.",
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        "YANDEX" -> {
                            ApiKeyTextField(
                                value = yandexKey,
                                onValueChange = { yandexKey = it },
                                label = "YandexGPT API Key",
                                placeholder = "AQVN...",
                                supportingText = "Provided key is stored in memory and masked in logs.",
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = yandexFolderId,
                                onValueChange = { yandexFolderId = it },
                                label = { Text("Folder ID (optional)") },
                                placeholder = { Text("default or b1g...") },
                                supportingText = { Text("Yandex Cloud Folder ID. Leave as 'default' for automatic detection.") },
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
                                    text = "API key is not required for local models\nTo download the selected model run: $ollamaCommand",
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
                            label = { Text("Base URL") },
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
                        label = { Text("Model") },
                        placeholder = { Text("gpt-4o-mini") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        singleLine = true
                    )
                    
                    OutlinedTextField(
                        value = language,
                        onValueChange = { language = it },
                        label = { Text("Transcription Language (ISO-639-1)") },
                        placeholder = { Text("ka, ru, en, etc.") },
                        supportingText = { Text("Language code for transcription. Leave empty for auto-detection.") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        singleLine = true
                    )
                    
                    Text(
                        text = "Speech Recognition Provider:",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                    )
                    
                    var transcriptionExpanded by remember { mutableStateOf(false) }
                    val transcriptionProviders = listOf(
                        "VOSK_LOCAL" to "Vosk Local (Offline & Free)",
                        "OPENAI_WHISPER" to "OpenAI Whisper",
                        "GOOGLE_SPEECH" to "Google Speech-to-Text",
                        "YANDEX_SPEECHKIT" to "Yandex SpeechKit"
                    )
                    
                    Box(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                        OutlinedButton(
                            onClick = { transcriptionExpanded = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(transcriptionProviders.find { it.first == transcriptionProvider }?.second ?: "Select Provider")
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
                                text = "✓ Model for '$currentLang' is downloaded and ready.",
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        } else {
                            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                                Text(
                                    text = "Vosk requires a ~45MB language model to be downloaded for offline use.",
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
                                        Text("Download Model")
                                    }
                                }
                                
                                if (downloadError != null) {
                                    Text(
                                        text = "Error: $downloadError",
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
                            label = "OpenAI API Key (Whisper)",
                            placeholder = "sk-...",
                            supportingText = "API key for OpenAI Whisper transcription.",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    if (transcriptionProvider == "GOOGLE_SPEECH") {
                        ApiKeyTextField(
                            value = googleKey,
                            onValueChange = { googleKey = it },
                            label = "Google AI API Key (Speech-to-Text)",
                            placeholder = "AIza...",
                            supportingText = "API key for Google Speech-to-Text.",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    if (transcriptionProvider == "YANDEX_SPEECHKIT") {
                        ApiKeyTextField(
                            value = yandexKey,
                            onValueChange = { yandexKey = it },
                            label = "Yandex Cloud API Key (SpeechKit)",
                            placeholder = "AQVN...",
                            supportingText = "API key for Yandex SpeechKit.",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }


                    Text(
                        text = "Advanced Settings:",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 48.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = temperature,
                            onValueChange = { temperature = it },
                            label = { Text("Temperature") },
                            placeholder = { Text("0.7") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        
                        OutlinedTextField(
                            value = maxTokens,
                            onValueChange = { maxTokens = it },
                            label = { Text("Max Tokens") },
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
                            testConnectionResult = "Testing connection..."
                            
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
                                        hirifyApiKey = hirifyKey
                                    )
                                    val client = aiClientFactory.createClient(currentConfig)
                                    val result = client.sendPromptSafe("Hello, are you there?", currentConfig)
                                    
                                    when (result) {
                                        is AiResult.Success -> {
                                            testConnectionSuccess = true
                                            testConnectionResult = "Connection successful!\nResponse: ${result.text.take(100)}"
                                        }
                                        is AiResult.Error -> {
                                            testConnectionSuccess = false
                                            testConnectionResult = "Connection failed:\n${result.message}"
                                        }
                                    }
                                } catch (e: Exception) {
                                    testConnectionSuccess = false
                                    testConnectionResult = "Error: ${e.message}"
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
                        Text("Test AI Connection")
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
