package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bible_verses")
data class BibleVerseEntity(
    @PrimaryKey
    val id: String, // e.g. "LSG_GEN_1_1" or "DRB_JHN_3_16"
    val version: String, // "LSG" or "DRB"
    val bookId: String, // "GEN", "EXO", "PSA", "MAT", "JHN", etc.
    val bookName: String, // "Genèse", "Jean", "Psaumes"
    val testament: String, // "OT" (Ancien Testament) or "NT" (Nouveau Testament)
    val chapter: Int,
    val verse: Int,
    val text: String,
    val highlightColor: String? = null, // "GOLD", "BLUE", "GREEN", "PURPLE", "TERRACOTTA"
    val isBookmarked: Boolean = false,
    val personalNote: String? = null,
    val noteUpdatedAt: Long? = null
)

@Entity(tableName = "bible_reading_plans")
data class BibleReadingPlanEntity(
    @PrimaryKey
    val id: String, // "plan_1_year", "plan_nt_90", "plan_psalms_30", "plan_gospels_40"
    val title: String,
    val subtitle: String,
    val totalDays: Int,
    val currentDay: Int = 1,
    val completedDaysCsv: String = "", // e.g. "1,2,3"
    val isActive: Boolean = false,
    val lastReadTimestamp: Long = 0L
)
