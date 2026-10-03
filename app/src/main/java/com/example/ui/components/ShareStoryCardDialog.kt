package com.example.ui.components

import android.content.Context
import android.content.Intent
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.OrangeAccent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ShareStoryCardDialog(
    userName: String,
    userRank: String,
    workoutTitle: String,
    durationMinutes: Int,
    caloriesBurned: Int,
    xpGained: Int,
    streakDays: Int,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val todayDisplay = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date())

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF101014)),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, OrangeAccent.copy(alpha = 0.4f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "INF",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = "FIT",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = OrangeAccent
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Story Visual Badge
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(OrangeAccent, AmberAccent))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🔥", fontSize = 42.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "WORKOUT CRUSHED",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 1.sp
                )
                Text(
                    text = workoutTitle,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = OrangeAccent
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Story Metrics Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF1B1B22))
                        .border(1.dp, Color(0x33969696), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "$durationMinutes", fontSize = 24.sp, fontWeight = FontWeight.Black, color = Color.White)
                            Text(text = "MINS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "$caloriesBurned", fontSize = 24.sp, fontWeight = FontWeight.Black, color = AmberAccent)
                            Text(text = "KCAL", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "+$xpGained", fontSize = 24.sp, fontWeight = FontWeight.Black, color = EmeraldSuccess)
                            Text(text = "XP", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // User details strip
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Athlete: $userName ($userRank)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.LightGray
                    )
                    Text(
                        text = "Streak: $streakDays 🔥",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = OrangeAccent
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = todayDisplay,
                    fontSize = 11.sp,
                    color = Color.Gray,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Share Button
                Button(
                    onClick = {
                        val shareText = "🔥 Just crushed $workoutTitle on INFFIT! " +
                                "💪 $durationMinutes mins | ⚡ $caloriesBurned kcal | 🏆 $streakDays day streak! " +
                                "#INFFIT #FitnessMotivation"
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, "INFFIT Workout Milestone")
                            putExtra(Intent.EXTRA_TEXT, shareText)
                        }
                        context.startActivity(Intent.createChooser(intent, "Share Workout Milestone"))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Share Story Card", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(text = "Dismiss", color = Color.LightGray, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
