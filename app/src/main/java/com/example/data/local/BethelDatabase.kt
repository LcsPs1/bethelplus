package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        PrayerReminderEntity::class,
        PrayerSessionEntity::class,
        PrayerJournalEntity::class,
        FavoriteVerseEntity::class,
        UserProfileEntity::class,
        BibleVerseEntity::class,
        BibleReadingPlanEntity::class,
        SpiritualChallengeEntity::class,
        MemorizedVerseEntity::class,
        SpiritualAlarmEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class BethelDatabase : RoomDatabase() {
    abstract fun bethelDao(): BethelDao

    companion object {
        @Volatile
        private var INSTANCE: BethelDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): BethelDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BethelDatabase::class.java,
                    "bethel_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(BethelDatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class BethelDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.bethelDao())
                    }
                }
            }

            private suspend fun populateInitialData(dao: BethelDao) {
                // Initialize default profile
                dao.insertOrUpdateProfile(
                    UserProfileEntity(
                        id = 1,
                        firstName = "Fidèle",
                        denomination = "Œcuménique",
                        dailyGoalMinutes = 15,
                        soundTheme = "HARPE"
                    )
                )

                // Initialize 4 canonical spiritual prayer hours
                dao.insertReminder(
                    PrayerReminderEntity(
                        title = "Prière de l'Aube (Matines)",
                        hour = 6,
                        minute = 0,
                        daysMask = 127,
                        isEnabled = true,
                        soundscape = "CLOCHE",
                        canonicalType = "AUBE"
                    )
                )
                dao.insertReminder(
                    PrayerReminderEntity(
                        title = "Prière de Midi (Sexte)",
                        hour = 12,
                        minute = 0,
                        daysMask = 127,
                        isEnabled = true,
                        soundscape = "HARPE",
                        canonicalType = "MIDI"
                    )
                )
                dao.insertReminder(
                    PrayerReminderEntity(
                        title = "Prière du Vespéral (Vêpres)",
                        hour = 18,
                        minute = 30,
                        daysMask = 127,
                        isEnabled = true,
                        soundscape = "PLUIE",
                        canonicalType = "VESPERAL"
                    )
                )
                dao.insertReminder(
                    PrayerReminderEntity(
                        title = "Prière de Nuit (Complies)",
                        hour = 21,
                        minute = 30,
                        daysMask = 127,
                        isEnabled = true,
                        soundscape = "SILENCE",
                        canonicalType = "COMPLIES"
                    )
                )

                // Initial welcome journal entry / intent
                dao.insertJournalEntry(
                    PrayerJournalEntity(
                        title = "Consécration à Béthel (Maison de Dieu)",
                        content = "Seigneur, que ce lieu soit pour moi une porte des cieux, comme pour Jacob à Béthel. Accorde-moi la grâce de la fidélité dans la prière et l'écoute de Ta Parole chaque jour.",
                        category = "Prière",
                        createdAt = System.currentTimeMillis()
                    )
                )

                // Populate Curated Challenges
                dao.insertChallenges(ChallengeData.ALL_CHALLENGES)
                // Set first challenge active by default
                dao.setActiveChallenge("challenge_gratitude_7")

                // Populate Starter Memory Verses
                dao.insertMemorizedVerses(MemoryData.INITIAL_MEMORY_VERSES)

                // Initialize Spiritual Alarm
                dao.insertOrUpdateSpiritualAlarm(
                    SpiritualAlarmEntity(
                        id = 1,
                        isEnabled = false,
                        hour = 6,
                        minute = 30,
                        daysMask = 127,
                        soundscape = "HARPE",
                        isDndEnabledOnWakeup = true,
                        morningVerseTheme = "GRACE",
                        snoozeCountMinutes = 5
                    )
                )
            }
        }
    }
}
