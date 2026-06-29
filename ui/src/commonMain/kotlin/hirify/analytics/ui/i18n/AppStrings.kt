package hirify.analytics.ui.i18n

import androidx.compose.runtime.staticCompositionLocalOf

interface AppStrings {
    // Common
    val ok: String
    val cancel: String
    val confirm: String
    val settings: String
    val about: String
    val openInBrowser: String

    // Main Screen
    val appName: String
    val allVacancies: String
    val stop: String
    val stopRecording: String
    val mic: String
    val voiceDictation: String
    
    // Sidebar
    val configureSources: String
    val reset: String
    val workFormat: String
    val workFormatTooltip: String
    val remote: String
    val hybrid: String
    val onsite: String
    val remoteType: String
    val remoteTypeTooltip: String
    val global: String
    val russia: String
    val europe: String
    val usa: String
    val specializations: String
    val specializationsTooltip: String
    val skills: String
    val skillsTooltip: String
    val and: String
    val or: String
    val grade: String
    val gradeTooltip: String
    val trainee: String
    val junior: String
    val middle: String
    val senior: String
    val lead: String
    val headGrade: String
    val director: String
    val cLevel: String
    val companyType: String
    val companyTypeTooltip: String
    val startup: String
    val corporation: String
    val productCompany: String
    val outsourcingCompany: String
    val searchPlaceholder: String
    val delete: String
    
    // About
    val freeSoftware: String
    val builtWith: String
    val sourceCodeAvailable: String
    val sourceCodeAvailableOn: String
    val author: String
    val feedbackEmail: String
    
    // Settings Screen
    val aiSettingsTitle: String
    val save: String
    val back: String
    val hirifyKeyWarning: String
    val hirifyKeyLabel: String
    val hirifyKeySupportingText: String
    val modelSelectionWarning: String
    val presetsLabel: String
    val selectPreset: String
    val apiKeyNotRequired: String
    val baseUrlLabel: String
    val modelLabel: String
    val transcriptionLanguageLabel: String
    val transcriptionLanguageSupportingText: String
    val speechRecognitionProviderLabel: String
    val selectProvider: String
    val voskRequiresModel: String
    val downloadModel: String
    val modelDownloaded: String
    val error: String
    val advancedSettings: String
    val temperatureLabel: String
    val maxTokensLabel: String
    val testAiConnection: String
    val testingConnection: String
    val connectionSuccessful: String
    val connectionFailed: String
    val interfaceLanguage: String
    
    // Providers
    val openaiApiKeyLabel: String
    val openaiWhisperApiKeyLabel: String
    val openaiWhisperApiKeySupportingText: String
    val openaiWhisperProvider: String
    val voskLocalProvider: String
    val googleApiKeyLabel: String
    val googleSpeechApiKeyLabel: String
    val googleSpeechApiKeySupportingText: String
    val googleSpeechProvider: String
    val yandexApiKeyLabel: String
    val yandexSpeechKitApiKeyLabel: String
    val yandexSpeechKitApiKeySupportingText: String
    val yandexSpeechKitProvider: String
    val yandexFolderIdLabel: String
    val yandexFolderIdSupportingText: String
    val keyStoredInMemory: String

    // About Screen
    val aboutTitle: String
    val version: String
    val privacyPolicy: String
    val privacyPolicyDesc: String
    val openSource: String
    val openSourceDesc: String
    val contactUs: String
}

object EnStrings : AppStrings {
    override val ok = "OK"
    override val cancel = "Cancel"
    override val confirm = "Confirm"
    override val settings = "Settings"
    override val about = "About"
    override val openInBrowser = "Open in browser"
    override val appName = "hirify analytics"
    override val allVacancies = "All vacancies"
    override val stop = "Stop"
    override val stopRecording = "Stop Recording"
    override val mic = "Mic"
    override val voiceDictation = "Voice Dictation"
    
