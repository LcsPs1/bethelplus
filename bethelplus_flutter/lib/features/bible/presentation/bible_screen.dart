import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../data/models/bible_data.dart';
import '../../../data/models/bible_verse.dart';
import '../../../data/models/meditation_item.dart';
import '../../../data/providers/bethel_providers.dart';

class BibleScreen extends ConsumerStatefulWidget {
  const BibleScreen({super.key});

  @override
  ConsumerState<BibleScreen> createState() => _BibleScreenState();
}

class _BibleScreenState extends ConsumerState<BibleScreen> {
  final TextEditingController _searchController = TextEditingController();

  @override
  void dispose() {
    _searchController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;
    final bibleState = ref.watch(bibleProvider);
    final bibleNotifier = ref.read(bibleProvider.notifier);

    return Scaffold(
      backgroundColor: theme.scaffoldBackgroundColor,
      appBar: AppBar(
        title: InkWell(
          onTap: () => _openBookChapterSelector(context, bibleState, bibleNotifier),
          borderRadius: BorderRadius.circular(12),
          child: Padding(
            padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 6),
            child: Row(
              mainAxisSize: MainAxisSize.min,
              children: [
                Text(
                  "${bibleState.selectedBook.name} ${bibleState.selectedChapter}",
                  style: theme.textTheme.titleLarge?.copyWith(
                    fontWeight: FontWeight.bold,
                    color: colorScheme.onSurface,
                  ),
                ),
                const SizedBox(width: 4),
                Icon(Icons.arrow_drop_down_rounded, color: colorScheme.primary),
              ],
            ),
          ),
        ),
        actions: [
          // Version toggle (LSG / DRB)
          TextButton(
            onPressed: () {
              final newVersion = bibleState.version == "LSG" ? "DRB" : "LSG";
              bibleNotifier.setVersion(newVersion);
            },
            child: Container(
              padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
              decoration: BoxDecoration(
                color: colorScheme.primaryContainer,
                borderRadius: BorderRadius.circular(8),
              ),
              child: Text(
                bibleState.version,
                style: TextStyle(
                  fontWeight: FontWeight.bold,
                  fontSize: 12,
                  color: colorScheme.onPrimaryContainer,
                ),
              ),
            ),
          ),
          // Search button
          IconButton(
            icon: const Icon(Icons.search_rounded),
            onPressed: () => _openSearchDialog(context, bibleNotifier),
          ),
          // Quiz button
          IconButton(
            icon: const Icon(Icons.quiz_rounded),
            onPressed: () => _openQuizSheet(context),
          ),
        ],
      ),
      body: Stack(
        children: [
          Column(
            children: [
              // Verses List
              Expanded(
                child: ListView.builder(
                  padding: EdgeInsets.only(
                    left: 18,
                    right: 18,
                    top: 12,
                    bottom: (bibleState.isTtsPlaying || bibleState.isTtsPaused) ? 140 : 80,
                  ),
                  itemCount: bibleState.verses.length,
                  itemBuilder: (context, index) {
                    final verse = bibleState.verses[index];
                    final isHighlighted = bibleState.highlightedVerses.containsKey(verse.id);
                    final highlightHex = bibleState.highlightedVerses[verse.id];
                    final isBookmarked = bibleState.bookmarkedVerses.contains(verse.id);
                    final hasNote = bibleState.verseNotes.containsKey(verse.id);
                    final isCurrentlySpeaking = bibleState.isTtsPlaying && bibleState.currentTtsVerseIndex == index;

                    Color? highlightColor;
                    if (isHighlighted && highlightHex != null) {
                      highlightColor = Color(int.parse(highlightHex)).withValues(alpha: 0.35);
                    } else if (isCurrentlySpeaking) {
                      highlightColor = colorScheme.primary.withValues(alpha: 0.25);
                    }

                    return InkWell(
                      onTap: () => _openVerseActionsSheet(context, verse, isBookmarked, hasNote),
                      borderRadius: BorderRadius.circular(8),
                      child: Container(
                        padding: const EdgeInsets.symmetric(vertical: 8, horizontal: 8),
                        margin: const EdgeInsets.symmetric(vertical: 2),
                        decoration: BoxDecoration(
                          color: highlightColor,
                          borderRadius: BorderRadius.circular(8),
                          border: isCurrentlySpeaking
                              ? Border.all(color: colorScheme.primary, width: 1.5)
                              : null,
                        ),
                        child: Row(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            // Verse Number
                            SizedBox(
                              width: 28,
                              child: Text(
                                "${verse.verse}",
                                style: theme.textTheme.labelSmall?.copyWith(
                                  fontWeight: FontWeight.bold,
                                  color: colorScheme.primary,
                                ),
                              ),
                            ),
                            // Verse Text
                            Expanded(
                              child: Column(
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  Text(
                                    verse.text,
                                    style: theme.textTheme.bodyLarge?.copyWith(
                                      fontSize: 16.5,
                                      height: 1.6,
                                      color: colorScheme.onSurface,
                                    ),
                                  ),
                                  if (hasNote) ...[
                                    const SizedBox(height: 4),
                                    Container(
                                      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                                      decoration: BoxDecoration(
                                        color: colorScheme.surfaceContainerHighest,
                                        borderRadius: BorderRadius.circular(6),
                                      ),
                                      child: Row(
                                        children: [
                                          Icon(Icons.edit_note_rounded, size: 14, color: colorScheme.primary),
                                          const SizedBox(width: 4),
                                          Expanded(
                                            child: Text(
                                              bibleState.verseNotes[verse.id] ?? "",
                                              style: theme.textTheme.bodySmall?.copyWith(
                                                fontStyle: FontStyle.italic,
                                                color: colorScheme.onSurfaceVariant,
                                              ),
                                            ),
                                          ),
                                        ],
                                      ),
                                    ),
                                  ],
                                ],
                              ),
                            ),
                            if (isBookmarked) ...[
                              const SizedBox(width: 4),
                              Icon(Icons.bookmark_rounded, size: 16, color: colorScheme.primary),
                            ],
                          ],
                        ),
                      ),
                    );
                  },
                ),
              ),

              // Chapter Navigation Bar
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 10),
                decoration: BoxDecoration(
                  color: theme.scaffoldBackgroundColor,
                  border: Border(top: BorderSide(color: colorScheme.outlineVariant.withValues(alpha: 0.4))),
                ),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    TextButton.icon(
                      onPressed: () => bibleNotifier.previousChapter(),
                      icon: const Icon(Icons.arrow_back_ios_rounded, size: 14),
                      label: const Text("Chapitre précédent"),
                    ),
                    IconButton(
                      icon: const Icon(Icons.volume_up_rounded),
                      tooltip: "Écouter le chapitre",
                      onPressed: () => bibleNotifier.startTts(startIndex: 0),
                    ),
                    TextButton.icon(
                      onPressed: () => bibleNotifier.nextChapter(),
                      icon: const Icon(Icons.arrow_forward_ios_rounded, size: 14),
                      label: const Text("Chapitre suivant"),
                    ),
                  ],
                ),
              ),
            ],
          ),

          // Audio Player Floating Bar
          if (bibleState.isTtsPlaying || bibleState.isTtsPaused)
            Positioned(
              left: 16,
              right: 16,
              bottom: 60,
              child: Card(
                elevation: 6,
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
                color: colorScheme.primaryContainer,
                child: Padding(
                  padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 8),
                  child: Row(
                    children: [
                      Container(
                        width: 36,
                        height: 36,
                        decoration: BoxDecoration(
                          color: colorScheme.primary,
                          shape: BoxShape.circle,
                        ),
                        child: Icon(
                          bibleState.isTtsPlaying ? Icons.volume_up_rounded : Icons.volume_mute_rounded,
                          color: Colors.white,
                          size: 20,
                        ),
                      ),
                      const SizedBox(width: 10),
                      Expanded(
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          mainAxisSize: MainAxisSize.min,
                          children: [
                            Text(
                              "${bibleState.selectedBook.name} ${bibleState.selectedChapter} : Verset ${bibleState.currentTtsVerseIndex + 1}",
                              style: theme.textTheme.labelMedium?.copyWith(
                                fontWeight: FontWeight.bold,
                                color: colorScheme.onPrimaryContainer,
                              ),
                            ),
                            Text(
                              "Vitesse: ${bibleState.ttsRate}x",
                              style: theme.textTheme.bodySmall?.copyWith(
                                color: colorScheme.onPrimaryContainer.withValues(alpha: 0.8),
                                fontSize: 11,
                              ),
                            ),
                          ],
                        ),
                      ),
                      IconButton(
                        icon: const Icon(Icons.skip_previous_rounded),
                        onPressed: () => bibleNotifier.previousTtsVerse(),
                      ),
                      IconButton(
                        icon: Icon(
                          bibleState.isTtsPlaying ? Icons.pause_rounded : Icons.play_arrow_rounded,
                        ),
                        onPressed: () => bibleNotifier.toggleTtsPlayPause(),
                      ),
                      IconButton(
                        icon: const Icon(Icons.skip_next_rounded),
                        onPressed: () => bibleNotifier.nextTtsVerse(),
                      ),
                      IconButton(
                        icon: const Icon(Icons.close_rounded, size: 20),
                        onPressed: () => bibleNotifier.stopTts(),
                      ),
                    ],
                  ),
                ),
              ),
            ),
        ],
      ),
    );
  }

  // --- Book & Chapter Selector Bottom Sheet ---
  void _openBookChapterSelector(BuildContext context, BibleState state, BibleNotifier notifier) {
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      backgroundColor: Theme.of(context).scaffoldBackgroundColor,
      shape: const RoundedRectangleBorder(borderRadius: BorderRadius.vertical(top: Radius.circular(24))),
      builder: (ctx) {
        return DefaultTabController(
          length: 2,
          initialIndex: state.selectedBook.testament == "OT" ? 0 : 1,
          child: SizedBox(
            height: MediaQuery.of(context).size.height * 0.75,
            child: Column(
              children: [
                const SizedBox(height: 12),
                Container(width: 40, height: 4, decoration: BoxDecoration(color: Colors.grey.shade400, borderRadius: BorderRadius.circular(2))),
                const SizedBox(height: 10),
                const TabBar(
                  tabs: [
                    Tab(text: "Ancien Testament (39)"),
                    Tab(text: "Nouveau Testament (27)"),
                  ],
                ),
                Expanded(
                  child: TabBarView(
                    children: [
                      _buildBooksList(BibleData.allBooks.where((b) => b.testament == "OT").toList(), state, notifier),
                      _buildBooksList(BibleData.allBooks.where((b) => b.testament == "NT").toList(), state, notifier),
                    ],
                  ),
                ),
              ],
            ),
          ),
        );
      },
    );
  }

  Widget _buildBooksList(List<BibleBookInfo> books, BibleState state, BibleNotifier notifier) {
    return ListView.builder(
      padding: const EdgeInsets.symmetric(vertical: 8),
      itemCount: books.length,
      itemBuilder: (ctx, i) {
        final book = books[i];
        final isSelected = book.id == state.selectedBook.id;
        return ExpansionTile(
          initiallyExpanded: isSelected,
          title: Text(
            book.name,
            style: TextStyle(
              fontWeight: isSelected ? FontWeight.bold : FontWeight.normal,
              color: isSelected ? Theme.of(ctx).colorScheme.primary : null,
            ),
          ),
          subtitle: Text("${book.chaptersCount} chapitres • ${book.category}", style: const TextStyle(fontSize: 12)),
          children: [
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
              child: Wrap(
                spacing: 8,
                runSpacing: 8,
                children: List.generate(book.chaptersCount, (cIndex) {
                  final chapNum = cIndex + 1;
                  final isChapSelected = isSelected && chapNum == state.selectedChapter;
                  return InkWell(
                    onTap: () {
                      notifier.loadChapter(book.id, chapNum);
                      Navigator.pop(ctx);
                    },
                    borderRadius: BorderRadius.circular(8),
                    child: Container(
                      width: 44,
                      height: 38,
                      alignment: Alignment.center,
                      decoration: BoxDecoration(
                        color: isChapSelected
                            ? Theme.of(ctx).colorScheme.primary
                            : Theme.of(ctx).colorScheme.surfaceContainerHighest,
                        borderRadius: BorderRadius.circular(8),
                      ),
                      child: Text(
                        "$chapNum",
                        style: TextStyle(
                          fontWeight: FontWeight.bold,
                          color: isChapSelected ? Colors.white : null,
                        ),
                      ),
                    ),
                  );
                }),
              ),
            ),
          ],
        );
      },
    );
  }

  // --- Verse Actions Bottom Sheet ---
  void _openVerseActionsSheet(BuildContext context, BibleVerse verse, bool isBookmarked, bool hasNote) {
    final colorScheme = Theme.of(context).colorScheme;
    final bibleNotifier = ref.read(bibleProvider.notifier);

    showModalBottomSheet(
      context: context,
      shape: const RoundedRectangleBorder(borderRadius: BorderRadius.vertical(top: Radius.circular(24))),
      builder: (ctx) {
        return SafeArea(
          child: Padding(
            padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 16),
            child: Column(
              mainAxisSize: MainAxisSize.min,
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  "${verse.bookName} ${verse.chapter}:${verse.verse}",
                  style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 18),
                ),
                const SizedBox(height: 6),
                Text(
                  "« ${verse.text} »",
                  style: const TextStyle(fontStyle: FontStyle.italic, fontSize: 14),
                ),
                const SizedBox(height: 16),

                // Surlignage Palette
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceAround,
                  children: [
                    _buildColorDot(ctx, "0xFFFFD54F", "Or", verse.id),
                    _buildColorDot(ctx, "0xFF81C784", "Vert", verse.id),
                    _buildColorDot(ctx, "0xFF64B5F6", "Bleu", verse.id),
                    _buildColorDot(ctx, "0xFFF48FB1", "Rose", verse.id),
                    IconButton(
                      icon: const Icon(Icons.format_color_reset_rounded),
                      tooltip: "Effacer surlignage",
                      onPressed: () {
                        bibleNotifier.toggleHighlight(verse.id, "");
                        Navigator.pop(ctx);
                      },
                    ),
                  ],
                ),
                const Divider(height: 24),

                // Actions List
                ListTile(
                  leading: Icon(
                    isBookmarked ? Icons.bookmark_remove_rounded : Icons.bookmark_add_rounded,
                    color: colorScheme.primary,
                  ),
                  title: Text(isBookmarked ? "Retirer le marque-page" : "Ajouter aux marque-pages"),
                  onTap: () {
                    bibleNotifier.toggleBookmark(verse.id);
                    Navigator.pop(ctx);
                  },
                ),
                ListTile(
                  leading: Icon(Icons.auto_awesome_rounded, color: Colors.amber.shade700),
                  title: const Text("Expliquer ce verset avec l'IA"),
                  onTap: () {
                    Navigator.pop(ctx);
                    _openAiVerseExplanation(context, verse);
                  },
                ),
                ListTile(
                  leading: Icon(Icons.self_improvement_rounded, color: Colors.blue.shade600),
                  title: const Text("Méditer ce passage"),
                  onTap: () {
                    Navigator.pop(ctx);
                    final med = MeditationItem(
                      id: "med_${verse.id}",
                      title: "Méditation : ${verse.bookName} ${verse.chapter}:${verse.verse}",
                      theme: MeditationTheme.faith,
                      scriptureReference: "${verse.bookName} ${verse.chapter}:${verse.verse}",
                      scriptureText: verse.text,
                      reflection: "Méditez ce passage en silence dans votre cœur.",
                      prayerVariations: const [
                        "Seigneur, fais vivre cette parole en moi. Amen.",
                        "Père, ouvre mon esprit à la compréhension de Ta vérité. Amen."
                      ],
                      estimatedDurationMinutes: 5,
                    );
                    ref.read(meditationProvider.notifier).startMeditation(med);
                    context.go('/meditation');
                  },
                ),
                ListTile(
                  leading: Icon(Icons.style_rounded, color: Colors.green.shade600),
                  title: const Text("Mémoriser (Flashcard Leitner)"),
                  onTap: () {
                    ref.read(memorizedVersesProvider.notifier).addVerse(
                      reference: "${verse.bookName} ${verse.chapter}:${verse.verse}",
                      text: verse.text,
                      category: verse.bookName,
                    );
                    Navigator.pop(ctx);
                    ScaffoldMessenger.of(context).showSnackBar(
                      const SnackBar(content: Text("Verset ajouté à vos cartes de mémorisation !")),
                    );
                  },
                ),
                ListTile(
                  leading: Icon(Icons.edit_note_rounded, color: colorScheme.primary),
                  title: Text(hasNote ? "Modifier la note" : "Ajouter une note"),
                  onTap: () {
                    Navigator.pop(ctx);
                    _openNoteDialog(context, verse);
                  },
                ),
              ],
            ),
          ),
        );
      },
    );
  }

  Widget _buildColorDot(BuildContext context, String hex, String name, String verseId) {
    final color = Color(int.parse(hex));
    return InkWell(
      onTap: () {
        ref.read(bibleProvider.notifier).toggleHighlight(verseId, hex);
        Navigator.pop(context);
      },
      customBorder: const CircleBorder(),
      child: Container(
        width: 34,
        height: 34,
        decoration: BoxDecoration(color: color, shape: BoxShape.circle),
      ),
    );
  }

  // --- Note Dialog ---
  void _openNoteDialog(BuildContext context, BibleVerse verse) {
    final existingNote = ref.read(bibleProvider).verseNotes[verse.id] ?? "";
    final controller = TextEditingController(text: existingNote);

    showDialog(
      context: context,
      builder: (ctx) {
        return AlertDialog(
          title: Text("Note sur ${verse.bookName} ${verse.chapter}:${verse.verse}"),
          content: TextField(
            controller: controller,
            maxLines: 4,
            decoration: const InputDecoration(
              hintText: "Écrivez votre réflexion ou prière personnelle...",
              border: OutlineInputBorder(),
            ),
          ),
          actions: [
            TextButton(onPressed: () => Navigator.pop(ctx), child: const Text("Annuler")),
            FilledButton(
              onPressed: () {
                ref.read(bibleProvider.notifier).saveNote(verse.id, controller.text);
                Navigator.pop(ctx);
              },
              child: const Text("Enregistrer"),
            ),
          ],
        );
      },
    );
  }

  // --- Search Dialog ---
  void _openSearchDialog(BuildContext context, BibleNotifier notifier) {
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      shape: const RoundedRectangleBorder(borderRadius: BorderRadius.vertical(top: Radius.circular(24))),
      builder: (ctx) {
        return StatefulBuilder(
          builder: (ctx, setModalState) {
            final state = ref.watch(bibleProvider);
            return SizedBox(
              height: MediaQuery.of(context).size.height * 0.8,
              child: Padding(
                padding: const EdgeInsets.all(16.0),
                child: Column(
                  children: [
                    TextField(
                      controller: _searchController,
                      autofocus: true,
                      decoration: InputDecoration(
                        hintText: "Rechercher un mot, un verset...",
                        prefixIcon: const Icon(Icons.search_rounded),
                        suffixIcon: IconButton(
                          icon: const Icon(Icons.clear_rounded),
                          onPressed: () {
                            _searchController.clear();
                            notifier.searchBible("");
                            setModalState(() {});
                          },
                        ),
                        border: OutlineInputBorder(borderRadius: BorderRadius.circular(14)),
                      ),
                      onChanged: (val) {
                        notifier.searchBible(val);
                        setModalState(() {});
                      },
                    ),
                    const SizedBox(height: 12),
                    Expanded(
                      child: state.searchResults.isEmpty
                          ? Center(
                              child: Text(
                                state.searchQuery.isEmpty
                                    ? "Tapez un mot-clé pour lancer la recherche"
                                    : "Aucun verset trouvé pour « ${state.searchQuery} »",
                                style: const TextStyle(color: Colors.grey),
                              ),
                            )
                          : ListView.builder(
                              itemCount: state.searchResults.length,
                              itemBuilder: (ctx, i) {
                                final res = state.searchResults[i];
                                return ListTile(
                                  title: Text(
                                    "${res.bookName} ${res.chapter}:${res.verse}",
                                    style: const TextStyle(fontWeight: FontWeight.bold),
                                  ),
                                  subtitle: Text(res.text),
                                  onTap: () {
                                    notifier.loadChapter(res.bookId, res.chapter);
                                    Navigator.pop(ctx);
                                  },
                                );
                              },
                            ),
                    ),
                  ],
                ),
              ),
            );
          },
        );
      },
    );
  }

  // --- AI Verse Explanation Sheet ---
  void _openAiVerseExplanation(BuildContext context, BibleVerse verse) {
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      shape: const RoundedRectangleBorder(borderRadius: BorderRadius.vertical(top: Radius.circular(24))),
      builder: (ctx) {
        return FutureBuilder<String>(
          future: ref.read(geminiServiceProvider).explainVerse(
            reference: "${verse.bookName} ${verse.chapter}:${verse.verse}",
            text: verse.text,
          ),
          builder: (ctx, snapshot) {
            return Padding(
              padding: const EdgeInsets.all(20.0),
              child: Column(
                mainAxisSize: MainAxisSize.min,
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Row(
                    children: [
                      const Icon(Icons.auto_awesome_rounded, color: Colors.amber),
                      const SizedBox(width: 8),
                      Text(
                        "Éclairage Spirituel IA",
                        style: Theme.of(context).textTheme.titleLarge?.copyWith(fontWeight: FontWeight.bold),
                      ),
                    ],
                  ),
                  const SizedBox(height: 6),
                  Text(
                    "${verse.bookName} ${verse.chapter}:${verse.verse}",
                    style: const TextStyle(fontWeight: FontWeight.bold, color: Colors.grey),
                  ),
                  const SizedBox(height: 14),
                  if (snapshot.connectionState == ConnectionState.waiting)
                    const Center(
                      child: Padding(
                        padding: EdgeInsets.symmetric(vertical: 24),
                        child: CircularProgressIndicator(),
                      ),
                    )
                  else
                    Text(
                      snapshot.data ?? "Explication non disponible.",
                      style: const TextStyle(fontSize: 15, height: 1.6),
                    ),
                  const SizedBox(height: 20),
                  FilledButton(
                    onPressed: () => Navigator.pop(ctx),
                    style: FilledButton.styleFrom(minimumSize: const Size(double.infinity, 46)),
                    child: const Text("Fermer"),
                  ),
                ],
              ),
            );
          },
        );
      },
    );
  }

  // --- Quiz Sheet ---
  void _openQuizSheet(BuildContext context) {
    ref.read(quizProvider.notifier).startQuiz(topic: ref.read(bibleProvider).selectedBook.name);

    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      shape: const RoundedRectangleBorder(borderRadius: BorderRadius.vertical(top: Radius.circular(24))),
      builder: (ctx) {
        return Consumer(
          builder: (ctx, ref, _) {
            final quiz = ref.watch(quizProvider);
            final quizNotifier = ref.read(quizProvider.notifier);

            return Container(
              padding: const EdgeInsets.all(20.0),
              height: MediaQuery.of(context).size.height * 0.75,
              child: quiz.isLoading
                  ? const Center(
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          CircularProgressIndicator(),
                          SizedBox(height: 12),
                          Text("Génération du Quiz Biblique..."),
                        ],
                      ),
                    )
                  : quiz.isFinished
                      ? Center(
                          child: Column(
                            mainAxisAlignment: MainAxisAlignment.center,
                            children: [
                              const Icon(Icons.emoji_events_rounded, color: Colors.amber, size: 64),
                              const SizedBox(height: 12),
                              Text(
                                "Quiz Terminé !",
                                style: Theme.of(context).textTheme.headlineMedium?.copyWith(fontWeight: FontWeight.bold),
                              ),
                              const SizedBox(height: 8),
                              Text(
                                "Votre score : ${quiz.score} / ${quiz.questions.length}",
                                style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
                              ),
                              const SizedBox(height: 20),
                              FilledButton(
                                onPressed: () => Navigator.pop(ctx),
                                child: const Text("Continuer ma lecture"),
                              ),
                            ],
                          ),
                        )
                      : quiz.questions.isEmpty
                          ? const Center(child: Text("Erreur lors de la génération du quiz."))
                          : Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                Row(
                                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                                  children: [
                                    Text(
                                      "Question ${quiz.currentIndex + 1} / ${quiz.questions.length}",
                                      style: const TextStyle(fontWeight: FontWeight.bold, color: Colors.grey),
                                    ),
                                    Text(
                                      "Score: ${quiz.score}",
                                      style: const TextStyle(fontWeight: FontWeight.bold),
                                    ),
                                  ],
                                ),
                                const SizedBox(height: 12),
                                Text(
                                  quiz.questions[quiz.currentIndex].question,
                                  style: const TextStyle(fontSize: 17, fontWeight: FontWeight.bold),
                                ),
                                const SizedBox(height: 16),
                                ...List.generate(
                                  quiz.questions[quiz.currentIndex].options.length,
                                  (optIndex) {
                                    final opt = quiz.questions[quiz.currentIndex].options[optIndex];
                                    final isSelected = quiz.selectedAnswerIndex == optIndex;
                                    final isCorrect = optIndex == quiz.questions[quiz.currentIndex].correctAnswerIndex;

                                    Color? btnColor;
                                    if (quiz.selectedAnswerIndex != null) {
                                      if (isCorrect) {
                                        btnColor = Colors.green.shade700;
                                      } else if (isSelected) {
                                        btnColor = Colors.red.shade700;
                                      }
                                    }

                                    return Padding(
                                      padding: const EdgeInsets.only(bottom: 8.0),
                                      child: OutlinedButton(
                                        onPressed: () => quizNotifier.submitAnswer(optIndex),
                                        style: OutlinedButton.styleFrom(
                                          minimumSize: const Size(double.infinity, 48),
                                          backgroundColor: btnColor?.withValues(alpha: 0.15),
                                          side: BorderSide(
                                            color: btnColor ?? Colors.grey.shade400,
                                            width: isSelected || isCorrect ? 2 : 1,
                                          ),
                                          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                                        ),
                                        child: Align(
                                          alignment: Alignment.centerLeft,
                                          child: Text(
                                            opt,
                                            style: TextStyle(
                                              fontWeight: isSelected ? FontWeight.bold : FontWeight.normal,
                                              color: btnColor,
                                            ),
                                          ),
                                        ),
                                      ),
                                    );
                                  },
                                ),
                                if (quiz.selectedAnswerIndex != null) ...[
                                  const SizedBox(height: 8),
                                  Text(
                                    "💡 ${quiz.questions[quiz.currentIndex].explanation}",
                                    style: TextStyle(
                                      color: Colors.grey.shade700,
                                      fontStyle: FontStyle.italic,
                                    ),
                                  ),
                                  const Spacer(),
                                  FilledButton(
                                    onPressed: () => quizNotifier.nextQuestion(),
                                    style: FilledButton.styleFrom(minimumSize: const Size(double.infinity, 46)),
                                    child: Text(
                                      quiz.currentIndex < quiz.questions.length - 1
                                          ? "Question suivante"
                                          : "Voir mon résultat",
                                    ),
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
