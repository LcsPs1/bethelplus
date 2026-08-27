class SpiritualAlarm {
  final int id;
  final bool isEnabled;
  final int hour;
  final int minute;
  final int daysMask;
  final String soundscape; // "HARPE", "CLOCHE", "SANCTUAIRE", "OISEAUX"
  final bool isDndEnabledOnWakeup;
  final String morningVerseTheme; // "GRACE", "PROTECTION", "FORCE", "PAIX"
  final int snoozeCountMinutes;
  final String? customScriptureReference;
  final String? customScriptureText;

  const SpiritualAlarm({
    this.id = 1,
    this.isEnabled = false,
    this.hour = 6,
    this.minute = 30,
    this.daysMask = 127,
    this.soundscape = 'HARPE',
    String? soundType,
    this.isDndEnabledOnWakeup = true,
    this.morningVerseTheme = 'GRACE',
    this.snoozeCountMinutes = 5,
    String? scriptureReference,
    String? scriptureText,
  })  : customScriptureReference = scriptureReference,
        customScriptureText = scriptureText;

  String get scriptureReference => customScriptureReference ?? "Psaumes 57:9";
  String get scriptureText =>
      customScriptureText ??
      "Réveille-toi, mon âme ! Réveillez-vous, mon luth et ma harpe ! Je réveillerai l'aurore.";
  String get soundType => soundscape;

  SpiritualAlarm copyWith({
    int? id,
    bool? isEnabled,
    int? hour,
    int? minute,
    int? daysMask,
    String? soundscape,
    String? soundType,
    bool? isDndEnabledOnWakeup,
    String? morningVerseTheme,
    int? snoozeCountMinutes,
    String? scriptureReference,
    String? scriptureText,
  }) {
    return SpiritualAlarm(
      id: id ?? this.id,
      isEnabled: isEnabled ?? this.isEnabled,
      hour: hour ?? this.hour,
      minute: minute ?? this.minute,
      daysMask: daysMask ?? this.daysMask,
      soundscape: soundType ?? soundscape ?? this.soundscape,
      isDndEnabledOnWakeup: isDndEnabledOnWakeup ?? this.isDndEnabledOnWakeup,
      morningVerseTheme: morningVerseTheme ?? this.morningVerseTheme,
      snoozeCountMinutes: snoozeCountMinutes ?? this.snoozeCountMinutes,
      scriptureReference: scriptureReference ?? customScriptureReference,
      scriptureText: scriptureText ?? customScriptureText,
    );
  }

  Map<String, dynamic> toMap() {
    return {
      'id': id,
      'isEnabled': isEnabled ? 1 : 0,
      'hour': hour,
      'minute': minute,
      'daysMask': daysMask,
      'soundscape': soundscape,
      'isDndEnabledOnWakeup': isDndEnabledOnWakeup ? 1 : 0,
      'morningVerseTheme': morningVerseTheme,
      'snoozeCountMinutes': snoozeCountMinutes,
      if (customScriptureReference != null) 'scriptureReference': customScriptureReference,
      if (customScriptureText != null) 'scriptureText': customScriptureText,
    };
  }

  Map<String, dynamic> toJson() => toMap();

  factory SpiritualAlarm.fromMap(Map<String, dynamic> map) {
    return SpiritualAlarm(
      id: map['id'] as int? ?? 1,
      isEnabled: (map['isEnabled'] as int? ?? 0) == 1 || (map['isEnabled'] == true),
      hour: map['hour'] as int? ?? 6,
      minute: map['minute'] as int? ?? 30,
      daysMask: map['daysMask'] as int? ?? 127,
      soundscape: map['soundscape'] as String? ?? (map['soundType'] as String? ?? 'HARPE'),
      isDndEnabledOnWakeup: (map['isDndEnabledOnWakeup'] as int? ?? 1) == 1 || (map['isDndEnabledOnWakeup'] == true),
      morningVerseTheme: map['morningVerseTheme'] as String? ?? 'GRACE',
      snoozeCountMinutes: map['snoozeCountMinutes'] as int? ?? 5,
      scriptureReference: map['scriptureReference'] as String?,
      scriptureText: map['scriptureText'] as String?,
    );
  }

  factory SpiritualAlarm.fromJson(Map<String, dynamic> json) => SpiritualAlarm.fromMap(json);
}
