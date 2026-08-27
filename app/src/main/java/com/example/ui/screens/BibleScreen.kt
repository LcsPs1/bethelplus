package com.example.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Search
import com.example.ui.components.BibleAudioPlayerBar
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.BibleBookInfo
import com.example.data.local.BibleData
import com.example.data.local.BibleReadingPlanEntity
import com.example.data.local.BibleVerseEntity
import com.example.ui.theme.BethelGold
import com.example.ui.theme.BethelGoldDark
import com.example.ui.theme.BethelGoldLight
import com.example.ui.theme.BethelMidnightBackground
import com.example.ui.theme.BethelMidnightSurface
import com.example.ui.theme.BethelTerracotta
import kotlinx.coroutines.launch

enum class BibleSubTab {
    READER,
    SEARCH,
    PLANS,
    TREASURES
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BibleScreen(
    currentVersion: String,
    currentBook: BibleBookInfo,
    currentChapter: Int,
    chapterVerses: List<BibleVerseEntity>,
    markedVerses: List<BibleVerseEntity>,
    readingPlans: List<BibleReadingPlanEntity>,
    activeReadingPlan: BibleReadingPlanEntity?,
    searchResults: List<BibleVerseEntity>,
    searchQuery: String,
    isSearching: Boolean,
    onSelectBookAndChapter: (String, Int) -> Unit,
    onNextChapter: () -> Unit,
    onPreviousChapter: () -> Unit,
    onChangeVersion: (String) -> Unit,
    onToggleHighlight: (BibleVerseEntity, String?) -> Unit,
    onToggleBookmark: (BibleVerseEntity) -> Unit,
    onAddToCollection: (BibleVerseEntity, String) -> Unit,
    onSaveNote: (BibleVerseEntity, String?) -> Unit,
    onSearchBible: (String) -> Unit,
    onClearSearch: () -> Unit,
    onSetActivePlan: (String) -> Unit,
    onTogglePlanDay: (BibleReadingPlanEntity, Int) -> Unit,
    onStartMeditationFromVerse: (BibleVerseEntity) -> Unit,
    onExplainVerse: (BibleVerseEntity) -> Unit = {},
    isVerseExplanationLoading: Boolean = false,
    verseExplanationResult: String? = null,
    onClearVerseExplanation: () -> Unit = {},
    isAudioPlaying: Boolean = false,
    isAudioPaused: Boolean = false,
    isAudioPlayerVisible: Boolean = false,
    currentSpeakingVerseIndex: Int = 0,
    currentSpeakingVerseNumber: Int = 1,
    audioSpeed: Float = 0.85f,
    onStartAudio: (startIndex: Int) -> Unit = {},
    onToggleAudioPlayPause: () -> Unit = {},
    onStopAudio: () -> Unit = {},
    onCloseAudioPlayer: () -> Unit = {},
    onNextAudioVerse: () -> Unit = {},
    onPreviousAudioVerse: () -> Unit = {},
    onSeekAudioVerse: (index: Int) -> Unit = {},
    onSetAudioSpeed: (rate: Float) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var currentSubTab by remember { mutableStateOf(BibleSubTab.READER) }
    var showBookSelectorDialog by remember { mutableStateOf(false) }
    var showVersionSelectorDialog by remember { mutableStateOf(false) }
    var selectedVerseForAction by remember { mutableStateOf<BibleVerseEntity?>(null) }
    var collectionSelectionTarget by remember { mutableStateOf<BibleVerseEntity?>(null) }
    var verseNoteDialogTarget by remember { mutableStateOf<BibleVerseEntity?>(null) }
    var verseCardShareTarget by remember { mutableStateOf<BibleVerseEntity?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Sub Navigation Header (Lecteur, Recherche, Plans, Mes Trésors)
        Surface(
            tonalElevation = 3.dp,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                TabRow(
                    selectedTabIndex = currentSubTab.ordinal,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[currentSubTab.ordinal]),
                            color = BethelGold,
                            height = 3.dp
                        )
                    }
                ) {
                    Tab(
                        selected = currentSubTab == BibleSubTab.READER,
                        onClick = { currentSubTab = BibleSubTab.READER },
                        text = { Text("Texte Sacré", fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(18.dp)) },
                        modifier = Modifier.testTag("bible_tab_reader")
                    )
                    Tab(
                        selected = currentSubTab == BibleSubTab.SEARCH,
                        onClick = { currentSubTab = BibleSubTab.SEARCH },
                        text = { Text("Recherche", fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                        modifier = Modifier.testTag("bible_tab_search")
                    )
                    Tab(
                        selected = currentSubTab == BibleSubTab.PLANS,
                        onClick = { currentSubTab = BibleSubTab.PLANS },
                        text = { Text("Plans", fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.EventNote, contentDescription = null, modifier = Modifier.size(18.dp)) },
                        modifier = Modifier.testTag("bible_tab_plans")
                    )
                    Tab(
                        selected = currentSubTab == BibleSubTab.TREASURES,
                        onClick = { currentSubTab = BibleSubTab.TREASURES },
                        text = { Text("Trésors (${markedVerses.size})", fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.Bookmark, contentDescription = null, modifier = Modifier.size(18.dp)) },
                        modifier = Modifier.testTag("bible_tab_treasures")
                    )
                }
            }
        }

