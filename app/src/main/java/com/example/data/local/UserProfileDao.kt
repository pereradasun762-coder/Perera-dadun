package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfileEntity)

    @Query("UPDATE user_profile SET creditsRemaining = creditsRemaining - 1, totalVideosGenerated = totalVideosGenerated + 1 WHERE id = 1 AND creditsRemaining > 0")
    suspend fun deductCredit(): Int

    @Query("UPDATE user_profile SET creditsRemaining = creditsRemaining + :credits, lastRefillTimestamp = :timestamp WHERE id = 1")
    suspend fun addCredits(credits: Int, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE user_profile SET languageCode = :languageCode WHERE id = 1")
    suspend fun setLanguage(languageCode: String)
}
