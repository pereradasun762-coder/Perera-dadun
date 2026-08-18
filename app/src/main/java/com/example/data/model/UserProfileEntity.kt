package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey
    val id: Int = 1,
    val username: String = "Kattiya_Creator_LK",
    val displayName: String = "Kasun Perera",
    val avatarEmoji: String = "🎬",
    val creditsRemaining: Int = 5,
    val maxCredits: Int = 10,
    val lastRefillTimestamp: Long = System.currentTimeMillis(),
    val totalVideosGenerated: Int = 0,
    val totalLikesReceived: Int = 0,
    val languageCode: String = "en",
    val videoQuality: String = "1080p",
    val autoPlayAudio: Boolean = true
) {
    val language: AppLanguage
        get() = if (languageCode == "si") AppLanguage.SINHALA else AppLanguage.ENGLISH
}
