package com.example.ai

import com.example.BuildConfig
import com.example.model.Exercise
import com.example.model.ExercisePose
import com.example.model.ExerciseType
import com.example.model.Workout
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

object AiWorkoutGenerator {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun generateRoutine(
        athleteName: String,
        athleteLevel: String,
        goalFocus: String,
        durationMinutes: Int,
        userNotes: String
    ): Workout = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (_: Exception) {
            ""
        }

        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val aiWorkout = callGeminiApi(apiKey, athleteName, athleteLevel, goalFocus, durationMinutes, userNotes)
                if (aiWorkout != null) {
                    return@withContext aiWorkout
                }
            } catch (_: Exception) {
                // Fall back smoothly to algorithmic generator
            }
        }

        // Algorithmic smart fitness routine generator
        generateAlgorithmicRoutine(athleteName, athleteLevel, goalFocus, durationMinutes, userNotes)
    }

    private fun callGeminiApi(
        apiKey: String,
        athleteName: String,
        athleteLevel: String,
        goalFocus: String,
        durationMinutes: Int,
        userNotes: String
    ): Workout? {
        val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"

        val prompt = """
            You are INFFIT AI, an elite athletic fitness coach. Create an intense, personalized, science-backed workout routine for athlete $athleteName (Rank: $athleteLevel).
            Goal Focus: $goalFocus
            Target Duration: $durationMinutes minutes
            Athlete Notes / Constraints: $userNotes
            
            Return ONLY a valid JSON object matching this exact schema:
            {
              "title": "string (powerful motivational workout name, max 25 chars)",
              "description": "string (concise 1-2 sentence athlete brief)",
              "category": "string (choose one: Quick, Beginner, Core, HIIT, Strength)",
              "level": "string (choose one: Beg, Int, Adv)",
              "calories": number (estimated total kcal, around 7-10 kcal per minute),
              "exercises": [
                {
                  "name": "string (e.g. Jumping Jacks, Push-Ups, Power Squats, Plank Hold, etc.)",
                  "type": "TIME" or "REPS",
                  "value": number (seconds between 20-45 if TIME, count between 8-20 if REPS),
                  "instructions": "string (short technical form cue, max 70 chars)",
                  "pose": "JUMPING_JACKS" | "SQUATS" | "PUSH_UPS" | "PLANK" | "CRUNCHES" | "MOUNTAIN_CLIMBERS" | "HIGH_KNEES" | "LUNGES" | "BURPEES"
                }
              ]
            }
        """.trimIndent()

        val jsonRequest = JSONObject().apply {
            val contents = JSONArray().apply {
                val partObj = JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply { put("text", prompt) })
                    })
                }
                put(partObj)
            }
            put("contents", contents)
            put("generationConfig", JSONObject().apply {
                put("responseMimeType", "application/json")
                put("temperature", 0.7)
            })
        }

        val requestBody = jsonRequest.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url(endpoint)
            .post(requestBody)
            .build()

        val response = client.newCall(request).execute()
        if (!response.isSuccessful) return null

        val responseBodyString = response.body?.string() ?: return null
        val root = JSONObject(responseBodyString)
        val candidates = root.optJSONArray("candidates") ?: return null
        val firstCandidate = candidates.optJSONObject(0) ?: return null
        val content = firstCandidate.optJSONObject("content") ?: return null
        val parts = content.optJSONArray("parts") ?: return null
        val text = parts.optJSONObject(0)?.optString("text") ?: return null

        return parseWorkoutJson(text, durationMinutes, goalFocus)
    }

    private fun parseWorkoutJson(rawJson: String, targetDuration: Int, fallbackCategory: String): Workout? {
        return try {
            val cleanJson = rawJson.trim()
                .removePrefix("```json")
                .removePrefix("```")
                .removeSuffix("```")
                .trim()
            val obj = JSONObject(cleanJson)
            val title = obj.optString("title", "AI Power Routine")
            val desc = obj.optString("description", "Custom routine engineered by INFFIT AI.")
            val category = obj.optString("category", if (targetDuration <= 7) "Quick" else "HIIT")
            val level = obj.optString("level", "Int")
            val cal = obj.optInt("calories", targetDuration * 8)

            val exArray = obj.optJSONArray("exercises") ?: return null
            val exercises = mutableListOf<Exercise>()

            for (i in 0 until exArray.length()) {
                val item = exArray.getJSONObject(i)
                val typeStr = item.optString("type", "TIME")
                val type = if (typeStr.equals("REPS", ignoreCase = true)) ExerciseType.REPS else ExerciseType.TIME
                val poseStr = item.optString("pose", "JUMPING_JACKS").uppercase()
                val pose = try {
                    ExercisePose.valueOf(poseStr)
                } catch (_: Exception) {
                    ExercisePose.JUMPING_JACKS
                }

                exercises.add(
                    Exercise(
                        id = "ai_ex_${UUID.randomUUID()}",
                        name = item.optString("name", "High Energy Move"),
                        type = type,
                        value = item.optInt("value", if (type == ExerciseType.TIME) 30 else 12),
                        instructions = item.optString("instructions", "Keep powerful form and breathe rhythmically."),
                        pose = pose
                    )
                )
            }

            if (exercises.isEmpty()) return null

            Workout(
                id = "ai_${UUID.randomUUID()}",
                title = title,
                category = category,
                level = level,
                durationMinutes = targetDuration,
                calories = cal,
                description = desc,
                exercises = exercises
            )
        } catch (_: Exception) {
            null
        }
    }

    fun generateAlgorithmicRoutine(
        athleteName: String,
        athleteLevel: String,
        goalFocus: String,
        durationMinutes: Int,
        userNotes: String
    ): Workout {
        val exerciseCount = maxOf(4, minOf(8, durationMinutes / 2 + 1))
        val exercises = mutableListOf<Exercise>()

        when {
            goalFocus.contains("Core", ignoreCase = true) || goalFocus.contains("Abs", ignoreCase = true) -> {
                exercises.add(Exercise("ai_c1", "Abdominal Crunches", ExerciseType.REPS, 15, "Contract upper abs tightly at peak curl.", ExercisePose.CRUNCHES))
                exercises.add(Exercise("ai_c2", "Mountain Climbers", ExerciseType.TIME, 30, "Drive knees explosively while hips stay locked.", ExercisePose.MOUNTAIN_CLIMBERS))
                exercises.add(Exercise("ai_c3", "Plank Hold", ExerciseType.TIME, 35, "Brace abdominal wall with unwavering posture.", ExercisePose.PLANK))
                exercises.add(Exercise("ai_c4", "High Knees", ExerciseType.TIME, 25, "Rapid rhythmic sprint core engagement.", ExercisePose.HIGH_KNEES))
                if (exerciseCount > 4) {
                    exercises.add(Exercise("ai_c5", "Abdominal Crunches", ExerciseType.REPS, 18, "Slow tempo descent for maximum muscle recruitment.", ExercisePose.CRUNCHES))
                    exercises.add(Exercise("ai_c6", "Plank Hold", ExerciseType.TIME, 40, "Finish with full isometric tension.", ExercisePose.PLANK))
                }
            }
            goalFocus.contains("Strength", ignoreCase = true) || goalFocus.contains("Muscle", ignoreCase = true) -> {
                exercises.add(Exercise("ai_s1", "Push-Ups", ExerciseType.REPS, 12, "Full chest expansion and crisp tricep lockout.", ExercisePose.PUSH_UPS))
                exercises.add(Exercise("ai_s2", "Bodyweight Squats", ExerciseType.REPS, 15, "Drive deeply through your heels, chest tall.", ExercisePose.SQUATS))
                exercises.add(Exercise("ai_s3", "Alternating Lunges", ExerciseType.REPS, 12, "Deep vertical step, drop rear knee to 90 degrees.", ExercisePose.LUNGES))
                exercises.add(Exercise("ai_s4", "Plank Hold", ExerciseType.TIME, 35, "Solid upper-body and core stabilizer brace.", ExercisePose.PLANK))
                if (exerciseCount > 4) {
                    exercises.add(Exercise("ai_s5", "Push-Ups", ExerciseType.REPS, 10, "Focus on explosive pressing drive.", ExercisePose.PUSH_UPS))
                    exercises.add(Exercise("ai_s6", "Power Burpees", ExerciseType.REPS, 8, "Dynamic full-body compound drive.", ExercisePose.BURPEES))
                }
            }
            goalFocus.contains("Legs", ignoreCase = true) || goalFocus.contains("Glutes", ignoreCase = true) -> {
                exercises.add(Exercise("ai_l1", "Bodyweight Squats", ExerciseType.REPS, 16, "Lower hips back and drive through heels.", ExercisePose.SQUATS))
                exercises.add(Exercise("ai_l2", "Alternating Lunges", ExerciseType.REPS, 14, "Long athletic step lowering hips smoothly.", ExercisePose.LUNGES))
                exercises.add(Exercise("ai_l3", "High Knees", ExerciseType.TIME, 30, "Rapid foot cadence pumping glutes and quads.", ExercisePose.HIGH_KNEES))
                exercises.add(Exercise("ai_l4", "Jumping Jacks", ExerciseType.TIME, 30, "Active bounce loading calves and hamstrings.", ExercisePose.JUMPING_JACKS))
                if (exerciseCount > 4) {
                    exercises.add(Exercise("ai_l5", "Bodyweight Squats", ExerciseType.REPS, 14, "Hold bottom 1 second for peak tension.", ExercisePose.SQUATS))
                }
            }
            else -> { // Fat Shred / HIIT / Full Body
                exercises.add(Exercise("ai_h1", "Jumping Jacks", ExerciseType.TIME, 30, "Warm up heart rate with wide arm swings.", ExercisePose.JUMPING_JACKS))
                exercises.add(Exercise("ai_h2", "Power Burpees", ExerciseType.REPS, 8, "Explosive metabolic leap and kick back.", ExercisePose.BURPEES))
                exercises.add(Exercise("ai_h3", "Mountain Climbers", ExerciseType.TIME, 30, "High velocity piston knee drive.", ExercisePose.MOUNTAIN_CLIMBERS))
                exercises.add(Exercise("ai_h4", "Push-Ups", ExerciseType.REPS, 10, "Strong upper-body push with locked core.", ExercisePose.PUSH_UPS))
                exercises.add(Exercise("ai_h5", "Bodyweight Squats", ExerciseType.REPS, 15, "Speed squats with rapid hip extension.", ExercisePose.SQUATS))
                if (exerciseCount > 5) {
                    exercises.add(Exercise("ai_h6", "High Knees", ExerciseType.TIME, 30, "Max effort sprint in place.", ExercisePose.HIGH_KNEES))
                    exercises.add(Exercise("ai_h7", "Plank Hold", ExerciseType.TIME, 35, "Final isometric cooldown brace.", ExercisePose.PLANK))
                }
            }
        }

        val category = when {
            durationMinutes <= 5 -> "Quick"
            goalFocus.contains("Core", ignoreCase = true) -> "Core"
            goalFocus.contains("Strength", ignoreCase = true) -> "Strength"
            else -> "HIIT"
        }

        val title = when {
            goalFocus.contains("Core", ignoreCase = true) -> "AI Core Matrix"
            goalFocus.contains("Strength", ignoreCase = true) -> "AI Titanium Forge"
            goalFocus.contains("Legs", ignoreCase = true) -> "AI Quad Blitz"
            durationMinutes <= 5 -> "AI Rapid Ignition"
            else -> "AI Velocity Shred"
        }

        return Workout(
            id = "ai_${UUID.randomUUID()}",
            title = title,
            category = category,
            level = if (athleteLevel == "Novice") "Beg" else if (athleteLevel == "Titan" || athleteLevel == "Beast" || athleteLevel == "Legend") "Adv" else "Int",
            durationMinutes = durationMinutes,
            calories = durationMinutes * 8,
            description = "AI-tailored routine for $athleteName focusing on $goalFocus with ${exercises.size} precision movements.",
            exercises = exercises
        )
    }
}
