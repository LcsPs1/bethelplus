package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_verses")
data class FavoriteVerseEntity(
    @PrimaryKey
    val id: String,
    val reference: String,
    val text: String,
    val theme: String,
    val reflection: String,
    val savedAt: Long = System.currentTimeMillis()
)
