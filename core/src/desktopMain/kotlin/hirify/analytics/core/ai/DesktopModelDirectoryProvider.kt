package hirify.analytics.core.ai

import java.io.File

class DesktopModelDirectoryProvider : ModelDirectoryProvider {
    override fun getDirectory(): String {
        val userHome = System.getProperty("user.home")
        val appDir = File(userHome, ".hirify-analytics/models")
        if (!appDir.exists()) {
            appDir.mkdirs()
        }
        return appDir.absolutePath
    }
}
