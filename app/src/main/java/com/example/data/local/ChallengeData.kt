package com.example.data.local

object ChallengeData {

    val ALL_CHALLENGES = listOf(
        SpiritualChallengeEntity(
            id = "challenge_gratitude_7",
            title = "7 Jours de Gratitude & Louange",
            subtitle = "Transformer son regard et bénir l'Éternel au quotidien",
            category = "GRATITUDE",
            totalDays = 7,
            badgeName = "Cœur d'Adorateur",
            badgeDescription = "A cultivé une atmosphère de louange continue durant 7 jours."
        ),
        SpiritualChallengeEntity(
            id = "challenge_fast_screens_21",
            title = "21 Jours de Jeûne des Écrans",
            subtitle = "Désencombrer l'esprit pour entendre la voix de Dieu",
            category = "FASTING",
            totalDays = 21,
            badgeName = "Lampe du Sanctuaire",
            badgeDescription = "A dompté le bruit du monde pour demeurer dans le sanctuaire intérieur."
        ),
        SpiritualChallengeEntity(
            id = "challenge_intercession_14",
            title = "14 Jours d'Intercession Profonde",
            subtitle = "Se tenir sur la brèche pour sa famille, l'Église et les nations",
            category = "INTERCESSION",
            totalDays = 14,
            badgeName = "Sentinelle de l'Aurore",
            badgeDescription = "A porté fidèlement le fardeau des âmes et des proches devant le Trône de Grâce."
        ),
        SpiritualChallengeEntity(
            id = "challenge_mind_renewal_30",
            title = "30 Jours de Renouvellement des Pensées",
            subtitle = "Selon Romains 12:2, vaincre les peurs et s'ancrer dans la Vérité",
            category = "MIND_RENEWAL",
            totalDays = 30,
            badgeName = "Esprit Sanctifié & Triomphant",
            badgeDescription = "A ancré ses pensées dans la puissance et la paix inébranlable de la Parole de Dieu."
        )
    )

