package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.VoiceOverOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ExercisePose
import com.example.model.ExerciseType
import com.example.ui.components.CircularProgressRing
import com.example.ui.components.ExerciseVisualizer
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.CrimsonDanger
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.OrangeAccent
import com.example.viewmodel.PlayerPhase
import com.example.viewmodel.PlayerState

@Composable
fun WorkoutPlayerScreen(
    playerState: PlayerState,
    isVoiceEnabled: Boolean,
    onToggleVoice: () -> Unit,
    onCompleteRepExercise: () -> Unit,
    onIncrementReps: () -> Unit,
    onSkipRest: () -> Unit,
    onTogglePause: () -> Unit,
    onSkipExercise: () -> Unit,
    onPreviousExercise: () -> Unit,
    onQuit: () -> Unit,
    onAdjustWeight: (Float) -> Unit = {},
    onNextSet: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showQuitDialog by remember { mutableStateOf(false) }

    BackHandler {
        showQuitDialog = true
    }

    val workout = playerState.workout ?: return
    val totalExercises = workout.exercises.size
    val currentIndex = playerState.currentExerciseIndex
    val currentExercise = workout.exercises.getOrNull(currentIndex)
    val totalProgress = if (totalExercises > 0) (currentIndex.toFloat() / totalExercises.toFloat()).coerceIn(0f, 1f) else 0f

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Top Overall Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(Color(0x33969696))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(totalProgress)
                        .height(6.dp)
                        .background(MaterialTheme.colorScheme.primary)
                )
            }

            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { showQuitDialog = true },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .testTag("player_quit_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Quit",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = if (playerState.phase == PlayerPhase.PREPARE) "GET READY" else "EXERCISE ${currentIndex + 1} / $totalExercises",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp
                )

                IconButton(
                    onClick = onToggleVoice,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(
                        imageVector = if (isVoiceEnabled) Icons.Default.RecordVoiceOver else Icons.Default.VoiceOverOff,
                        contentDescription = "Voice Coach",
                        tint = if (isVoiceEnabled) OrangeAccent else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Visual Center Stage
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                when (playerState.phase) {
                    PlayerPhase.PREPARE -> {
                        Text(
                            text = "PREPARE TO SWEAT",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = OrangeAccent,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        ExerciseVisualizer(
                            pose = ExercisePose.REST,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp),
                            isPlaying = !playerState.isPaused
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Large Countdown
                        Text(
                            text = "${playerState.timerSeconds}",
                            fontSize = 80.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "First: ${workout.exercises.firstOrNull()?.name ?: ""}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    PlayerPhase.REST -> {
                        Text(
                            text = "REST & BREATHE",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = EmeraldSuccess,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        ExerciseVisualizer(
                            pose = ExercisePose.REST,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            isPlaying = !playerState.isPaused
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Next exercise preview pill
                        val nextEx = currentExercise
                        if (nextEx != null) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "Up Next: ${nextEx.name} (${if (nextEx.type == ExerciseType.TIME) "${nextEx.value}s" else "${nextEx.value} reps"})",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // 15s Ring
                        val restProgress = (playerState.timerSeconds.toFloat() / 15f).coerceIn(0f, 1f)
                        CircularProgressRing(
                            progress = restProgress,
                            strokeWidth = 10.dp,
                            modifier = Modifier.size(140.dp),
                            gradientColors = listOf(EmeraldSuccess, Color(0xFF34D399))
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${playerState.timerSeconds}",
                                    fontSize = 44.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "SEC",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = onSkipRest,
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.testTag("skip_rest_button")
                        ) {
                            Icon(imageVector = Icons.Default.FastForward, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Skip Rest", fontWeight = FontWeight.Black, color = Color.Black)
                        }
                    }

                    PlayerPhase.EXERCISE -> {
                        val exercise = currentExercise ?: return
                        Text(
                            text = exercise.name,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Target Muscle & Set Badges
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "TARGET: ${exercise.targetMuscle.uppercase()}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "SET ${playerState.currentSet} OF ${playerState.totalSets}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Animated Exercise Pose
                        ExerciseVisualizer(
                            pose = exercise.pose,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp),
                            isPlaying = !playerState.isPaused
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Instruction Box
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                                .border(1.dp, Color(0x26969696), RoundedCornerShape(12.dp))
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = exercise.instructions,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }

                        // Resistance / Weight Selector Row
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(top = 6.dp)
                        ) {
                            Text(
                                text = "Load: ",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            IconButton(
                                onClick = { onAdjustWeight(-2.5f) },
                                modifier = Modifier.size(26.dp)
                            ) {
                                Text("-", fontSize = 15.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                            }
                            Text(
                                text = if (playerState.weightKg > 0) "${playerState.weightKg} kg" else "Bodyweight",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                            IconButton(
                                onClick = { onAdjustWeight(2.5f) },
                                modifier = Modifier.size(26.dp)
                            ) {
                                Text("+", fontSize = 15.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        if (exercise.type == ExerciseType.TIME) {
                            // Time countdown ring
                            val progress = (playerState.timerSeconds.toFloat() / exercise.value.toFloat()).coerceIn(0f, 1f)
                            CircularProgressRing(
                                progress = progress,
                                strokeWidth = 10.dp,
                                modifier = Modifier.size(150.dp),
                                gradientColors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${playerState.timerSeconds}",
                                        fontSize = 50.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (playerState.timerSeconds <= 5) CrimsonDanger else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "SECONDS",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        letterSpacing = 1.sp
                                    )
                                }
                            }
                        } else {
                            // Rep based: Target & increment button
                            CircularProgressRing(
                                progress = (playerState.repsCompleted.toFloat() / exercise.value.toFloat()).coerceIn(0f, 1f),
                                strokeWidth = 10.dp,
                                modifier = Modifier
                                    .size(150.dp)
                                    .clickable { onIncrementReps() },
                                gradientColors = listOf(MaterialTheme.colorScheme.secondary, EmeraldSuccess)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${playerState.repsCompleted} / ${exercise.value}",
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "TAP TO COUNT",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.secondary,
                                        letterSpacing = 1.sp
                                    )
                                }
                            }
                        }
                    }

                    PlayerPhase.COMPLETED -> {
                        Text(text = "Finalizing...", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Bottom Controls Bar
            if (playerState.phase == PlayerPhase.EXERCISE) {
                val exercise = currentExercise
                if (exercise?.type == ExerciseType.REPS) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (playerState.currentSet < playerState.totalSets) {
                            Button(
                                onClick = onNextSet,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(54.dp)
                                    .testTag("next_set_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text(text = "Next Set (${playerState.currentSet + 1})", fontSize = 13.sp, fontWeight = FontWeight.Black, color = Color.White)
                            }
                        }

                        Button(
                            onClick = onCompleteRepExercise,
                            modifier = Modifier
                                .weight(1f)
                                .height(54.dp)
                                .testTag("complete_rep_exercise_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "DONE ✓", fontSize = 15.sp, fontWeight = FontWeight.Black, color = Color.Black)
                        }
                    }
                } else {
                    // Time Controls: Prev, Pause/Play, Skip
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 36.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onPreviousExercise,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .testTag("player_prev_button")
                        ) {
                            Icon(imageVector = Icons.Default.FastRewind, contentDescription = "Previous")
                        }

                        Spacer(modifier = Modifier.width(28.dp))

                        // Play/Pause
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                                .clickable { onTogglePause() }
                                .testTag("player_pause_play_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (playerState.isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                                contentDescription = if (playerState.isPaused) "Play" else "Pause",
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(28.dp))

                        IconButton(
                            onClick = onSkipExercise,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .testTag("player_skip_button")
                        ) {
                            Icon(imageVector = Icons.Default.FastForward, contentDescription = "Skip")
                        }
                    }
                }
            }
        }
    }

    if (showQuitDialog) {
        AlertDialog(
            onDismissRequest = { showQuitDialog = false },
            title = { Text(text = "Exit Workout?", fontWeight = FontWeight.Black) },
            text = { Text(text = "Are you sure you want to stop? Your current exercise session will not be saved.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showQuitDialog = false
                        onQuit()
                    }
                ) {
                    Text(text = "Exit", color = CrimsonDanger, fontWeight = FontWeight.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showQuitDialog = false }) {
                    Text(text = "Resume", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}
