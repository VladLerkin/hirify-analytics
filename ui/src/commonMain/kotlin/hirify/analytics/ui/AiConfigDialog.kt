package hirify.analytics.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Clear
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import hirify.analytics.core.ai.AiConfig
import hirify.analytics.core.ai.AiPresets
import hirify.analytics.core.ai.VoskRecognizerManager
import hirify.analytics.core.ai.AiClientFactory
import hirify.analytics.core.ai.sendPromptSafe
import hirify.analytics.core.ai.AiResult
import kotlinx.coroutines.launch


/**
 * Dialog for configuring AI settings before text import.
 */
@Composable
fun AiConfigDialog(
    initialConfig: AiConfig = AiPresets.OPENAI_GPT4O_MINI,
    onDismiss: () -> Unit,
    onConfirm: (AiConfig) -> Unit
) {
    val aiClientFactory = org.koin.compose.koinInject<AiClientFactory>()
    val presets = AiPresets.getAllPresets()
    
    // Find the preset index that matches initialConfig.model
    val initialPresetIndex = presets.indexOfFirst { (_, preset) -> 
        preset.model == initialConfig.model && preset.provider == initialConfig.provider
    }.let { if (it >= 0) it else 0 }
    
    var selectedPresetIndex by remember { mutableStateOf(initialPresetIndex) }
    var provider by remember { mutableStateOf(initialConfig.provider) }
    @Suppress("DEPRECATION")
    var apiKey by remember { mutableStateOf(initialConfig.apiKey) }  // Deprecated, for backward compatibility
    var model by remember { mutableStateOf(initialConfig.model) }
    var baseUrl by remember { mutableStateOf(initialConfig.baseUrl) }
    var temperature by remember { mutableStateOf(initialConfig.temperature.toString()) }
    var maxTokens by remember { mutableStateOf(initialConfig.maxTokens.toString()) }
    var language by remember { mutableStateOf(initialConfig.language) }
    var transcriptionProvider by remember { mutableStateOf(initialConfig.transcriptionProvider) }
    @Suppress("DEPRECATION")
    var googleApiKey by remember { mutableStateOf(initialConfig.googleApiKey) }  // Deprecated
    
    // New separate API keys for each provider group
    var openAiKey by remember { mutableStateOf(initialConfig.openaiApiKey) }
    var googleKey by remember { mutableStateOf(initialConfig.googleAiApiKey) }
    var yandexKey by remember { mutableStateOf(initialConfig.yandexApiKey) }
    var yandexFolderId by remember { mutableStateOf(initialConfig.yandexFolderId) }
    var tavilyApiKey by remember { mutableStateOf(initialConfig.tavilyApiKey) }
    var autoresearchRepoPath by remember { mutableStateOf(initialConfig.autoresearchRepoPath) }
    var pamyatNarodaCookies by remember { mutableStateOf(initialConfig.pamyatNarodaCookies) }
    var familySearchCookies by remember { mutableStateOf(initialConfig.familySearchCookies) }
    
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .width(600.dp)
                .heightIn(max = 700.dp),
            shape = MaterialTheme.shapes.large,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState())
                ) {
                        // Preset selector
                        var presetsExpanded by remember { mutableStateOf(false) }
                        
                        Text(
                            text = "Presets:",
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
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
                        
                        // Show API key field depending on the selected provider
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
                                    supportingText = { Text("Yandex Cloud Folder ID. Leave as 'default' for automatic detection when using a service account API key.") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 12.dp),
                                    singleLine = true
                                )
                            }
                            "LOCAL_LLAMATIK" -> {
                                val scope = rememberCoroutineScope()
                                val localModelManager = org.koin.compose.koinInject<hirify.analytics.core.ai.LocalModelManager>()
                                var downloadProgress by remember { mutableStateOf(-1f) }
                                var downloadError by remember { mutableStateOf<String?>(null) }
                                var isDownloaded by remember { mutableStateOf(false) }
                                
                                Column(modifier = Modifier.padding(bottom = 12.dp)) {
                                    Text(
                                        text = "Local on-device execution requires downloading the model (~4.5 GB).",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                    
                                    if (isDownloaded) {
                                        Text(
                                            text = "✓ Model is downloaded or ready.",
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(vertical = 8.dp)
                                        )
                                    } else if (downloadProgress >= 0f && downloadProgress <= 1f) {
                                        LinearProgressIndicator(
                                            progress = { downloadProgress },
                                            modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)
                                        )
                                        Text("${(downloadProgress * 100).toInt()}% downloaded", style = MaterialTheme.typography.bodySmall)
                                    } else {
                                        Button(onClick = {
                                            scope.launch {
                                                downloadProgress = 0f
                                                downloadError = null
                                                try {
                                                    localModelManager.downloadModel(baseUrl, model).collect { status ->
                                                        when (status) {
                                                            is hirify.analytics.core.ai.DownloadStatus.Progress -> {
                                                                downloadProgress = status.progress
                                                            }
                                                            is hirify.analytics.core.ai.DownloadStatus.Finished -> {
                                                                isDownloaded = true
                                                                downloadProgress = -1f
                                                            }
                                                            is hirify.analytics.core.ai.DownloadStatus.Error -> {
                                                                downloadError = status.exception.message ?: "Unknown error"
                                                                downloadProgress = -1f
                                                            }
                                                        }
                                                    }
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
                            "OLLAMA", "CUSTOM" -> {
                                // For Ollama and Custom, no API key is required or baseUrl is used
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
                        
                        // Custom URL (for Ollama and Custom)
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
                        
                        // Model
                        var modelExpanded by remember { mutableStateOf(false) }
                        val predefinedModels = when (provider) {
                            "OPENAI" -> listOf("gpt-4o-mini", "gpt-4o", "gpt-4-turbo", "o1-mini", "o1")
                            "GOOGLE" -> listOf("gemini-2.5-flash", "gemini-2.0-flash", "gemini-1.5-flash", "gemini-1.5-pro", "gemini-2.0-pro-exp")
                            "YANDEX" -> listOf("yandexgpt-lite", "yandexgpt", "yandexgpt-32k")
                            else -> emptyList()
                        }
                        
                        Text(
                            text = "Model:",
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                        )
                        
                        if (predefinedModels.isNotEmpty()) {
                            Box(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                                OutlinedButton(
                                    onClick = { modelExpanded = true },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(if (model.isNotBlank()) model else "Select Model")
                                }
                                DropdownMenu(
                                    expanded = modelExpanded,
                                    onDismissRequest = { modelExpanded = false },
                                    modifier = Modifier.fillMaxWidth(0.9f)
                                ) {
                                    predefinedModels.forEach { modelName ->
                                        DropdownMenuItem(
                                            text = { Text(modelName) },
                                            onClick = {
                                                model = modelName
                                                modelExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        } else {
                            OutlinedTextField(
                                value = model,
                                onValueChange = { model = it },
                                label = { Text("Model") },
                                placeholder = { Text("Model name") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp),
                                singleLine = true
                            )
                        }
                        
                        // Language for voice transcription
                        OutlinedTextField(
                            value = language,
                            onValueChange = { language = it },
                            label = { Text("Transcription Language (ISO-639-1)") },
                            placeholder = { Text("ka, ru, en, etc.") },
                            supportingText = { Text("Language code for transcription (e.g., 'ka' for Georgian, 'ru' for Russian). Leave empty for auto-detection.") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            singleLine = true
                        )
                        
                        var transcriptionExpanded by remember { mutableStateOf(false) }
                        val transcriptionProviders = listOf(
                            "VOSK_LOCAL" to "Vosk Local (Offline & Free)",
                            "OPENAI_WHISPER" to "OpenAI Whisper",
                            "GOOGLE_SPEECH" to "Google Speech-to-Text (best for Georgian)",
                            "YANDEX_SPEECHKIT" to "Yandex SpeechKit (best for Russian and CIS languages)"
                        )
                        
                        // Transcription provider selection
                        Text(
                            text = "Speech Recognition Provider:",
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
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
                        
                        // OpenAI API Key (shown only when OpenAI Whisper is selected)
                        if (transcriptionProvider == "OPENAI_WHISPER") {
                            ApiKeyTextField(
                                value = openAiKey,
                                onValueChange = { openAiKey = it },
                                label = "OpenAI API Key (Whisper)",
                                placeholder = "sk-...",
                                supportingText = "API key for OpenAI Whisper transcription. Uses the same key as for GPT models.",
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        
                        // Google AI API Key (shown only when Google Speech is selected)
                        if (transcriptionProvider == "GOOGLE_SPEECH") {
                            ApiKeyTextField(
                                value = googleKey,
                                onValueChange = { googleKey = it },
                                label = "Google AI API Key (Speech-to-Text)",
                                placeholder = "AIza...",
                                supportingText = "API key for Google Speech-to-Text. Uses the same key as for Gemini models.",
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        
                        // Yandex API Key (shown only when Yandex SpeechKit is selected)
                        if (transcriptionProvider == "YANDEX_SPEECHKIT") {
                            ApiKeyTextField(
                                value = yandexKey,
                                onValueChange = { yandexKey = it },
                                label = "Yandex Cloud API Key (SpeechKit)",
                                placeholder = "AQVN...",
                                supportingText = "API key for Yandex SpeechKit. Get it from the Yandex Cloud Console.",
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        
                        // Vosk Download Manager
                        if (transcriptionProvider == "VOSK_LOCAL") {
                            val scope = rememberCoroutineScope()
                            val voskManager = remember { VoskRecognizerManager() }
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
                        
                        // Advanced settings
                        Text(
                            text = "Advanced Settings:",
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
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
                        
                        // Help text
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)) {
                            val ollamaCommand = "ollama run ${if (model.isNotBlank()) model else "qwen2.5:7b"}"
                            Text(
                                text = "OpenAI requires an API key. For Ollama, ensure the server is running (ollama serve).\nTo download the selected model run: $ollamaCommand",
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
                
                Spacer(modifier = Modifier.height(16.dp))
                
                var testConnectionResult by remember { mutableStateOf<String?>(null) }
                var isTestingConnection by remember { mutableStateOf(false) }
                var testConnectionSuccess by remember { mutableStateOf(false) }
                val scope = rememberCoroutineScope()
                
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
                                    tavilyApiKey = tavilyApiKey,
                                    autoresearchRepoPath = autoresearchRepoPath,
                                    pamyatNarodaCookies = pamyatNarodaCookies,
                                    familySearchCookies = familySearchCookies
                                )
                                val client = aiClientFactory.createClient(currentConfig)
                                val llmResult = client.sendPromptSafe("Hello, are you there?", currentConfig)
                                
                                val llmMessage = when (llmResult) {
                                    is AiResult.Success -> "LLM ($provider): Success"
                                    is AiResult.Error -> "LLM ($provider): Failed - ${llmResult.message}"
                                }
                                val llmSuccess = llmResult is AiResult.Success

                                var sttMessage = "STT ($transcriptionProvider): Ready (Offline)"
                                var sttSuccess = true

                                // Test STT connection using the equivalent LLM ping since the API keys are the same
                                val sttEquivalentProvider = when (transcriptionProvider) {
                                    "OPENAI_WHISPER" -> "OPENAI"
                                    "GOOGLE_SPEECH" -> "GOOGLE"
                                    "YANDEX_SPEECHKIT" -> "YANDEX"
                                    else -> null
                                }
                                
                                if (sttEquivalentProvider != null) {
                                    val sttConfig = currentConfig.copy(provider = sttEquivalentProvider)
                                    val sttClient = aiClientFactory.createClient(sttConfig)
                                    val sttResult = sttClient.sendPromptSafe("Hello?", sttConfig)
                                    
                                    sttMessage = when (sttResult) {
                                        is AiResult.Success -> "STT ($transcriptionProvider): Success"
                                        is AiResult.Error -> "STT ($transcriptionProvider): Failed - ${sttResult.message}"
                                    }
                                    sttSuccess = sttResult is AiResult.Success
                                }
                                
                                testConnectionSuccess = llmSuccess && sttSuccess
                                testConnectionResult = "$llmMessage\n$sttMessage"
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
                
                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            @Suppress("DEPRECATION")
                            val config = AiConfig(
                                provider = provider,
                                apiKey = apiKey,  // Deprecated, but kept for backward compatibility
                                model = model,
                                baseUrl = baseUrl,
                                temperature = temperature.toDoubleOrNull() ?: 0.7,
                                maxTokens = maxTokens.toIntOrNull() ?: 4000,
                                language = language,
                                transcriptionProvider = transcriptionProvider,
                                googleApiKey = googleApiKey,  // Deprecated
                                
                                // New fields for separate provider group keys
                                openaiApiKey = openAiKey,
                                googleAiApiKey = googleKey,
                                yandexApiKey = yandexKey,
                                yandexFolderId = yandexFolderId,
                                tavilyApiKey = tavilyApiKey,
                                autoresearchRepoPath = autoresearchRepoPath,
                                pamyatNarodaCookies = pamyatNarodaCookies,
                                familySearchCookies = familySearchCookies
                            )
                            onConfirm(config)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}

