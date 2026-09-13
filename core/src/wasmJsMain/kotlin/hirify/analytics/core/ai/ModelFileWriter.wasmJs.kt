package hirify.analytics.core.ai

actual class ModelFileWriter actual constructor() {
    actual fun writeChunk(absolutePath: String, bytes: ByteArray, append: Boolean) {
        // No-op for Wasm
    }
    actual fun exists(absolutePath: String): Boolean {
        // No-op for Wasm
        return false
    }

    actual fun delete(absolutePath: String): Boolean {
        // No-op for Wasm
        return true
    }
    actual fun length(absolutePath: String): Long {
        return 0L
    }
    actual fun rename(from: String, to: String): Boolean {
        return false
    }
}
