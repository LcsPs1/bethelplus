import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../data/models/meditation_item.dart';
import '../../../data/providers/bethel_providers.dart';

class MeditationScreen extends ConsumerStatefulWidget {
  const MeditationScreen({super.key});

  @override
  ConsumerState<MeditationScreen> createState() => _MeditationScreenState();
}

class _MeditationScreenState extends ConsumerState<MeditationScreen> {
  MeditationTheme _selectedTheme = MeditationTheme.all;

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;
    final meditationState = ref.watch(meditationProvider);
    final meditationNotifier = ref.read(meditationProvider.notifier);

    // Filter items
    final filteredItems = _selectedTheme == MeditationTheme.all
        ? MeditationLibrary.items
        : MeditationLibrary.items.where((item) => item.theme == _selectedTheme).toList();

    return Scaffold(
      backgroundColor: theme.scaffoldBackgroundColor,
      appBar: AppBar(
        title: const Text(
          "Méditations & Recueillement",
          style: TextStyle(fontWeight: FontWeight.bold),
        ),
      ),
      body: Stack(
        children: [
          Column(
            children: [
              // 1. Theme Filter Chips
              SingleChildScrollView(
                scrollDirection: Axis.horizontal,
                padding: const EdgeInsets.symmetric(horizontal: 16.0, vertical: 8.0),
                child: Row(
                  children: MeditationTheme.values.map((t) {
                    final isSelected = _selectedTheme == t;
                    return Padding(
                      padding: const EdgeInsets.only(right: 8.0),
                      child: FilterChip(
                        selected: isSelected,
                        label: Text(t.displayName),
                        onSelected: (_) {
                          setState(() {
                            _selectedTheme = t;
                          });
                        },
                        selectedColor: colorScheme.primaryContainer,
                        checkmarkColor: colorScheme.onPrimaryContainer,
                        labelStyle: TextStyle(
                          fontWeight: isSelected ? FontWeight.bold : FontWeight.normal,
                          color: isSelected ? colorScheme.onPrimaryContainer : colorScheme.onSurface,
                        ),
                      ),
                    );
                  }).toList(),
                ),
              ),

              // 2. Meditations List
              Expanded(
                child: ListView.builder(
                  padding: EdgeInsets.only(
                    left: 16,
                    right: 16,
                    top: 8,
                    bottom: (meditationState.isRunning || meditationState.timerTotalSeconds > 0) ? 140 : 80,
                  ),
                  itemCount: filteredItems.length,
                  itemBuilder: (context, index) {
                    final item = filteredItems[index];
                    return Card(
                      elevation: 1,
                      margin: const EdgeInsets.only(bottom: 12),
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(16),
                        side: BorderSide(color: colorScheme.outlineVariant.withValues(alpha: 0.5)),
                      ),
                      color: colorScheme.surface,
                      child: InkWell(
                        onTap: () {
                          meditationNotifier.startMeditation(item);
                          _openActiveMeditationSheet(context);
                        },
                        borderRadius: BorderRadius.circular(16),
                        child: Padding(
                          padding: const EdgeInsets.all(16.0),
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Row(
                                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                                children: [
                                  Container(
                                    padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                                    decoration: BoxDecoration(
                                      color: colorScheme.primaryContainer,
                                      borderRadius: BorderRadius.circular(8),
                                    ),
                                    child: Text(
                                      item.theme.displayName,
                                      style: TextStyle(
                                        fontSize: 11,
                                        fontWeight: FontWeight.bold,
                                        color: colorScheme.onPrimaryContainer,
                                      ),
                                    ),
                                  ),
                                  Row(
                                    children: [
                                      const Icon(Icons.timer_outlined, size: 14, color: Colors.grey),
                                      const SizedBox(width: 4),
                                      Text(
                                        "${item.estimatedDurationMinutes} min",
                                        style: theme.textTheme.bodySmall?.copyWith(fontWeight: FontWeight.w600),
                                      ),
                                    ],
                                  ),
                                ],
                              ),
                              const SizedBox(height: 10),
                              Text(
                                item.title,
                                style: theme.textTheme.titleMedium?.copyWith(
                                  fontWeight: FontWeight.bold,
                                  color: colorScheme.onSurface,
                                ),
                              ),
                              const SizedBox(height: 4),
                              Text(
                                item.scriptureReference,
                                style: TextStyle(
                                  fontWeight: FontWeight.w600,
                                  fontSize: 13,
                                  color: colorScheme.primary,
                                ),
                              ),
                              const SizedBox(height: 8),
                              Text(
                                item.reflection,
                                maxLines: 2,
                                overflow: TextOverflow.ellipsis,
                                style: theme.textTheme.bodyMedium?.copyWith(
                                  color: colorScheme.onSurfaceVariant,
                                ),
                              ),
                              const SizedBox(height: 12),
                              Align(
                                alignment: Alignment.centerRight,
                                child: FilledButton.tonalIcon(
                                  onPressed: () {
                                    meditationNotifier.startMeditation(item);
                                    _openActiveMeditationSheet(context);
                                  },
                                  icon: const Icon(Icons.play_arrow_rounded, size: 18),
                                  label: const Text("Entrer en prière"),
                                ),
                              ),
                            ],
                          ),
                        ),
                      ),
                    );
                  },
                ),
              ),
            ],
          ),

