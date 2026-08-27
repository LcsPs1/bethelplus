class PrayerReminder {
  final int id;
  final String title;
  final int hour;
  final int minute;
  final int daysMask;
  final bool isEnabled;
  final String soundscape;
  final String canonicalType; // "AUBE", "MIDI", "VESPERAL", "COMPLIES", "CUSTOM"
  final bool isVibrationEnabled;

  const PrayerReminder({
    this.id = 0,
    required this.title,
    required this.hour,
    required this.minute,
    this.daysMask = 127,
    this.isEnabled = true,
    this.soundscape = 'CLOCHE',
    String? soundType,
    this.canonicalType = 'CUSTOM',
    bool? isCanonical,
    this.isVibrationEnabled = true,
  });

  bool get isCanonical => canonicalType != 'CUSTOM';
  String get soundType => soundscape;

  PrayerReminder copyWith({
    int? id,
    String? title,
    int? hour,
    int? minute,
    int? daysMask,
    bool? isEnabled,
    String? soundscape,
    String? soundType,
    String? canonicalType,
    bool? isCanonical,
    bool? isVibrationEnabled,
  }) {
    return PrayerReminder(
      id: id ?? this.id,
      title: title ?? this.title,
      hour: hour ?? this.hour,
      minute: minute ?? this.minute,
      daysMask: daysMask ?? this.daysMask,
      isEnabled: isEnabled ?? this.isEnabled,
      soundscape: soundType ?? soundscape ?? this.soundscape,
      canonicalType: (isCanonical != null ? (isCanonical ? 'CANONICAL' : 'CUSTOM') : null) ?? canonicalType ?? this.canonicalType,
      isVibrationEnabled: isVibrationEnabled ?? this.isVibrationEnabled,
    );
  }

  Map<String, dynamic> toMap() {
    return {
      if (id != 0) 'id': id,
      'title': title,
      'hour': hour,
      'minute': minute,
      'daysMask': daysMask,
      'isEnabled': isEnabled ? 1 : 0,
      'soundscape': soundscape,
      'canonicalType': canonicalType,
      'isVibrationEnabled': isVibrationEnabled ? 1 : 0,
    };
  }

  Map<String, dynamic> toJson() => toMap();

  factory PrayerReminder.fromMap(Map<String, dynamic> map) {
    return PrayerReminder(
      id: map['id'] as int? ?? 0,
      title: map['title'] as String? ?? '',
      hour: map['hour'] as int? ?? 0,
      minute: map['minute'] as int? ?? 0,
      daysMask: map['daysMask'] as int? ?? 127,
      isEnabled: (map['isEnabled'] as int? ?? 1) == 1 || (map['isEnabled'] == true),
      soundscape: map['soundscape'] as String? ?? (map['soundType'] as String? ?? 'CLOCHE'),
      canonicalType: map['canonicalType'] as String? ?? ((map['isCanonical'] == true) ? 'CANONICAL' : 'CUSTOM'),
      isVibrationEnabled: (map['isVibrationEnabled'] as int? ?? 1) == 1 || (map['isVibrationEnabled'] == true),
    );
  }

  factory PrayerReminder.fromJson(Map<String, dynamic> json) => PrayerReminder.fromMap(json);
}
