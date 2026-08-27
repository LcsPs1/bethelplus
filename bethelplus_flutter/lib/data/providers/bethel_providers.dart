import 'dart:async';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:intl/intl.dart';

import '../models/bible_data.dart';
import '../models/bible_verse.dart';
import '../models/meditation_item.dart';
import '../models/memorized_verse.dart';
import '../models/prayer_journal.dart';
import '../models/prayer_reminder.dart';
import '../models/prayer_session.dart';
import '../models/quiz.dart';
import '../models/spiritual_alarm.dart';
import '../models/spiritual_challenge.dart';
import '../models/spiritual_stats.dart';
import '../models/user_profile.dart';

import '../services/storage_service.dart';
import '../services/gemini_service.dart';
import '../services/tts_service.dart';
import '../services/audio_service.dart';
import '../services/notification_service.dart';
import '../services/icloud_service.dart';
import '../services/supabase_service.dart';
import '../repositories/bible_repository.dart';

// --- Base Services Providers ---
final bibleRepositoryProvider = Provider<BibleRepository>((ref) => BibleRepository.instance);
final storageServiceProvider = Provider<StorageService>((ref) {
  throw UnimplementedError('StorageService must be overridden in main()');
});

final geminiServiceProvider = Provider<GeminiService>((ref) => GeminiService());
final ttsServiceProvider = Provider<TtsService>((ref) => TtsService());
final audioServiceProvider = Provider<AudioService>((ref) => AudioService.instance);
final notificationServiceProvider = Provider<NotificationService>((ref) => NotificationService.instance);
final icloudServiceProvider = Provider<ICloudService>((ref) => ICloudService.instance);
final supabaseServiceProvider = Provider<SupabaseService>((ref) => SupabaseService.instance);

// ==========================================
// 1. USER PROFILE & THEME STATE
// ==========================================
class UserProfileNotifier extends StateNotifier<UserProfile> {
  final StorageService _storage;

  UserProfileNotifier(this._storage) : super(_storage.getUserProfile());

  Future<void> updateProfile(UserProfile profile) async {
    state = profile;
    await _storage.saveUserProfile(profile);
  }

  Future<void> setThemeMode(String mode) async {
    final updated = state.copyWith(themeMode: mode);
    await updateProfile(updated);
  }

  Future<void> setDailyGoalMinutes(int minutes) async {
    final updated = state.copyWith(dailyGoalMinutes: minutes);
    await updateProfile(updated);
  }

  Future<void> toggleAutoDnd(bool enabled) async {
    final updated = state.copyWith(isAutoDndEnabled: enabled);
    await updateProfile(updated);
  }

  Future<void> syncWithCloud() async {
    final now = DateTime.now().millisecondsSinceEpoch;
    final updated = state.copyWith(
      lastBackupTimestamp: now,
      isGoogleConnected: true,
    );
    await updateProfile(updated);
  }
}

final userProfileProvider = StateNotifierProvider<UserProfileNotifier, UserProfile>((ref) {
  final storage = ref.watch(storageServiceProvider);
  return UserProfileNotifier(storage);
});

// ==========================================
// 2. PRAYER SESSIONS & SPIRITUAL STATS
// ==========================================
class PrayerSessionsNotifier extends StateNotifier<List<PrayerSession>> {
  final StorageService _storage;

  PrayerSessionsNotifier(this._storage) : super(_storage.getPrayerSessions());

  Future<void> addSession(PrayerSession session) async {
    await _storage.recordPrayerSession(session);
    state = _storage.getPrayerSessions();
  }
}

final prayerSessionsProvider = StateNotifierProvider<PrayerSessionsNotifier, List<PrayerSession>>((ref) {
  final storage = ref.watch(storageServiceProvider);
  return PrayerSessionsNotifier(storage);
});

