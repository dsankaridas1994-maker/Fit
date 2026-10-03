package com.example.ui.components

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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.UserProfile
import com.example.ui.theme.OrangeAccent

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditProfileDialog(
    profile: UserProfile,
    onSave: (
        name: String,
        age: Int,
        fitnessGoal: String,
        experienceLevel: String,
        preferredWorkoutType: String,
        availableEquipment: String,
        workoutFrequency: Int,
        dailyGoalMinutes: Int
    ) -> Unit,
    onDismiss: () -> Unit
) {
    var nameInput by remember { mutableStateOf(profile.name) }
    var ageInput by remember { mutableStateOf(profile.age.toString()) }
    var selectedGoal by remember { mutableStateOf(profile.fitnessGoal) }
    var selectedExperience by remember { mutableStateOf(profile.experienceLevel) }
    var selectedType by remember { mutableStateOf(profile.preferredWorkoutType) }
    var selectedEquipment by remember { mutableStateOf(profile.availableEquipment) }
    var workoutFreq by remember { mutableIntStateOf(profile.workoutFrequency.coerceIn(2, 7)) }
    var goalMinutes by remember { mutableIntStateOf(profile.dailyGoalMinutes.coerceIn(5, 60)) }

    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    val goalsList = listOf(
        "Build Muscle & Burn Fat",
        "Fat Loss & Shred",
        "Strength & Hypertrophy",
        "Athletic Conditioning",
        "Calisthenics & Agility"
    )

    val expLevels = listOf("Beginner", "Intermediate", "Advanced")

    val workoutTypes = listOf(
        "HIIT & Strength",
        "Pure Calisthenics",
        "Core & Cardio",
        "Full-Body Shred"
    )

    val equipmentList = listOf(
        "Bodyweight",
        "Dumbbells & Mat",
        "Full Gym Access"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
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
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .testTag("edit_profile_dialog")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(scrollState)
                        .padding(22.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Athlete Profile & Goals",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Personalize your training parameters",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FitnessCenter,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Name and Age Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { if (it.length <= 25) nameInput = it },
                            label = { Text("Name") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            modifier = Modifier
                                .weight(2f)
                                .testTag("edit_name_input"),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                focusedLabelColor = MaterialTheme.colorScheme.primary
                            )
                        )

                        OutlinedTextField(
                            value = ageInput,
                            onValueChange = { ageInput = it.filter { c -> c.isDigit() }.take(2) },
                            label = { Text("Age") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("edit_age_input"),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                focusedLabelColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Primary Fitness Goal
                    Text(
                        text = "PRIMARY FITNESS GOAL",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        goalsList.forEach { goal ->
                            val isSelected = selectedGoal == goal
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    .clickable { selectedGoal = goal }
                                    .padding(horizontal = 12.dp, vertical = 7.dp)
                            ) {
                                Text(
                                    text = goal,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Experience Level
                    Text(
                        text = "EXPERIENCE LEVEL",
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
                        expLevels.forEach { exp ->
                            val isSelected = selectedExperience == exp
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    .clickable { selectedExperience = exp }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = exp,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Available Equipment
                    Text(
                        text = "AVAILABLE EQUIPMENT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        equipmentList.forEach { equip ->
                            val isSelected = selectedEquipment == equip
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    .clickable { selectedEquipment = equip }
                                    .padding(horizontal = 12.dp, vertical = 7.dp)
                            ) {
                                Text(
                                    text = equip,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Workout Frequency and Daily Goal Steppers
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Days per week stepper
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "DAYS / WEEK", fontSize = 10.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { if (workoutFreq > 2) workoutFreq-- },
                                        modifier = Modifier.size(30.dp)
                                    ) {
                                        Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(16.dp))
                                    }
                                    Text(
                                        text = "$workoutFreq",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(horizontal = 6.dp)
                                    )
                                    IconButton(
                                        onClick = { if (workoutFreq < 7) workoutFreq++ },
                                        modifier = Modifier.size(30.dp)
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }

                        // Daily Goal minutes stepper
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "DAILY GOAL", fontSize = 10.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { if (goalMinutes > 5) goalMinutes -= 5 },
                                        modifier = Modifier.size(30.dp)
                                    ) {
                                        Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(16.dp))
                                    }
                                    Text(
                                        text = "${goalMinutes}m",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(horizontal = 6.dp)
                                    )
                                    IconButton(
                                        onClick = { if (goalMinutes < 60) goalMinutes += 5 },
                                        modifier = Modifier.size(30.dp)
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(text = "Cancel", fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Button(
                            onClick = {
                                focusManager.clearFocus()
                                val age = ageInput.toIntOrNull() ?: profile.age
                                onSave(
                                    nameInput.trim().ifBlank { profile.name },
                                    age,
                                    selectedGoal,
                                    selectedExperience,
                                    selectedType,
                                    selectedEquipment,
                                    workoutFreq,
                                    goalMinutes
                                )
                            },
                            modifier = Modifier.weight(1f).height(48.dp).testTag("save_profile_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(text = "Save Profile", fontWeight = FontWeight.Black, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
