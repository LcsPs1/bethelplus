enum MeditationTheme {
  all(displayName: 'Tous', iconName: 'Sparkles'),
  peace(displayName: 'Paix & Sérénité', iconName: 'Spa'),
  gratitude(displayName: 'Gratitude', iconName: 'Favorite'),
  courage(displayName: 'Épreuve & Courage', iconName: 'Shield'),
  family(displayName: 'Famille & Amour', iconName: 'Group'),
  work(displayName: 'Travail & Sagesse', iconName: 'Work'),
  faith(displayName: 'Foi & Espérance', iconName: 'WbSunny'),
  healing(displayName: 'Guérison', iconName: 'LocalHospital'),
  morning(displayName: 'Louange du Matin', iconName: 'WbTwilight'),
  night(displayName: 'Repos de Nuit', iconName: 'NightsStay');

  final String displayName;
  final String iconName;
  const MeditationTheme({required this.displayName, required this.iconName});
}

class MeditationItem {
  final String id;
  final String title;
  final MeditationTheme theme;
  final String scriptureReference;
  final String scriptureText;
  final String reflection;
  final List<String> prayerVariations;
  final int estimatedDurationMinutes;
  final String recommendedTime;

  const MeditationItem({
    required this.id,
    required this.title,
    required this.theme,
    required this.scriptureReference,
    required this.scriptureText,
    required this.reflection,
    this.prayerVariations = const [],
    this.estimatedDurationMinutes = 5,
    this.recommendedTime = 'Tout moment',
  });

  String get guidedPrayer => prayerVariations.isNotEmpty ? prayerVariations.first : '';

  MeditationItem copyWith({
    String? id,
    String? title,
    MeditationTheme? theme,
    String? scriptureReference,
    String? scriptureText,
    String? reflection,
    List<String>? prayerVariations,
    int? estimatedDurationMinutes,
    String? recommendedTime,
  }) {
    return MeditationItem(
      id: id ?? this.id,
      title: title ?? this.title,
      theme: theme ?? this.theme,
      scriptureReference: scriptureReference ?? this.scriptureReference,
      scriptureText: scriptureText ?? this.scriptureText,
      reflection: reflection ?? this.reflection,
      prayerVariations: prayerVariations ?? this.prayerVariations,
      estimatedDurationMinutes: estimatedDurationMinutes ?? this.estimatedDurationMinutes,
      recommendedTime: recommendedTime ?? this.recommendedTime,
    );
  }
}

