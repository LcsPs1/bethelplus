package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "prayer_journals")
data class PrayerJournalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val category: String = "Prière", // "Prière", "Intercession", "Action de grâce", "Méditation"
    val createdAt: Long = System.currentTimeMillis(),
    val isAnswered: Boolean = false,
    val answeredAt: Long? = null,
    val testimony: String? = null
)
