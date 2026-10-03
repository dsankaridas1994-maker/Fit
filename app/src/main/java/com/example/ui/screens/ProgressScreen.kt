package com.example.ui.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Achievement
import com.example.model.DailyActivity
import com.example.model.WorkoutHistoryItem
import com.example.ui.components.ActivityBarChart
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.OrangeAccent

@Composable
fun ProgressScreen(
    currentStreak: Int,
    maxStreak: Int,
    totalWorkouts: Int,
    totalMinutes: Int,
    totalCalories: Int,
    timeframeDays: Int,
    dailyActivities: List<DailyActivity>,
    history: List<WorkoutHistoryItem>,
    achievements: List<Achievement>,
    onSelectTimeframe: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp, bottom = 96.dp)
    ) {
        // Top Bar
        Text(
            text = "Progress",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Timeframe switch pills
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            val is7 = timeframeDays == 7
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (is7) OrangeAccent else MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { onSelectTimeframe(7) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("timeframe_7_days")
            ) {
                Text(
                    text = "7 Days",
                    fontSize = 12.sp,
                    fontWeight = if (is7) FontWeight.Black else FontWeight.Bold,
                    color = if (is7) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            val is30 = timeframeDays == 30
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (is30) OrangeAccent else MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { onSelectTimeframe(30) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("timeframe_30_days")
            ) {
                Text(
                    text = "30 Days",
                    fontSize = 12.sp,
                    fontWeight = if (is30) FontWeight.Black else FontWeight.Bold,
                    color = if (is30) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Streaks & Stats Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = EmeraldSuccess.copy(alpha = 0.15f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldSuccess.copy(alpha = 0.4f)),
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "$currentStreak", fontSize = 22.sp, fontWeight = FontWeight.Black, color = EmeraldSuccess)
                    Text(text = "DAY STREAK", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = EmeraldSuccess)
                }
            }

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "$maxStreak", fontSize = 22.sp, fontWeight = FontWeight.Black, color = AmberAccent)
                    Text(text = "BEST STREAK", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "$totalWorkouts", fontSize = 22.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
                    Text(text = "TOTAL WKTS", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Activity Chart Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, OrangeAccent.copy(alpha = 0.2f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Last $timeframeDays Days (Minutes)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "$totalMinutes mins total",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = OrangeAccent
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                ActivityBarChart(activities = dailyActivities)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // History Section
        Text(
            text = "📜 WORKOUT HISTORY",
            fontSize = 13.sp,
            fontWeight = FontWeight.Black,
            color = OrangeAccent,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (history.isEmpty()) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "🏋️", fontSize = 32.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No workouts recorded yet.",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Complete your first workout to see history here!",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                history.take(15).forEach { item ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.workoutTitle,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = item.dateFormatted,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(end = 12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Timer,
                                        contentDescription = null,
                                        tint = OrangeAccent,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = " ${item.durationMinutes}m",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.LocalFireDepartment,
                                        contentDescription = null,
                                        tint = AmberAccent,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = " ${item.caloriesBurned} kcal",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AmberAccent
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Achievements Section
        Text(
            text = "🏆 ACHIEVEMENTS",
            fontSize = 13.sp,
            fontWeight = FontWeight.Black,
            color = AmberAccent,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            achievements.forEach { ach ->
                val isUnlocked = ach.isUnlocked
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isUnlocked) AmberAccent.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ),
                    border = if (isUnlocked) androidx.compose.foundation.BorderStroke(1.dp, AmberAccent.copy(alpha = 0.5f)) else null,
                    modifier = Modifier.fillMaxWidth().testTag("achievement_${ach.id}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = ach.icon,
                            fontSize = 30.sp,
                            modifier = Modifier.padding(end = 12.dp)
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = ach.title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isUnlocked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (isUnlocked) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Unlocked",
                                        tint = EmeraldSuccess,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                            Text(
                                text = ach.description,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (isUnlocked && ach.unlockedDate != null) {
                                Text(
                                    text = "Unlocked: ${ach.unlockedDate}",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AmberAccent,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }

                        if (!isUnlocked) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Locked",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
