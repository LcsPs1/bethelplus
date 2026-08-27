package com.example.data.local

data class BibleBookInfo(
    val id: String,
    val name: String,
    val testament: String, // "OT" or "NT"
    val category: String, // "Pentateuque", "Historique", "Poétique", "Prophètes", "Évangiles", "Épîtres", "Apocalypse"
    val chaptersCount: Int,
    val abbreviation: String
)

data class ReadingPlanDay(
    val dayNumber: Int,
    val passageTitle: String,
    val bookId: String,
    val chapter: Int,
    val versesRange: String, // e.g. "1-20" or "Chapitre entier"
    val devotionalTheme: String
)

object BibleData {

    val ALL_BOOKS = listOf(
        // Ancien Testament (39 livres)
        // Pentateuque
        BibleBookInfo("GEN", "Genèse", "OT", "Pentateuque", 50, "Gen"),
        BibleBookInfo("EXO", "Exode", "OT", "Pentateuque", 40, "Ex"),
        BibleBookInfo("LEV", "Lévitique", "OT", "Pentateuque", 27, "Lév"),
        BibleBookInfo("NUM", "Nombres", "OT", "Pentateuque", 36, "Nom"),
        BibleBookInfo("DEU", "Deutéronome", "OT", "Pentateuque", 34, "Deut"),

        // Historiques
        BibleBookInfo("JOS", "Josué", "OT", "Historique", 24, "Jos"),
        BibleBookInfo("JDG", "Juges", "OT", "Historique", 21, "Jug"),
        BibleBookInfo("RUT", "Ruth", "OT", "Historique", 4, "Ruth"),
        BibleBookInfo("1SA", "1 Samuel", "OT", "Historique", 31, "1Sam"),
        BibleBookInfo("2SA", "2 Samuel", "OT", "Historique", 24, "2Sam"),
        BibleBookInfo("1KI", "1 Rois", "OT", "Historique", 22, "1Rois"),
        BibleBookInfo("2KI", "2 Rois", "OT", "Historique", 25, "2Rois"),
        BibleBookInfo("1CH", "1 Chroniques", "OT", "Historique", 29, "1Chr"),
        BibleBookInfo("2CH", "2 Chroniques", "OT", "Historique", 36, "2Chr"),
        BibleBookInfo("EZR", "Esdras", "OT", "Historique", 10, "Esd"),
        BibleBookInfo("NEH", "Néhémie", "OT", "Historique", 13, "Néh"),
        BibleBookInfo("EST", "Esther", "OT", "Historique", 10, "Est"),

        // Poétiques & Sapientiaux
        BibleBookInfo("JOB", "Job", "OT", "Poétique", 42, "Job"),
        BibleBookInfo("PSA", "Psaumes", "OT", "Poétique", 150, "Ps"),
        BibleBookInfo("PRO", "Proverbes", "OT", "Poétique", 31, "Prov"),
        BibleBookInfo("ECC", "Ecclésiaste", "OT", "Poétique", 12, "Ecc"),
        BibleBookInfo("SNG", "Cantique des Cantiques", "OT", "Poétique", 8, "Cant"),

        // Grands Prophètes
        BibleBookInfo("ISA", "Ésaïe", "OT", "Prophètes", 66, "És"),
        BibleBookInfo("JER", "Jérémie", "OT", "Prophètes", 52, "Jér"),
        BibleBookInfo("LAM", "Lamentations", "OT", "Prophètes", 5, "Lam"),
        BibleBookInfo("EZK", "Ézéchiel", "OT", "Prophètes", 48, "Ézék"),
        BibleBookInfo("DAN", "Daniel", "OT", "Prophètes", 12, "Dan"),

        // Petits Prophètes
        BibleBookInfo("HOS", "Osée", "OT", "Prophètes", 14, "Os"),
        BibleBookInfo("JOL", "Joël", "OT", "Prophètes", 3, "Joël"),
        BibleBookInfo("AMO", "Amos", "OT", "Prophètes", 9, "Am"),
        BibleBookInfo("OBA", "Abdias", "OT", "Prophètes", 1, "Abd"),
        BibleBookInfo("JON", "Jonas", "OT", "Prophètes", 4, "Jon"),
        BibleBookInfo("MIC", "Michée", "OT", "Prophètes", 7, "Mich"),
        BibleBookInfo("NAM", "Nahum", "OT", "Prophètes", 3, "Nah"),
        BibleBookInfo("HAB", "Habacuc", "OT", "Prophètes", 3, "Hab"),
        BibleBookInfo("ZEP", "Sophonie", "OT", "Prophètes", 3, "Soph"),
        BibleBookInfo("HAG", "Aggée", "OT", "Prophètes", 2, "Agg"),
        BibleBookInfo("ZEC", "Zacharie", "OT", "Prophètes", 14, "Zach"),
        BibleBookInfo("MAL", "Malachie", "OT", "Prophètes", 4, "Mal"),

        // Nouveau Testament (27 livres)
        // Évangiles & Actes
        BibleBookInfo("MAT", "Matthieu", "NT", "Évangiles", 28, "Mat"),
        BibleBookInfo("MRK", "Marc", "NT", "Évangiles", 16, "Marc"),
        BibleBookInfo("LUK", "Luc", "NT", "Évangiles", 24, "Luc"),
        BibleBookInfo("JHN", "Jean", "NT", "Évangiles", 21, "Jean"),
        BibleBookInfo("ACT", "Actes des Apôtres", "NT", "Histoire", 28, "Actes"),

        // Épîtres de Paul
        BibleBookInfo("ROM", "Romains", "NT", "Épîtres", 16, "Rom"),
        BibleBookInfo("1CO", "1 Corinthiens", "NT", "Épîtres", 16, "1Cor"),
        BibleBookInfo("2CO", "2 Corinthiens", "NT", "Épîtres", 13, "2Cor"),
        BibleBookInfo("GAL", "Galates", "NT", "Épîtres", 6, "Gal"),
        BibleBookInfo("EPH", "Éphésiens", "NT", "Épîtres", 6, "Éph"),
        BibleBookInfo("PHP", "Philippiens", "NT", "Épîtres", 4, "Phil"),
        BibleBookInfo("COL", "Colossiens", "NT", "Épîtres", 4, "Col"),
        BibleBookInfo("1TH", "1 Thessaloniciens", "NT", "Épîtres", 5, "1Thes"),
        BibleBookInfo("2TH", "2 Thessaloniciens", "NT", "Épîtres", 3, "2Thes"),
        BibleBookInfo("1TI", "1 Timothée", "NT", "Épîtres", 6, "1Tim"),
        BibleBookInfo("2TI", "2 Timothée", "NT", "Épîtres", 4, "2Tim"),
        BibleBookInfo("TIT", "Tite", "NT", "Épîtres", 3, "Tite"),
        BibleBookInfo("PHM", "Philémon", "NT", "Épîtres", 1, "Phm"),

        // Autres Épîtres
        BibleBookInfo("HEB", "Hébreux", "NT", "Épîtres", 13, "Héb"),
        BibleBookInfo("JAS", "Jacques", "NT", "Épîtres", 5, "Jac"),
        BibleBookInfo("1PE", "1 Pierre", "NT", "Épîtres", 5, "1Pi"),
        BibleBookInfo("2PE", "2 Pierre", "NT", "Épîtres", 3, "2Pi"),
        BibleBookInfo("1JN", "1 Jean", "NT", "Épîtres", 5, "1Jn"),
        BibleBookInfo("2JN", "2 Jean", "NT", "Épîtres", 1, "2Jn"),
        BibleBookInfo("3JN", "3 Jean", "NT", "Épîtres", 1, "3Jn"),
        BibleBookInfo("JUD", "Jude", "NT", "Épîtres", 1, "Jude"),

        // Révélation
        BibleBookInfo("REV", "Apocalypse", "NT", "Apocalypse", 22, "Apoc")
    )

