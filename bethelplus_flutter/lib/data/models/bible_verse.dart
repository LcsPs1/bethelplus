class BibleVerse {
  final String id; // e.g. "LSG_GEN_1_1"
  final String version; // "LSG" or "DRB"
  final String bookId; // "GEN", "EXO", etc.
  final String bookName; // "Genèse", "Jean", etc.
  final String testament; // "OT" or "NT"
  final int chapter;
  final int verse;
  final String text;
  final String? highlightColor; // "GOLD", "BLUE", "GREEN", "PURPLE", "TERRACOTTA"
  final bool isBookmarked;
  final String? personalNote;
  final int? noteUpdatedAt;

  const BibleVerse({
    required this.id,
    required this.version,
    required this.bookId,
    required this.bookName,
    required this.testament,
    required this.chapter,
    required this.verse,
    required this.text,
    this.highlightColor,
    this.isBookmarked = false,
    this.personalNote,
    this.noteUpdatedAt,
  });

  BibleVerse copyWith({
    String? id,
    String? version,
    String? bookId,
    String? bookName,
    String? testament,
    int? chapter,
    int? verse,
    String? text,
    String? highlightColor,
    bool? isBookmarked,
    String? personalNote,
    int? noteUpdatedAt,
  }) {
    return BibleVerse(
      id: id ?? this.id,
      version: version ?? this.version,
      bookId: bookId ?? this.bookId,
      bookName: bookName ?? this.bookName,
      testament: testament ?? this.testament,
      chapter: chapter ?? this.chapter,
      verse: verse ?? this.verse,
      text: text ?? this.text,
      highlightColor: highlightColor ?? this.highlightColor,
      isBookmarked: isBookmarked ?? this.isBookmarked,
      personalNote: personalNote ?? this.personalNote,
      noteUpdatedAt: noteUpdatedAt ?? this.noteUpdatedAt,
    );
  }

  Map<String, dynamic> toMap() {
    return {
      'id': id,
      'version': version,
      'bookId': bookId,
      'bookName': bookName,
      'testament': testament,
      'chapter': chapter,
      'verse': verse,
      'text': text,
      'highlightColor': highlightColor,
      'isBookmarked': isBookmarked ? 1 : 0,
      'personalNote': personalNote,
      'noteUpdatedAt': noteUpdatedAt,
    };
  }

  factory BibleVerse.fromMap(Map<String, dynamic> map) {
    return BibleVerse(
      id: map['id'] as String? ?? '',
      version: map['version'] as String? ?? 'LSG',
      bookId: map['bookId'] as String? ?? '',
      bookName: map['bookName'] as String? ?? '',
      testament: map['testament'] as String? ?? 'OT',
      chapter: map['chapter'] as int? ?? 1,
      verse: map['verse'] as int? ?? 1,
      text: map['text'] as String? ?? '',
      highlightColor: map['highlightColor'] as String?,
      isBookmarked: (map['isBookmarked'] as int? ?? 0) == 1,
      personalNote: map['personalNote'] as String?,
      noteUpdatedAt: map['noteUpdatedAt'] as int?,
    );
  }
}
