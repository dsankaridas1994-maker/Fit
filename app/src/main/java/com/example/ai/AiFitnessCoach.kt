package com.example.ai

import com.example.BuildConfig
import com.example.model.AiChatMessage
import com.example.model.SavedWorkoutPlan
import com.example.model.UserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID
import java.util.concurrent.TimeUnit

object AiFitnessCoach {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun askCoach(
        userQuestion: String,
        history: List<AiChatMessage>,
        athlete: UserProfile
    ): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (_: Exception) {
            ""
        }

        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val response = callGeminiForChat(apiKey, userQuestion, history, athlete)
                if (!response.isNullOrBlank()) {
                    return@withContext response
                }
            } catch (_: Exception) {
                // Fall back smoothly to algorithmic assistant
            }
        }

        // Smart offline algorithmic response
        generateSmartCoachAnswer(userQuestion, athlete)
    }

    private fun callGeminiForChat(
        apiKey: String,
        question: String,
        history: List<AiChatMessage>,
        athlete: UserProfile
    ): String? {
        val systemPrompt = """
            You are INFFIT AI Coach, an encouraging, elite certified personal trainer and sports scientist.
            Athlete Name: ${athlete.name}, Fitness Level: ${athlete.experienceLevel} (Lvl ${athlete.level}), Goal: ${athlete.fitnessGoal}, Equipment: ${athlete.availableEquipment}.
            
            GUIDELINES:
            1. Answer with concise, science-backed athletic fitness advice (under 120 words).
            2. Explain exercises, proper biomechanics, workout terminology, and beginner modifications.
            3. SAFETY RULE: You are NOT a doctor. For injuries or medical pain, explicitly advise consulting a qualified physical therapist or doctor before training.
            4. Tone: High-energy, motivating, and direct.
        """.trimIndent()

        val jsonRequest = JSONObject().apply {
            val contents = JSONArray().apply {
                // System context
                put(JSONObject().apply {
                    put("role", "user")
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply { put("text", systemPrompt) })
                    })
                })
                put(JSONObject().apply {
                    put("role", "model")
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply { put("text", "Understood. I am INFFIT AI Coach, ready to coach ${athlete.name} with elite training science and safety.") })
                    })
                })
                // Previous turns (last 4)
                history.takeLast(4).forEach { msg ->
                    put(JSONObject().apply {
                        put("role", if (msg.sender == "user") "user" else "model")
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply { put("text", msg.text) })
                        })
                    })
                }
                // Current user question
                put(JSONObject().apply {
                    put("role", "user")
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply { put("text", question) })
                    })
                })
            }
            put("contents", contents)
        }

        val request = Request.Builder()
            .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey")
            .post(jsonRequest.toString().toRequestBody("application/json".toMediaType()))
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return null
            val responseBody = response.body?.string() ?: return null
            val root = JSONObject(responseBody)
            val candidates = root.optJSONArray("candidates") ?: return null
            if (candidates.length() == 0) return null
            val content = candidates.getJSONObject(0).optJSONObject("content") ?: return null
            val parts = content.optJSONArray("parts") ?: return null
            if (parts.length() == 0) return null
            return parts.getJSONObject(0).optString("text")
        }
    }

    private fun generateSmartCoachAnswer(question: String, athlete: UserProfile): String {
        val q = question.lowercase()
        return when {
            q.contains("knee") || q.contains("pain") || q.contains("hurt") || q.contains("injury") -> {
                "⚠️ Safety First: If you are experiencing joint pain or injury, please stop the movement immediately and consult a doctor or physical therapist. As an alternative for knee pressure, swap jumping exercises for low-impact glute bridges or wall sits without deep knee flexion."
            }
            q.contains("protein") || q.contains("diet") || q.contains("food") || q.contains("eat") -> {
                "For your goal of ${athlete.fitnessGoal}, aim for 1.6 to 2.2 grams of protein per kg of body weight daily. Prioritize whole foods: eggs, chicken breast, lentils, Greek yogurt, and drink 3+ liters of water daily to support muscle recovery!"
            }
            q.contains("rest") || q.contains("sore") || q.contains("recovery") -> {
                "Muscle growth occurs during rest, not during workouts! Active recovery days with light mobility, stretching, and 7-9 hours of deep sleep are essential to prevent central nervous system fatigue and prevent plateauing."
            }
            q.contains("push up") || q.contains("pushup") -> {
                "Push-Up Mastery Form Cue: Keep your core braced like an iron plank. Elbows should track back at a 45-degree arrow angle (not flared out at 90 degrees). If standard push-ups are too challenging, perform incline push-ups with hands elevated on a bench or wall!"
            }
            q.contains("abs") || q.contains("belly") || q.contains("six pack") -> {
                "Core Definition Truth: Abs are built in the gym and revealed through nutritional consistency (caloric balance). Combine isometric core holds (Plank, Hollow Body) with progressive hypertrophy exercises (Crunches, Leg Raises) 3x per week."
            }
            else -> {
                "Great question, ${athlete.name}! To maximize results for ${athlete.fitnessGoal}, focus on progressive overload (increasing reps or decreasing rest time), consistent hydration, and keeping your workout streak alive. Let's crush your next session!"
            }
        }
    }

    // Generate multi-day periodized training plan
    suspend fun generateMultiDayPlan(
        athlete: UserProfile,
        daysPerWeek: Int,
        durationMin: Int,
        targetFocus: String,
        userNotes: String
    ): SavedWorkoutPlan = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (_: Exception) {
            ""
        }

        var planJson: String? = null
        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                planJson = callGeminiForMultiDayPlan(apiKey, athlete, daysPerWeek, durationMin, targetFocus, userNotes)
            } catch (_: Exception) {
                // fallback below
            }
        }

        if (planJson.isNullOrBlank()) {
            planJson = buildAlgorithmicMultiDayPlan(athlete, daysPerWeek, durationMin, targetFocus)
        }

        SavedWorkoutPlan(
            id = UUID.randomUUID().toString(),
            userId = athlete.userId,
            title = "$targetFocus ${daysPerWeek}-Day Split",
            goal = targetFocus,
            experienceLevel = athlete.experienceLevel,
            daysPerWeek = daysPerWeek,
            durationMin = durationMin,
            planDetailsJson = planJson,
            createdAt = System.currentTimeMillis()
        )
    }

    private fun callGeminiForMultiDayPlan(
        apiKey: String,
        athlete: UserProfile,
        days: Int,
        duration: Int,
        focus: String,
        notes: String
    ): String? {
        val prompt = """
            Create a structured $days-day weekly workout plan for athlete ${athlete.name} (${athlete.experienceLevel}).
            Goal: $focus
            Daily Session Duration: $duration minutes
            Equipment: ${athlete.availableEquipment}
            Notes: $notes
            
            Return ONLY a valid JSON array of objects representing each workout day:
            [
              {
                "dayNumber": 1,
                "dayTitle": "string (e.g. Upper Body Armor)",
                "focus": "string",
                "exercises": [
                  {
                    "name": "string",
                    "sets": 3,
                    "repsOrSeconds": "12 reps" or "30 sec",
                    "restSec": 30,
                    "cue": "string"
                  }
                ]
              }
            ]
        """.trimIndent()

        val jsonRequest = JSONObject().apply {
            val contents = JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply { put("text", prompt) })
                    })
                })
            }
            put("contents", contents)
            put("generationConfig", JSONObject().apply {
                put("response_mime_type", "application/json")
            })
        }

        val request = Request.Builder()
            .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey")
            .post(jsonRequest.toString().toRequestBody("application/json".toMediaType()))
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return null
            val body = response.body?.string() ?: return null
            val root = JSONObject(body)
            val candidates = root.optJSONArray("candidates") ?: return null
            if (candidates.length() == 0) return null
            val text = candidates.getJSONObject(0).optJSONObject("content")?.optJSONArray("parts")?.getJSONObject(0)?.optString("text")
            return text
        }
    }

    private fun buildAlgorithmicMultiDayPlan(
        athlete: UserProfile,
        days: Int,
        duration: Int,
        focus: String
    ): String {
        val daysList = JSONArray()
        val splitThemes = listOf(
            "Upper Body Hypertrophy" to listOf("Push-Ups", "Dumbbell Press", "Plank Tap", "Tricep Dips"),
            "Lower Body Power" to listOf("Deep Squats", "Reverse Lunges", "Glute Bridges", "Calf Raises"),
            "Core Shred & Endurance" to listOf("Mountain Climbers", "Bicycle Crunches", "Plank Hold", "Leg Flutter Kicks"),
            "HIIT Conditioning" to listOf("Burpees", "High Knees", "Jumping Jacks", "Speed Skaters"),
            "Full Body Circuit" to listOf("Push-Ups", "Squat Jumps", "Plank Shoulder Taps", "High Knees")
        )

        for (i in 1..days) {
            val theme = splitThemes[(i - 1) % splitThemes.size]
            val dayObj = JSONObject().apply {
                put("dayNumber", i)
                put("dayTitle", "Day $i: ${theme.first}")
                put("focus", focus)
                val exArray = JSONArray()
                theme.second.forEach { exName ->
                    exArray.put(JSONObject().apply {
                        put("name", exName)
                        put("sets", 3)
                        put("repsOrSeconds", if (exName.contains("Plank")) "40 sec" else "12-15 reps")
                        put("restSec", 30)
                        put("cue", "Maintain steady cadence and controlled eccentric contraction.")
                    })
                }
                put("exercises", exArray)
            }
            daysList.put(dayObj)
        }
        return daysList.toString()
    }
}