final spiritualStatsProvider = Provider<SpiritualStats>((ref) {
  final sessions = ref.watch(prayerSessionsProvider);

  if (sessions.isEmpty) {
    return SpiritualStats(
      currentStreak: 0,
      totalMinutes: 0,
      totalSessions: 0,
      hasPrayedToday: false,
      weeklyHeatmap: _generateDefaultHeatmap(),
      unlockedBadges: _calculateBadges(0, 0, 0),
    );
  }

  final totalSeconds = sessions.fold<int>(0, (sum, s) => sum + s.durationSeconds);
  final totalMinutes = totalSeconds ~/ 60;
  final totalSessions = sessions.length;

  // Calculate consecutive streak
  final dateFormat = DateFormat('yyyy-MM-dd');
  final prayedDates = sessions.map((s) => dateFormat.format(s.createdAt)).toSet();

  final today = DateTime.now();
  final todayStr = dateFormat.format(today);
  final hasPrayedToday = prayedDates.contains(todayStr);

  int streak = 0;
  var checkDate = hasPrayedToday ? today : today.subtract(const Duration(days: 1));

  while (true) {
    final dateStr = dateFormat.format(checkDate);
    if (prayedDates.contains(dateStr)) {
      streak++;
      checkDate = checkDate.subtract(const Duration(days: 1));
    } else {
      break;
    }
  }

  // Weekly heatmap (7 days)
  final heatmap = <DayActivity>[];
  final dayNameFormat = DateFormat('EEE', 'fr');

  for (int i = 6; i >= 0; i--) {
    final date = today.subtract(Duration(days: i));
    final dateStr = dateFormat.format(date);
    final daySessions = sessions.where((s) => dateFormat.format(s.createdAt) == dateStr).toList();
    final dayMins = daySessions.fold<int>(0, (sum, s) => sum + s.durationSeconds) ~/ 60;

    heatmap.add(
      DayActivity(
        dayName: dayNameFormat.format(date).toUpperCase().substring(0, 3),
        dayOfMonth: date.day,
        isToday: i == 0,
        sessionCount: daySessions.length,
        totalMinutes: dayMins,
      ),
    );
  }

  return SpiritualStats(
    currentStreak: streak,
    totalMinutes: totalMinutes,
    totalSessions: totalSessions,
    hasPrayedToday: hasPrayedToday,
    weeklyHeatmap: heatmap,
    unlockedBadges: _calculateBadges(streak, totalMinutes, totalSessions),
  );
});

List<DayActivity> _generateDefaultHeatmap() {
  final heatmap = <DayActivity>[];
  final dayNameFormat = DateFormat('EEE', 'fr');
  final today = DateTime.now();

  for (int i = 6; i >= 0; i--) {
    final date = today.subtract(Duration(days: i));
    heatmap.add(
      DayActivity(
        dayName: dayNameFormat.format(date).toUpperCase().substring(0, 3),
        dayOfMonth: date.day,
        isToday: i == 0,
        sessionCount: 0,
        totalMinutes: 0,
      ),
    );
  }
  return heatmap;
}

List<SpiritualBadge> _calculateBadges(int streak, int totalMinutes, int totalSessions) {
  return [
    SpiritualBadge(
      id: 'badge_first_altar',
      title: 'Premier Autel',
      subtitle: 'Première prière à Béthel',
      icon: 'temple',
      isUnlocked: totalSessions >= 1,
    ),
    SpiritualBadge(
      id: 'badge_3_streak',
      title: 'Cœur Constant',
      subtitle: '3 jours consécutifs de prière',
      icon: 'fire',
      isUnlocked: streak >= 3,
    ),
    SpiritualBadge(
      id: 'badge_7_streak',
      title: 'Veilleur de Béthel',
      subtitle: '7 jours d\'affilée dans Sa présence',
      icon: 'shield',
      isUnlocked: streak >= 7,
    ),
    SpiritualBadge(
      id: 'badge_hour',
      title: 'Heure Sacrée',
      subtitle: 'Plus de 60 minutes de recueillement',
      icon: 'hourglass',
      isUnlocked: totalMinutes >= 60,
    ),
    SpiritualBadge(
      id: 'badge_master',
      title: 'Porte des Cieux',
      subtitle: '10 sessions de méditation accomplies',
      icon: 'sparkles',
      isUnlocked: totalSessions >= 10,
    ),
  ];
}

// ==========================================
// 3. PRAYER REMINDERS & ALARM
// ==========================================
class RemindersNotifier extends StateNotifier<List<PrayerReminder>> {
  final StorageService _storage;

  RemindersNotifier(this._storage) : super(_storage.getReminders());

  Future<void> toggleReminder(int id, bool enabled) async {
    final updated = state.map((r) {
      if (r.id == id) {
        return r.copyWith(isEnabled: enabled);
      }
      return r;
    }).toList();
    state = updated;
    await _storage.saveReminders(updated);
  }

  Future<void> saveReminder(PrayerReminder reminder) async {
    final index = state.indexWhere((r) => r.id == reminder.id);
    List<PrayerReminder> updated;
    if (index >= 0) {
      updated = List.from(state)..[index] = reminder;
    } else {
      updated = List.from(state)..add(reminder);
    }
    state = updated;
    await _storage.saveReminders(updated);
  }