        // Sub View Content with bottom Audio Player
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            when (currentSubTab) {
                BibleSubTab.READER -> {
                    BibleReaderView(
                        currentVersion = currentVersion,
                        currentBook = currentBook,
                        currentChapter = currentChapter,
                        verses = chapterVerses,
                        onOpenBookSelector = { showBookSelectorDialog = true },
                        onOpenVersionSelector = { showVersionSelectorDialog = true },
                        onPreviousChapter = onPreviousChapter,
                        onNextChapter = onNextChapter,
                        onVerseClick = { verse -> selectedVerseForAction = verse },
                        isAudioPlaying = isAudioPlaying,
                        isAudioPaused = isAudioPaused,
                        currentSpeakingVerseIndex = currentSpeakingVerseIndex,
                        onToggleAudioPlayPause = onToggleAudioPlayPause
                    )
                }
                BibleSubTab.SEARCH -> {
                    BibleSearchView(
                        query = searchQuery,
                        results = searchResults,
                        isSearching = isSearching,
                        onQueryChange = onSearchBible,
                        onClear = onClearSearch,
                        onSelectVerse = { verse ->
                            onSelectBookAndChapter(verse.bookId, verse.chapter)
                            currentSubTab = BibleSubTab.READER
                        },
                        onVerseAction = { verse -> selectedVerseForAction = verse }
                    )
                }
                BibleSubTab.PLANS -> {
                    BibleReadingPlansView(
                        plans = readingPlans,
                        activePlan = activeReadingPlan,
                        onSetActivePlan = onSetActivePlan,
                        onToggleDay = onTogglePlanDay,
                        onOpenPassage = { bookId, chapter ->
                            onSelectBookAndChapter(bookId, chapter)
                            currentSubTab = BibleSubTab.READER
                        }
                    )
                }
                BibleSubTab.TREASURES -> {
                    BibleTreasuresView(
                        markedVerses = markedVerses,
                        onSelectVerse = { verse ->
                            onSelectBookAndChapter(verse.bookId, verse.chapter)
                            currentSubTab = BibleSubTab.READER
                        },
                        onVerseAction = { verse -> selectedVerseForAction = verse }
                    )
                }
            }

            // Persistent Audio Player Bar
            BibleAudioPlayerBar(
                isVisible = isAudioPlayerVisible,
                isPlaying = isAudioPlaying,
                isPaused = isAudioPaused,
                currentBook = currentBook,
                currentChapter = currentChapter,
                currentVerseNumber = currentSpeakingVerseNumber,
                totalVersesCount = chapterVerses.size,
                currentSpeed = audioSpeed,
                onTogglePlayPause = onToggleAudioPlayPause,
                onPreviousVerse = onPreviousAudioVerse,
                onNextVerse = onNextAudioVerse,
                onStop = onStopAudio,
                onClose = onCloseAudioPlayer,
                onSetSpeed = onSetAudioSpeed,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }

    // Modal Book & Chapter Selector
    if (showBookSelectorDialog) {
        BibleBookChapterSelectorDialog(
            currentBook = currentBook,
            currentChapter = currentChapter,
            onDismiss = { showBookSelectorDialog = false },
            onSelect = { bookId, chapter ->
                onSelectBookAndChapter(bookId, chapter)
                showBookSelectorDialog = false
            }
        )
    }

    // Modal Version Selector (LSG / Darby)
    if (showVersionSelectorDialog) {
        BibleVersionSelectorDialog(
            currentVersion = currentVersion,
            onDismiss = { showVersionSelectorDialog = false },
            onSelect = { version ->
                onChangeVersion(version)
                showVersionSelectorDialog = false
            }
        )
    }

