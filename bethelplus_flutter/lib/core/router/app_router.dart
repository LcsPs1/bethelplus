import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import '../../features/home/presentation/home_screen.dart';
import '../../features/bible/presentation/bible_screen.dart';
import '../../features/meditation/presentation/meditation_screen.dart';
import '../../features/journal/presentation/journal_screen.dart';
import '../../features/profile/presentation/profile_screen.dart';
import '../../features/challenges/presentation/challenges_screen.dart';
import '../../features/flashcards/presentation/flashcards_screen.dart';
import '../../features/reminders/presentation/reminders_screen.dart';
import '../../features/prayers/presentation/situational_prayers_screen.dart';
import '../widgets/main_scaffold.dart';

final appRouterProvider = Provider<GoRouter>((ref) {
  return GoRouter(
    initialLocation: '/home',
    routes: [
      ShellRoute(
        builder: (context, state, child) => MainScaffold(child: child),
        routes: [
          GoRoute(
            path: '/home',
            name: 'home',
            builder: (context, state) => const HomeScreen(),
          ),
          GoRoute(
            path: '/bible',
            name: 'bible',
            builder: (context, state) => const BibleScreen(),
          ),
          GoRoute(
            path: '/meditation',
            name: 'meditation',
            builder: (context, state) => const MeditationScreen(),
          ),
          GoRoute(
            path: '/journal',
            name: 'journal',
            builder: (context, state) => const JournalScreen(),
          ),
          GoRoute(
            path: '/profile',
            name: 'profile',
            builder: (context, state) => const ProfileScreen(),
          ),
        ],
      ),
      GoRoute(
        path: '/challenges',
        name: 'challenges',
        builder: (context, state) => const ChallengesScreen(),
      ),
      GoRoute(
        path: '/flashcards',
        name: 'flashcards',
        builder: (context, state) => const FlashcardsScreen(),
      ),
      GoRoute(
        path: '/reminders',
        name: 'reminders',
        builder: (context, state) => const RemindersScreen(),
      ),
      GoRoute(
        path: '/prayers',
        name: 'prayers',
        builder: (context, state) => const SituationalPrayersScreen(),
      ),
    ],
  );
});