  Future<void> deleteReminder(int id) async {
    final updated = state.where((r) => r.id != id).toList();
    state = updated;
    await _storage.saveReminders(updated);
  }

  Future<void> restoreCanonicalReminders() async {
    const canonicalReminders = [
      PrayerReminder(
        id: 1,
        title: "Prière de l'Aube (Matines)",
        hour: 6,
        minute: 0,
        isEnabled: true,
        soundscape: "HARPE",
        isVibrationEnabled: true,
        canonicalType: "AUBE",
      ),
      PrayerReminder(
        id: 2,
        title: "Prière de Midi (Sexte)",
        hour: 12,
        minute: 0,
        isEnabled: true,
        soundscape: "BRISE",
        isVibrationEnabled: true,
        canonicalType: "MIDI",
      ),
      PrayerReminder(
        id: 3,
        title: "Prière du Soir (Vêpres)",
        hour: 18,
        minute: 0,
        isEnabled: true,
        soundscape: "CLOCHE",
        isVibrationEnabled: true,
        canonicalType: "VESPERAL",
      ),
      PrayerReminder(
        id: 4,
        title: "Prière de la Nuit (Complies)",
        hour: 21,
        minute: 30,
        isEnabled: true,
        soundscape: "PLUIE",
        isVibrationEnabled: true,
        canonicalType: "COMPLIES",
      ),
    ];
    state = canonicalReminders;
    await _storage.saveReminders(canonicalReminders);
  }
}

final remindersProvider = StateNotifierProvider<RemindersNotifier, List<PrayerReminder>>((ref) {
  final storage = ref.watch(storageServiceProvider);
  return RemindersNotifier(storage);
});

class SpiritualAlarmNotifier extends StateNotifier<SpiritualAlarm> {
  final StorageService _storage;

  SpiritualAlarmNotifier(this._storage) : super(_storage.getSpiritualAlarm());

  Future<void> saveAlarm(SpiritualAlarm alarm) async {
    state = alarm;
    await _storage.saveSpiritualAlarm(alarm);
  }

  Future<void> toggleAlarm(bool enabled) async {
    final updated = state.copyWith(isEnabled: enabled);
    state = updated;
    await _storage.saveSpiritualAlarm(updated);
  }
}

final spiritualAlarmProvider = StateNotifierProvider<SpiritualAlarmNotifier, SpiritualAlarm>((ref) {
  final storage = ref.watch(storageServiceProvider);
  return SpiritualAlarmNotifier(storage);
});

// ==========================================
// 4. PRAYER JOURNAL
// ==========================================
class JournalNotifier extends StateNotifier<List<PrayerJournal>> {
  final StorageService _storage;

  JournalNotifier(this._storage) : super(_storage.getJournalEntries());

  Future<void> addEntry({
    required String title,
    required String content,
    String category = "Prière",
  }) async {
    final entry = PrayerJournal(
      id: DateTime.now().millisecondsSinceEpoch % 1000000,
      title: title,
      content: content,
      category: category,
      createdAt: DateTime.now().millisecondsSinceEpoch,
    );
    final updated = [entry, ...state];
    state = updated;
    await _storage.saveJournalEntries(updated);
  }

  Future<void> markAnswered(int id, String testimony) async {
    final updated = state.map((e) {
      if (e.id == id) {
        return e.copyWith(
          isAnswered: true,
          answeredAt: DateTime.now().millisecondsSinceEpoch,
          testimony: testimony,
        );
      }
      return e;
    }).toList();
    state = updated;
    await _storage.saveJournalEntries(updated);
  }

  Future<void> deleteEntry(int id) async {
    final updated = state.where((e) => e.id != id).toList();
    state = updated;
    await _storage.saveJournalEntries(updated);
  }
}

final journalProvider = StateNotifierProvider<JournalNotifier, List<PrayerJournal>>((ref) {
  final storage = ref.watch(storageServiceProvider);
  return JournalNotifier(storage);
});

// ==========================================
// 5. FLASHCARDS & LEITNER MEMORIZATION
// ==========================================
class MemorizedVersesNotifier extends StateNotifier<List<MemorizedVerse>> {
  final StorageService _storage;

  MemorizedVersesNotifier(this._storage) : super(_storage.getMemorizedVerses());

