package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.VideoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoDao {
    @Query("SELECT * FROM videos ORDER BY createdAt DESC")
    fun getAllVideos(): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE isUserCreated = 1 ORDER BY createdAt DESC")
    fun getUserVideos(): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE isLiked = 1 ORDER BY createdAt DESC")
    fun getLikedVideos(): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE isSaved = 1 ORDER BY createdAt DESC")
    fun getSavedVideos(): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE id = :id LIMIT 1")
    fun getVideoById(id: Long): Flow<VideoEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideo(video: VideoEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideos(videos: List<VideoEntity>)

    @Update
    suspend fun updateVideo(video: VideoEntity)

    @Query("UPDATE videos SET isLiked = :isLiked, likesCount = likesCount + :delta WHERE id = :id")
    suspend fun toggleLike(id: Long, isLiked: Boolean, delta: Int)

    @Query("UPDATE videos SET isSaved = :isSaved WHERE id = :id")
    suspend fun toggleSave(id: Long, isSaved: Boolean)

    @Query("UPDATE videos SET viewsCount = viewsCount + 1 WHERE id = :id")
    suspend fun incrementViews(id: Long)

    @Query("DELETE FROM videos WHERE id = :id")
    suspend fun deleteVideoById(id: Long)

    @Query("DELETE FROM videos")
    suspend fun deleteAllVideos()
}
