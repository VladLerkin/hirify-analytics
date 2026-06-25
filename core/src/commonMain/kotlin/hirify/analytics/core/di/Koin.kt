package hirify.analytics.core.di

import hirify.analytics.core.ai.*
import hirify.analytics.core.ai.AiSettingsStorage
import hirify.analytics.core.ai.VoiceInputProcessor
import hirify.analytics.core.ai.agent.AgentService
import hirify.analytics.core.analytics.HirifyApiClient
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.json.Json
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

val coreModule = module {
    single { AiSettingsStorage() }
    single { HirifyApiClient(get(), get()) }

    // AI Clients
    single { OpenAiClient() }
    single { GoogleClient() }
    single { YandexClient() }
    single { OllamaClient() }
    single { CustomClient() }
    single { LocalAiClient(get()) }
    single { LocalModelManager(get(), get()) }
    single { AiClientFactory(get(), get(), get(), get(), get(), get()) }

    // Transcription Clients
    single { OpenAiWhisperClient(get(), get()) }
    single { GoogleSpeechClient(get(), get()) }
    single { YandexSpeechClient(get(), get()) }
    single { TranscriptionClientFactory(get(), get(), get()) }

    single { AgentService(get(), get(), get(), get(), get()) }

    single {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            encodeDefaults = true
            coerceInputValues = true
            explicitNulls = false
        }
    }

    single {
        HttpClient {
            install(HttpTimeout) {
                requestTimeoutMillis = 120_000
                connectTimeoutMillis = 30_000
                socketTimeoutMillis = 120_000
            }
            install(ContentNegotiation) { json(get()) }
        }
    }

    factory { (scope: CoroutineScope) ->
        VoiceInputProcessor(
                voiceRecorder = get(),
                settingsStorage = get(),
                transcriptionClientFactory = get(),
                agentService = get(),
                coroutineScope = scope
        )
    }
}

fun initKoin(
        additionalModules: List<Module> = emptyList(),
        appDeclaration: KoinApplication.() -> Unit = {}
) = startKoin {
    appDeclaration()
    modules(coreModule, *additionalModules.toTypedArray())
}