  Future<void> addVerse({
    required String reference,
    required String text,
    String category = "Général",
  }) async {
    final now = DateTime.now().millisecondsSinceEpoch;
    final newVerse = MemorizedVerse(
      id: "mem_$now",
      reference: reference,
      text: text,
      category: category,
      boxLevel: 1,
      nextReviewTimestamp: now,
      addedDate: now,
    );
    final updated = [...state, newVerse];
    state = updated;
    await _storage.saveMemorizedVerses(updated);
  }

  Future<void> recordReview(String id, bool isKnown) async {
    final updated = state.map((v) {
      if (v.id == id) {
        final newBox = isKnown ? (v.box < 5 ? v.box + 1 : 5) : 1;
        return v.copyWith(
          box: newBox,
          lastReviewedTimestamp: DateTime.now().millisecondsSinceEpoch,
          reviewCount: v.reviewCount + 1,
        );
      }
      return v;
    }).toList();
    state = updated;
    await _storage.saveMemorizedVerses(updated);
  }

  Future<void> deleteVerse(String id) async {
    final updated = state.where((v) => v.id != id).toList();
    state = updated;
    await _storage.saveMemorizedVerses(updated);
  }
}

final memorizedVersesProvider = StateNotifierProvider<MemorizedVersesNotifier, List<MemorizedVerse>>((ref) {
  final storage = ref.watch(storageServiceProvider);
  return MemorizedVersesNotifier(storage);
});

// ==========================================
// 6. SPIRITUAL CHALLENGES
// ==========================================
class ChallengesNotifier extends StateNotifier<List<SpiritualChallenge>> {
  final StorageService _storage;

  ChallengesNotifier(this._storage) : super(_storage.getChallenges());

  Future<void> toggleDay(String challengeId, int dayNumber) async {
    final updated = state.map((c) {
      if (c.id == challengeId) {
        final updatedDays = c.days.map((d) {
          if (d.dayNumber == dayNumber) {
            return d.copyWith(isCompleted: !d.isCompleted);
          }
          return d;
        }).toList();
        return c.copyWith(days: updatedDays);
      }
      return c;
    }).toList();
    state = updated;
    await _storage.saveChallenges(updated);
  }
}

final challengesProvider = StateNotifierProvider<ChallengesNotifier, List<SpiritualChallenge>>((ref) {
  final storage = ref.watch(storageServiceProvider);
  return ChallengesNotifier(storage);
});

// ==========================================
// 7. BIBLE READER STATE
// ==========================================
class BibleState {
  final String version; // "LSG" or "DRB"
  final BibleBookInfo selectedBook;
  final int selectedChapter;
  final List<BibleVerse> verses;
  final Map<String, String> highlightedVerses; // verseId -> colorHex
  final Set<String> bookmarkedVerses; // set of verseIds
  final Map<String, String> verseNotes; // verseId -> note
  final bool isSearching;
  final String searchQuery;
  final List<BibleVerse> searchResults;
  final bool isTtsPlaying;
  final bool isTtsPaused;
  final int currentTtsVerseIndex;
  final double ttsRate;

  const BibleState({
    this.version = "LSG",
    required this.selectedBook,
    this.selectedChapter = 1,
    this.verses = const [],
    this.highlightedVerses = const {},
    this.bookmarkedVerses = const {},
    this.verseNotes = const {},
    this.isSearching = false,
    this.searchQuery = "",
    this.searchResults = const [],
    this.isTtsPlaying = false,
    this.isTtsPaused = false,
    this.currentTtsVerseIndex = 0,
    this.ttsRate = 0.5,
  });

  BibleState copyWith({
    String? version,
    BibleBookInfo? selectedBook,
    int? selectedChapter,
    List<BibleVerse>? verses,
    Map<String, String>? highlightedVerses,
    Set<String>? bookmarkedVerses,
    Map<String, String>? verseNotes,
    bool? isSearching,
    String? searchQuery,
    List<BibleVerse>? searchResults,
    bool? isTtsPlaying,
    bool? isTtsPaused,
    int? currentTtsVerseIndex,
    double? ttsRate,
  }) {
    return BibleState(
      version: version ?? this.version,
      selectedBook: selectedBook ?? this.selectedBook,
      selectedChapter: selectedChapter ?? this.selectedChapter,
      verses: verses ?? this.verses,
      highlightedVerses: highlightedVerses ?? this.highlightedVerses,
      bookmarkedVerses: bookmarkedVerses ?? this.bookmarkedVerses,
      verseNotes: verseNotes ?? this.verseNotes,
      isSearching: isSearching ?? this.isSearching,
      searchQuery: searchQuery ?? this.searchQuery,
      searchResults: searchResults ?? this.searchResults,
      isTtsPlaying: isTtsPlaying ?? this.isTtsPlaying,
      isTtsPaused: isTtsPaused ?? this.isTtsPaused,
      currentTtsVerseIndex: currentTtsVerseIndex ?? this.currentTtsVerseIndex,
      ttsRate: ttsRate ?? this.ttsRate,
    );
  }
}

