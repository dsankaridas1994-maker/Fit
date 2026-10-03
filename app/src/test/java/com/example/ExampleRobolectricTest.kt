package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.ai.AiWorkoutGenerator
import com.example.data.InffitRepository
import com.example.data.WorkoutCatalog
import com.example.model.AppThemePreset
import com.example.model.UserProfile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("INFFIT", appName)
    }

    @Test
    fun `default workouts catalog is populated`() {
        val workouts = WorkoutCatalog.defaultWorkouts
        assertTrue(workouts.isNotEmpty())
        val morning = workouts.find { it.id == "q_5" }
        assertNotNull(morning)
        assertEquals("Morning Ignition", morning?.title)
        assertTrue((morning?.exercises?.size ?: 0) >= 3)
    }

    @Test
    fun `user profile levels calculate correctly`() {
        val novice = UserProfile(xp = 40)
        assertEquals(1, novice.level)
        assertEquals("Novice", novice.rankTitle)

        val warrior = UserProfile(xp = 250)
        assertEquals(3, warrior.level)
        assertEquals("Warrior", warrior.rankTitle)
    }

    @Test
    fun `repository updates onboarding and preferences`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repository = InffitRepository(context)

        repository.completeOnboarding("Thunder", 25)
        assertEquals("Thunder", repository.profile.value.name)
        assertEquals(25, repository.profile.value.dailyGoalMinutes)

        repository.updatePreferences(isVoice = false, theme = AppThemePreset.CYBER_NEON)
        assertEquals(false, repository.preferences.value.isVoiceEnabled)
        assertEquals(AppThemePreset.CYBER_NEON, repository.preferences.value.selectedTheme)
    }

    @Test
    fun `ai routine generator builds valid tailored routines`() {
        val routine = AiWorkoutGenerator.generateAlgorithmicRoutine(
            athleteName = "Rex",
            athleteLevel = "Warrior",
            goalFocus = "Six-Pack & Core",
            durationMinutes = 10,
            userNotes = "Focus on lower abs"
        )
        assertNotNull(routine)
        assertTrue(routine.title.isNotBlank())
        assertEquals(10, routine.durationMinutes)
        assertTrue(routine.exercises.isNotEmpty())
    }

    @Test
    fun `theme presets unlock levels are configured`() {
        assertEquals(1, AppThemePreset.ONYX_FLAME.requiredLevel)
        assertEquals(2, AppThemePreset.CYBER_NEON.requiredLevel)
        assertEquals(3, AppThemePreset.ELECTRIC_VOLT.requiredLevel)
        assertEquals(5, AppThemePreset.SOLAR_SPARTAN.requiredLevel)
        assertEquals(7, AppThemePreset.CRIMSON_FURY.requiredLevel)
        assertEquals(10, AppThemePreset.ASTRAL_VOID.requiredLevel)
    }
}
