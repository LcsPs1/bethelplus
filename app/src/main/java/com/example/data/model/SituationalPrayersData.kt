package com.example.data.model

data class SituationalPrayerCategory(
    val id: String,
    val title: String,
    val iconName: String,
    val description: String,
    val prayers: List<SituationalPrayerItem>
)

data class SituationalPrayerItem(
    val id: String,
    val title: String,
    val categoryId: String,
    val subtitle: String,
    val scriptureRef: String,
    val scriptureText: String,
    val prayerText: String,
    val declaration: String,
    val recommendedDurationMinutes: Int = 3
)

object SituationalPrayersRepository {

    val CATEGORIES = listOf(
        SituationalPrayerCategory(
            id = "healing",
            title = "Maladie & Guérison",
            iconName = "Healing",
            description = "Prière pour la restauration physique, le réconfort et la foi",
            prayers = listOf(
                SituationalPrayerItem(
                    id = "heal_1",
                    categoryId = "healing",
                    title = "Prière pour la Guérison Physique",
                    subtitle = "S'appuyer sur les meurtrissures de Jésus",
                    scriptureRef = "Ésaïe 53:5",
                    scriptureText = "Mais il était blessé pour nos péchés, brisé pour nos iniquités; Le châtiment qui nous donne la paix est tombé sur lui, et c'est par ses meurtrissures que nous sommes guéris.",
                    prayerText = "Seigneur Jésus, Toi qui as porté nos maladies et nos douleurs à la Croix, je viens devant Toi avec foi. Pose Ta main bienveillante et restauratrice sur mon corps (ou sur celui de mon proche). Que Ton souffle divin ranime chaque cellule affaiblie, chasse toute infection, apaise chaque douleur et restaure la santé parfaite. Je reçois Ta paix et Ta guérison maintenant. Amen.",
                    declaration = "Par les meurtrissures de Jésus, mon corps est racheté et guéri !"
                ),
                SituationalPrayerItem(
                    id = "heal_2",
                    categoryId = "healing",
                    title = "Prière avant une Opération ou Examen Médical",
                    subtitle = "Placer les mains des soignants sous la conduite divine",
                    scriptureRef = "Psaume 107:20",
                    scriptureText = "Il envoya sa parole et les guérit, Il les fit échapper de la fosse.",
                    prayerText = "Père Céleste, je remets entre Tes mains souveraines cet acte médical. Guide l'intelligence, la précision et la dextérité des médecins et du personnel soignant. Éloigne toute complication, accorde un réveil serein et une convalescence rapide. Je me repose dans Ton amour parfait. Amen.",
                    declaration = "L'Éternel est mon refuge et ma force dans cette épreuve médicale."
                )
            )
        ),
        SituationalPrayerCategory(
            id = "comfort",
            title = "Deuil & Consolation",
            iconName = "Favorite",
            description = "Paix pour le cœur brisé et espérance vivante",
            prayers = listOf(
                SituationalPrayerItem(
                    id = "comf_1",
                    categoryId = "comfort",
                    title = "Prière dans la Douleur du Deuil",
                    subtitle = "Consolation du Dieu de toute grâce",
                    scriptureRef = "Psaume 34:19",
                    scriptureText = "L'Éternel est près de ceux qui ont le cœur brisé, Et il sauve ceux qui ont l'esprit dans l'abattement.",
                    prayerText = "Seigneur, les larmes coulent et le vide est immense. Mais je sais que Tu es tout près de mon cœur blessé. Viens essuyer mes larmes, comble l'absence par Ta sainte présence et dépose en moi l'espérance de la vie éternelle. Tiens-moi debout quand la peine se fait trop lourde. Amen.",
                    declaration = "Même dans la vallée de l'ombre, le Seigneur marche à mes côtés."
                )
            )
        ),
        SituationalPrayerCategory(
            id = "exams",
            title = "Examens & Concours",
            iconName = "School",
            description = "Clarté mentale, mémoire, concentration et faveur",
            prayers = listOf(
                SituationalPrayerItem(
                    id = "exam_1",
                    categoryId = "exams",
                    title = "Prière avant une Épreuve ou Entretien",
                    subtitle = "Intelligence et calme divin",
                    scriptureRef = "Jacques 1:5",
                    scriptureText = "Si quelqu'un d'entre vous manque de sagesse, qu'il la demande à Dieu, qui donne à tous simplement et sans reproche, et elle lui sera donnée.",
                    prayerText = "Seigneur Dieu, Toi la source de toute connaissance et sagesse, bénis le travail que j'ai fourni. Chasse tout trac et toute confusion de mon esprit. Accorde-moi une mémoire vive, des idées claires et la faveur devant les examinateurs. Que ma bouche s'exprime avec assurance et humilité. Amen.",
                    declaration = "Je puis tout par Christ qui me fortifie et illumine mon intelligence."
                )
            )
        ),
        SituationalPrayerCategory(
            id = "travel",
            title = "Voyage & Déplacement",
            iconName = "DirectionsCar",
            description = "Couverture angélique et protection sur la route",
            prayers = listOf(
                SituationalPrayerItem(
                    id = "trav_1",
                    categoryId = "travel",
                    title = "Prière avant un Voyage ou Trajet",
                    subtitle = "Protection du Psaume 91",
                    scriptureRef = "Psaume 121:8",
                    scriptureText = "L'Éternel gardera ton départ et ton arrivée, dès maintenant et à jamais.",
                    prayerText = "Éternel des armées, je Te consacre ce déplacement (route, avion, transport). Déploie Tes anges sur mon chemin pour écarter tout danger, panne ou piège de l'ennemi. Garde mon départ et mon arrivée sous Ton aile protectrice, et ramène-moi dans la paix et la joie. Amen.",
                    declaration = "Aucun malheur ne m'arrivera, l'Éternel veille sur chacun de mes pas."
                )
            )
        ),
        SituationalPrayerCategory(
            id = "anxiety",
            title = "Angoisse & Insomnie",
            iconName = "NightsStay",
            description = "Apaisement immédiat, sommeil doux et esprit délivré de la panique",
            prayers = listOf(
                SituationalPrayerItem(
                    id = "anx_1",
                    categoryId = "anxiety",
                    title = "Prière contre la Panique et l'Oppression",
                    subtitle = "Recevoir la paix surnaturelle de Christ",
                    scriptureRef = "Jean 14:27",
                    scriptureText = "Je vous laisse la paix, je vous donne ma paix. Je ne vous donne pas comme le monde donne. Que votre cœur ne se trouble point, et ne s'alarme point.",
                    prayerText = "Jésus, Prince de la Paix, je dépose à Tes pieds ce tourbillon de pensées et ce nœud dans ma poitrine. Je refuse l'esprit de peur et d'angoisse. J'ordonne au calme de revenir dans mon esprit au Nom de Jésus. Remplis mon cœur de Ta douce quiétude. Je suis en sécurité dans Tes bras. Amen.",
                    declaration = "Dieu ne m'a pas donné un esprit de timidité, mais de force, d'amour et de sagesse."
                ),
                SituationalPrayerItem(
                    id = "anx_2",
                    categoryId = "anxiety",
                    title = "Prière pour un Sommeil Réparateur",
                    subtitle = "Reposer en paix sous la garde de Dieu",
                    scriptureRef = "Psaume 4:9",
                    scriptureText = "Je me couche et je m'endors en paix, car toi seul, ô Éternel ! tu me fais habiter en sécurité.",
                    prayerText = "Seigneur, la journée s'achève et je remets toutes mes préoccupations entre Tes mains. Chasse tout cauchemar, calme mon système nerveux et accorde-moi une nuit de repos profond et sanctifié. À mon réveil, je serai renouvelé(e) pour Te servir. Amen.",
                    declaration = "Je m'endors dans la paix de mon Dieu, entouré(e) de Sa présence rassurante."
                )
            )
        ),
        SituationalPrayerCategory(
            id = "decision",
            title = "Décision & Sagesse",
            iconName = "Navigation",
            description = "Discernement divin pour les choix cruciaux de vie",
            prayers = listOf(
                SituationalPrayerItem(
                    id = "dec_1",
                    categoryId = "decision",
                    title = "Prière pour un Choix Important",
                    subtitle = "Suivre la voix du Bon Berger",
                    scriptureRef = "Proverbes 3:6",
                    scriptureText = "Reconnais-le dans toutes tes voies, et il aplanira tes sentiers.",
                    prayerText = "Saint-Esprit, Guide infaillible, je suis face à un carrefour important. Ne permets pas que mes émotions ou mes intérêts charnels dictent mon choix. Ouvre les portes qui viennent de Toi et ferme hermétiquement celles qui ne sont pas Ta volonté. Rends mon cœur sensible à Ta voix et donne-moi le courage d'obéir. Amen.",
                    declaration = "Mes pas sont ordonnés par l'Éternel et Sa Parole est une lampe à mes pieds."
                )
            )
        ),
        SituationalPrayerCategory(
            id = "finances",
            title = "Finances & Travail",
            iconName = "Work",
            description = "Pourvoyance, percée professionnelle et intégrité",
            prayers = listOf(
                SituationalPrayerItem(
                    id = "fin_1",
                    categoryId = "finances",
                    title = "Prière dans le Besoin Matériel ou la Recherche d'Emploi",
                    subtitle = "Confiance en Jéhovah Jiré",
                    scriptureRef = "Philippiens 4:19",
                    scriptureText = "Et mon Dieu pourvoira à tous vos besoins selon sa richesse, avec gloire, en Jésus-Christ.",
                    prayerText = "Dieu Tout-Puissant, Toi à qui appartiennent l'or et l'argent, je Te présente mes factures, mes dettes et mes charges. Ouvre les écluses des cieux pour une opportunité de travail digne, accorde-moi la sagesse pour gérer mes ressources et bénis le fruit de mes mains. Je ne manquerai d'aucun bien. Amen.",
                    declaration = "L'Éternel est mon berger, je ne manquerai de rien !"
                )
            )
        ),
        SituationalPrayerCategory(
            id = "relationships",
            title = "Couple, Famille & Pardon",
            iconName = "Handshake",
            description = "Réconciliation, brisement des rancœurs et amour vrai",
            prayers = listOf(
                SituationalPrayerItem(
                    id = "rel_1",
                    categoryId = "relationships",
                    title = "Prière pour la Réconciliation et le Pardon",
                    subtitle = "Libérer l'amour du Christ dans les relations",
                    scriptureRef = "Éphésiens 4:32",
                    scriptureText = "Soyez bons les uns envers les autres, compatissants, vous pardonnant réciproquement, comme Dieu vous a pardonné en Christ.",
                    prayerText = "Père Céleste, je choisis aujourd'hui de renoncer à l'amertume, à la rancune et au désir de vengeance. Comme Tu m'as tout pardonné, je libère mon cœur et je pardonne à ceux qui m'ont offensé(e). Répands l'esprit de concorde et d'amour au sein de mon foyer et de mes relations. Amen.",
                    declaration = "L'amour de Dieu triomphe du ressentiment et restaure les cœurs."
                )
            )
        ),
        SituationalPrayerCategory(
            id = "spiritual_warfare",
            title = "Combat Spirituel & Délivrance",
            iconName = "Shield",
            description = "Autorité du Nom de Jésus contre toute oppression",
            prayers = listOf(
                SituationalPrayerItem(
                    id = "war_1",
                    categoryId = "spiritual_warfare",
                    title = "Prière de Protection et d'Autorité",
                    subtitle = "Revêtir toutes les armes de Dieu",
                    scriptureRef = "Éphésiens 6:11",
                    scriptureText = "Revêtez-vous de toutes les armes de Dieu, afin de pouvoir tenir ferme contre les ruses du diable.",
                    prayerText = "Au Nom tout-puissant de Jésus-Christ, je me couvre de Son précieux sang. Je brise toute parole négative, toute malédiction ou tout piège de l'ennemi tendu contre ma vie, ma famille et ma destinée. Je revêts la cuirasse de justice, le bouclier de la foi et l'épée de l'Esprit. Rien ne pourra me nuire. Amen.",
                    declaration = "Aucune arme forgée contre moi ne prospérera !"
                )
            )
        ),
        SituationalPrayerCategory(
            id = "thanksgiving",
            title = "Action de Grâce & Victoire",
            iconName = "Celebration",
            description = "Célébrer la bonté du Seigneur et Ses bienfaits",
            prayers = listOf(
                SituationalPrayerItem(
                    id = "thk_1",
                    categoryId = "thanksgiving",
                    title = "Prière d'Émerveillement et de Louange",
                    subtitle = "Bénir l'Éternel pour Ses merveilles",
                    scriptureRef = "Psaume 103:2",
                    scriptureText = "Mon âme, bénis l'Éternel, Et n'oublie aucun de ses bienfaits !",
                    prayerText = "Seigneur, mon cœur déborde de joie et de gratitude ! Merci pour Ta grâce inépuisable, pour Tes réponses à mes prières et pour la victoire que Tu m'as accordée. Reçois toute la gloire, l'honneur et l'adoration. Que ma vie entière soit un cantique à Ta louange. Alléluia ! Amen.",
                    declaration = "Je chanterai les louanges de mon Dieu tant que j'existerai !"
                )
            )
        )
    )

    val ALL_PRAYERS: List<SituationalPrayerItem> by lazy {
        CATEGORIES.flatMap { it.prayers }
    }
}
