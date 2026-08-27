package com.example.data.model

import java.util.Calendar

data class DailyVerse(
    val id: String,
    val reference: String,
    val text: String,
    val reflection: String,
    val actionPrompt: String,
    val translation: String = "Louis Segond (LSG)"
)

object DailyVerseRepository {
    val verses: List<DailyVerse> = listOf(
        DailyVerse(
            id = "VERSE_JOS_1_9",
            reference = "Josué 1:9",
            text = "« Ne t'ai-je pas donné cet ordre : Fortifie-toi et prends courage ? Ne t'effraie point et ne t'épouvante point, car l'Éternel, ton Dieu, est avec toi dans tout ce que tu entreprendras. »",
            reflection = "La promesse de Sa présence suffit pour vaincre toute appréhension aujourd'hui.",
            actionPrompt = "Respirez avec confiance et engagez votre première tâche avec hardiesse."
        ),
        DailyVerse(
            id = "VERSE_GEN_28_17",
            reference = "Genèse 28:17",
            text = "« Que ce lieu est redoutable ! C'est ici la maison de Dieu, c'est ici la porte des cieux ! »",
            reflection = "Béthel n'est pas un lieu lointain, c'est la présence vivante de Dieu dans votre quotidien.",
            actionPrompt = "Consacrez 5 minutes de silence complet pour honorer Sa présence."
        ),
        DailyVerse(
            id = "VERSE_PSA_23_1",
            reference = "Psaume 23:1",
            text = "« L'Éternel est mon berger : je ne manquerai de rien. Il me fait reposer dans de verts pâturages, il me dirige près des eaux paisibles. »",
            reflection = "Le Bon Berger devance vos pas et prépare pour vous le repos intérieur.",
            actionPrompt = "Remerciez Dieu pour Sa providence et Sa fidélité constante."
        ),
        DailyVerse(
            id = "VERSE_ISA_40_31",
            reference = "Ésaïe 40:31",
            text = "« Mais ceux qui se confient en l'Éternel renouvellent leur force. Ils prennent le vol comme les aigles ; ils courent, et ne se lassent point ; ils marchent, et ne se fatiguent point. »",
            reflection = "L'attente dans la prière n'est pas une perte de temps, c'est le rechargement de votre âme.",
            actionPrompt = "Déposez votre fatigue et demandez un renouveau spirituel."
        ),
        DailyVerse(
            id = "VERSE_MAT_6_33",
            reference = "Matthieu 6:33",
            text = "« Cherchez premièrement le royaume et la justice de Dieu ; et toutes ces choses vous seront données par-dessus. »",
            reflection = "Remettre Dieu au centre de vos priorités ordonne harmonieusement tout le reste.",
            actionPrompt = "Identifiez votre priorité spirituelle numéro un pour aujourd'hui."
        ),
        DailyVerse(
            id = "VERSE_PRO_3_5",
            reference = "Proverbes 3:5-6",
            text = "« Confie-toi en l'Éternel de tout ton cœur, et ne t'appuie pas sur ta sagesse ; reconnais-le dans toutes tes voies, et il aplanira tes sentiers. »",
            reflection = "Lâcher prise sur le contrôle permet à la divine providence de tracer votre route.",
            actionPrompt = "Présentez à Dieu une décision complexe qui pèse sur votre esprit."
        ),
        DailyVerse(
            id = "VERSE_ROM_8_28",
            reference = "Romains 8:28",
            text = "« Nous savons, du reste, que toutes choses concourent au bien de ceux qui aiment Dieu, de ceux qui sont appelés selon son dessein. »",
            reflection = "Aucune épreuve n'est vaine dans le plan de grâce de notre Seigneur.",
            actionPrompt = "Bénissez Dieu même pour les imprévus récents."
        )
    )

    fun getTodayVerse(): DailyVerse {
        val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        val index = (dayOfYear - 1) % verses.size
        return verses[index]
    }
}
