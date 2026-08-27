import 'dart:math';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../data/models/memorized_verse.dart';
import '../../../data/providers/bethel_providers.dart';

class FlashcardsScreen extends ConsumerStatefulWidget {
  const FlashcardsScreen({super.key});

  @override
  ConsumerState<FlashcardsScreen> createState() => _FlashcardsScreenState();
}

class _FlashcardsScreenState extends ConsumerState<FlashcardsScreen> with SingleTickerProviderStateMixin {
  late AnimationController _flipController;
  late Animation<double> _flipAnimation;
  bool _isFront = true;
  int _currentIndex = 0;

  @override
  void initState() {
    super.initState();
    _flipController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 450),
    );
    _flipAnimation = Tween<double>(begin: 0, end: 1).animate(
      CurvedAnimation(parent: _flipController, curve: Curves.easeInOut),
    );
  }

  @override
  void dispose() {
    _flipController.dispose();
    super.dispose();
  }

  void _flipCard() {
    if (_isFront) {
      _flipController.forward();
    } else {
      _flipController.reverse();
    }
    setState(() {
      _isFront = !_isFront;
    });
  }

  void _nextCard(int total) {
    if (total == 0) return;
    if (!_isFront) {
      _flipController.reverse();
      _isFront = true;
    }
    setState(() {
      _currentIndex = (_currentIndex + 1) % total;
    });
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;
    final verses = ref.watch(memorizedVersesProvider);
    final versesNotifier = ref.read(memorizedVersesProvider.notifier);

    if (_currentIndex >= verses.length && verses.isNotEmpty) {
      _currentIndex = 0;
    }

    // Leitner Boxes breakdown
    final b1 = verses.where((v) => v.box == 1).length;
    final b2 = verses.where((v) => v.box == 2).length;
    final b3 = verses.where((v) => v.box == 3).length;
    final b4 = verses.where((v) => v.box == 4).length;
    final b5 = verses.where((v) => v.box == 5).length;

    return Scaffold(
      backgroundColor: theme.scaffoldBackgroundColor,
      appBar: AppBar(
        title: const Text(
          "Mémorisation de Versets",
          style: TextStyle(fontWeight: FontWeight.bold),
        ),
        actions: [
          IconButton(
            icon: const Icon(Icons.add_rounded),
            tooltip: "Ajouter un verset",
            onPressed: () => _openAddVerseDialog(context),
          ),
        ],
      ),
      body: verses.isEmpty
          ? Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Icon(Icons.style_outlined, size: 64, color: colorScheme.outlineVariant),
                  const SizedBox(height: 14),
                  const Text(
                    "Aucun verset dans vos cartes",
                    style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
                  ),
                  const SizedBox(height: 6),
                  const Text("Ajoutez des passages de la Bible à mémoriser."),
                  const SizedBox(height: 16),
                  FilledButton.icon(
                    onPressed: () => _openAddVerseDialog(context),
                    icon: const Icon(Icons.add),
                    label: const Text("Ajouter un premier verset"),
                  ),
                ],
              ),
            )
          : ListView(
              padding: const EdgeInsets.symmetric(horizontal: 20.0, vertical: 12.0),
              children: [
                // 1. Leitner Spaced Repetition Boxes
                Card(
                  elevation: 0,
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(16),
                    side: BorderSide(color: colorScheme.outlineVariant.withValues(alpha: 0.5)),
                  ),
                  color: colorScheme.surface,
                  child: Padding(
                    padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 12),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          "Système de Répétition Espacée (Leitner)",
                          style: theme.textTheme.labelMedium?.copyWith(
                            fontWeight: FontWeight.bold,
                            color: colorScheme.primary,
                          ),
                        ),
                        const SizedBox(height: 10),
                        Row(
                          mainAxisAlignment: MainAxisAlignment.spaceAround,
                          children: [
                            _buildBoxBadge("Boîte 1", b1, Colors.red.shade400),
                            _buildBoxBadge("Boîte 2", b2, Colors.orange.shade400),
                            _buildBoxBadge("Boîte 3", b3, Colors.amber.shade600),
                            _buildBoxBadge("Boîte 4", b4, Colors.blue.shade400),
                            _buildBoxBadge("Boîte 5", b5, Colors.green.shade600),
                          ],
                        ),
                      ],
                    ),
                  ),
                ),
                const SizedBox(height: 20),

                // Card Counter
                Center(
                  child: Text(
                    "Carte ${_currentIndex + 1} sur ${verses.length}",
                    style: theme.textTheme.labelMedium?.copyWith(
                      fontWeight: FontWeight.bold,
                      color: colorScheme.onSurfaceVariant,
                    ),
                  ),
                ),
                const SizedBox(height: 14),

                // 2. Interactive 3D Flipping Card
                GestureDetector(
                  onTap: _flipCard,
                  child: AnimatedBuilder(
                    animation: _flipAnimation,
                    builder: (context, child) {
                      final angle = _flipAnimation.value * pi;
                      final isFrontVisible = angle < (pi / 2);

                      return Transform(
                        transform: Matrix4.identity()
                          ..setEntry(3, 2, 0.0015)
                          ..rotateY(angle),
                        alignment: Alignment.center,
                        child: isFrontVisible
                            ? _buildCardFront(verses[_currentIndex], colorScheme, theme)
                            : Transform(
                                transform: Matrix4.identity()..rotateY(pi),
                                alignment: Alignment.center,
                                child: _buildCardBack(verses[_currentIndex], colorScheme, theme),
                              ),
                      );
                    },
                  ),
                ),
                const SizedBox(height: 24),

                // 3. Action Buttons (À Revoir vs Je Sais !)
                Row(
                  children: [
                    Expanded(
                      child: OutlinedButton.icon(
                        onPressed: () {
                          versesNotifier.recordReview(verses[_currentIndex].id, false);
                          ScaffoldMessenger.of(context).showSnackBar(
                            const SnackBar(content: Text("Verset remis en Boîte 1 pour révision")),
                          );
                          _nextCard(verses.length);
                        },
                        icon: const Icon(Icons.refresh_rounded, color: Colors.red),
                        label: const Text(
                          "À revoir",
                          style: TextStyle(color: Colors.red, fontWeight: FontWeight.bold),
                        ),
                        style: OutlinedButton.styleFrom(
                          minimumSize: const Size(double.infinity, 50),
                          side: const BorderSide(color: Colors.red),
                          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(14)),
                        ),
                      ),
                    ),
                    const SizedBox(width: 14),
                    Expanded(
                      child: FilledButton.icon(
                        onPressed: () {
                          versesNotifier.recordReview(verses[_currentIndex].id, true);
                          ScaffoldMessenger.of(context).showSnackBar(
                            const SnackBar(content: Text("Bravo ! Progression dans le niveau supérieur")),
                          );
                          _nextCard(verses.length);
                        },
                        icon: const Icon(Icons.check_rounded),
                        label: const Text(
                          "Je sais !",
                          style: TextStyle(fontWeight: FontWeight.bold),
                        ),
                        style: FilledButton.styleFrom(
                          minimumSize: const Size(double.infinity, 50),
                          backgroundColor: Colors.green.shade700,
                          foregroundColor: Colors.white,
                          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(14)),
                        ),
                      ),
                    ),
                  ],
                ),
                const SizedBox(height: 16),
                Center(
                  child: TextButton.icon(
                    onPressed: () => _nextCard(verses.length),
                    icon: const Icon(Icons.skip_next_rounded),
                    label: const Text("Passer à la carte suivante"),
                  ),
                ),
              ],
            ),
    );
  }

  Widget _buildBoxBadge(String label, int count, Color color) {
    return Column(
      children: [
        Container(
          width: 32,
          height: 32,
          decoration: BoxDecoration(
            color: color.withValues(alpha: 0.15),
            shape: BoxShape.circle,
            border: Border.all(color: color),
          ),
          child: Center(
            child: Text(
              "$count",
              style: TextStyle(fontWeight: FontWeight.bold, color: color, fontSize: 13),
            ),
          ),
        ),
        const SizedBox(height: 4),
        Text(label, style: const TextStyle(fontSize: 10, color: Colors.grey)),
      ],
    );
  }

  Widget _buildCardFront(MemorizedVerse verse, ColorScheme colorScheme, ThemeData theme) {
    return Card(
      elevation: 4,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(24),
        side: BorderSide(color: colorScheme.primary.withValues(alpha: 0.4), width: 1.5),
      ),
      color: colorScheme.surface,
      child: Container(
        height: 280,
        padding: const EdgeInsets.all(24.0),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
              decoration: BoxDecoration(
                color: colorScheme.primaryContainer,
                borderRadius: BorderRadius.circular(8),
              ),
              child: Text(
                "Boîte ${verse.box} • ${verse.category}",
                style: TextStyle(fontSize: 11, fontWeight: FontWeight.bold, color: colorScheme.onPrimaryContainer),
              ),
            ),
            const Spacer(),
            Text(
              verse.reference,
              style: theme.textTheme.headlineMedium?.copyWith(
                fontWeight: FontWeight.bold,
                color: colorScheme.primary,
              ),
              textAlign: TextAlign.center,
            ),
            const Spacer(),
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Icon(Icons.touch_app_rounded, size: 16, color: colorScheme.onSurfaceVariant),
                const SizedBox(width: 6),
                Text(
                  "Touchez pour révéler le texte",
                  style: TextStyle(fontSize: 12, color: colorScheme.onSurfaceVariant),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildCardBack(MemorizedVerse verse, ColorScheme colorScheme, ThemeData theme) {
    return Card(
      elevation: 4,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(24),
        side: BorderSide(color: colorScheme.primary, width: 2),
      ),
      color: colorScheme.primaryContainer,
      child: Container(
        height: 280,
        padding: const EdgeInsets.all(24.0),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text(
              verse.reference,
              style: TextStyle(
                fontWeight: FontWeight.bold,
                fontSize: 14,
                color: colorScheme.onPrimaryContainer,
              ),
            ),
            const SizedBox(height: 12),
            Expanded(
              child: Center(
                child: SingleChildScrollView(
                  child: Text(
                    "« ${verse.text} »",
                    style: theme.textTheme.bodyLarge?.copyWith(
                      fontSize: 17,
                      fontStyle: FontStyle.italic,
                      height: 1.5,
                      color: colorScheme.onPrimaryContainer,
                    ),
                    textAlign: TextAlign.center,
                  ),
                ),
              ),
            ),
            const SizedBox(height: 8),
            Text(
              "Touchez pour retourner",
              style: TextStyle(fontSize: 11, color: colorScheme.onPrimaryContainer.withValues(alpha: 0.7)),
            ),
          ],
        ),
      ),
    );
  }

  void _openAddVerseDialog(BuildContext context) {
    final refController = TextEditingController();
    final textController = TextEditingController();
    final catController = TextEditingController(text: "Foi");

    showDialog(
      context: context,
      builder: (ctx) {
        return AlertDialog(
          title: const Text("Ajouter une carte"),
          content: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              TextField(
                controller: refController,
                decoration: const InputDecoration(
                  labelText: "Référence (ex: Psaumes 23:1)",
                  border: OutlineInputBorder(),
                ),
              ),
              const SizedBox(height: 12),
              TextField(
                controller: textController,
                maxLines: 3,
                decoration: const InputDecoration(
                  labelText: "Texte biblique complet",
                  border: OutlineInputBorder(),
                ),
              ),
              const SizedBox(height: 12),
              TextField(
                controller: catController,
                decoration: const InputDecoration(
                  labelText: "Thème ou Catégorie",
                  border: OutlineInputBorder(),
                ),
              ),
            ],
          ),
          actions: [
            TextButton(onPressed: () => Navigator.pop(ctx), child: const Text("Annuler")),
            FilledButton(
              onPressed: () {
                if (refController.text.trim().isNotEmpty && textController.text.trim().isNotEmpty) {
                  ref.read(memorizedVersesProvider.notifier).addVerse(
                    reference: refController.text.trim(),
                    text: textController.text.trim(),
                    category: catController.text.trim(),
                  );
                  Navigator.pop(ctx);
                }
              },
              child: const Text("Ajouter"),
            ),
          ],
        );
      },
    );
  }
}
