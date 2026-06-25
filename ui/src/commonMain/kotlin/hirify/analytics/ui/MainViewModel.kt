package hirify.analytics.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hirify.analytics.core.ai.VoiceInputProcessor
import hirify.analytics.core.analytics.CountResponse
import hirify.analytics.core.analytics.HirifyApiClient
import hirify.analytics.core.analytics.VacancyFilter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MainState(
        val filter: VacancyFilter = VacancyFilter(),
        val analyticsData: CountResponse? = null,
        val isLoading: Boolean = false,
        val error: String? = null
)

class MainViewModel(
        private val apiClient: HirifyApiClient,
        voiceInputProcessorFactory: (CoroutineScope) -> VoiceInputProcessor
) : ViewModel() {

    val voiceInputProcessor = voiceInputProcessorFactory(viewModelScope)

    private val _state = MutableStateFlow(MainState())
    val state: StateFlow<MainState> = _state.asStateFlow()

    init {
        loadData()

        viewModelScope.launch {
            voiceInputProcessor.lastResult.collect { filter ->
                if (filter != null) {
                    updateFilter(filter)
                    voiceInputProcessor.clearResult()
                }
            }
        }
    }

    fun updateFilter(filter: VacancyFilter) {
        _state.update { it.copy(filter = filter) }
        loadData()
    }

    fun reload() {
        loadData()
    }

    fun toggleVoiceInput() {
        voiceInputProcessor.toggleRecording()
    }

    private fun loadData() =
            viewModelScope.launch(Dispatchers.Default) {
                _state.update { it.copy(isLoading = true, error = null) }

                val result = apiClient.getAnalyticsCount(_state.value.filter)
                result
                        .onSuccess { response ->
                            _state.update { it.copy(analyticsData = response, isLoading = false) }
                        }
                        .onFailure { error ->
                            _state.update { it.copy(error = error.message, isLoading = false) }
                        }
            }
}
