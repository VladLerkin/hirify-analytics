import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class Test {
    val isRecording: StateFlow<Boolean> field = MutableStateFlow(false)
    
    fun update() {
        isRecording.value = true
    }
}
