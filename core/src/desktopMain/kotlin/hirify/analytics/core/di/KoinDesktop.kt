package hirify.analytics.core.di

import hirify.analytics.core.ai.DesktopModelDirectoryProvider
import hirify.analytics.core.ai.ModelDirectoryProvider
import hirify.analytics.core.platform.VoiceRecorder
import org.koin.dsl.module

val platformModule = module {
    single { VoiceRecorder(null) }
    single<ModelDirectoryProvider> { DesktopModelDirectoryProvider() }
}
