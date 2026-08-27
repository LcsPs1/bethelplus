package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey
    val id: Int = 1,
    val firstName: String = "Fidèle",
    val email: String = "",
    val photoUrl: String = "",
    val isGoogleConnected: Boolean = false,
    val lastBackupTimestamp: Long = 0L,
    val denomination: String = "Œcuménique",
    val dailyGoalMinutes: Int = 15,
    val soundTheme: String = "HARPE", // "HARPE", "CLOCHE", "PLUIE", "SILENCE", "BRISE"
    val themeMode: String = "SYSTEM", // "SYSTEM", "LIGHT", "DARK"
    val scriptureFontScale: Float = 1.0f,
    val isAutoDndEnabled: Boolean = false
)
