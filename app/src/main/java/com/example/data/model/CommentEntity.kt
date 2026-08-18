package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val videoId: Long,
    val authorName: String,
    val authorAvatar: String,
    val text: String,
    val createdAt: Long = System.currentTimeMillis(),
    val likes: Int = 0
)
