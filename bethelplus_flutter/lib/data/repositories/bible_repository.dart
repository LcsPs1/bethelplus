import 'dart:convert';
import 'package:flutter/services.dart';
import '../models/bible_verse.dart';
import '../models/bible_data.dart';

class BibleRepository {
  static final BibleRepository _instance = BibleRepository._();
  static BibleRepository get instance => _instance;

  BibleRepository._();

  final Map<String, Map<String, dynamic>> _loadedBibles = {};
  bool _isInitialized = false;

  Future<void> init() async {
    if (_isInitialized) return;
    try {
      await _loadVersion('LSG');
      _isInitialized = true;
    } catch (_) {}
  }

  Future<Map<String, dynamic>?> _loadVersion(String version) async {
    final v = version.toUpperCase();
    if (_loadedBibles.containsKey(v)) {
      return _loadedBibles[v];
    }

    try {
      final assetPath = v == 'DRB' ? 'assets/bible/bible_drb.json' : 'assets/bible/bible_lsg.json';
      final jsonStr = await rootBundle.loadString(assetPath);
      final decoded = jsonDecode(jsonStr) as Map<String, dynamic>;
      _loadedBibles[v] = decoded;
      return decoded;
    } catch (e) {
      return null;
    }
  }

  List<BibleBookInfo> getBooks() {
    return BibleData.allBooks;
  }

  Future<List<BibleVerse>> getChapterVerses({
    required String bookId,
    required int chapter,
    String version = 'LSG',
  }) async {
    final data = await _loadVersion(version);
    final bookMeta = BibleData.allBooks.firstWhere(
      (b) => b.id.toUpperCase() == bookId.toUpperCase(),
      orElse: () => const BibleBookInfo(
        id: 'GEN',
        name: 'Genèse',
        category: 'Pentateuque',
        testament: 'OT',
        chaptersCount: 50,
        abbreviation: 'Gen',
      ),
    );

    if (data != null && data['books'] is List) {
      final booksList = data['books'] as List<dynamic>;
      final foundBook = booksList.firstWhere(
        (b) => (b['id'] as String).toUpperCase() == bookId.toUpperCase(),
        orElse: () => null,
      );

      if (foundBook != null && foundBook['chapters'] is Map) {
        final chaptersMap = foundBook['chapters'] as Map<String, dynamic>;
        final chapterKey = chapter.toString();
        if (chaptersMap.containsKey(chapterKey) && chaptersMap[chapterKey] is List) {
          final versesRaw = chaptersMap[chapterKey] as List<dynamic>;
          return versesRaw.map((v) {
            final verseNum = v['verse'] as int? ?? 1;
            final verseText = v['text'] as String? ?? '';
            return BibleVerse(
              id: "${version}_${bookId}_${chapter}_$verseNum",
              bookId: bookId,
              bookName: bookMeta.name,
              testament: bookMeta.testament,
              chapter: chapter,
              verse: verseNum,
              text: verseText,
              version: version,
            );
          }).toList();
        }
      }
    }

    // Fallback if not found in JSON
    return [
      BibleVerse(
        id: "${version}_${bookId}_${chapter}_1",
        bookId: bookId,
        bookName: bookMeta.name,
        testament: bookMeta.testament,
        chapter: chapter,
        verse: 1,
        text: "Chapitre $chapter du livre de ${bookMeta.name}. « Ta parole est une lampe à mes pieds, et une lumière sur mon sentier. »",
        version: version,
      ),
      BibleVerse(
        id: "${version}_${bookId}_${chapter}_2",
        bookId: bookId,
        bookName: bookMeta.name,
        testament: bookMeta.testament,
        chapter: chapter,
        verse: 2,
        text: "Que la paix du Seigneur et Sa sagesse vous accompagnent dans ce recueillement.",
        version: version,
      ),
    ];
  }

  Future<List<BibleVerse>> searchVerses({
    required String query,
    String version = 'LSG',
    int maxResults = 50,
  }) async {
    if (query.trim().isEmpty) return [];

    final data = await _loadVersion(version);
    final results = <BibleVerse>[];
    final q = query.toLowerCase().trim();

    if (data != null && data['books'] is List) {
      final booksList = data['books'] as List<dynamic>;
      for (final book in booksList) {
        final bookId = book['id'] as String? ?? '';
        final bookName = book['name'] as String? ?? bookId;
        final testament = book['testament'] as String? ?? 'OT';
        final chaptersMap = book['chapters'] as Map<String, dynamic>? ?? {};

        for (final entry in chaptersMap.entries) {
          final chapterNum = int.tryParse(entry.key) ?? 1;
          final versesList = entry.value as List<dynamic>? ?? [];

          for (final v in versesList) {
            final verseNum = v['verse'] as int? ?? 1;
            final verseText = v['text'] as String? ?? '';

            if (verseText.toLowerCase().contains(q) ||
                bookName.toLowerCase().contains(q) ||
                "$bookName $chapterNum:$verseNum".toLowerCase().contains(q)) {
              results.add(
                BibleVerse(
                  id: "${version}_${bookId}_${chapterNum}_$verseNum",
                  bookId: bookId,
                  bookName: bookName,
                  testament: testament,
                  chapter: chapterNum,
                  verse: verseNum,
                  text: verseText,
                  version: version,
                ),
              );

              if (results.length >= maxResults) {
                return results;
              }
            }
          }
        }
      }
    }

    return results;
  }
}
