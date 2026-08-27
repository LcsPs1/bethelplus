import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../data/models/meditation_item.dart';
import '../../../data/providers/bethel_providers.dart';

class ChallengesScreen extends ConsumerWidget {
  const ChallengesScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;
    final challenges = ref.watch(challengesProvider);
    final challengesNotifier = ref.read(challengesProvider.notifier);

    return Scaffold(
      backgroundColor: theme.scaffoldBackgroundColor,
      appBar: AppBar(
        title: const Text(
          "Défis Spirituels",
          style: TextStyle(fontWeight: FontWeight.bold),
        ),
      ),
      body: ListView.builder(
        padding: const EdgeInsets.symmetric(horizontal: 16.0, vertical: 12.0),
        itemCount: challenges.length,
        itemBuilder: (context, index) {
          final challenge = challenges[index];
          final completedDays = challenge.days.where((d) => d.isCompleted).length;
          final progress = challenge.days.isNotEmpty ? completedDays / challenge.days.length : 0.0;
          final isFinished = completedDays == challenge.days.length;

          return Card(
            elevation: 2,
            margin: const EdgeInsets.only(bottom: 16),
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(20),
              side: BorderSide(
                color: isFinished
                    ? Colors.green.withValues(alpha: 0.6)
                    : colorScheme.outlineVariant.withValues(alpha: 0.4),
                width: isFinished ? 1.5 : 1,
              ),
            ),
            color: colorScheme.surface,
            child: Padding(
              padding: const EdgeInsets.all(18.0),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      Container(
                        padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
                        decoration: BoxDecoration(
                          color: isFinished ? Colors.green.withValues(alpha: 0.15) : colorScheme.primaryContainer,
                          borderRadius: BorderRadius.circular(8),
                        ),
                        child: Text(
                          "${challenge.durationDays} Jours • ${challenge.category}",
                          style: TextStyle(
                            fontSize: 12,
                            fontWeight: FontWeight.bold,
                            color: isFinished ? Colors.green.shade800 : colorScheme.onPrimaryContainer,
                          ),
                        ),
                      ),
                      Text(
                        "$completedDays / ${challenge.durationDays} complétés",
                        style: theme.textTheme.bodySmall?.copyWith(fontWeight: FontWeight.bold),
                      ),
                    ],
                  ),
                  const SizedBox(height: 12),
                  Text(
                    challenge.title,
                    style: theme.textTheme.titleLarge?.copyWith(
                      fontWeight: FontWeight.bold,
                      color: colorScheme.onSurface,
                    ),
                  ),
                  const SizedBox(height: 4),
                  Text(
                    challenge.subtitle,
                    style: theme.textTheme.bodyMedium?.copyWith(
                      color: colorScheme.onSurfaceVariant,
                    ),
                  ),
                  const SizedBox(height: 14),

                  // Linear Progress Bar
                  ClipRRect(
                    borderRadius: BorderRadius.circular(8),
                    child: LinearProgressIndicator(
                      value: progress,
                      minHeight: 8,
                      backgroundColor: colorScheme.surfaceContainerHighest,
                      color: isFinished ? Colors.green : colorScheme.primary,
                    ),
                  ),
                  const SizedBox(height: 16),

                  // Day-by-Day Expansion List
                  Theme(
                    data: theme.copyWith(dividerColor: Colors.transparent),
                    child: ExpansionTile(
                      tilePadding: EdgeInsets.zero,
                      title: Text(
                        "Voir les étapes quotidiennes",
                        style: TextStyle(
                          fontSize: 14,
                          fontWeight: FontWeight.bold,
                          color: colorScheme.primary,
                        ),
                      ),
                      children: challenge.days.map((day) {
                        return Container(
                          margin: const EdgeInsets.only(bottom: 10),
                          padding: const EdgeInsets.all(12),
                          decoration: BoxDecoration(
                            color: day.isCompleted
                                ? Colors.green.withValues(alpha: 0.08)
                                : colorScheme.surfaceContainerHighest.withValues(alpha: 0.35),
                            borderRadius: BorderRadius.circular(14),
                            border: Border.all(
                              color: day.isCompleted
                                  ? Colors.green.withValues(alpha: 0.3)
                                  : colorScheme.outlineVariant.withValues(alpha: 0.3),
                            ),
                          ),
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Row(
                                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                                children: [
                                  Expanded(
                                    child: Text(
                                      day.theme,
                                      style: TextStyle(
                                        fontWeight: FontWeight.bold,
                                        fontSize: 14,
                                        color: colorScheme.onSurface,
                                      ),
                                    ),
                                  ),
                                  Checkbox(
                                    value: day.isCompleted,
                                    activeColor: Colors.green,
                                    shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(4)),
                                    onChanged: (_) {
                                      challengesNotifier.toggleDay(challenge.id, day.dayNumber);
                                    },
                                  ),
                                ],
                              ),
                              const SizedBox(height: 4),
                              Text(
                                "📖 ${day.scriptureRef}",
                                style: TextStyle(
                                  fontWeight: FontWeight.w600,
                                  fontSize: 12,
                                  color: colorScheme.primary,
                                ),
                              ),
                              const SizedBox(height: 4),
                              Text(
                                "« ${day.scriptureText} »",
                                style: const TextStyle(fontSize: 12.5, fontStyle: FontStyle.italic),
                              ),
                              const SizedBox(height: 6),
                              Text(
                                "💡 Action : ${day.actionItem}",
                                style: TextStyle(
                                  fontSize: 12,
                                  fontWeight: FontWeight.w500,
                                  color: colorScheme.onSurfaceVariant,
                                ),
                              ),
                              const SizedBox(height: 8),
                              Align(
                                alignment: Alignment.centerRight,
                                child: TextButton.icon(
                                  onPressed: () {
                                    final med = MeditationItem(
                                      id: "med_challenge_${challenge.id}_${day.dayNumber}",
                                      title: "${challenge.title} — ${day.theme}",
                                      theme: MeditationTheme.faith,
                                      scriptureReference: day.scriptureRef,
                                      scriptureText: day.scriptureText,
                                      reflection: "${day.reflection}\n\nAction : ${day.actionItem}",
                                      prayerVariations: [day.prayerPrompt],
                                      estimatedDurationMinutes: 7,
                                    );
                                    ref.read(meditationProvider.notifier).startMeditation(med);
                                    context.go('/meditation');
                                  },
                                  icon: const Icon(Icons.self_improvement_rounded, size: 16),
                                  label: const Text("Méditer cette étape"),
                                ),
                              ),
                            ],
                          ),
                        );
                      }).toList(),
                    ),
                  ),
                ],
              ),
            ),
          );
        },
      ),
    );
  }
}
