package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.LocalLibrary
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.BethelBottomNavBar
import com.example.ui.components.BethelTopBar
import com.example.ui.screens.ActiveMeditationSheet
import com.example.ui.screens.BibleScreen
import com.example.ui.screens.ChallengesBottomSheet
import com.example.ui.screens.FlashcardsBottomSheet
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.JournalProgressScreen
import com.example.ui.screens.MeditationScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.RemindersScreen
import com.example.ui.screens.SituationalPrayersBottomSheet
import com.example.ui.screens.SpiritualWakeupFullScreenDialog
import com.example.ui.theme.BethelGold
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.BethelViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: BethelViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
            val isSystemDark = isSystemInDarkTheme()
            val isDarkTheme = when (userProfile.themeMode) {
                "DARK" -> true
                "LIGHT" -> false
                else -> isSystemDark
            }

            MyApplicationTheme(darkTheme = isDarkTheme) {
                // Request Notification Permission on Android 13+
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val permissionLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestPermission()
                    ) { _ -> }

                    LaunchedEffect(Unit) {
                        val permission = Manifest.permission.POST_NOTIFICATIONS
                        if (ContextCompat.checkSelfPermission(this@MainActivity, permission) != PackageManager.PERMISSION_GRANTED) {
                            permissionLauncher.launch(permission)
                        }
                    }
                }

                BethelApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BethelApp(viewModel: BethelViewModel) {
    val currentTab by viewModel.currentTabIndex.collectAsStateWithLifecycle()
    val reminders by viewModel.reminders.collectAsStateWithLifecycle()
    val journalEntries by viewModel.journalEntries.collectAsStateWithLifecycle()
    val favoriteVerses by viewModel.favoriteVerses.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val todayVerse by viewModel.todayVerse.collectAsStateWithLifecycle()
    val selectedThemeFilter by viewModel.selectedThemeFilter.collectAsStateWithLifecycle()
    val spiritualStats by viewModel.spiritualStats.collectAsStateWithLifecycle()
    val syncMessage by viewModel.syncMessage.collectAsStateWithLifecycle()

    // Bible states
    val selectedBibleVersion by viewModel.selectedBibleVersion.collectAsStateWithLifecycle()
    val selectedBook by viewModel.selectedBook.collectAsStateWithLifecycle()
    val selectedChapter by viewModel.selectedChapter.collectAsStateWithLifecycle()
    val chapterVerses by viewModel.chapterVerses.collectAsStateWithLifecycle()
    val markedVerses by viewModel.markedVerses.collectAsStateWithLifecycle()
    val readingPlans by viewModel.readingPlans.collectAsStateWithLifecycle()
    val activeReadingPlan by viewModel.activeReadingPlan.collectAsStateWithLifecycle()
    val bibleSearchResults by viewModel.bibleSearchResults.collectAsStateWithLifecycle()
    val bibleSearchQuery by viewModel.bibleSearchQuery.collectAsStateWithLifecycle()
    val isBibleSearching by viewModel.isBibleSearching.collectAsStateWithLifecycle()
    val isBibleAudioPlaying by viewModel.isBibleAudioPlaying.collectAsStateWithLifecycle()
    val isBibleAudioPaused by viewModel.isBibleAudioPaused.collectAsStateWithLifecycle()
    val isBibleAudioPlayerVisible by viewModel.isBibleAudioPlayerVisible.collectAsStateWithLifecycle()
    val currentSpeakingVerseIndex by viewModel.currentSpeakingVerseIndex.collectAsStateWithLifecycle()
    val currentSpeakingVerseNumber by viewModel.currentSpeakingVerseNumber.collectAsStateWithLifecycle()
    val bibleAudioSpeed by viewModel.bibleAudioSpeed.collectAsStateWithLifecycle()
    val isVerseExplanationLoading by viewModel.isVerseExplanationLoading.collectAsStateWithLifecycle()
    val verseExplanationResult by viewModel.verseExplanationResult.collectAsStateWithLifecycle()

    // New Features: Challenges, Flashcards, Alarm, Situational Prayers
    val allChallenges by viewModel.allChallenges.collectAsStateWithLifecycle()
    val activeChallenge by viewModel.activeChallenge.collectAsStateWithLifecycle()
    val allMemorizedVerses by viewModel.allMemorizedVerses.collectAsStateWithLifecycle()
    val dueMemorizedVerses by viewModel.dueMemorizedVerses.collectAsStateWithLifecycle()
    val spiritualAlarm by viewModel.spiritualAlarm.collectAsStateWithLifecycle()
    val showSituationalPrayersSheet by viewModel.showSituationalPrayersSheet.collectAsStateWithLifecycle()
    val showChallengesSheet by viewModel.showChallengesSheet.collectAsStateWithLifecycle()
    val showFlashcardsSheet by viewModel.showFlashcardsSheet.collectAsStateWithLifecycle()
    val showCollectionsSheet by viewModel.showCollectionsSheet.collectAsStateWithLifecycle()
    val showSpiritualWakeupModal by viewModel.showSpiritualWakeupModal.collectAsStateWithLifecycle()
    val isQuizSheetVisible by viewModel.isQuizSheetVisible.collectAsStateWithLifecycle()

    var showProfileModal by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    val isSystemDark = isSystemInDarkTheme()
    val isDarkTheme = when (userProfile.themeMode) {
        "DARK" -> true
        "LIGHT" -> false
        else -> isSystemDark
    }

    LaunchedEffect(syncMessage) {
        syncMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSyncMessage()
        }
    }

    // Timer state
    val showTimerSheet by viewModel.showTimerSheet.collectAsStateWithLifecycle()
    val activeMeditation by viewModel.activeMeditation.collectAsStateWithLifecycle()
    val timerTotalSeconds by viewModel.timerTotalSeconds.collectAsStateWithLifecycle()
    val timerRemainingSeconds by viewModel.timerRemainingSeconds.collectAsStateWithLifecycle()
    val isTimerRunning by viewModel.isTimerRunning.collectAsStateWithLifecycle()
    val activeSoundscape by viewModel.activeSoundscape.collectAsStateWithLifecycle()
    val sessionCompletedDialog by viewModel.sessionCompletedDialog.collectAsStateWithLifecycle()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val activeRemindersCount = reminders.count { it.isEnabled }

    BackHandler(enabled = drawerState.isOpen) {
        scope.launch { drawerState.close() }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.width(280.dp)) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Outils Spirituels",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp),
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                HorizontalDivider()
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Lightbulb, contentDescription = null) },
                    label = { Text("SOS Prières (Situation)") },
                    selected = false,
                    onClick = { 
                        scope.launch { drawerState.close() }
                        viewModel.openSituationalPrayersSheet()
                    }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.CollectionsBookmark, contentDescription = null) },
                    label = { Text("Collections & Favoris") },
                    selected = false,
                    onClick = { 
                        scope.launch { drawerState.close() }
                        viewModel.openCollectionsSheet()
                    }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Quiz, contentDescription = null) },
                    label = { Text("Quiz Biblique IA") },
                    selected = false,
                    onClick = { 
                        scope.launch { drawerState.close() }
                        viewModel.openQuizSheet()
                    }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Assignment, contentDescription = null) },
                    label = { Text("Défis Spirituels") },
                    selected = false,
                    onClick = { 
                        scope.launch { drawerState.close() }
                        viewModel.openChallengesSheet()
                    }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.LocalLibrary, contentDescription = null) },
                    label = { Text("Cartes de Mémorisation") },
                    selected = false,
                    onClick = { 
                        scope.launch { drawerState.close() }
                        viewModel.openFlashcardsSheet()
                    }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Alarm, contentDescription = null) },
                    label = { Text("Réveil Spirituel") },
                    selected = false,
                    onClick = { 
                        scope.launch { drawerState.close() }
                        viewModel.openSpiritualWakeup()
                    }
                )
            }
        }
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
        if (showProfileModal) {
            BackHandler {
                showProfileModal = false
            }
            Scaffold(
                snackbarHost = { SnackbarHost(snackbarHostState) },
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = "Profil & Synchronisation",
                                style = MaterialTheme.typography.titleLarge,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { showProfileModal = false }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            titleContentColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    ProfileScreen(
                        userProfile = userProfile,
                        favoriteVerses = favoriteVerses,
                        onUpdateProfile = { profile -> viewModel.updateProfile(profile) },
                        onConnectGoogle = { name, email -> viewModel.connectGoogleAccount(name, email) },
                        onDisconnectGoogle = { viewModel.disconnectGoogleAccount() },
                        onTriggerBackup = { viewModel.triggerCloudSync() }
                    )
                }
            }
        } else {
            Scaffold(
                snackbarHost = { SnackbarHost(snackbarHostState) },
                topBar = {
                    BethelTopBar(
                        streakCount = spiritualStats.currentStreak,
                        isDarkTheme = isDarkTheme,
                        onToggleTheme = { newDark ->
                            viewModel.setThemeMode(if (newDark) "DARK" else "LIGHT")
                        },
                        onStreakClick = { viewModel.selectTab(4) },
                        onProfileClick = { showProfileModal = true },
                        onMenuClick = { scope.launch { drawerState.open() } }
                    )
                },
                bottomBar = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Mini Meditation Player (visible when meditation is active but sheet is minimized)
                        AnimatedVisibility(
                            visible = !showTimerSheet && (isTimerRunning || timerRemainingSeconds > 0),
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            val hours = timerRemainingSeconds / 3600
                            val minutes = (timerRemainingSeconds % 3600) / 60
                            val seconds = timerRemainingSeconds % 60
                            val formattedRemaining = if (hours > 0) {
                                "%02d:%02d:%02d".format(hours, minutes, seconds)
                            } else {
                                "%02d:%02d".format(minutes, seconds)
                            }

                            ElevatedCard(
                                onClick = { viewModel.reopenTimerSheet() },
                                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                                colors = CardDefaults.elevatedCardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                ),
                                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp)
                                    .testTag("mini_meditation_player")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text("🕊️", fontSize = 16.sp)
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = activeMeditation?.title ?: "Prière en cours...",
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = "$formattedRemaining • $activeSoundscape",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.primary,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(
                                            onClick = { viewModel.toggleTimerPause() },
                                            modifier = Modifier
                                                .size(36.dp)
                                                .testTag("mini_player_pause_button")
                                        ) {
                                            Icon(
                                                imageVector = if (isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                                contentDescription = if (isTimerRunning) "Pause" else "Reprendre",
                                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                        IconButton(
                                            onClick = { viewModel.reopenTimerSheet() },
                                            modifier = Modifier
                                                .size(36.dp)
                                                .testTag("mini_player_expand_button")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.OpenInFull,
                                                contentDescription = "Agrandir",
                                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                        IconButton(
                                            onClick = { viewModel.cancelSession() },
                                            modifier = Modifier
                                                .size(36.dp)
                                                .testTag("mini_player_close_button")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Arrêter la prière",
                                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        BethelBottomNavBar(
                            selectedTabIndex = currentTab,
                            onTabSelected = { viewModel.selectTab(it) },
                            activeRemindersCount = activeRemindersCount
                        )
                    }
                }
            ) { innerPadding ->
                Crossfade(
                    targetState = currentTab,
                    label = "tab_crossfade",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) { tab ->
                    when (tab) {
                        0 -> HomeScreen(
                            userProfile = userProfile,
                            todayVerse = todayVerse,
                            favoriteVerses = favoriteVerses,
                            reminders = reminders,
                            spiritualStats = spiritualStats,
                            onStartPrayerClick = { minutes -> viewModel.openFreePrayerTimer(minutes) },
                            onMeditationClick = { item -> viewModel.startMeditation(item) },
                            onToggleFavoriteVerse = { verse, isFav -> viewModel.toggleFavorite(verse, isFav) },
                            onNavigateToReminders = { viewModel.selectTab(3) },
                            onNavigateToMeditation = { viewModel.selectTab(2) },
                            onNavigateToBible = { book, chap -> viewModel.navigateToBiblePassage(book, chap) },
                            onNavigateToBibleTab = { viewModel.selectTab(1) },
                            onOpenSituationalPrayers = { viewModel.openSituationalPrayersSheet() },
                            onOpenChallenges = { viewModel.openChallengesSheet() },
                            onOpenFlashcards = { viewModel.openFlashcardsSheet() },
                            onOpenSpiritualAlarm = { viewModel.openSpiritualWakeup() },
                            onOpenQuiz = { viewModel.openQuizSheet() },
                            onOpenCollections = { viewModel.openCollectionsSheet() },
                            activeChallenge = activeChallenge,
                            dueFlashcardsCount = dueMemorizedVerses.size,
                            spiritualAlarm = spiritualAlarm
                        )
                        1 -> BibleScreen(
                            currentVersion = selectedBibleVersion,
                            currentBook = selectedBook,
                            currentChapter = selectedChapter,
                            chapterVerses = chapterVerses,
                            markedVerses = markedVerses,
                            readingPlans = readingPlans,
                            activeReadingPlan = activeReadingPlan,
                            searchResults = bibleSearchResults,
                            searchQuery = bibleSearchQuery,
                            isSearching = isBibleSearching,
                            onSelectBookAndChapter = { bookId, chap -> viewModel.selectBibleBookAndChapter(bookId, chap) },
                            onNextChapter = { viewModel.nextBibleChapter() },
                            onPreviousChapter = { viewModel.previousBibleChapter() },
                            onChangeVersion = { ver -> viewModel.setBibleVersion(ver) },
                            onToggleHighlight = { verse, color -> viewModel.toggleVerseHighlight(verse, color) },
                            onToggleBookmark = { verse -> viewModel.toggleVerseBookmark(verse) },
                            onAddToCollection = { verse, collection -> viewModel.addVerseToCollection(verse, collection) },
                            onSaveNote = { verse, note -> viewModel.saveVerseNote(verse, note) },
                            onSearchBible = { q -> viewModel.searchBible(q) },
                            onClearSearch = { viewModel.clearBibleSearch() },
                            onSetActivePlan = { planId -> viewModel.setActiveReadingPlan(planId) },
                            onTogglePlanDay = { plan, day -> viewModel.toggleReadingPlanDay(plan, day) },
                            onStartMeditationFromVerse = { verse -> viewModel.startMeditationFromVerse(verse) },
                            onExplainVerse = { verse -> viewModel.explainVerse(verse) },
                            isVerseExplanationLoading = isVerseExplanationLoading,
                            verseExplanationResult = verseExplanationResult,
                            onClearVerseExplanation = { viewModel.clearVerseExplanation() },
                            isAudioPlaying = isBibleAudioPlaying,
                            isAudioPaused = isBibleAudioPaused,
                            isAudioPlayerVisible = isBibleAudioPlayerVisible,
                            currentSpeakingVerseIndex = currentSpeakingVerseIndex,
                            currentSpeakingVerseNumber = currentSpeakingVerseNumber,
                            audioSpeed = bibleAudioSpeed,
                            onStartAudio = { startIndex -> viewModel.startBibleAudio(startIndex) },
                            onToggleAudioPlayPause = { viewModel.toggleBibleAudioPlayPause() },
                            onStopAudio = { viewModel.stopBibleAudio() },
                            onCloseAudioPlayer = { viewModel.closeBibleAudioPlayer() },
                            onNextAudioVerse = { viewModel.nextBibleAudioVerse() },
                            onPreviousAudioVerse = { viewModel.previousBibleAudioVerse() },
                            onSeekAudioVerse = { idx -> viewModel.seekBibleAudioVerse(idx) },
                            onSetAudioSpeed = { speed -> viewModel.setBibleAudioSpeed(speed) }
                        )
                        2 -> MeditationScreen(
                            selectedTheme = selectedThemeFilter,
                            onThemeSelected = { viewModel.setThemeFilter(it) },
                            onStartMeditation = { item -> viewModel.startMeditation(item) },
                            onStartCustomTimer = { duration -> viewModel.openFreePrayerTimer(duration) },
                            onOpenBiblePassage = { ref ->
                                val parts = ref.split(" ", ":")
                                val bookName = parts.firstOrNull() ?: "Jean"
                                val chapNum = parts.getOrNull(1)?.toIntOrNull() ?: 1
                                viewModel.navigateToBiblePassage(bookName, chapNum)
                            }
                        )
                        3 -> RemindersScreen(
                            reminders = reminders,
                            isAutoDndEnabled = userProfile.isAutoDndEnabled,
                            spiritualAlarm = spiritualAlarm,
                            onToggleReminder = { reminder, isEnabled -> viewModel.toggleReminder(reminder, isEnabled) },
                            onSaveReminder = { reminder -> viewModel.saveReminder(reminder) },
                            onDeleteReminder = { reminder -> viewModel.deleteReminder(reminder) },
                            onTestReminder = { ctx, reminder -> viewModel.sendTestNotification(ctx, reminder) },
                            onToggleAutoDnd = { enabled -> viewModel.setAutoDndEnabled(enabled) },
                            onSaveSpiritualAlarm = { alarm -> viewModel.saveSpiritualAlarm(alarm) },
                            onTestSpiritualAlarm = { viewModel.testSpiritualAlarm() },
                            onRestoreCanonical = { viewModel.restoreCanonicalReminders() }
                        )
                        4 -> JournalProgressScreen(
                            spiritualStats = spiritualStats,
                            journalEntries = journalEntries,
                            onSaveJournalEntry = { entry -> viewModel.saveJournalEntry(entry) },
                            onMarkJournalAnswered = { entry, testimony -> viewModel.markJournalAnswered(entry, testimony) },
                            onDeleteJournalEntry = { entry -> viewModel.deleteJournalEntry(entry) }
                        )
                    }
                }
            }
        }

        // Active Meditation Bottom Sheet
        if (showTimerSheet) {
            ActiveMeditationSheet(
                meditationItem = activeMeditation,
                totalSeconds = timerTotalSeconds,
                remainingSeconds = timerRemainingSeconds,
                isRunning = isTimerRunning,
                activeSoundscape = activeSoundscape,
                showCompletionDialog = sessionCompletedDialog,
                isDndActive = userProfile.isAutoDndEnabled,
                onTogglePause = { viewModel.toggleTimerPause() },
                onAddMinutes = { mins -> viewModel.addTimerMinutes(mins) },
                onSoundscapeChange = { sound -> viewModel.setSoundscape(sound) },
                onCompleteEarly = { viewModel.completeSession() },
                onMinimize = { viewModel.minimizeTimerSheet() },
                onCancelSession = { viewModel.cancelSession() },
                onDismissCompletion = { viewModel.dismissCompletedDialog() }
            )
        }

        // Situational Prayers Bottom Sheet
        if (showSituationalPrayersSheet) {
            SituationalPrayersBottomSheet(
                viewModel = viewModel,
                onDismiss = { viewModel.closeSituationalPrayersSheet() }
            )
        }

        // Spiritual Challenges Bottom Sheet
        if (showChallengesSheet) {
            ChallengesBottomSheet(
                viewModel = viewModel,
                onDismiss = { viewModel.closeChallengesSheet() }
            )
        }

        // Memorized Verses (Flashcards) Bottom Sheet
        if (showFlashcardsSheet) {
            FlashcardsBottomSheet(
                viewModel = viewModel,
                onDismiss = { viewModel.closeFlashcardsSheet() }
            )
        }

        // Spiritual Wakeup Full-Screen Dialog
        if (showSpiritualWakeupModal) {
            SpiritualWakeupFullScreenDialog(
                viewModel = viewModel,
                onDismiss = { viewModel.dismissSpiritualWakeup() }
            )
        }

        // Quiz Bottom Sheet
        if (isQuizSheetVisible) {
            com.example.ui.screens.QuizBottomSheet(
                viewModel = viewModel,
                onDismiss = { viewModel.closeQuizSheet() }
            )
        }

        // Collections Bottom Sheet
        if (showCollectionsSheet) {
            com.example.ui.screens.FavoritesScreenSheet(
                viewModel = viewModel,
                onDismiss = { viewModel.closeCollectionsSheet() }
            )
        }
    }
    }
}
