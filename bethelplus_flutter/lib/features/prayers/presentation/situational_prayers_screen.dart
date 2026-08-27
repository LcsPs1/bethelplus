import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../data/models/situational_prayer.dart';
import '../../../data/models/meditation_item.dart';
import '../../../data/providers/bethel_providers.dart';

class SituationalPrayersScreen extends ConsumerStatefulWidget {
  const SituationalPrayersScreen({super.key});

  @override
  ConsumerState<SituationalPrayersScreen> createState() => _SituationalPrayersScreenState();
}

class _SituationalPrayersScreenState extends ConsumerState<SituationalPrayersScreen> {
  String _selectedCategory = "Toutes";
  final TextEditingController _searchController = TextEditingController();
  String _searchQuery = "";

  @override
  void dispose() {
    _searchController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    // Filter items
    var items = SituationalPrayersRepository.items;
    if (_selectedCategory != "Toutes") {
      items = items.where((p) => p.category == _selectedCategory).toList();
    }
    if (_searchQuery.trim().isNotEmpty) {
      final q = _searchQuery.toLowerCase();
      items = items.where((p) {
        return p.title.toLowerCase().contains(q) ||
            p.scriptureRef.toLowerCase().contains(q) ||
            p.prayerText.toLowerCase().contains(q) ||
            p.tags.any((t) => t.toLowerCase().contains(q));
      }).toList();
    }

    return Scaffold(
      backgroundColor: theme.scaffoldBackgroundColor,
      appBar: AppBar(
        title: const Text(
          "Prières de Situation",
          style: TextStyle(fontWeight: FontWeight.bold),
        ),
      ),
      floatingActionButton: FloatingActionButton.extended(
        onPressed: () => _openAiPrayerGenerator(context),
        icon: const Icon(Icons.auto_awesome_rounded),
        label: const Text("Prière IA"),
        backgroundColor: Colors.amber.shade700,
        foregroundColor: Colors.white,
      ),
      body: Column(
        children: [
          // 1. Search Bar
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 16.0, vertical: 8.0),
            child: TextField(
              controller: _searchController,
              decoration: InputDecoration(
                hintText: "Rechercher par besoin, verset, émotion...",
                prefixIcon: const Icon(Icons.search_rounded),
                suffixIcon: _searchQuery.isNotEmpty
                    ? IconButton(
                        icon: const Icon(Icons.clear_rounded),
                        onPressed: () {
                          _searchController.clear();
                          setState(() {
                            _searchQuery = "";
                          });
                        },
                      )
                    : null,
                contentPadding: const EdgeInsets.symmetric(vertical: 0, horizontal: 16),
                border: OutlineInputBorder(borderRadius: BorderRadius.circular(16)),
              ),
              onChanged: (val) {
                setState(() {
                  _searchQuery = val;
                });
              },
            ),
          ),

          // 2. Categories Horizontal Chips
          SingleChildScrollView(
            scrollDirection: Axis.horizontal,
            padding: const EdgeInsets.symmetric(horizontal: 16.0, vertical: 6.0),
            child: Row(
              children: [
                _buildCategoryChip("Toutes", colorScheme),
                ...SituationalPrayersRepository.categories.map((cat) {
                  return _buildCategoryChip(cat.title, colorScheme);
                }),
              ],
            ),
          ),

          // 3. Prayers List
          Expanded(
            child: items.isEmpty
                ? Center(
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Icon(Icons.volunteer_activism_outlined, size: 64, color: colorScheme.outlineVariant),
                        const SizedBox(height: 12),
                        const Text(
                          "Aucune prière trouvée",
                          style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
                        ),
                        const SizedBox(height: 4),
                        const Text("Essayez un autre mot-clé ou générez une prière avec l'IA."),
                        const SizedBox(height: 16),
                        FilledButton.icon(
                          onPressed: () => _openAiPrayerGenerator(context),
                          icon: const Icon(Icons.auto_awesome_rounded),
                          label: const Text("Créer avec l'IA"),
                        ),
                      ],
                    ),
                  )
                : ListView.builder(
                    padding: const EdgeInsets.only(left: 16, right: 16, top: 8, bottom: 90),
                    itemCount: items.length,
                    itemBuilder: (context, index) {
                      final prayer = items[index];
                      return Card(
                        elevation: 1,
                        margin: const EdgeInsets.only(bottom: 14),
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(20),
                          side: BorderSide(color: colorScheme.outlineVariant.withValues(alpha: 0.5)),
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
                                      color: colorScheme.primaryContainer,
                                      borderRadius: BorderRadius.circular(8),
                                    ),
                                    child: Text(
                                      prayer.category,
                                      style: TextStyle(
                                        fontSize: 11,
                                        fontWeight: FontWeight.bold,
                                        color: colorScheme.onPrimaryContainer,
                                      ),
                                    ),
                                  ),
                                  Text(
                                    prayer.scriptureRef,
                                    style: TextStyle(
                                      fontWeight: FontWeight.bold,
                                      fontSize: 12,
                                      color: colorScheme.primary,
                                    ),
                                  ),
                                ],
                              ),
                              const SizedBox(height: 10),
                              Text(
                                prayer.title,
                                style: theme.textTheme.titleMedium?.copyWith(
                                  fontWeight: FontWeight.bold,
                                  color: colorScheme.onSurface,
                                ),
                              ),
                              const SizedBox(height: 8),

