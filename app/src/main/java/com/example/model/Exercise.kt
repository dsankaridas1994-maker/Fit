package com.example.model

enum class ExerciseType {
    TIME,
    REPS
}

enum class ExercisePose {
    JUMPING_JACKS,
    SQUATS,
    PUSH_UPS,
    PLANK,
    CRUNCHES,
    MOUNTAIN_CLIMBERS,
    HIGH_KNEES,
    LUNGES,
    BURPEES,
    REST
}

data class Exercise(
    val id: String,
    val name: String,
    val type: ExerciseType,
    val value: Int, // seconds for TIME, count for REPS
    val instructions: String,
    val pose: ExercisePose,
    val targetMuscle: String = "Full Body",
    val secondaryMuscles: String = "Core",
    val equipment: String = "Bodyweight",
    val difficulty: String = "Beginner",
    val commonMistakes: String = "Rushing the movement and breaking posture.",
    val alternatives: String = "Modified tempo or incline regression."
)
