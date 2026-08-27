class PrayerSession {
  final int id;
  final int dateMillis;
  final String? meditationId;
  final String title;
  final int durationSeconds;
  final bool completed;
  final String? notes;

  const PrayerSession({
    this.id = 0,
    this.dateMillis = 0,
    this.meditationId,
    required this.title,
    required this.durationSeconds,
    this.completed = true,
    this.notes,
  });

  DateTime get createdAt => DateTime.fromMillisecondsSinceEpoch(dateMillis > 0 ? dateMillis : DateTime.now().millisecondsSinceEpoch);

  PrayerSession copyWith({
    int? id,
    int? dateMillis,
    DateTime? createdAt,
    String? meditationId,
    String? title,
    int? durationSeconds,
    bool? completed,
    String? notes,
  }) {
    return PrayerSession(
      id: id ?? this.id,
      dateMillis: dateMillis ?? createdAt?.millisecondsSinceEpoch ?? this.dateMillis,
      meditationId: meditationId ?? this.meditationId,
      title: title ?? this.title,
      durationSeconds: durationSeconds ?? this.durationSeconds,
      completed: completed ?? this.completed,
      notes: notes ?? this.notes,
    );
  }

  Map<String, dynamic> toMap() {
    return {
      if (id != 0) 'id': id,
      'dateMillis': dateMillis,
      'meditationId': meditationId,
      'title': title,
      'durationSeconds': durationSeconds,
      'completed': completed ? 1 : 0,
      'notes': notes,
    };
  }

  Map<String, dynamic> toJson() => toMap();

  factory PrayerSession.fromMap(Map<String, dynamic> map) {
    return PrayerSession(
      id: map['id'] is int ? map['id'] as int : int.tryParse(map['id']?.toString() ?? '0') ?? 0,
      dateMillis: map['dateMillis'] is int ? map['dateMillis'] as int : int.tryParse(map['dateMillis']?.toString() ?? '0') ?? DateTime.now().millisecondsSinceEpoch,
      meditationId: map['meditationId'] as String?,
      title: map['title'] as String? ?? 'Temps de Prière',
      durationSeconds: map['durationSeconds'] as int? ?? 0,
      completed: (map['completed'] as int? ?? 1) == 1 || (map['completed'] == true),
      notes: map['notes'] as String?,
    );
  }

  factory PrayerSession.fromJson(Map<String, dynamic> json) => PrayerSession.fromMap(json);
}
