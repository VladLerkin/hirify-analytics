package hirify.analytics.core.ai

import java.security.MessageDigest
import java.util.prefs.Preferences
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import kotlin.io.encoding.Base64

/**
 * Desktop implementation using Java Preferences API with AES encryption for API keys.
 */
@Suppress("DEPRECATION")
actual class AiSettingsStorage {
    private val prefs: Preferences = Preferences.userNodeForPackage(AiSettingsStorage::class.java)

    actual fun saveConfig(config: AiConfig) {
        prefs.put(KEY_PROVIDER, config.provider)
        prefs.put(KEY_MODEL, config.model)
        prefs.put(KEY_BASE_URL, config.baseUrl)
        prefs.putDouble(KEY_TEMPERATURE, config.temperature)
        prefs.putInt(KEY_MAX_TOKENS, config.maxTokens)
        prefs.put(KEY_LANGUAGE, config.language)
        prefs.put(KEY_TRANSCRIPTION_PROVIDER, config.transcriptionProvider)

        // Secrets are encrypted before saving
        putSecret(KEY_API_KEY, config.apiKey) // deprecated, kept for backward compatibility
        putSecret(KEY_GOOGLE_API_KEY, config.googleApiKey) // deprecated
        putSecret(KEY_OPENAI_API_KEY, config.openaiApiKey)
        putSecret(KEY_GOOGLE_AI_API_KEY, config.googleAiApiKey)
        putSecret(KEY_HIRIFY_API_KEY, config.hirifyApiKey)

        prefs.flush()
    }

    actual fun loadConfig(): AiConfig = AiConfig(
        provider = prefs.get(KEY_PROVIDER, AiPresets.OPENAI_GPT6_LUNA.provider),
        apiKey = getSecret(KEY_API_KEY),
        model = prefs.get(KEY_MODEL, AiPresets.OPENAI_GPT6_LUNA.model),
        baseUrl = prefs.get(KEY_BASE_URL, ""),
        temperature = prefs.getDouble(KEY_TEMPERATURE, 0.7),
        maxTokens = prefs.getInt(KEY_MAX_TOKENS, 4000),
        language = prefs.get(KEY_LANGUAGE, ""),
        transcriptionProvider = prefs.get(KEY_TRANSCRIPTION_PROVIDER, "OPENAI_WHISPER"),
        googleApiKey = getSecret(KEY_GOOGLE_API_KEY),
        openaiApiKey = getSecret(KEY_OPENAI_API_KEY),
        googleAiApiKey = getSecret(KEY_GOOGLE_AI_API_KEY),
        hirifyApiKey = getSecret(KEY_HIRIFY_API_KEY)
    ).withMigratedModel()

    actual fun clearConfig() {
        ALL_KEYS.forEach(prefs::remove)
        prefs.flush()
    }

    /** Saves [value] encrypted, or an empty string if it is blank. */
    private fun putSecret(key: String, value: String) {
        prefs.put(key, if (value.isNotBlank()) encryptApiKey(value) else "")
    }

    /** Reads and decrypts a secret; falls back to the raw value if it is in an old/unencrypted format. */
    private fun getSecret(key: String): String {
        val stored = prefs.get(key, "")
        if (stored.isBlank()) return ""
        return runCatching { decryptApiKey(stored) }.getOrDefault(stored)
    }

    /**
     * Encrypts API key using AES.
     */
    private fun encryptApiKey(plainText: String): String {
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey())
        return Base64.encode(cipher.doFinal(plainText.toByteArray(Charsets.UTF_8)))
    }

    /**
     * Decrypts API key.
     */
    private fun decryptApiKey(encryptedText: String): String {
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey())
        return cipher.doFinal(Base64.decode(encryptedText)).toString(Charsets.UTF_8)
    }

    /**
     * Generates a secret key based on unique system parameters.
     * Uses a combination of username, home directory, and a constant to create a stable key.
     */
    private fun getSecretKey(): SecretKeySpec {
        // Create key based on system parameters (stable for this system)
        val keySource = buildString {
            append(System.getProperty("user.name") ?: "default")
            append(System.getProperty("user.home") ?: "default")
            append(SALT) // Additional salt for security
        }

        // Hash with SHA-256 to get a 256-bit key
        val keyBytes = MessageDigest.getInstance("SHA-256").digest(keySource.toByteArray(Charsets.UTF_8))
        return SecretKeySpec(keyBytes, "AES")
    }

    companion object {
        private const val KEY_PROVIDER = "ai_provider"
        private const val KEY_API_KEY = "ai_api_key"
        private const val KEY_MODEL = "ai_model"
        private const val KEY_BASE_URL = "ai_base_url"
        private const val KEY_TEMPERATURE = "ai_temperature"
        private const val KEY_MAX_TOKENS = "ai_max_tokens"
        private const val KEY_LANGUAGE = "ai_language"
        private const val KEY_TRANSCRIPTION_PROVIDER = "ai_transcription_provider"
        private const val KEY_GOOGLE_API_KEY = "ai_google_api_key"

        // Keys for separate provider group API keys
        private const val KEY_OPENAI_API_KEY = "ai_openai_api_key"
        private const val KEY_GOOGLE_AI_API_KEY = "ai_google_ai_api_key"
        private const val KEY_HIRIFY_API_KEY = "ai_hirify_api_key"

        private val ALL_KEYS = listOf(
            KEY_PROVIDER, KEY_API_KEY, KEY_MODEL, KEY_BASE_URL, KEY_TEMPERATURE, KEY_MAX_TOKENS,
            KEY_LANGUAGE, KEY_TRANSCRIPTION_PROVIDER, KEY_GOOGLE_API_KEY, KEY_OPENAI_API_KEY,
            KEY_GOOGLE_AI_API_KEY, KEY_HIRIFY_API_KEY
        )

        private const val ALGORITHM = "AES"
        private const val SALT = "FamilyTreeApp-AI-Key-Salt-v1"
    }
}
