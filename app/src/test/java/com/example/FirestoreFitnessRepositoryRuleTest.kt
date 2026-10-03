package com.example

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.FirestoreFitnessRepository
import com.example.model.FitnessGoal
import com.example.model.UserProfile
import com.example.model.WorkoutHistoryItem
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FirestoreFitnessRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun testAuthenticatedUserCanSaveAndRetrieveProfile() = runTest {
        val userUid = signInTestUser("athlete_test@inffit.com")
        val repo = FirestoreFitnessRepository(firestore)

        val profile = UserProfile(
            userId = userUid,
            name = "Test Athlete",
            age = 25,
            fitnessGoal = "Endurance",
            xp = 320
        )

        val saveResult = repo.saveUserProfile(profile)
        assertTrue("Profile save should succeed for authenticated owner", saveResult)

        val fetched = repo.getUserProfile(userUid)
        assertNotNull(fetched)
        assertEquals("Test Athlete", fetched?.name)
        assertEquals(25, fetched?.age)
        assertEquals(320, fetched?.xp)
        assertEquals(4, fetched?.level) // level = 320/100 + 1 = 4
    }

    @Test
    fun testAuthenticatedUserCanSaveAndObserveWorkoutSession() = runTest {
        val userUid = signInTestUser("workout_user@inffit.com")
        val repo = FirestoreFitnessRepository(firestore)

        val session = WorkoutHistoryItem(
            workoutId = "w_hiit",
            workoutTitle = "HIIT Inferno",
            dateFormatted = "Oct 02, 2026",
            isoDate = "2026-10-02",
            timestamp = System.currentTimeMillis(),
            durationMinutes = 15,
            caloriesBurned = 180,
            exercisesCompleted = 8,
            userId = userUid
        )

        val saveResult = repo.saveWorkoutSession(session)
        assertTrue("Session save should succeed", saveResult)

        val sessions = repo.observeWorkoutSessions(userUid).first()
        assertTrue("Sessions list should not be empty", sessions.isNotEmpty())
        assertEquals("HIIT Inferno", sessions.first().workoutTitle)
        assertEquals(180, sessions.first().caloriesBurned)
    }

    @Test
    fun testAuthenticatedUserCanSaveAndQueryGoals() = runTest {
        val userUid = signInTestUser("goals_user@inffit.com")
        val repo = FirestoreFitnessRepository(firestore)

        val goal = FitnessGoal(
            userId = userUid,
            title = "Complete 10 Workouts",
            targetValue = 10,
            currentValue = 2,
            unit = "workouts",
            isCompleted = false
        )

        val saveResult = repo.saveGoal(goal)
        assertTrue("Goal save should succeed", saveResult)

        val goals = repo.observeGoals(userUid).first()
        assertTrue("Goals should contain saved goal", goals.isNotEmpty())
        assertEquals("Complete 10 Workouts", goals.first().title)
        assertEquals(10, goals.first().targetValue)
    }
}
