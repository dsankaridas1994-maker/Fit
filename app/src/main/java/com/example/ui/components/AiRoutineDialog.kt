package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.ExerciseType
import com.example.model.Workout
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.OrangeAccent

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AiRoutineDialog(
    athleteName: String,
    athleteLevel: String,
    isGenerating: Boolean,
    generatedWorkout: Workout?,
    onGenerate: (focus: String, duration: Int, notes: String) -> Unit,
    onStartWorkout: (Workout) -> Unit,
    onSaveToLibrary: (Workout) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedFocus by remember { mutableStateOf("Fat Shred & HIIT") }
    var selectedDuration by remember { mutableStateOf(10) }
    var notesInput by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    val focuses = listOf(
        "Fat Shred & HIIT",
        "Six-Pack & Core",
        "Full-Body Muscle",
        "Upper Body Armor",
        "Legs & Glutes Power"
    )
    val durations = listOf(5, 10, 15, 20)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(decorFitsSystemWindows = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .navigationBarsPadding()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(
                    1.5.dp,
                    Brush.horizontalGradient(listOf(OrangeAccent, AmberAccent))
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ai_routine_dialog")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(scrollState)
                        .padding(22.dp)
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
                                    .background(
                                        Brush.linearGradient(listOf(OrangeAccent, AmberAccent))
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "AI Routine Coach",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Custom calibrated for $athleteName",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OrangeAccent
                                )
                            }
                        }

                        IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (isGenerating) {
                        // Loading State
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(
                                    color = OrangeAccent,
                                    modifier = Modifier.size(52.dp),
                                    strokeWidth = 5.dp
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "AI Coach is crafting your routine...",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Analyzing $selectedFocus for $selectedDuration mins",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    } else if (generatedWorkout != null) {
                        // Generated Workout Preview State
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .background(OrangeAccent.copy(alpha = 0.1f))
                                .border(1.dp, OrangeAccent.copy(alpha = 0.35f), RoundedCornerShape(18.dp))
                                .padding(16.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = generatedWorkout.title,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(EmeraldSuccess)
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = "AI GENERATED",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color.Black
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Timer, contentDescription = null, tint = OrangeAccent, modifier = Modifier.size(13.dp))
                                    Text(text = " ${generatedWorkout.durationMinutes}m   ", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Icon(imageVector = Icons.Default.LocalFireDepartment, contentDescription = null, tint = AmberAccent, modifier = Modifier.size(13.dp))
                                    Text(text = " ${generatedWorkout.calories} kcal   •  ${generatedWorkout.level}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }

                                Text(
                                    text = generatedWorkout.description,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 6.dp)
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = "CIRCUIT (${generatedWorkout.exercises.size} EXERCISES)",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = OrangeAccent,
                                    letterSpacing = 1.sp
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    generatedWorkout.exercises.forEachIndexed { idx, ex ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                                .padding(horizontal = 10.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "${idx + 1}. ${ex.name}",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Text(
                                                text = if (ex.type == ExerciseType.TIME) "${ex.value}s" else "x${ex.value}",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = OrangeAccent
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Action Buttons
                        Button(
                            onClick = { onStartWorkout(generatedWorkout) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("start_ai_routine_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(
                                text = "START ROUTINE NOW 🚀",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            OutlinedButton(
                                onClick = { onSaveToLibrary(generatedWorkout) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("save_ai_routine_button"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(text = "Save to Library", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            OutlinedButton(
                                onClick = {
                                    onGenerate(selectedFocus, selectedDuration, notesInput)
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(text = "Regenerate", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    } else {
                        // Input State
                        Text(
                            text = "TRAINING FOCUS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = OrangeAccent,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            focuses.forEach { focus ->
                                val isSelected = focus == selectedFocus
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(if (isSelected) OrangeAccent else MaterialTheme.colorScheme.surfaceVariant)
                                        .clickable { selectedFocus = focus }
                                        .padding(horizontal = 14.dp, vertical = 7.dp)
                                        .testTag("ai_focus_$focus")
                                ) {
                                    Text(
                                        text = focus,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "DURATION (MINUTES)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = OrangeAccent,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            durations.forEach { dur ->
                                val isSelected = dur == selectedDuration
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(if (isSelected) OrangeAccent else MaterialTheme.colorScheme.surfaceVariant)
                                        .clickable { selectedDuration = dur }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${dur}m",
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "SPECIAL INSTRUCTIONS (OPTIONAL)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = OrangeAccent,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = notesInput,
                            onValueChange = { if (it.length <= 100) notesInput = it },
                            placeholder = { Text("e.g. Low impact on knees, burn belly fat, no pushups") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("ai_notes_input"),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = OrangeAccent,
                                focusedLabelColor = OrangeAccent
                            )
                        )

                        Spacer(modifier = Modifier.height(22.dp))

                        Button(
                            onClick = {
                                focusManager.clearFocus()
                                onGenerate(selectedFocus, selectedDuration, notesInput)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("generate_ai_routine_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "✨ Generate My Routine",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