          // Mini Player Banner if meditation active
          if (meditationState.isRunning || meditationState.timerTotalSeconds > 0)
            Positioned(
              left: 16,
              right: 16,
              bottom: 16,
              child: Card(
                elevation: 4,
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
                color: colorScheme.primaryContainer,
                child: InkWell(
                  onTap: () => _openActiveMeditationSheet(context),
                  borderRadius: BorderRadius.circular(16),
                  child: Padding(
                    padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
                    child: Row(
                      children: [
                        CircularProgressIndicator(
                          value: meditationState.timerTotalSeconds > 0
                              ? 1 - (meditationState.timerRemainingSeconds / meditationState.timerTotalSeconds)
                              : 0,
                          strokeWidth: 3,
                          color: colorScheme.primary,
                        ),
                        const SizedBox(width: 14),
                        Expanded(
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            mainAxisSize: MainAxisSize.min,
                            children: [
                              Text(
                                meditationState.activeMeditation?.title ?? "Prière Libre",
                                style: TextStyle(fontWeight: FontWeight.bold, color: colorScheme.onPrimaryContainer),
                                maxLines: 1,
                                overflow: TextOverflow.ellipsis,
                              ),
                              Text(
                                "${(meditationState.timerRemainingSeconds ~/ 60).toString().padLeft(2, '0')}:${(meditationState.timerRemainingSeconds % 60).toString().padLeft(2, '0')}",
                                style: TextStyle(color: colorScheme.onPrimaryContainer.withValues(alpha: 0.8)),
                              ),
                            ],
                          ),
                        ),
                        IconButton(
                          icon: Icon(meditationState.isRunning ? Icons.pause_rounded : Icons.play_arrow_rounded),
                          onPressed: () => meditationNotifier.togglePlayPause(),
                        ),
                        IconButton(
                          icon: const Icon(Icons.close_rounded),
                          onPressed: () => meditationNotifier.cancelSession(),
                        ),
                      ],
                    ),
                  ),
                ),
              ),
            ),
        ],
      ),
    );
  }

  void _openActiveMeditationSheet(BuildContext context) {
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      backgroundColor: Theme.of(context).scaffoldBackgroundColor,
      shape: const RoundedRectangleBorder(borderRadius: BorderRadius.vertical(top: Radius.circular(28))),
      builder: (ctx) {
        int selectedPrayerIndex = 0;
        bool isGeneratingPrayer = false;
        List<String>? dynamicPrayers;

        return StatefulBuilder(
          builder: (ctx, setModalState) {
            return Consumer(
              builder: (ctx, ref, _) {
                final state = ref.watch(meditationProvider);
                final notifier = ref.read(meditationProvider.notifier);
                final colorScheme = Theme.of(context).colorScheme;

                if (state.isCompleted) {
                  return Padding(
                    padding: const EdgeInsets.all(28.0),
                    child: Column(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        const Icon(Icons.celebration_rounded, color: Colors.amber, size: 64),
                        const SizedBox(height: 16),
                        const Text(
                          "Temps de Prière Accomplie",
                          style: TextStyle(fontWeight: FontWeight.bold, fontSize: 22),
                          textAlign: TextAlign.center,
                        ),
                        const SizedBox(height: 8),
                        Text(
                          "Votre session a été enregistrée dans vos statistiques spirituelles. Que cette paix vous accompagne tout au long de la journée.",
                          textAlign: TextAlign.center,
                          style: TextStyle(color: colorScheme.onSurfaceVariant),
                        ),
                        const SizedBox(height: 24),
                        FilledButton(
                          onPressed: () {
                            notifier.dismissCompleted();
                            Navigator.pop(ctx);
                          },
                          style: FilledButton.styleFrom(minimumSize: const Size(double.infinity, 50)),
                          child: const Text("Gloire à Dieu"),
                        ),
                      ],
                    ),
                  );
                }

                final mins = state.timerRemainingSeconds ~/ 60;
                final secs = state.timerRemainingSeconds % 60;
                final timeStr = "${mins.toString().padLeft(2, '0')}:${secs.toString().padLeft(2, '0')}";
                final progress = state.timerTotalSeconds > 0
                    ? 1.0 - (state.timerRemainingSeconds / state.timerTotalSeconds)
                    : 0.0;

                // Initialize prayers if not already set
                dynamicPrayers ??= state.activeMeditation != null
                    ? List<String>.from(state.activeMeditation!.prayerVariations)
                    : ["Seigneur, remplis mon cœur de Ta présence et de Ta sainte paix. Amen."];

                if (selectedPrayerIndex >= dynamicPrayers!.length) {
                  selectedPrayerIndex = 0;
                }

                return SizedBox(
                  height: MediaQuery.of(context).size.height * 0.88,
                  child: Padding(
                    padding: const EdgeInsets.symmetric(horizontal: 24.0, vertical: 16.0),
                    child: ListView(
                      children: [
                        Center(
                          child: Container(
                            width: 40,
                            height: 4,
                            decoration: BoxDecoration(color: Colors.grey.shade400, borderRadius: BorderRadius.circular(2)),
                          ),
                        ),
                        const SizedBox(height: 16),
                        Text(
                          state.activeMeditation?.title ?? "Temps de Prière & Silence",
                          style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 20),
                          textAlign: TextAlign.center,
                        ),
                        if (state.activeMeditation != null) ...[
                          const SizedBox(height: 4),
                          Text(
                            state.activeMeditation!.scriptureReference,
                            style: TextStyle(fontWeight: FontWeight.bold, color: colorScheme.primary),
                            textAlign: TextAlign.center,
                          ),
                        ],
                        const SizedBox(height: 24),

                        // Big Circular Countdown Timer
                        Center(
                          child: SizedBox(
                            width: 180,
                            height: 180,
                            child: Stack(
                              alignment: Alignment.center,
                              children: [
                                SizedBox(
                                  width: 180,
                                  height: 180,
                                  child: CircularProgressIndicator(
                                    value: progress,
                                    strokeWidth: 8,
                                    backgroundColor: colorScheme.surfaceContainerHighest,
                                    color: colorScheme.primary,
                                  ),
                                ),
                                Column(
                                  mainAxisSize: MainAxisSize.min,
                                  children: [
                                    Text(
                                      timeStr,
                                      style: const TextStyle(fontSize: 36, fontWeight: FontWeight.bold),
                                    ),
                                    Text(
                                      state.isRunning ? "En prière..." : "En pause",
                                      style: TextStyle(color: colorScheme.onSurfaceVariant, fontSize: 12),
                                    ),
                                  ],
                                ),
                              ],
                            ),
                          ),
                        ),
                        const SizedBox(height: 24),

                        // Soundscape Selector
                        Row(
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            _buildSoundscapeChip("HARPE", "Harpe", Icons.music_note_rounded, state, notifier, colorScheme),
                            const SizedBox(width: 8),
                            _buildSoundscapeChip("PLUIE", "Pluie", Icons.water_drop_rounded, state, notifier, colorScheme),
                            const SizedBox(width: 8),
                            _buildSoundscapeChip("BRISE", "Brise", Icons.air_rounded, state, notifier, colorScheme),
                            const SizedBox(width: 8),
                            _buildSoundscapeChip("SILENCE", "Silence", Icons.volume_off_rounded, state, notifier, colorScheme),
                          ],
                        ),
                        const SizedBox(height: 20),

                        // Timer Controls
                        Row(
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            OutlinedButton.icon(
                              onPressed: () => notifier.addMinutes(5),
                              icon: const Icon(Icons.add, size: 16),
                              label: const Text("+5 min"),
                            ),
                            const SizedBox(width: 16),
                            FilledButton(
                              onPressed: () => notifier.togglePlayPause(),
                              style: FilledButton.styleFrom(
                                shape: const CircleBorder(),
                                padding: const EdgeInsets.all(16),
                              ),
                              child: Icon(state.isRunning ? Icons.pause_rounded : Icons.play_arrow_rounded, size: 28),
                            ),
                            const SizedBox(width: 16),
                            OutlinedButton(
                              onPressed: () => notifier.completeSession(),
                              child: const Text("Terminer"),
                            ),
                          ],
                        ),
                        const SizedBox(height: 24),

                        // Scripture & Guided Prayer Card
                        if (state.activeMeditation != null) ...[
                          Card(
                            color: colorScheme.surfaceContainerHighest.withValues(alpha: 0.5),
                            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
                            child: Padding(
                              padding: const EdgeInsets.all(16.0),
                              child: Column(
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  Row(
                                    children: [
                                      Icon(Icons.menu_book_rounded, color: colorScheme.primary, size: 18),
                                      const SizedBox(width: 8),
                                      Text(
                                        "Parole de Dieu d'Ancrage :",
                                        style: TextStyle(fontWeight: FontWeight.bold, color: colorScheme.primary),
                                      ),
                                    ],
                                  ),
                                  const SizedBox(height: 6),
                                  Text(
                                    state.activeMeditation!.scriptureText,
                                    style: const TextStyle(fontStyle: FontStyle.italic, height: 1.5),
                                  ),
                                  const Divider(height: 24),

                                  // Prayer variations selector header
                                  Row(
                                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                                    children: [
                                      Text(
                                        "Prière Guidée :",
                                        style: TextStyle(fontWeight: FontWeight.bold, color: colorScheme.primary),
                                      ),
                                      if (isGeneratingPrayer)
                                        const SizedBox(
                                          width: 16,
                                          height: 16,
                                          child: CircularProgressIndicator(strokeWidth: 2),
                                        )
                                      else
                                        TextButton.icon(
                                          onPressed: () async {
                                            setModalState(() {
                                              isGeneratingPrayer = true;
                                            });
                                            try {
                                              final gemini = ref.read(geminiServiceProvider);
                                              final freshPrayer = await gemini.generateSituationalPrayer(
                                                situation: "Méditation intime sur le verset: ${state.activeMeditation!.scriptureReference} « ${state.activeMeditation!.scriptureText} »",
                                              );
                                              final text = freshPrayer['prayerText'] ?? '';
                                              if (text.isNotEmpty) {
                                                setModalState(() {
                                                  dynamicPrayers!.add(text);
                                                  selectedPrayerIndex = dynamicPrayers!.length - 1;
                                                  isGeneratingPrayer = false;
                                                });
                                              } else {
                                                setModalState(() {
                                                  isGeneratingPrayer = false;
                                                });
                                              }
                                            } catch (_) {
                                              setModalState(() {
                                                isGeneratingPrayer = false;
                                              });
                                            }
                                          },
                                          icon: const Icon(Icons.auto_awesome, size: 16),
                                          label: const Text("✨ Inspirer une prière", style: TextStyle(fontSize: 12)),
                                        ),
                                    ],
                                  ),

                                  // Variation selector chips
                                  if (dynamicPrayers!.length > 1) ...[
                                    const SizedBox(height: 8),
                                    SingleChildScrollView(
                                      scrollDirection: Axis.horizontal,
                                      child: Row(
                                        children: List.generate(dynamicPrayers!.length, (idx) {
                                          final isSel = selectedPrayerIndex == idx;
                                          final label = idx == 0
                                              ? "Intimité"
                                              : idx == 1
                                                  ? "Déclaration"
                                                  : idx == 2
                                                      ? "Louange"
                                                      : "Inspirée ${idx - 2}";
                                          return Padding(
                                            padding: const EdgeInsets.only(right: 8.0),
                                            child: ChoiceChip(
                                              label: Text(label, style: const TextStyle(fontSize: 12)),
                                              selected: isSel,
                                              onSelected: (_) {
                                                setModalState(() {
                                                  selectedPrayerIndex = idx;
                                                });
                                              },
                                            ),
                                          );
                                        }),
                                      ),
                                    ),
                                  ],

                                  const SizedBox(height: 10),
                                  AnimatedSwitcher(
                                    duration: const Duration(milliseconds: 300),
                                    child: Text(
                                      dynamicPrayers![selectedPrayerIndex],
                                      key: ValueKey<int>(selectedPrayerIndex),
                                      style: const TextStyle(height: 1.5, fontSize: 14),
                                    ),
                                  ),
                                ],
                              ),
                            ),
                          ),
                        ],
                      ],
                    ),
                  ),
                );
              },
            );
          },
        );
      },
    );
  }

  Widget _buildSoundscapeChip(
    String type,
    String label,
    IconData icon,
    MeditationState state,
    MeditationNotifier notifier,
    ColorScheme colorScheme,
  ) {
    final isSelected = state.activeSoundscape == type;
    return ChoiceChip(
      selected: isSelected,
      label: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          Icon(icon, size: 14, color: isSelected ? colorScheme.onPrimaryContainer : null),
          const SizedBox(width: 4),
          Text(label, style: const TextStyle(fontSize: 11)),
        ],
      ),
      onSelected: (_) => notifier.setSoundscape(type),
    );
  }
}
