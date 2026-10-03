package com.example.model

data class Workout(
    val id: String,
    val title: String,
    val category: String, // Quick, Beginner, Core, HIIT, Strength
    val level: String, // Beg, Int, Adv
    val durationMinutes: Int,
    val calories: Int,
    val description: String,
    val exercises: List<Exercise>
)
