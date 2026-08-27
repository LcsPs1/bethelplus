package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.os.Vibrator
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.BethelDatabase
import com.example.data.local.BibleBookInfo
import com.example.data.local.BibleData
import com.example.data.local.BibleReadingPlanEntity
import com.example.data.local.BibleVerseEntity
import com.example.data.local.FavoriteVerseEntity
import com.example.data.local.PrayerJournalEntity
import com.example.data.local.PrayerReminderEntity
import com.example.data.local.PrayerSessionEntity
import com.example.data.local.UserProfileEntity
import com.example.data.model.DailyVerse
import com.example.data.model.DailyVerseRepository
import com.example.data.model.MeditationItem
import com.example.data.model.MeditationLibrary
import com.example.data.model.MeditationTheme
import com.example.data.repository.BethelRepository
import com.example.data.local.ChallengeData
import com.example.data.local.ChallengeDayContent
import com.example.data.local.MemorizedVerseEntity
import com.example.data.local.MemoryData
import com.example.data.local.SpiritualAlarmEntity
import com.example.data.local.SpiritualChallengeEntity
import com.example.data.model.SituationalPrayerItem
import com.example.data.model.SituationalPrayersRepository
import com.example.services.AudioSynthHelper
import com.example.services.BibleTtsManager
import com.example.services.DndHelper
import com.example.services.NotificationHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class SpiritualStats(
    val currentStreak: Int = 0,
    val totalMinutes: Int = 0,
    val totalSessions: Int = 0,
    val hasPrayedToday: Boolean = false,
    val weeklyHeatmap: List<DayActivity> = emptyList(),
    val unlockedBadges: List<SpiritualBadge> = emptyList()
)

data class DayActivity(
    val dayName: String,
    val dayOfMonth: Int,
    val isToday: Boolean,
    val sessionCount: Int,
    val totalMinutes: Int
)

data class SpiritualBadge(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: String,
    val isUnlocked: Boolean
)

class BethelViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BethelRepository
    private val vibrator: Vibrator? = application.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator

    init {
        val db = BethelDatabase.getDatabase(application, viewModelScope)
        repository = BethelRepository(application, db.bethelDao())
        NotificationHelper.createNotificationChannel(application)
        viewModelScope.launch {
            repository.ensureInitialDefaults()
        }
    }

    // --- Navigation ---
    private val _currentTabIndex = MutableStateFlow(0)
    val currentTabIndex: StateFlow<Int> = _currentTabIndex.asStateFlow()

    fun selectTab(index: Int) {
        _currentTabIndex.value = index
    }

    // --- Data Streams ---
    val reminders: StateFlow<List<PrayerReminderEntity>> = repository.allReminders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val journalEntries: StateFlow<List<PrayerJournalEntity>> = repository.allJournalEntries
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteVerses: StateFlow<List<FavoriteVerseEntity>> = repository.allFavoriteVerses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Bible State ---
    private val _selectedBibleVersion = MutableStateFlow("LSG") // "LSG" (Louis Segond 1910) or "DRB" (Darby)
    val selectedBibleVersion: StateFlow<String> = _selectedBibleVersion.asStateFlow()

    private val _selectedBook = MutableStateFlow(BibleData.ALL_BOOKS.first { it.id == "GEN" })
    val selectedBook: StateFlow<BibleBookInfo> = _selectedBook.asStateFlow()

    private val _selectedChapter = MutableStateFlow(1)
    val selectedChapter: StateFlow<Int> = _selectedChapter.asStateFlow()

    private val _chapterVerses = MutableStateFlow<List<BibleVerseEntity>>(emptyList())
    val chapterVerses: StateFlow<List<BibleVerseEntity>> = _chapterVerses.asStateFlow()

    val markedVerses: StateFlow<List<BibleVerseEntity>> = repository.getMarkedVersesFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val readingPlans: StateFlow<List<BibleReadingPlanEntity>> = repository.allReadingPlans
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeReadingPlan: StateFlow<BibleReadingPlanEntity?> = repository.activeReadingPlan
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // --- Spiritual Challenges State ---
    val allChallenges: StateFlow<List<SpiritualChallengeEntity>> = repository.allChallenges
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeChallenge: StateFlow<SpiritualChallengeEntity?> = repository.activeChallenge
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _selectedChallengeForDetail = MutableStateFlow<SpiritualChallengeEntity?>(null)
    val selectedChallengeForDetail: StateFlow<SpiritualChallengeEntity?> = _selectedChallengeForDetail.asStateFlow()

    private val _selectedChallengeDayForDetail = MutableStateFlow<ChallengeDayContent?>(null)
    val selectedChallengeDayForDetail: StateFlow<ChallengeDayContent?> = _selectedChallengeDayForDetail.asStateFlow()

    // --- Memorized Verses (Flashcards & Leitner) State ---
    val allMemorizedVerses: StateFlow<List<MemorizedVerseEntity>> = repository.allMemorizedVerses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dueMemorizedVerses: StateFlow<List<MemorizedVerseEntity>> = repository.dueMemorizedVerses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val masteredMemorizedVerses: StateFlow<List<MemorizedVerseEntity>> = repository.masteredMemorizedVerses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Spiritual Alarm State ---
    val spiritualAlarm: StateFlow<SpiritualAlarmEntity?> = repository.spiritualAlarm
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _showSpiritualWakeupModal = MutableStateFlow(false)
    val showSpiritualWakeupModal: StateFlow<Boolean> = _showSpiritualWakeupModal.asStateFlow()

    // --- DND Mode State ---
    private val _isDndActive = MutableStateFlow(false)
    val isDndActive: StateFlow<Boolean> = _isDndActive.asStateFlow()

    private val _hasDndPermission = MutableStateFlow(false)
    val hasDndPermission: StateFlow<Boolean> = _hasDndPermission.asStateFlow()

    // --- Situational Prayers State ---
    private val _showSituationalModal = MutableStateFlow(false)
    val showSituationalModal: StateFlow<Boolean> = _showSituationalModal.asStateFlow()
    val showSituationalPrayersSheet: StateFlow<Boolean> = _showSituationalModal.asStateFlow()

    private val _showChallengesSheet = MutableStateFlow(false)
    val showChallengesSheet: StateFlow<Boolean> = _showChallengesSheet.asStateFlow()

    private val _showFlashcardsSheet = MutableStateFlow(false)
    val showFlashcardsSheet: StateFlow<Boolean> = _showFlashcardsSheet.asStateFlow()

    private val _showCollectionsSheet = MutableStateFlow(false)
    val showCollectionsSheet: StateFlow<Boolean> = _showCollectionsSheet.asStateFlow()

    private val _selectedSituationalCategory = MutableStateFlow<String?>("healing")
    val selectedSituationalCategory: StateFlow<String?> = _selectedSituationalCategory.asStateFlow()

    private val _situationalSearchQuery = MutableStateFlow("")
    val situationalSearchQuery: StateFlow<String> = _situationalSearchQuery.asStateFlow()

    private val _bibleSearchResults = MutableStateFlow<List<BibleVerseEntity>>(emptyList())
    val bibleSearchResults: StateFlow<List<BibleVerseEntity>> = _bibleSearchResults.asStateFlow()

    private val _bibleSearchQuery = MutableStateFlow("")
    val bibleSearchQuery: StateFlow<String> = _bibleSearchQuery.asStateFlow()

    private val _isBibleSearching = MutableStateFlow(false)
    val isBibleSearching: StateFlow<Boolean> = _isBibleSearching.asStateFlow()

    // --- Bible Audio & TTS State ---
    val bibleTtsManager = BibleTtsManager(application, viewModelScope)
    val isBibleAudioPlaying: StateFlow<Boolean> = bibleTtsManager.isPlaying
    val isBibleAudioPaused: StateFlow<Boolean> = bibleTtsManager.isPaused
    val isBibleAudioPlayerVisible: StateFlow<Boolean> = bibleTtsManager.isPlayerVisible
    val currentSpeakingVerseIndex: StateFlow<Int> = bibleTtsManager.currentVerseIndex
    val currentSpeakingVerseNumber: StateFlow<Int> = bibleTtsManager.currentVerseNumber
    val bibleAudioSpeed: StateFlow<Float> = bibleTtsManager.speechRate

    // --- Gemini AI Paraphrase State ---
    private val _isVerseExplanationLoading = MutableStateFlow(false)
    val isVerseExplanationLoading: StateFlow<Boolean> = _isVerseExplanationLoading.asStateFlow()

    private val _verseExplanationResult = MutableStateFlow<String?>(null)
    val verseExplanationResult: StateFlow<String?> = _verseExplanationResult.asStateFlow()

    fun explainVerse(verse: BibleVerseEntity) {
        viewModelScope.launch {
            _isVerseExplanationLoading.value = true
            _verseExplanationResult.value = null
            try {
                val apiKey = com.example.BuildConfig.GEMINI_API_KEY
                if (apiKey.isEmpty() || apiKey == "YOUR_GEMINI_API_KEY_HERE") {
                    _verseExplanationResult.value = "Erreur: Clé API Gemini manquante. Veuillez configurer GEMINI_API_KEY."
                    _isVerseExplanationLoading.value = false
                    return@launch
                }

                val prompt = "Tu es un expert biblique et un accompagnateur spirituel bienveillant. Paraphrase et explique ce verset de la Bible de façon très simple, accessible et inspirante: ${verse.bookName} ${verse.chapter}:${verse.verse} - '${verse.text}'. Garde l'explication courte (2 à 3 paragraphes maximum). Utilise le français."
                val request = com.example.data.api.GenerateContentRequest(
                    contents = listOf(
                        com.example.data.api.Content(
                            parts = listOf(com.example.data.api.Part(text = prompt))
                        )
                    )
                )

                val response = com.example.data.api.RetrofitClient.service.generateContent(apiKey, request)
                val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                _verseExplanationResult.value = text ?: "Désolé, aucune explication n'a pu être générée."
            } catch (e: Exception) {
                _verseExplanationResult.value = "Erreur lors de la connexion à l'IA: ${e.localizedMessage}"
            } finally {
                _isVerseExplanationLoading.value = false
            }
        }
    }

    fun clearVerseExplanation() {
        _verseExplanationResult.value = null
    }

    // --- AI Bible Quiz State ---
    private val _isQuizSheetVisible = MutableStateFlow(false)
    val isQuizSheetVisible: StateFlow<Boolean> = _isQuizSheetVisible.asStateFlow()

    private val _isQuizLoading = MutableStateFlow(false)
    val isQuizLoading: StateFlow<Boolean> = _isQuizLoading.asStateFlow()

    private val _quizQuestions = MutableStateFlow<List<com.example.data.model.QuizQuestion>>(emptyList())
    val quizQuestions: StateFlow<List<com.example.data.model.QuizQuestion>> = _quizQuestions.asStateFlow()

    private val _currentQuizQuestionIndex = MutableStateFlow(0)
    val currentQuizQuestionIndex: StateFlow<Int> = _currentQuizQuestionIndex.asStateFlow()

    private val _selectedQuizAnswerIndex = MutableStateFlow<Int?>(null)
    val selectedQuizAnswerIndex: StateFlow<Int?> = _selectedQuizAnswerIndex.asStateFlow()

    private val _quizScore = MutableStateFlow(0)
    val quizScore: StateFlow<Int> = _quizScore.asStateFlow()

    private val _isQuizFinished = MutableStateFlow(false)
    val isQuizFinished: StateFlow<Boolean> = _isQuizFinished.asStateFlow()

    private val _quizError = MutableStateFlow<String?>(null)
    val quizError: StateFlow<String?> = _quizError.asStateFlow()

    fun openQuizSheet() {
        _isQuizSheetVisible.value = true
        if (_quizQuestions.value.isEmpty() && !_isQuizLoading.value) {
            resetQuiz()
        }
    }

    fun closeQuizSheet() {
        _isQuizSheetVisible.value = false
    }

    fun resetQuiz() {
        _quizQuestions.value = emptyList()
        _currentQuizQuestionIndex.value = 0
        _selectedQuizAnswerIndex.value = null
        _quizScore.value = 0
        _isQuizFinished.value = false
        _quizError.value = null
    }

    fun startQuiz(topic: String = "Général") {
        viewModelScope.launch {
            resetQuiz()
            _isQuizLoading.value = true
            
            try {
                val apiKey = com.example.BuildConfig.GEMINI_API_KEY
                if (apiKey.isEmpty() || apiKey == "YOUR_GEMINI_API_KEY_HERE") {
                    _quizError.value = "Erreur: Clé API Gemini manquante. Veuillez configurer GEMINI_API_KEY."
                    _isQuizLoading.value = false
                    return@launch
                }

                val prompt = """
                    Tu es un expert biblique. Génère un quiz biblique interactif de 5 questions sur le thème suivant : "$topic".
                    Réponds EXCLUSIVEMENT avec un objet JSON structuré comme ceci (sans formatage markdown, sans commentaires, uniquement le JSON brut) :
                    {
                      "questions": [
                        {
                          "question": "Texte de la question ?",
                          "options": ["A", "B", "C", "D"],
                          "correctAnswerIndex": 2,
                          "explanation": "Brève explication avec la référence biblique."
                        }
                      ]
                    }
                """.trimIndent()
                
                val request = com.example.data.api.GenerateContentRequest(
                    contents = listOf(
                        com.example.data.api.Content(
                            parts = listOf(com.example.data.api.Part(text = prompt))
                        )
                    )
                )

                val response = com.example.data.api.RetrofitClient.service.generateContent(apiKey, request)
                val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
                
                // Clean the text in case it's wrapped in markdown
                val cleanJson = text.replace(Regex("```json\n?|```"), "").trim()
                
                val moshi = com.squareup.moshi.Moshi.Builder()
                    .add(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory())
                    .build()
                val adapter = moshi.adapter(com.example.data.model.QuizResponse::class.java)
                
                val quizResponse = adapter.fromJson(cleanJson)
                if (quizResponse?.questions.isNullOrEmpty()) {
                    _quizError.value = "Impossible de générer le quiz. Veuillez réessayer."
                } else {
                    _quizQuestions.value = quizResponse!!.questions
                }
            } catch (e: Exception) {
                _quizError.value = "Erreur lors de la connexion à l'IA: ${e.localizedMessage}"
            } finally {
                _isQuizLoading.value = false
            }
        }
    }

    fun submitQuizAnswer(index: Int) {
        if (_selectedQuizAnswerIndex.value != null || _isQuizFinished.value) return
        
        _selectedQuizAnswerIndex.value = index
        val currentQuestion = _quizQuestions.value.getOrNull(_currentQuizQuestionIndex.value)
        if (currentQuestion != null && index == currentQuestion.correctAnswerIndex) {
            _quizScore.value += 1
        }
    }

    fun nextQuizQuestion() {
        if (_currentQuizQuestionIndex.value < _quizQuestions.value.size - 1) {
            _currentQuizQuestionIndex.value += 1
            _selectedQuizAnswerIndex.value = null
        } else {
            _isQuizFinished.value = true
        }
    }

    init {
        loadCurrentChapter()
    }

    private fun loadCurrentChapter() {
        viewModelScope.launch {
            val v = _selectedBibleVersion.value
            val b = _selectedBook.value.id
            val c = _selectedChapter.value
            repository.ensureChapterLoaded(v, b, c)
            repository.getVersesForChapterFlow(v, b, c).collect { list ->
                val finalVerses = if (list.isNotEmpty()) {
                    list
                } else {
                    com.example.data.local.BibleTextProvider.getVersesForChapter(v, b, c)
                }
                _chapterVerses.value = finalVerses
                bibleTtsManager.onChapterChanged(finalVerses, _selectedBook.value.name, _selectedChapter.value)
            }
        }
    }

    // --- Bible Audio / TTS Controls ---
    fun startBibleAudio(startIndex: Int = 0) {
        val verses = _chapterVerses.value
        if (verses.isNotEmpty()) {
            bibleTtsManager.startPlaying(
                verses = verses,
                startIndex = startIndex,
                bookName = _selectedBook.value.name,
                chapter = _selectedChapter.value
            )
        }
    }

    fun toggleBibleAudioPlayPause() {
        if (isBibleAudioPlaying.value) {
            bibleTtsManager.pause()
        } else if (isBibleAudioPaused.value) {
            bibleTtsManager.resume()
        } else {
            startBibleAudio(0)
        }
    }

    fun stopBibleAudio() {
        bibleTtsManager.stop()
    }

    fun closeBibleAudioPlayer() {
        bibleTtsManager.closePlayer()
    }

    fun openBibleAudioPlayer() {
        bibleTtsManager.showPlayer()
    }

    fun nextBibleAudioVerse() {
        bibleTtsManager.nextVerse()
    }

    fun previousBibleAudioVerse() {
        bibleTtsManager.previousVerse()
    }

    fun seekBibleAudioToVerse(index: Int) {
        bibleTtsManager.seekToVerseIndex(index)
    }

    fun seekBibleAudioVerse(index: Int) {
        seekBibleAudioToVerse(index)
    }

    fun setBibleAudioSpeed(rate: Float) {
        bibleTtsManager.setSpeed(rate)
    }

    fun setBibleVersion(version: String) {
        _selectedBibleVersion.value = version
        loadCurrentChapter()
    }

    fun selectBibleBookAndChapter(bookId: String, chapter: Int) {
        val book = BibleData.getBookById(bookId)
        _selectedBook.value = book
        _selectedChapter.value = chapter.coerceIn(1, book.chaptersCount)
        loadCurrentChapter()
    }

    fun nextBibleChapter() {
        val book = _selectedBook.value
        val currentChap = _selectedChapter.value
        if (currentChap < book.chaptersCount) {
            _selectedChapter.value = currentChap + 1
            loadCurrentChapter()
        } else {
            // Next book
            val currentIndex = BibleData.ALL_BOOKS.indexOfFirst { it.id == book.id }
            if (currentIndex < BibleData.ALL_BOOKS.size - 1) {
                val nextBook = BibleData.ALL_BOOKS[currentIndex + 1]
                _selectedBook.value = nextBook
                _selectedChapter.value = 1
                loadCurrentChapter()
            }
        }
    }

    fun previousBibleChapter() {
        val book = _selectedBook.value
        val currentChap = _selectedChapter.value
        if (currentChap > 1) {
            _selectedChapter.value = currentChap - 1
            loadCurrentChapter()
        } else {
            // Previous book
            val currentIndex = BibleData.ALL_BOOKS.indexOfFirst { it.id == book.id }
            if (currentIndex > 0) {
                val prevBook = BibleData.ALL_BOOKS[currentIndex - 1]
                _selectedBook.value = prevBook
                _selectedChapter.value = prevBook.chaptersCount
                loadCurrentChapter()
            }
        }
    }

    fun toggleVerseHighlight(verse: BibleVerseEntity, color: String?) {
        viewModelScope.launch {
            repository.toggleVerseHighlight(verse, color)
            loadCurrentChapter()
        }
    }

    fun toggleVerseBookmark(verse: BibleVerseEntity) {
        viewModelScope.launch {
            repository.toggleVerseBookmark(verse)
            loadCurrentChapter()
        }
    }

    fun saveVerseNote(verse: BibleVerseEntity, note: String?) {
        viewModelScope.launch {
            repository.saveVerseNote(verse, note)
            loadCurrentChapter()
            _syncMessage.value = "Note personnelle enregistrée sur ${verse.bookName} ${verse.chapter}:${verse.verse}"
        }
    }

    fun searchBible(query: String) {
        _bibleSearchQuery.value = query
        if (query.isBlank()) {
            _bibleSearchResults.value = emptyList()
            _isBibleSearching.value = false
            return
        }
        _isBibleSearching.value = true
        viewModelScope.launch {
            val results = repository.searchBible(_selectedBibleVersion.value, query)
            _bibleSearchResults.value = results
            _isBibleSearching.value = false
        }
    }

    fun clearBibleSearch() {
        _bibleSearchQuery.value = ""
        _bibleSearchResults.value = emptyList()
        _isBibleSearching.value = false
    }

    fun setActiveReadingPlan(planId: String) {
        viewModelScope.launch {
            repository.setActiveReadingPlan(planId)
            _syncMessage.value = "Plan de lecture biblique activé !"
        }
    }

    fun toggleReadingPlanDay(plan: BibleReadingPlanEntity, day: Int) {
        viewModelScope.launch {
            repository.toggleReadingPlanDay(plan, day)
        }
    }

    fun navigateToBiblePassage(bookName: String, chapter: Int) {
        val book = BibleData.getBookByName(bookName) ?: BibleData.ALL_BOOKS.first()
        _selectedBook.value = book
        _selectedChapter.value = chapter.coerceIn(1, book.chaptersCount)
        loadCurrentChapter()
        _currentTabIndex.value = 1 // Switch to Bible tab
    }

    fun startMeditationFromVerse(verse: BibleVerseEntity) {
        val meditation = MeditationItem(
            id = "med_verse_${verse.id}",
            title = "Méditation : ${verse.bookName} ${verse.chapter}:${verse.verse}",
            theme = com.example.data.model.MeditationTheme.PEACE,
            scriptureReference = "${verse.bookName} ${verse.chapter}:${verse.verse}",
            scriptureText = verse.text,
            reflection = "Méditez ce verset dans le silence de votre cœur. Laissez cette parole pénétrer votre esprit, apporter la paix, fortifier votre foi et vous rapprocher du Seigneur.",
            guidedPrayer = "Seigneur, grave cette Parole vivante au plus profond de mon être. Que Ta volonté s'accomplisse dans ma vie, au nom de Jésus. Amen.",
            estimatedDurationMinutes = 7,
            recommendedTime = "Méditation biblique"
        )
        startMeditation(meditation)
    }

    val userProfile: StateFlow<UserProfileEntity> = repository.userProfile
        .combine(MutableStateFlow(Unit)) { profile, _ ->
            profile ?: UserProfileEntity()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserProfileEntity())

    val sessions: StateFlow<List<PrayerSessionEntity>> = repository.allSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Daily Verse ---
    private val _todayVerse = MutableStateFlow(DailyVerseRepository.getTodayVerse())
    val todayVerse: StateFlow<DailyVerse> = _todayVerse.asStateFlow()

    // --- Meditation Filter ---
    private val _selectedThemeFilter = MutableStateFlow(MeditationTheme.ALL)
    val selectedThemeFilter: StateFlow<MeditationTheme> = _selectedThemeFilter.asStateFlow()

    fun setThemeFilter(theme: MeditationTheme) {
        _selectedThemeFilter.value = theme
    }

    // --- Active Meditation / Prayer Timer ---
    private val _showTimerSheet = MutableStateFlow(false)
    val showTimerSheet: StateFlow<Boolean> = _showTimerSheet.asStateFlow()

    private val _activeMeditation = MutableStateFlow<MeditationItem?>(null)
    val activeMeditation: StateFlow<MeditationItem?> = _activeMeditation.asStateFlow()

    private val _timerTotalSeconds = MutableStateFlow(0) // Default 0
    val timerTotalSeconds: StateFlow<Int> = _timerTotalSeconds.asStateFlow()

    private val _timerRemainingSeconds = MutableStateFlow(0)
    val timerRemainingSeconds: StateFlow<Int> = _timerRemainingSeconds.asStateFlow()

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()

    private val _activeSoundscape = MutableStateFlow("HARPE")
    val activeSoundscape: StateFlow<String> = _activeSoundscape.asStateFlow()

    private val _sessionCompletedDialog = MutableStateFlow(false)
    val sessionCompletedDialog: StateFlow<Boolean> = _sessionCompletedDialog.asStateFlow()

    private var timerJob: Job? = null
    private var targetEndTimeRealtime: Long = 0L

    fun startMeditation(item: MeditationItem?, customDurationMinutes: Int = 5) {
        _activeMeditation.value = item
        val totalSecs = (item?.estimatedDurationMinutes ?: customDurationMinutes) * 60
        _timerTotalSeconds.value = totalSecs
        _timerRemainingSeconds.value = totalSecs
        _isTimerRunning.value = true
        _showTimerSheet.value = true
        targetEndTimeRealtime = android.os.SystemClock.elapsedRealtime() + (totalSecs * 1000L)

        // Auto DND integration
        val app = getApplication<Application>()
        val profile = userProfile.value
        if (profile?.isAutoDndEnabled == true && DndHelper.hasDndPermission(app)) {
            val activated = DndHelper.activateDnd(app)
            _isDndActive.value = activated
        }

        AudioSynthHelper.startAmbient(viewModelScope, _activeSoundscape.value)
        startTimerLoop()
    }

    fun openFreePrayerTimer(durationMinutes: Int = 10) {
        startMeditation(null, durationMinutes)
    }

    fun toggleTimerPause() {
        if (_isTimerRunning.value) {
            val remainingMs = (targetEndTimeRealtime - android.os.SystemClock.elapsedRealtime()).coerceAtLeast(0L)
            _timerRemainingSeconds.value = ((remainingMs + 999) / 1000).toInt()
            _isTimerRunning.value = false
            timerJob?.cancel()
            AudioSynthHelper.stopAmbient()
        } else {
            if (_timerRemainingSeconds.value <= 0) {
                _timerRemainingSeconds.value = _timerTotalSeconds.value
            }
            targetEndTimeRealtime = android.os.SystemClock.elapsedRealtime() + (_timerRemainingSeconds.value * 1000L)
            _isTimerRunning.value = true
            AudioSynthHelper.startAmbient(viewModelScope, _activeSoundscape.value)
            startTimerLoop()
        }
    }

    fun setSoundscape(soundType: String) {
        _activeSoundscape.value = soundType
        if (_isTimerRunning.value) {
            AudioSynthHelper.startAmbient(viewModelScope, soundType)
        }
    }

    fun addTimerMinutes(minutes: Int) {
        val addition = minutes * 60
        _timerTotalSeconds.value += addition
        _timerRemainingSeconds.value += addition
        if (_isTimerRunning.value) {
            targetEndTimeRealtime += (addition * 1000L)
        }
    }

    private fun startTimerLoop() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive && _isTimerRunning.value) {
                val remainingMs = targetEndTimeRealtime - android.os.SystemClock.elapsedRealtime()
                val remainingSecs = ((remainingMs + 999) / 1000).coerceAtLeast(0L).toInt()
                _timerRemainingSeconds.value = remainingSecs
                if (remainingSecs <= 0) {
                    completeSession()
                    break
                }
                delay(500)
            }
        }
    }

    fun completeSession() {
        timerJob?.cancel()
        _isTimerRunning.value = false
        AudioSynthHelper.stopAmbient()
        AudioSynthHelper.playSanctuaryBell(viewModelScope)
        AudioSynthHelper.triggerGentleHaptic(vibrator)

        // Restore DND mode
        val app = getApplication<Application>()
        if (_isDndActive.value) {
            DndHelper.restoreDnd(app)
            _isDndActive.value = false
        }

        val durationSpent = _timerTotalSeconds.value - _timerRemainingSeconds.value
        val effectiveDuration = if (durationSpent <= 10) _timerTotalSeconds.value else durationSpent

        viewModelScope.launch {
            repository.recordPrayerSession(
                title = _activeMeditation.value?.title ?: "Temps de Prière Libre",
                durationSeconds = effectiveDuration,
                meditationId = _activeMeditation.value?.id,
                notes = _activeMeditation.value?.scriptureReference
            )
        }
        _timerRemainingSeconds.value = 0
        _timerTotalSeconds.value = 0
        _sessionCompletedDialog.value = true
    }

    fun minimizeTimerSheet() {
        _showTimerSheet.value = false
    }

    fun reopenTimerSheet() {
        _showTimerSheet.value = true
    }

    fun cancelSession() {
        timerJob?.cancel()
        _isTimerRunning.value = false
        AudioSynthHelper.stopAmbient()
        
        // Restore DND if active
        val app = getApplication<Application>()
        if (_isDndActive.value) {
            DndHelper.restoreDnd(app)
            _isDndActive.value = false
        }

        _timerRemainingSeconds.value = 0
        _timerTotalSeconds.value = 0
        _showTimerSheet.value = false
        _sessionCompletedDialog.value = false
        _activeMeditation.value = null
    }

    fun closeTimerSheet() {
        if (_isTimerRunning.value) {
            minimizeTimerSheet()
        } else {
            cancelSession()
        }
    }

    // --- DND Permissions & Helpers ---
    fun checkDndPermission(context: Context) {
        _hasDndPermission.value = DndHelper.hasDndPermission(context)
    }

    fun toggleAutoDnd(isEnabled: Boolean) {
        val current = userProfile.value ?: UserProfileEntity()
        updateProfile(current.copy(isAutoDndEnabled = isEnabled))
    }

    fun setAutoDndEnabled(isEnabled: Boolean) {
        toggleAutoDnd(isEnabled)
    }

    // --- Day/Night Theme Management ---
    fun setThemeMode(mode: String) {
        val current = userProfile.value ?: UserProfileEntity()
        updateProfile(current.copy(themeMode = mode))
    }

    fun toggleQuickTheme() {
        val current = userProfile.value ?: UserProfileEntity()
        val nextMode = when (current.themeMode) {
            "SYSTEM" -> "DARK"
            "DARK" -> "LIGHT"
            else -> "SYSTEM"
        }
        setThemeMode(nextMode)
    }

    // --- Situational Prayers Actions ---
    fun toggleSituationalModal(show: Boolean) {
        _showSituationalModal.value = show
    }

    fun openSituationalPrayersSheet() {
        _showSituationalModal.value = true
    }

    fun closeSituationalPrayersSheet() {
        _showSituationalModal.value = false
    }

    fun openChallengesSheet() {
        _showChallengesSheet.value = true
    }

    fun closeChallengesSheet() {
        _showChallengesSheet.value = false
    }

    fun openFlashcardsSheet() {
        _showFlashcardsSheet.value = true
    }

    fun closeFlashcardsSheet() {
        _showFlashcardsSheet.value = false
    }

    fun openCollectionsSheet() {
        _showCollectionsSheet.value = true
    }

    fun closeCollectionsSheet() {
        _showCollectionsSheet.value = false
    }

    fun openSpiritualWakeup() {
        triggerSpiritualWakeupPreview()
    }

    fun testSpiritualAlarm() {
        triggerSpiritualWakeupPreview()
    }

    fun selectSituationalCategory(category: String?) {
        _selectedSituationalCategory.value = category
    }

    fun setSituationalSearchQuery(query: String) {
        _situationalSearchQuery.value = query
    }

    fun startPrayerFromSituation(prayerItem: SituationalPrayerItem) {
        _showSituationalModal.value = false
        val meditationItem = MeditationItem(
            id = "situation_${prayerItem.id}",
            theme = MeditationTheme.COURAGE,
            title = prayerItem.title,
            scriptureReference = prayerItem.scriptureRef,
            scriptureText = prayerItem.scriptureText,
            reflection = "${prayerItem.subtitle}\n\nDéclaration de Foi : ${prayerItem.declaration}",
            guidedPrayer = prayerItem.prayerText,
            estimatedDurationMinutes = prayerItem.recommendedDurationMinutes
        )
        startMeditation(meditationItem, prayerItem.recommendedDurationMinutes)
    }

    fun addSituationalPrayerToJournal(prayerItem: SituationalPrayerItem) {
        viewModelScope.launch {
            val entry = PrayerJournalEntity(
                title = "Prière pour : ${prayerItem.title}",
                content = "« ${prayerItem.scriptureText} » (${prayerItem.scriptureRef})\n\n${prayerItem.prayerText}\n\nDéclaration : ${prayerItem.declaration}",
                category = "Prière",
                createdAt = System.currentTimeMillis()
            )
            repository.saveJournalEntry(entry)
            _syncMessage.value = "Prière ajoutée à votre journal personnel !"
        }
    }

    // --- Spiritual Challenges Actions ---
    fun setActiveChallenge(challengeId: String) {
        viewModelScope.launch {
            repository.setActiveChallenge(challengeId)
            _syncMessage.value = "Défi spirituel activé !"
        }
    }

    fun toggleChallengeDay(challenge: SpiritualChallengeEntity, dayNumber: Int) {
        viewModelScope.launch {
            repository.toggleChallengeDay(challenge, dayNumber)
            AudioSynthHelper.triggerGentleHaptic(vibrator)
        }
    }

    fun toggleChallengeReminder(challenge: SpiritualChallengeEntity, isEnabled: Boolean) {
        viewModelScope.launch {
            repository.toggleChallengeReminder(challenge, isEnabled)
        }
    }

    fun openChallengeDetail(challenge: SpiritualChallengeEntity?) {
        _selectedChallengeForDetail.value = challenge
    }

    fun openChallengeDayDetail(content: ChallengeDayContent?) {
        _selectedChallengeDayForDetail.value = content
    }

    // --- Flashcards / Memorized Verses Actions ---
    fun addVerseToMemory(
        reference: String,
        text: String,
        version: String = "LSG",
        category: String = "Général",
        bookId: String = "",
        chapter: Int = 1,
        verse: Int = 1
    ) {
        viewModelScope.launch {
            repository.addVerseToMemory(reference, text, version, category, bookId, chapter, verse)
            _syncMessage.value = "Verset « $reference » ajouté aux cartes de mémorisation !"
        }
    }

    fun recordFlashcardReview(verse: MemorizedVerseEntity, isKnown: Boolean) {
        viewModelScope.launch {
            repository.recordFlashcardReview(verse, isKnown)
            AudioSynthHelper.triggerGentleHaptic(vibrator)
        }
    }

    fun deleteMemorizedVerse(verse: MemorizedVerseEntity) {
        viewModelScope.launch {
            repository.deleteMemorizedVerse(verse)
            _syncMessage.value = "Verset retiré des cartes de mémorisation."
        }
    }

    // --- Spiritual Alarm Actions ---
    fun saveSpiritualAlarm(alarm: SpiritualAlarmEntity) {
        viewModelScope.launch {
            repository.saveSpiritualAlarm(alarm)
            if (alarm.isEnabled) {
                _syncMessage.value = "Réveil Spirituel programmé pour %02d:%02d".format(alarm.hour, alarm.minute)
            } else {
                _syncMessage.value = "Réveil Spirituel désactivé."
            }
        }
    }

    fun toggleSpiritualAlarm(alarm: SpiritualAlarmEntity, isEnabled: Boolean) {
        viewModelScope.launch {
            repository.toggleSpiritualAlarm(alarm, isEnabled)
            if (isEnabled) {
                _syncMessage.value = "Réveil Spirituel activé pour %02d:%02d".format(alarm.hour, alarm.minute)
            } else {
                _syncMessage.value = "Réveil Spirituel désactivé."
            }
        }
    }

    fun triggerSpiritualWakeupPreview() {
        AudioSynthHelper.startAmbient(viewModelScope, "HARPE")
        _showSpiritualWakeupModal.value = true
    }

    fun dismissSpiritualWakeup() {
        AudioSynthHelper.stopAmbient()
        _showSpiritualWakeupModal.value = false
    }

    fun dismissCompletedDialog() {
        _sessionCompletedDialog.value = false
        _showTimerSheet.value = false
        _activeMeditation.value = null
        _timerRemainingSeconds.value = 0
        _timerTotalSeconds.value = 0
    }

    // --- Spiritual Stats Calculation ---
    val spiritualStats: StateFlow<SpiritualStats> = sessions.combine(userProfile) { list, profile ->
        calculateStats(list, profile.dailyGoalMinutes)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SpiritualStats())

    private fun calculateStats(sessions: List<PrayerSessionEntity>, dailyGoalMinutes: Int): SpiritualStats {
        if (sessions.isEmpty()) {
            return SpiritualStats(
                weeklyHeatmap = generateDefaultWeeklyHeatmap(),
                unlockedBadges = getBadges(0, 0, 0)
            )
        }

        val totalSecs = sessions.sumOf { it.durationSeconds }
        val totalMinutes = totalSecs / 60
        val totalSessions = sessions.size

        // Calculate consecutive streak
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val prayedDays = sessions.map { sdf.format(Date(it.dateMillis)) }.toSet()

        val calendar = Calendar.getInstance()
        val todayStr = sdf.format(calendar.time)
        val hasPrayedToday = prayedDays.contains(todayStr)

        var streak = 0
        val checkCal = Calendar.getInstance()
        if (!hasPrayedToday) {
            checkCal.add(Calendar.DAY_OF_YEAR, -1)
        }

        while (true) {
            val dateStr = sdf.format(checkCal.time)
            if (prayedDays.contains(dateStr)) {
                streak++
                checkCal.add(Calendar.DAY_OF_YEAR, -1)
            } else {
                break
            }
        }

        // Weekly Heatmap (Last 7 days)
        val heatmap = mutableListOf<DayActivity>()
        val dayFormatter = SimpleDateFormat("EEE", Locale.FRENCH)
        val heatCal = Calendar.getInstance()
        val todayDayOfYear = heatCal.get(Calendar.DAY_OF_YEAR)

        for (i in 6 downTo 0) {
            val c = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -i) }
            val cDayStr = sdf.format(c.time)
            val daySessions = sessions.filter { sdf.format(Date(it.dateMillis)) == cDayStr }
            heatmap.add(
                DayActivity(
                    dayName = dayFormatter.format(c.time).take(3).replaceFirstChar { it.uppercase() },
                    dayOfMonth = c.get(Calendar.DAY_OF_MONTH),
                    isToday = c.get(Calendar.DAY_OF_YEAR) == todayDayOfYear,
                    sessionCount = daySessions.size,
                    totalMinutes = daySessions.sumOf { it.durationSeconds } / 60
                )
            )
        }

        return SpiritualStats(
            currentStreak = streak,
            totalMinutes = totalMinutes,
            totalSessions = totalSessions,
            hasPrayedToday = hasPrayedToday,
            weeklyHeatmap = heatmap,
            unlockedBadges = getBadges(streak, totalMinutes, totalSessions)
        )
    }

    private fun generateDefaultWeeklyHeatmap(): List<DayActivity> {
        val heatmap = mutableListOf<DayActivity>()
        val dayFormatter = SimpleDateFormat("EEE", Locale.FRENCH)
        val todayCal = Calendar.getInstance()

        for (i in 6 downTo 0) {
            val c = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -i) }
            heatmap.add(
                DayActivity(
                    dayName = dayFormatter.format(c.time).take(3).replaceFirstChar { it.uppercase() },
                    dayOfMonth = c.get(Calendar.DAY_OF_MONTH),
                    isToday = i == 0,
                    sessionCount = 0,
                    totalMinutes = 0
                )
            )
        }
        return heatmap
    }

    private fun getBadges(streak: Int, totalMinutes: Int, totalSessions: Int): List<SpiritualBadge> {
        return listOf(
            SpiritualBadge(
                id = "badge_first_altar",
                title = "Premier Autel",
                subtitle = "Première prière à Béthel",
                icon = "TempleBuddhist",
                isUnlocked = totalSessions >= 1
            ),
            SpiritualBadge(
                id = "badge_3_streak",
                title = "Cœur Constant",
                subtitle = "3 jours consécutifs de prière",
                icon = "LocalFireDepartment",
                isUnlocked = streak >= 3
            ),
            SpiritualBadge(
                id = "badge_7_streak",
                title = "Veilleur de Béthel",
                subtitle = "7 jours d'affilée dans Sa présence",
                icon = "MilitaryTech",
                isUnlocked = streak >= 7
            ),
            SpiritualBadge(
                id = "badge_hour",
                title = "Heure Sacrée",
                subtitle = "Plus de 60 minutes de recueillement",
                icon = "HourglassFull",
                isUnlocked = totalMinutes >= 60
            ),
            SpiritualBadge(
                id = "badge_master",
                title = "Porte des Cieux",
                subtitle = "10 sessions de méditation accomplies",
                icon = "AutoAwesome",
                isUnlocked = totalSessions >= 10
            )
        )
    }

    // --- Reminders Management ---
    fun toggleReminder(reminder: PrayerReminderEntity, isEnabled: Boolean) {
        viewModelScope.launch {
            repository.toggleReminder(reminder, isEnabled)
        }
    }

    fun saveReminder(reminder: PrayerReminderEntity) {
        viewModelScope.launch {
            repository.saveReminder(reminder)
        }
    }

    fun deleteReminder(reminder: PrayerReminderEntity) {
        viewModelScope.launch {
            repository.deleteReminder(reminder)
        }
    }

    fun restoreCanonicalReminders() {
        viewModelScope.launch {
            repository.restoreCanonicalReminders()
            _syncMessage.value = "Les 4 Heures Saintes ont été restaurées et programmées."
        }
    }

    fun sendTestNotification(context: Context, reminder: PrayerReminderEntity) {
        NotificationHelper.showInstantPrayerNotification(
            context = context,
            notificationId = reminder.id,
            title = reminder.title,
            message = "« C'est ici la maison de Dieu, c'est ici la porte des cieux. » Entrez dans le silence et la prière."
        )
    }

    // --- Journal Management ---
    fun saveJournalEntry(entry: PrayerJournalEntity) {
        viewModelScope.launch {
            repository.saveJournalEntry(entry)
        }
    }

    fun markJournalAnswered(entry: PrayerJournalEntity, testimony: String) {
        viewModelScope.launch {
            repository.markJournalAnswered(entry, testimony)
        }
    }

    fun deleteJournalEntry(entry: PrayerJournalEntity) {
        viewModelScope.launch {
            repository.deleteJournalEntry(entry)
        }
    }

    // --- Favorites ---
    fun addVerseToCollection(verse: BibleVerseEntity, collectionName: String) {
        viewModelScope.launch {
            repository.toggleFavoriteVerse(
                verseId = verse.id,
                reference = "${verse.bookName} ${verse.chapter}:${verse.verse}",
                text = verse.text,
                theme = collectionName,
                reflection = "",
                isFav = false // since we want to insert, isFav=false causes it to insert
            )
            // also bookmark the verse so it shows the icon
            if (!verse.isBookmarked) {
                repository.toggleVerseBookmark(verse)
                loadCurrentChapter()
            }
            _syncMessage.value = "Verset ajouté à la collection '$collectionName'"
        }
    }

    fun removeFavoriteVerse(verseId: String) {
        viewModelScope.launch {
            repository.toggleFavoriteVerse(verseId, "", "", "", "", true)
        }
    }

    fun toggleFavorite(verse: DailyVerse, isFav: Boolean) {
        viewModelScope.launch {
            repository.toggleFavoriteVerse(
                verseId = verse.id,
                reference = verse.reference,
                text = verse.text,
                theme = "Verset du Jour",
                reflection = verse.reflection,
                isFav = isFav
            )
        }
    }

    // --- Profile & Google Cloud Synchronization ---
    private val _syncMessage = MutableStateFlow<String?>(null)
    val syncMessage: StateFlow<String?> = _syncMessage.asStateFlow()

    fun clearSyncMessage() {
        _syncMessage.value = null
    }

    fun updateProfile(profile: UserProfileEntity) {
        viewModelScope.launch {
            repository.updateProfile(profile)
        }
    }

    fun connectGoogleAccount(name: String, email: String, photoUrl: String = "") {
        viewModelScope.launch {
            repository.connectGoogleAccount(name, email, photoUrl)
            _syncMessage.value = "Compte Google synchronisé avec succès ($email) !"
        }
    }

    fun disconnectGoogleAccount() {
        viewModelScope.launch {
            repository.disconnectGoogleAccount()
            _syncMessage.value = "Compte Google déconnecté."
        }
    }

    fun triggerCloudSync() {
        viewModelScope.launch {
            val ts = repository.recordBackupTimestamp()
            val sdf = SimpleDateFormat("HH:mm", Locale.FRENCH)
            _syncMessage.value = "Synchronisation réussie avec Google (${sdf.format(Date(ts))}) !"
        }
    }

    fun triggerCloudBackup() {
        triggerCloudSync()
    }

    override fun onCleared() {
        super.onCleared()
        bibleTtsManager.release()
        AudioSynthHelper.stopAmbient()
    }
}
