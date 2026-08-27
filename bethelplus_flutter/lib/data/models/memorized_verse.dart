class MemorizedVerse {
  final String id;
  final String reference;
  final String text;
  final String version;
  final String bookId;
  final int chapter;
  final int verse;
  final String category; // "Foi", "Paix", "Combat", "Promesse", "Identité"
  final int boxLevel; // 1: Quotidien, 2: 3 jours, 3: Hebdomadaire, 4: Bimensuel, 5: Maîtrisé !
  final int nextReviewTimestamp;
  final int lastReviewedTimestamp;
  final int reviewCount;
  final int successStreak;
  final bool isMastered;
  final int addedDate;

  const MemorizedVerse({
    required this.id,
    required this.reference,
    required this.text,
    this.version = 'LSG',
    this.bookId = '',
    this.chapter = 1,
    this.verse = 1,
    this.category = 'Général',
    this.boxLevel = 1,
    required this.nextReviewTimestamp,
    this.lastReviewedTimestamp = 0,
    this.reviewCount = 0,
    this.successStreak = 0,
    this.isMastered = false,
    required this.addedDate,
  });

  int get box => boxLevel;
  DateTime get lastReviewed => DateTime.fromMillisecondsSinceEpoch(lastReviewedTimestamp > 0 ? lastReviewedTimestamp : addedDate);

  MemorizedVerse copyWith({
    String? id,
    String? reference,
    String? text,
    String? version,
    String? bookId,
    int? chapter,
    int? verse,
    String? category,
    int? boxLevel,
    int? box,
    int? nextReviewTimestamp,
    int? lastReviewedTimestamp,
    DateTime? lastReviewed,
    int? reviewCount,
    int? successStreak,
    bool? isMastered,
    int? addedDate,
  }) {
    return MemorizedVerse(
      id: id ?? this.id,
      reference: reference ?? this.reference,
      text: text ?? this.text,
      version: version ?? this.version,
      bookId: bookId ?? this.bookId,
      chapter: chapter ?? this.chapter,
      verse: verse ?? this.verse,
      category: category ?? this.category,
      boxLevel: box ?? boxLevel ?? this.boxLevel,
      nextReviewTimestamp: nextReviewTimestamp ?? this.nextReviewTimestamp,
      lastReviewedTimestamp: lastReviewed?.millisecondsSinceEpoch ?? lastReviewedTimestamp ?? this.lastReviewedTimestamp,
      reviewCount: reviewCount ?? this.reviewCount,
      successStreak: successStreak ?? this.successStreak,
      isMastered: isMastered ?? this.isMastered,
      addedDate: addedDate ?? this.addedDate,
    );
  }

  Map<String, dynamic> toMap() {
    return {
      'id': id,
      'reference': reference,
      'text': text,
      'version': version,
      'bookId': bookId,
      'chapter': chapter,
      'verse': verse,
      'category': category,
      'boxLevel': boxLevel,
      'nextReviewTimestamp': nextReviewTimestamp,
      'lastReviewedTimestamp': lastReviewedTimestamp,
      'reviewCount': reviewCount,
      'successStreak': successStreak,
      'isMastered': isMastered ? 1 : 0,
      'addedDate': addedDate,
    };
  }

  Map<String, dynamic> toJson() => toMap();

  factory MemorizedVerse.fromMap(Map<String, dynamic> map) {
    return MemorizedVerse(
      id: map['id'] as String? ?? '',
      reference: map['reference'] as String? ?? '',
      text: map['text'] as String? ?? '',
      version: map['version'] as String? ?? 'LSG',
      bookId: map['bookId'] as String? ?? '',
      chapter: map['chapter'] as int? ?? 1,
      verse: map['verse'] as int? ?? 1,
      category: map['category'] as String? ?? 'Général',
      boxLevel: map['boxLevel'] as int? ?? (map['box'] as int? ?? 1),
      nextReviewTimestamp: map['nextReviewTimestamp'] as int? ?? DateTime.now().millisecondsSinceEpoch,
      lastReviewedTimestamp: map['lastReviewedTimestamp'] as int? ?? 0,
      reviewCount: map['reviewCount'] as int? ?? 0,
      successStreak: map['successStreak'] as int? ?? 0,
      isMastered: (map['isMastered'] as int? ?? 0) == 1,
      addedDate: map['addedDate'] as int? ?? DateTime.now().millisecondsSinceEpoch,
    );
  }

  factory MemorizedVerse.fromJson(Map<String, dynamic> json) => MemorizedVerse.fromMap(json);
}