    // Verse Actions Sheet (Surligner, Favori, Note, Méditer, Partager, Écouter)
    if (selectedVerseForAction != null) {
        val verse = selectedVerseForAction!!
        VerseActionBottomSheet(
            verse = verse,
            onDismiss = { selectedVerseForAction = null },
            onPlayFromVerse = {
                val index = chapterVerses.indexOfFirst { it.id == verse.id }
                onStartAudio(if (index >= 0) index else 0)
                selectedVerseForAction = null
            },
            onExplainVerse = {
                onExplainVerse(verse)
                selectedVerseForAction = null
            },
            onToggleHighlight = { color ->
                onToggleHighlight(verse, color)
                selectedVerseForAction = null
            },
            onAddToCollection = {
                collectionSelectionTarget = verse
                selectedVerseForAction = null
            },
            onOpenNote = {
                verseNoteDialogTarget = verse
                selectedVerseForAction = null
            },
            onMeditate = {
                onStartMeditationFromVerse(verse)
                selectedVerseForAction = null
            },
            onShareText = {
                val shareText = "« ${verse.text} »\n— ${verse.bookName} ${verse.chapter}:${verse.verse} ($currentVersion)\n\nBéthel+ • Sanctuaire Spirituel"
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, shareText)
                    type = "text/plain"
                }
                context.startActivity(Intent.createChooser(sendIntent, "Partager ce verset biblique"))
                selectedVerseForAction = null
            },
            onGenerateCard = {
                verseCardShareTarget = verse
                selectedVerseForAction = null
            }
        )
    }

    // Collection Selection Sheet
    if (collectionSelectionTarget != null) {
        val verse = collectionSelectionTarget!!
        CollectionSelectionSheet(
            verse = verse,
            onDismiss = { collectionSelectionTarget = null },
            onCollectionSelected = { collectionName ->
                onAddToCollection(verse, collectionName)
                collectionSelectionTarget = null
            }
        )
    }

    // Note Editor Dialog
    if (verseNoteDialogTarget != null) {
        val verse = verseNoteDialogTarget!!
        var noteContent by remember { mutableStateOf(verse.personalNote ?: "") }

        AlertDialog(
            onDismissRequest = { verseNoteDialogTarget = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.EditNote, contentDescription = null, tint = BethelGold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Note sur ${verse.bookName} ${verse.chapter}:${verse.verse}",
                        style = MaterialTheme.typography.titleMedium,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "« ${verse.text} »",
                        style = MaterialTheme.typography.bodySmall,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = noteContent,
                        onValueChange = { noteContent = it },
                        placeholder = { Text("Écrivez vos pensées, révélations et prières inspirées par ce verset...") },
                        minLines = 4,
                        maxLines = 8,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("verse_note_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSaveNote(verse, noteContent.ifBlank { null })
                        verseNoteDialogTarget = null
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.testTag("save_verse_note_button")
                ) {
                    Text("Enregistrer")
                }
            },
            dismissButton = {
                TextButton(onClick = { verseNoteDialogTarget = null }) {
                    Text("Annuler")
                }
            }
        )
    }

    // Verse Explanation Dialog
    if (isVerseExplanationLoading || verseExplanationResult != null) {
        AlertDialog(
            onDismissRequest = onClearVerseExplanation,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.SelfImprovement, contentDescription = null, tint = BethelGold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Éclairage (Gemini IA)",
                        style = MaterialTheme.typography.titleMedium,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = BethelGoldDark
                    )
                }
            },
            text = {
                if (isVerseExplanationLoading) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth().padding(32.dp)
                    ) {
                        androidx.compose.material3.CircularProgressIndicator(color = BethelGold)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Méditation et analyse du verset en cours...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    androidx.compose.foundation.lazy.LazyColumn {
                        item {
                            Text(
                                text = verseExplanationResult ?: "",
                                style = MaterialTheme.typography.bodyLarge,
                                lineHeight = 24.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            },
            confirmButton = {
                if (!isVerseExplanationLoading) {
                    Button(
                        onClick = onClearVerseExplanation,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BethelGold)
                    ) {
                        Text("J'ai compris")
                    }
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        )
    }

    // Verse Visual Card Dialog for Sharing
    if (verseCardShareTarget != null) {
        val verse = verseCardShareTarget!!
        VerseCardShareDialog(
            verse = verse,
            version = currentVersion,
            onDismiss = { verseCardShareTarget = null },
            onShare = {
                val shareText = "« ${verse.text} »\n— ${verse.bookName} ${verse.chapter}:${verse.verse} ($currentVersion)\n\n✨ Partagé depuis Béthel+, Maison de Dieu"
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, shareText)
                    type = "text/plain"
                }
                context.startActivity(Intent.createChooser(sendIntent, "Partager la carte de verset"))
                verseCardShareTarget = null
            }
        )
    }
}

