class SituationalPrayerCategory {
  final String id;
  final String title;
  final String iconName;
  final String description;
  final List<SituationalPrayerItem> prayers;

  const SituationalPrayerCategory({
    required this.id,
    required this.title,
    required this.iconName,
    required this.description,
    required this.prayers,
  });
}

class SituationalPrayerItem {
  final String id;
  final String title;
  final String categoryId;
  final String subtitle;
  final String scriptureRef;
  final String scriptureText;
  final String prayerText;
  final String declaration;
  final int recommendedDurationMinutes;

  const SituationalPrayerItem({
    required this.id,
    required this.title,
    required this.categoryId,
    required this.subtitle,
    required this.scriptureRef,
    required this.scriptureText,
    required this.prayerText,
    required this.declaration,
    this.recommendedDurationMinutes = 3,
  });

  String get category => categoryId;
  List<String> get tags => [categoryId, title, subtitle];
}

class SituationalPrayersRepository {
  static const List<SituationalPrayerCategory> categories = [
    SituationalPrayerCategory(
      id: 'healing',
      title: 'Maladie & Guérison',
      iconName: 'Healing',
      description: 'Prière pour la restauration physique, le réconfort et la foi',
      prayers: [
        SituationalPrayerItem(
          id: 'heal_1',
          categoryId: 'Maladie & Guérison',
          title: 'Prière pour la Guérison Physique',
          subtitle: 'S\'appuyer sur les meurtrissures de Jésus',
          scriptureRef: 'Ésaïe 53:5',
          scriptureText: 'Mais il était blessé pour nos péchés, brisé pour nos iniquités; Le châtiment qui nous donne la paix est tombé sur lui, et c\'est par ses meurtrissures que nous sommes guéris.',
          prayerText: 'Seigneur Jésus, Toi qui as porté nos maladies et nos douleurs à la Croix, je viens devant Toi avec foi. Pose Ta main bienveillante et restauratrice sur mon corps (ou sur celui de mon proche). Que Ton souffle divin ranime chaque cellule affaiblie, chasse toute infection, apaise chaque douleur et restaure la santé parfaite. Je reçois Ta paix et Ta guérison maintenant. Amen.',
          declaration: 'Par les meurtrissures de Jésus, mon corps est racheté et guéri !',
        ),
        SituationalPrayerItem(
          id: 'heal_2',
          categoryId: 'Maladie & Guérison',
          title: 'Force dans la Convalescence',
          subtitle: 'Patience et espérance pendant le rétablissement',
          scriptureRef: 'Psaume 103:2-3',
          scriptureText: 'Mon âme, bénis l\'Éternel, Et n\'oublie aucun de ses bienfaits ! C\'est lui qui pardonne toutes tes iniquités, Qui guérit toutes tes maladies.',
          prayerText: 'Père des Miséricordes, donne-moi la patience et renouvelle mes forces jour après jour. Même lorsque la fatigue physique se fait sentir, mon âme s\'appuie sur Ta fidélité inébranlable. Rétablis mon énergie pour Te servir. Amen.',
          declaration: 'L\'Éternel est le soutien de ma vie et ma force dans la faiblesse.',
        ),
      ],
    ),
    SituationalPrayerCategory(
      id: 'anxiety',
      title: 'Anxiété, Stress & Doute',
      iconName: 'Spa',
      description: 'Déposer le fardeau des pensées troublées et recevoir la paix',
      prayers: [
        SituationalPrayerItem(
          id: 'anx_1',
          categoryId: 'Anxiété, Stress & Doute',
          title: 'Prière pour Calmer l\'Esprit et Vaincre l\'Angoisse',
          subtitle: 'Déposer chaque pensée captive sous l\'autorité du Christ',
          scriptureRef: 'Philippiens 4:6-7',
          scriptureText: 'Ne vous inquiétez de rien; mais en toute chose faites connaître vos besoins à Dieu par des prières et des supplications, avec des actions de grâces. Et la paix de Dieu, qui surpasse toute intelligence, gardera vos cœurs et vos pensées en Jésus-Christ.',
          prayerText: 'Seigneur, Tu connais les tempêtes de mon esprit et le poids de mes inquiétudes. Aujourd\'hui, je choisis d\'abandonner le contrôle entre Tes mains souveraines. Remplis mon cœur de Ta paix surnaturelle. Que chaque battement de mon cœur soit au rythme de Ta grâce. Amen.',
          declaration: 'Je refuse l\'esprit de peur ; je reçois un esprit de force, d\'amour et de sagesse.',
        ),
      ],
    ),
    SituationalPrayerCategory(
      id: 'finances',
      title: 'Travail & Finances',
      iconName: 'Work',
      description: 'Sagesse professionnelle, providence divine et paix matérielle',
      prayers: [
        SituationalPrayerItem(
          id: 'fin_1',
          categoryId: 'Travail & Finances',
          title: 'Prière pour la Bénédiction du Travail et la Providence',
          subtitle: 'Faire confiance à Jéhovah Jiré pour chaque besoin',
          scriptureRef: 'Philippiens 4:19',
          scriptureText: 'Et mon Dieu pourvoira à tous vos besoins selon sa richesse, avec gloire, en Jésus-Christ.',
          prayerText: 'Seigneur Dieu, Maître de l\'or et de l\'argent, bénis le fruit de mes mains et accorde-moi la sagesse financière. Ouvre des portes d\'opportunités que nul ne peut fermer, et apprends-moi à être un gestionnaire fidèle et généreux de Tes bénédictions. Amen.',
          declaration: 'Dieu pourvoit abondamment à tous mes besoins légitimes.',
        ),
      ],
    ),
    SituationalPrayerCategory(
      id: 'relationships',
      title: 'Couple, Famille & Pardon',
      iconName: 'Handshake',
      description: 'Réconciliation, brisement des rancœurs et amour vrai',
      prayers: [
        SituationalPrayerItem(
          id: 'rel_1',
          categoryId: 'Couple, Famille & Pardon',
          title: 'Prière pour la Réconciliation et le Pardon',
          subtitle: 'Libérer l\'amour du Christ dans les relations',
          scriptureRef: 'Éphésiens 4:32',
          scriptureText: 'Soyez bons les uns envers les autres, compatissants, vous pardonnant réciproquement, comme Dieu vous a pardonné en Christ.',
          prayerText: 'Père Céleste, je choisis aujourd\'hui de renoncer à l\'amertume, à la rancune et au désir de vengeance. Comme Tu m\'as tout pardonné, je libère mon cœur et je pardonne à ceux qui m\'ont offensé(e). Répands l\'esprit de concorde et d\'amour au sein de mon foyer et de mes relations. Amen.',
          declaration: 'L\'amour de Dieu triomphe du ressentiment et restaure les cœurs.',
        ),
      ],
    ),
    SituationalPrayerCategory(
      id: 'spiritual_warfare',
      title: 'Combat Spirituel & Délivrance',
      iconName: 'Shield',
      description: 'Autorité du Nom de Jésus contre toute oppression',
      prayers: [
        SituationalPrayerItem(
          id: 'war_1',
          categoryId: 'Combat Spirituel & Délivrance',
          title: 'Prière de Protection et d\'Autorité',
          subtitle: 'Revêtir toutes les armes de Dieu',
          scriptureRef: 'Éphésiens 6:11',
          scriptureText: 'Revêtez-vous de toutes les armes de Dieu, afin de pouvoir tenir ferme contre les ruses du diable.',
          prayerText: 'Au Nom tout-puissant de Jésus-Christ, je me couvre de Son précieux sang. Je brise toute parole négative, toute malédiction ou tout piège de l\'ennemi tendu contre ma vie, ma famille et ma destinée. Je revêts la cuirasse de justice, le bouclier de la foi et l\'épée de l\'Esprit. Rien ne pourra me nuire. Amen.',
          declaration: 'Aucune arme forgée contre moi ne prospérera !',
        ),
      ],
    ),
    SituationalPrayerCategory(
      id: 'thanksgiving',
      title: 'Action de Grâce & Victoire',
      iconName: 'Celebration',
      description: 'Célébrer la bonté du Seigneur et Ses bienfaits',
      prayers: [
        SituationalPrayerItem(
          id: 'thk_1',
          categoryId: 'Action de Grâce & Victoire',
          title: 'Prière d\'Émerveillement et de Louange',
          subtitle: 'Bénir l\'Éternel pour Ses merveilles',
          scriptureRef: 'Psaume 103:2',
          scriptureText: 'Mon âme, bénis l\'Éternel, Et n\'oublie aucun de ses bienfaits !',
          prayerText: 'Seigneur, mon cœur déborde de joie et de gratitude ! Merci pour Ta grâce inépuisable, pour Tes réponses à mes prières et pour la victoire que Tu m\'as accordée. Reçois toute la gloire, l\'honneur et l\'adoration. Que ma vie entière soit un cantique à Ta louange. Alléluia ! Amen.',
          declaration: 'Je chanterai les louanges de mon Dieu tant que j\'existerai !',
        ),
      ],
    ),
  ];

  static List<SituationalPrayerItem> get allPrayers {
    return categories.expand((cat) => cat.prayers).toList();
  }

  static List<SituationalPrayerItem> get items => allPrayers;
}
