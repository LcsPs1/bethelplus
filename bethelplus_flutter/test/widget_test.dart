import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:bethelplus/main.dart';
import 'package:bethelplus/data/services/storage_service.dart';
import 'package:bethelplus/data/providers/bethel_providers.dart';
import 'package:shared_preferences/shared_preferences.dart';

void main() {
  testWidgets('BethelPlusApp initialization test', (WidgetTester tester) async {
    SharedPreferences.setMockInitialValues({});
    final storageService = await StorageService.getInstance();

    await tester.pumpWidget(
      ProviderScope(
        overrides: [
          storageServiceProvider.overrideWithValue(storageService),
        ],
        child: const BethelPlusApp(),
      ),
    );

    await tester.pumpAndSettle();
    expect(find.textContaining('Béthel'), findsWidgets);
  });
}