class BibleNotifier extends StateNotifier<BibleState> {
  final StorageService _storage;
  final TtsService _tts;
  final BibleRepository _repo;

  BibleNotifier(this._storage, this._tts, this._repo)
      : super(
          BibleState(
            selectedBook: BibleData.allBooks.firstWhere((b) => b.id == "GEN"),
            highlightedVerses: _storage.getHighlightedVerses(),
            bookmarkedVerses: _storage.getBookmarkedVerses(),
            verseNotes: _storage.getVerseNotes(),
          ),
        ) {
    loadChapter(state.selectedBook.id, state.selectedChapter);
    _initTtsCallbacks();
  }

  void _initTtsCallbacks() {
    _tts.onCompleted = () {
      if (state.isTtsPlaying && state.currentTtsVerseIndex < state.verses.length - 1) {
        nextTtsVerse();
      } else {
        stopTts();
      }
    };
  }

  Future<void> loadChapter(String bookId, int chapter) async {
    final book = BibleData.allBooks.firstWhere((b) => b.id == bookId, orElse: () => BibleData.allBooks.first);
    final clampedChapter = chapter.clamp(1, book.chaptersCount);
    final verses = await _repo.getChapterVerses(bookId: book.id, chapter: clampedChapter, version: state.version);

    state = state.copyWith(
      selectedBook: book,
      selectedChapter: clampedChapter,
      verses: verses,
      currentTtsVerseIndex: 0,
      isTtsPlaying: false,
      isTtsPaused: false,
    );
  }

  Future<void> setVersion(String version) async {
    if (state.version == version) return;
    final verses = await _repo.getChapterVerses(bookId: state.selectedBook.id, chapter: state.selectedChapter, version: version);
    state = state.copyWith(version: version, verses: verses);
  }

  void nextChapter() {
    if (state.selectedChapter < state.selectedBook.chaptersCount) {
      loadChapter(state.selectedBook.id, state.selectedChapter + 1);
    } else {
      final currentIndex = BibleData.allBooks.indexOf(state.selectedBook);
      if (currentIndex < BibleData.allBooks.length - 1) {
        final nextBook = BibleData.allBooks[currentIndex + 1];
        loadChapter(nextBook.id, 1);
      }
    }
  }

  void previousChapter() {
    if (state.selectedChapter > 1) {
      loadChapter(state.selectedBook.id, state.selectedChapter - 1);
    } else {
      final currentIndex = BibleData.allBooks.indexOf(state.selectedBook);
      if (currentIndex > 0) {
        final prevBook = BibleData.allBooks[currentIndex - 1];
        loadChapter(prevBook.id, prevBook.chaptersCount);
      }
    }
  }

  Future<void> toggleHighlight(String verseId, String colorHex) async {
    final highlights = Map<String, String>.from(state.highlightedVerses);
    if (highlights[verseId] == colorHex) {
      highlights.remove(verseId);
    } else {
      highlights[verseId] = colorHex;
    }
    state = state.copyWith(highlightedVerses: highlights);
    await _storage.saveHighlightedVerses(highlights);
  }

  Future<void> toggleBookmark(String verseId) async {
    final bookmarks = Set<String>.from(state.bookmarkedVerses);
    if (bookmarks.contains(verseId)) {
      bookmarks.remove(verseId);
    } else {
      bookmarks.add(verseId);
    }
    state = state.copyWith(bookmarkedVerses: bookmarks);
    await _storage.saveBookmarkedVerses(bookmarks);
  }

  Future<void> saveNote(String verseId, String note) async {
    final notes = Map<String, String>.from(state.verseNotes);
    if (note.trim().isEmpty) {
      notes.remove(verseId);
    } else {
      notes[verseId] = note;
    }
    state = state.copyWith(verseNotes: notes);
    await _storage.saveVerseNotes(notes);
  }