// -------------------------------------------------------------
// 1. READER VIEW (LECTURE BIBLIQUE FLUIDE & HORS-LIGNE)
// -------------------------------------------------------------
@Composable
fun BibleReaderView(
    currentVersion: String,
    currentBook: BibleBookInfo,
    currentChapter: Int,
    verses: List<BibleVerseEntity>,
    onOpenBookSelector: () -> Unit,
    onOpenVersionSelector: () -> Unit,
    onPreviousChapter: () -> Unit,
    onNextChapter: () -> Unit,
    onVerseClick: (BibleVerseEntity) -> Unit,
    isAudioPlaying: Boolean = false,
    isAudioPaused: Boolean = false,
    currentSpeakingVerseIndex: Int = 0,
    onToggleAudioPlayPause: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    // Scroll to top when book or chapter changes
    LaunchedEffect(currentBook.id, currentChapter) {
        listState.scrollToItem(0)
    }

    // Auto-scroll to currently speaking verse
    LaunchedEffect(currentSpeakingVerseIndex, isAudioPlaying) {
        if (isAudioPlaying && currentSpeakingVerseIndex in verses.indices) {
            listState.animateScrollToItem((currentSpeakingVerseIndex + 1).coerceAtMost(verses.size))
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Reader Toolbar
        Surface(
            tonalElevation = 2.dp,
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Book & Chapter Selector Button
                OutlinedButton(
                    onClick = onOpenBookSelector,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, BethelGold.copy(alpha = 0.6f)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.testTag("book_chapter_picker_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = null,
                        tint = BethelGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${currentBook.name} $currentChapter",
                        style = MaterialTheme.typography.titleMedium,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Audio TTS Listen Button
                    Button(
                        onClick = onToggleAudioPlayPause,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isAudioPlaying) BethelGold else BethelGoldLight.copy(alpha = 0.7f),
                            contentColor = if (isAudioPlaying) Color.White else BethelGoldDark
                        ),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier
                            .height(36.dp)
                            .testTag("bible_listen_button")
                    ) {
                        Icon(
                            imageVector = if (isAudioPlaying) Icons.Default.GraphicEq else Icons.Default.Headphones,
                            contentDescription = if (isAudioPlaying) "En lecture" else "Écouter le chapitre",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isAudioPlaying) "En cours" else "Écouter",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    // Version Selector Pill (LSG / DRB)
                    AssistChip(
                        onClick = onOpenVersionSelector,
                        label = {
                            Text(
                                text = if (currentVersion == "LSG") "LSG" else "DRB",
                                fontWeight = FontWeight.Bold,
                                color = BethelGoldDark
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = BethelGoldLight.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier
                            .height(36.dp)
                            .testTag("version_picker_button")
                    )
                }
            }
        }

        // Verses Lazy Column
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .testTag("bible_verses_list")
        ) {
            // Chapter Title Header
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = currentBook.name.uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        letterSpacing = 2.sp,
                        color = BethelGold,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Chapitre $currentChapter",
                        style = MaterialTheme.typography.headlineMedium,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "${currentBook.category} • ${if (currentBook.testament == "OT") "Ancien Testament" else "Nouveau Testament"}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = BethelGold.copy(alpha = 0.3f), thickness = 1.dp, modifier = Modifier.width(120.dp))
                }
            }

            // Verses Items with Highlight on Active Speaking Verse
            items(verses, key = { it.id }) { verse ->
                val verseIndex = verses.indexOf(verse)
                val isSpeaking = (isAudioPlaying || isAudioPaused) && currentSpeakingVerseIndex == verseIndex

                VerseItemRow(
                    verse = verse,
                    isSpeaking = isSpeaking,
                    onClick = { onVerseClick(verse) }
                )
            }

            // Bottom Chapter Navigation Pill & End of Chapter spacing
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onPreviousChapter,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("prev_chapter_button")
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Précédent", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Précédent")
                    }

                    Text(
                        text = "${currentBook.name} $currentChapter",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Button(
                        onClick = onNextChapter,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.testTag("next_chapter_button")
                    ) {
                        Text("Suivant")
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.Default.ArrowForward, contentDescription = "Suivant", modifier = Modifier.size(18.dp))
                    }
                }
                Spacer(modifier = Modifier.height(90.dp)) // Padding for bottom player bar
            }
        }
    }
}