    // Sidebar
    override val configureSources = "Configure sources"
    override val reset = "Reset"
    override val workFormat = "Work format"
    override val workFormatTooltip = "Select format: remote work, hybrid format (a few days in the office) or fully onsite"
    override val remote = "Remote"
    override val hybrid = "Hybrid"
    override val onsite = "Onsite"
    override val remoteType = "Remote type"
    override val remoteTypeTooltip = "Specify where you can work from: Global (Worldwide), from Russia, from Europe, etc. Often employers limit the location even for remote work"
    override val global = "Global"
    override val russia = "Russia"
    override val europe = "Europe"
    override val usa = "USA"
    override val specializations = "Specializations"
    override val specializationsTooltip = "Select one or more professions. For example, Frontend, Backend, or QA"
    override val skills = "Skills"
    override val skillsTooltip = "Specify technologies and tools. The AND/OR switch allows you to search for vacancies that require ALL specified skills (AND), or AT LEAST ONE of them (OR)"
    override val and = "AND"
    override val or = "OR"
    override val grade = "Grade"
    override val gradeTooltip = "Required level of candidate experience: from Trainee to C-level"
    override val trainee = "Trainee"
    override val junior = "Junior"
    override val middle = "Middle"
    override val senior = "Senior"
    override val lead = "Lead"
    override val headGrade = "Head"
    override val director = "Director"
    override val cLevel = "C-level"
    override val companyType = "Company type"
    override val companyTypeTooltip = "Choose where you are more comfortable working: in a startup, corporation, product company, or outsourcing"
    override val startup = "Startup"
    override val corporation = "Corporation"
    override val productCompany = "Product company"
    override val outsourcingCompany = "Outsourcing company"
    override val searchPlaceholder = "Search..."
    override val delete = "Delete"
    
    override val freeSoftware = "This program is free software."
    override val builtWith = "Built with Kotlin Multiplatform and Compose Multiplatform for Android, Desktop, iOS, and Web."
    override val sourceCodeAvailable = "Source code is available on GitHub:\n"
    override val sourceCodeAvailableOn = "The source code is available on "
    override val author = "Author: "
    override val feedbackEmail = "Please send all comments and feedback to the email above."
    
    override val aiSettingsTitle = "AI Settings"
    override val save = "Save"
    override val back = "Back"
    override val hirifyKeyWarning = "To use this app, you need a Hirify analytics key. If you don't have one, please contact Hirify support."
    override val hirifyKeyLabel = "Hirify Analytics API Key"
    override val hirifyKeySupportingText = "API key for Hirify Vacancy Analytics."
    override val modelSelectionWarning = "Select an AI model for voice recognition and automatic filter setup. This allows you to get vacancy analytics simply by speaking your preferences out loud."
    override val presetsLabel = "Presets:"
    override val selectPreset = "Select Preset"
    override val apiKeyNotRequired = "API key is not required for local models\nTo download the selected model run: "
    override val baseUrlLabel = "Base URL"
    override val modelLabel = "Model"
    override val transcriptionLanguageLabel = "Transcription Language (ISO-639-1)"
    override val transcriptionLanguageSupportingText = "Language code for transcription. Leave empty for auto-detection."
    override val speechRecognitionProviderLabel = "Speech Recognition Provider:"
    override val selectProvider = "Select Provider"
    override val voskRequiresModel = "Vosk requires a ~45MB language model to be downloaded for offline use."
    override val downloadModel = "Download Model"
    override val modelDownloaded = "Model is downloaded and ready."
    override val error = "Error"
    override val advancedSettings = "Advanced Settings:"
    override val temperatureLabel = "Temperature"
    override val maxTokensLabel = "Max Tokens"
    override val testAiConnection = "Test AI Connection"
    override val testingConnection = "Testing connection..."
    override val connectionSuccessful = "Connection successful!"
    override val connectionFailed = "Connection failed"
    override val interfaceLanguage = "Interface Language"
    
