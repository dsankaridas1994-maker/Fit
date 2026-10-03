package com.example.data

import com.example.model.Achievement
import com.example.model.Exercise
import com.example.model.ExercisePose
import com.example.model.ExerciseType
import com.example.model.Workout

object WorkoutCatalog {

    val defaultExercises = listOf(
        Exercise(
            id = "ex_jj",
            name = "Jumping Jacks",
            type = ExerciseType.TIME,
            value = 30,
            instructions = "Jump with feet apart while raising arms overhead. Keep rhythm smooth.",
            pose = ExercisePose.JUMPING_JACKS,
            targetMuscle = "Cardio & Calves",
            secondaryMuscles = "Shoulders & Core",
            equipment = "Bodyweight",
            difficulty = "Beginner",
            commonMistakes = "Landing heavy on heels instead of soft on balls of feet.",
            alternatives = "Step jacks without jumping or high knees march."
        ),
        Exercise(
            id = "ex_squats",
            name = "Bodyweight Squats",
            type = ExerciseType.REPS,
            value = 12,
            instructions = "Lower your hips back and down like sitting in a chair. Keep chest upright.",
            pose = ExercisePose.SQUATS,
            targetMuscle = "Quadriceps & Glutes",
            secondaryMuscles = "Hamstrings & Lower Back",
            equipment = "Bodyweight",
            difficulty = "Beginner",
            commonMistakes = "Knees caving inward or heels lifting off the ground.",
            alternatives = "Box squats or wall sit hold."
        ),
        Exercise(
            id = "ex_pushups",
            name = "Push-Ups",
            type = ExerciseType.REPS,
            value = 10,
            instructions = "Keep a straight plank line from head to heels. Lower chest to floor and push up.",
            pose = ExercisePose.PUSH_UPS,
            targetMuscle = "Chest & Triceps",
            secondaryMuscles = "Anterior Deltoids & Core",
            equipment = "Bodyweight",
            difficulty = "Intermediate",
            commonMistakes = "Flaring elbows to 90 degrees or letting hips sag.",
            alternatives = "Incline push-ups or knee-supported push-ups."
        ),
        Exercise(
            id = "ex_plank",
            name = "Plank Hold",
            type = ExerciseType.TIME,
            value = 30,
            instructions = "Engage your core, glutes, and shoulders. Maintain a perfectly flat back.",
            pose = ExercisePose.PLANK,
            targetMuscle = "Core & Transverse Abdominis",
            secondaryMuscles = "Shoulders & Glutes",
            equipment = "Bodyweight",
            difficulty = "Beginner",
            commonMistakes = "Piking hips into the air or letting lumbar spine collapse.",
            alternatives = "Knee plank hold or bird-dog isometric."
        ),
        Exercise(
            id = "ex_crunches",
            name = "Abdominal Crunches",
            type = ExerciseType.REPS,
            value = 15,
            instructions = "Lie on your back, knees bent. Curl shoulders toward pelvis squeezing abs.",
            pose = ExercisePose.CRUNCHES,
            targetMuscle = "Upper & Mid Abs",
            secondaryMuscles = "Obliques",
            equipment = "Bodyweight",
            difficulty = "Beginner",
            commonMistakes = "Yanking neck with hands instead of contracting abs.",
            alternatives = "Dead bugs or hollow body hold."
        ),
        Exercise(
            id = "ex_mountain_climbers",
            name = "Mountain Climbers",
            type = ExerciseType.TIME,
            value = 30,
            instructions = "In push-up stance, alternate driving knees rapidly toward your chest.",
            pose = ExercisePose.MOUNTAIN_CLIMBERS,
            targetMuscle = "Cardio & Core",
            secondaryMuscles = "Hip Flexors & Shoulders",
            equipment = "Bodyweight",
            difficulty = "Intermediate",
            commonMistakes = "Bouncing hips up and down instead of steady plank drive.",
            alternatives = "Slow controlled cross-body knee tucks."
        ),
        Exercise(
            id = "ex_high_knees",
            name = "High Knees",
            type = ExerciseType.TIME,
            value = 30,
            instructions = "Run in place driving knees up to hip height. Pump your arms vigorously.",
            pose = ExercisePose.HIGH_KNEES,
            targetMuscle = "Cardio & Quads",
            secondaryMuscles = "Calves & Hip Flexors",
            equipment = "Bodyweight",
            difficulty = "Beginner",
            commonMistakes = "Leaning back excessively instead of maintaining slight forward lean.",
            alternatives = "Marching high knees in place."
        ),
        Exercise(
            id = "ex_lunges",
            name = "Alternating Lunges",
            type = ExerciseType.REPS,
            value = 12,
            instructions = "Step forward lowering front thigh parallel to floor. Step back and switch legs.",
            pose = ExercisePose.LUNGES,
            targetMuscle = "Quadriceps & Glutes",
            secondaryMuscles = "Hamstrings & Balance Stabilizers",
            equipment = "Bodyweight",
            difficulty = "Intermediate",
            commonMistakes = "Front knee drifting far past toes or slamming back knee on floor.",
            alternatives = "Reverse lunges or static split squats."
        ),
        Exercise(
            id = "ex_burpees",
            name = "Power Burpees",
            type = ExerciseType.REPS,
            value = 8,
            instructions = "Drop into squat, kick feet into push-up, return feet forward, and leap upwards.",
            pose = ExercisePose.BURPEES,
            targetMuscle = "Full Body Power & Cardio",
            secondaryMuscles = "Chest, Legs, Core & Triceps",
            equipment = "Bodyweight",
            difficulty = "Advanced",
            commonMistakes = "Collapsing lower back when kicking feet out.",
            alternatives = "Step-back burpees without the push-up or jump."
        )
    )

