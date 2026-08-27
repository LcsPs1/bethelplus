class UserProfile {
  final int id;
  final String firstName;
  final String email;
  final String photoUrl;
  final bool isGoogleConnected;
  final int lastBackupTimestamp;
  final String denomination;
  final int dailyGoalMinutes;
  final String soundTheme;
  final String themeMode;
  final double scriptureFontScale;
  final bool isAutoDndEnabled;

  const UserProfile({
    this.id = 1,
    this.firstName = 'Fidèle',
    this.email = '',
    this.photoUrl = '',
    this.isGoogleConnected = false,
    this.lastBackupTimestamp = 0,
    this.denomination = 'Œcuménique',
    this.dailyGoalMinutes = 15,
    this.soundTheme = 'HARPE',
    this.themeMode = 'SYSTEM',
    this.scriptureFontScale = 1.0,
    this.isAutoDndEnabled = false,
  });

  UserProfile copyWith({
    int? id,
    String? firstName,
    String? email,
    String? photoUrl,
    bool? isGoogleConnected,
    int? lastBackupTimestamp,
    String? denomination,
    int? dailyGoalMinutes,
    String? soundTheme,
    String? themeMode,
    double? scriptureFontScale,
    bool? isAutoDndEnabled,
  }) {
    return UserProfile(
      id: id ?? this.id,
      firstName: firstName ?? this.firstName,
      email: email ?? this.email,
      photoUrl: photoUrl ?? this.photoUrl,
      isGoogleConnected: isGoogleConnected ?? this.isGoogleConnected,
      lastBackupTimestamp: lastBackupTimestamp ?? this.lastBackupTimestamp,
      denomination: denomination ?? this.denomination,
      dailyGoalMinutes: dailyGoalMinutes ?? this.dailyGoalMinutes,
      soundTheme: soundTheme ?? this.soundTheme,
      themeMode: themeMode ?? this.themeMode,
      scriptureFontScale: scriptureFontScale ?? this.scriptureFontScale,
      isAutoDndEnabled: isAutoDndEnabled ?? this.isAutoDndEnabled,
    );
  }

  Map<String, dynamic> toMap() {
    return {
      'id': id,
      'firstName': firstName,
      'email': email,
      'photoUrl': photoUrl,
      'isGoogleConnected': isGoogleConnected ? 1 : 0,
      'lastBackupTimestamp': lastBackupTimestamp,
      'denomination': denomination,
      'dailyGoalMinutes': dailyGoalMinutes,
      'soundTheme': soundTheme,
      'themeMode': themeMode,
      'scriptureFontScale': scriptureFontScale,
      'isAutoDndEnabled': isAutoDndEnabled ? 1 : 0,
    };
  }

  Map<String, dynamic> toJson() => toMap();

  factory UserProfile.fromMap(Map<String, dynamic> map) {
    return UserProfile(
      id: map['id'] as int? ?? 1,
      firstName: map['firstName'] as String? ?? 'Fidèle',
      email: map['email'] as String? ?? '',
      photoUrl: map['photoUrl'] as String? ?? '',
      isGoogleConnected: (map['isGoogleConnected'] as int? ?? 0) == 1 || (map['isGoogleConnected'] == true),
      lastBackupTimestamp: map['lastBackupTimestamp'] as int? ?? 0,
      denomination: map['denomination'] as String? ?? 'Œcuménique',
      dailyGoalMinutes: map['dailyGoalMinutes'] as int? ?? 15,
      soundTheme: map['soundTheme'] as String? ?? 'HARPE',
      themeMode: map['themeMode'] as String? ?? 'SYSTEM',
      scriptureFontScale: (map['scriptureFontScale'] as num?)?.toDouble() ?? 1.0,
      isAutoDndEnabled: (map['isAutoDndEnabled'] as int? ?? 0) == 1 || (map['isAutoDndEnabled'] == true),
    );
  }

  factory UserProfile.fromJson(Map<String, dynamic> json) => UserProfile.fromMap(json);
}
