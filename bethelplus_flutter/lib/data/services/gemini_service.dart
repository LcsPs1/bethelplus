import 'dart:convert';
import 'package:dio/dio.dart';
import '../models/quiz.dart';

class GeminiService {
  final Dio _dio = Dio(
    BaseOptions(
      baseUrl: 'https://generativelanguage.googleapis.com/v1beta',
      connectTimeout: const Duration(seconds: 15),
      receiveTimeout: const Duration(seconds: 20),
    ),
  );

  static const String defaultApiKey = String.fromEnvironment('GEMINI_API_KEY', defaultValue: '');

  Future<String> explainVerse({
    required String reference,
    required String text,
    String apiKey = defaultApiKey,
  }) async {
    if (apiKey.isEmpty) {
      // Fallback offline explanation if no API key is provided
      return "« $reference » nous rappelle la bonté infinie et la souveraineté de Dieu.\n\n"
          "Ce passage nous invite à déposer nos fardeaux, à renouveler notre foi et à marcher avec confiance sous Sa conduite quotidienne. "
          "Méditez sur cette promesse et laissez Sa paix remplir votre cœur aujourd'hui.";
    }

    try {
      final prompt = "Tu es un guide spirituel et expert biblique bienveillant. "
          "Paraphrase et explique ce verset de la Bible de façon accessible, profonde et encourageante: "
          "$reference - '$text'. "
          "Rédige en français, en 2 paragraphes clairs et inspirants avec une application pratique pour la vie quotidienne.";

      final response = await _dio.post(
        '/models/gemini-1.5-flash:generateContent?key=$apiKey',
        data: {
          'contents': [
            {
              'parts': [{'text': prompt}]
            }
          ]
        },
      );

      final candidates = response.data['candidates'] as List?;
      if (candidates != null && candidates.isNotEmpty) {
        final content = candidates[0]['content'];
        final parts = content['parts'] as List?;
        if (parts != null && parts.isNotEmpty) {
          return parts[0]['text'] as String;
        }
      }
      return "Explication non disponible pour le moment.";
    } catch (e) {
      return "« $reference » : Gardez cette parole dans votre cœur. Elle est une source de paix et de réconfort divin pour votre journée.";
    }
  }

  Future<Map<String, String>> generateSituationalPrayer({
    required String situation,
    String apiKey = defaultApiKey,
  }) async {
    if (apiKey.isEmpty) {
      return {
        'title': "Prière pour : $situation",
        'scriptureRef': "Philippiens 4:6-7",
        'scriptureText': "Ne vous inquiétez de rien; mais en toute chose faites connaître vos besoins à Dieu par des prières et des supplications...",
        'prayerText': "Père céleste, je remets ma situation concernant $situation entre Tes mains. Accorde-moi Ta paix, Ta sagesse et Ta direction claire. Au nom de Jésus, Amen.",
        'declaration': "Je déclare que Dieu est au contrôle et qu'Il pourvoit à tous mes besoins selon Sa gloire.",
      };
    }

    try {
      final prompt = """
Tu es un conseiller spirituel chrétien. Génère une prière inspirée pour la situation suivante : "$situation".
Réponds UNIQUEMENT avec un JSON valide sous la forme :
{
  "title": "Titre court de la prière",
  "scriptureRef": "Référence biblique",
  "scriptureText": "Texte du verset",
  "prayerText": "Prière fervente et personnalisée (environ 4 à 6 phrases)",
  "declaration": "Déclaration de foi courte et puissante"
}
""";

      final response = await _dio.post(
        '/models/gemini-1.5-flash:generateContent?key=$apiKey',
        data: {
          'contents': [
            {
              'parts': [{'text': prompt}]
            }
          ]
        },
      );

      final candidates = response.data['candidates'] as List?;
      if (candidates != null && candidates.isNotEmpty) {
        final rawText = candidates[0]['content']['parts'][0]['text'] as String;
        final cleanJson = rawText.replaceAll(RegExp(r'```json\n?|```'), '').trim();
        final Map<String, dynamic> data = jsonDecode(cleanJson);
        return {
          'title': data['title']?.toString() ?? "Prière personnalisée",
          'scriptureRef': data['scriptureRef']?.toString() ?? "Psaumes 46:2",
          'scriptureText': data['scriptureText']?.toString() ?? "Dieu est pour nous un refuge et un appui...",
          'prayerText': data['prayerText']?.toString() ?? "",
          'declaration': data['declaration']?.toString() ?? "",
        };
      }
    } catch (_) {}

    return {
      'title': "Prière pour : $situation",
      'scriptureRef': "Psaumes 121:2",
      'scriptureText': "Le secours me vient de l'Éternel, qui a fait les cieux et la terre.",
      'prayerText': "Seigneur Tout-Puissant, je Te confie $situation. Que Ta grâce et Ta paix reposent sur moi. Amen.",
      'declaration': "Mon secours est en l'Éternel qui ne sommeille ni ne dort.",
    };
  }

