import 'dart:async';
import 'package:supabase_flutter/supabase_flutter.dart';
import 'storage_service.dart';

class SupabaseService {
  static final SupabaseService _instance = SupabaseService._();
  static SupabaseService get instance => _instance;

  SupabaseService._();

  bool _isInitialized = false;
  bool get isInitialized => _isInitialized;

  SupabaseClient? get client => _isInitialized ? Supabase.instance.client : null;
  User? get currentUser => client?.auth.currentUser;
  bool get isAuthenticated => currentUser != null;

  Future<void> init({String? url, String? anonKey}) async {
    if (_isInitialized) return;

    final supabaseUrl = url ?? const String.fromEnvironment('SUPABASE_URL', defaultValue: 'https://placeholder.supabase.co');
    final supabaseKey = anonKey ?? const String.fromEnvironment('SUPABASE_ANON_KEY', defaultValue: 'placeholder-anon-key');

    if (supabaseUrl == 'https://placeholder.supabase.co') {
      return;
    }

    try {
      await Supabase.initialize(
        url: supabaseUrl,
        anonKey: supabaseKey,
      );
      _isInitialized = true;
    } catch (_) {
      _isInitialized = false;
    }
  }

  // --- Authentification ---

  Future<AuthResponse?> signInAnonymously() async {
    if (!isInitialized || client == null) return null;
    try {
      return await client!.auth.signInAnonymously();
    } catch (_) {
      return null;
    }
  }

  Future<AuthResponse?> signInWithEmail(String email, String password) async {
    if (!isInitialized || client == null) return null;
    try {
      return await client!.auth.signInWithPassword(email: email, password: password);
    } catch (_) {
      rethrow;
    }
  }

  Future<AuthResponse?> signUpWithEmail(String email, String password, {String? displayName}) async {
    if (!isInitialized || client == null) return null;
    try {
      return await client!.auth.signUp(
        email: email,
        password: password,
        data: displayName != null ? {'display_name': displayName} : null,
      );
    } catch (_) {
      rethrow;
    }
  }

  Future<void> signOut() async {
    if (!isInitialized || client == null) return;
    try {
      await client!.auth.signOut();
    } catch (_) {}
  }

  // --- Synchronisation Cloud ---

  Future<bool> pushLocalDataToCloud(StorageService storage) async {
    if (!isInitialized || client == null || currentUser == null) {
      return true;
    }

    try {
      final userId = currentUser!.id;
      final profile = storage.getUserProfile();
      final prayers = storage.getPrayerJournal();
      final sessions = storage.getPrayerSessions();
      final memorized = storage.getMemorizedVerses();

      // Upsert profile
      await client!.from('user_profiles').upsert({
        'id': userId,
        'display_name': profile.firstName,
        'daily_goal_minutes': profile.dailyGoalMinutes,
        'theme_mode': profile.themeMode,
        'dnd_enabled': profile.isAutoDndEnabled,
        'updated_at': DateTime.now().toIso8601String(),
      });

      // Upsert prayers
      for (final prayer in prayers) {
        await client!.from('prayer_journal').upsert({
          'id': prayer.id.toString(),
          'user_id': userId,
          'title': prayer.title,
          'description': prayer.content,
          'category': prayer.category,
          'is_answered': prayer.isAnswered,
          'answered_at': prayer.answeredAt != null
              ? DateTime.fromMillisecondsSinceEpoch(prayer.answeredAt!).toIso8601String()
              : null,
          'testimony': prayer.testimony,
          'created_at': DateTime.fromMillisecondsSinceEpoch(prayer.createdAt).toIso8601String(),
        });
      }

      // Upsert sessions
      for (final session in sessions) {
        await client!.from('prayer_sessions').upsert({
          'id': session.id.toString(),
          'user_id': userId,
          'title': session.title,
          'duration_seconds': session.durationSeconds,
          'date_millis': session.dateMillis,
          'meditation_id': session.meditationId,
        });
      }

      // Upsert memorized verses
      for (final verse in memorized) {
        await client!.from('memorized_verses').upsert({
          'id': verse.id,
          'user_id': userId,
          'reference': verse.reference,
          'text': verse.text,
          'box': verse.box,
          'last_reviewed_at': verse.lastReviewedTimestamp > 0
              ? DateTime.fromMillisecondsSinceEpoch(verse.lastReviewedTimestamp).toIso8601String()
              : null,
          'next_review_at': DateTime.fromMillisecondsSinceEpoch(verse.nextReviewTimestamp).toIso8601String(),
        });
      }

      return true;
    } catch (_) {
      return false;
    }
  }

  Future<bool> pullDataFromCloud(StorageService storage) async {
    if (!isInitialized || client == null || currentUser == null) {
      return true;
    }

    try {
      final userId = currentUser!.id;

      // Pull Profile
      final profileRes = await client!.from('user_profiles').select().eq('id', userId).maybeSingle();
      if (profileRes != null) {
        final profile = storage.getUserProfile().copyWith(
          firstName: profileRes['display_name'] as String?,
          dailyGoalMinutes: profileRes['daily_goal_minutes'] as int?,
          themeMode: profileRes['theme_mode'] as String?,
          isAutoDndEnabled: profileRes['dnd_enabled'] as bool?,
        );
        await storage.saveUserProfile(profile);
      }

      return true;
    } catch (_) {
      return false;
    }
  }
}
