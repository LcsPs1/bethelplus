import 'bible_reading_plan.dart';

class BibleBookInfo {
  final String id;
  final String name;
  final String testament; // "OT" or "NT"
  final String category; // "Pentateuque", "Historique", "Poétique", "Prophètes", "Évangiles", "Épîtres", "Apocalypse"
  final int chaptersCount;
  final String abbreviation;

  const BibleBookInfo({
    required this.id,
    required this.name,
    required this.testament,
    required this.category,
    required this.chaptersCount,
    required this.abbreviation,
  });
}

class ReadingPlanDay {
  final int dayNumber;
  final String passageTitle;
  final String bookId;
  final int chapter;
  final String versesRange;
  final String devotionalTheme;

  const ReadingPlanDay({
    required this.dayNumber,
    required this.passageTitle,
    required this.bookId,
    required this.chapter,
    required this.versesRange,
    required this.devotionalTheme,
  });
}

class BibleData {
  static const List<BibleBookInfo> allBooks = [
    // Ancien Testament (39 livres)
    // Pentateuque
    BibleBookInfo(id: "GEN", name: "Genèse", testament: "OT", category: "Pentateuque", chaptersCount: 50, abbreviation: "Gen"),
    BibleBookInfo(id: "EXO", name: "Exode", testament: "OT", category: "Pentateuque", chaptersCount: 40, abbreviation: "Ex"),
    BibleBookInfo(id: "LEV", name: "Lévitique", testament: "OT", category: "Pentateuque", chaptersCount: 27, abbreviation: "Lév"),
    BibleBookInfo(id: "NUM", name: "Nombres", testament: "OT", category: "Pentateuque", chaptersCount: 36, abbreviation: "Nom"),
    BibleBookInfo(id: "DEU", name: "Deutéronome", testament: "OT", category: "Pentateuque", chaptersCount: 34, abbreviation: "Deut"),

    // Historiques
    BibleBookInfo(id: "JOS", name: "Josué", testament: "OT", category: "Historique", chaptersCount: 24, abbreviation: "Jos"),
    BibleBookInfo(id: "JDG", name: "Juges", testament: "OT", category: "Historique", chaptersCount: 21, abbreviation: "Jug"),
    BibleBookInfo(id: "RUT", name: "Ruth", testament: "OT", category: "Historique", chaptersCount: 4, abbreviation: "Ruth"),
    BibleBookInfo(id: "1SA", name: "1 Samuel", testament: "OT", category: "Historique", chaptersCount: 31, abbreviation: "1Sam"),
    BibleBookInfo(id: "2SA", name: "2 Samuel", testament: "OT", category: "Historique", chaptersCount: 24, abbreviation: "2Sam"),
    BibleBookInfo(id: "1KI", name: "1 Rois", testament: "OT", category: "Historique", chaptersCount: 22, abbreviation: "1Rois"),
    BibleBookInfo(id: "2KI", name: "2 Rois", testament: "OT", category: "Historique", chaptersCount: 25, abbreviation: "2Rois"),
    BibleBookInfo(id: "1CH", name: "1 Chroniques", testament: "OT", category: "Historique", chaptersCount: 29, abbreviation: "1Chr"),
    BibleBookInfo(id: "2CH", name: "2 Chroniques", testament: "OT", category: "Historique", chaptersCount: 36, abbreviation: "2Chr"),
    BibleBookInfo(id: "EZR", name: "Esdras", testament: "OT", category: "Historique", chaptersCount: 10, abbreviation: "Esd"),
    BibleBookInfo(id: "NEH", name: "Néhémie", testament: "OT", category: "Historique", chaptersCount: 13, abbreviation: "Néh"),
    BibleBookInfo(id: "EST", name: "Esther", testament: "OT", category: "Historique", chaptersCount: 10, abbreviation: "Est"),

    // Poétiques & Sapientiaux
    BibleBookInfo(id: "JOB", name: "Job", testament: "OT", category: "Poétique", chaptersCount: 42, abbreviation: "Job"),
    BibleBookInfo(id: "PSA", name: "Psaumes", testament: "OT", category: "Poétique", chaptersCount: 150, abbreviation: "Ps"),
    BibleBookInfo(id: "PRO", name: "Proverbes", testament: "OT", category: "Poétique", chaptersCount: 31, abbreviation: "Prov"),
    BibleBookInfo(id: "ECC", name: "Ecclésiaste", testament: "OT", category: "Poétique", chaptersCount: 12, abbreviation: "Ecc"),
    BibleBookInfo(id: "SNG", name: "Cantique des Cantiques", testament: "OT", category: "Poétique", chaptersCount: 8, abbreviation: "Cant"),

    // Grands Prophètes
    BibleBookInfo(id: "ISA", name: "Ésaïe", testament: "OT", category: "Prophètes", chaptersCount: 66, abbreviation: "És"),
    BibleBookInfo(id: "JER", name: "Jérémie", testament: "OT", category: "Prophètes", chaptersCount: 52, abbreviation: "Jér"),
    BibleBookInfo(id: "LAM", name: "Lamentations", testament: "OT", category: "Prophètes", chaptersCount: 5, abbreviation: "Lam"),
    BibleBookInfo(id: "EZK", name: "Ézéchiel", testament: "OT", category: "Prophètes", chaptersCount: 48, abbreviation: "Ézék"),
    BibleBookInfo(id: "DAN", name: "Daniel", testament: "OT", category: "Prophètes", chaptersCount: 12, abbreviation: "Dan"),

    // Petits Prophètes
    BibleBookInfo(id: "HOS", name: "Osée", testament: "OT", category: "Prophètes", chaptersCount: 14, abbreviation: "Os"),
    BibleBookInfo(id: "JOL", name: "Joël", testament: "OT", category: "Prophètes", chaptersCount: 3, abbreviation: "Joël"),
    BibleBookInfo(id: "AMO", name: "Amos", testament: "OT", category: "Prophètes", chaptersCount: 9, abbreviation: "Am"),
    BibleBookInfo(id: "OBA", name: "Abdias", testament: "OT", category: "Prophètes", chaptersCount: 1, abbreviation: "Abd"),
    BibleBookInfo(id: "JON", name: "Jonas", testament: "OT", category: "Prophètes", chaptersCount: 4, abbreviation: "Jon"),
    BibleBookInfo(id: "MIC", name: "Michée", testament: "OT", category: "Prophètes", chaptersCount: 7, abbreviation: "Mich"),
    BibleBookInfo(id: "NAM", name: "Nahum", testament: "OT", category: "Prophètes", chaptersCount: 3, abbreviation: "Nah"),
    BibleBookInfo(id: "HAB", name: "Habacuc", testament: "OT", category: "Prophètes", chaptersCount: 3, abbreviation: "Hab"),
    BibleBookInfo(id: "ZEP", name: "Sophonie", testament: "OT", category: "Prophètes", chaptersCount: 3, abbreviation: "Soph"),
    BibleBookInfo(id: "HAG", name: "Aggée", testament: "OT", category: "Prophètes", chaptersCount: 2, abbreviation: "Agg"),
    BibleBookInfo(id: "ZEC", name: "Zacharie", testament: "OT", category: "Prophètes", chaptersCount: 14, abbreviation: "Zach"),
    BibleBookInfo(id: "MAL", name: "Malachie", testament: "OT", category: "Prophètes", chaptersCount: 4, abbreviation: "Mal"),

    // Nouveau Testament (27 livres)
    // Évangiles & Actes
    BibleBookInfo(id: "MAT", name: "Matthieu", testament: "NT", category: "Évangiles", chaptersCount: 28, abbreviation: "Mat"),
    BibleBookInfo(id: "MRK", name: "Marc", testament: "NT", category: "Évangiles", chaptersCount: 16, abbreviation: "Marc"),
    BibleBookInfo(id: "LUK", name: "Luc", testament: "NT", category: "Évangiles", chaptersCount: 24, abbreviation: "Luc"),
    BibleBookInfo(id: "JHN", name: "Jean", testament: "NT", category: "Évangiles", chaptersCount: 21, abbreviation: "Jean"),
    BibleBookInfo(id: "ACT", name: "Actes des Apôtres", testament: "NT", category: "Histoire", chaptersCount: 28, abbreviation: "Actes"),

    // Épîtres de Paul
    BibleBookInfo(id: "ROM", name: "Romains", testament: "NT", category: "Épîtres", chaptersCount: 16, abbreviation: "Rom"),
    BibleBookInfo(id: "1CO", name: "1 Corinthiens", testament: "NT", category: "Épîtres", chaptersCount: 16, abbreviation: "1Cor"),
    BibleBookInfo(id: "2CO", name: "2 Corinthiens", testament: "NT", category: "Épîtres", chaptersCount: 13, abbreviation: "2Cor"),
    BibleBookInfo(id: "GAL", name: "Galates", testament: "NT", category: "Épîtres", chaptersCount: 6, abbreviation: "Gal"),
    BibleBookInfo(id: "EPH", name: "Éphésiens", testament: "NT", category: "Épîtres", chaptersCount: 6, abbreviation: "Éph"),
    BibleBookInfo(id: "PHP", name: "Philippiens", testament: "NT", category: "Épîtres", chaptersCount: 4, abbreviation: "Phil"),
    BibleBookInfo(id: "COL", name: "Colossiens", testament: "NT", category: "Épîtres", chaptersCount: 4, abbreviation: "Col"),
    BibleBookInfo(id: "1TH", name: "1 Thessaloniciens", testament: "NT", category: "Épîtres", chaptersCount: 5, abbreviation: "1Thes"),
    BibleBookInfo(id: "2TH", name: "2 Thessaloniciens", testament: "NT", category: "Épîtres", chaptersCount: 3, abbreviation: "2Thes"),
    BibleBookInfo(id: "1TI", name: "1 Timothée", testament: "NT", category: "Épîtres", chaptersCount: 6, abbreviation: "1Tim"),
    BibleBookInfo(id: "2TI", name: "2 Timothée", testament: "NT", category: "Épîtres", chaptersCount: 4, abbreviation: "2Tim"),
    BibleBookInfo(id: "TIT", name: "Tite", testament: "NT", category: "Épîtres", chaptersCount: 3, abbreviation: "Tite"),
    BibleBookInfo(id: "PHM", name: "Philémon", testament: "NT", category: "Épîtres", chaptersCount: 1, abbreviation: "Phm"),

    // Autres Épîtres
    BibleBookInfo(id: "HEB", name: "Hébreux", testament: "NT", category: "Épîtres", chaptersCount: 13, abbreviation: "Héb"),
    BibleBookInfo(id: "JAS", name: "Jacques", testament: "NT", category: "Épîtres", chaptersCount: 5, abbreviation: "Jac"),
    BibleBookInfo(id: "1PE", name: "1 Pierre", testament: "NT", category: "Épîtres", chaptersCount: 5, abbreviation: "1Pi"),
    BibleBookInfo(id: "2PE", name: "2 Pierre", testament: "NT", category: "Épîtres", chaptersCount: 3, abbreviation: "2Pi"),
    BibleBookInfo(id: "1JN", name: "1 Jean", testament: "NT", category: "Épîtres", chaptersCount: 5, abbreviation: "1Jn"),
    BibleBookInfo(id: "2JN", name: "2 Jean", testament: "NT", category: "Épîtres", chaptersCount: 1, abbreviation: "2Jn"),
    BibleBookInfo(id: "3JN", name: "3 Jean", testament: "NT", category: "Épîtres", chaptersCount: 1, abbreviation: "3Jn"),
    BibleBookInfo(id: "JUD", name: "Jude", testament: "NT", category: "Épîtres", chaptersCount: 1, abbreviation: "Jude"),

    // Révélation
    BibleBookInfo(id: "REV", name: "Apocalypse", testament: "NT", category: "Apocalypse", chaptersCount: 22, abbreviation: "Apoc")
  ];