    override val openaiApiKeyLabel = "OpenAI API Key"
    override val openaiWhisperApiKeyLabel = "OpenAI API Key (Whisper)"
    override val openaiWhisperApiKeySupportingText = "API key for OpenAI Whisper transcription."
    override val openaiWhisperProvider = "OpenAI Whisper (Recommended)"
    override val voskLocalProvider = "Vosk Local (Offline & Free)"
    override val googleApiKeyLabel = "Google AI API Key"
    override val googleSpeechApiKeyLabel = "Google AI API Key (Speech-to-Text)"
    override val googleSpeechApiKeySupportingText = "API key for Google Speech-to-Text."
    override val googleSpeechProvider = "Google Speech-to-Text (best for Georgian)"
    override val yandexApiKeyLabel = "YandexGPT API Key"
    override val yandexSpeechKitApiKeyLabel = "Yandex Cloud API Key (SpeechKit)"
    override val yandexSpeechKitApiKeySupportingText = "API key for Yandex SpeechKit."
    override val yandexSpeechKitProvider = "Yandex SpeechKit (best for Russian and CIS languages)"
    override val yandexFolderIdLabel = "Folder ID (optional)"
    override val yandexFolderIdSupportingText = "Yandex Cloud Folder ID. Leave as 'default' for automatic detection."
    override val keyStoredInMemory = "Provided key is stored in memory and masked in logs."

    override val aboutTitle = "About Hirify Analytics"
    override val version = "Version: 1.0.7"
    override val privacyPolicy = "Privacy Policy"
    override val privacyPolicyDesc = "This application respects your privacy. Analytics data is processed according to our terms of service."
    override val openSource = "Open Source Components"
    override val openSourceDesc = "This software uses open source components. See the repository for details."
    override val contactUs = "Contact Us"
}

object RuStrings : AppStrings {
    override val ok = "ОК"
    override val cancel = "Отмена"
    override val confirm = "Подтвердить"
    override val settings = "Настройки"
    override val about = "О программе"
    override val openInBrowser = "Открыть в браузере"
    override val appName = "hirify analytics"
    override val allVacancies = "Все вакансии"
    override val stop = "Стоп"
    override val stopRecording = "Остановить запись"
    override val mic = "Микрофон"
    override val voiceDictation = "Голосовой ввод"
    
    // Sidebar
    override val configureSources = "Настроить источники"
    override val reset = "Сбросить"
    override val workFormat = "Формат работы"
    override val workFormatTooltip = "Выберите формат: удаленная работа, гибридный формат (несколько дней в офисе) или полностью в офисе"
    override val remote = "Удаленно"
    override val hybrid = "Гибрид"
    override val onsite = "В офисе"
    override val remoteType = "Тип удаленки"
    override val remoteTypeTooltip = "Укажите, откуда вы можете работать: Глобал (Worldwide), из РФ, из Европы и т.д. Часто работодатели ограничивают локацию даже для удаленки"
    override val global = "Глобал"
    override val russia = "РФ"
    override val europe = "Европа"
    override val usa = "США"
    override val specializations = "Специализации"
    override val specializationsTooltip = "Выберите одну или несколько профессий. Например, Frontend, Backend или QA"
    override val skills = "Навыки"
    override val skillsTooltip = "Укажите технологии и инструменты. Переключатель И/ИЛИ позволяет искать вакансии, где требуются ВСЕ указанные навыки (И), либо ХОТЯ БЫ ОДИН из них (ИЛИ)"
    override val and = "И"
    override val or = "ИЛИ"
    override val grade = "Грейд"
    override val gradeTooltip = "Требуемый уровень опыта кандидата: от стажера (Trainee) до руководителя (C-level)"
    override val trainee = "Стажер"
    override val junior = "Джуниор"
    override val middle = "Мидл"
    override val senior = "Сеньор"
    override val lead = "Лид"
    override val headGrade = "Head"
    override val director = "Директор"
    override val cLevel = "C-level"
    override val companyType = "Тип компании"
    override val companyTypeTooltip = "Выберите, где вам комфортнее работать: в стартапе, корпорации, продуктовой компании или в аутсорсе"
    override val startup = "Стартап"
    override val corporation = "Корпорация"
    override val productCompany = "Продуктовая компания"
    override val outsourcingCompany = "Аутсорс компания"
    override val searchPlaceholder = "Поиск..."
    override val delete = "Удалить"
    
    override val freeSoftware = "Эта программа является свободным программным обеспечением."
    override val builtWith = "Создано с использованием Kotlin Multiplatform и Compose Multiplatform для Android, Desktop, iOS и Web."
    override val sourceCodeAvailable = "Исходный код доступен на GitHub:\n"
    override val sourceCodeAvailableOn = "Исходный код доступен на "
    override val author = "Автор: "
    override val feedbackEmail = "Пожалуйста, отправляйте все комментарии и отзывы на указанный выше email."
    
