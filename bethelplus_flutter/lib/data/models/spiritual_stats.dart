class DayActivity {
  final String dayName;
  final int dayOfMonth;
  final bool isToday;
  final int sessionCount;
  final int totalMinutes;

  const DayActivity({
    required this.dayName,
    required this.dayOfMonth,
    required this.isToday,
    required this.sessionCount,
    required this.totalMinutes,
  });
}

class SpiritualBadge {
  final String id;
  final String title;
  final String subtitle;
  final String icon;
  final bool isUnlocked;

  const SpiritualBadge({
    required this.id,
    required this.title,
    required this.subtitle,
    required this.icon,
    required this.isUnlocked,
  });
}

class SpiritualStats {
  final int currentStreak;
  final int totalMinutes;
  final int totalSessions;
  final bool hasPrayedToday;
  final List<DayActivity> weeklyHeatmap;
  final List<SpiritualBadge> unlockedBadges;

  const SpiritualStats({
    this.currentStreak = 0,
    this.totalMinutes = 0,
    this.totalSessions = 0,
    this.hasPrayedToday = false,
    this.weeklyHeatmap = const [],
    this.unlockedBadges = const [],
  });
}
