package com.example.data

import android.content.Context
import com.example.R
import com.example.model.Achievement
import com.example.model.FitnessGoal
import com.example.model.SavedWorkoutPlan
import com.example.model.UserPreferences
import com.example.model.UserProfile
import com.example.model.WorkoutHistoryItem
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FirestoreFitnessRepository(
    private val db: FirebaseFirestore
) {
    // Primary constructor for production using the custom named database ID from firebase_applet_config.xml
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth = Firebase.auth

    fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    // --- User Profile ---
    suspend fun getUserProfile(userId: String): UserProfile? {
        val path = "userProfiles/$userId"
        return try {
            val doc = db.collection("userProfiles").document(userId).get().await()
            if (doc.exists()) {
                UserProfile(
                    userId = userId,
                    name = doc.getString("name") ?: "Athlete",
                    age = (doc.getLong("age") ?: 24L).toInt(),
                    fitnessGoal = doc.getString("fitnessGoal") ?: "Build Muscle & Burn Fat",
                    experienceLevel = doc.getString("experienceLevel") ?: "Intermediate",
                    preferredWorkoutType = doc.getString("preferredWorkoutType") ?: "HIIT & Strength",
                    availableEquipment = doc.getString("availableEquipment") ?: "Bodyweight / Dumbbells",
                    workoutFrequency = (doc.getLong("workoutFrequency") ?: 4L).toInt(),
                    dailyGoalMinutes = (doc.getLong("dailyGoalMinutes") ?: 20L).toInt(),
                    xp = (doc.getLong("xp") ?: 0L).toInt()
                )
            } else {
                null
            }
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, path)
            null
        }
    }

    suspend fun saveUserProfile(profile: UserProfile): Boolean {
        val uid = requireUserId()
        val path = "userProfiles/$uid"
        return try {
            val data = mapOf(
                "userId" to uid,
                "name" to profile.name,
                "age" to profile.age,
                "fitnessGoal" to profile.fitnessGoal,
                "experienceLevel" to profile.experienceLevel,
                "preferredWorkoutType" to profile.preferredWorkoutType,
                "availableEquipment" to profile.availableEquipment,
                "workoutFrequency" to profile.workoutFrequency,
                "dailyGoalMinutes" to profile.dailyGoalMinutes,
                "level" to profile.level,
                "xp" to profile.xp,
                "rankTitle" to profile.rankTitle,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            db.collection("userProfiles").document(uid).set(data, SetOptions.merge()).await()
            true
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            false
        }
    }

    // --- Workout Sessions (Cloud History) ---
    fun observeWorkoutSessions(userId: String): Flow<List<WorkoutHistoryItem>> = flow {
        val path = "workoutSessions"
        emitAll(
            db.collection("workoutSessions")
                .whereEqualTo("userId", userId)
                .snapshots()
                .map { snapshot ->
                    snapshot.documents.mapNotNull { doc ->
                        try {
                            val durationSec = (doc.getLong("durationSeconds") ?: 0L).toInt()
                            WorkoutHistoryItem(
                                id = doc.id,
                                workoutId = doc.getString("workoutId") ?: "",
                                workoutTitle = doc.getString("workoutTitle") ?: "Workout",
                                dateFormatted = doc.getString("dateFormatted") ?: "",
                                isoDate = doc.getString("isoDate") ?: "",
                                timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis(),
                                durationMinutes = if (durationSec > 0) (durationSec + 59) / 60 else (doc.getLong("durationMinutes") ?: 0L).toInt(),
                                caloriesBurned = (doc.getLong("caloriesBurned") ?: 0L).toInt(),
                                exercisesCompleted = (doc.getLong("exercisesCompleted") ?: 0L).toInt(),
                                userId = userId
                            )
                        } catch (_: Exception) {
                            null
                        }
                    }.sortedByDescending { it.timestamp }
                }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                    throw error
                }
        )
    }

    suspend fun saveWorkoutSession(session: WorkoutHistoryItem): Boolean {
        val uid = requireUserId()
        val path = "workoutSessions"
        return try {
            val data = mapOf(
                "userId" to uid,
                "workoutId" to session.workoutId,
                "workoutTitle" to session.workoutTitle,
                "dateFormatted" to session.dateFormatted,
                "isoDate" to session.isoDate,
                "timestamp" to session.timestamp,
                "durationMinutes" to session.durationMinutes,
                "durationSeconds" to session.durationMinutes * 60,
                "caloriesBurned" to session.caloriesBurned,
                "exercisesCompleted" to session.exercisesCompleted,
                "completedAt" to FieldValue.serverTimestamp()
            )
            db.collection("workoutSessions").add(data).await()
            true
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            false
        }
    }

    // --- Fitness Goals ---
    fun observeGoals(userId: String): Flow<List<FitnessGoal>> = flow {
        val path = "goals"
        emitAll(
            db.collection("goals")
                .whereEqualTo("userId", userId)
                .snapshots()
                .map { snapshot ->
                    snapshot.documents.mapNotNull { doc ->
                        try {
                            FitnessGoal(
                                id = doc.id,
                                userId = userId,
                                title = doc.getString("title") ?: "Goal",
                                targetValue = (doc.getLong("targetValue") ?: 5L).toInt(),
                                currentValue = (doc.getLong("currentValue") ?: 0L).toInt(),
                                unit = doc.getString("unit") ?: "workouts",
                                isCompleted = doc.getBoolean("isCompleted") ?: false,
                                createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()
                            )
                        } catch (_: Exception) {
                            null
                        }
                    }
                }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                    throw error
                }
        )
    }

    suspend fun saveGoal(goal: FitnessGoal): Boolean {
        val uid = requireUserId()
        val path = "goals"
        return try {
            val data = mapOf(
                "userId" to uid,
                "title" to goal.title,
                "targetValue" to goal.targetValue,
                "currentValue" to goal.currentValue,
                "unit" to goal.unit,
                "isCompleted" to goal.isCompleted,
                "createdAt" to FieldValue.serverTimestamp()
            )
            if (goal.id.isNotBlank()) {
                db.collection("goals").document(goal.id).set(data, SetOptions.merge()).await()
            } else {
                db.collection("goals").add(data).await()
            }
            true
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            false
        }
    }

    suspend fun deleteGoal(goalId: String): Boolean {
        val uid = requireUserId()
        val path = "goals/$goalId"
        return try {
            db.collection("goals").document(goalId).delete().await()
            true
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, path)
            false
        }
    }

    // --- Saved Multi-Day AI Workout Plans ---
    fun observeWorkoutPlans(userId: String): Flow<List<SavedWorkoutPlan>> = flow {
        val path = "workoutPlans"
        emitAll(
            db.collection("workoutPlans")
                .whereEqualTo("userId", userId)
                .snapshots()
                .map { snapshot ->
                    snapshot.documents.mapNotNull { doc ->
                        try {
                            SavedWorkoutPlan(
                                id = doc.id,
                                userId = userId,
                                title = doc.getString("title") ?: "Workout Plan",
                                goal = doc.getString("goal") ?: "",
                                experienceLevel = doc.getString("experienceLevel") ?: "",
                                daysPerWeek = (doc.getLong("daysPerWeek") ?: 3L).toInt(),
                                durationMin = (doc.getLong("durationMin") ?: 15L).toInt(),
                                planDetailsJson = doc.getString("planJson") ?: "",
                                createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()
                            )
                        } catch (_: Exception) {
                            null
                        }
                    }
                }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                    throw error
                }
        )
    }

    suspend fun saveWorkoutPlan(plan: SavedWorkoutPlan): Boolean {
        val uid = requireUserId()
        val path = "workoutPlans"
        return try {
            val data = mapOf(
                "userId" to uid,
                "title" to plan.title,
                "goal" to plan.goal,
                "experienceLevel" to plan.experienceLevel,
                "daysPerWeek" to plan.daysPerWeek,
                "durationMin" to plan.durationMin,
                "planJson" to plan.planDetailsJson,
                "createdAt" to FieldValue.serverTimestamp()
            )
            if (plan.id.isNotBlank()) {
                db.collection("workoutPlans").document(plan.id).set(data, SetOptions.merge()).await()
            } else {
                db.collection("workoutPlans").add(data).await()
            }
            true
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            false
        }
    }

    suspend fun deleteWorkoutPlan(planId: String): Boolean {
        val path = "workoutPlans/$planId"
        return try {
            db.collection("workoutPlans").document(planId).delete().await()
            true
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, path)
            false
        }
    }

    // --- App Settings (Cloud Sync) ---
    suspend fun getAppSettings(userId: String): UserPreferences? {
        val path = "appSettings/$userId"
        return try {
            val doc = db.collection("appSettings").document(userId).get().await()
            if (doc.exists()) {
                val themeName = doc.getString("selectedTheme") ?: "ONYX_FLAME"
                val themePreset = try {
                    com.example.model.AppThemePreset.valueOf(themeName)
                } catch (_: Exception) {
                    com.example.model.AppThemePreset.ONYX_FLAME
                }
                UserPreferences(
                    userId = userId,
                    isDarkTheme = doc.getBoolean("isDarkTheme") ?: true,
                    isVoiceEnabled = doc.getBoolean("isVoiceEnabled") ?: true,
                    isSoundEnabled = doc.getBoolean("isSoundEnabled") ?: true,
                    isVibrationEnabled = doc.getBoolean("isVibrationEnabled") ?: true,
                    selectedTheme = themePreset
                )
            } else {
                null
            }
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, path)
            null
        }
    }

    suspend fun saveUserAccount(userId: String, email: String, displayName: String): Boolean {
        val path = "users/$userId"
        return try {
            val data = mapOf(
                "userId" to userId,
                "email" to email,
                "displayName" to displayName,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            db.collection("users").document(userId).set(data, SetOptions.merge()).await()
            true
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            false
        }
    }

    suspend fun deleteUserAccountData(userId: String): Boolean {
        return try {
            // Delete user doc
            db.collection("users").document(userId).delete().await()
            // Delete userProfile doc
            db.collection("userProfiles").document(userId).delete().await()
            // Delete appSettings doc
            db.collection("appSettings").document(userId).delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun saveAppSettings(preferences: UserPreferences): Boolean {
        val uid = requireUserId()
        val path = "appSettings/$uid"
        return try {
            val data = mapOf(
                "userId" to uid,
                "isDarkTheme" to preferences.isDarkTheme,
                "isVoiceEnabled" to preferences.isVoiceEnabled,
                "isSoundEnabled" to preferences.isSoundEnabled,
                "isVibrationEnabled" to preferences.isVibrationEnabled,
                "selectedTheme" to preferences.selectedTheme.name,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            db.collection("appSettings").document(uid).set(data, SetOptions.merge()).await()
            true
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            false
        }
    }
}
