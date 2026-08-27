package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "memorized_verses")
data class MemorizedVerseEntity(
    @PrimaryKey
    val id: String,
    val reference: String,
    val text: String,
    val version: String = "LSG",
    val bookId: String = "",
    val chapter: Int = 1,
    val verse: Int = 1,
    val category: String = "Général", // "Foi", "Paix", "Combat", "Promesse", "Identité"
    val boxLevel: Int = 1, // 1: Quotidien, 2: 3 jours, 3: Hebdomadaire, 4: Bimensuel, 5: Maîtrisé !
    val nextReviewTimestamp: Long = System.currentTimeMillis(),
    val lastReviewedTimestamp: Long = 0L,
    val reviewCount: Int = 0,
    val successStreak: Int = 0,
    val isMastered: Boolean = false,
    val addedDate: Long = System.currentTimeMillis()
)
