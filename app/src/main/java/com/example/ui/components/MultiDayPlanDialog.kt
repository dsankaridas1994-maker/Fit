package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ai.AiFitnessCoach
import com.example.model.SavedWorkoutPlan
import com.example.model.UserProfile
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

@Composable
fun MultiDayPlanDialog(
    athlete: UserProfile,
    onSavePlan: (SavedWorkoutPlan) -> Unit,
    onDismiss: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    var selectedGoal by remember { mutableStateOf("Hypertrophy & Muscle") }
    var selectedDays by remember { mutableIntStateOf(3) }
    var selectedDuration by remember { mutableIntStateOf(20) }
    var notesInput by remember { mutableStateOf("") }

    var isGenerating by remember { mutableStateOf(false) }
    var generatedPlan by remember { mutableStateOf<SavedWorkoutPlan?>(null) }

    val goals = listOf(
        "Hypertrophy & Muscle",
        "Fat Shred & HIIT",
        "Athletic Conditioning",
        "Core & Six-Pack"
    )

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
                                text = "Multi-Day AI Plan Generator",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Periodized weekly training splits",
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

                if (generatedPlan == null) {
                    // Plan Builder Form
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            Text(
                                text = "TARGET TRAINING GOAL",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                goals.forEach { goal ->
                                    val isSelected = selectedGoal == goal
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(
                                                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                            )
                                            .border(
                                                1.dp,
                                                if (isSelected) MaterialTheme.colorScheme.primary
                                                else Color.Transparent,
                                                RoundedCornerShape(12.dp)
                                            )
                                            .clickable { selectedGoal = goal }
                                            .padding(12.dp)
                                    ) {
                                        Text(
                                            text = goal,
                                            fontSize = 13.sp,
                                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }

                        item {
                            Text(
                                text = "TRAINING DAYS PER WEEK",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(3, 4, 5).forEach { days ->
                                    val isSelected = selectedDays == days
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(
                                                if (isSelected) MaterialTheme.colorScheme.primary
                                                else MaterialTheme.colorScheme.surfaceVariant
                                            )
                                            .clickable { selectedDays = days }
                                            .padding(vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "$days Days",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }

                        item {
                            Text(
                                text = "DAILY SESSION DURATION",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(15, 20, 30).forEach { mins ->
                                    val isSelected = selectedDuration == mins
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(
                                                if (isSelected) MaterialTheme.colorScheme.primary
                                                else MaterialTheme.colorScheme.surfaceVariant
                                            )
                                            .clickable { selectedDuration = mins }
                                            .padding(vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "$mins Mins",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }

                        item {
                            Text(
                                text = "OPTIONAL ATHLETE NOTES / CONSTRAINTS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = notesInput,
                                onValueChange = { notesInput = it },
                                placeholder = { Text("e.g. Include dumbbell press, no box jumps", fontSize = 12.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            isGenerating = true
                            coroutineScope.launch {
                                val plan = AiFitnessCoach.generateMultiDayPlan(
                                    athlete = athlete,
                                    daysPerWeek = selectedDays,
                                    durationMin = selectedDuration,
                                    targetFocus = selectedGoal,
                                    userNotes = notesInput
                                )
                                generatedPlan = plan
                                isGenerating = false
                            }
                        },
                        enabled = !isGenerating,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("generate_multi_day_plan_button")
                    ) {
                        if (isGenerating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generating Science-Backed Split...", fontSize = 14.sp)
                        } else {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("GENERATE PLAN WITH AI ✨", fontSize = 14.sp, fontWeight = FontWeight.Black)
                        }
                    }
                } else {
                    // Generated Plan Review Screen
                    val plan = generatedPlan!!
                    val daysJsonArray = try {
                        JSONArray(plan.planDetailsJson)
                    } catch (_: Exception) {
                        JSONArray()
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = plan.title,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Row(
                                    modifier = Modifier.padding(top = 4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(
                                        text = "📅 ${plan.daysPerWeek} Days/Week",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "⏱️ ${plan.durationMin} Mins/Day",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(daysJsonArray.length()) { index ->
                                val dayObj = daysJsonArray.optJSONObject(index) ?: JSONObject()
                                val dayTitle = dayObj.optString("dayTitle", "Day ${index + 1}")
                                val exercises = dayObj.optJSONArray("exercises") ?: JSONArray()

                                Card(
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = dayTitle,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Black,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        for (j in 0 until exercises.length()) {
                                            val ex = exercises.optJSONObject(j) ?: continue
                                            val name = ex.optString("name", "Exercise")
                                            val sets = ex.optInt("sets", 3)
                                            val reps = ex.optString("repsOrSeconds", "12 reps")
                                            val cue = ex.optString("cue", "")

                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 3.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(text = name, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                                    if (cue.isNotBlank()) {
                                                        Text(text = cue, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                    }
                                                }
                                                Text(
                                                    text = "$sets × $reps",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = MaterialTheme.colorScheme.secondary
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { generatedPlan = null },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Regenerate", color = MaterialTheme.colorScheme.onSurface)
                        }

                        Button(
                            onClick = {
                                onSavePlan(plan)
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.weight(1.4f)
                        ) {
                            Icon(imageVector = Icons.Default.Bookmark, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("SAVE TO CLOUD", fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }
    }
}
