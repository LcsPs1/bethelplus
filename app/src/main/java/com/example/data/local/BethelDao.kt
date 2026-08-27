package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BethelDao {
    // --- Reminders ---
    @Query("SELECT * FROM prayer_reminders ORDER BY hour ASC, minute ASC")
    fun getAllReminders(): Flow<List<PrayerReminderEntity>>

    @Query("SELECT * FROM prayer_reminders WHERE isEnabled = 1 ORDER BY hour ASC, minute ASC")
    fun getActiveReminders(): Flow<List<PrayerReminderEntity>>

    @Query("SELECT COUNT(*) FROM prayer_reminders")
    suspend fun getRemindersCount(): Int

    @Query("SELECT * FROM prayer_reminders")
    suspend fun getAllRemindersSync(): List<PrayerReminderEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: PrayerReminderEntity): Long

    @Update
    suspend fun updateReminder(reminder: PrayerReminderEntity)

    @Delete
    suspend fun deleteReminder(reminder: PrayerReminderEntity)

    @Query("DELETE FROM prayer_reminders WHERE id = :id")
    suspend fun deleteReminderById(id: Int)

    // --- Prayer Sessions / Streaks ---
    @Query("SELECT * FROM prayer_sessions ORDER BY dateMillis DESC")
    fun getAllSessions(): Flow<List<PrayerSessionEntity>>

    @Query("SELECT * FROM prayer_sessions WHERE dateMillis >= :startOfDayMillis AND dateMillis <= :endOfDayMillis")
    fun getSessionsForDay(startOfDayMillis: Long, endOfDayMillis: Long): Flow<List<PrayerSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: PrayerSessionEntity): Long

    // --- Prayer Journal ---
    @Query("SELECT * FROM prayer_journals ORDER BY createdAt DESC")
    fun getAllJournalEntries(): Flow<List<PrayerJournalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJournalEntry(entry: PrayerJournalEntity): Long

    @Update
    suspend fun updateJournalEntry(entry: PrayerJournalEntity)

    @Delete
    suspend fun deleteJournalEntry(entry: PrayerJournalEntity)

    // --- Favorites Verses ---
    @Query("SELECT * FROM favorite_verses ORDER BY savedAt DESC")
    fun getAllFavoriteVerses(): Flow<List<FavoriteVerseEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_verses WHERE id = :verseId)")
    fun isVerseFavorite(verseId: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteVerse(verse: FavoriteVerseEntity)

    @Query("DELETE FROM favorite_verses WHERE id = :verseId")
    suspend fun deleteFavoriteVerseById(verseId: String)

    // --- User Profile ---
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getUserProfileSync(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfileEntity)

    // --- Bible Module ---
    @Query("SELECT * FROM bible_verses WHERE version = :version AND bookId = :bookId AND chapter = :chapter ORDER BY verse ASC")
    fun getVersesForChapter(version: String, bookId: String, chapter: Int): Flow<List<BibleVerseEntity>>

    @Query("SELECT * FROM bible_verses WHERE id = :id LIMIT 1")
    suspend fun getVerseById(id: String): BibleVerseEntity?

    @Query("SELECT * FROM bible_verses WHERE highlightColor IS NOT NULL OR isBookmarked = 1 OR personalNote IS NOT NULL ORDER BY noteUpdatedAt DESC")
    fun getMarkedVerses(): Flow<List<BibleVerseEntity>>

    @Query("SELECT * FROM bible_verses WHERE highlightColor IS NOT NULL ORDER BY verse ASC")
    fun getHighlightedVerses(): Flow<List<BibleVerseEntity>>

    @Query("SELECT * FROM bible_verses WHERE isBookmarked = 1")
    fun getBookmarkedVerses(): Flow<List<BibleVerseEntity>>

    @Query("SELECT * FROM bible_verses WHERE personalNote IS NOT NULL AND personalNote != ''")
    fun getVersesWithNotes(): Flow<List<BibleVerseEntity>>

    @Query("SELECT * FROM bible_verses WHERE version = :version AND text LIKE '%' || :query || '%' LIMIT 100")
    suspend fun searchVersesByText(version: String, query: String): List<BibleVerseEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVerses(verses: List<BibleVerseEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateVerse(verse: BibleVerseEntity)

    @Query("UPDATE bible_verses SET highlightColor = :color WHERE id = :verseId")
    suspend fun updateHighlightColor(verseId: String, color: String?)

    @Query("UPDATE bible_verses SET isBookmarked = :isBookmarked WHERE id = :verseId")
    suspend fun updateBookmarkStatus(verseId: String, isBookmarked: Boolean)

    @Query("UPDATE bible_verses SET personalNote = :note, noteUpdatedAt = :updatedAt WHERE id = :verseId")
    suspend fun updatePersonalNote(verseId: String, note: String?, updatedAt: Long = System.currentTimeMillis())

    // --- Bible Reading Plans ---
    @Query("SELECT * FROM bible_reading_plans")
    fun getAllReadingPlans(): Flow<List<BibleReadingPlanEntity>>

    @Query("SELECT * FROM bible_reading_plans WHERE isActive = 1 LIMIT 1")
    fun getActiveReadingPlan(): Flow<BibleReadingPlanEntity?>

    @Query("SELECT COUNT(*) FROM bible_reading_plans")
    suspend fun getReadingPlansCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReadingPlans(plans: List<BibleReadingPlanEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateReadingPlan(plan: BibleReadingPlanEntity)

    @Query("UPDATE bible_reading_plans SET isActive = (CASE WHEN id = :planId THEN 1 ELSE 0 END)")
    suspend fun setActivePlan(planId: String)

    // --- Spiritual Challenges ---
    @Query("SELECT * FROM spiritual_challenges")
    fun getAllChallenges(): Flow<List<SpiritualChallengeEntity>>

    @Query("SELECT * FROM spiritual_challenges WHERE isActive = 1 LIMIT 1")
    fun getActiveChallenge(): Flow<SpiritualChallengeEntity?>

    @Query("SELECT * FROM spiritual_challenges WHERE id = :id LIMIT 1")
    suspend fun getChallengeById(id: String): SpiritualChallengeEntity?

    @Query("SELECT COUNT(*) FROM spiritual_challenges")
    suspend fun getChallengesCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChallenges(challenges: List<SpiritualChallengeEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateChallenge(challenge: SpiritualChallengeEntity)

    @Query("UPDATE spiritual_challenges SET isActive = (CASE WHEN id = :challengeId THEN 1 ELSE 0 END)")
    suspend fun setActiveChallenge(challengeId: String)

    // --- Memorized Verses (Flashcards & Leitner) ---
    @Query("SELECT * FROM memorized_verses ORDER BY addedDate DESC")
    fun getAllMemorizedVerses(): Flow<List<MemorizedVerseEntity>>

    @Query("SELECT * FROM memorized_verses WHERE nextReviewTimestamp <= :now ORDER BY boxLevel ASC, nextReviewTimestamp ASC")
    fun getDueMemorizedVerses(now: Long = System.currentTimeMillis()): Flow<List<MemorizedVerseEntity>>

    @Query("SELECT * FROM memorized_verses WHERE isMastered = 1 ORDER BY lastReviewedTimestamp DESC")
    fun getMasteredMemorizedVerses(): Flow<List<MemorizedVerseEntity>>

    @Query("SELECT COUNT(*) FROM memorized_verses")
    suspend fun getMemorizedVersesCount(): Int

    @Query("SELECT EXISTS(SELECT 1 FROM memorized_verses WHERE reference = :reference)")
    fun isVerseMemorized(reference: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemorizedVerse(verse: MemorizedVerseEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemorizedVerses(verses: List<MemorizedVerseEntity>)

    @Update
    suspend fun updateMemorizedVerse(verse: MemorizedVerseEntity)

    @Delete
    suspend fun deleteMemorizedVerse(verse: MemorizedVerseEntity)

    @Query("DELETE FROM memorized_verses WHERE id = :id")
    suspend fun deleteMemorizedVerseById(id: String)

    // --- Spiritual Alarm ---
    @Query("SELECT * FROM spiritual_alarm WHERE id = 1 LIMIT 1")
    fun getSpiritualAlarm(): Flow<SpiritualAlarmEntity?>

    @Query("SELECT * FROM spiritual_alarm WHERE id = 1 LIMIT 1")
    suspend fun getSpiritualAlarmSync(): SpiritualAlarmEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSpiritualAlarm(alarm: SpiritualAlarmEntity)
}
