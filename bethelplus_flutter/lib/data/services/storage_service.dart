import 'dart:convert';
import 'package:shared_preferences/shared_preferences.dart';
import '../models/prayer_reminder.dart';
import '../models/prayer_journal.dart';
import '../models/memorized_verse.dart';
import '../models/spiritual_challenge.dart';
import '../models/spiritual_alarm.dart';
import '../models/user_profile.dart';
import '../models/prayer_session.dart';

class StorageService {
  static StorageService? _instance;
  late SharedPreferences _prefs;

  StorageService._();

  static Future<StorageService> getInstance() async {
    if (_instance == null) {
      _instance = StorageService._();
      await _instance!._init();
    }
    return _instance!;
  }

  Future<void> _init() async {
    _prefs = await SharedPreferences.getInstance();
    await _seedInitialDataIfNeeded();
  }

  // --- Initial Data Seeding ---
  Future<void> _seedInitialDataIfNeeded() async {
    final hasSeeded = _prefs.getBool('has_seeded_initial_data') ?? false;
    if (!hasSeeded) {
      // 1. Seed Profile
      if (!_prefs.containsKey('user_profile')) {
        await saveUserProfile(const UserProfile());
      }

      // 2. Seed Canonical Prayer Hours
      if (!_prefs.containsKey('prayer_reminders')) {
        final canonicalReminders = [
          const PrayerReminder(
            id: 1,
            title: "Prière de l'Aube (Matines)",
            hour: 6,
            minute: 0,
            isEnabled: true,
            soundscape: "HARPE",
            isVibrationEnabled: true,
            canonicalType: "AUBE",
          ),
          const PrayerReminder(
            id: 2,
            title: "Prière de Midi (Sexte)",
            hour: 12,
            minute: 0,
            isEnabled: true,
            soundscape: "BRISE",
            isVibrationEnabled: true,
            canonicalType: "MIDI",
          ),
          const PrayerReminder(
            id: 3,
            title: "Prière du Soir (Vêpres)",
            hour: 18,
            minute: 0,
            isEnabled: true,
            soundscape: "CLOCHE",
            isVibrationEnabled: true,
            canonicalType: "VESPERAL",
          ),
          const PrayerReminder(
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
        await saveReminders(canonicalReminders);
      }

      // 3. Seed Spiritual Alarm
      if (!_prefs.containsKey('spiritual_alarm')) {
        await saveSpiritualAlarm(
          const SpiritualAlarm(
            id: 1,
            hour: 5,
            minute: 30,
            isEnabled: false,
            soundscape: "HARPE",
          ),
        );
      }

      // 4. Seed Spiritual Challenges
      if (!_prefs.containsKey('spiritual_challenges')) {
        final initialChallenges = [
          const SpiritualChallenge(
            id: "challenge_7_secret",
            title: "7 Jours dans le Lieu Secret",
            subtitle: "Un parcours immersif pour cultiver l'intimité avec le Père",
            totalDays: 7,
            category: "Intimité",
            days: [
              ChallengeDay(
                dayNumber: 1,
                theme: "Fermer la porte au bruit du monde",
                scriptureRef: "Matthieu 6:6",
                scriptureText: "Mais quand tu pries, entre dans ta chambre, ferme ta porte...",
                reflection: "La première étape est de couper les distractions extérieures pour être présent à Dieu.",
                prayerPrompt: "Seigneur, aide-moi à faire silence et à me concentrer sur Ta seule présence.",
                actionItem: "Passez 5 minutes en silence total avant de prononcer un seul mot.",
                isCompleted: false,
              ),
              ChallengeDay(
                dayNumber: 2,
                theme: "L'Autel de l'Adoration",
                scriptureRef: "Jean 4:24",
                scriptureText: "Dieu est Esprit, et il faut que ceux qui l'adorent l'adorent en esprit et en vérité.",
                reflection: "L'adoration déplace notre regard de nos problèmes vers la majesté divine.",
                prayerPrompt: "Je T'adore Père pour qui Tu es, au-delà de ce que Tu fais.",
                actionItem: "Citez 5 attributs de Dieu et rendez-Lui gloire.",
                isCompleted: false,
              ),
              ChallengeDay(
                dayNumber: 3,
                theme: "L'Écoute attentive",
                scriptureRef: "1 Samuel 3:9",
                scriptureText: "Parle, Éternel, car ton serviteur écoute.",
                reflection: "La prière est un dialogue, pas un monologue.",
                prayerPrompt: "Seigneur, ouvre mes oreilles spirituelles pour entendre Ta voix douce.",
                actionItem: "Notez dans votre journal ce que vous ressentez que Dieu vous dit aujourd'hui.",
                isCompleted: false,
              ),
              ChallengeDay(
                dayNumber: 4,
                theme: "Le Pardon libérateur",
                scriptureRef: "Marc 11:25",
                scriptureText: "Et, lorsque vous êtes debout faisant votre prière, si vous avez quelque chose contre quelqu'un, pardonnez...",
                reflection: "Le pardon libère l'atmosphère de prière et guérit notre cœur.",
                prayerPrompt: "Père, je choisis de bénir et de pardonner à ceux qui m'ont blessé.",
                actionItem: "Priez nommément pour une personne avec qui vous avez eu un différend.",
                isCompleted: false,
              ),
              ChallengeDay(
                dayNumber: 5,
                theme: "L'Intercession d'Amour",
                scriptureRef: "1 Timothée 2:1",
                scriptureText: "J'exhorte donc, avant toutes choses, à faire des prières, des supplications, des requêtes, des actions de grâces...",
                reflection: "Se tenir à la brèche pour son prochain est le cœur de Christ.",
                prayerPrompt: "Seigneur, visite ma famille, mes amis et ma communauté.",
                actionItem: "Envoyez un message d'encouragement spirituel à un ami.",
                isCompleted: false,
              ),
              ChallengeDay(
                dayNumber: 6,
                theme: "La Proclamation de Foi",
                scriptureRef: "Romains 10:17",
                scriptureText: "Ainsi la foi vient de ce qu'on entend, et ce qu'on entend vient de la parole de Christ.",
                reflection: "Déclarez les promesses divines avec assurance sur votre vie.",
                prayerPrompt: "Seigneur, Ta Parole est une lampe à mes pieds et une vérité inébranlable.",
                actionItem: "Récitez à haute voix le Psaume 23 en vous l'appropriant personnellement.",
                isCompleted: false,
              ),
              ChallengeDay(
                dayNumber: 7,
                theme: "La Consécration Perpétuelle",
                scriptureRef: "Genèse 28:18",
                scriptureText: "Et Jacob se leva de bon matin; il prit la pierre... et il l'érigea en monument...",
                reflection: "Établissez un autel durable dans votre quotidien avec Béthel.",
                prayerPrompt: "Mon Dieu, je Te consacre mes journées et mon avenir.",
                actionItem: "Définissez une heure fixe quotidienne pour votre recueillement personnel.",
                isCompleted: false,
              ),
            ],
          ),
          SpiritualChallenge(
            id: "challenge_21_transformation",
            title: "21 Jours de Transformation Spirituelle",
            subtitle: "Ancrez une habitude sacrée de prière et de méditation biblique",
            totalDays: 21,
            category: "Discipline",
            days: List.generate(21, (index) {
              final dayNum = index + 1;
              return ChallengeDay(
                dayNumber: dayNum,
                theme: "Jour $dayNum : Renouvellement de l'Intelligence",
                scriptureRef: "Romains 12:2",
                scriptureText: "Soyez transformés par le renouvellement de l'esprit...",
                reflection: "Chaque jour passé avec le Seigneur façonne votre être intérieur à Son image.",
                prayerPrompt: "Seigneur, transforme mes pensées et aligne mes désirs sur les Tiens.",
                actionItem: "10 minutes de méditation et prière guidée dans Béthel+.",
                isCompleted: false,
              );
            }),
          ),
        ];
        await saveChallenges(initialChallenges);
      }

      // 5. Seed Initial Memorized Verses (Flashcards)
      final now = DateTime.now().millisecondsSinceEpoch;
      if (!_prefs.containsKey('memorized_verses')) {
        final initialFlashcards = [
          MemorizedVerse(
            id: "mem_jer_29_11",
            reference: "Jérémie 29:11",
            text: "Car je connais les projets que j'ai formés sur vous, dit l'Éternel, projets de paix et non de malheur, afin de vous donner un avenir et de l'espérance.",
            boxLevel: 1,
            category: "Espérance",
            nextReviewTimestamp: now,
            addedDate: now,
          ),
          MemorizedVerse(
            id: "mem_psa_23_1",
            reference: "Psaumes 23:1",
            text: "L'Éternel est mon berger: je ne manquerai de rien.",
            boxLevel: 2,
            category: "Confiance",
            nextReviewTimestamp: now,
            addedDate: now,
          ),
          MemorizedVerse(
            id: "mem_php_4_13",
            reference: "Philippiens 4:13",
            text: "Je puis tout par celui qui me fortifie.",
            boxLevel: 3,
            category: "Victoire",
            nextReviewTimestamp: now,
            addedDate: now,
          ),
          MemorizedVerse(
            id: "mem_jhn_3_16",
            reference: "Jean 3:16",
            text: "Car Dieu a tant aimé le monde qu'il a donné son Fils unique, afin que quiconque croit en lui ne périsse point, mais qu'il ait la vie éternelle.",
            boxLevel: 1,
            category: "Salut",
            nextReviewTimestamp: now,
            addedDate: now,
          ),
        ];
        await saveMemorizedVerses(initialFlashcards);
      }

      // 6. Seed Initial Journal Entries
      if (!_prefs.containsKey('prayer_journal')) {
        final initialJournal = [
          PrayerJournal(
            id: 1,
            title: "Remerciement pour la grâce du jour",
            content: "Seigneur, merci pour Ta paix qui surpasse toute intelligence et pour Ta fidélité dans ma famille.",
            category: "Gratitude",
            createdAt: now - 86400000,
            isAnswered: true,
            answeredAt: now,
            testimony: "Dieu a accordé une issue favorable et une paix profonde !",
          ),
        ];
        await saveJournalEntries(initialJournal);
      }

      await _prefs.setBool('has_seeded_initial_data', true);
    }
  }

  // --- User Profile ---
  UserProfile getUserProfile() {
    final jsonStr = _prefs.getString('user_profile');
    if (jsonStr == null) return const UserProfile();
    try {
      return UserProfile.fromJson(jsonDecode(jsonStr));
    } catch (_) {
      return const UserProfile();
    }
  }

  Future<void> saveUserProfile(UserProfile profile) async {
    await _prefs.setString('user_profile', jsonEncode(profile.toJson()));
  }

  // --- Prayer Reminders ---
  List<PrayerReminder> getReminders() {
    final jsonList = _prefs.getStringList('prayer_reminders');
    if (jsonList == null) return [];
    return jsonList.map((str) => PrayerReminder.fromJson(jsonDecode(str))).toList();
  }

  Future<void> saveReminders(List<PrayerReminder> reminders) async {
    final jsonList = reminders.map((r) => jsonEncode(r.toJson())).toList();
    await _prefs.setStringList('prayer_reminders', jsonList);
  }

  // --- Spiritual Alarm ---
  SpiritualAlarm getSpiritualAlarm() {
    final jsonStr = _prefs.getString('spiritual_alarm');
    if (jsonStr == null) return const SpiritualAlarm();
    try {
      return SpiritualAlarm.fromJson(jsonDecode(jsonStr));
    } catch (_) {
      return const SpiritualAlarm();
    }
  }

  Future<void> saveSpiritualAlarm(SpiritualAlarm alarm) async {
    await _prefs.setString('spiritual_alarm', jsonEncode(alarm.toJson()));
  }

  // --- Prayer Journal ---
  List<PrayerJournal> getJournalEntries() {
    final jsonList = _prefs.getStringList('prayer_journal');
    if (jsonList == null) return [];
    return jsonList.map((str) => PrayerJournal.fromJson(jsonDecode(str))).toList();
  }

  List<PrayerJournal> getPrayerJournal() => getJournalEntries();

  Future<void> saveJournalEntries(List<PrayerJournal> entries) async {
    final jsonList = entries.map((e) => jsonEncode(e.toJson())).toList();
    await _prefs.setStringList('prayer_journal', jsonList);
  }

  // --- Memorized Verses (Flashcards Leitner) ---
  List<MemorizedVerse> getMemorizedVerses() {
    final jsonList = _prefs.getStringList('memorized_verses');
    if (jsonList == null) return [];
    return jsonList.map((str) => MemorizedVerse.fromJson(jsonDecode(str))).toList();
  }

  Future<void> saveMemorizedVerses(List<MemorizedVerse> verses) async {
    final jsonList = verses.map((v) => jsonEncode(v.toJson())).toList();
    await _prefs.setStringList('memorized_verses', jsonList);
  }

  // --- Spiritual Challenges ---
  List<SpiritualChallenge> getChallenges() {
    final jsonList = _prefs.getStringList('spiritual_challenges');
    if (jsonList == null) return [];
    return jsonList.map((str) => SpiritualChallenge.fromJson(jsonDecode(str))).toList();
  }

  Future<void> saveChallenges(List<SpiritualChallenge> challenges) async {
    final jsonList = challenges.map((c) => jsonEncode(c.toJson())).toList();
    await _prefs.setStringList('spiritual_challenges', jsonList);
  }

  // --- Prayer Sessions History & Streak ---
  List<PrayerSession> getPrayerSessions() {
    final jsonList = _prefs.getStringList('prayer_sessions');
    if (jsonList == null) return [];
    return jsonList.map((str) => PrayerSession.fromJson(jsonDecode(str))).toList();
  }

  Future<void> recordPrayerSession(PrayerSession session) async {
    final list = getPrayerSessions();
    list.insert(0, session);
    final jsonList = list.map((s) => jsonEncode(s.toJson())).toList();
    await _prefs.setStringList('prayer_sessions', jsonList);
  }

  // --- Highlights, Bookmarks & Notes ---
  Map<String, String> getHighlightedVerses() {
    final jsonStr = _prefs.getString('highlighted_verses');
    if (jsonStr == null) return {};
    try {
      return Map<String, String>.from(jsonDecode(jsonStr));
    } catch (_) {
      return {};
    }
  }

  Future<void> saveHighlightedVerses(Map<String, String> highlights) async {
    await _prefs.setString('highlighted_verses', jsonEncode(highlights));
  }

  Set<String> getBookmarkedVerses() {
    final list = _prefs.getStringList('bookmarked_verses');
    return list?.toSet() ?? {};
  }

  Future<void> saveBookmarkedVerses(Set<String> bookmarks) async {
    await _prefs.setStringList('bookmarked_verses', bookmarks.toList());
  }

  Map<String, String> getVerseNotes() {
    final jsonStr = _prefs.getString('verse_notes');
    if (jsonStr == null) return {};
    try {
      return Map<String, String>.from(jsonDecode(jsonStr));
    } catch (_) {
      return {};
    }
  }

  Future<void> saveVerseNotes(Map<String, String> notes) async {
    await _prefs.setString('verse_notes', jsonEncode(notes));
  }
}
