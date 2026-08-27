package com.example.data.local

object MemoryData {

    val INITIAL_MEMORY_VERSES = listOf(
        MemorizedVerseEntity(
            id = "mem_jean_3_16",
            reference = "Jean 3:16",
            text = "Car Dieu a tant aimé le monde qu'il a donné son Fils unique, afin que quiconque croit en lui ne périsse point, mais qu'il ait la vie éternelle.",
            bookId = "JHN",
            chapter = 3,
            verse = 16,
            category = "Salut & Amour",
            boxLevel = 1
        ),
        MemorizedVerseEntity(
            id = "mem_phil_4_6_7",
            reference = "Philippiens 4:6-7",
            text = "Ne vous inquiétez de rien; mais en toute chose faites connaître vos besoins à Dieu par des prières et des supplications, avec des actions de grâces. Et la paix de Dieu, qui surpasse toute intelligence, gardera vos cœurs et vos pensées en Jésus-Christ.",
            bookId = "PHP",
            chapter = 4,
            verse = 6,
            category = "Paix & Prière",
            boxLevel = 1
        ),
        MemorizedVerseEntity(
            id = "mem_rom_8_28",
            reference = "Romains 8:28",
            text = "Nous savons, du reste, que toutes choses concourent au bien de ceux qui aiment Dieu, de ceux qui sont appelés selon son dessein.",
            bookId = "ROM",
            chapter = 8,
            verse = 28,
            category = "Confiance & Espérance",
            boxLevel = 1
        ),
        MemorizedVerseEntity(
            id = "mem_ps_23_1",
            reference = "Psaume 23:1",
            text = "L'Éternel est mon berger: je ne manquerai de rien.",
            bookId = "PSA",
            chapter = 23,
            verse = 1,
            category = "Pourvoyance & Sécurité",
            boxLevel = 1
        ),
        MemorizedVerseEntity(
            id = "mem_josue_1_9",
            reference = "Josué 1:9",
            text = "Ne t'ai-je pas donné cet ordre: Fortifie-toi et prends courage ? Ne t'effraie point et ne t'épouvante point, car l'Éternel, ton Dieu, est avec toi dans tout ce que tu entreprendras.",
            bookId = "JOS",
            chapter = 1,
            verse = 9,
            category = "Courage & Foi",
            boxLevel = 1
        ),
        MemorizedVerseEntity(
            id = "mem_esaie_41_10",
            reference = "Ésaïe 41:10",
            text = "Ne crains rien, car je suis avec toi; Ne promène pas des regards inquiets, car je suis ton Dieu; Je te fortifie, je viens à ton secours, Je te soutiens de ma droite triomphante.",
            bookId = "ISA",
            chapter = 41,
            verse = 10,
            category = "Courage & Protection",
            boxLevel = 1
        ),
        MemorizedVerseEntity(
            id = "mem_jer_29_11",
            reference = "Jérémie 29:11",
            text = "Car je connais les projets que j'ai formés sur vous, dit l'Éternel, projets de paix et non de malheur, afin de vous donner un avenir et de l'espérance.",
            bookId = "JER",
            chapter = 29,
            verse = 11,
            category = "Destinée & Avenir",
            boxLevel = 1
        ),
        MemorizedVerseEntity(
            id = "mem_prov_3_5_6",
            reference = "Proverbes 3:5-6",
            text = "Confie-toi en l'Éternel de tout ton cœur, et ne t'appuie pas sur ta sagesse; Reconnais-le dans toutes tes voies, et il aplanira tes sentiers.",
            bookId = "PRO",
            chapter = 3,
            verse = 5,
            category = "Sagesse & Direction",
            boxLevel = 1
        )
    )

    /**
     * Calculates the next review timestamp using the Leitner Spaced Repetition model:
     * - Box 1: In 1 day (86,400,000 ms)
     * - Box 2: In 3 days (3 * 86,400,000 ms)
     * - Box 3: In 7 days (7 * 86,400,000 ms)
     * - Box 4: In 14 days (14 * 86,400,000 ms)
     * - Box 5: In 30 days (30 * 86,400,000 ms) -> Mastered!
     */
    fun calculateNextReview(currentBox: Int, isCorrect: Boolean): Pair<Int, Long> {
        val oneDayMillis = 24 * 60 * 60 * 1000L
        val now = System.currentTimeMillis()

        return if (isCorrect) {
            val newBox = (currentBox + 1).coerceAtMost(5)
            val intervalDays = when (newBox) {
                1 -> 1
                2 -> 3
                3 -> 7
                4 -> 14
                5 -> 30
                else -> 30
            }
            Pair(newBox, now + (intervalDays * oneDayMillis))
        } else {
            // Reset to Box 1 for reinforcement
            Pair(1, now + (1 * oneDayMillis))
        }
    }
}
