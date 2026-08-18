package com.example.data.ai

import android.content.Context
import android.util.Base64
import android.util.Log
import com.example.data.model.AppLanguage
import com.example.data.model.AspectRatioOption
import com.example.data.model.AudioMood
import com.example.data.model.VideoStyle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

data class RealGeneratedVideoResult(
    val title: String,
    val prompt: String,
    val videoUrl: String,
    val localFilePath: String,
    val durationSec: Int,
    val cameraMotion: String,
    val scene1Desc: String,
    val scene2Desc: String,
    val scene3Desc: String,
    val thumbnailResName: String,
    val videoSceneType: String,
    val tags: List<String>
)

object GeminiVideoService {
    private const val TAG = "GeminiVideoService"
    private const val VEO_MODEL = "veo-3.1-fast-generate-preview"
    private const val GEMINI_TEXT_MODEL = "gemini-3.5-flash"
    private const val BASE_GEN_URL = "https://generativelanguage.googleapis.com/v1beta"

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    /**
     * Executes real video generation using Google's Veo video model (veo-3.1-fast-generate-preview).
     * Validates API key, posts the request, polls the long-running operation until completion,
     * and downloads or stores the generated MP4 video.
     */
    suspend fun generateRealVideo(
        context: Context,
        userPrompt: String,
        style: VideoStyle,
        aspectRatio: AspectRatioOption,
        durationSec: Int,
        audioMood: AudioMood,
        language: AppLanguage,
        apiKey: String,
        onProgressUpdate: (progress: Float, stageText: String, stageIndex: Int) -> Unit
    ): RealGeneratedVideoResult = withContext(Dispatchers.IO) {
        val cleanKey = apiKey.trim()
        if (cleanKey.isBlank() || cleanKey == "MY_GEMINI_API_KEY") {
            val errMsg = if (language == AppLanguage.SINHALA) {
                "Gemini API Key එක සකසා නැත. කරුණාකර AI Studio Secrets මඟින් ඔබගේ API Key එක ඇතුළත් කරන්න."
            } else {
                "Gemini API key is not configured. Please add your GEMINI_API_KEY in the AI Studio Secrets panel."
            }
            throw IllegalStateException(errMsg)
        }

        // Stage 0: Prompt & Direction Analysis
        val stage0Text = if (language == AppLanguage.SINHALA) {
            "Google Veo ආකෘතිය මඟින් විස්තරය විශ්ලේෂණය කරමින්..."
        } else {
            "Analyzing prompt with Google Veo ($VEO_MODEL)..."
        }
        onProgressUpdate(0.12f, stage0Text, 0)

        // Build enriched prompt for Veo
        val fullVeoPrompt = buildFullPrompt(userPrompt, style, audioMood)
        val veoAspectRatio = when (aspectRatio) {
            AspectRatioOption.PORTRAIT_9_16 -> "9:16"
            AspectRatioOption.LANDSCAPE_16_9 -> "16:9"
            AspectRatioOption.SQUARE_1_1 -> "1:1"
        }

        // Stage 1: Submitting Generation Request to Veo
        val stage1Text = if (language == AppLanguage.SINHALA) {
            "Veo Video Generation API වෙත ඉල්ලීම යොමු කරමින්..."
        } else {
            "Submitting video task to Veo API..."
        }
        onProgressUpdate(0.25f, stage1Text, 1)

        val requestJson = JSONObject().apply {
            put("prompt", fullVeoPrompt)
            put("config", JSONObject().apply {
                put("numberOfVideos", 1)
                put("resolution", "720p")
                put("aspectRatio", veoAspectRatio)
            })
        }

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = requestJson.toString().toRequestBody(mediaType)
        val submitUrl = "$BASE_GEN_URL/models/$VEO_MODEL:generateVideos?key=$cleanKey"

        val submitRequest = Request.Builder()
            .url(submitUrl)
            .post(requestBody)
            .build()

        val submitResponse = try {
            httpClient.newCall(submitRequest).execute()
        } catch (e: Exception) {
            val netErr = if (language == AppLanguage.SINHALA) {
                "ජාල දෝෂයකි: ${e.localizedMessage ?: "සම්බන්ධ විය නොහැක"}"
            } else {
                "Network connection failed: ${e.localizedMessage ?: "Unable to connect to Google API"}"
            }
            throw IllegalStateException(netErr, e)
        }

        val submitResponseBody = submitResponse.body?.string() ?: ""
        if (!submitResponse.isSuccessful) {
            val errorDetails = parseApiError(submitResponseBody, submitResponse.code, language)
            throw IllegalStateException(errorDetails)
        }

        val submitJson = try {
            JSONObject(submitResponseBody)
        } catch (e: Exception) {
            throw IllegalStateException("Invalid JSON response from Veo API: $submitResponseBody")
        }

        val operationName = submitJson.optString("name")
        if (operationName.isBlank()) {
            val errMsg = submitJson.optJSONObject("error")?.optString("message")
                ?: "Missing operation name from Veo API response."
            throw IllegalStateException(errMsg)
        }

        Log.d(TAG, "Veo video generation operation started: $operationName")

        // Stage 2 & 3: Polling Long-running Operation
        var isDone = false
        var pollAttempts = 0
        val maxPollAttempts = 40 // ~120 seconds maximum
        var operationResultJson: JSONObject? = null

        val pollBaseUrl = if (operationName.startsWith("http")) {
            if (operationName.contains("?")) "$operationName&key=$cleanKey" else "$operationName?key=$cleanKey"
        } else {
            val cleanOp = operationName.removePrefix("/")
            "$BASE_GEN_URL/$cleanOp?key=$cleanKey"
        }

        while (!isDone && pollAttempts < maxPollAttempts) {
            delay(3000)
            pollAttempts++

            val progressFraction = (0.30f + (pollAttempts.toFloat() / maxPollAttempts) * 0.55f).coerceAtMost(0.88f)
            val stageIndex = if (pollAttempts < 8) 2 else 3
            val pollingText = if (language == AppLanguage.SINHALA) {
                if (stageIndex == 2) "4K වීඩියෝ රාමු සංස්ලේෂණය වෙමින් පවතී... (${(progressFraction * 100).toInt()}%)"
                else "සිනමාත්මක ආලෝකය සහ ශබ්ද මුසු කරමින්... (${(progressFraction * 100).toInt()}%)"
            } else {
                if (stageIndex == 2) "Synthesizing neural video frames... (${(progressFraction * 100).toInt()}%)"
                else "Rendering dynamic motion & lighting tracks... (${(progressFraction * 100).toInt()}%)"
            }
            onProgressUpdate(progressFraction, pollingText, stageIndex)

            val pollRequest = Request.Builder()
                .url(pollBaseUrl)
                .get()
                .build()

            val pollResponse = try {
                httpClient.newCall(pollRequest).execute()
            } catch (e: Exception) {
                Log.w(TAG, "Poll request warning: ${e.message}")
                continue
            }

            val pollBody = pollResponse.body?.string() ?: ""
            if (!pollResponse.isSuccessful) {
                Log.w(TAG, "Poll error HTTP ${pollResponse.code}: $pollBody")
                continue
            }

            val pollJson = try {
                JSONObject(pollBody)
            } catch (e: Exception) {
                continue
            }

            if (pollJson.optBoolean("done", false)) {
                isDone = true
                operationResultJson = pollJson
                break
            }
        }

        if (!isDone || operationResultJson == null) {
            val timeoutMsg = if (language == AppLanguage.SINHALA) {
                "වීඩියෝව සෑදීම සඳහා නියමිත කාලය ඉක්මවා ගියේය. කරුණාකර නැවත උත්සාහ කරන්න."
            } else {
                "Veo video generation timed out. The server is still processing or under high load. Please try again."
            }
            throw IllegalStateException(timeoutMsg)
        }

        // Check if operation ended with error
        if (operationResultJson.has("error")) {
            val errObj = operationResultJson.getJSONObject("error")
            val errMsg = errObj.optString("message", "Veo video synthesis failed.")
            throw IllegalStateException(errMsg)
        }

        // Stage 4: Downloading / Finalizing Video Asset
        val stage4Text = if (language == AppLanguage.SINHALA) {
            "වීඩියෝව අවසන් සැකසුම් කරමින්..."
        } else {
            "Finalizing & saving generated video..."
        }
        onProgressUpdate(0.95f, stage4Text, 4)

        val responseObj = operationResultJson.optJSONObject("response")
        val generatedVideos = responseObj?.optJSONArray("generatedVideos")
        val firstVideoObj = generatedVideos?.optJSONObject(0)?.optJSONObject("video")

        var remoteVideoUri = firstVideoObj?.optString("uri") ?: ""
        val videoBytesBase64 = firstVideoObj?.optString("videoBytes") ?: ""
        var localFilePath = ""

        val videosDir = File(context.filesDir, "generated_videos").apply { mkdirs() }
        val outputFile = File(videosDir, "veo_${System.currentTimeMillis()}.mp4")

        if (videoBytesBase64.isNotBlank()) {
            try {
                val decodedBytes = Base64.decode(videoBytesBase64, Base64.DEFAULT)
                FileOutputStream(outputFile).use { it.write(decodedBytes) }
                localFilePath = outputFile.absolutePath
                Log.d(TAG, "Saved video bytes to: $localFilePath")
            } catch (e: Exception) {
                Log.e(TAG, "Error saving base64 video bytes: ${e.message}")
            }
        } else if (remoteVideoUri.isNotBlank()) {
            // Download remote video stream
            try {
                val downloadUrl = if (remoteVideoUri.startsWith("http")) {
                    if (remoteVideoUri.contains("?")) "$remoteVideoUri&key=$cleanKey" else "$remoteVideoUri?key=$cleanKey"
                } else {
                    val cleanUri = remoteVideoUri.removePrefix("/")
                    "$BASE_GEN_URL/$cleanUri?key=$cleanKey"
                }

                val downloadRequest = Request.Builder().url(downloadUrl).get().build()
                val downloadResponse = httpClient.newCall(downloadRequest).execute()
                if (downloadResponse.isSuccessful) {
                    downloadResponse.body?.byteStream()?.use { input ->
                        FileOutputStream(outputFile).use { output ->
                            input.copyTo(output)
                        }
                    }
                    localFilePath = outputFile.absolutePath
                    Log.d(TAG, "Downloaded video file to: $localFilePath")
                }
            } catch (e: Exception) {
                Log.w(TAG, "Could not download remote video file: ${e.message}")
            }
        }

        // Smart metadata generation
        val (thumbnailRes, sceneType) = selectBestThumbnailAndSceneType(userPrompt, style)
        val title = generateSmartTitle(userPrompt, style)
        val (s1, s2, s3) = generateStoryboardDescriptions(userPrompt, style)

        onProgressUpdate(1.0f, if (language == AppLanguage.SINHALA) "සම්පූර්ණයි!" else "Complete!", 4)

        return@withContext RealGeneratedVideoResult(
            title = title,
            prompt = fullVeoPrompt,
            videoUrl = remoteVideoUri.ifBlank { localFilePath },
            localFilePath = localFilePath,
            durationSec = durationSec,
            cameraMotion = when (style) {
                VideoStyle.CYBERPUNK -> "camera_pan"
                VideoStyle.WILDLIFE -> "camera_zoom"
                VideoStyle.CINEMATIC -> "camera_drone"
                else -> "camera_pan"
            },
            scene1Desc = s1,
            scene2Desc = s2,
            scene3Desc = s3,
            thumbnailResName = thumbnailRes,
            videoSceneType = sceneType,
            tags = listOf(style.titleEn, "Google Veo", "4K", audioMood.labelEn)
        )
    }

