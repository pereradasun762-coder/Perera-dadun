package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "videos")
data class VideoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val prompt: String,
    val sinhalaPrompt: String = "",
    val styleId: String,
    val aspectRatioName: String,
    val durationSec: Int = 10,
    val audioMoodId: String,
    val thumbnailResName: String,
    val videoSceneType: String,
    val creatorName: String,
    val creatorAvatar: String,
    val likesCount: Int = 0,
    val viewsCount: Int = 0,
    val isLiked: Boolean = false,
    val isSaved: Boolean = false,
    val isUserCreated: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val tags: String = "",
    val cameraMotion: String = "camera_pan",
    val scene1Desc: String = "",
    val scene2Desc: String = "",
    val scene3Desc: String = "",
    val videoUrl: String = "",
    val videoFilePath: String = ""
) {
    val style: VideoStyle
        get() = VideoStyle.entries.find { it.id == styleId } ?: VideoStyle.CINEMATIC

    val aspectRatio: AspectRatioOption
        get() = try {
            AspectRatioOption.valueOf(aspectRatioName)
        } catch (_: Exception) {
            AspectRatioOption.PORTRAIT_9_16
        }

    val audioMood: AudioMood
        get() = AudioMood.entries.find { it.id == audioMoodId } ?: AudioMood.SYNTHWAVE
}