  static BibleBookInfo getBookById(String bookId) {
    return allBooks.firstWhere(
      (b) => b.id.toUpperCase() == bookId.toUpperCase(),
      orElse: () => allBooks.first,
    );
  }

  static BibleBookInfo? getBookByName(String name) {
    final clean = name.trim().toLowerCase();
    for (final b in allBooks) {
      if (b.name.toLowerCase() == clean ||
          b.abbreviation.toLowerCase() == clean ||
          b.id.toLowerCase() == clean) {
        return b;
      }
    }
    return null;
  }

  static const List<BibleReadingPlan> defaultReadingPlans = [
    BibleReadingPlan(
      id: "plan_1_year",
      title: "La Bible en 1 an",
      subtitle: "Un parcours équilibré à travers l'Ancien Testament, les Psaumes et le Nouveau Testament",
      totalDays: 365,
      currentDay: 1,
      completedDaysCsv: "1",
      isActive: true,
    ),
    BibleReadingPlan(
      id: "plan_nt_90",
      title: "Le Nouveau Testament en 90 jours",
      subtitle: "Lisez les 27 livres du Nouveau Testament en profondeur en 3 mois",
      totalDays: 90,
      currentDay: 1,
      completedDaysCsv: "",
      isActive: false,
    ),
    BibleReadingPlan(
      id: "plan_psalms_30",
      title: "Psaumes & Proverbes en 30 jours",
      subtitle: "Nourrissez votre prière quotidienne des plus beaux cantiques et maximes de sagesse",
      totalDays: 30,
      currentDay: 1,
      completedDaysCsv: "",
      isActive: false,
    ),
    BibleReadingPlan(
      id: "plan_gospels_40",
      title: "Sur les pas de Jésus (40 jours)",
      subtitle: "Méditez la vie, les miracles, les paraboles et la résurrection du Christ dans les 4 Évangiles",
      totalDays: 40,
      currentDay: 1,
      completedDaysCsv: "",
      isActive: false,
    ),
  ];

