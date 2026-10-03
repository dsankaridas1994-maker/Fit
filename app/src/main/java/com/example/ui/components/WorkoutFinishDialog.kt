package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.Achievement
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.OrangeAccent

@Composable
fun WorkoutFinishDialog(
    workoutTitle: String,
    durationMinutes: Int,
    caloriesBurned: Int,
    xpGained: Int,
    unlockedAchievements: List<Achievement>,
    onShareStory: () -> Unit,
    onDone: () -> Unit
) {
    Dialog(
        onDismissRequest = onDone,
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Card(
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(2.dp, EmeraldSuccess.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .testTag("workout_finish_dialog")
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    ConfettiEffect(modifier = Modifier.matchParentSize())

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(EmeraldSuccess.copy(alpha = 0.15f))
                                .border(2.dp, EmeraldSuccess, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = EmeraldSuccess,
                                modifier = Modifier.size(40.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "WORKOUT CRUSHED!",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Black,
                            color = EmeraldSuccess,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = workoutTitle,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Results grid
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$durationMinutes",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "MINUTES",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$caloriesBurned",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black,
                                    color = AmberAccent
                                )
                                Text(
                                    text = "KCAL",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "+$xpGained",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black,
                                    color = OrangeAccent
                                )
                                Text(
                                    text = "XP EARNED",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Unlocked achievements highlight
                        if (unlockedAchievements.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(AmberAccent.copy(alpha = 0.15f))
                                    .border(1.dp, AmberAccent.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                                    .padding(12.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = "🏆 Achievement Unlocked!",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black,
                                        color = AmberAccent
                                    )
                                    unlockedAchievements.forEach { ach ->
                                        Text(
                                            text = "${ach.icon} ${ach.title}",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = onShareStory,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("share_story_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "📸 Share Story Card",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = onDone,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("finish_done_button"),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(
                                text = "Done",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}
