package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "spiritual_alarm")
data class SpiritualAlarmEntity(
    @PrimaryKey
    val id: Int = 1,
    val isEnabled: Boolean = false,
    val hour: Int = 6,
    val minute: Int = 30,
    val daysMask: Int = 127, // Everyday by default
    val soundscape: String = "HARPE", // "HARPE", "CLOCHE", "SANCTUAIRE", "OISEAUX"
    val isDndEnabledOnWakeup: Boolean = true,
    val morningVerseTheme: String = "GRACE", // "GRACE", "PROTECTION", "FORCE", "PAIX"
    val snoozeCountMinutes: Int = 5
)
