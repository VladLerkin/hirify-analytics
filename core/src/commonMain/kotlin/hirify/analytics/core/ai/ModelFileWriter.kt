package hirify.analytics.core.ai

expect class ModelFileWriter() {
    fun writeChunk(absolutePath: String, bytes: ByteArray, append: Boolean)
    fun exists(absolutePath: String): Boolean
}
