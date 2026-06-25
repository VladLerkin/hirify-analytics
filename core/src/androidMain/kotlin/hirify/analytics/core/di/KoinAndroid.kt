package hirify.analytics.core.di

import hirify.analytics.core.platform.VoiceRecorder
import hirify.analytics.core.ai.AndroidModelDirectoryProvider
import hirify.analytics.core.ai.ModelDirectoryProvider
import org.koin.dsl.module
import org.koin.android.ext.koin.androidContext

val platformModule = module {
    single { VoiceRecorder(androidContext()) }
    single<ModelDirectoryProvider> { AndroidModelDirectoryProvider(androidContext()) }
}
