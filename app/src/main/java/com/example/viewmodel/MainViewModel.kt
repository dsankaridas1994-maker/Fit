package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.AiWorkoutGenerator
import com.example.audio.WorkoutCoach
import com.example.data.FirestoreFitnessRepository
import com.example.data.InffitRepository
import com.example.data.WorkoutCatalog
import com.example.model.Achievement
import com.example.model.AppThemePreset
import com.example.model.DailyActivity
import com.example.model.Exercise
import com.example.model.ExerciseType
import com.example.model.FitnessGoal
import com.example.model.SavedWorkoutPlan
import com.example.model.UserPreferences
import com.example.model.UserProfile
import com.example.model.Workout
import com.example.model.WorkoutHistoryItem
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class NavTab {
    HOME,
    LIBRARY,
    PROGRESS,
    PROFILE
}

enum class PlayerPhase {
    PREPARE,
    EXERCISE,
    REST,
    COMPLETED
}

data class PlayerState(
    val workout: Workout? = null,
    val phase: PlayerPhase = PlayerPhase.PREPARE,
    val currentExerciseIndex: Int = 0,
    val isPaused: Boolean = false,
    val timerSeconds: Int = 3,
    val totalSeconds: Int = 3,
    val repsCompleted: Int = 0,
    val currentSet: Int = 1,
    val totalSets: Int = 3,
    val weightKg: Float = 0f,
    val startTimeMs: Long = 0L
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = InffitRepository(application)
    val firestoreRepo = FirestoreFitnessRepository(application)
    val coach = WorkoutCoach(application)

    val profile: StateFlow<UserProfile> = repository.profile
    val preferences: StateFlow<UserPreferences> = repository.preferences
    val history: StateFlow<List<WorkoutHistoryItem>> = repository.history
    val achievements: StateFlow<List<Achievement>> = repository.achievements
    val customWorkouts: StateFlow<List<Workout>> = repository.customWorkouts

    // Cloud Goals and Saved Multi-Day Plans
    private val _goals = MutableStateFlow<List<FitnessGoal>>(emptyList())
    val goals: StateFlow<List<FitnessGoal>> = _goals.asStateFlow()

    private val _savedPlans = MutableStateFlow<List<SavedWorkoutPlan>>(emptyList())
    val savedPlans: StateFlow<List<SavedWorkoutPlan>> = _savedPlans.asStateFlow()

    // New AI Assistant and Multi-Day Dialog states
    private val _showAiAssistantDialog = MutableStateFlow(false)
    val showAiAssistantDialog: StateFlow<Boolean> = _showAiAssistantDialog.asStateFlow()

    private val _showMultiDayPlanDialog = MutableStateFlow(false)
    val showMultiDayPlanDialog: StateFlow<Boolean> = _showMultiDayPlanDialog.asStateFlow()

    private val _showGoalsDialog = MutableStateFlow(false)
    val showGoalsDialog: StateFlow<Boolean> = _showGoalsDialog.asStateFlow()

    private val _showSavedPlansDialog = MutableStateFlow(false)
    val showSavedPlansDialog: StateFlow<Boolean> = _showSavedPlansDialog.asStateFlow()

    val totalWorkouts: StateFlow<Int> = repository.totalWorkouts
    val totalMinutes: StateFlow<Int> = repository.totalMinutes
    val totalCalories: StateFlow<Int> = repository.totalCalories
    val currentStreak: StateFlow<Int> = repository.currentStreak
    val maxStreak: StateFlow<Int> = repository.maxStreak

    private val _currentTab = MutableStateFlow(NavTab.HOME)
    val currentTab: StateFlow<NavTab> = _currentTab.asStateFlow()

    private val _showOnboarding = MutableStateFlow(repository.isFirstLaunch)
    val showOnboarding: StateFlow<Boolean> = _showOnboarding.asStateFlow()

    // Library filtering
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    // Preview Workout Modal
    private val _previewWorkout = MutableStateFlow<Workout?>(null)
    val previewWorkout: StateFlow<Workout?> = _previewWorkout.asStateFlow()

    // Active Workout Player
    private val _playerState = MutableStateFlow<PlayerState?>(null)
    val playerState: StateFlow<PlayerState?> = _playerState.asStateFlow()

    // Workout Finished Modal
    data class FinishSummary(
        val workout: Workout,
        val durationMinutes: Int,
        val calories: Int,
        val xpGained: Int,
        val unlockedAchievements: List<Achievement>
    )

    private val _finishSummary = MutableStateFlow<FinishSummary?>(null)
    val finishSummary: StateFlow<FinishSummary?> = _finishSummary.asStateFlow()

    private val _showShareDialog = MutableStateFlow(false)
    val showShareDialog: StateFlow<Boolean> = _showShareDialog.asStateFlow()

    private val _showEditProfileDialog = MutableStateFlow(false)
    val showEditProfileDialog: StateFlow<Boolean> = _showEditProfileDialog.asStateFlow()

    private val _showCreateWorkoutDialog = MutableStateFlow(false)
    val showCreateWorkoutDialog: StateFlow<Boolean> = _showCreateWorkoutDialog.asStateFlow()

    // AI Routine Generator State
    private val _showAiRoutineDialog = MutableStateFlow(false)
    val showAiRoutineDialog: StateFlow<Boolean> = _showAiRoutineDialog.asStateFlow()

    private val _isAiGenerating = MutableStateFlow(false)
    val isAiGenerating: StateFlow<Boolean> = _isAiGenerating.asStateFlow()

    private val _generatedAiWorkout = MutableStateFlow<Workout?>(null)
    val generatedAiWorkout: StateFlow<Workout?> = _generatedAiWorkout.asStateFlow()

    private val _progressTimeframeDays = MutableStateFlow(7)
    val progressTimeframeDays: StateFlow<Int> = _progressTimeframeDays.asStateFlow()

    private var timerJob: Job? = null

    // Combined workouts: Default + Custom
    val allWorkouts: StateFlow<List<Workout>> = combine(
        customWorkouts
    ) { (custom) ->
        WorkoutCatalog.defaultWorkouts + custom
    }.stateIn(viewModelScope, SharingStarted.Lazily, WorkoutCatalog.defaultWorkouts)

    fun selectTab(tab: NavTab) {
        _currentTab.value = tab
    }

    fun completeOnboarding(name: String, goal: Int) {
        repository.completeOnboarding(name, goal)
        _showOnboarding.value = false
    }

    fun updateProfile(name: String, goal: Int) {
        repository.updateProfile(name, goal)
        _showEditProfileDialog.value = false
        val uid = Firebase.auth.currentUser?.uid
        if (uid != null) {
            viewModelScope.launch {
                firestoreRepo.saveUserProfile(profile.value.copy(userId = uid))
            }
        }
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
        repository.updateFullProfile(
            name = name,
            age = age,
            fitnessGoal = fitnessGoal,
            experienceLevel = experienceLevel,
            preferredWorkoutType = preferredWorkoutType,
            availableEquipment = availableEquipment,
            workoutFrequency = workoutFrequency,
            dailyGoalMinutes = dailyGoalMinutes
        )
        _showEditProfileDialog.value = false
        val uid = Firebase.auth.currentUser?.uid
        if (uid != null) {
            viewModelScope.launch {
                firestoreRepo.saveUserProfile(profile.value.copy(userId = uid))
            }
        }
    }

    fun deleteAccount(onComplete: () -> Unit) {
        val user = Firebase.auth.currentUser
        val uid = user?.uid
        viewModelScope.launch {
            if (uid != null) {
                firestoreRepo.deleteUserAccountData(uid)
            }
            try {
                user?.delete()
            } catch (_: Exception) { }
            repository.resetAllData()
            onComplete()
        }
    }

    fun toggleTheme() {
        val current = preferences.value.isDarkTheme
        repository.updatePreferences(isDark = !current)
    }

    fun toggleVoice() {
        val current = preferences.value.isVoiceEnabled
        repository.updatePreferences(isVoice = !current)
    }

    fun toggleSound() {
        val current = preferences.value.isSoundEnabled
        repository.updatePreferences(isSound = !current)
    }

    fun toggleVibration() {
        val current = preferences.value.isVibrationEnabled
        repository.updatePreferences(isVib = !current)
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setProgressTimeframe(days: Int) {
        _progressTimeframeDays.value = days
    }

    fun openPreview(workout: Workout) {
        _previewWorkout.value = workout
    }

    fun closePreview() {
        _previewWorkout.value = null
    }

    fun openEditProfile() {
        _showEditProfileDialog.value = true
    }

    fun closeEditProfile() {
        _showEditProfileDialog.value = false
    }

    fun openCreateWorkout() {
        _showCreateWorkoutDialog.value = true
    }

    fun closeCreateWorkout() {
        _showCreateWorkoutDialog.value = false
    }

    fun openShareDialog() {
        _showShareDialog.value = true
    }

    fun closeShareDialog() {
        _showShareDialog.value = false
    }

    fun closeFinishSummary() {
        _finishSummary.value = null
        _currentTab.value = NavTab.HOME
    }

    fun resetAllData() {
        repository.resetAllData()
    }

    // --- AI Assistant Dialog ---
    fun openAiAssistant() {
        _showAiAssistantDialog.value = true
    }

    fun closeAiAssistant() {
        _showAiAssistantDialog.value = false
    }

    // --- Multi-Day Plan Dialog ---
    fun openMultiDayPlanDialog() {
        _showMultiDayPlanDialog.value = true
    }

    fun closeMultiDayPlanDialog() {
        _showMultiDayPlanDialog.value = false
    }

    // --- Goals Dialog ---
    fun openGoalsDialog() {
        _showGoalsDialog.value = true
    }

    fun closeGoalsDialog() {
        _showGoalsDialog.value = false
    }

    fun addGoal(goal: FitnessGoal) {
        val updated = _goals.value + goal
        _goals.value = updated
        val uid = Firebase.auth.currentUser?.uid
        if (uid != null) {
            viewModelScope.launch {
                firestoreRepo.saveGoal(goal.copy(userId = uid))
            }
        }
    }

    fun toggleGoalComplete(goal: FitnessGoal) {
        val updatedGoal = goal.copy(
            isCompleted = !goal.isCompleted,
            currentValue = if (!goal.isCompleted) goal.targetValue else goal.currentValue
        )
        _goals.value = _goals.value.map { if (it.id == goal.id) updatedGoal else it }
        val uid = Firebase.auth.currentUser?.uid
        if (uid != null) {
            viewModelScope.launch {
                firestoreRepo.saveGoal(updatedGoal.copy(userId = uid))
            }
        }
    }

    fun deleteGoal(goalId: String) {
        _goals.value = _goals.value.filter { it.id != goalId }
        val uid = Firebase.auth.currentUser?.uid
        if (uid != null) {
            viewModelScope.launch {
                firestoreRepo.deleteGoal(goalId)
            }
        }
    }

    // --- Saved Plans Dialog ---
    fun openSavedPlansDialog() {
        _showSavedPlansDialog.value = true
    }

    fun closeSavedPlansDialog() {
        _showSavedPlansDialog.value = false
    }

    fun saveWorkoutPlan(plan: SavedWorkoutPlan) {
        _savedPlans.value = _savedPlans.value + plan
        val uid = Firebase.auth.currentUser?.uid
        if (uid != null) {
            viewModelScope.launch {
                firestoreRepo.saveWorkoutPlan(plan.copy(userId = uid))
            }
        }
    }

    fun deleteWorkoutPlan(planId: String) {
        _savedPlans.value = _savedPlans.value.filter { it.id != planId }
        val uid = Firebase.auth.currentUser?.uid
        if (uid != null) {
            viewModelScope.launch {
                firestoreRepo.deleteWorkoutPlan(planId)
            }
        }
    }

    // --- Cloud Sync Hooks ---
    fun syncUserFromCloud(userId: String) {
        viewModelScope.launch {
            val cloudProfile = firestoreRepo.getUserProfile(userId)
            if (cloudProfile != null) {
                repository.updateProfile(
                    name = cloudProfile.name,
                    goal = cloudProfile.dailyGoalMinutes
                )
            } else {
                // Initialize default profile in cloud
                firestoreRepo.saveUserProfile(profile.value.copy(userId = userId))
            }

            // Sync app settings
            val cloudSettings = firestoreRepo.getAppSettings(userId)
            if (cloudSettings != null) {
                repository.updatePreferences(
                    isDark = cloudSettings.isDarkTheme,
                    isVoice = cloudSettings.isVoiceEnabled,
                    isSound = cloudSettings.isSoundEnabled,
                    isVib = cloudSettings.isVibrationEnabled,
                    theme = cloudSettings.selectedTheme
                )
            }

            // Observe cloud goals
            firestoreRepo.observeGoals(userId).collect { cloudGoals ->
                _goals.value = cloudGoals
            }
        }

        viewModelScope.launch {
            // Observe saved plans
            firestoreRepo.observeWorkoutPlans(userId).collect { plans ->
                _savedPlans.value = plans
            }
        }
    }

    fun selectTheme(preset: AppThemePreset) {
        if (profile.value.level >= preset.requiredLevel) {
            repository.updatePreferences(theme = preset)
        }
    }

    fun openAiRoutineDialog() {
        _generatedAiWorkout.value = null
        _showAiRoutineDialog.value = true
    }

    fun closeAiRoutineDialog() {
        _showAiRoutineDialog.value = false
        _generatedAiWorkout.value = null
    }

    fun generateAiRoutine(focus: String, duration: Int, notes: String) {
        viewModelScope.launch {
            _isAiGenerating.value = true
            val workout = AiWorkoutGenerator.generateRoutine(
                athleteName = profile.value.name,
                athleteLevel = profile.value.rankTitle,
                goalFocus = focus,
                durationMinutes = duration,
                userNotes = notes
            )
            _generatedAiWorkout.value = workout
            _isAiGenerating.value = false
        }
    }

    fun saveAiRoutineToLibrary(workout: Workout) {
        repository.addCustomWorkout(workout)
        closeAiRoutineDialog()
    }

    fun startAiRoutineImmediately(workout: Workout) {
        closeAiRoutineDialog()
        startWorkout(workout)
    }

    fun addCustomWorkout(workout: Workout) {
        repository.addCustomWorkout(workout)
        _showCreateWorkoutDialog.value = false
    }

    fun getTodayActiveMinutes(): Int {
        return repository.getTodayActiveMinutes()
    }

    fun getDailyActivityHistory(days: Int): List<DailyActivity> {
        return repository.getDailyActivityHistory(days)
    }

    // --- Workout Player Logic ---

    fun startWorkout(workout: Workout) {
        _previewWorkout.value = null
        timerJob?.cancel()

        _playerState.value = PlayerState(
            workout = workout,
            phase = PlayerPhase.PREPARE,
            currentExerciseIndex = 0,
            isPaused = false,
            timerSeconds = 3,
            totalSeconds = 3,
            repsCompleted = 0,
            startTimeMs = System.currentTimeMillis()
        )

        coach.speak("Get ready", preferences.value.isVoiceEnabled)
        startPreparationCountdown()
    }

    private fun startPreparationCountdown() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            var remaining = 3
            while (remaining > 0) {
                coach.tick(preferences.value.isSoundEnabled)
                _playerState.value = _playerState.value?.copy(timerSeconds = remaining, totalSeconds = 3)
                delay(1000)
                remaining--
            }
            coach.go(preferences.value.isSoundEnabled)
            coach.vibrateShort(preferences.value.isVibrationEnabled)
            startCurrentExercise()
        }
    }

    private fun startCurrentExercise() {
        val state = _playerState.value ?: return
        val workout = state.workout ?: return

        if (state.currentExerciseIndex >= workout.exercises.size) {
            finishWorkout()
            return
        }

        val exercise = workout.exercises[state.currentExerciseIndex]
        coach.speak(exercise.name, preferences.value.isVoiceEnabled)

        if (exercise.type == ExerciseType.TIME) {
            _playerState.value = state.copy(
                phase = PlayerPhase.EXERCISE,
                timerSeconds = exercise.value,
                totalSeconds = exercise.value,
                isPaused = false,
                repsCompleted = 0
            )
            runTimeExerciseCountdown(exercise.value)
        } else {
            _playerState.value = state.copy(
                phase = PlayerPhase.EXERCISE,
                timerSeconds = exercise.value,
                totalSeconds = exercise.value,
                isPaused = false,
                repsCompleted = 0
            )
        }
    }

    private fun runTimeExerciseCountdown(initialSeconds: Int) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            var remaining = initialSeconds
            while (remaining > 0) {
                if (_playerState.value?.isPaused == true) {
                    delay(200)
                    continue
                }

                if (remaining in 1..5) {
                    coach.speak("$remaining", preferences.value.isVoiceEnabled)
                } else if (remaining > 5) {
                    coach.tick(preferences.value.isSoundEnabled)
                }

                _playerState.value = _playerState.value?.copy(timerSeconds = remaining)
                delay(1000)
                remaining--
            }

            // Exercise finished
            coach.finishBeep(preferences.value.isSoundEnabled)
            coach.vibrateLong(preferences.value.isVibrationEnabled)
            startRestInterval()
        }
    }

    fun adjustWeight(deltaKg: Float) {
        val state = _playerState.value ?: return
        val newWeight = (state.weightKg + deltaKg).coerceIn(0f, 150f)
        _playerState.value = state.copy(weightKg = newWeight)
    }

    fun completeRepExercise() {
        val state = _playerState.value ?: return
        if (state.phase != PlayerPhase.EXERCISE) return
        coach.finishBeep(preferences.value.isSoundEnabled)
        coach.vibrateShort(preferences.value.isVibrationEnabled)
        startRestInterval()
    }

    fun nextSetOrComplete() {
        val state = _playerState.value ?: return
        if (state.currentSet < state.totalSets) {
            val nextSet = state.currentSet + 1
            coach.finishBeep(preferences.value.isSoundEnabled)
            coach.vibrateShort(preferences.value.isVibrationEnabled)
            coach.speak("Set ${state.currentSet} complete. Prepare for Set $nextSet", preferences.value.isVoiceEnabled)
            _playerState.value = state.copy(currentSet = nextSet, repsCompleted = 0)
            startSetRestInterval()
        } else {
            completeRepExercise()
        }
    }

    private fun startSetRestInterval() {
        val state = _playerState.value ?: return
        val restDuration = 15
        _playerState.value = state.copy(
            phase = PlayerPhase.REST,
            timerSeconds = restDuration,
            totalSeconds = restDuration,
            isPaused = false
        )
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            var remaining = restDuration
            while (remaining > 0) {
                if (_playerState.value?.isPaused == true) {
                    delay(200)
                    continue
                }
                if (remaining in 1..3) {
                    coach.speak("$remaining", preferences.value.isVoiceEnabled)
                }
                _playerState.value = _playerState.value?.copy(timerSeconds = remaining)
                delay(1000)
                remaining--
            }
            coach.go(preferences.value.isSoundEnabled)
            startCurrentExercise()
        }
    }

    fun incrementReps() {
        val state = _playerState.value ?: return
        val current = state.repsCompleted
        val target = state.timerSeconds
        val updated = current + 1
        _playerState.value = state.copy(repsCompleted = updated)
        coach.tick(preferences.value.isSoundEnabled)
        if (updated >= target) {
            completeRepExercise()
        }
    }

    private fun startRestInterval() {
        val state = _playerState.value ?: return
        val workout = state.workout ?: return
        val nextIndex = state.currentExerciseIndex + 1

        if (nextIndex >= workout.exercises.size) {
            finishWorkout()
            return
        }

        val nextExercise = workout.exercises[nextIndex]
        coach.speak("Rest. Next up: ${nextExercise.name}", preferences.value.isVoiceEnabled)

        val restDuration = 15
        _playerState.value = state.copy(
            phase = PlayerPhase.REST,
            currentExerciseIndex = nextIndex,
            timerSeconds = restDuration,
            totalSeconds = restDuration,
            isPaused = false,
            repsCompleted = 0
        )

        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            var remaining = restDuration
            while (remaining > 0) {
                if (_playerState.value?.isPaused == true) {
                    delay(200)
                    continue
                }

                if (remaining in 1..3) {
                    coach.speak("$remaining", preferences.value.isVoiceEnabled)
                }
                _playerState.value = _playerState.value?.copy(timerSeconds = remaining)
                delay(1000)
                remaining--
            }
            coach.go(preferences.value.isSoundEnabled)
            startCurrentExercise()
        }
    }

    fun skipRest() {
        val state = _playerState.value ?: return
        if (state.phase == PlayerPhase.REST) {
            timerJob?.cancel()
            coach.go(preferences.value.isSoundEnabled)
            startCurrentExercise()
        }
    }

    fun togglePause() {
        val state = _playerState.value ?: return
        val newPaused = !state.isPaused
        _playerState.value = state.copy(isPaused = newPaused)
        if (newPaused) {
            coach.speak("Paused", preferences.value.isVoiceEnabled)
        } else {
            coach.speak("Resuming", preferences.value.isVoiceEnabled)
        }
    }

    fun skipExercise() {
        val state = _playerState.value ?: return
        val workout = state.workout ?: return
        timerJob?.cancel()
        if (state.phase == PlayerPhase.REST) {
            startCurrentExercise()
        } else {
            val nextIndex = state.currentExerciseIndex + 1
            if (nextIndex >= workout.exercises.size) {
                finishWorkout()
            } else {
                _playerState.value = state.copy(currentExerciseIndex = nextIndex)
                startCurrentExercise()
            }
        }
    }

    fun previousExercise() {
        val state = _playerState.value ?: return
        timerJob?.cancel()
        val prevIndex = maxOf(0, state.currentExerciseIndex - 1)
        _playerState.value = state.copy(currentExerciseIndex = prevIndex)
        startCurrentExercise()
    }

    fun quitWorkout() {
        timerJob?.cancel()
        _playerState.value = null
    }

    private fun finishWorkout() {
        val state = _playerState.value ?: return
        val workout = state.workout ?: return
        timerJob?.cancel()

        val elapsedMs = System.currentTimeMillis() - state.startTimeMs
        val elapsedMinutes = maxOf(1, (elapsedMs / 60000).toInt())

        coach.speak("Outstanding job! Workout complete!", preferences.value.isVoiceEnabled)
        coach.finishBeep(preferences.value.isSoundEnabled)
        coach.vibrateLong(preferences.value.isVibrationEnabled)

        val (xpGained, unlocked) = repository.recordCompletedWorkout(workout, elapsedMinutes)
        val calories = maxOf(elapsedMinutes * 7, (workout.calories * elapsedMinutes) / maxOf(1, workout.durationMinutes))

        val uid = Firebase.auth.currentUser?.uid
        if (uid != null) {
            viewModelScope.launch {
                val sessionItem = WorkoutHistoryItem(
                    workoutId = workout.id,
                    workoutTitle = workout.title,
                    dateFormatted = SimpleDateFormat("MMM dd, yyyy", Locale.US).format(Date()),
                    isoDate = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()),
                    timestamp = System.currentTimeMillis(),
                    durationMinutes = elapsedMinutes,
                    caloriesBurned = calories,
                    exercisesCompleted = workout.exercises.size,
                    userId = uid
                )
                firestoreRepo.saveWorkoutSession(sessionItem)
                firestoreRepo.saveUserProfile(profile.value.copy(userId = uid))
            }
        }

        _playerState.value = null
        _finishSummary.value = FinishSummary(
            workout = workout,
            durationMinutes = elapsedMinutes,
            calories = calories,
            xpGained = xpGained,
            unlockedAchievements = unlocked
        )
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        coach.shutdown()
    }
}
