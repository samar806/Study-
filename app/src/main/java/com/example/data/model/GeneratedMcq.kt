package com.example.data.model

data class GeneratedMcq(
    val question: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val explanation: String,
    val difficulty: String = "Medium",
    val subject: String = "General",
    val topic: String = "General"
)
