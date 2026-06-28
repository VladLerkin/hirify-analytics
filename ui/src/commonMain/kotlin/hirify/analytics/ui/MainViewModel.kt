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

data class ChartSeries(
        val filter: VacancyFilter = VacancyFilter(),
        val data: CountResponse? = null,
        val isLoading: Boolean = false,
        val error: String? = null
)

data class MainState(
        val seriesList: List<ChartSeries> = listOf(ChartSeries()),
        val activeSeriesIndex: Int = 0
)

class MainViewModel(
        private val apiClient: HirifyApiClient,
        voiceInputProcessorFactory: (CoroutineScope) -> VoiceInputProcessor
) : ViewModel() {

    val voiceInputProcessor = voiceInputProcessorFactory(viewModelScope)

    private val _state = MutableStateFlow(MainState())
    val state: StateFlow<MainState> = _state.asStateFlow()

    init {
        loadDataForSeries(0)

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
        val currentIndex = _state.value.activeSeriesIndex
        _state.update { state ->
            val updatedList = state.seriesList.toMutableList()
            updatedList[currentIndex] = updatedList[currentIndex].copy(filter = filter)
            state.copy(seriesList = updatedList)
        }
        loadDataForSeries(currentIndex)
    }

    fun addSeries() {
        if (_state.value.seriesList.size >= 5) return
        _state.update { state ->
            val newList = state.seriesList + ChartSeries(filter = VacancyFilter())
            state.copy(seriesList = newList, activeSeriesIndex = newList.lastIndex)
        }
        loadDataForSeries(_state.value.seriesList.lastIndex)
    }

    fun removeSeries(index: Int) {
        if (_state.value.seriesList.size <= 1) return
        _state.update { state ->
            val newList = state.seriesList.toMutableList().apply { removeAt(index) }
            val newActiveIndex = if (state.activeSeriesIndex >= newList.size) newList.lastIndex else state.activeSeriesIndex
            state.copy(seriesList = newList, activeSeriesIndex = newActiveIndex)
        }
    }

    fun selectSeries(index: Int) {
        if (index in _state.value.seriesList.indices) {
            _state.update { it.copy(activeSeriesIndex = index) }
        }
    }

    fun reload() {
        val currentIndex = _state.value.activeSeriesIndex
        loadDataForSeries(currentIndex)
    }

    fun toggleVoiceInput() {
        voiceInputProcessor.toggleRecording()
    }

    private fun loadDataForSeries(index: Int) {
        if (index !in _state.value.seriesList.indices) return

        viewModelScope.launch(Dispatchers.Default) {
            _state.update { state ->
                val updatedList = state.seriesList.toMutableList()
                updatedList[index] = updatedList[index].copy(isLoading = true, error = null)
                state.copy(seriesList = updatedList)
            }

            val filter = _state.value.seriesList[index].filter
            val result = apiClient.getAnalyticsCount(filter)
            
            result
                    .onSuccess { response ->
                        _state.update { state ->
                            val updatedList = state.seriesList.toMutableList()
                            if (index < updatedList.size) { // check bounds in case it was removed
                                updatedList[index] = updatedList[index].copy(data = response, isLoading = false)
                            }
                            state.copy(seriesList = updatedList)
                        }
                    }
                    .onFailure { error ->
                        _state.update { state ->
                            val updatedList = state.seriesList.toMutableList()
                            if (index < updatedList.size) {
                                updatedList[index] = updatedList[index].copy(error = error.message, isLoading = false)
                            }
                            state.copy(seriesList = updatedList)
                        }
                    }
        }
    }
}