  Future<void> searchBible(String query) async {
    if (query.trim().isEmpty) {
      state = state.copyWith(isSearching: false, searchQuery: "", searchResults: []);
      return;
    }

    final results = await _repo.searchVerses(query: query, version: state.version);
    state = state.copyWith(isSearching: true, searchQuery: query, searchResults: results);
  }

  // --- TTS Audio Controls ---
  void startTts({int startIndex = 0}) {
    if (state.verses.isEmpty) return;
    final index = startIndex.clamp(0, state.verses.length - 1);
    final verse = state.verses[index];
    state = state.copyWith(
      isTtsPlaying: true,
      isTtsPaused: false,
      currentTtsVerseIndex: index,
    );
    _tts.speak("Verset ${verse.verse}. ${verse.text}");
  }

  void toggleTtsPlayPause() {
    if (state.isTtsPlaying) {
      _tts.pause();
      state = state.copyWith(isTtsPlaying: false, isTtsPaused: true);
    } else if (state.isTtsPaused) {
      final verse = state.verses[state.currentTtsVerseIndex];
      state = state.copyWith(isTtsPlaying: true, isTtsPaused: false);
      _tts.speak("Verset ${verse.verse}. ${verse.text}");
    } else {
      startTts(startIndex: 0);
    }
  }

  void nextTtsVerse() {
    if (state.currentTtsVerseIndex < state.verses.length - 1) {
      startTts(startIndex: state.currentTtsVerseIndex + 1);
    } else {
      stopTts();
    }
  }

  void previousTtsVerse() {
    if (state.currentTtsVerseIndex > 0) {
      startTts(startIndex: state.currentTtsVerseIndex - 1);
    }
  }

  void stopTts() {
    _tts.stop();
    state = state.copyWith(isTtsPlaying: false, isTtsPaused: false);
  }

  void setTtsSpeed(double speed) {
    _tts.setRate(speed);
    state = state.copyWith(ttsRate: speed);
  }
}

final bibleProvider = StateNotifierProvider<BibleNotifier, BibleState>((ref) {
  final storage = ref.watch(storageServiceProvider);
  final tts = ref.watch(ttsServiceProvider);
  final repo = ref.watch(bibleRepositoryProvider);
  return BibleNotifier(storage, tts, repo);
});

// ==========================================
// 8. MEDITATION & PRAYER TIMER STATE
// ==========================================
class MeditationState {
  final MeditationItem? activeMeditation;
  final int timerTotalSeconds;
  final int timerRemainingSeconds;
  final bool isRunning;
  final String activeSoundscape; // "HARPE", "PLUIE", "BRISE", "CLOCHE", "SILENCE"
  final bool isCompleted;

  const MeditationState({
    this.activeMeditation,
    this.timerTotalSeconds = 0,
    this.timerRemainingSeconds = 0,
    this.isRunning = false,
    this.activeSoundscape = "HARPE",
    this.isCompleted = false,
  });

  MeditationState copyWith({
    MeditationItem? activeMeditation,
    int? timerTotalSeconds,
    int? timerRemainingSeconds,
    bool? isRunning,
    String? activeSoundscape,
    bool? isCompleted,
  }) {
    return MeditationState(
      activeMeditation: activeMeditation ?? this.activeMeditation,
      timerTotalSeconds: timerTotalSeconds ?? this.timerTotalSeconds,
      timerRemainingSeconds: timerRemainingSeconds ?? this.timerRemainingSeconds,
      isRunning: isRunning ?? this.isRunning,
      activeSoundscape: activeSoundscape ?? this.activeSoundscape,
      isCompleted: isCompleted ?? this.isCompleted,
    );
  }
}

class MeditationNotifier extends StateNotifier<MeditationState> {
  final AudioService _audio;
  final Ref _ref;
  Timer? _timer;

  MeditationNotifier(this._audio, this._ref) : super(const MeditationState());

  void startMeditation(MeditationItem? item, {int durationMinutes = 5}) {
    final totalSecs = (item?.estimatedDurationMinutes ?? durationMinutes) * 60;
    state = state.copyWith(
      activeMeditation: item,
      timerTotalSeconds: totalSecs,
      timerRemainingSeconds: totalSecs,
      isRunning: true,
      isCompleted: false,
    );
    _audio.startAmbient(state.activeSoundscape);
    _startTimer();
  }

  void _startTimer() {
    _timer?.cancel();
    _timer = Timer.periodic(const Duration(seconds: 1), (t) {
      if (!state.isRunning) return;
      if (state.timerRemainingSeconds > 1) {
        state = state.copyWith(timerRemainingSeconds: state.timerRemainingSeconds - 1);
      } else {
        completeSession();
      }
    });
  }

