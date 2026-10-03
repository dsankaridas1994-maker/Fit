package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.Achievement
import com.example.model.AppThemePreset
import com.example.model.DailyActivity
import com.example.model.Exercise
import com.example.model.ExercisePose
import com.example.model.ExerciseType
import com.example.model.UserPreferences
import com.example.model.UserProfile
import com.example.model.Workout
import com.example.model.WorkoutHistoryItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class InffitRepository(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("inffit_storage_v6", Context.MODE_PRIVATE)

    private val _profile = MutableStateFlow(loadProfile())
    val profile: StateFlow<UserProfile> = _profile.asStateFlow()

    private val _preferences = MutableStateFlow(loadPreferences())
    val preferences: StateFlow<UserPreferences> = _preferences.asStateFlow()

    private val _history = MutableStateFlow(loadHistory())
    val history: StateFlow<List<WorkoutHistoryItem>> = _history.asStateFlow()

    private val _achievements = MutableStateFlow(loadAchievements())
    val achievements: StateFlow<List<Achievement>> = _achievements.asStateFlow()

    private val _customWorkouts = MutableStateFlow(loadCustomWorkouts())
    val customWorkouts: StateFlow<List<Workout>> = _customWorkouts.asStateFlow()

    private val _totalWorkouts = MutableStateFlow(prefs.getInt("stat_workouts", 0))
    val totalWorkouts: StateFlow<Int> = _totalWorkouts.asStateFlow()

    private val _totalMinutes = MutableStateFlow(prefs.getInt("stat_minutes", 0))
    val totalMinutes: StateFlow<Int> = _totalMinutes.asStateFlow()

    private val _totalCalories = MutableStateFlow(prefs.getInt("stat_calories", 0))
    val totalCalories: StateFlow<Int> = _totalCalories.asStateFlow()

    private val _currentStreak = MutableStateFlow(prefs.getInt("stat_streak", 0))
    val currentStreak: StateFlow<Int> = _currentStreak.asStateFlow()

    private val _maxStreak = MutableStateFlow(prefs.getInt("stat_max_streak", 0))
    val maxStreak: StateFlow<Int> = _maxStreak.asStateFlow()

    val isFirstLaunch: Boolean
        get() = !prefs.getBoolean("has_completed_onboarding", false)

    fun completeOnboarding(name: String, goal: Int) {
        val updated = _profile.value.copy(name = name.ifBlank { "Athlete" }, dailyGoalMinutes = if (goal > 0) goal else 20)
        _profile.value = updated
        saveProfile(updated)
        prefs.edit().putBoolean("has_completed_onboarding", true).apply()
    }

    fun updateProfile(name: String, goal: Int) {
        val updated = _profile.value.copy(
            name = name.ifBlank { _profile.value.name },
            dailyGoalMinutes = if (goal > 0) goal else _profile.value.dailyGoalMinutes
        )
        _profile.value = updated
        saveProfile(updated)
    }

    fun updateFullProfile(
        name: String,
        age: Int,
        fitnessGoal: String,
        experienceLevel: String,
        preferredWorkoutType: String,
        availableEquipment: String,
        workoutFrequency: Int,
        dailyGoalMinutes: Int
    ) {
        val updated = _profile.value.copy(
            name = name.ifBlank { _profile.value.name },
            age = if (age > 0) age else _profile.value.age,
            fitnessGoal = fitnessGoal.ifBlank { _profile.value.fitnessGoal },
            experienceLevel = experienceLevel.ifBlank { _profile.value.experienceLevel },
            preferredWorkoutType = preferredWorkoutType.ifBlank { _profile.value.preferredWorkoutType },
            availableEquipment = availableEquipment.ifBlank { _profile.value.availableEquipment },
            workoutFrequency = if (workoutFrequency > 0) workoutFrequency else _profile.value.workoutFrequency,
            dailyGoalMinutes = if (dailyGoalMinutes > 0) dailyGoalMinutes else _profile.value.dailyGoalMinutes
        )
        _profile.value = updated
        saveProfile(updated)
    }

    fun updatePreferences(
        isDark: Boolean? = null,
        isVoice: Boolean? = null,
        isSound: Boolean? = null,
        isVib: Boolean? = null,
        theme: AppThemePreset? = null
    ) {
        val curr = _preferences.value
        val updated = curr.copy(
            isDarkTheme = isDark ?: curr.isDarkTheme,
            isVoiceEnabled = isVoice ?: curr.isVoiceEnabled,
            isSoundEnabled = isSound ?: curr.isSoundEnabled,
            isVibrationEnabled = isVib ?: curr.isVibrationEnabled,
            selectedTheme = theme ?: curr.selectedTheme
        )
        _preferences.value = updated
        savePreferences(updated)
    }

    fun addCustomWorkout(workout: Workout) {
        val updated = _customWorkouts.value + workout
        _customWorkouts.value = updated
        saveCustomWorkouts(updated)
    }

    fun recordCompletedWorkout(workout: Workout, actualMinutes: Int): Pair<Int, List<Achievement>> {
        val durationMins = maxOf(1, actualMinutes)
        val calories = maxOf(durationMins * 7, (workout.calories * durationMins) / maxOf(1, workout.durationMinutes))
        val xpGained = durationMins * 15 + 25

        // Update profile XP
        val newXp = _profile.value.xp + xpGained
        val updatedProfile = _profile.value.copy(xp = newXp)
        _profile.value = updatedProfile
        saveProfile(updatedProfile)

        // Today ISO date
        val todayIso = getTodayIso()
        val displayDateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date())

        val historyItem = WorkoutHistoryItem(
            id = System.currentTimeMillis().toString(),
            workoutId = workout.id,
            workoutTitle = workout.title,
            dateFormatted = displayDateFormat,
            isoDate = todayIso,
            timestamp = System.currentTimeMillis(),
            durationMinutes = durationMins,
            caloriesBurned = calories,
            exercisesCompleted = workout.exercises.size
        )

        val updatedHistory = listOf(historyItem) + _history.value
        _history.value = updatedHistory
        saveHistory(updatedHistory)

        // Update overall stats
        val newTotalWorkouts = _totalWorkouts.value + 1
        val newTotalMinutes = _totalMinutes.value + durationMins
        val newTotalCalories = _totalCalories.value + calories

        _totalWorkouts.value = newTotalWorkouts
        _totalMinutes.value = newTotalMinutes
        _totalCalories.value = newTotalCalories

        // Update streak logic
        val lastDate = prefs.getString("last_active_date", "") ?: ""
        var streak = _currentStreak.value
        if (lastDate.isEmpty()) {
            streak = 1
        } else if (lastDate == todayIso) {
            // Already worked out today, streak stays
        } else {
            val yesterdayIso = getYesterdayIso()
            if (lastDate == yesterdayIso) {
                streak += 1
            } else {
                streak = 1
            }
        }
        val maxStr = maxOf(_maxStreak.value, streak)
        _currentStreak.value = streak
        _maxStreak.value = maxStr

        prefs.edit()
            .putInt("stat_workouts", newTotalWorkouts)
            .putInt("stat_minutes", newTotalMinutes)
            .putInt("stat_calories", newTotalCalories)
            .putInt("stat_streak", streak)
            .putInt("stat_max_streak", maxStr)
            .putString("last_active_date", todayIso)
            .apply()

        // Check achievements unlock
        val newlyUnlocked = checkAndUnlockAchievements(
            workouts = newTotalWorkouts,
            minutes = newTotalMinutes,
            calories = newTotalCalories,
            streak = streak,
            level = updatedProfile.level
        )

        return Pair(xpGained, newlyUnlocked)
    }

    fun getTodayActiveMinutes(): Int {
        val todayIso = getTodayIso()
        return _history.value
            .filter { it.isoDate == todayIso }
            .sumOf { it.durationMinutes }
    }

    fun getDailyActivityHistory(days: Int): List<DailyActivity> {
        val calendar = Calendar.getInstance()
        val list = mutableListOf<DailyActivity>()
        val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
        val isoFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val historyByDate = _history.value.groupBy { it.isoDate }

        for (i in (days - 1) downTo 0) {
            val c = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -i) }
            val dateStr = isoFormat.format(c.time)
            val label = if (days <= 7) dayFormat.format(c.time) else "${c.get(Calendar.DAY_OF_MONTH)}"
            val mins = historyByDate[dateStr]?.sumOf { it.durationMinutes } ?: 0
            list.add(DailyActivity(date = dateStr, dayLabel = label, minutes = mins))
        }
        return list
    }

    private fun checkAndUnlockAchievements(
        workouts: Int,
        minutes: Int,
        calories: Int,
        streak: Int,
        level: Int
    ): List<Achievement> {
        val newlyUnlocked = mutableListOf<Achievement>()
        val currentAchievements = _achievements.value.toMutableList()
        val todayDisplay = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date())

        for (i in currentAchievements.indices) {
            val ach = currentAchievements[i]
            if (!ach.isUnlocked) {
                val shouldUnlock = when (ach.id) {
                    "w1" -> workouts >= 1
                    "w5" -> workouts >= 5
                    "w10" -> workouts >= 10
                    "s3" -> streak >= 3
                    "s7" -> streak >= 7
                    "c500" -> calories >= 500
                    "m60" -> minutes >= 60
                    "lvl3" -> level >= 3
                    else -> false
                }
                if (shouldUnlock) {
                    val unlocked = ach.copy(isUnlocked = true, unlockedDate = todayDisplay)
                    currentAchievements[i] = unlocked
                    newlyUnlocked.add(unlocked)
                }
            }
        }

        if (newlyUnlocked.isNotEmpty()) {
            _achievements.value = currentAchievements
            saveAchievements(currentAchievements)
        }
        return newlyUnlocked
    }

    fun resetAllData() {
        prefs.edit().clear().apply()
        _profile.value = UserProfile()
        _preferences.value = UserPreferences()
        _history.value = emptyList()
        _achievements.value = WorkoutCatalog.defaultAchievements
        _customWorkouts.value = emptyList()
        _totalWorkouts.value = 0
        _totalMinutes.value = 0
        _totalCalories.value = 0
        _currentStreak.value = 0
        _maxStreak.value = 0
    }

    private fun loadProfile(): UserProfile {
        return UserProfile(
            name = prefs.getString("prof_name", "Bro") ?: "Bro",
            dailyGoalMinutes = prefs.getInt("prof_goal", 20),
            xp = prefs.getInt("prof_xp", 0)
        )
    }

    private fun saveProfile(profile: UserProfile) {
        prefs.edit()
            .putString("prof_name", profile.name)
            .putInt("prof_goal", profile.dailyGoalMinutes)
            .putInt("prof_xp", profile.xp)
            .apply()
    }

    private fun loadPreferences(): UserPreferences {
        val themeId = prefs.getString("pref_theme_preset", AppThemePreset.ONYX_FLAME.id)
        return UserPreferences(
            isDarkTheme = prefs.getBoolean("pref_dark", true),
            isVoiceEnabled = prefs.getBoolean("pref_voice", true),
            isSoundEnabled = prefs.getBoolean("pref_sound", true),
            isVibrationEnabled = prefs.getBoolean("pref_vib", true),
            selectedTheme = AppThemePreset.fromId(themeId)
        )
    }

    private fun savePreferences(p: UserPreferences) {
        prefs.edit()
            .putBoolean("pref_dark", p.isDarkTheme)
            .putBoolean("pref_voice", p.isVoiceEnabled)
            .putBoolean("pref_sound", p.isSoundEnabled)
            .putBoolean("pref_vib", p.isVibrationEnabled)
            .putString("pref_theme_preset", p.selectedTheme.id)
            .apply()
    }

    private fun loadHistory(): List<WorkoutHistoryItem> {
        val raw = prefs.getString("history_json", null) ?: return emptyList()
        val list = mutableListOf<WorkoutHistoryItem>()
        try {
            val array = JSONArray(raw)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    WorkoutHistoryItem(
                        id = obj.getString("id"),
                        workoutId = obj.getString("workoutId"),
                        workoutTitle = obj.getString("workoutTitle"),
                        dateFormatted = obj.getString("dateFormatted"),
                        isoDate = obj.optString("isoDate", ""),
                        timestamp = obj.getLong("timestamp"),
                        durationMinutes = obj.getInt("durationMinutes"),
                        caloriesBurned = obj.getInt("caloriesBurned"),
                        exercisesCompleted = obj.getInt("exercisesCompleted")
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    private fun saveHistory(list: List<WorkoutHistoryItem>) {
        val array = JSONArray()
        list.take(100).forEach { item ->
            val obj = JSONObject().apply {
                put("id", item.id)
                put("workoutId", item.workoutId)
                put("workoutTitle", item.workoutTitle)
                put("dateFormatted", item.dateFormatted)
                put("isoDate", item.isoDate)
                put("timestamp", item.timestamp)
                put("durationMinutes", item.durationMinutes)
                put("caloriesBurned", item.caloriesBurned)
                put("exercisesCompleted", item.exercisesCompleted)
            }
            array.put(obj)
        }
        prefs.edit().putString("history_json", array.toString()).apply()
    }

    private fun loadAchievements(): List<Achievement> {
        val raw = prefs.getString("achievements_json", null)
        if (raw == null) return WorkoutCatalog.defaultAchievements

        val list = mutableListOf<Achievement>()
        try {
            val array = JSONArray(raw)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    Achievement(
                        id = obj.getString("id"),
                        title = obj.getString("title"),
                        description = obj.getString("description"),
                        icon = obj.getString("icon"),
                        isUnlocked = obj.getBoolean("isUnlocked"),
                        unlockedDate = if (obj.has("unlockedDate")) obj.getString("unlockedDate") else null
                    )
                )
            }
        } catch (_: Exception) {
            return WorkoutCatalog.defaultAchievements
        }
        return list
    }

    private fun saveAchievements(list: List<Achievement>) {
        val array = JSONArray()
        list.forEach { item ->
            val obj = JSONObject().apply {
                put("id", item.id)
                put("title", item.title)
                put("description", item.description)
                put("icon", item.icon)
                put("isUnlocked", item.isUnlocked)
                item.unlockedDate?.let { put("unlockedDate", it) }
            }
            array.put(obj)
        }
        prefs.edit().putString("achievements_json", array.toString()).apply()
    }

    private fun loadCustomWorkouts(): List<Workout> {
        val raw = prefs.getString("custom_workouts_json", null) ?: return emptyList()
        val list = mutableListOf<Workout>()
        try {
            val array = JSONArray(raw)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val exArray = obj.getJSONArray("exercises")
                val exList = mutableListOf<Exercise>()
                for (j in 0 until exArray.length()) {
                    val exObj = exArray.getJSONObject(j)
                    exList.add(
                        Exercise(
                            id = exObj.getString("id"),
                            name = exObj.getString("name"),
                            type = ExerciseType.valueOf(exObj.getString("type")),
                            value = exObj.getInt("value"),
                            instructions = exObj.getString("instructions"),
                            pose = ExercisePose.valueOf(exObj.getString("pose"))
                        )
                    )
                }
                list.add(
                    Workout(
                        id = obj.getString("id"),
                        title = obj.getString("title"),
                        category = obj.getString("category"),
                        level = obj.getString("level"),
                        durationMinutes = obj.getInt("durationMinutes"),
                        calories = obj.getInt("calories"),
                        description = obj.getString("description"),
                        exercises = exList
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    private fun saveCustomWorkouts(list: List<Workout>) {
        val array = JSONArray()
        list.forEach { w ->
            val wObj = JSONObject().apply {
                put("id", w.id)
                put("title", w.title)
                put("category", w.category)
                put("level", w.level)
                put("durationMinutes", w.durationMinutes)
                put("calories", w.calories)
                put("description", w.description)
                val exArray = JSONArray()
                w.exercises.forEach { ex ->
                    val exObj = JSONObject().apply {
                        put("id", ex.id)
                        put("name", ex.name)
                        put("type", ex.type.name)
                        put("value", ex.value)
                        put("instructions", ex.instructions)
                        put("pose", ex.pose.name)
                    }
                    exArray.put(exObj)
                }
                put("exercises", exArray)
            }
            array.put(wObj)
        }
        prefs.edit().putString("custom_workouts_json", array.toString()).apply()
    }

    private fun getTodayIso(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    private fun getYesterdayIso(): String {
        val c = Calendar.getInstance()
        c.add(Calendar.DAY_OF_YEAR, -1)
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(c.time)
    }
}
