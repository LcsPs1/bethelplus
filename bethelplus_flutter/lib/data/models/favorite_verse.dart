class FavoriteVerse {
  final String id;
  final String reference;
  final String text;
  final String theme;
  final String reflection;
  final int savedAt;

  const FavoriteVerse({
    required this.id,
    required this.reference,
    required this.text,
    required this.theme,
    required this.reflection,
    required this.savedAt,
  });

  Map<String, dynamic> toMap() {
    return {
      'id': id,
      'reference': reference,
      'text': text,
      'theme': theme,
      'reflection': reflection,
      'savedAt': savedAt,
    };
  }

  factory FavoriteVerse.fromMap(Map<String, dynamic> map) {
    return FavoriteVerse(
      id: map['id'] as String? ?? '',
      reference: map['reference'] as String? ?? '',
      text: map['text'] as String? ?? '',
      theme: map['theme'] as String? ?? '',
      reflection: map['reflection'] as String? ?? '',
      savedAt: map['savedAt'] as int? ?? DateTime.now().millisecondsSinceEpoch,
    );
  }
}