  void togglePlayPause() {
    if (state.isRunning) {
      state = state.copyWith(isRunning: false);
      _audio.stopAmbient();
    } else {
      state = state.copyWith(isRunning: true);
      _audio.startAmbient(state.activeSoundscape);
    }
  }

  void addMinutes(int minutes) {
    final addSec = minutes * 60;
    state = state.copyWith(
      timerTotalSeconds: state.timerTotalSeconds + addSec,
      timerRemainingSeconds: state.timerRemainingSeconds + addSec,
    );
  }

  void setSoundscape(String soundType) {
    state = state.copyWith(activeSoundscape: soundType);
    if (state.isRunning) {
      _audio.startAmbient(soundType);
    }
  }

  void completeSession() {
    _timer?.cancel();
    _audio.stopAmbient();
    _audio.playSanctuaryBell();

    final spentSeconds = state.timerTotalSeconds - state.timerRemainingSeconds;
    final duration = spentSeconds < 10 ? state.timerTotalSeconds : spentSeconds;

    final session = PrayerSession(
      id: DateTime.now().millisecondsSinceEpoch % 1000000,
      title: state.activeMeditation?.title ?? "Temps de Prière Libre",
      durationSeconds: duration,
      dateMillis: DateTime.now().millisecondsSinceEpoch,
      meditationId: state.activeMeditation?.id,
    );

    _ref.read(prayerSessionsProvider.notifier).addSession(session);

    state = state.copyWith(
      isRunning: false,
      timerRemainingSeconds: 0,
      isCompleted: true,
    );
  }

  void dismissCompleted() {
    state = state.copyWith(
      isCompleted: false,
      activeMeditation: null,
      timerTotalSeconds: 0,
      timerRemainingSeconds: 0,
    );
  }

  void cancelSession() {
    _timer?.cancel();
    _audio.stopAmbient();
    state = const MeditationState();
  }
}

final meditationProvider = StateNotifierProvider<MeditationNotifier, MeditationState>((ref) {
  final audio = ref.watch(audioServiceProvider);
  return MeditationNotifier(audio, ref);
});

// ==========================================
// 9. AI BIBLE QUIZ STATE
// ==========================================
class QuizState {
  final bool isLoading;
  final List<QuizQuestion> questions;
  final int currentIndex;
  final int? selectedAnswerIndex;
  final int score;
  final bool isFinished;
  final String? errorMessage;

  const QuizState({
    this.isLoading = false,
    this.questions = const [],
    this.currentIndex = 0,
    this.selectedAnswerIndex,
    this.score = 0,
    this.isFinished = false,
    this.errorMessage,
  });

  QuizState copyWith({
    bool? isLoading,
    List<QuizQuestion>? questions,
    int? currentIndex,
    int? selectedAnswerIndex,
    int? score,
    bool? isFinished,
    String? errorMessage,
  }) {
    return QuizState(
      isLoading: isLoading ?? this.isLoading,
      questions: questions ?? this.questions,
      currentIndex: currentIndex ?? this.currentIndex,
      selectedAnswerIndex: selectedAnswerIndex,
      score: score ?? this.score,
      isFinished: isFinished ?? this.isFinished,
      errorMessage: errorMessage,
    );
  }
}

class QuizNotifier extends StateNotifier<QuizState> {
  final GeminiService _gemini;

  QuizNotifier(this._gemini) : super(const QuizState());

  Future<void> startQuiz({String topic = "Général"}) async {
    state = state.copyWith(
      isLoading: true,
      questions: [],
      currentIndex: 0,
      selectedAnswerIndex: null,
      score: 0,
      isFinished: false,
      errorMessage: null,
    );

    try {
      final questions = await _gemini.generateQuiz(topic: topic);
      state = state.copyWith(
        isLoading: false,
        questions: questions,
      );
    } catch (e) {
      state = state.copyWith(
        isLoading: false,
        errorMessage: "Erreur lors du chargement du quiz.",
      );
    }
  }

  void submitAnswer(int index) {
    if (state.selectedAnswerIndex != null || state.isFinished) return;

    final currentQuestion = state.questions[state.currentIndex];
    final isCorrect = index == currentQuestion.correctAnswerIndex;

    state = state.copyWith(
      selectedAnswerIndex: index,
      score: isCorrect ? state.score + 1 : state.score,
    );
  }

