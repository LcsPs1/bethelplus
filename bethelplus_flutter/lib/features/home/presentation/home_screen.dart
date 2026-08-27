import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:intl/intl.dart';

import '../../../data/models/daily_verse.dart';
import '../../../data/models/prayer_reminder.dart';
import '../../../data/models/meditation_item.dart';
import '../../../data/providers/bethel_providers.dart';

class HomeScreen extends ConsumerWidget {
  const HomeScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    final profile = ref.watch(userProfileProvider);
    final stats = ref.watch(spiritualStatsProvider);
    final reminders = ref.watch(remindersProvider);

    final todayVerse = DailyVerseRepository.getTodayVerse();
    final now = DateTime.now();
    final frenchDate = DateFormat("EEEE d MMMM yyyy", "fr").format(now);
    final capitalizedDate = frenchDate.isNotEmpty ? "${frenchDate[0].toUpperCase()}${frenchDate.substring(1)}" : "";

    // Find next reminder
    PrayerReminder? nextReminder;
    final enabledReminders = reminders.where((r) => r.isEnabled).toList();
    if (enabledReminders.isNotEmpty) {
      final currentMinutes = now.hour * 60 + now.minute;
      enabledReminders.sort((a, b) => (a.hour * 60 + a.minute).compareTo(b.hour * 60 + b.minute));
      nextReminder = enabledReminders.firstWhere(
        (r) => (r.hour * 60 + r.minute) > currentMinutes,
        orElse: () => enabledReminders.first,
      );
    }