class MeditationLibrary {
  static const List<MeditationItem> items = [
    MeditationItem(
      id: 'med_bethel_01',
      title: 'Béthel, la Porte des Cieux',
      theme: MeditationTheme.faith,
      scriptureReference: 'Genèse 28:16-17',
      scriptureText: '« Jacob s\'éveilla de son sommeil et dit : Certainement, l\'Éternel est en ce lieu, et moi, je ne le savais pas ! Il eut peur, et dit : Que ce lieu est redoutable ! C\'est ici la maison de Dieu, c\'est ici la porte des cieux ! »',
      reflection: 'Même lorsque nous nous sentons isolés ou en chemin incertain, Dieu est présent. Béthel nous rappelle que notre cœur est Son sanctuaire. Prenons un instant pour faire silence et accueillir Sa présence bienveillante.',
      prayerVariations: [
        'Seigneur, ouvre les yeux de ma foi pour reconnaître Ta présence en ce lieu et en cet instant. Que mon esprit s\'apaise et devienne une demeure où Tu aimes habiter. Amen.',
        'Dieu de Béthel, là où je ne voyais que solitude, Tu révèles une porte ouverte vers le ciel. Je proclame que je ne suis jamais abandonné et que Tes anges veillent sur ma vie. Amen.',
        'Père céleste, je dresse aujourd\'hui un autel de reconnaissance dans le secret de mon cœur. Que Ta sainte présence transforme chaque recoin de mon existence en terre sainte. Amen.'
      ],
      estimatedDurationMinutes: 5,
      recommendedTime: 'Matin ou Soir',
    ),
    MeditationItem(
      id: 'med_peace_01',
      title: 'La Paix qui Surpasse l\'Intelligence',
      theme: MeditationTheme.peace,
      scriptureReference: 'Philippiens 4:6-7',
      scriptureText: '« Ne vous inquiétez de rien ; mais en toute chose faites connaître vos besoins à Dieu par des prières et des supplications, avec des actions de grâces. Et la paix de Dieu, qui surpasse toute intelligence, gardera vos cœurs et vos pensées en Jésus-Christ. »',
      reflection: 'Déposer nos fardeaux n\'est pas fuir le réel, c\'est confier l\'issue à Celui qui tient l\'univers entre Ses mains. Respirez lentement : à chaque expiration, relâchez l\'anxiété ; à chaque inspiration, recevez Sa paix.',
      prayerVariations: [
        'Père céleste, je dépose à Tes pieds mes angoisses et mes soucis d\'aujourd\'hui. Remplis mon être de ce calme profond que le monde ne peut donner. Garde mes pensées sereines. Amen.',
        'Prince de Paix, j\'ordonne à tout tumulte intérieur de se taire au nom de Jésus. Ta paix monte la garde autour de mon cœur et dissipe toute ombre d\'inquiétude. Amen.',
        'Seigneur, je choisis l\'action de grâce au lieu du découragement. Merci de pourvoir souverainement à ce qui m\'échappe et d\'inonder mon âme de Ta douce quiétude. Amen.'
      ],
      estimatedDurationMinutes: 7,
      recommendedTime: 'En journée / Stress',
    ),
    MeditationItem(
      id: 'med_morning_01',
      title: 'L\'Aube et le Renouvellement',
      theme: MeditationTheme.morning,
      scriptureReference: 'Lamentations 3:22-23',
      scriptureText: '« Les bontés de l\'Éternel ne sont pas épuisées, ses compassions ne sont pas à leur terme ; elles se renouvellent chaque matin. Oh ! que ta fidélité est grande ! »',
      reflection: 'Un nouveau jour est une page immaculée. Quelle que soit l\'obscurité d\'hier, la grâce de Dieu se lève avec le soleil pour vous accorder une force fraîche.',
      prayerVariations: [
        'Mon Dieu, merci pour le souffle de vie renouvelé en ce matin. Que mes paroles, mes actes et mes pensées de cette journée rendent hommage à Ta bonté infinie. Amen.',
        'Seigneur, j\'accueille Ta compassion toute neuve pour ce jour. Je laisse derrière moi les regrets passés et m\'avance avec assurance sous Ta divine conduite. Amen.',
        'Fidèle Créateur, revêts-moi de Ta lumière matinale. Que mon cœur reste aligné sur Ta volonté et que je sois un canal de bénédiction pour tous ceux que je croiserai. Amen.'
      ],
      estimatedDurationMinutes: 4,
      recommendedTime: '6h00 - 8h00',
    ),
    MeditationItem(
      id: 'med_gratitude_01',
      title: 'Le Trésor de la Reconnaissance',
      theme: MeditationTheme.gratitude,
      scriptureReference: 'Psaume 103:2-4',
      scriptureText: '« Mon âme, bénis l\'Éternel, et n\'oublie aucun de ses bienfaits ! C\'est lui qui pardonne toutes tes iniquités, qui guérit toutes tes maladies ; c\'est lui qui délivre ta vie de la fosse, qui te couronne de bonté et de miséricorde. »',
      reflection: 'La gratitude transforme ce que nous avons en suffisance. Prenez trois respirations lentes et nommez intérieurement trois grâces reçues aujourd\'hui.',
      prayerVariations: [
        'Seigneur, mon âme Te célèbre. Pour le don de la vie, pour les proches qui m\'entourent, et pour Ta présence silencieuse mais constante, reçois ma louange sincère. Amen.',
        'Père de miséricorde, je me remémore Tes victoires d\'hier pour fortifier ma foi d\'aujourd\'hui. Merci pour Ton pardon libérateur et Ta fidélité inébranlable. Amen.',
        'Mon Dieu, que ma bouche déborde d\'hymnes de joie. Je refuse la plainte et choisis de contempler la magnificence de Tes œuvres dans mon quotidien. Amen.'
      ],
      estimatedDurationMinutes: 5,
      recommendedTime: 'Soir ou Midi',
    ),
    MeditationItem(
      id: 'med_courage_01',
      title: 'La Forteresse au Cœur de l\'Épreuve',
      theme: MeditationTheme.courage,
      scriptureReference: 'Psaume 46:1-2',
      scriptureText: '« Dieu est pour nous un refuge et un appui, un secours qui ne manque jamais dans la détresse. C\'est pourquoi nous sommes sans crainte quand la terre est ébranlée. »',
      reflection: 'Dans la tempête, l\'ancre de notre âme n\'est pas notre propre courage, mais l\'immuabilité de Dieu. Il est notre roc inébranlable.',
      prayerVariations: [
        'Seigneur, affermis mes pas lorsque mes forces fléchissent. Sois mon bouclier et rappelle-moi que Tu as déjà vaincu ce monde. En Toi je place toute ma confiance. Amen.',
        'Dieu tout-puissant, Tu es ma tour imprenable. Aucune tempête ne peut m\'arracher de Ta main. Je me tiens debout par la force de Ton Esprit saint. Amen.',
        'Père des cieux, transforme ma faiblesse en témoignage de Ta gloire. Quand tout tremble autour de moi, je choisis le repos à l\'ombre de Tes ailes. Amen.'
      ],
      estimatedDurationMinutes: 6,
      recommendedTime: 'Temps de combat',
    ),
    MeditationItem(
      id: 'med_night_01',
      title: 'Le Sommeil Paisible du Juste',
      theme: MeditationTheme.night,
      scriptureReference: 'Psaume 4:8',
      scriptureText: '« Je me couche et je m\'endors en paix, car toi seul, ô Éternel ! tu me fais habiter en sécurité. »',
      reflection: "La journée s'achève. Le travail accompli et les tâches inachevées sont désormais entre les mains de Dieu. Laissez votre corps et votre esprit entrer dans le repos sacré.",
      prayerVariations: [
        'Père bien-aimé, je Te remets cette journée écoulée. Pardonne mes faux pas et veille sur mon sommeil ainsi que sur mes proches. Que cette nuit régénère mon âme. Amen.',
        'Seigneur, je dépose à Tes pieds toute charge mentale et tout fardeau. Je m\'endors dans la certitude que Ton amour ne dort ni ne sommeille. Amen.',
        'Doux Jésus, remplis ma chambre de Ta sainte présence. Eloigne tout cauchemar ou agitation et accorde-moi un sommeil réparateur sous Ta protection. Amen.'
      ],
      estimatedDurationMinutes: 5,
      recommendedTime: '21h00 - 23h00',
    ),
    MeditationItem(
      id: 'med_work_01',
      title: 'Sagesse et Intégrité au Travail',
      theme: MeditationTheme.work,
      scriptureReference: 'Colossiens 3:23-24',
      scriptureText: '« Tout ce que vous faites, faites-le de bon cœur, comme pour le Seigneur et non pour des hommes, sachant que vous recevrez du Seigneur l\'héritage pour récompense. »',
      reflection: 'Votre travail et vos études peuvent devenir une prière continue. Consacrer son labeur au Seigneur libère de la vaine gloire et élève l\'effort quotidien.',
      prayerVariations: [
        'Seigneur Jésus, sanctifie mes mains et mon intelligence. Accorde-moi la sagesse, la patience avec mes collègues et la fidélité dans les moindres détails. Amen.',
        'Père des lumières, que mon travail reflète Ton excellence et Ta justice. Inspire mes décisions et accorde-moi la grâce d\'être une source de paix dans mon environnement professionnel. Amen.',
        'Dieu souverain, je Te consacre mes projets et mes ambitions. Que mon labeur porte du fruit durable et glorifie Ton saint Nom. Amen.'
      ],
      estimatedDurationMinutes: 5,
      recommendedTime: 'Midi ou Avant réunion',
    ),
    MeditationItem(
      id: 'med_family_01',
      title: 'La Grâce du Pardon et de l\'Unité',
      theme: MeditationTheme.family,
      scriptureReference: '1 Corinthiens 13:4-7',
      scriptureText: '« L\'amour est patient, il est plein de bonté ; l\'amour n\'est point envieux ; l\'amour ne se vante point, il ne s\'enfle point d\'orgueil... Il excuse tout, il croit tout, il espère tout, il supporte tout. »',
      reflection: 'Aimer sa famille et son prochain exige une grâce renouvelée quotidiennement. Le pardon n\'est pas un sentiment, mais un acte libérateur que Dieu nous donne d\'accomplir.',
      prayerVariations: [
        'Seigneur, fais de moi un instrument de réconciliation et de douceur au sein de mon foyer. Aide-moi à aimer avec générosité, patience et humilité. Amen.',
        'Père céleste, brise toute racine d\'amertume ou de rancœur dans mes relations. Verse Ton amour agapé dans mon cœur pour bénir ceux qui m\'ont blessé. Amen.',
        'Jésus notre modèle, restaure l\'harmonie dans ma maison. Que notre famille soit un havre de paix, de respect mutuel et d\'encouragement sous Ton regard. Amen.'
      ],
      estimatedDurationMinutes: 6,
      recommendedTime: 'Fin d\'après-midi',
    ),
    MeditationItem(
      id: 'med_healing_01',
      title: 'Le Baume de la Guérison Intérieure',
      theme: MeditationTheme.healing,
      scriptureReference: 'Jérémie 17:14',
      scriptureText: '« Guéris-moi, Éternel, et je serai guéri ; sauve-moi, et je serai sauvé ; car tu es ma louange. »',
      reflection: 'Dieu entend les soupirs les plus discrets de notre cœur blessé. Approchons-nous avec confiance du trône de la grâce pour trouver compassion et réconfort.',
      prayerVariations: [
        'Divin Médecin, pose Ta main bienfaisante sur les plaies de mon corps et de mon âme. Restaure ma joie et accorde-moi la force de me relever. Amen.',
        'Seigneur Jésus, par Tes meurtrissures j\'ai été guéri. Je reçois Ta vie et Ton souffle régénérateur dans chaque cellule de mon être. Amen.',
        'Père compatissant, libère-moi de la tristesse et de l\'oppression. Fais couler Ton huile de joie sur mon esprit abattu et ranime ma foi. Amen.'
      ],
      estimatedDurationMinutes: 8,
      recommendedTime: 'Moment de vulnérabilité',
    ),
  ];
}