  void nextQuestion() {
    if (state.currentIndex < state.questions.length - 1) {
      state = state.copyWith(
        currentIndex: state.currentIndex + 1,
        selectedAnswerIndex: null,
      );
    } else {
      state = state.copyWith(isFinished: true);
    }
  }
}

final quizProvider = StateNotifierProvider<QuizNotifier, QuizState>((ref) {
  final gemini = ref.watch(geminiServiceProvider);
  return QuizNotifier(gemini);
});

// ==========================================
// 10. CLOUD & ICLOUD SYNC STATE
// ==========================================
class CloudSyncState {
  final bool isSyncing;
  final bool isICloudSyncing;
  final DateTime? lastSyncTime;
  final DateTime? lastICloudBackupTime;
  final String? message;
  final bool isSuccess;

  const CloudSyncState({
    this.isSyncing = false,
    this.isICloudSyncing = false,
    this.lastSyncTime,
    this.lastICloudBackupTime,
    this.message,
    this.isSuccess = false,
  });

  CloudSyncState copyWith({
    bool? isSyncing,
    bool? isICloudSyncing,
    DateTime? lastSyncTime,
    DateTime? lastICloudBackupTime,
    String? message,
    bool? isSuccess,
  }) {
    return CloudSyncState(
      isSyncing: isSyncing ?? this.isSyncing,
      isICloudSyncing: isICloudSyncing ?? this.isICloudSyncing,
      lastSyncTime: lastSyncTime ?? this.lastSyncTime,
      lastICloudBackupTime: lastICloudBackupTime ?? this.lastICloudBackupTime,
      message: message,
      isSuccess: isSuccess ?? this.isSuccess,
    );
  }
}

class CloudSyncNotifier extends StateNotifier<CloudSyncState> {
  final StorageService _storage;
  final ICloudService _icloud;
  final SupabaseService _supabase;

  CloudSyncNotifier(this._storage, this._icloud, this._supabase)
      : super(const CloudSyncState()) {
    _loadInitialBackupInfo();
  }

  Future<void> _loadInitialBackupInfo() async {
    final info = await _icloud.getLatestBackupInfo();
    if (info != null) {
      state = state.copyWith(lastICloudBackupTime: info.createdAt);
    }
  }

  Future<void> backupToICloud() async {
    state = state.copyWith(isICloudSyncing: true, message: null);
    try {
      final info = await _icloud.exportToICloud(_storage);
      state = state.copyWith(
        isICloudSyncing: false,
        lastICloudBackupTime: info.createdAt,
        isSuccess: true,
        message: "Sauvegarde iCloud réussie (${info.totalPrayers} prières, ${info.totalSessions} sessions)",
      );
    } catch (_) {
      state = state.copyWith(
        isICloudSyncing: false,
        isSuccess: false,
        message: "Échec de la sauvegarde iCloud",
      );
    }
  }

  Future<void> restoreFromICloud({String? customJson}) async {
    state = state.copyWith(isICloudSyncing: true, message: null);
    try {
      final success = await _icloud.importFromICloud(_storage, customJson: customJson);
      state = state.copyWith(
        isICloudSyncing: false,
        isSuccess: success,
        message: success ? "Restauration iCloud effectuée !" : "Aucune sauvegarde iCloud trouvée",
      );
    } catch (_) {
      state = state.copyWith(
        isICloudSyncing: false,
        isSuccess: false,
        message: "Erreur lors de la restauration",
      );
    }
  }

  Future<void> syncSupabase() async {
    state = state.copyWith(isSyncing: true, message: null);
    try {
      final pushed = await _supabase.pushLocalDataToCloud(_storage);
      final pulled = await _supabase.pullDataFromCloud(_storage);
      final ok = pushed && pulled;
      state = state.copyWith(
        isSyncing: false,
        lastSyncTime: DateTime.now(),
        isSuccess: ok,
        message: ok ? "Synchronisation Cloud réussie !" : "Synchronisation partielle",
      );
    } catch (_) {
      state = state.copyWith(
        isSyncing: false,
        isSuccess: false,
        message: "Erreur de connexion au serveur",
      );
    }
  }
}

final cloudSyncProvider = StateNotifierProvider<CloudSyncNotifier, CloudSyncState>((ref) {
  final storage = ref.watch(storageServiceProvider);
  final icloud = ref.watch(icloudServiceProvider);
  final supabase = ref.watch(supabaseServiceProvider);
  return CloudSyncNotifier(storage, icloud, supabase);
});