    fun getBookById(bookId: String): BibleBookInfo {
        return ALL_BOOKS.find { it.id.equals(bookId, ignoreCase = true) }
            ?: ALL_BOOKS.first()
    }

    fun getBookByName(name: String): BibleBookInfo? {
        return ALL_BOOKS.find {
            it.name.equals(name, ignoreCase = true) ||
            it.abbreviation.equals(name, ignoreCase = true) ||
            it.id.equals(name, ignoreCase = true)
        }
    }

    // Default reading plans definition
    val DEFAULT_READING_PLANS = listOf(
        BibleReadingPlanEntity(
            id = "plan_1_year",
            title = "La Bible en 1 an",
            subtitle = "Un parcours équilibré à travers l'Ancien Testament, les Psaumes et le Nouveau Testament",
            totalDays = 365,
            currentDay = 1,
            completedDaysCsv = "1",
            isActive = true
        ),
        BibleReadingPlanEntity(
            id = "plan_nt_90",
            title = "Le Nouveau Testament en 90 jours",
            subtitle = "Lisez les 27 livres du Nouveau Testament en profondeur en 3 mois",
            totalDays = 90,
            currentDay = 1,
            completedDaysCsv = "",
            isActive = false
        ),
        BibleReadingPlanEntity(
            id = "plan_psalms_30",
            title = "Psaumes & Proverbes en 30 jours",
            subtitle = "Nourrissez votre prière quotidienne des plus beaux cantiques et maximes de sagesse",
            totalDays = 30,
            currentDay = 1,
            completedDaysCsv = "",
            isActive = false
        ),
        BibleReadingPlanEntity(
            id = "plan_gospels_40",
            title = "Sur les pas de Jésus (40 jours)",
            subtitle = "Méditez la vie, les miracles, les paraboles et la résurrection du Christ dans les 4 Évangiles",
            totalDays = 40,
            currentDay = 1,
            completedDaysCsv = "",
            isActive = false
        )
    )