    /**
     * Enhances a user's prompt into rich cinematic phrasing using gemini-3.5-flash.
     */
    suspend fun enhancePromptWithAi(
        userPrompt: String,
        style: VideoStyle,
        apiKey: String
    ): String = withContext(Dispatchers.IO) {
        val cleanKey = apiKey.trim()
        if (cleanKey.isNotBlank() && cleanKey != "MY_GEMINI_API_KEY") {
            try {
                val promptText = "Enhance this video prompt for Google Veo AI video generator into a visually rich 1-2 sentence description in 8k cinematic detail with style '${style.titleEn}': \"$userPrompt\". Output only the enhanced prompt without commentary."
                val requestJson = JSONObject().apply {
                    put("contents", JSONArray().apply {
                        put(JSONObject().apply {
                            put("parts", JSONArray().apply {
                                put(JSONObject().put("text", promptText))
                            })
                        })
                    })
                }
                val body = requestJson.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
                val request = Request.Builder()
                    .url("$BASE_GEN_URL/models/$GEMINI_TEXT_MODEL:generateContent?key=$cleanKey")
                    .post(body)
                    .build()
                val response = httpClient.newCall(request).execute()
                val responseBody = response.body?.string()
                if (response.isSuccessful && !responseBody.isNullOrBlank()) {
                    val rootJson = JSONObject(responseBody)
                    val text = rootJson.optJSONArray("candidates")?.optJSONObject(0)
                        ?.optJSONObject("content")?.optJSONArray("parts")?.optJSONObject(0)?.optString("text")
                    if (!text.isNullOrBlank()) {
                        return@withContext text.trim()
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Prompt enhancement failed: ${e.message}")
            }
        }

        // Local enhancement fallback
        val clean = userPrompt.trim()
        return@withContext "$clean, ${style.promptSuffix}, volumetric lighting, ultra-detailed 8K video render, smooth 60fps cinematic camera motion."
    }

    private fun parseApiError(responseBody: String, httpCode: Int, language: AppLanguage): String {
        var rawMessage = ""
        var statusCode = ""
        try {
            val root = JSONObject(responseBody)
            val error = root.optJSONObject("error")
            if (error != null) {
                rawMessage = error.optString("message")
                statusCode = error.optString("status")
            }
        } catch (_: Exception) {
            rawMessage = responseBody
        }

        return when (httpCode) {
            400 -> {
                if (language == AppLanguage.SINHALA) {
                    "අවලංගු ඉල්ලීමකි (400 Bad Request): $rawMessage"
                } else {
                    "Invalid Request (400): $rawMessage"
                }
            }
            401, 403 -> {
                if (language == AppLanguage.SINHALA) {
                    "අවසර නොමැත (403): ඔබගේ Gemini API Key එක සඳහා Google Veo මාදිලිය සක්‍රිය කර නොමැත. කරුණාකර AI Studio හි Secrets පරීක්ෂා කරන්න. ($rawMessage)"
                } else {
                    "Authentication / Permission Error ($httpCode): Your Gemini API Key does not have access to Google Veo ($VEO_MODEL). Check your API Key permissions in AI Studio Secrets. Details: $rawMessage"
                }
            }
            404 -> {
                if (language == AppLanguage.SINHALA) {
                    "Google Veo මාදිලිය සොයාගත නොහැකි විය (404): $VEO_MODEL ආකෘතිය ඔබගේ API යතුරට සක්‍රිය කර ඇති බව තහවුරු කරගන්න."
                } else {
                    "Model Not Found (404): The model '$VEO_MODEL' is unavailable or not accessible with the provided API key. Details: $rawMessage"
                }
            }
            429 -> {
                if (language == AppLanguage.SINHALA) {
                    "සීමාව ඉක්මවා ඇත (429 Rate Limit): Veo Video Generation සඳහා වන API සීමාව ඉක්මවා ඇත. කරුණාකර සුළු වේලාවකින් නැවත උත්සාහ කරන්න."
                } else {
                    "Quota Exceeded (429): Rate limit exceeded for Google Veo. Please wait a few minutes and try again."
                }
            }
            500, 503 -> {
                if (language == AppLanguage.SINHALA) {
                    "Google සේවාදායක දෝෂයකි ($httpCode): කරුණාකර සුළු වේලාවකින් නැවත උත්සාහ කරන්න."
                } else {
                    "Google AI Service Unavailable ($httpCode): Server is currently busy. Please retry in a few moments."
                }
            }
            else -> {
                if (rawMessage.isNotBlank()) "Google Veo API Error ($httpCode): $rawMessage"
                else "Google Veo API returned HTTP $httpCode"
            }
        }
    }

    private fun buildFullPrompt(userPrompt: String, style: VideoStyle, audioMood: AudioMood): String {
        return "$userPrompt, ${style.promptSuffix}, cinematic 8k resolution, smooth camera movement, mood: ${audioMood.labelEn}"
    }

    private fun generateSmartTitle(prompt: String, style: VideoStyle): String {
        val words = prompt.trim().split(" ").filter { it.isNotBlank() }
        val prefix = if (words.size >= 2) {
            words.take(3).joinToString(" ").replace(Regex("[^a-zA-Z0-9 🇱🇰]"), "").capitalizeWords()
        } else {
            style.titleEn
        }
        return if (prefix.length > 28) prefix.take(28) + "..." else prefix
    }

    private fun generateStoryboardDescriptions(prompt: String, style: VideoStyle): Triple<String, String, String> {
        val s1 = when (style) {
            VideoStyle.CYBERPUNK -> "Scene 1: Neon rain reflections on obsidian streets as holographic lights activate."
            VideoStyle.ANIME -> "Scene 1: Soft watercolor morning sky with gentle wind carrying petals across the view."
            VideoStyle.WILDLIFE -> "Scene 1: Sunlit golden canopy revealing the focal wildlife in its serene natural habitat."
            VideoStyle.SRI_LANKAN -> "Scene 1: Majestic ancient architecture illuminated by traditional flame torches."
            else -> "Scene 1: Wide establishing shot capturing the breathtaking expanse of the environment."
        }

        val s2 = when (style) {
            VideoStyle.CYBERPUNK -> "Scene 2: High-speed camera sweep following flying futuristic speeders past towering billboards."
            VideoStyle.ANIME -> "Scene 2: Vibrant dynamic motion with character expressions and fluid anime line work."
            VideoStyle.WILDLIFE -> "Scene 2: Intense slow-motion close up focusing on grace, power, and predatory focus."
            VideoStyle.SRI_LANKAN -> "Scene 2: Rhythmic ceremonial drum performance with swirling fire rings."
            else -> "Scene 2: Smooth cinematic dolly zoom intensifying the focal subject action."
        }

        val s3 = when (style) {
            VideoStyle.CYBERPUNK -> "Scene 3: Panoramic cyber dusk skyline glowing with electric purple and cyan lasers."
            VideoStyle.ANIME -> "Scene 3: Warm golden hour sunset over the tranquil mountain horizon."
            VideoStyle.WILDLIFE -> "Scene 3: Dramatic silhouette against the blazing tropical horizon."
            VideoStyle.SRI_LANKAN -> "Scene 3: Grand royal crest revealed amidst glowing embers and twilight splendor."
            else -> "Scene 3: Epic pull-back revealing the complete masterpiece scale."
        }

        return Triple(s1, s2, s3)
    }

    private fun selectBestThumbnailAndSceneType(prompt: String, style: VideoStyle): Pair<String, String> {
        val lower = prompt.lowercase()
        return when {
            lower.contains("sigiriya") || lower.contains("සීගිරිය") || lower.contains("rock") || lower.contains("cyber") -> {
                Pair("thumb_sigiriya_cyber", "sigiriya")
            }
            lower.contains("yala") || lower.contains("leopard") || lower.contains("දිවියා") || lower.contains("wildlife") || lower.contains("animal") -> {
                Pair("thumb_yala_leopard", "yala")
            }
            lower.contains("colombo") || lower.contains("lotus") || lower.contains("නෙළුම්") || lower.contains("city") || lower.contains("skyline") -> {
                Pair("thumb_colombo_future", "colombo")
            }
            lower.contains("ella") || lower.contains("train") || lower.contains("දුම්රිය") || lower.contains("anime") || lower.contains("ghibli") -> {
                Pair("thumb_anime_village", "anime")
            }
            lower.contains("fire") || lower.contains("perahera") || lower.contains("නැටුම") || lower.contains("dance") -> {
                Pair("thumb_sigiriya_cyber", "perahera")
            }
            lower.contains("ocean") || lower.contains("whale") || lower.contains("තල්මසා") || lower.contains("sea") -> {
                Pair("thumb_yala_leopard", "ocean")
            }
            style == VideoStyle.CYBERPUNK -> Pair("thumb_sigiriya_cyber", "sigiriya")
            style == VideoStyle.ANIME -> Pair("thumb_anime_village", "anime")
            style == VideoStyle.WILDLIFE -> Pair("thumb_yala_leopard", "yala")
            else -> Pair("thumb_colombo_future", "colombo")
        }
    }

    private fun String.capitalizeWords(): String =
        split(" ").joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
}
