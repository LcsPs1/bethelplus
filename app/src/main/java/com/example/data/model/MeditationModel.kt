package com.example.data.model

enum class MeditationTheme(val displayName: String, val iconName: String) {
    ALL("Tous", "Sparkles"),
    PEACE("Paix & Sérénité", "Spa"),
    GRATITUDE("Gratitude", "Favorite"),
    COURAGE("Épreuve & Courage", "Shield"),
    FAMILY("Famille & Amour", "Group"),
    WORK("Travail & Sagesse", "Work"),
    FAITH("Foi & Espérance", "WbSunny"),
    HEALING("Guérison", "LocalHospital"),
    MORNING("Louange du Matin", "WbTwilight"),
    NIGHT("Repos de Nuit", "NightsStay")
}

data class MeditationItem(
    val id: String,
    val title: String,
    val theme: MeditationTheme,
    val scriptureReference: String,
    val scriptureText: String,
    val reflection: String,
    val guidedPrayer: String,
    val estimatedDurationMinutes: Int = 5,
    val recommendedTime: String = "Tout moment"
)

object MeditationLibrary {
    val items: List<MeditationItem> = listOf(
        MeditationItem(
            id = "med_bethel_01",
            title = "Béthel, la Porte des Cieux",
            theme = MeditationTheme.FAITH,
            scriptureReference = "Genèse 28:16-17",
            scriptureText = "« Jacob s'éveilla de son sommeil et dit : Certainement, l'Éternel est en ce lieu, et moi, je ne le savais pas ! Il eut peur, et dit : Que ce lieu est redoutable ! C'est ici la maison de Dieu, c'est ici la porte des cieux ! »",
            reflection = "Même lorsque nous nous sentons isolés ou en chemin incertain, Dieu est présent. Béthel nous rappelle que notre cœur est Son sanctuaire. Prenons un instant pour faire silence et accueillir Sa présence bienveillante.",
            guidedPrayer = "Seigneur, ouvre les yeux de ma foi pour reconnaître Ta présence en ce lieu et en cet instant. Que mon esprit s'apaise et devienne une demeure où Tu aimes habiter. Amen.",
            estimatedDurationMinutes = 5,
            recommendedTime = "Matin ou Soir"
        ),
        MeditationItem(
            id = "med_peace_01",
            title = "La Paix qui Surpasse l'Intelligence",
            theme = MeditationTheme.PEACE,
            scriptureReference = "Philippiens 4:6-7",
            scriptureText = "« Ne vous inquiétez de rien ; mais en toute chose faites connaître vos besoins à Dieu par des prières et des supplications, avec des actions de grâces. Et la paix de Dieu, qui surpasse toute intelligence, gardera vos cœurs et vos pensées en Jésus-Christ. »",
            reflection = "Déposer nos fardeaux n'est pas fuir le réel, c'est confier l'issue à Celui qui tient l'univers entre Ses mains. Respirez lentement : à chaque expiration, relâchez l'anxiété ; à chaque inspiration, recevez Sa paix.",
            guidedPrayer = "Père céleste, je dépose à Tes pieds mes angoisses et mes soucis d'aujourd'hui. Remplis mon être de ce calme profond que le monde ne peut donner. Garde mes pensées sereines. Amen.",
            estimatedDurationMinutes = 7,
            recommendedTime = "En journée / Stress"
        ),
        MeditationItem(
            id = "med_morning_01",
            title = "L'Aube et le Renouvellement",
            theme = MeditationTheme.MORNING,
            scriptureReference = "Lamentations 3:22-23",
            scriptureText = "« Les bontés de l'Éternel ne sont pas épuisées, ses compassions ne sont pas à leur terme ; elles se renouvellent chaque matin. Oh ! que ta fidélité est grande ! »",
            reflection = "Un nouveau jour est une page immaculée. Quelle que soit l'obscurité d'hier, la grâce de Dieu se lève avec le soleil pour vous accorder une force fraîche.",
            guidedPrayer = "Mon Dieu, merci pour le souffle de vie renouvelé en ce matin. Que mes paroles, mes actes et mes pensées de cette journée rendent hommage à Ta bonté infinie. Amen.",
            estimatedDurationMinutes = 4,
            recommendedTime = "6h00 - 8h00"
        ),
        MeditationItem(
            id = "med_gratitude_01",
            title = "Le Trésor de la Reconnaissance",
            theme = MeditationTheme.GRATITUDE,
            scriptureReference = "Psaume 103:2",
            scriptureText = "« Mon âme, bénis l'Éternel, et n'oublie aucun de ses bienfaits ! C'est lui qui pardonne toutes tes iniquités, qui guérit toutes tes maladies. »",
            reflection = "La gratitude transforme ce que nous avons en suffisance. Prenez trois respirations lentes et nommez intérieurement trois grâces reçues aujourd'hui.",
            guidedPrayer = "Seigneur, mon âme Te célèbre. Pour le don de la vie, pour les proches qui m'entourent, et pour Ta présence silencieuse mais constante, reçois ma louange sincère. Amen.",
            estimatedDurationMinutes = 5,
            recommendedTime = "Soir ou Midi"
        ),
        MeditationItem(
            id = "med_courage_01",
            title = "La Forteresse au Cœur de l'Épreuve",
            theme = MeditationTheme.COURAGE,
            scriptureReference = "Psaume 46:1-2",
            scriptureText = "« Dieu est pour nous un refuge et un appui, un secours qui ne manque jamais dans la détresse. C'est pourquoi nous sommes sans crainte quand la terre est ébranlée. »",
            reflection = "Dans la tempête, l'ancre de notre âme n'est pas notre propre courage, mais l'immuabilité de Dieu. Il est notre roc inébranlable.",
            guidedPrayer = "Seigneur, affermis mes pas lorsque mes forces fléchissent. Sois mon bouclier et rappelle-moi que Tu as déjà vaincu ce monde. En Toi je place toute ma confiance. Amen.",
            estimatedDurationMinutes = 6,
            recommendedTime = "Temps de combat"
        ),
        MeditationItem(
            id = "med_night_01",
            title = "Le Sommeil Paisible du Juste",
            theme = MeditationTheme.NIGHT,
            scriptureReference = "Psaume 4:8",
            scriptureText = "« Je me couche et je m'endors en paix, car toi seul, ô Éternel ! tu me fais habiter en sécurité. »",
            reflection = "La journée s'achève. Le travail accompli et les tâches inachevées sont désormais entre les mains de Dieu. Laissez votre corps et votre esprit entrer dans le repos sacré.",
            guidedPrayer = "Père bien-aimé, je Te remets cette journée écoulée. Pardonne mes faux pas et veille sur mon sommeil ainsi que sur mes proches. Que cette nuit régénère mon âme. Amen.",
            estimatedDurationMinutes = 5,
            recommendedTime = "21h00 - 23h00"
        ),
        MeditationItem(
            id = "med_work_01",
            title = "Sagesse et Intégrité au Travail",
            theme = MeditationTheme.WORK,
            scriptureReference = "Colossiens 3:23-24",
            scriptureText = "« Tout ce que vous faites, faites-le de bon cœur, comme pour le Seigneur et non pour des hommes, sachant que vous recevrez du Seigneur l'héritage pour récompense. »",
            reflection = "Votre travail et vos études peuvent devenir une prière continue. Consacrer son labeur au Seigneur libère de la vaine gloire et élève l'effort quotidien.",
            guidedPrayer = "Seigneur Jésus, sanctifie mes mains et mon intelligence. Accorde-moi la sagesse, la patience avec mes collègues et la fidélité dans les moindres détails. Amen.",
            estimatedDurationMinutes = 5,
            recommendedTime = "Midi ou Avant réunion"
        ),
        MeditationItem(
            id = "med_family_01",
            title = "La Grâce du Pardon et de l'Unité",
            theme = MeditationTheme.FAMILY,
            scriptureReference = "1 Corinthiens 13:4-7",
            scriptureText = "« L'amour est patient, il est plein de bonté ; l'amour n'est point envieux ; l'amour ne se vante point, il ne s'enfle point d'orgueil... Il excuse tout, il croit tout, il espère tout, il supporte tout. »",
            reflection = "Aimer sa famille et son prochain exige une grâce renouvelée quotidiennement. Le pardon n'est pas un sentiment, mais un acte libérateur que Dieu nous donne d'accomplir.",
            guidedPrayer = "Seigneur, fais de moi un instrument de réconciliation et de douceur au sein de mon foyer. Aide-moi à aimer avec générosité, patience et humilité. Amen.",
            estimatedDurationMinutes = 6,
            recommendedTime = "Fin d'après-midi"
        ),
        MeditationItem(
            id = "med_healing_01",
            title = "Le Baume de la Guérison Intérieure",
            theme = MeditationTheme.HEALING,
            scriptureReference = "Jérémie 17:14",
            scriptureText = "« Guéris-moi, Éternel, et je serai guéri ; sauve-moi, et je serai sauvé ; car tu es ma louange. »",
            reflection = "Dieu entend les soupirs les plus discrets de notre cœur blessé. Approchons-nous avec confiance du trône de la grâce pour trouver compassion et réconfort.",
            guidedPrayer = "Divin Médecin, pose Ta main bienfaisante sur les plaies de mon corps et de mon âme. Restaure ma joie et accorde-moi la force de me relever. Amen.",
            estimatedDurationMinutes = 8,
            recommendedTime = "Moment de vulnérabilité"
        )
    )
}