    fun getReadingPlanDay(planId: String, day: Int): ReadingPlanDay {
        return when (planId) {
            "plan_nt_90" -> {
                val bookIndex = ((day - 1) % 27) + 39 // NT books start at index 39
                val book = ALL_BOOKS.getOrNull(bookIndex) ?: ALL_BOOKS.first { it.id == "MAT" }
                val chap = ((day - 1) % book.chaptersCount) + 1
                ReadingPlanDay(
                    dayNumber = day,
                    passageTitle = "${book.name} $chap",
                    bookId = book.id,
                    chapter = chap,
                    versesRange = "1-25",
                    devotionalTheme = "La grâce et la vie dans l'Esprit"
                )
            }
            "plan_psalms_30" -> {
                val psalmNum = ((day - 1) * 5) + 1
                val provNum = (day % 31) + 1
                ReadingPlanDay(
                    dayNumber = day,
                    passageTitle = "Psaumes $psalmNum & Proverbes $provNum",
                    bookId = "PSA",
                    chapter = psalmNum.coerceIn(1, 150),
                    versesRange = "1-12",
                    devotionalTheme = "Louange, confiance et sagesse divine"
                )
            }
            "plan_gospels_40" -> {
                val gospelId = when ((day / 10) % 4) {
                    0 -> "MAT"
                    1 -> "MRK"
                    2 -> "LUK"
                    else -> "JHN"
                }
                val book = getBookById(gospelId)
                val chap = ((day % 10) + 1).coerceIn(1, book.chaptersCount)
                ReadingPlanDay(
                    dayNumber = day,
                    passageTitle = "${book.name} $chap",
                    bookId = book.id,
                    chapter = chap,
                    versesRange = "Passage évangélique",
                    devotionalTheme = "La rencontre vivante avec le Christ"
                )
            }
            else -> {
                // Bible en 1 an
                val otBook = ALL_BOOKS[(day - 1) % 39]
                val otChap = ((day - 1) % otBook.chaptersCount) + 1
                val psChap = ((day - 1) % 150) + 1
                ReadingPlanDay(
                    dayNumber = day,
                    passageTitle = "${otBook.name} $otChap & Psaume $psChap",
                    bookId = otBook.id,
                    chapter = otChap,
                    versesRange = "Lecture du jour",
                    devotionalTheme = "L'Alliance éternelle de Dieu avec son peuple"
                )
            }
        }
    }
}
