class PrayerJournal {
  final int id;
  final String title;
  final String content;
  final String category; // "Prière", "Intercession", "Action de grâce", "Méditation", "Requête"
  final int createdAt;
  final bool isAnswered;
  final int? answeredAt;
  final String? testimony;

  const PrayerJournal({
    this.id = 0,
    required this.title,
    required this.content,
    this.category = 'Prière',
    required this.createdAt,
    this.isAnswered = false,
    this.answeredAt,
    this.testimony,
  });

  DateTime get createdDateTime => DateTime.fromMillisecondsSinceEpoch(createdAt);
  DateTime? get answeredDateTime => answeredAt != null ? DateTime.fromMillisecondsSinceEpoch(answeredAt!) : null;

  PrayerJournal copyWith({
    int? id,
    String? title,
    String? content,
    String? category,
    int? createdAt,
    DateTime? createdDate,
    bool? isAnswered,
    int? answeredAt,
    DateTime? answeredDate,
    String? testimony,
  }) {
    return PrayerJournal(
      id: id ?? this.id,
      title: title ?? this.title,
      content: content ?? this.content,
      category: category ?? this.category,
      createdAt: createdDate?.millisecondsSinceEpoch ?? createdAt ?? this.createdAt,
      isAnswered: isAnswered ?? this.isAnswered,
      answeredAt: answeredDate?.millisecondsSinceEpoch ?? answeredAt ?? this.answeredAt,
      testimony: testimony ?? this.testimony,
    );
  }

  Map<String, dynamic> toMap() {
    return {
      if (id != 0) 'id': id,
      'title': title,
      'content': content,
      'category': category,
      'createdAt': createdAt,
      'isAnswered': isAnswered ? 1 : 0,
      'answeredAt': answeredAt,
      'testimony': testimony,
    };
  }

  Map<String, dynamic> toJson() => toMap();

  factory PrayerJournal.fromMap(Map<String, dynamic> map) {
    return PrayerJournal(
      id: map['id'] is int ? map['id'] as int : int.tryParse(map['id']?.toString() ?? '0') ?? 0,
      title: map['title'] as String? ?? '',
      content: map['content'] as String? ?? '',
      category: map['category'] as String? ?? 'Prière',
      createdAt: map['createdAt'] is int ? map['createdAt'] as int : int.tryParse(map['createdAt']?.toString() ?? '0') ?? DateTime.now().millisecondsSinceEpoch,
      isAnswered: (map['isAnswered'] as int? ?? 0) == 1 || (map['isAnswered'] == true),
      answeredAt: map['answeredAt'] is int ? map['answeredAt'] as int : int.tryParse(map['answeredAt']?.toString() ?? ''),
      testimony: map['testimony'] as String?,
    );
  }

  factory PrayerJournal.fromJson(Map<String, dynamic> json) => PrayerJournal.fromMap(json);
}