                              // Scripture Box
                              Container(
                                width: double.infinity,
                                padding: const EdgeInsets.all(12),
                                decoration: BoxDecoration(
                                  color: colorScheme.surfaceContainerHighest.withValues(alpha: 0.4),
                                  borderRadius: BorderRadius.circular(12),
                                ),
                                child: Text(
                                  "« ${prayer.scriptureText} »",
                                  style: const TextStyle(fontStyle: FontStyle.italic, fontSize: 13),
                                ),
                              ),
                              const SizedBox(height: 10),

                              // Prayer Text
                              Text(
                                prayer.prayerText,
                                style: theme.textTheme.bodyMedium?.copyWith(
                                  color: colorScheme.onSurface,
                                  height: 1.5,
                                ),
                              ),
                              const SizedBox(height: 10),

                              // Declaration of Faith
                              Container(
                                width: double.infinity,
                                padding: const EdgeInsets.all(10),
                                decoration: BoxDecoration(
                                  color: Colors.amber.withValues(alpha: 0.1),
                                  borderRadius: BorderRadius.circular(10),
                                  border: Border.all(color: Colors.amber.withValues(alpha: 0.3)),
                                ),
                                child: Row(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: [
                                    const Icon(Icons.shield_rounded, color: Colors.amber, size: 16),
                                    const SizedBox(width: 8),
                                    Expanded(
                                      child: Text(
                                        "Déclaration : ${prayer.declaration}",
                                        style: TextStyle(
                                          fontSize: 12,
                                          fontWeight: FontWeight.w600,
                                          color: colorScheme.onSurface,
                                        ),
                                      ),
                                    ),
                                  ],
                                ),
                              ),
                              const SizedBox(height: 14),

