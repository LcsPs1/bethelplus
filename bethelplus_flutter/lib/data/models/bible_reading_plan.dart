class BibleReadingPlan {
  final String id;
  final String title;
  final String subtitle;
  final int totalDays;
  final int currentDay;
  final String completedDaysCsv;
  final bool isActive;
  final int lastReadTimestamp;

  const BibleReadingPlan({
    required this.id,
    required this.title,
    required this.subtitle,
    required this.totalDays,
    this.currentDay = 1,
    this.completedDaysCsv = '',
    this.isActive = false,
    this.lastReadTimestamp = 0,
  });

  BibleReadingPlan copyWith({
    String? id,
    String? title,
    String? subtitle,
    int? totalDays,
    int? currentDay,
    String? completedDaysCsv,
    bool? isActive,
    int? lastReadTimestamp,
  }) {
    return BibleReadingPlan(
      id: id ?? this.id,
      title: title ?? this.title,
      subtitle: subtitle ?? this.subtitle,
      totalDays: totalDays ?? this.totalDays,
      currentDay: currentDay ?? this.currentDay,
      completedDaysCsv: completedDaysCsv ?? this.completedDaysCsv,
      isActive: isActive ?? this.isActive,
      lastReadTimestamp: lastReadTimestamp ?? this.lastReadTimestamp,
    );
  }

  Map<String, dynamic> toMap() {
    return {
      'id': id,
      'title': title,
      'subtitle': subtitle,
      'totalDays': totalDays,
      'currentDay': currentDay,
      'completedDaysCsv': completedDaysCsv,
      'isActive': isActive ? 1 : 0,
      'lastReadTimestamp': lastReadTimestamp,
    };
  }

  factory BibleReadingPlan.fromMap(Map<String, dynamic> map) {
    return BibleReadingPlan(
      id: map['id'] as String? ?? '',
      title: map['title'] as String? ?? '',
      subtitle: map['subtitle'] as String? ?? '',
      totalDays: map['totalDays'] as int? ?? 1,
      currentDay: map['currentDay'] as int? ?? 1,
      completedDaysCsv: map['completedDaysCsv'] as String? ?? '',
      isActive: (map['isActive'] as int? ?? 0) == 1,
      lastReadTimestamp: map['lastReadTimestamp'] as int? ?? 0,
    );
  }
}
