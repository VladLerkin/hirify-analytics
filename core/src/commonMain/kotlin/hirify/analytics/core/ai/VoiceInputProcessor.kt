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

    val isRecording: StateFlow<Boolean>
        field = MutableStateFlow(false)

    val isProcessing: StateFlow<Boolean>
        field = MutableStateFlow(false)

    val lastResult: StateFlow<VacancyFilter?>
        field = MutableStateFlow(null)

    val errorMessage: StateFlow<String?>
        field = MutableStateFlow(null)

    fun toggleRecording() {
        if (isRecording.value) {
            stopRecording()
        } else {
            startRecording()
        }
    }

    private fun startRecording() {
        errorMessage.value = null
        try {
            voiceRecorder.startRecording(
                onResult = { audioData ->
                    isRecording.value = false
                    autoStopJob?.cancel()
                    processAudio(audioData)
                },
                onError = { error ->
                    isRecording.value = false
                    autoStopJob?.cancel()
                    errorMessage.value = error
                }
            )
            isRecording.value = true
            
            autoStopJob?.cancel()
            autoStopJob = coroutineScope.launch {
                delay(3 * 60 * 1000L) // 3 minutes
                if (isRecording.value) {
                    stopRecording()
                }
            }
        } catch (e: Exception) {
            errorMessage.value = "Failed to start recording. Check permissions."
        }
    }

    private fun stopRecording() {
        autoStopJob?.cancel()
        voiceRecorder.stopRecording()
    }

    private fun processAudio(audioData: ByteArray) {
        isProcessing.value = true
        coroutineScope.launch {
            try {
                val config = settingsStorage.loadConfig()
                val client = transcriptionClientFactory.createClient(config)
                val transcript = client.transcribeAudio(audioData, config)
                
                if (transcript.isNotBlank()) {
                    val filter = agentService.parseVoiceToFilter(transcript, config)
                    lastResult.value = filter
                }
            } catch (e: Exception) {
                errorMessage.value = e.message ?: "Transcription failed"
            } finally {
                isProcessing.value = false
            }
        }
    }
    
    fun clearResult() {
        lastResult.update { null }
    }
}
