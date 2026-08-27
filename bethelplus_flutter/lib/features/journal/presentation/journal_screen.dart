import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:intl/intl.dart';

import '../../../data/models/prayer_journal.dart';
import '../../../data/providers/bethel_providers.dart';

class JournalScreen extends ConsumerStatefulWidget {
  const JournalScreen({super.key});

  @override
  ConsumerState<JournalScreen> createState() => _JournalScreenState();
}

class _JournalScreenState extends ConsumerState<JournalScreen> {
  int _filterIndex = 0; // 0 = Toutes, 1 = En attente, 2 = Exaucées

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;
    final entries = ref.watch(journalProvider);
    final journalNotifier = ref.read(journalProvider.notifier);

    // Filter
    final filteredEntries = entries.where((e) {
      if (_filterIndex == 1) return !e.isAnswered;
      if (_filterIndex == 2) return e.isAnswered;
      return true;
    }).toList();

    return Scaffold(
      backgroundColor: theme.scaffoldBackgroundColor,
      appBar: AppBar(
        title: const Text(
          "Journal de Prière",
          style: TextStyle(fontWeight: FontWeight.bold),
        ),
      ),
      floatingActionButton: FloatingActionButton.extended(
        onPressed: () => _openNewPrayerModal(context),
        icon: const Icon(Icons.edit_note_rounded),
        label: const Text("Nouvelle Prière"),
        backgroundColor: colorScheme.primary,
        foregroundColor: colorScheme.onPrimary,
      ),
      body: Column(
        children: [
          // Filter Tabs
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 16.0, vertical: 8.0),
            child: Row(
              children: [
                _buildFilterChip("Toutes (${entries.length})", 0, colorScheme),
                const SizedBox(width: 8),
                _buildFilterChip("En attente (${entries.where((e) => !e.isAnswered).length})", 1, colorScheme),
                const SizedBox(width: 8),
                _buildFilterChip("Exaucées (${entries.where((e) => e.isAnswered).length})", 2, colorScheme),
              ],
            ),
          ),

          // Entries List
          Expanded(
            child: filteredEntries.isEmpty
                ? Center(
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Icon(Icons.book_outlined, size: 64, color: colorScheme.outlineVariant),
                        const SizedBox(height: 12),
                        Text(
                          "Aucune prière dans cette section",
                          style: theme.textTheme.titleMedium?.copyWith(color: colorScheme.onSurfaceVariant),
                        ),
                        const SizedBox(height: 4),
                        Text(
                          "Déposez vos requêtes au pied de la croix.",
                          style: theme.textTheme.bodySmall?.copyWith(color: colorScheme.onSurfaceVariant),
                        ),
                      ],
                    ),
                  )
                : ListView.builder(
                    padding: const EdgeInsets.only(left: 16, right: 16, top: 8, bottom: 90),
                    itemCount: filteredEntries.length,
                    itemBuilder: (context, index) {
                      final item = filteredEntries[index];
                      final dateStr = DateFormat("d MMMM yyyy", "fr").format(
                        DateTime.fromMillisecondsSinceEpoch(item.createdAt),
                      );

                      return Card(
                        elevation: 1,
                        margin: const EdgeInsets.only(bottom: 12),
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(16),
                          side: BorderSide(
                            color: item.isAnswered
                                ? Colors.green.withValues(alpha: 0.5)
                                : colorScheme.outlineVariant.withValues(alpha: 0.4),
                          ),
                        ),
                        color: colorScheme.surface,
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
                                      item.category,
                                      style: TextStyle(
                                        fontSize: 11,
                                        fontWeight: FontWeight.bold,
                                        color: colorScheme.onPrimaryContainer,
                                      ),
                                    ),
                                  ),
                                  Text(
                                    dateStr,
                                    style: theme.textTheme.bodySmall?.copyWith(color: colorScheme.onSurfaceVariant),
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
                              const SizedBox(height: 6),
                              Text(
                                item.content,
                                style: theme.textTheme.bodyMedium?.copyWith(
                                  color: colorScheme.onSurfaceVariant,
                                  height: 1.5,
                                ),
                              ),

                              // Testimony section if answered
                              if (item.isAnswered && item.testimony != null) ...[
                                const SizedBox(height: 12),
                                Container(
                                  width: double.infinity,
                                  padding: const EdgeInsets.all(12),
                                  decoration: BoxDecoration(
                                    color: Colors.green.withValues(alpha: 0.1),
                                    borderRadius: BorderRadius.circular(12),
                                    border: Border.all(color: Colors.green.withValues(alpha: 0.3)),
                                  ),
                                  child: Column(
                                    crossAxisAlignment: CrossAxisAlignment.start,
                                    children: [
                                      const Row(
                                        children: [
                                          Icon(Icons.check_circle_rounded, color: Colors.green, size: 16),
                                          SizedBox(width: 6),
                                          Text(
                                            "Prière Exaucée — Témoignage :",
                                            style: TextStyle(
                                              fontWeight: FontWeight.bold,
                                              fontSize: 12,
                                              color: Colors.green,
                                            ),
                                          ),
                                        ],
                                      ),
                                      const SizedBox(height: 4),
                                      Text(
                                        item.testimony!,
                                        style: const TextStyle(fontSize: 13, fontStyle: FontStyle.italic),
                                      ),
                                    ],
                                  ),
                                ),
                              ],

                              const SizedBox(height: 12),
                              Row(
                                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                                children: [
                                  if (!item.isAnswered)
                                    TextButton.icon(
                                      onPressed: () => _openTestimonyDialog(context, item),
                                      icon: const Icon(Icons.check_circle_outline_rounded, size: 16, color: Colors.green),
                                      label: const Text(
                                        "Marquer comme exaucée",
                                        style: TextStyle(color: Colors.green, fontSize: 12, fontWeight: FontWeight.bold),
                                      ),
                                    )
                                  else
                                    const SizedBox.shrink(),
                                  IconButton(
                                    icon: const Icon(Icons.delete_outline_rounded, size: 20, color: Colors.grey),
                                    onPressed: () => journalNotifier.deleteEntry(item.id),
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

  Widget _buildFilterChip(String label, int index, ColorScheme colorScheme) {
    final isSelected = _filterIndex == index;
    return FilterChip(
      selected: isSelected,
      label: Text(label),
      onSelected: (_) {
        setState(() {
          _filterIndex = index;
        });
      },
      selectedColor: colorScheme.primaryContainer,
      checkmarkColor: colorScheme.onPrimaryContainer,
      labelStyle: TextStyle(
        fontSize: 12,
        fontWeight: isSelected ? FontWeight.bold : FontWeight.normal,
        color: isSelected ? colorScheme.onPrimaryContainer : null,
      ),
    );
  }

  // --- New Prayer Modal ---
  void _openNewPrayerModal(BuildContext context) {
    final titleController = TextEditingController();
    final contentController = TextEditingController();
    String selectedCategory = "Requête";
    final categories = ["Requête", "Gratitude", "Famille", "Guérison", "Combat Spirituel", "Sagesse"];

    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      shape: const RoundedRectangleBorder(borderRadius: BorderRadius.vertical(top: Radius.circular(24))),
      builder: (ctx) {
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
                  const Text(
                    "Nouvelle Requête de Prière",
                    style: TextStyle(fontWeight: FontWeight.bold, fontSize: 20),
                  ),
                  const SizedBox(height: 14),
                  TextField(
                    controller: titleController,
                    decoration: const InputDecoration(
                      labelText: "Sujet de prière",
                      border: OutlineInputBorder(),
                    ),
                  ),
                  const SizedBox(height: 12),
                  TextField(
                    controller: contentController,
                    maxLines: 4,
                    decoration: const InputDecoration(
                      labelText: "Détail de votre prière ou verset d'appui...",
                      border: OutlineInputBorder(),
                    ),
                  ),
                  const SizedBox(height: 12),
                  const Text("Catégorie :", style: TextStyle(fontWeight: FontWeight.w600, fontSize: 13)),
                  const SizedBox(height: 6),
                  Wrap(
                    spacing: 8,
                    children: categories.map((cat) {
                      final isCatSelected = selectedCategory == cat;
                      return ChoiceChip(
                        selected: isCatSelected,
                        label: Text(cat, style: const TextStyle(fontSize: 12)),
                        onSelected: (_) {
                          setModalState(() {
                            selectedCategory = cat;
                          });
                        },
                      );
                    }).toList(),
                  ),
                  const SizedBox(height: 20),
                  FilledButton(
                    onPressed: () {
                      if (titleController.text.trim().isNotEmpty) {
                        ref.read(journalProvider.notifier).addEntry(
                          title: titleController.text.trim(),
                          content: contentController.text.trim(),
                          category: selectedCategory,
                        );
                        Navigator.pop(ctx);
                      }
                    },
                    style: FilledButton.styleFrom(minimumSize: const Size(double.infinity, 50)),
                    child: const Text("Déposer cette prière"),
                  ),
                ],
              ),
            );
          },
        );
      },
    );
  }

  // --- Mark Answered & Add Testimony Dialog ---
  void _openTestimonyDialog(BuildContext context, PrayerJournal entry) {
    final testimonyController = TextEditingController();

    showDialog(
      context: context,
      builder: (ctx) {
        return AlertDialog(
          title: const Text("Témoignage de Reconnaissance"),
          content: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              Text(
                "Comment Dieu a-t-Il répondu à votre prière « ${entry.title} » ?",
                style: const TextStyle(fontSize: 14),
              ),
              const SizedBox(height: 12),
              TextField(
                controller: testimonyController,
                maxLines: 3,
                decoration: const InputDecoration(
                  hintText: "Racontez l'exaucement ou le bienfait reçu...",
                  border: OutlineInputBorder(),
                ),
              ),
            ],
          ),
          actions: [
            TextButton(onPressed: () => Navigator.pop(ctx), child: const Text("Annuler")),
            FilledButton(
              onPressed: () {
                ref.read(journalProvider.notifier).markAnswered(
                  entry.id,
                  testimonyController.text.trim().isEmpty
                      ? "Dieu a répondu avec fidélité à cette prière."
                      : testimonyController.text.trim(),
                );
                Navigator.pop(ctx);
              },
              child: const Text("Confirmer l'exaucement"),
            ),
          ],
        );
      },
    );
  }
}