    return Scaffold(
      backgroundColor: theme.scaffoldBackgroundColor,
      body: SafeArea(
        child: ListView(
          padding: const EdgeInsets.symmetric(horizontal: 18.0, vertical: 14.0),
          children: [
            // 1. Welcome & Spiritual Date Banner
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        capitalizedDate,
                        style: theme.textTheme.labelMedium?.copyWith(
                          color: colorScheme.primary,
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                      const SizedBox(height: 3),
                      Text(
                        "Paix et grâce, ${profile.firstName.isNotEmpty ? profile.firstName : 'Fidèle'}",
                        style: theme.textTheme.headlineMedium?.copyWith(
                          fontWeight: FontWeight.bold,
                          color: colorScheme.onSurface,
                        ),
                      ),
                      const SizedBox(height: 2),
                      Text(
                        "« L'Éternel est en ce lieu. » Bienvenue à Béthel.",
                        style: theme.textTheme.bodyMedium?.copyWith(
                          color: colorScheme.onSurfaceVariant,
                        ),
                      ),
                    ],
                  ),
                ),
                // Streak Badge
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
                  decoration: BoxDecoration(
                    color: colorScheme.primaryContainer,
                    borderRadius: BorderRadius.circular(16),
                    border: Border.all(color: colorScheme.primary.withValues(alpha: 0.3)),
                  ),
                  child: Row(
                    children: [
                      const Icon(Icons.local_fire_department, color: Colors.orange, size: 20),
                      const SizedBox(width: 4),
                      Text(
                        "${stats.currentStreak} j",
                        style: theme.textTheme.labelLarge?.copyWith(
                          fontWeight: FontWeight.bold,
                          color: colorScheme.onPrimaryContainer,
                        ),
                      ),
                    ],
                  ),
                ),
              ],
            ),
            const SizedBox(height: 16),

            // 2. Next Scheduled Prayer Card
            Card(
              elevation: 0,
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(16),
                side: BorderSide(color: colorScheme.outlineVariant),
              ),
              color: colorScheme.surfaceContainerHighest.withValues(alpha: 0.45),
              child: InkWell(
                onTap: () => context.push('/reminders'),
                borderRadius: BorderRadius.circular(16),
                child: Padding(
                  padding: const EdgeInsets.symmetric(horizontal: 16.0, vertical: 12.0),
                  child: Row(
                    children: [
                      Container(
                        width: 42,
                        height: 42,
                        decoration: BoxDecoration(
                          color: colorScheme.primaryContainer,
                          shape: BoxShape.circle,
                        ),
                        child: Icon(Icons.alarm_rounded, color: colorScheme.onPrimaryContainer, size: 22),
                      ),
                      const SizedBox(width: 14),
                      Expanded(
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Text(
                              "Prochain temps de prière",
                              style: theme.textTheme.labelSmall?.copyWith(
                                color: colorScheme.onSurfaceVariant,
                              ),
                            ),
                            Text(
                              nextReminder != null
                                  ? "${nextReminder.title} à ${nextReminder.hour.toString().padLeft(2, '0')}:${nextReminder.minute.toString().padLeft(2, '0')}"
                                  : "Aucun rappel actif",
                              style: theme.textTheme.titleMedium?.copyWith(
                                fontWeight: FontWeight.w600,
                                color: colorScheme.onSurface,
                              ),
                            ),
                          ],
                        ),
                      ),
                      Icon(Icons.arrow_forward_ios_rounded, color: colorScheme.onSurfaceVariant, size: 16),
                    ],
                  ),
                ),
              ),
            ),
            const SizedBox(height: 16),

            // 3. Highlighted Daily Scripture Card
            Card(
              elevation: 2,
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(20),
                side: BorderSide(color: colorScheme.primary.withValues(alpha: 0.5), width: 1.5),
              ),
              color: colorScheme.surface,
              child: Padding(
                padding: const EdgeInsets.all(20.0),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        Container(
                          padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
                          decoration: BoxDecoration(
                            color: colorScheme.primaryContainer,
                            borderRadius: BorderRadius.circular(8),
                          ),
                          child: Row(
                            children: [
                              Icon(Icons.wb_sunny_rounded, size: 14, color: colorScheme.onPrimaryContainer),
                              const SizedBox(width: 5),
                              Text(
                                "Verset du Jour",
                                style: theme.textTheme.labelSmall?.copyWith(
                                  fontWeight: FontWeight.bold,
                                  color: colorScheme.onPrimaryContainer,
                                ),
                              ),
                            ],
                          ),
                        ),
                        Row(
                          children: [
                            IconButton(
                              icon: const Icon(Icons.bookmark_outline_rounded, size: 20),
                              onPressed: () {
                                ScaffoldMessenger.of(context).showSnackBar(
                                  const SnackBar(content: Text("Verset ajouté à vos favoris")),
                                );
                              },
                            ),
                          ],
                        ),
                      ],
                    ),
                    const SizedBox(height: 12),
                    Text(
                      "« ${todayVerse.text} »",
                      style: theme.textTheme.bodyLarge?.copyWith(
                        color: colorScheme.onSurface,
                        height: 1.5,
                        fontSize: 17,
                        fontStyle: FontStyle.italic,
                      ),
                    ),
                    const SizedBox(height: 8),
                    Align(
                      alignment: Alignment.centerRight,
                      child: Text(
                        "— ${todayVerse.reference} (LSG)",
                        style: theme.textTheme.labelMedium?.copyWith(
                          fontWeight: FontWeight.bold,
                          color: colorScheme.primary,
                        ),
                      ),
                    ),
                    const SizedBox(height: 14),
                    Divider(color: colorScheme.outlineVariant.withValues(alpha: 0.5)),
                    const SizedBox(height: 12),

                    // Daily Reflection Box
                    Container(
                      width: double.infinity,
                      padding: const EdgeInsets.all(12),
                      decoration: BoxDecoration(
                        color: colorScheme.surfaceContainerHighest.withValues(alpha: 0.5),
                        borderRadius: BorderRadius.circular(12),
                      ),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(
                            "Méditation & Action :",
                            style: theme.textTheme.labelSmall?.copyWith(
                              fontWeight: FontWeight.bold,
                              color: colorScheme.primary,
                            ),
                          ),
                          const SizedBox(height: 3),
                          Text(
                            todayVerse.reflection,
                            style: theme.textTheme.bodyMedium?.copyWith(
                              color: colorScheme.onSurfaceVariant,
                            ),
                          ),
                        ],
                      ),
                    ),
                    const SizedBox(height: 14),

                    // Buttons
                    Row(
                      children: [
                        Expanded(
                          child: OutlinedButton.icon(
                            onPressed: () => context.go('/bible'),
                            icon: Icon(Icons.menu_book_rounded, size: 16, color: colorScheme.primary),
                            label: Text(
                              "Lire la Bible",
                              style: TextStyle(fontSize: 13, color: colorScheme.primary),
                            ),
                            style: OutlinedButton.styleFrom(
                              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
                              padding: const EdgeInsets.symmetric(vertical: 8),
                            ),
                          ),
                        ),
                        const SizedBox(width: 10),
                        Expanded(
                          child: FilledButton.icon(
                            onPressed: () {
                              final item = MeditationItem(
                                id: "med_${todayVerse.id}",
                                title: "Méditation du Jour : ${todayVerse.reference}",
                                theme: MeditationTheme.faith,
                                scriptureReference: todayVerse.reference,
                                scriptureText: todayVerse.text,
                                reflection: todayVerse.reflection,
                                prayerVariations: const [
                                  "Seigneur, grave cette parole dans mon esprit et guide mes pas aujourd'hui. Amen.",
                                  "Père céleste, que Ta lumière éclaire ma journée à travers cette promesse. Amen."
                                ],
                                estimatedDurationMinutes: 5,
                              );
                              ref.read(meditationProvider.notifier).startMeditation(item);
                              context.go('/meditation');
                            },
                            icon: const Icon(Icons.self_improvement_rounded, size: 16),
                            label: const Text(
                              "Méditer",
                              style: TextStyle(fontSize: 13, fontWeight: FontWeight.bold),
                            ),
                            style: FilledButton.styleFrom(
                              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
                              backgroundColor: colorScheme.primary,
                              foregroundColor: colorScheme.onPrimary,
                              padding: const EdgeInsets.symmetric(vertical: 8),
                            ),
                          ),
                        ),
                      ],
                    ),
                  ],
                ),
              ),
            ),
            const SizedBox(height: 16),

            // 4. Quick Actions Grid
            Row(
              children: [
                Expanded(
                  child: _buildQuickActionCard(
                    context,
                    title: "Prières de Situation",
                    subtitle: "10 thèmes & IA",
                    icon: Icons.volunteer_activism_rounded,
                    color: const Color(0xFFE11D48),
                    onTap: () => context.push('/prayers'),
                  ),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: _buildQuickActionCard(
                    context,
                    title: "Mémorisation",
                    subtitle: "Cartes Leitner",
                    icon: Icons.style_rounded,
                    color: const Color(0xFF059669),
                    onTap: () => context.push('/flashcards'),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 12),
            Row(
              children: [
                Expanded(
                  child: _buildQuickActionCard(
                    context,
                    title: "Défis Spirituels",
                    subtitle: "7 & 21 jours",
                    icon: Icons.emoji_events_rounded,
                    color: const Color(0xFFD97706),
                    onTap: () => context.push('/challenges'),
                  ),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: _buildQuickActionCard(
                    context,
                    title: "Heures Saintes",
                    subtitle: "Rappels & Réveil",
                    icon: Icons.access_time_filled_rounded,
                    color: const Color(0xFF7C3AED),
                    onTap: () => context.push('/reminders'),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 20),

            // 5. Start Free Prayer Card
            Card(
              elevation: 2,
              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
              color: colorScheme.primaryContainer,
              child: Padding(
                padding: const EdgeInsets.all(18.0),
                child: Column(
                  children: [
                    Row(
                      children: [
                        Container(
                          width: 44,
                          height: 44,
                          decoration: BoxDecoration(
                            color: colorScheme.primary,
                            shape: BoxShape.circle,
                          ),
                          child: const Icon(Icons.self_improvement_rounded, color: Colors.white, size: 24),
                        ),
                        const SizedBox(width: 14),
                        Expanded(
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Text(
                                "Temps de Silence & Prière",
                                style: theme.textTheme.titleMedium?.copyWith(
                                  fontWeight: FontWeight.bold,
                                  color: colorScheme.onPrimaryContainer,
                                ),
                              ),
                              Text(
                                "Entrez dans le lieu secret",
                                style: theme.textTheme.bodySmall?.copyWith(
                                  color: colorScheme.onPrimaryContainer.withValues(alpha: 0.8),
                                ),
                              ),
                            ],
                          ),
                        ),
                      ],
                    ),
                    const SizedBox(height: 14),
                    Row(
                      children: [
                        Expanded(child: _buildDurationButton(context, ref, "5 min", 5, colorScheme)),
                        const SizedBox(width: 8),
                        Expanded(child: _buildDurationButton(context, ref, "10 min", 10, colorScheme)),
                        const SizedBox(width: 8),
                        Expanded(child: _buildDurationButton(context, ref, "15 min", 15, colorScheme)),
                      ],
                    ),
                  ],
                ),
              ),
            ),
            const SizedBox(height: 40),
          ],
        ),
      ),
    );
  }

  Widget _buildQuickActionCard(
    BuildContext context, {
    required String title,
    required String subtitle,
    required IconData icon,
    required Color color,
    required VoidCallback onTap,
  }) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    return Card(
      elevation: 0,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(16),
        side: BorderSide(color: colorScheme.outlineVariant.withValues(alpha: 0.5)),
      ),
      color: colorScheme.surface,
      child: InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(16),
        child: Padding(
          padding: const EdgeInsets.all(14.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Container(
                width: 36,
                height: 36,
                decoration: BoxDecoration(
                  color: color.withValues(alpha: 0.15),
                  borderRadius: BorderRadius.circular(10),
                ),
                child: Icon(icon, color: color, size: 20),
              ),
              const SizedBox(height: 10),
              Text(
                title,
                style: theme.textTheme.titleSmall?.copyWith(
                  fontWeight: FontWeight.bold,
                  color: colorScheme.onSurface,
                ),
              ),
              Text(
                subtitle,
                style: theme.textTheme.bodySmall?.copyWith(
                  color: colorScheme.onSurfaceVariant,
                  fontSize: 11,
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildDurationButton(
    BuildContext context,
    WidgetRef ref,
    String label,
    int minutes,
    ColorScheme colorScheme,
  ) {
    return OutlinedButton(
      onPressed: () {
        ref.read(meditationProvider.notifier).startMeditation(null, durationMinutes: minutes);
        context.go('/meditation');
      },
      style: OutlinedButton.styleFrom(
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
        side: BorderSide(color: colorScheme.onPrimaryContainer.withValues(alpha: 0.4)),
        foregroundColor: colorScheme.onPrimaryContainer,
        padding: const EdgeInsets.symmetric(vertical: 8),
      ),
      child: Text(
        label,
        style: const TextStyle(fontWeight: FontWeight.w600, fontSize: 12),
      ),
    );
  }
}
