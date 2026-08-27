enum SoundscapeType {
  harp(id: 'HARPE', displayName: 'Harpe Céleste', description: 'Notes harmoniques douces et apaisantes'),
  chime(id: 'CLOCHE', displayName: 'Cloche du Sanctuaire', description: 'Carillon doux marquant les temps sacrés'),
  rain(id: 'PLUIE', displayName: 'Pluie Apaisante', description: 'Ruissellement calme d\'une averse bienfaisante'),
  breeze(id: 'BRISE', displayName: 'Brise d\'Éden', description: 'Souffle léger et murmurant'),
  silence(id: 'SILENCE', displayName: 'Silence Sacré', description: 'Méditation en silence pur avec vibration douce');

  final String id;
  final String displayName;
  final String description;

  const SoundscapeType({
    required this.id,
    required this.displayName,
    required this.description,
  });

  static SoundscapeType fromId(String id) {
    return SoundscapeType.values.firstWhere(
      (e) => e.id.toUpperCase() == id.toUpperCase(),
      orElse: () => SoundscapeType.harp,
    );
  }
}