  Future<List<QuizQuestion>> generateQuiz({
    String topic = "Général",
    String apiKey = defaultApiKey,
  }) async {
    if (apiKey.isEmpty) {
      // Return curated standard Bible quiz questions
      return [
        const QuizQuestion(
          question: "Qui a reçu les Dix Commandements sur le mont Sinaï ?",
          options: ["Abraham", "Moïse", "David", "Élie"],
          correctAnswerIndex: 1,
          explanation: "Moïse a reçu les tables de la loi sur le Sinaï (Exode 20).",
        ),
        const QuizQuestion(
          question: "Combien de livres composent le Nouveau Testament ?",
          options: ["24", "27", "39", "66"],
          correctAnswerIndex: 1,
          explanation: "Le Nouveau Testament contient 27 livres (4 Évangiles, Actes, Épîtres, Apocalypse).",
        ),
        const QuizQuestion(
          question: "Dans quelle ville Jésus est-il né ?",
          options: ["Nazareth", "Jérusalem", "Bethléem", "Capharnaüm"],
          correctAnswerIndex: 2,
          explanation: "Selon Michée 5:2 et Matthieu 2:1, Jésus est né à Bethléem en Judée.",
        ),
        const QuizQuestion(
          question: "Quel est le plus long psaume de la Bible ?",
          options: ["Psaume 23", "Psaume 91", "Psaume 119", "Psaume 150"],
          correctAnswerIndex: 2,
          explanation: "Le Psaume 119 est le plus long avec 176 versets.",
        ),
        const QuizQuestion(
          question: "Quel disciple a marché sur l'eau vers Jésus ?",
          options: ["Jean", "Pierre", "Jacques", "André"],
          correctAnswerIndex: 1,
          explanation: "Pierre est sorti de la barque et a marché sur l'eau (Matthieu 14:29).",
        ),
      ];
    }

    try {
      final prompt = """
Tu es un expert biblique. Génère un quiz de 5 questions sur le thème "$topic".
Réponds UNIQUEMENT avec un JSON valide :
{
  "questions": [
    {
      "question": "Texte de la question ?",
      "options": ["Choix A", "Choix B", "Choix C", "Choix D"],
      "correctAnswerIndex": 0,
      "explanation": "Brève explication avec référence biblique."
    }
  ]
}
""";

      final response = await _dio.post(
        '/models/gemini-1.5-flash:generateContent?key=$apiKey',
        data: {
          'contents': [
            {
              'parts': [{'text': prompt}]
            }
          ]
        },
      );

      final candidates = response.data['candidates'] as List?;
      if (candidates != null && candidates.isNotEmpty) {
        final rawText = candidates[0]['content']['parts'][0]['text'] as String;
        final cleanJson = rawText.replaceAll(RegExp(r'```json\n?|```'), '').trim();
        final Map<String, dynamic> data = jsonDecode(cleanJson);
        final list = data['questions'] as List;
        return list.map((q) => QuizQuestion.fromJson(q)).toList();
      }
    } catch (_) {}

    return generateQuiz(topic: topic, apiKey: '');
  }
}
