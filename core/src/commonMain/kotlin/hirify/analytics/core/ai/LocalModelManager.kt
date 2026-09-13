package hirify.analytics.core.ai

import io.ktor.client.HttpClient
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import io.ktor.utils.io.readAvailable
import io.ktor.client.plugins.timeout
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.header
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LocalModelManager(
    private val httpClient: HttpClient,
    private val directoryProvider: ModelDirectoryProvider
) {
    /**
     * Downloads a model file from the specified URL to the local directory.
     * Emits the download progress as a Float between 0.0 and 1.0.
     * Finally emits the absolute path to the downloaded file.
     */
    suspend fun downloadModel(url: String, fileName: String): Flow<DownloadStatus> = flow {
        val dirPath = directoryProvider.getDirectory()
        val absolutePath = "$dirPath/$fileName"
        
        try {
            val fileWriter = ModelFileWriter()
            
            val tmpPath = "$absolutePath.tmp"
            
            // If already downloaded completely
            if (fileWriter.exists(absolutePath) && fileWriter.length(absolutePath) > 0) {
                emit(DownloadStatus.Progress(1.0f))
                emit(DownloadStatus.Finished(absolutePath))
                return@flow
            }
            
            var downloadedBytes = 0L
            if (fileWriter.exists(tmpPath)) {
                downloadedBytes = fileWriter.length(tmpPath)
            }
            
            httpClient.prepareGet(url) {
                if (downloadedBytes > 0) {
                    header(HttpHeaders.Range, "bytes=$downloadedBytes-")
                }
                timeout {
                    requestTimeoutMillis = Long.MAX_VALUE
                    socketTimeoutMillis = Long.MAX_VALUE
                }
            }.execute { response ->
                if (!response.status.isSuccess()) {
                    throw RuntimeException("HTTP Error ${response.status.value}: ${response.status.description}")
                }
                
                val channel = response.bodyAsChannel()
                var totalBytes = response.headers[io.ktor.http.HttpHeaders.ContentLength]?.toLong() ?: 0L
                
                if (downloadedBytes > 0 && response.status != io.ktor.http.HttpStatusCode.PartialContent) {
                    downloadedBytes = 0L
                    fileWriter.writeChunk(tmpPath, ByteArray(0), append = false)
                } else if (downloadedBytes > 0) {
                    if (totalBytes >= 0) totalBytes += downloadedBytes
                } else {
                    fileWriter.writeChunk(tmpPath, ByteArray(0), append = false)
                }
                
                var bytesCopied = downloadedBytes
                
                val buffer = ByteArray(8192)
                while (!channel.isClosedForRead) {
                    val read = channel.readAvailable(buffer, 0, buffer.size)
                    if (read > 0) {
                        val bytes = if (read == buffer.size) buffer else buffer.copyOf(read)
                        fileWriter.writeChunk(tmpPath, bytes, append = true)
                        bytesCopied += read
                        
                        if (totalBytes > 0L) {
                            emit(DownloadStatus.Progress(bytesCopied.toFloat() / totalBytes))
                        }
                    } else if (read < 0) {
                        break
                    }
                }
                
                if (totalBytes > 0 && bytesCopied != totalBytes) {
                    throw Exception("Download incomplete: expected $totalBytes bytes but got $bytesCopied bytes")
                }
                
                fileWriter.rename(tmpPath, absolutePath)
                emit(DownloadStatus.Finished(absolutePath))
            }
        } catch (e: Exception) {
            println("[DEBUG_LOG] LocalModelManager error: ${e.message}")
            e.printStackTrace()
            emit(DownloadStatus.Error(e))
        }
    }

    fun isModelDownloaded(fileName: String): Boolean {
        val dirPath = directoryProvider.getDirectory()
        val absolutePath = "$dirPath/$fileName"
        return ModelFileWriter().exists(absolutePath)
    }

    fun deleteModel(fileName: String) {
        val dirPath = directoryProvider.getDirectory()
        val absolutePath = "$dirPath/$fileName"
        ModelFileWriter().delete(absolutePath)
    }
}

sealed class DownloadStatus {
    data class Progress(val progress: Float) : DownloadStatus()
    data class Finished(val absolutePath: String) : DownloadStatus()
    data class Error(val exception: Exception) : DownloadStatus()
}
