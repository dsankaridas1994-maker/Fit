package com.example.model

import com.google.firebase.Timestamp

data class UserProfile(
    val userId: String = "",
    val name: String = "Bro",
    val age: Int = 24,
    val fitnessGoal: String = "Build Muscle & Burn Fat",
    val experienceLevel: String = "Intermediate",
    val preferredWorkoutType: String = "HIIT & Strength",
    val availableEquipment: String = "Bodyweight / Dumbbells",
    val workoutFrequency: Int = 4,
    val dailyGoalMinutes: Int = 20,
    val xp: Int = 0
) {
    val level: Int
        get() = (xp / 100) + 1

    val currentLevelXp: Int
        get() = xp % 100

    val rankTitle: String
        get() = when {
            level >= 10 -> "Legend"
            level >= 7 -> "Beast"
            level >= 5 -> "Titan"
            level >= 3 -> "Warrior"
            level >= 2 -> "Challenger"
            else -> "Novice"
        }
}

data class UserPreferences(
    val userId: String = "",
    val isDarkTheme: Boolean = true,
    val isVoiceEnabled: Boolean = true,
    val isSoundEnabled: Boolean = true,
    val isVibrationEnabled: Boolean = true,
    val selectedTheme: AppThemePreset = AppThemePreset.ONYX_FLAME
)

data class WorkoutHistoryItem(
    val id: String = "",
    val workoutId: String = "",
    val workoutTitle: String = "",
    val dateFormatted: String = "", // e.g. "Oct 02, 2026"
    val isoDate: String = "", // "YYYY-MM-DD"
    val timestamp: Long = 0L,
    val durationMinutes: Int = 0,
    val caloriesBurned: Int = 0,
    val exercisesCompleted: Int = 0,
    val userId: String = ""
)

data class Achievement(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val icon: String = "",
    val isUnlocked: Boolean = false,
    val unlockedDate: String? = null,
    val userId: String = ""
)

data class DailyActivity(
    val date: String = "", // YYYY-MM-DD
    val dayLabel: String = "", // e.g. "Mon"
    val minutes: Int = 0
)

data class FitnessGoal(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val targetValue: Int = 5,
    val currentValue: Int = 0,
    val unit: String = "workouts",
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

data class SavedWorkoutPlan(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val goal: String = "",
    val experienceLevel: String = "",
    val daysPerWeek: Int = 3,
    val durationMin: Int = 15,
    val planDetailsJson: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

data class AiChatMessage(
    val id: String = "",
    val sender: String = "user", // "user" or "ai"
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
