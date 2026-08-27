package com.example.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class QuizQuestion(
    val question: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val explanation: String
)

@JsonClass(generateAdapter = true)
data class QuizResponse(
    val questions: List<QuizQuestion>
)
