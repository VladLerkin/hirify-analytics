package hirify.analytics.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hirify.analytics.core.ai.VoiceInputProcessor
import hirify.analytics.core.analytics.CountResponse
import hirify.analytics.core.analytics.HirifyApiClient
import hirify.analytics.core.analytics.VacancyFilter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch

data class ChartSeries(
        val id: Long = kotlin.random.Random.nextLong(),
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

    val state: StateFlow<MainState>
        field = MutableStateFlow(MainState())

    /** In-flight load per series id; a newer load cancels the previous one so stale responses can't overwrite fresh data. */
    private val loadJobs = mutableMapOf<Long, Job>()

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
        val currentIndex = state.value.activeSeriesIndex
        updateSeries(currentIndex) { it.copy(filter = filter) }
        loadDataForSeries(currentIndex)
    }

    /** Applies [transform] to the series at [index], ignoring indices that no longer exist. */
    private inline fun updateSeries(index: Int, crossinline transform: (ChartSeries) -> ChartSeries) {
        state.update { current ->
            if (index !in current.seriesList.indices) return@update current
            current.copy(
                seriesList = current.seriesList.mapIndexed { i, series -> if (i == index) transform(series) else series }
            )
        }
    }

    /** Applies [transform] to the series with [id], ignoring series that no longer exist. */
    private inline fun updateSeriesById(id: Long, crossinline transform: (ChartSeries) -> ChartSeries) {
        state.update { current ->
            current.copy(seriesList = current.seriesList.map { if (it.id == id) transform(it) else it })
        }
    }

    fun addSeries() {
        if (state.value.seriesList.size >= 5) return
        state.update { state ->
            val newList = state.seriesList + ChartSeries(filter = VacancyFilter())
            state.copy(seriesList = newList, activeSeriesIndex = newList.lastIndex)
        }
        loadDataForSeries(state.value.seriesList.lastIndex)
    }

    fun removeSeries(index: Int) {
        if (state.value.seriesList.size <= 1) return
        state.value.seriesList.getOrNull(index)?.let { loadJobs.remove(it.id)?.cancel() }
        state.update { state ->
            val newList = state.seriesList.toMutableList().apply { removeAt(index) }
            val newActiveIndex = if (state.activeSeriesIndex >= newList.size) newList.lastIndex else state.activeSeriesIndex
            state.copy(seriesList = newList, activeSeriesIndex = newActiveIndex)
        }
    }

    fun selectSeries(index: Int) {
        if (index in state.value.seriesList.indices) {
            state.update { it.copy(activeSeriesIndex = index) }
        }
    }

    fun reload() {
        val currentIndex = state.value.activeSeriesIndex
        loadDataForSeries(currentIndex)
    }

    fun toggleVoiceInput() {
        voiceInputProcessor.toggleRecording()
    }

    private fun loadDataForSeries(index: Int) {
        val series = state.value.seriesList.getOrNull(index) ?: return
        val id = series.id
        val filter = series.filter

        loadJobs.remove(id)?.cancel()
        if (!filter.hasNarrowingCriteria) {
            // Don't fetch the whole market: the UI prompts the user to pick filters instead.
            updateSeriesById(id) { it.copy(data = null, isLoading = false, error = null) }
            return
        }
        loadJobs[id] = viewModelScope.launch(Dispatchers.Default) {
            updateSeriesById(id) { it.copy(isLoading = true, error = null) }

            val result = apiClient.getAnalyticsCount(filter)
            // getAnalyticsCount swallows CancellationException, so bail out explicitly if superseded.
            ensureActive()
            // Drop the response if the series' filter changed while the request was in flight.
            updateSeriesById(id) { current ->
                if (current.filter != filter) return@updateSeriesById current
                result.fold(
                    onSuccess = { current.copy(data = it, isLoading = false) },
                    onFailure = { current.copy(error = it.message, isLoading = false) }
                )
            }
        }
    }
}
