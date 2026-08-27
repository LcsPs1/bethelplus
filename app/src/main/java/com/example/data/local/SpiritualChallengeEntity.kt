package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "spiritual_challenges")
data class SpiritualChallengeEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val subtitle: String,
    val category: String, // "GRATITUDE", "FASTING", "INTERCESSION", "MIND_RENEWAL", "PEACE"
    val totalDays: Int,
    val completedDaysCsv: String = "", // e.g. "1,2,3"
    val isActive: Boolean = false,
    val isCompleted: Boolean = false,
    val startDate: Long = 0L,
    val reminderHour: Int = 8,
    val reminderMinute: Int = 30,
    val isReminderEnabled: Boolean = true,
    val badgeName: String = "Couronne de Persévérance",
    val badgeDescription: String = "Fidélité et marche quotidienne accomplies.",
    val completionDate: Long = 0L
)

data class ChallengeDayContent(
    val dayNumber: Int,
    val title: String,
    val scriptureRef: String,
    val scriptureText: String,
    val reflection: String,
    val dailyAction: String,
    val guidedPrayer: String
)
