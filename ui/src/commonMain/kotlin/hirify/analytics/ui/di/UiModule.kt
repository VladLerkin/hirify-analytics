package hirify.analytics.ui.di

import hirify.analytics.core.ai.VoiceInputProcessor
import hirify.analytics.ui.MainViewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val uiModule = module {
    single {
        MainViewModel(
                apiClient = get(),
                voiceInputProcessorFactory = { scope ->
                    get<VoiceInputProcessor> { parametersOf(scope) }
                }
        )
    }
}
