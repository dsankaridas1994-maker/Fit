package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.Exercise
import com.example.model.ExercisePose
import com.example.model.ExerciseType
import com.example.model.SavedWorkoutPlan
import com.example.model.Workout
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

@Composable
fun SavedPlansDialog(
    savedPlans: List<SavedWorkoutPlan>,
    onStartWorkout: (Workout) -> Unit,
    onDeletePlan: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(18.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Saved Training Plans",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Cloud-synced AI workout splits",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (savedPlans.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No Saved Workout Plans Yet",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Generate a multi-day plan with AI to save here!",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(savedPlans) { plan ->
                            val daysJson = try {
                                JSONArray(plan.planDetailsJson)
                            } catch (_: Exception) {
                                JSONArray()
                            }

                            Card(
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = plan.title,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Black,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            Text(
                                                text = "${plan.daysPerWeek} Days/Week • ${plan.durationMin} Mins • ${plan.experienceLevel}",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        IconButton(
                                            onClick = { onDeletePlan(plan.id) },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete Plan",
                                                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    for (i in 0 until daysJson.length()) {
                                        val dayObj = daysJson.optJSONObject(i) ?: continue
                                        val dayTitle = dayObj.optString("dayTitle", "Day ${i + 1}")
                                        val exercisesArray = dayObj.optJSONArray("exercises") ?: JSONArray()

                                        Card(
                                            shape = RoundedCornerShape(12.dp),
                                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(10.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = dayTitle,
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                    Text(
                                                        text = "${exercisesArray.length()} exercises",
                                                        fontSize = 10.sp,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }

                                                Button(
                                                    onClick = {
                                                        val exercises = mutableListOf<Exercise>()
                                                        for (k in 0 until exercisesArray.length()) {
                                                            val exObj = exercisesArray.optJSONObject(k) ?: continue
                                                            val exName = exObj.optString("name", "Exercise")
                                                            val cue = exObj.optString("cue", "")
                                                            val pose = when {
                                                                exName.contains("Push", true) -> ExercisePose.PUSH_UPS
                                                                exName.contains("Squat", true) -> ExercisePose.SQUATS
                                                                exName.contains("Plank", true) -> ExercisePose.PLANK
                                                                exName.contains("Knee", true) -> ExercisePose.HIGH_KNEES
                                                                exName.contains("Burpee", true) -> ExercisePose.BURPEES
                                                                exName.contains("Climber", true) -> ExercisePose.MOUNTAIN_CLIMBERS
                                                                exName.contains("Crunch", true) -> ExercisePose.CRUNCHES
                                                                else -> ExercisePose.JUMPING_JACKS
                                                            }
                                                            exercises.add(
                                                                Exercise(
                                                                    id = UUID.randomUUID().toString(),
                                                                    name = exName,
                                                                    type = ExerciseType.TIME,
                                                                    value = 35,
                                                                    instructions = cue.ifBlank { "Execute with proper athletic form." },
                                                                    pose = pose
                                                                )
                                                            )
                                                        }

                                                        val workout = Workout(
                                                            id = UUID.randomUUID().toString(),
                                                            title = dayTitle,
                                                            category = "Strength",
                                                            level = "Int",
                                                            durationMinutes = plan.durationMin,
                                                            calories = plan.durationMin * 8,
                                                            description = "From plan: ${plan.title}",
                                                            exercises = exercises
                                                        )
                                                        onStartWorkout(workout)
                                                        onDismiss()
                                                    },
                                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                                    shape = RoundedCornerShape(10.dp),
                                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                                ) {
                                                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(14.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("START", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
