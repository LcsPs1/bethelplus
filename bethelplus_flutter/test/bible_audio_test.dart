import 'package:flutter_test/flutter_test.dart';
import 'package:flutter/services.dart';
import 'package:bethelplus/data/repositories/bible_repository.dart';
import 'package:bethelplus/data/services/audio_service.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  setUpAll(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger.setMockMethodCallHandler(
      const MethodChannel('xyz.luan/audioplayers.global'),
      (MethodCall methodCall) async {
        return 1;
      },
    );
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger.setMockMethodCallHandler(
      const MethodChannel('xyz.luan/audioplayers'),
      (MethodCall methodCall) async {
        return 1;
      },
    );
  });

  group('Bible & Audio Assets Unit Tests', () {
    test('Canonical 66 books list completeness', () {
      final repo = BibleRepository.instance;
      final books = repo.getBooks();
      expect(books.length, equals(66));
      expect(books.first.name, equals('Genèse'));
      expect(books.last.name, equals('Apocalypse'));

      final otBooks = books.where((b) => b.testament == 'OT').toList();
      final ntBooks = books.where((b) => b.testament == 'NT').toList();
      expect(otBooks.length, equals(39));
      expect(ntBooks.length, equals(27));
    });

    test('AudioService initialization and ambient sound resolution', () {
      final audio = AudioService.instance;
      expect(audio.currentSoundType, equals('HARPE'));
      expect(audio.isPlayingAmbient, isFalse);
      expect(audio.ambientVolume, equals(0.8));
    });

    test('Bible chapter verses retrieval fallback', () async {
      final repo = BibleRepository.instance;
      final verses = await repo.getChapterVerses(bookId: 'PSA', chapter: 23);
      expect(verses, isNotEmpty);
      expect(verses.first.bookName, equals('Psaumes'));
      expect(verses.first.chapter, equals(23));
    });
  });
}