  static ReadingPlanDay getReadingPlanDay(String planId, int day) {
    switch (planId) {
      case "plan_nt_90":
        final bookIndex = ((day - 1) % 27) + 39;
        final book = allBooks.length > bookIndex ? allBooks[bookIndex] : allBooks[39];
        final chap = ((day - 1) % book.chaptersCount) + 1;
        return ReadingPlanDay(
          dayNumber: day,
          passageTitle: "${book.name} $chap",
          bookId: book.id,
          chapter: chap,
          versesRange: "1-25",
          devotionalTheme: "La grâce et la vie dans l'Esprit",
        );
      case "plan_psalms_30":
        final psalmNum = ((day - 1) * 5) + 1;
        final provNum = (day % 31) + 1;
        final validPsalm = psalmNum.clamp(1, 150);
        return ReadingPlanDay(
          dayNumber: day,
          passageTitle: "Psaumes $psalmNum & Proverbes $provNum",
          bookId: "PSA",
          chapter: validPsalm,
          versesRange: "1-12",
          devotionalTheme: "Louange, confiance et sagesse divine",
        );
      case "plan_gospels_40":
        final gospelId = switch ((day ~/ 10) % 4) {
          0 => "MAT",
          1 => "MRK",
          2 => "LUK",
          _ => "JHN",
        };
        final book = getBookById(gospelId);
        final chap = ((day % 10) + 1).clamp(1, book.chaptersCount);
        return ReadingPlanDay(
          dayNumber: day,
          passageTitle: "${book.name} $chap",
          bookId: book.id,
          chapter: chap,
          versesRange: "Passage évangélique",
          devotionalTheme: "La rencontre vivante avec le Christ",
        );
      default:
        final otBook = allBooks[(day - 1) % 39];
        final otChap = ((day - 1) % otBook.chaptersCount) + 1;
        final psChap = ((day - 1) % 150) + 1;
        return ReadingPlanDay(
          dayNumber: day,
          passageTitle: "${otBook.name} $otChap & Psaume $psChap",
          bookId: otBook.id,
          chapter: otChap,
          versesRange: "Lecture du jour",
          devotionalTheme: "L'Alliance éternelle de Dieu avec son peuple",
        );
    }
  }
}
