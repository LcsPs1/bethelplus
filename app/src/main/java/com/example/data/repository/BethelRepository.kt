package com.example.data.repository

import android.content.Context
import com.example.data.local.BethelDao
import com.example.data.local.BibleData
import com.example.data.local.BibleReadingPlanEntity
import com.example.data.local.BibleTextProvider
import com.example.data.local.BibleVerseEntity
import com.example.data.local.FavoriteVerseEntity
import com.example.data.local.PrayerJournalEntity
import com.example.data.local.PrayerReminderEntity
import com.example.data.local.PrayerSessionEntity
import com.example.data.local.UserProfileEntity
import com.example.services.NotificationHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class BethelRepository(
    private val context: Context,
    private val dao: BethelDao
) {
    // --- Reminders ---
    val allReminders: Flow<List<PrayerReminderEntity>> = dao.getAllReminders()
    val activeReminders: Flow<List<PrayerReminderEntity>> = dao.getActiveReminders()

    suspend fun ensureInitialDefaults() {
        // 1. Ensure profile exists and defaults to "Fidèle"
        val profile = dao.getUserProfileSync()
        if (profile == null) {
            dao.insertOrUpdateProfile(
                UserProfileEntity(
                    id = 1,
                    firstName = "Fidèle",
                    denomination = "Œcuménique",
                    dailyGoalMinutes = 15,
                    soundTheme = "HARPE"
                )
            )
        } else if (profile.firstName == "Jean-Luc" && !profile.isGoogleConnected) {
            dao.insertOrUpdateProfile(profile.copy(firstName = "Fidèle"))
        }

        // 2. Ensure prayer reminders exist
        if (dao.getRemindersCount() == 0) {
            restoreCanonicalReminders()
        } else {
            // Re-register active reminders with AlarmManager
            val reminders = dao.getAllRemindersSync()
            for (r in reminders) {
                if (r.isEnabled) {
                    NotificationHelper.scheduleReminder(context, r)
                }
            }
        }

        // 3. Ensure Bible reading plans exist
        if (dao.getReadingPlansCount() == 0) {
            dao.insertReadingPlans(BibleData.DEFAULT_READING_PLANS)
        }
    }

    suspend fun restoreCanonicalReminders() {
        val canonicals = listOf(
            PrayerReminderEntity(
                title = "Prière de l'Aube (Matines)",
                hour = 6,
                minute = 0,
                daysMask = 127,
                isEnabled = true,
                soundscape = "CLOCHE",
                canonicalType = "AUBE"
            ),
            PrayerReminderEntity(
                title = "Prière de Midi (Sexte)",
                hour = 12,
                minute = 0,
                daysMask = 127,
                isEnabled = true,
                soundscape = "HARPE",
                canonicalType = "MIDI"
            ),
            PrayerReminderEntity(
                title = "Prière du Vespéral (Vêpres)",
                hour = 18,
                minute = 30,
                daysMask = 127,
                isEnabled = true,
                soundscape = "PLUIE",
                canonicalType = "VESPERAL"
            ),
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
        for (r in canonicals) {
            val id = dao.insertReminder(r)
            NotificationHelper.scheduleReminder(context, r.copy(id = id.toInt()))
        }
    }

    suspend fun saveReminder(reminder: PrayerReminderEntity): Long {
        val id = if (reminder.id == 0) {
            dao.insertReminder(reminder)
        } else {
            dao.updateReminder(reminder)
            reminder.id.toLong()
        }
        val updatedReminder = reminder.copy(id = id.toInt())
        if (updatedReminder.isEnabled) {
            NotificationHelper.scheduleReminder(context, updatedReminder)
        } else {
            NotificationHelper.cancelReminder(context, updatedReminder.id)
        }
        return id
    }

    suspend fun toggleReminder(reminder: PrayerReminderEntity, isEnabled: Boolean) {
        val updated = reminder.copy(isEnabled = isEnabled)
        dao.updateReminder(updated)
        if (isEnabled) {
            NotificationHelper.scheduleReminder(context, updated)
        } else {
            NotificationHelper.cancelReminder(context, updated.id)
        }
    }

    suspend fun deleteReminder(reminder: PrayerReminderEntity) {
        NotificationHelper.cancelReminder(context, reminder.id)
        dao.deleteReminder(reminder)
    }

    // --- Sessions & Streaks ---
    val allSessions: Flow<List<PrayerSessionEntity>> = dao.getAllSessions()

    suspend fun recordPrayerSession(
        title: String,
        durationSeconds: Int,
        meditationId: String? = null,
        notes: String? = null
    ): Long {
        val session = PrayerSessionEntity(
            dateMillis = System.currentTimeMillis(),
            title = title,
            durationSeconds = durationSeconds,
            meditationId = meditationId,
            notes = notes,
            completed = true
        )
        return dao.insertSession(session)
    }

    // --- Journal ---
    val allJournalEntries: Flow<List<PrayerJournalEntity>> = dao.getAllJournalEntries()

    suspend fun saveJournalEntry(entry: PrayerJournalEntity): Long {
        return if (entry.id == 0L) {
            dao.insertJournalEntry(entry)
        } else {
            dao.updateJournalEntry(entry)
            entry.id
        }
    }

    suspend fun markJournalAnswered(entry: PrayerJournalEntity, testimony: String) {
        val updated = entry.copy(
            isAnswered = true,
            answeredAt = System.currentTimeMillis(),
            testimony = testimony
        )
        dao.updateJournalEntry(updated)
    }

    suspend fun deleteJournalEntry(entry: PrayerJournalEntity) {
        dao.deleteJournalEntry(entry)
    }

    // --- Favorites ---
    val allFavoriteVerses: Flow<List<FavoriteVerseEntity>> = dao.getAllFavoriteVerses()

    fun isVerseFavorite(verseId: String): Flow<Boolean> = dao.isVerseFavorite(verseId)

    suspend fun toggleFavoriteVerse(
        verseId: String,
        reference: String,
        text: String,
        theme: String,
        reflection: String,
        isFav: Boolean
    ) {
        if (isFav) {
            dao.deleteFavoriteVerseById(verseId)
        } else {
            dao.insertFavoriteVerse(
                FavoriteVerseEntity(
                    id = verseId,
                    reference = reference,
                    text = text,
                    theme = theme,
                    reflection = reflection
                )
            )
        }
    }

    // --- Profile & Google Sync ---
    val userProfile: Flow<UserProfileEntity?> = dao.getUserProfile()

    suspend fun updateProfile(profile: UserProfileEntity) {
        dao.insertOrUpdateProfile(profile)
    }

    suspend fun connectGoogleAccount(name: String, email: String, photoUrl: String = "") {
        val current = dao.getUserProfileSync() ?: UserProfileEntity()
        val updated = current.copy(
            firstName = if (name.isNotBlank()) name else current.firstName,
            email = email,
            photoUrl = photoUrl,
            isGoogleConnected = true,
            lastBackupTimestamp = System.currentTimeMillis()
        )
        dao.insertOrUpdateProfile(updated)
    }

    suspend fun disconnectGoogleAccount() {
        val current = dao.getUserProfileSync() ?: UserProfileEntity()
        val updated = current.copy(
            isGoogleConnected = false
        )
        dao.insertOrUpdateProfile(updated)
    }

    suspend fun recordBackupTimestamp(): Long {
        val now = System.currentTimeMillis()
        val current = dao.getUserProfileSync() ?: UserProfileEntity()
        dao.insertOrUpdateProfile(current.copy(lastBackupTimestamp = now))
        return now
    }

    // --- Bible Module Repository ---
    fun getVersesForChapterFlow(version: String, bookId: String, chapter: Int): Flow<List<BibleVerseEntity>> {
        return dao.getVersesForChapter(version, bookId, chapter)
    }

    suspend fun ensureChapterLoaded(version: String, bookId: String, chapter: Int) {
        val existing = dao.getVersesForChapter(version, bookId, chapter).firstOrNull()
        if (existing.isNullOrEmpty()) {
            val generated = BibleTextProvider.getVersesForChapter(version, bookId, chapter)
            dao.insertVerses(generated)
        }
    }

    fun getMarkedVersesFlow(): Flow<List<BibleVerseEntity>> = dao.getMarkedVerses()
    fun getHighlightedVersesFlow(): Flow<List<BibleVerseEntity>> = dao.getHighlightedVerses()
    fun getBookmarkedVersesFlow(): Flow<List<BibleVerseEntity>> = dao.getBookmarkedVerses()
    fun getVersesWithNotesFlow(): Flow<List<BibleVerseEntity>> = dao.getVersesWithNotes()

    suspend fun toggleVerseHighlight(verse: BibleVerseEntity, color: String?) {
        val newColor = if (verse.highlightColor == color) null else color
        dao.updateHighlightColor(verse.id, newColor)
    }

    suspend fun toggleVerseBookmark(verse: BibleVerseEntity) {
        dao.updateBookmarkStatus(verse.id, !verse.isBookmarked)
    }

    suspend fun saveVerseNote(verse: BibleVerseEntity, note: String?) {
        dao.updatePersonalNote(verse.id, note?.trim(), System.currentTimeMillis())
    }

    suspend fun searchBible(version: String, query: String): List<BibleVerseEntity> {
        val cleanQuery = query.trim()
        if (cleanQuery.isBlank()) return emptyList()

        // Check if query is a direct reference like "Jean 3:16" or "Jean 3" or "Ps 23" or "Genèse 28:19"
        val refMatch = Regex("""^([0-9]?\s*[a-zA-ZÀ-ÿ]+)\s*(\d+)(?::(\d+))?""").find(cleanQuery)
        if (refMatch != null) {
            val bookQuery = refMatch.groupValues[1].trim()
            val chapterNum = refMatch.groupValues[2].toIntOrNull() ?: 1
            val verseNum = refMatch.groupValues.getOrNull(3)?.toIntOrNull()

            val matchedBook = BibleData.getBookByName(bookQuery)
            if (matchedBook != null) {
                ensureChapterLoaded(version, matchedBook.id, chapterNum)
                val chapterVerses = dao.getVersesForChapter(version, matchedBook.id, chapterNum).firstOrNull()
                    ?: BibleTextProvider.getVersesForChapter(version, matchedBook.id, chapterNum)

                return if (verseNum != null) {
                    chapterVerses.filter { it.verse == verseNum }
                } else {
                    chapterVerses
                }
            }
        }

        // Full text search in Room DB
        val dbResults = dao.searchVersesByText(version, cleanQuery)
        if (dbResults.isNotEmpty()) {
            return dbResults
        }

        // Also search in all known key chapters in provider
        val results = mutableListOf<BibleVerseEntity>()
        for ((key, list) in BibleTextProvider.LSG_CHAPTERS) {
            val parts = key.split("_")
            val bookId = parts[0]
            val chap = parts[1].toInt()
            val bookInfo = BibleData.getBookById(bookId)
            for ((vNum, text) in list) {
                if (text.contains(cleanQuery, ignoreCase = true)) {
                    results.add(
                        BibleVerseEntity(
                            id = "${version}_${bookId}_${chap}_$vNum",
                            version = version,
                            bookId = bookId,
                            bookName = bookInfo.name,
                            testament = bookInfo.testament,
                            chapter = chap,
                            verse = vNum,
                            text = text
                        )
                    )
                }
            }
        }
        return results
    }

    // --- Reading Plans ---
    val allReadingPlans: Flow<List<BibleReadingPlanEntity>> = dao.getAllReadingPlans()
    val activeReadingPlan: Flow<BibleReadingPlanEntity?> = dao.getActiveReadingPlan()

    suspend fun setActiveReadingPlan(planId: String) {
        dao.setActivePlan(planId)
    }

    suspend fun toggleReadingPlanDay(plan: BibleReadingPlanEntity, dayNumber: Int) {
        val completedSet = plan.completedDaysCsv
            .split(",")
            .filter { it.isNotBlank() }
            .mapNotNull { it.toIntOrNull() }
            .toMutableSet()

        if (completedSet.contains(dayNumber)) {
            completedSet.remove(dayNumber)
        } else {
            completedSet.add(dayNumber)
        }

        val updatedCsv = completedSet.sorted().joinToString(",")
        val nextDay = if (completedSet.isEmpty()) 1 else (completedSet.maxOrNull() ?: 1)
        val updatedPlan = plan.copy(
            completedDaysCsv = updatedCsv,
            currentDay = nextDay,
            lastReadTimestamp = System.currentTimeMillis()
        )
        dao.insertOrUpdateReadingPlan(updatedPlan)
    }

    // --- Spiritual Challenges Repository ---
    val allChallenges: Flow<List<com.example.data.local.SpiritualChallengeEntity>> = dao.getAllChallenges()
    val activeChallenge: Flow<com.example.data.local.SpiritualChallengeEntity?> = dao.getActiveChallenge()

    suspend fun setActiveChallenge(challengeId: String) {
        val challenges = dao.getChallengesCount()
        if (challenges == 0) {
            dao.insertChallenges(com.example.data.local.ChallengeData.ALL_CHALLENGES)
        }
        dao.setActiveChallenge(challengeId)
    }

    suspend fun toggleChallengeDay(challenge: com.example.data.local.SpiritualChallengeEntity, dayNumber: Int) {
        val completedSet = challenge.completedDaysCsv
            .split(",")
            .filter { it.isNotBlank() }
            .mapNotNull { it.toIntOrNull() }
            .toMutableSet()

        if (completedSet.contains(dayNumber)) {
            completedSet.remove(dayNumber)
        } else {
            completedSet.add(dayNumber)
        }

        val updatedCsv = completedSet.sorted().joinToString(",")
        val isAllCompleted = completedSet.size >= challenge.totalDays
        val updatedChallenge = challenge.copy(
            completedDaysCsv = updatedCsv,
            isCompleted = isAllCompleted,
            completionDate = if (isAllCompleted && challenge.completionDate == 0L) System.currentTimeMillis() else challenge.completionDate
        )
        dao.insertOrUpdateChallenge(updatedChallenge)
    }

    suspend fun toggleChallengeReminder(challenge: com.example.data.local.SpiritualChallengeEntity, isEnabled: Boolean) {
        val updated = challenge.copy(isReminderEnabled = isEnabled)
        dao.insertOrUpdateChallenge(updated)
    }

    // --- Memorized Verses (Flashcards & Leitner) ---
    val allMemorizedVerses: Flow<List<com.example.data.local.MemorizedVerseEntity>> = dao.getAllMemorizedVerses()
    val dueMemorizedVerses: Flow<List<com.example.data.local.MemorizedVerseEntity>> = dao.getDueMemorizedVerses()
    val masteredMemorizedVerses: Flow<List<com.example.data.local.MemorizedVerseEntity>> = dao.getMasteredMemorizedVerses()

    fun isVerseMemorized(ref: String): Flow<Boolean> = dao.isVerseMemorized(ref)

    suspend fun addVerseToMemory(
        reference: String,
        text: String,
        version: String = "LSG",
        category: String = "Général",
        bookId: String = "",
        chapter: Int = 1,
        verse: Int = 1
    ) {
        val id = "mem_${reference.replace(" ", "_").replace(":", "_")}_${System.currentTimeMillis()}"
        val entity = com.example.data.local.MemorizedVerseEntity(
            id = id,
            reference = reference,
            text = text,
            version = version,
            bookId = bookId,
            chapter = chapter,
            verse = verse,
            category = category,
            boxLevel = 1,
            nextReviewTimestamp = System.currentTimeMillis()
        )
        dao.insertMemorizedVerse(entity)
    }

    suspend fun recordFlashcardReview(verse: com.example.data.local.MemorizedVerseEntity, isKnown: Boolean) {
        val (newBox, nextReview) = com.example.data.local.MemoryData.calculateNextReview(verse.boxLevel, isKnown)
        val updated = verse.copy(
            boxLevel = newBox,
            nextReviewTimestamp = nextReview,
            lastReviewedTimestamp = System.currentTimeMillis(),
            reviewCount = verse.reviewCount + 1,
            successStreak = if (isKnown) verse.successStreak + 1 else 0,
            isMastered = (newBox >= 5)
        )
        dao.updateMemorizedVerse(updated)
    }

    suspend fun deleteMemorizedVerse(verse: com.example.data.local.MemorizedVerseEntity) {
        dao.deleteMemorizedVerse(verse)
    }

    // --- Spiritual Alarm ---
    val spiritualAlarm: Flow<com.example.data.local.SpiritualAlarmEntity?> = dao.getSpiritualAlarm()

    suspend fun saveSpiritualAlarm(alarm: com.example.data.local.SpiritualAlarmEntity) {
        dao.insertOrUpdateSpiritualAlarm(alarm)
        if (alarm.isEnabled) {
            NotificationHelper.scheduleSpiritualAlarm(context, alarm.hour, alarm.minute)
        } else {
            NotificationHelper.cancelSpiritualAlarm(context)
        }
    }

    suspend fun toggleSpiritualAlarm(alarm: com.example.data.local.SpiritualAlarmEntity, isEnabled: Boolean) {
        val updated = alarm.copy(isEnabled = isEnabled)
        dao.insertOrUpdateSpiritualAlarm(updated)
        if (isEnabled) {
            NotificationHelper.scheduleSpiritualAlarm(context, updated.hour, updated.minute)
        } else {
            NotificationHelper.cancelSpiritualAlarm(context)
        }
    }
}