@Composable
fun VerseItemRow(
    verse: BibleVerseEntity,
    onClick: () -> Unit,
    isSpeaking: Boolean = false,
    modifier: Modifier = Modifier
) {
    val highlightBg = when {
        isSpeaking -> BethelGoldLight.copy(alpha = 0.85f)
        verse.highlightColor == "GOLD" -> BethelGoldLight.copy(alpha = 0.5f)
        verse.highlightColor == "BLUE" -> Color(0xFFE1F0FA)
        verse.highlightColor == "GREEN" -> Color(0xFFE3F7E8)
        verse.highlightColor == "PURPLE" -> Color(0xFFF3E8FA)
        verse.highlightColor == "TERRACOTTA" -> Color(0xFFFFECE5)
        else -> Color.Transparent
    }

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = highlightBg,
        border = when {
            isSpeaking -> BorderStroke(2.dp, BethelGold)
            verse.isBookmarked -> BorderStroke(1.dp, BethelGold)
            else -> null
        },
        shadowElevation = if (isSpeaking) 4.dp else 0.dp,
        modifier = modifier
            .fillMaxWidth()
            .testTag("verse_item_${verse.verse}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Verse Number Pill / Speaking Equalizer
                if (isSpeaking) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = "Verset en cours de lecture",
                        tint = BethelGoldDark,
                        modifier = Modifier
                            .padding(top = 2.dp, end = 6.dp)
                            .size(20.dp)
                    )
                } else {
                    Text(
                        text = "${verse.verse}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = BethelGoldDark,
                        modifier = Modifier
                            .padding(top = 2.dp, end = 10.dp)
                            .width(26.dp)
                    )
                }

                // Verse Text
                Text(
                    text = verse.text,
                    style = MaterialTheme.typography.bodyLarge,
                    fontFamily = FontFamily.Serif,
                    lineHeight = 26.sp,
                    fontWeight = if (isSpeaking) FontWeight.SemiBold else FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )

                // Bookmark icon if marked
                if (verse.isBookmarked) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = "Favori",
                        tint = BethelGold,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Note pill if present
            if (!verse.personalNote.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 26.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.EditNote,
                            contentDescription = null,
                            tint = BethelGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = verse.personalNote,
                            style = MaterialTheme.typography.bodySmall,
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 2. SEARCH VIEW (RECHERCHE PLEIN TEXTE & PAR MOT-CLÉ/RÉFÉRENCE)
// -------------------------------------------------------------
@Composable
fun BibleSearchView(
    query: String,
    results: List<BibleVerseEntity>,
    isSearching: Boolean,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    onSelectVerse: (BibleVerseEntity) -> Unit,
    onVerseAction: (BibleVerseEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val quickTopics = listOf(
        "Jean 3:16",
        "Psaumes 23",
        "Genèse 28:19",
        "Paix",
        "Lumière",
        "Prière",
        "Foi",
        "Amour",
        "Grâce",
        "Guérison",
        "Béthel",
        "Espérance"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Search Input
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text("Ex : Jean 3:16, Béthel, Psaumes 23, Grâce, Amour...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = BethelGold) },
            trailingIcon = {
                if (query.isNotBlank()) {
                    IconButton(onClick = onClear) {
                        Icon(Icons.Default.Clear, contentDescription = "Effacer")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("bible_search_text_field")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Quick Topic Chips Horizontal Row
        Text(
            text = "Recherches suggérées & Références clés :",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            quickTopics.forEach { topic ->
                AssistChip(
                    onClick = { onQueryChange(topic) },
                    label = { Text(topic, fontSize = 12.sp) },
                    shape = RoundedCornerShape(12.dp),
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Results Section
        if (isSearching) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = BethelGold)
            }
        } else if (query.isNotBlank() && results.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Aucun verset trouvé pour « $query »",
                        style = MaterialTheme.typography.titleMedium,
                        fontFamily = FontFamily.Serif
                    )
                    Text(
                        text = "Essayez un mot-clé (ex: amour, paix, grâce) ou une référence (ex: Jean 3:16).",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else if (results.isNotEmpty()) {
            Text(
                text = "${results.size} verset(s) trouvé(s) pour « $query »",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = BethelGold
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(results, key = { it.id }) { verse ->
                    Card(
                        onClick = { onSelectVerse(verse) },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${verse.bookName} ${verse.chapter}:${verse.verse}",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    color = BethelGoldDark
                                )

                                IconButton(
                                    onClick = { onVerseAction(verse) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = "Actions",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "« ${verse.text} »",
                                style = MaterialTheme.typography.bodyMedium,
                                fontFamily = FontFamily.Serif,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        } else {
            // Initial empty search state
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = null,
                        tint = BethelGold.copy(alpha = 0.6f),
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Recherche Biblique Complète",
                        style = MaterialTheme.typography.titleLarge,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Tapez un mot-clé pour chercher dans tous les livres, ou entrez une référence directe (ex: Jean 3:16, Romains 8, Psaume 91).",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 3. READING PLANS VIEW (PLANS DE LECTURE BIBLIQUE)
// -------------------------------------------------------------
@Composable
fun BibleReadingPlansView(
    plans: List<BibleReadingPlanEntity>,
    activePlan: BibleReadingPlanEntity?,
    onSetActivePlan: (String) -> Unit,
    onToggleDay: (BibleReadingPlanEntity, Int) -> Unit,
    onOpenPassage: (String, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedPlanForDetails by remember { mutableStateOf<BibleReadingPlanEntity?>(activePlan ?: plans.firstOrNull()) }

    LaunchedEffect(activePlan) {
        if (selectedPlanForDetails == null && activePlan != null) {
            selectedPlanForDetails = activePlan
        }
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.fillMaxSize()
    ) {
        item {
            Text(
                text = "Plans de Lecture Biblique",
                style = MaterialTheme.typography.headlineSmall,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Parcourez les Écritures à votre rythme avec un suivi quotidien de progression.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Plans Horizontal Chips / Cards
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                plans.forEach { plan ->
                    val isSelected = selectedPlanForDetails?.id == plan.id
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedPlanForDetails = plan },
                        label = { Text(plan.title, fontWeight = FontWeight.Bold) },
                        leadingIcon = {
                            if (plan.isActive) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = BethelGold, modifier = Modifier.size(16.dp))
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }
        }

        // Active / Selected Plan Overview Card
        if (selectedPlanForDetails != null) {
            val currentPlan = selectedPlanForDetails!!
            val completedDays = currentPlan.completedDaysCsv
                .split(",")
                .filter { it.isNotBlank() }
                .mapNotNull { it.toIntOrNull() }
                .toSet()

            val progressPercent = if (currentPlan.totalDays > 0) {
                (completedDays.size.toFloat() / currentPlan.totalDays.toFloat()).coerceIn(0f, 1f)
            } else 0f

            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, BethelGold.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = currentPlan.title,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = currentPlan.subtitle,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            if (!currentPlan.isActive) {
                                Button(
                                    onClick = { onSetActivePlan(currentPlan.id) },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                    modifier = Modifier.testTag("activate_plan_button")
                                ) {
                                    Text("Activer", fontSize = 12.sp)
                                }
                            } else {
                                AssistChip(
                                    onClick = {},
                                    label = { Text("Plan Actif", color = BethelGoldDark, fontWeight = FontWeight.Bold) },
                                    leadingIcon = { Icon(Icons.Default.CheckCircle, contentDescription = null, tint = BethelGoldDark, modifier = Modifier.size(16.dp)) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Progress Bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Progression : ${completedDays.size} / ${currentPlan.totalDays} jours",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = BethelGoldDark
                            )
                            Text(
                                text = "${(progressPercent * 100).toInt()}%",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = BethelGoldDark
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { progressPercent },
                            color = BethelGold,
                            trackColor = BethelGoldLight.copy(alpha = 0.4f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Lectures du Plan :",
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold
                )
            }

            // Display Days for the Plan (showing 15 days or full range)
            val displayMaxDays = minOf(currentPlan.totalDays, 40)
            items((1..displayMaxDays).toList()) { dayNum ->
                val dayData = BibleData.getReadingPlanDay(currentPlan.id, dayNum)
                val isCompleted = completedDays.contains(dayNum)

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isCompleted) BethelGoldLight.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, if (isCompleted) BethelGold else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { onToggleDay(currentPlan, dayNum) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                contentDescription = "Marquer comme lu",
                                tint = if (isCompleted) BethelGold else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Jour $dayNum : ${dayData.passageTitle}",
                                style = MaterialTheme.typography.titleSmall,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = dayData.devotionalTheme,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        OutlinedButton(
                            onClick = { onOpenPassage(dayData.bookId, dayData.chapter) },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Lire", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 4. TREASURES VIEW (VERSETS FAVORIS, SURLIGNÉS & NOTES)
// -------------------------------------------------------------
@Composable
fun BibleTreasuresView(
    markedVerses: List<BibleVerseEntity>,
    onSelectVerse: (BibleVerseEntity) -> Unit,
    onVerseAction: (BibleVerseEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    if (markedVerses.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.BookmarkBorder,
                    contentDescription = null,
                    tint = BethelGold.copy(alpha = 0.6f),
                    modifier = Modifier.size(56.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Aucun verset marqué pour le moment",
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Dans le lecteur biblique, appuyez sur un verset pour le surligner, l'ajouter à vos favoris ou lui associer une note d'étude personnelle.",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = modifier.fillMaxSize()
        ) {
            item {
                Text(
                    text = "Mes Trésors & Surlignages (${markedVerses.size})",
                    style = MaterialTheme.typography.headlineSmall,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Retrouvez vos passages favoris, surlignages et notes spirituelles.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(markedVerses, key = { it.id }) { verse ->
                Card(
                    onClick = { onSelectVerse(verse) },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, BethelGold.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (verse.isBookmarked) Icons.Default.Bookmark else Icons.Default.FormatQuote,
                                    contentDescription = null,
                                    tint = BethelGold,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${verse.bookName} ${verse.chapter}:${verse.verse}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    color = BethelGoldDark
                                )
                            }

                            IconButton(
                                onClick = { onVerseAction(verse) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Actions",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "« ${verse.text} »",
                            style = MaterialTheme.typography.bodyMedium,
                            fontFamily = FontFamily.Serif,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        if (!verse.personalNote.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(8.dp)
                                ) {
                                    Icon(Icons.Default.EditNote, contentDescription = null, tint = BethelGold, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = verse.personalNote,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontStyle = FontStyle.Italic
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 5. MODAL BOOK & CHAPTER PICKER DIALOG
// -------------------------------------------------------------
@Composable
fun BibleBookChapterSelectorDialog(
    currentBook: BibleBookInfo,
    currentChapter: Int,
    onDismiss: () -> Unit,
    onSelect: (String, Int) -> Unit
) {
    var selectedTestament by remember { mutableStateOf(currentBook.testament) }
    var selectedBookForChapters by remember { mutableStateOf(currentBook) }
    var isSelectingChapter by remember { mutableStateOf(false) }

    val filteredBooks = BibleData.ALL_BOOKS.filter { it.testament == selectedTestament }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isSelectingChapter) "Choisir le Chapitre (${selectedBookForChapters.name})" else "Livres de la Bible",
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold
                )
                if (isSelectingChapter) {
                    TextButton(onClick = { isSelectingChapter = false }) {
                        Text("← Livres")
                    }
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (!isSelectingChapter) {
                    // Testament Switcher (Ancien / Nouveau)
                    TabRow(
                        selectedTabIndex = if (selectedTestament == "OT") 0 else 1,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[if (selectedTestament == "OT") 0 else 1]),
                                color = BethelGold
                            )
                        }
                    ) {
                        Tab(
                            selected = selectedTestament == "OT",
                            onClick = { selectedTestament = "OT" },
                            text = { Text("Ancien Testament (39)", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                        )
                        Tab(
                            selected = selectedTestament == "NT",
                            onClick = { selectedTestament = "NT" },
                            text = { Text("Nouveau Testament (27)", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Books Grid
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(340.dp)
                    ) {
                        items(filteredBooks, key = { it.id }) { book ->
                            val isCurrent = book.id == currentBook.id
                            Surface(
                                onClick = {
                                    selectedBookForChapters = book
                                    isSelectingChapter = true
                                },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isCurrent) BethelGoldLight.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                border = BorderStroke(1.dp, if (isCurrent) BethelGold else Color.Transparent),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = book.name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${book.chaptersCount} chapitres",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // Chapters Grid for the chosen book
                    Text(
                        text = "Sélectionnez un chapitre parmi les ${selectedBookForChapters.chaptersCount} de ${selectedBookForChapters.name} :",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(5),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(320.dp)
                    ) {
                        items((1..selectedBookForChapters.chaptersCount).toList()) { chap ->
                            val isCurrent = selectedBookForChapters.id == currentBook.id && chap == currentChapter
                            Surface(
                                onClick = { onSelect(selectedBookForChapters.id, chap) },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isCurrent) BethelGold else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "$chap",
                                        fontWeight = FontWeight.Bold,
                                        color = if (isCurrent) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Fermer")
            }
        }
    )
}

// -------------------------------------------------------------
// 6. MODAL VERSION SELECTOR DIALOG (LSG 1910 / DARBY)
// -------------------------------------------------------------
@Composable
fun BibleVersionSelectorDialog(
    currentVersion: String,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit
) {
    val versions = listOf(
        Pair("LSG", Pair("Louis Segond 1910", "Traduction française de référence, poétique et majestueuse, idéale pour la prière et la méditation.")),
        Pair("DRB", Pair("J.N. Darby (Français)", "Traduction littérale reconnue pour sa fidélité rigoureuse aux textes originaux hébreux et grecs."))
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Translate, contentDescription = null, tint = BethelGold)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Traductions Bibliques",
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Choisissez votre version de lecture. Toutes les traductions sont 100% accessibles hors-ligne.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                versions.forEach { (code, info) ->
                    val isSelected = currentVersion == code
                    Surface(
                        onClick = { onSelect(code) },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) BethelGoldLight.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        border = BorderStroke(1.dp, if (isSelected) BethelGold else Color.Transparent),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = info.first,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) BethelGoldDark else MaterialTheme.colorScheme.onSurface
                                )
                                if (isSelected) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = BethelGoldDark, modifier = Modifier.size(18.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = info.second,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Fermer")
            }
        }
    )
}

// -------------------------------------------------------------
// 7. BOTTOM SHEET FOR VERSE ACTIONS (SURLIGNER, FAVORIS, NOTE, MÉDITER, PARTAGER)
// -------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerseActionBottomSheet(
    verse: BibleVerseEntity,
    onDismiss: () -> Unit,
    onPlayFromVerse: () -> Unit,
    onExplainVerse: () -> Unit,
    onToggleHighlight: (String?) -> Unit,
    onAddToCollection: () -> Unit,
    onOpenNote: () -> Unit,
    onMeditate: () -> Unit,
    onShareText: () -> Unit,
    onGenerateCard: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Verse Reference & Excerpt
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${verse.bookName} ${verse.chapter}:${verse.verse}",
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = BethelGoldDark
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Fermer")
                }
            }

            Text(
                text = "« ${verse.text} »",
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = FontFamily.Serif,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Primary Audio Listen Button
            Button(
                onClick = onPlayFromVerse,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BethelGold),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("play_from_verse_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Headphones,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Écouter à partir de ce verset",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // AI Explanation Button
            Button(
                onClick = onExplainVerse,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BethelGoldLight.copy(alpha = 0.4f),
                    contentColor = BethelGoldDark
                ),
                elevation = ButtonDefaults.buttonElevation(0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("explain_verse_button")
            ) {
                Icon(
                    imageVector = Icons.Default.SelfImprovement,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Comprendre ce verset (IA)",
                    fontWeight = FontWeight.Bold
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

            // Highlight Color Palette
            Text(
                text = "Surligner le verset :",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val highlightOptions = listOf(
                    Triple("GOLD", "Or", BethelGold),
                    Triple("BLUE", "Azur", Color(0xFF64B5F6)),
                    Triple("GREEN", "Émeraude", Color(0xFF81C784)),
                    Triple("PURPLE", "Royauté", Color(0xFFBA68C8)),
                    Triple("TERRACOTTA", "Terre", BethelTerracotta)
                )

                highlightOptions.forEach { (colorCode, name, colorVal) ->
                    val isCurrent = verse.highlightColor == colorCode
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { onToggleHighlight(colorCode) }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(colorVal)
                                .then(if (isCurrent) Modifier.background(colorVal, CircleShape) else Modifier),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isCurrent) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = name, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                if (verse.highlightColor != null) {
                    IconButton(onClick = { onToggleHighlight(null) }) {
                        Icon(Icons.Default.Clear, contentDescription = "Retirer surlignage", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

            // Primary Action Buttons Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Méditer sur ce verset
                Button(
                    onClick = onMeditate,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("meditate_on_verse_button")
                ) {
                    Icon(Icons.Default.SelfImprovement, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Méditer", fontWeight = FontWeight.Bold)
                }

                // Collection
                OutlinedButton(
                    onClick = onAddToCollection,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = if (verse.isBookmarked) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = null,
                        tint = BethelGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (verse.isBookmarked) "Collection" else "Collection")
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Note personnelle
                OutlinedButton(
                    onClick = onOpenNote,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.EditNote, contentDescription = null, tint = BethelGold, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Ajouter Note")
                }

                // Partager Texte & Carte
                OutlinedButton(
                    onClick = onGenerateCard,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Partager")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

// -------------------------------------------------------------
// 8. VERSE CARD VISUALIZER & GENERATOR DIALOG
// -------------------------------------------------------------
@Composable
fun VerseCardShareDialog(
    verse: BibleVerseEntity,
    version: String,
    onDismiss: () -> Unit,
    onShare: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Carte de Verset Biblique",
                style = MaterialTheme.typography.titleLarge,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Sacred Card Canvas
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(BethelMidnightBackground, BethelMidnightSurface)
                            )
                        )
                        .padding(24.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Gold Emblem
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(BethelGold.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("†", color = BethelGold, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Verse Text
                        Text(
                            text = "« ${verse.text} »",
                            style = MaterialTheme.typography.bodyLarge,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Medium,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            lineHeight = 24.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = BethelGold.copy(alpha = 0.5f), thickness = 1.dp, modifier = Modifier.width(60.dp))
                        Spacer(modifier = Modifier.height(12.dp))

                        // Reference
                        Text(
                            text = "${verse.bookName} ${verse.chapter}:${verse.verse}",
                            style = MaterialTheme.typography.titleMedium,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            color = BethelGold
                        )

                        Text(
                            text = "Béthel+ • Maison de Dieu",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }
                }

                Text(
                    text = "Prêt à être partagé sur WhatsApp, Instagram, Telegram ou par message.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onShare,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.testTag("confirm_share_verse_card_button")
            ) {
                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Partager la Carte")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Fermer")
            }
        }
    )
}
