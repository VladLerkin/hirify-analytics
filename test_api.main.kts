import java.util.prefs.Preferences
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import java.security.MessageDigest
import java.util.Base64
import java.net.URL
import java.net.HttpURLConnection

val prefs = Preferences.userNodeForPackage(Class.forName("hirify.analytics.core.ai.AiSettingsStorage"))
val encryptedKey = prefs.get("ai_hirify_api_key", "")

val keySource = System.getProperty("user.name") + System.getProperty("user.home") + "FamilyTreeApp-AI-Key-Salt-v1"
val digest = MessageDigest.getInstance("SHA-256")
val keyBytes = digest.digest(keySource.toByteArray(Charsets.UTF_8))
val secretKey = SecretKeySpec(keyBytes, "AES")

val cipher = Cipher.getInstance("AES")
cipher.init(Cipher.DECRYPT_MODE, secretKey)
val decodedBytes = Base64.getDecoder().decode(encryptedKey)
val decryptedKey = String(cipher.doFinal(decodedBytes), Charsets.UTF_8)

fun testParam(param: String) {
    val url = URL("https://api.hirify.me/api/partner/dictionary/skills?$param=aws&limit=5")
    val conn = url.openConnection() as HttpURLConnection
    conn.setRequestProperty("X-API-Key", decryptedKey)
    println("$param response code: ${conn.responseCode}")
    if (conn.responseCode == 200) {
        println(conn.inputStream.bufferedReader().readText())
    }
}

testParam("query")
testParam("search")
testParam("q")
testParam("name")
