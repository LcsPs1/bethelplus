import 'package:flutter_test/flutter_test.dart';
import 'package:bethelplus/data/models/meditation_item.dart';
import 'package:bethelplus/data/services/storage_service.dart';
import 'package:bethelplus/data/services/icloud_service.dart';
import 'package:bethelplus/data/services/supabase_service.dart';
import 'package:shared_preferences/shared_preferences.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  group('Meditation Dynamic Prayers & Cloud Sync Tests', () {
    test('Meditation items contain multiple dynamic prayer variations', () {
      const items = MeditationLibrary.items;
      expect(items, isNotEmpty);
      for (final item in items) {
        expect(item.prayerVariations.length, greaterThanOrEqualTo(2));
        expect(item.guidedPrayer, isNotEmpty);
        expect(item.scriptureReference, isNotEmpty);
        expect(item.scriptureText, isNotEmpty);
      }
    });

    test('ICloudService backup JSON creation and restore simulation', () async {
      SharedPreferences.setMockInitialValues({});
      final storage = await StorageService.getInstance();
      final icloud = ICloudService.instance;

      // Mock custom JSON import
      const mockBackup = '''
      {
        "version": "1.0",
        "app": "Béthel+",
        "data": {
          "profile": {
            "name": "David",
            "dailyGoalMinutes": 25,
            "streakDays": 14
          },
          "prayerJournal": [],
          "prayerSessions": []
        }
      }
      ''';

      final success = await icloud.importFromICloud(storage, customJson: mockBackup);
      expect(success, isTrue);

      final restoredProfile = storage.getUserProfile();
      expect(restoredProfile.firstName, equals("David"));
      expect(restoredProfile.dailyGoalMinutes, equals(25));
    });

    test('SupabaseService initialization and offline fallback', () {
      final supabase = SupabaseService.instance;
      expect(supabase.isInitialized, isFalse);
      expect(supabase.isAuthenticated, isFalse);
    });
  });
}