    override val aiSettingsTitle = "Настройки ИИ"
    override val save = "Сохранить"
    override val back = "Назад"
    override val hirifyKeyWarning = "Для работы приложения необходим ключ аналитики Hirify. Если у вас его нет, обратитесь в службу поддержки Hirify."
    override val hirifyKeyLabel = "API Ключ Hirify Analytics"
    override val hirifyKeySupportingText = "API ключ для аналитики вакансий Hirify."
    override val modelSelectionWarning = "Выберите AI-модель для распознавания голоса и автоматической настройки фильтров. Это позволяет получать аналитику по вакансиям, просто произнося свои пожелания вслух."
    override val presetsLabel = "Пресеты:"
    override val selectPreset = "Выберите пресет"
    override val apiKeyNotRequired = "API ключ не требуется для локальных моделей\nДля загрузки выбранной модели выполните: "
    override val baseUrlLabel = "Базовый URL"
    override val modelLabel = "Модель"
    override val transcriptionLanguageLabel = "Язык распознавания (ISO-639-1)"
    override val transcriptionLanguageSupportingText = "Код языка для распознавания речи. Оставьте пустым для автоопределения."
    override val speechRecognitionProviderLabel = "Провайдер распознавания речи:"
    override val selectProvider = "Выберите провайдера"
    override val voskRequiresModel = "Vosk требует загрузки языковой модели (~45 МБ) для работы офлайн."
    override val downloadModel = "Скачать модель"
    override val modelDownloaded = "Модель загружена и готова к работе."
    override val error = "Ошибка"
    override val advancedSettings = "Дополнительные настройки:"
    override val temperatureLabel = "Температура"
    override val maxTokensLabel = "Макс. токенов"
    override val testAiConnection = "Проверить подключение ИИ"
    override val testingConnection = "Проверка подключения..."
    override val connectionSuccessful = "Подключение успешно!"
    override val connectionFailed = "Ошибка подключения"
    override val interfaceLanguage = "Язык интерфейса"
    
    override val openaiApiKeyLabel = "OpenAI API Ключ"
    override val openaiWhisperApiKeyLabel = "OpenAI API Ключ (Whisper)"
    override val openaiWhisperApiKeySupportingText = "Ключ API для OpenAI Whisper."
    override val openaiWhisperProvider = "OpenAI Whisper (Рекомендуется)"
    override val voskLocalProvider = "Vosk Local (Офлайн и бесплатно)"
    override val googleApiKeyLabel = "Google AI API Ключ"
    override val googleSpeechApiKeyLabel = "Google AI API Ключ (Speech-to-Text)"
    override val googleSpeechApiKeySupportingText = "Ключ API для Google Speech-to-Text."
    override val googleSpeechProvider = "Google Speech-to-Text (лучше для грузинского)"
    override val yandexApiKeyLabel = "YandexGPT API Ключ"
    override val yandexSpeechKitApiKeyLabel = "Yandex Cloud API Ключ (SpeechKit)"
    override val yandexSpeechKitApiKeySupportingText = "Ключ API для Yandex SpeechKit."
    override val yandexSpeechKitProvider = "Yandex SpeechKit (лучше для русского и языков СНГ)"
    override val yandexFolderIdLabel = "Folder ID (опционально)"
    override val yandexFolderIdSupportingText = "Yandex Cloud Folder ID. Оставьте 'default' для автоопределения."
    override val keyStoredInMemory = "Ключ сохраняется в памяти и маскируется в логах."

    override val aboutTitle = "О Hirify Analytics"
    override val version = "Версия: 1.0.7"
    override val privacyPolicy = "Политика конфиденциальности"
    override val privacyPolicyDesc = "Это приложение уважает вашу конфиденциальность. Данные аналитики обрабатываются в соответствии с нашими условиями предоставления услуг."
    override val openSource = "Открытое ПО"
    override val openSourceDesc = "В этом ПО используются компоненты с открытым исходным кодом. Подробности см. в репозитории."
    override val contactUs = "Свяжитесь с нами"
}

val LocalAppStrings = staticCompositionLocalOf<AppStrings> { EnStrings }
val LocalLanguageUpdater = staticCompositionLocalOf<(String) -> Unit> { {} }
