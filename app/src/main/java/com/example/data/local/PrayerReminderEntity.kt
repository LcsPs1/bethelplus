package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "prayer_reminders")
data class PrayerReminderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val hour: Int,
    val minute: Int,
    val daysMask: Int = 127, // 127 = every day (Mon-Sun: 1|2|4|8|16|32|64)
    val isEnabled: Boolean = true,
    val soundscape: String = "CLOCHE",
    val canonicalType: String = "CUSTOM" // "AUBE", "MIDI", "VESPERAL", "COMPLIES", "CUSTOM"
)