    fun getChallengeDays(challengeId: String): List<ChallengeDayContent> {
        return when (challengeId) {
            "challenge_gratitude_7" -> listOf(
                ChallengeDayContent(
                    dayNumber = 1,
                    title = "Le Souffle de Vie",
                    scriptureRef = "Psaume 150:6",
                    scriptureText = "Que tout ce qui respire loue l'Éternel ! Louez l'Éternel !",
                    reflection = "Chaque matin où nos yeux s'ouvrent est une preuve renouvelée de la bonté de Dieu. Le souffle que nous prenons n'est pas un dû, mais une grâce imméritée.",
                    dailyAction = "Prenez 3 minutes ce matin pour simplement fermer les yeux, respirer et remercier Dieu pour votre santé et la vie reçue.",
                    guidedPrayer = "Seigneur, merci pour le souffle de vie qui m'anime ce matin. Même au milieu des défis, je choisis de commencer ma journée par la reconnaissance. Amen."
                ),
                ChallengeDayContent(
                    dayNumber = 2,
                    title = "La Fidélité Passée",
                    scriptureRef = "1 Samuel 7:12",
                    scriptureText = "Samuel prit une pierre... et lui donna le nom d'Ében-Ézer, en disant : Jusqu'ici l'Éternel nous a secourus.",
                    reflection = "Regardez en arrière : combien de fois Dieu a-t-il ouvert des portes impossibles, pourvu à vos besoins ou calmé vos tempêtes ? Son passé est le garant de son futur.",
                    dailyAction = "Notez dans votre journal spirituel 3 délivrances ou victoires passées que Dieu vous a accordées.",
                    guidedPrayer = "Éternel, Tu es mon Ében-Ézer. Merci pour toutes les fois où Ta main m'a relevé(e). Je dépose ma confiance en Toi pour l'avenir. Amen."
                ),
                ChallengeDayContent(
                    dayNumber = 3,
                    title = "Les Épreuves Transformées",
                    scriptureRef = "Romains 8:28",
                    scriptureText = "Nous savons, du reste, que toutes choses concourent au bien de ceux qui aiment Dieu.",
                    reflection = "Rendre grâce quand tout va bien est naturel. Rendre grâce dans l'épreuve est un acte de foi qui brise l'amertume et invite la gloire divine.",
                    dailyAction = "Identifiez une situation difficile actuelle et remerciez Dieu par avance pour ce qu'Il va en faire grandir en vous.",
                    guidedPrayer = "Père Céleste, je Te remets mes zones de combat. Même sans tout comprendre, je Te remercie car Tu transformes mes déserts en fleuves de bénédiction. Amen."
                ),
                ChallengeDayContent(
                    dayNumber = 4,
                    title = "Les Personnes de Bénédiction",
                    scriptureRef = "Philippiens 1:3",
                    scriptureText = "Je rends grâces à mon Dieu de tout le souvenir que je garde de vous.",
                    reflection = "Dieu utilise souvent des hommes et des femmes pour nous encourager, nous corriger ou nous porter.",
                    dailyAction = "Envoyez aujourd'hui un message de gratitude sincère à une personne qui a été un instrument de paix ou de conseil dans votre vie.",
                    guidedPrayer = "Seigneur, merci pour les personnes bienveillantes que Tu as placées sur mon chemin. Bénis-les au-delà de leurs espérances. Amen."
                ),
                ChallengeDayContent(
                    dayNumber = 5,
                    title = "La Provision Quotidienne",
                    scriptureRef = "Matthieu 6:11",
                    scriptureText = "Donne-nous aujourd'hui notre pain quotidien.",
                    reflection = "Le toit au-dessus de notre tête, le repas sur notre table, l'eau fraîche : autant de miracles discrets que nous oublions de célébrer.",
                    dailyAction = "Avant votre prochain repas, prenez un temps réel de recueillement et de louange pour la nourriture et la pourvoyance divine.",
                    guidedPrayer = "Jéhovah Jiré, le Dieu qui pourvoit, merci pour le pain de chaque jour et pour Ta protection constante sur ma maison. Amen."
                ),
                ChallengeDayContent(
                    dayNumber = 6,
                    title = "La Paix Surnaturelle",
                    scriptureRef = "Colossiens 3:15",
                    scriptureText = "Et que la paix de Christ, à laquelle vous avez été appelés pour former un seul corps, règne dans vos cœurs. Et soyez reconnaissants.",
                    reflection = "La paix du Christ n'est pas l'absence de bruit, mais la présence tangible de Dieu dans le vacarme du monde.",
                    dailyAction = "Dédiez 5 minutes de silence complet ce soir en confessant la paix de Dieu sur vos pensées et votre foyer.",
                    guidedPrayer = "Jésus, Prince de la Paix, que Ta quiétude règne souverainement dans mon esprit. Éloigne tout esprit de plainte et d'inquiétude. Amen."
                ),
                ChallengeDayContent(
                    dayNumber = 7,
                    title = "L'Alliance d'Action de Grâce",
                    scriptureRef = "1 Thessaloniciens 5:18",
                    scriptureText = "Rendez grâces en toutes choses, car c'est à votre égard la volonté de Dieu en Jésus-Christ.",
                    reflection = "Félicitations pour ces 7 jours ! La gratitude n'est plus un exercice ponctuel, elle est devenue votre mode de vie.",
                    dailyAction = "Déclarez à haute voix votre louange finale et célébrez la complétion de ce défi avec joie !",
                    guidedPrayer = "Seigneur, reçois la louange perpétuelle de mon cœur. Que ma bouche ne cesse jamais de proclamer Tes hauts faits. Amen."
                )
            )
            "challenge_fast_screens_21" -> (1..21).map { day ->
                val themes = listOf(
                    "Silence Intérieur", "Désencombrement Mental", "Retour à la Parole Écrite",
                    "Présence Réelle aux Autres", "Protection des Yeux", "Calme du Cœur",
                    "Renouvellement de la Concentration", "Prière au lieu du Scroll", "Discernement Spirituel",
                    "Sommeil Paisible", "Méditation Approfondie", "Écoute de l'Esprit",
                    "Temps Racheté", "Guérison de la Comparaison", "Liberté Intérieure",
                    "Sobriété Numérique", "Adoration Pure", "Fidélité au Sanctuaire",
                    "Esprit Apaisé", "Vision Céleste", "Victoire et Clarté Retrouvée"
                )
                val currentTheme = themes.getOrElse(day - 1) { "Journée de Grâce" }
                ChallengeDayContent(
                    dayNumber = day,
                    title = "Jour $day : $currentTheme",
                    scriptureRef = if (day % 2 == 0) "Psaume 46:11" else "Éphésiens 5:16",
                    scriptureText = if (day % 2 == 0) "Arrêtez, et sachez que je suis Dieu : Je domine sur les nations, je domine sur la terre." else "Rachetez le temps, car les jours sont mauvais.",
                    reflection = "En coupant les distractions futiles aujourd'hui (réseaux sociaux, vidéos inutiles), vous faites de la place dans votre âme pour la voix douce et puissante du Saint-Esprit.",
                    dailyAction = "Fixez une limite stricte de 30 minutes d'écrans aujourd'hui hors travail, et remplacez les temps de pause par la lecture biblique.",
                    guidedPrayer = "Seigneur, purifie mes yeux et mes pensées. Délivre-moi de la captivité des flux numériques et attire-moi dans le secret de Ta présence. Amen."
                )
            }
            "challenge_intercession_14" -> (1..14).map { day ->
                val focusList = listOf(
                    "Ma Famille Proche", "Les Malades & Affligés", "Les Pasteurs & Serviteurs de Dieu",
                    "Les Jeunes & Enfants", "Ceux qui ne connaissent pas Christ", "Les Couples & Mariages",
                    "Mon Pays & Gouvernants", "Les Pauvres & Orphelins", "L'Unité du Corps de Christ",
                    "La Paix à Jérusalem et dans le Monde", "Les Prisonniers & Déprimés", "Les Projets Missionnaires",
                    "La Protection Spirituelle de mes Amis", "Le Réveil Spirituel Global"
                )
                val focus = focusList.getOrElse(day - 1) { "Les Âmes" }
                ChallengeDayContent(
                    dayNumber = day,
                    title = "Jour $day : Prier pour $focus",
                    scriptureRef = "1 Timothée 2:1-2",
                    scriptureText = "J'exhorte donc, avant toutes choses, à faire des prières, des supplications, des requêtes, des actions de grâces, pour tous les hommes.",
                    reflection = "L'intercession est le plus grand acte d'amour : se tenir devant Dieu pour porter le fardeau d'un autre jusqu'à ce que la délivrance éclate.",
                    dailyAction = "Prenez 10 minutes aujourd'hui pour citer des noms précis en rapport avec le thème du jour et intercéder avec ferveur.",
                    guidedPrayer = "Père compatissant, j'élève devant Toi aujourd'hui $focus. Répands Ta grâce, Ta guérison, Ta protection et Ta lumière sur eux, au nom de Jésus. Amen."
                )
            }
            "challenge_mind_renewal_30" -> (1..30).map { day ->
                ChallengeDayContent(
                    dayNumber = day,
                    title = "Jour $day : Pensée de Victoire #$day",
                    scriptureRef = "Romains 12:2",
                    scriptureText = "Ne vous conformez pas au siècle présent, mais soyez transformés par le renouvellement de l'intelligence...",
                    reflection = "Vos pensées déterminent votre trajectoire. Quand le doute ou la panique surgit, alignez votre esprit sur la vérité immuable de l'Écriture.",
                    dailyAction = "Répétez 3 fois ce verset aujourd'hui et rejetez immédiatement toute pensée de défaite en proclamant la victoire du Christ.",
                    guidedPrayer = "Seigneur, je soumets chaque pensée captive à l'obéissance de Christ. Mon esprit est renouvelé, fortifié et inondé de Ta lumière. Amen."
                )
            }
            else -> emptyList()
        }
    }
}