                              // Action Buttons
                              Row(
                                children: [
                                  Expanded(
                                    child: OutlinedButton.icon(
                                      onPressed: () {
                                        ref.read(journalProvider.notifier).addEntry(
                                          title: prayer.title,
                                          content: "${prayer.scriptureText} (${prayer.scriptureRef})\n\n${prayer.prayerText}\n\nDéclaration : ${prayer.declaration}",
                                          category: prayer.category,
                                        );
                                        ScaffoldMessenger.of(context).showSnackBar(
                                          const SnackBar(content: Text("Prière ajoutée à votre journal !")),
                                        );
                                      },
                                      icon: const Icon(Icons.bookmark_add_outlined, size: 16),
                                      label: const Text("Au Journal", style: TextStyle(fontSize: 12)),
                                    ),
                                  ),
                                  const SizedBox(width: 10),
                                  Expanded(
                                    child: FilledButton.icon(
                                      onPressed: () {
                                        final med = MeditationItem(
                                          id: "med_situation_${prayer.id}",
                                          title: prayer.title,
                                          theme: MeditationTheme.faith,
                                          scriptureReference: prayer.scriptureRef,
                                          scriptureText: prayer.scriptureText,
                                          reflection: "${prayer.subtitle}\n\nDéclaration : ${prayer.declaration}",
                                          prayerVariations: [prayer.prayerText],
                                          estimatedDurationMinutes: prayer.recommendedDurationMinutes,
                                        );
                                        ref.read(meditationProvider.notifier).startMeditation(med);
                                        context.go('/meditation');
                                      },
                                      icon: const Icon(Icons.self_improvement_rounded, size: 16),
                                      label: const Text("Prier", style: TextStyle(fontSize: 12, fontWeight: FontWeight.bold)),
                                    ),
                                  ),
                                ],
                              ),
                            ],
                          ),
                        ),
                      );
                    },
                  ),
          ),
        ],
      ),
    );
  }

  Widget _buildCategoryChip(String title, ColorScheme colorScheme) {
    final isSelected = _selectedCategory == title;
    return Padding(
      padding: const EdgeInsets.only(right: 6.0),
      child: FilterChip(
        selected: isSelected,
        label: Text(title),
        onSelected: (_) {
          setState(() {
            _selectedCategory = title;
          });
        },
        selectedColor: colorScheme.primaryContainer,
        checkmarkColor: colorScheme.onPrimaryContainer,
        labelStyle: TextStyle(
          fontSize: 12,
          fontWeight: isSelected ? FontWeight.bold : FontWeight.normal,
          color: isSelected ? colorScheme.onPrimaryContainer : null,
        ),
      ),
    );
  }

  // --- AI Prayer Generator Modal ---
  void _openAiPrayerGenerator(BuildContext context) {
    final situationController = TextEditingController();

    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      shape: const RoundedRectangleBorder(borderRadius: BorderRadius.vertical(top: Radius.circular(24))),
      builder: (ctx) {
        bool isLoading = false;
        Map<String, String>? generatedPrayer;

        return StatefulBuilder(
          builder: (ctx, setModalState) {
            return Padding(
              padding: EdgeInsets.only(
                left: 20,
                right: 20,
                top: 20,
                bottom: MediaQuery.of(context).viewInsets.bottom + 20,
              ),
              child: Column(
                mainAxisSize: MainAxisSize.min,
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const Row(
                    children: [
                      Icon(Icons.auto_awesome_rounded, color: Colors.amber),
                      SizedBox(width: 8),
                      Text(
                        "Générateur de Prière Inspirée IA",
                        style: TextStyle(fontWeight: FontWeight.bold, fontSize: 18),
                      ),
                    ],
                  ),
                  const SizedBox(height: 10),
                  if (generatedPrayer == null) ...[
                    const Text(
                      "Décrivez votre besoin, votre émotion ou l'épreuve traversée (ex: paix avant un examen, guérison d'un proche, protection lors d'un voyage) :",
                      style: TextStyle(fontSize: 13),
                    ),
                    const SizedBox(height: 12),
                    TextField(
                      controller: situationController,
                      maxLines: 3,
                      decoration: const InputDecoration(
                        hintText: "Ex : Seigneur, j'ai peur pour mon avenir professionnel et j'ai besoin de Ton secours...",
                        border: OutlineInputBorder(),
                      ),
                    ),
                    const SizedBox(height: 16),
                    if (isLoading)
                      const Center(
                        child: Padding(
                          padding: EdgeInsets.all(12.0),
                          child: CircularProgressIndicator(),
                        ),
                      )
                    else
                      FilledButton.icon(
                        onPressed: () async {
                          if (situationController.text.trim().isNotEmpty) {
                            setModalState(() {
                              isLoading = true;
                            });
                            final prayer = await ref.read(geminiServiceProvider).generateSituationalPrayer(
                              situation: situationController.text.trim(),
                            );
                            setModalState(() {
                              isLoading = false;
                              generatedPrayer = prayer;
                            });
                          }
                        },
                        icon: const Icon(Icons.auto_awesome_rounded),
                        label: const Text("Générer ma prière"),
                        style: FilledButton.styleFrom(minimumSize: const Size(double.infinity, 50)),
                      ),
                  ] else ...[
                    Text(
                      generatedPrayer!['title'] ?? "Prière Inspirée",
                      style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 18),
                    ),
                    const SizedBox(height: 4),
                    Text(
                      generatedPrayer!['scriptureRef'] ?? "",
                      style: TextStyle(fontWeight: FontWeight.bold, color: Theme.of(context).colorScheme.primary),
                    ),
                    const SizedBox(height: 10),
                    Text(
                      "« ${generatedPrayer!['scriptureText']} »",
                      style: const TextStyle(fontStyle: FontStyle.italic, fontSize: 13),
                    ),
                    const SizedBox(height: 10),
                    Text(
                      generatedPrayer!['prayerText'] ?? "",
                      style: const TextStyle(height: 1.5),
                    ),
                    const SizedBox(height: 10),
                    Text(
                      "💡 Déclaration : ${generatedPrayer!['declaration']}",
                      style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 12),
                    ),
                    const SizedBox(height: 20),
                    Row(
                      children: [
                        Expanded(
                          child: OutlinedButton(
                            onPressed: () {
                              ref.read(journalProvider.notifier).addEntry(
                                title: generatedPrayer!['title'] ?? "Prière IA",
                                content: "${generatedPrayer!['scriptureText']} (${generatedPrayer!['scriptureRef']})\n\n${generatedPrayer!['prayerText']}\n\nDéclaration : ${generatedPrayer!['declaration']}",
                                category: "Prière IA",
                              );
                              Navigator.pop(ctx);
                              ScaffoldMessenger.of(context).showSnackBar(
                                const SnackBar(content: Text("Prière enregistrée dans votre journal !")),
                              );
                            },
                            child: const Text("Au Journal"),
                          ),
                        ),
                        const SizedBox(width: 10),
                        Expanded(
                          child: FilledButton(
                            onPressed: () {
                              final med = MeditationItem(
                                id: "med_ai_${DateTime.now().millisecondsSinceEpoch}",
                                title: generatedPrayer!['title'] ?? "Prière Inspirée",
                                theme: MeditationTheme.faith,
                                scriptureReference: generatedPrayer!['scriptureRef'] ?? "",
                                scriptureText: generatedPrayer!['scriptureText'] ?? "",
                                reflection: "Déclaration de Foi : ${generatedPrayer!['declaration']}",
                                prayerVariations: [generatedPrayer!['prayerText'] ?? ""],
                                estimatedDurationMinutes: 5,
                              );
                              ref.read(meditationProvider.notifier).startMeditation(med);
                              Navigator.pop(ctx);
                              context.go('/meditation');
                            },
                            child: const Text("Prier maintenant"),
                          ),
                        ),
                      ],
                    ),
                  ],
                ],
              ),
            );
          },
        );
      },
    );
  }
}
