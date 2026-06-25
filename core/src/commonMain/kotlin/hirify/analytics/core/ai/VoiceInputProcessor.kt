package hirify.analytics.core.ai

import hirify.analytics.core.analytics.VacancyFilter
import hirify.analytics.core.platform.VoiceRecorder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

class VoiceInputProcessor(
    private val voiceRecorder: VoiceRecorder,
    private val settingsStorage: AiSettingsStorage,
    private val transcriptionClientFactory: TranscriptionClientFactory,
    private val agentService: hirify.analytics.core.ai.agent.AgentService,
    private val coroutineScope: CoroutineScope
) {
    private var autoStopJob: Job? = null

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    private val _lastResult = MutableStateFlow<VacancyFilter?>(null)
    val lastResult: StateFlow<VacancyFilter?> = _lastResult.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun toggleRecording() {
        if (_isRecording.value) {
            stopRecording()
        } else {
            startRecording()
        }
    }

    private fun startRecording() {
        _errorMessage.value = null
        try {
            voiceRecorder.startRecording(
                onResult = { audioData ->
                    _isRecording.value = false
                    autoStopJob?.cancel()
                    processAudio(audioData)
                },
                onError = { error ->
                    _isRecording.value = false
                    autoStopJob?.cancel()
                    _errorMessage.value = error
                }
            )
            _isRecording.value = true
            
            autoStopJob?.cancel()
            autoStopJob = coroutineScope.launch {
                delay(3 * 60 * 1000L) // 3 minutes
                if (_isRecording.value) {
                    stopRecording()
                }
            }
        } catch (e: Exception) {
            _errorMessage.value = "Failed to start recording. Check permissions."
        }
    }

    private fun stopRecording() {
        autoStopJob?.cancel()
        voiceRecorder.stopRecording()
    }

    private fun processAudio(audioData: ByteArray) {
        _isProcessing.value = true
        coroutineScope.launch {
            try {
                val config = settingsStorage.loadConfig()
                val client = transcriptionClientFactory.createClient(config)
                val transcript = client.transcribeAudio(audioData, config)
                
                if (transcript.isNotBlank()) {
                    val filter = agentService.parseVoiceToFilter(transcript, config)
                    _lastResult.value = filter
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Transcription failed"
            } finally {
                _isProcessing.value = false
            }
        }
    }
    
    fun clearResult() {
        _lastResult.update { null }
    }
}
