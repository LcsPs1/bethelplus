import 'dart:convert';
import 'dart:io';
import 'package:path_provider/path_provider.dart';
import 'storage_service.dart';

class ICloudBackupInfo {
  final DateTime createdAt;
  final int totalPrayers;
  final int totalSessions;
  final int totalVerses;
  final int streakDays;
  final int fileSizeBytes;
  final String filePath;

  const ICloudBackupInfo({
    required this.createdAt,
    required this.totalPrayers,
    required this.totalSessions,
    required this.totalVerses,
    required this.streakDays,
    required this.fileSizeBytes,
    required this.filePath,
  });
}

class ICloudService {
  static final ICloudService _instance = ICloudService._();
  static ICloudService get instance => _instance;

  ICloudService._();

  static const String _backupFileName = "iCloud_BethelPlus_Backup.json";

  Future<File> _getBackupFile() async {
    final dir = await getApplicationDocumentsDirectory();
    return File("${dir.path}/$_backupFileName");
  }

  Future<ICloudBackupInfo> exportToICloud(StorageService storage) async {
    final profile = storage.getUserProfile();
    final prayers = storage.getPrayerJournal();
    final sessions = storage.getPrayerSessions();
    final memorized = storage.getMemorizedVerses();
    final highlights = storage.getHighlightedVerses();
    final bookmarks = storage.getBookmarkedVerses().toList();
    final notes = storage.getVerseNotes();
    final challenges = storage.getChallenges();

    final backupPayload = {
      "version": "1.0",
      "exportedAt": DateTime.now().toIso8601String(),
      "app": "Béthel+",
      "data": {
        "profile": profile.toJson(),
        "prayerJournal": prayers.map((p) => p.toJson()).toList(),
        "prayerSessions": sessions.map((s) => s.toJson()).toList(),
        "memorizedVerses": memorized.map((m) => m.toJson()).toList(),
        "highlightedVerses": highlights,
        "bookmarkedVerses": bookmarks,
        "verseNotes": notes,
        "challenges": challenges.map((c) => c.toJson()).toList(),
      }
    };

    final jsonStr = jsonEncode(backupPayload);
    final file = await _getBackupFile();
    await file.writeAsString(jsonStr, flush: true);

      return ICloudBackupInfo(
        createdAt: DateTime.now(),
        totalPrayers: prayers.length,
        totalSessions: sessions.length,
        totalVerses: memorized.length,
        streakDays: 0,
        fileSizeBytes: await file.length(),
        filePath: file.path,
      );
    }

    Future<ICloudBackupInfo?> getLatestBackupInfo() async {
      try {
        final file = await _getBackupFile();
        if (!await file.exists()) return null;

        final content = await file.readAsString();
        final decoded = jsonDecode(content) as Map<String, dynamic>;
        final data = decoded['data'] as Map<String, dynamic>? ?? {};

        final prayers = data['prayerJournal'] as List<dynamic>? ?? [];
        final sessions = data['prayerSessions'] as List<dynamic>? ?? [];
        final verses = data['memorizedVerses'] as List<dynamic>? ?? [];

        final stat = await file.stat();

        return ICloudBackupInfo(
          createdAt: DateTime.tryParse(decoded['exportedAt'] as String? ?? '') ?? stat.modified,
          totalPrayers: prayers.length,
          totalSessions: sessions.length,
          totalVerses: verses.length,
          streakDays: 0,
          fileSizeBytes: stat.size,
          filePath: file.path,
        );
      } catch (_) {
        return null;
      }
    }

    Future<bool> importFromICloud(StorageService storage, {String? customJson}) async {
      try {
        String content;
        if (customJson != null && customJson.trim().isNotEmpty) {
          content = customJson;
        } else {
          final file = await _getBackupFile();
          if (!await file.exists()) return false;
          content = await file.readAsString();
        }

        final decoded = jsonDecode(content) as Map<String, dynamic>;
        final data = decoded['data'] as Map<String, dynamic>? ?? {};

        // Restore profile
        if (data['profile'] is Map<String, dynamic>) {
          final profile = storage.getUserProfile().copyWith(
            firstName: (data['profile']['firstName'] ?? data['profile']['name']) as String?,
            dailyGoalMinutes: data['profile']['dailyGoalMinutes'] as int?,
            lastBackupTimestamp: DateTime.now().millisecondsSinceEpoch,
          );
          await storage.saveUserProfile(profile);
        }

      // Restore highlighted verses
      if (data['highlightedVerses'] is Map) {
        final hl = Map<String, String>.from(data['highlightedVerses'] as Map);
        await storage.saveHighlightedVerses(hl);
      }

      // Restore bookmarks
      if (data['bookmarkedVerses'] is List) {
        final bm = Set<String>.from(data['bookmarkedVerses'] as List);
        await storage.saveBookmarkedVerses(bm);
      }

      // Restore verse notes
      if (data['verseNotes'] is Map) {
        final vn = Map<String, String>.from(data['verseNotes'] as Map);
        await storage.saveVerseNotes(vn);
      }

      return true;
    } catch (_) {
      return false;
    }
  }
}
