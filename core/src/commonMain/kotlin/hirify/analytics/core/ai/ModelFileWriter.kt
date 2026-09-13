package hirify.analytics.core.ai

expect class ModelFileWriter() {
    fun writeChunk(absolutePath: String, bytes: ByteArray, append: Boolean)
    fun exists(absolutePath: String): Boolean
    fun delete(absolutePath: String): Boolean
    fun length(absolutePath: String): Long
    fun rename(from: String, to: String): Boolean
}
