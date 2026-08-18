package com.example.data.repository

import android.content.Context
import com.example.BuildConfig
import com.example.data.ai.GeminiVideoService
import com.example.data.local.CommentDao
import com.example.data.local.PrepopulatedData
import com.example.data.local.UserProfileDao
import com.example.data.local.VideoDao
import com.example.data.model.AppLanguage
import com.example.data.model.AspectRatioOption
import com.example.data.model.AudioMood
import com.example.data.model.CommentEntity
import com.example.data.model.UserProfileEntity
import com.example.data.model.VideoEntity
import com.example.data.model.VideoStyle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class VideoRepository(
    private val context: Context,
    private val videoDao: VideoDao,
    private val userProfileDao: UserProfileDao,
    private val commentDao: CommentDao
) {
    val allVideos: Flow<List<VideoEntity>> = videoDao.getAllVideos()
    val userVideos: Flow<List<VideoEntity>> = videoDao.getUserVideos()
    val likedVideos: Flow<List<VideoEntity>> = videoDao.getLikedVideos()
    val savedVideos: Flow<List<VideoEntity>> = videoDao.getSavedVideos()
    val userProfile: Flow<UserProfileEntity?> = userProfileDao.getUserProfile()

    fun getVideoById(id: Long): Flow<VideoEntity?> = videoDao.getVideoById(id)

    fun getCommentsForVideo(videoId: Long): Flow<List<CommentEntity>> =
        commentDao.getCommentsForVideo(videoId)

    suspend fun toggleLike(video: VideoEntity) {
        val newIsLiked = !video.isLiked
        val delta = if (newIsLiked) 1 else -1
        videoDao.toggleLike(video.id, newIsLiked, delta)
    }

    suspend fun toggleSave(video: VideoEntity) {
        videoDao.toggleSave(video.id, !video.isSaved)
    }

    suspend fun incrementViews(videoId: Long) {
        videoDao.incrementViews(videoId)
    }

    suspend fun deleteVideo(videoId: Long) {
        videoDao.deleteVideoById(videoId)
    }

    suspend fun postComment(videoId: Long, text: String, authorName: String = "Kavindu (You)"): Long {
        val comment = CommentEntity(
            videoId = videoId,
            authorName = authorName,
            authorAvatar = "🚀",
            text = text
        )
        return commentDao.insertComment(comment)
    }

    suspend fun addCredits(amount: Int) {
        userProfileDao.addCredits(amount)
    }

    suspend fun setLanguage(language: AppLanguage) {
        userProfileDao.setLanguage(language.code)
    }

    suspend fun resetFeedData() {
        videoDao.deleteAllVideos()
        videoDao.insertVideos(PrepopulatedData.initialVideos)
    }

    suspend fun initDefaultDataIfNeeded() {
        try {
            val existingProfile = userProfileDao.getUserProfile().firstOrNull()
            if (existingProfile == null) {
                userProfileDao.insertOrUpdateProfile(UserProfileEntity())
            }
            val existingVideos = videoDao.getAllVideos().firstOrNull()
            if (existingVideos.isNullOrEmpty()) {
                videoDao.insertVideos(PrepopulatedData.initialVideos)
                commentDao.insertComments(PrepopulatedData.sampleComments)
            }
        } catch (e: Exception) {
            android.util.Log.e("VideoRepository", "Error ensuring default data", e)
        }
    }

    suspend fun generateVideo(
        prompt: String,
        style: VideoStyle,
        aspectRatio: AspectRatioOption,
        durationSec: Int,
        audioMood: AudioMood,
        language: AppLanguage,
        onProgressUpdate: (progress: Float, stageText: String, stageIndex: Int) -> Unit
    ): Result<VideoEntity> {
        val profile = userProfile.firstOrNull() ?: UserProfileEntity()
        if (profile.creditsRemaining <= 0) {
            val outOfCreditsMsg = if (language == AppLanguage.SINHALA) {
                "ක්‍රෙඩිට්ස් අවසන් වී ඇත. කරුණාකර නොමිලේ ක්‍රෙඩිට්ස් ලබාගන්න."
            } else {
                "No credits remaining. Please claim your free credits."
            }
            return Result.failure(IllegalStateException(outOfCreditsMsg))
        }

        // Get API key from BuildConfig (configured via secrets plugin from .env)
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (_: Exception) {
            ""
        }

        try {
            val result = GeminiVideoService.generateRealVideo(
                context = context,
                userPrompt = prompt,
                style = style,
                aspectRatio = aspectRatio,
                durationSec = durationSec,
                audioMood = audioMood,
                language = language,
                apiKey = apiKey,
                onProgressUpdate = onProgressUpdate
            )

            // Deduct 1 credit ONLY after successful generation
            userProfileDao.deductCredit()

            val newVideo = VideoEntity(
                title = result.title,
                prompt = result.prompt,
                sinhalaPrompt = if (language == AppLanguage.SINHALA) prompt else "",
                styleId = style.id,
                aspectRatioName = aspectRatio.name,
                durationSec = result.durationSec,
                audioMoodId = audioMood.id,
                thumbnailResName = result.thumbnailResName,
                videoSceneType = result.videoSceneType,
                creatorName = profile.displayName + " (You)",
                creatorAvatar = profile.avatarEmoji,
                likesCount = 1,
                viewsCount = 1,
                isLiked = true,
                isSaved = false,
                isUserCreated = true,
                createdAt = System.currentTimeMillis(),
                tags = result.tags.joinToString(","),
                cameraMotion = result.cameraMotion,
                scene1Desc = result.scene1Desc,
                scene2Desc = result.scene2Desc,
                scene3Desc = result.scene3Desc,
                videoUrl = result.videoUrl,
                videoFilePath = result.localFilePath
            )

            val id = videoDao.insertVideo(newVideo)
            val savedVideo = newVideo.copy(id = id)
            return Result.success(savedVideo)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    suspend fun enhancePrompt(prompt: String, style: VideoStyle): String {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (_: Exception) {
            ""
        }
        return GeminiVideoService.enhancePromptWithAi(prompt, style, apiKey)
    }
}
