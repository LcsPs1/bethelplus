class ChallengeDay {
  final int dayNumber;
  final String theme;
  final String scriptureRef;
  final String scriptureText;
  final String reflection;
  final String prayerPrompt;
  final String actionItem;
  final bool isCompleted;

  const ChallengeDay({
    required this.dayNumber,
    required this.theme,
    required this.scriptureRef,
    required this.scriptureText,
    required this.reflection,
    required this.prayerPrompt,
    required this.actionItem,
    this.isCompleted = false,
  });

  ChallengeDay copyWith({
    int? dayNumber,
    String? theme,
    String? scriptureRef,
    String? scriptureText,
    String? reflection,
    String? prayerPrompt,
    String? actionItem,
    bool? isCompleted,
  }) {
    return ChallengeDay(
      dayNumber: dayNumber ?? this.dayNumber,
      theme: theme ?? this.theme,
      scriptureRef: scriptureRef ?? this.scriptureRef,
      scriptureText: scriptureText ?? this.scriptureText,
      reflection: reflection ?? this.reflection,
      prayerPrompt: prayerPrompt ?? this.prayerPrompt,
      actionItem: actionItem ?? this.actionItem,
      isCompleted: isCompleted ?? this.isCompleted,
    );
  }

  Map<String, dynamic> toMap() {
    return {
      'dayNumber': dayNumber,
      'theme': theme,
      'scriptureRef': scriptureRef,
      'scriptureText': scriptureText,
      'reflection': reflection,
      'prayerPrompt': prayerPrompt,
      'actionItem': actionItem,
      'isCompleted': isCompleted,
    };
  }

  factory ChallengeDay.fromMap(Map<String, dynamic> map) {
    return ChallengeDay(
      dayNumber: map['dayNumber'] as int? ?? 1,
      theme: map['theme'] as String? ?? '',
      scriptureRef: map['scriptureRef'] as String? ?? '',
      scriptureText: map['scriptureText'] as String? ?? '',
      reflection: map['reflection'] as String? ?? '',
      prayerPrompt: map['prayerPrompt'] as String? ?? '',
      actionItem: map['actionItem'] as String? ?? '',
      isCompleted: map['isCompleted'] as bool? ?? false,
    );
  }
}

class SpiritualChallenge {
  final String id;
  final String title;
  final String subtitle;
  final String category; // "GRATITUDE", "FASTING", "INTERCESSION", "MIND_RENEWAL", "PEACE", "Intimité", "Discipline"
  final int totalDays;
  final String completedDaysCsv;
  final bool isActive;
  final bool isCompleted;
  final int startDate;
  final int reminderHour;
  final int reminderMinute;
  final bool isReminderEnabled;
  final String badgeName;
  final String badgeDescription;
  final int completionDate;
  final List<ChallengeDay> days;

  const SpiritualChallenge({
    required this.id,
    required this.title,
    required this.subtitle,
    required this.category,
    this.totalDays = 7,
    this.completedDaysCsv = '',
    this.isActive = false,
    this.isCompleted = false,
    this.startDate = 0,
    this.reminderHour = 8,
    this.reminderMinute = 30,
    this.isReminderEnabled = true,
    this.badgeName = 'Couronne de Persévérance',
    this.badgeDescription = 'Fidélité et marche quotidienne accomplies.',
    this.completionDate = 0,
    this.days = const [],
  });

  int get durationDays => totalDays;

  SpiritualChallenge copyWith({
    String? id,
    String? title,
    String? subtitle,
    String? category,
    int? totalDays,
    int? durationDays,
    String? completedDaysCsv,
    bool? isActive,
    bool? isCompleted,
    int? startDate,
    int? reminderHour,
    int? reminderMinute,
    bool? isReminderEnabled,
    String? badgeName,
    String? badgeDescription,
    int? completionDate,
    List<ChallengeDay>? days,
  }) {
    return SpiritualChallenge(
      id: id ?? this.id,
      title: title ?? this.title,
      subtitle: subtitle ?? this.subtitle,
      category: category ?? this.category,
      totalDays: totalDays ?? durationDays ?? this.totalDays,
      completedDaysCsv: completedDaysCsv ?? this.completedDaysCsv,
      isActive: isActive ?? this.isActive,
      isCompleted: isCompleted ?? this.isCompleted,
      startDate: startDate ?? this.startDate,
      reminderHour: reminderHour ?? this.reminderHour,
      reminderMinute: reminderMinute ?? this.reminderMinute,
      isReminderEnabled: isReminderEnabled ?? this.isReminderEnabled,
      badgeName: badgeName ?? this.badgeName,
      badgeDescription: badgeDescription ?? this.badgeDescription,
      completionDate: completionDate ?? this.completionDate,
      days: days ?? this.days,
    );
  }

  Map<String, dynamic> toMap() {
    return {
      'id': id,
      'title': title,
      'subtitle': subtitle,
      'category': category,
      'totalDays': totalDays,
      'completedDaysCsv': completedDaysCsv,
      'isActive': isActive ? 1 : 0,
      'isCompleted': isCompleted ? 1 : 0,
      'startDate': startDate,
      'reminderHour': reminderHour,
      'reminderMinute': reminderMinute,
      'isReminderEnabled': isReminderEnabled ? 1 : 0,
      'badgeName': badgeName,
      'badgeDescription': badgeDescription,
      'completionDate': completionDate,
      'days': days.map((d) => d.toMap()).toList(),
    };
  }

  Map<String, dynamic> toJson() => toMap();

  factory SpiritualChallenge.fromMap(Map<String, dynamic> map) {
    return SpiritualChallenge(
      id: map['id'] as String? ?? '',
      title: map['title'] as String? ?? '',
      subtitle: map['subtitle'] as String? ?? '',
      category: map['category'] as String? ?? 'GRATITUDE',
      totalDays: map['totalDays'] as int? ?? 7,
      completedDaysCsv: map['completedDaysCsv'] as String? ?? '',
      isActive: (map['isActive'] as int? ?? 0) == 1 || (map['isActive'] == true),
      isCompleted: (map['isCompleted'] as int? ?? 0) == 1 || (map['isCompleted'] == true),
      startDate: map['startDate'] as int? ?? 0,
      reminderHour: map['reminderHour'] as int? ?? 8,
      reminderMinute: map['reminderMinute'] as int? ?? 30,
      isReminderEnabled: (map['isReminderEnabled'] as int? ?? 1) == 1 || (map['isReminderEnabled'] == true),
      badgeName: map['badgeName'] as String? ?? 'Couronne de Persévérance',
      badgeDescription: map['badgeDescription'] as String? ?? 'Fidélité et marche quotidienne accomplies.',
      completionDate: map['completionDate'] as int? ?? 0,
      days: (map['days'] as List<dynamic>?)
              ?.map((d) => ChallengeDay.fromMap(d as Map<String, dynamic>))
              .toList() ??
          [],
    );
  }

  factory SpiritualChallenge.fromJson(Map<String, dynamic> json) => SpiritualChallenge.fromMap(json);
}
