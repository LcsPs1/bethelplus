package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "prayer_sessions")
data class PrayerSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dateMillis: Long = System.currentTimeMillis(),
    val meditationId: String? = null,
    val title: String,
    val durationSeconds: Int,
    val completed: Boolean = true,
    val notes: String? = null
)