    val defaultWorkouts = listOf(
        Workout(
            id = "q_5",
            title = "Morning Ignition",
            category = "Quick",
            level = "Beg",
            durationMinutes = 5,
            calories = 35,
            description = "Wake up your metabolic engine with fast high-energy compound movements.",
            exercises = listOf(
                Exercise("mi_1", "Jumping Jacks", ExerciseType.TIME, 30, "Jump with rhythm and swing arms overhead.", ExercisePose.JUMPING_JACKS),
                Exercise("mi_2", "Bodyweight Squats", ExerciseType.REPS, 12, "Lower hips down, drive up through heels.", ExercisePose.SQUATS),
                Exercise("mi_3", "Mountain Climbers", ExerciseType.TIME, 25, "Drive knees rapidly toward your chest.", ExercisePose.MOUNTAIN_CLIMBERS),
                Exercise("mi_4", "Plank Hold", ExerciseType.TIME, 30, "Engage core and glutes firmly.", ExercisePose.PLANK),
                Exercise("mi_5", "High Knees", ExerciseType.TIME, 25, "Pump arms and drive knees high.", ExercisePose.HIGH_KNEES)
            )
        ),
        Workout(
            id = "f_b",
            title = "Full Body Basics",
            category = "Beginner",
            level = "Beg",
            durationMinutes = 10,
            calories = 75,
            description = "Complete full-body endurance and strength workout targeting major muscle groups.",
            exercises = listOf(
                Exercise("fb_1", "Jumping Jacks", ExerciseType.TIME, 30, "Warm up heart and shoulders.", ExercisePose.JUMPING_JACKS),
                Exercise("fb_2", "Push-Ups", ExerciseType.REPS, 10, "Controlled descent, strong lockout.", ExercisePose.PUSH_UPS),
                Exercise("fb_3", "Bodyweight Squats", ExerciseType.REPS, 15, "Full depth squat, keep chest proud.", ExercisePose.SQUATS),
                Exercise("fb_4", "Abdominal Crunches", ExerciseType.REPS, 15, "Contract upper abs tightly at peak.", ExercisePose.CRUNCHES),
                Exercise("fb_5", "Alternating Lunges", ExerciseType.REPS, 12, "Step deep, keep torso vertical.", ExercisePose.LUNGES),
                Exercise("fb_6", "Plank Hold", ExerciseType.TIME, 35, "Lock in straight spine and breathe.", ExercisePose.PLANK)
            )
        ),
        Workout(
            id = "c_c",
            title = "Core Crusher",
            category = "Core",
            level = "Int",
            durationMinutes = 8,
            calories = 65,
            description = "Sculpt your midsection with high-tension abdominal and stabilizer work.",
            exercises = listOf(
                Exercise("cc_1", "Abdominal Crunches", ExerciseType.REPS, 20, "Contract abs with controlled tempo.", ExercisePose.CRUNCHES),
                Exercise("cc_2", "Mountain Climbers", ExerciseType.TIME, 30, "Keep hips level while alternating knees.", ExercisePose.MOUNTAIN_CLIMBERS),
                Exercise("cc_3", "Plank Hold", ExerciseType.TIME, 40, "Tighten core like absorbing a punch.", ExercisePose.PLANK),
                Exercise("cc_4", "Abdominal Crunches", ExerciseType.REPS, 15, "Focus on deep abdominal burn.", ExercisePose.CRUNCHES),
                Exercise("cc_5", "High Knees", ExerciseType.TIME, 30, "Rapid explosive core engagement.", ExercisePose.HIGH_KNEES)
            )
        ),
        Workout(
            id = "h_t",
            title = "HIIT Torch",
            category = "HIIT",
            level = "Adv",
            durationMinutes = 12,
            calories = 120,
            description = "Maximum calorie afterburn with high-intensity interval bursts.",
            exercises = listOf(
                Exercise("ht_1", "Power Burpees", ExerciseType.REPS, 10, "Explosive jump from deep push-up position.", ExercisePose.BURPEES),
                Exercise("ht_2", "High Knees", ExerciseType.TIME, 35, "Sprint intensity knees up to chest.", ExercisePose.HIGH_KNEES),
                Exercise("ht_3", "Push-Ups", ExerciseType.REPS, 12, "Rapid solid chest repetitions.", ExercisePose.PUSH_UPS),
                Exercise("ht_4", "Mountain Climbers", ExerciseType.TIME, 35, "Piston-like leg drive in plank.", ExercisePose.MOUNTAIN_CLIMBERS),
                Exercise("ht_5", "Bodyweight Squats", ExerciseType.REPS, 15, "Speed squats with full lockout.", ExercisePose.SQUATS),
                Exercise("ht_6", "Plank Hold", ExerciseType.TIME, 45, "Finish strong with unwavering posture.", ExercisePose.PLANK)
            )
        ),
        Workout(
            id = "u_a",
            title = "Upper Body Armor",
            category = "Strength",
            level = "Int",
            durationMinutes = 10,
            calories = 80,
            description = "Build resilient chest, arms, shoulders, and back strength.",
            exercises = listOf(
                Exercise("ua_1", "Push-Ups", ExerciseType.REPS, 12, "Full chest expansion at bottom.", ExercisePose.PUSH_UPS),
                Exercise("ua_2", "Plank Hold", ExerciseType.TIME, 35, "Shoulder stability and core brace.", ExercisePose.PLANK),
                Exercise("ua_3", "Mountain Climbers", ExerciseType.TIME, 30, "Shoulder endurance under tension.", ExercisePose.MOUNTAIN_CLIMBERS),
                Exercise("ua_4", "Push-Ups", ExerciseType.REPS, 10, "Focus on tricep and pectoral drive.", ExercisePose.PUSH_UPS),
                Exercise("ua_5", "Power Burpees", ExerciseType.REPS, 8, "Dynamic upper body push and rebound.", ExercisePose.BURPEES)
            )
        ),
        Workout(
            id = "l_p",
            title = "Legs & Glutes Power",
            category = "Strength",
            level = "Int",
            durationMinutes = 10,
            calories = 85,
            description = "Fortify lower body power, quad endurance, and glute activation.",
            exercises = listOf(
                Exercise("lp_1", "Bodyweight Squats", ExerciseType.REPS, 16, "Deep squats targeting quads & glutes.", ExercisePose.SQUATS),
                Exercise("lp_2", "Alternating Lunges", ExerciseType.REPS, 14, "Step forward steady, drop rear knee.", ExercisePose.LUNGES),
                Exercise("lp_3", "High Knees", ExerciseType.TIME, 30, "Explosive foot turnover and cadence.", ExercisePose.HIGH_KNEES),
                Exercise("lp_4", "Bodyweight Squats", ExerciseType.REPS, 12, "Slow 3-second descent for max burn.", ExercisePose.SQUATS),
                Exercise("lp_5", "Jumping Jacks", ExerciseType.TIME, 30, "Calf bounce and metabolic cool down.", ExercisePose.JUMPING_JACKS)
            )
        )
    )

    val defaultAchievements = listOf(
        Achievement("w1", "First Blood", "Complete your first workout", "🏆"),
        Achievement("w5", "Getting Serious", "Complete 5 total workouts", "⚡"),
        Achievement("w10", "Unstoppable", "Complete 10 total workouts", "🔥"),
        Achievement("s3", "Three's A Charm", "Achieve a 3-day active streak", "🌟"),
        Achievement("s7", "Week Warrior", "Achieve a 7-day streak", "🛡️"),
        Achievement("c500", "Firestarter", "Burn over 500 total calories", "💥"),
        Achievement("m60", "Hour of Power", "Log 60 minutes of active workouts", "⏱️"),
        Achievement("lvl3", "Warrior Born", "Reach Level 3 rank", "👑")
    )
}
