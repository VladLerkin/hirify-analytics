package hirify.analytics.core.di

import hirify.analytics.core.platform.VoiceRecorder
import hirify.analytics.core.ai.WasmModelDirectoryProvider
import hirify.analytics.core.ai.ModelDirectoryProvider
import org.koin.dsl.module

val platformModule = module {
    single { VoiceRecorder(null) }
    single<ModelDirectoryProvider> { WasmModelDirectoryProvider() }
}
