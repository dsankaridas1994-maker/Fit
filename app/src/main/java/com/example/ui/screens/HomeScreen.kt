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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.UserProfile
import com.example.model.Workout
import com.example.ui.components.CircularProgressRing
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.OrangeAccent
import com.example.viewmodel.NavTab

@Composable
fun HomeScreen(
    profile: UserProfile,
    todayMinutes: Int,
    streak: Int,
    totalWorkouts: Int,
    totalCalories: Int,
    recommendedWorkout: Workout?,
    onNavigateToTab: (NavTab) -> Unit,
    onSelectWorkout: (Workout) -> Unit,
    onOpenAiRoutine: () -> Unit,
    onOpenAiCoach: () -> Unit,
    onOpenMultiDayPlan: () -> Unit,
    onOpenDrawer: () -> Unit,
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
        // Top App Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onOpenDrawer,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                        .testTag("home_drawer_menu_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Open Drawer Menu",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = "INF",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "FIT",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.sp
                )
            }

            // Avatar button linking to Profile
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.sweepGradient(listOf(OrangeAccent, AmberAccent, OrangeAccent))
                    )
                    .clickable { onNavigateToTab(NavTab.PROFILE) }
                    .padding(2.5.dp)
                    .testTag("home_avatar_button"),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = profile.name.take(1).uppercase().ifBlank { "A" },
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = OrangeAccent
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Hero Greeting
        Text(
            text = "Ready to sweat,",
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "${profile.name}?",
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            color = OrangeAccent
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Rank & XP Bar
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateToTab(NavTab.PROFILE) }
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row {
                        Text(
                            text = "Rank: ",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = profile.rankTitle,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = OrangeAccent
                        )
                    }
                    Text(
                        text = "${profile.xp} XP (Lvl ${profile.level})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = AmberAccent
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // XP Progress Bar
                val xpProgress = (profile.currentLevelXp / 100f).coerceIn(0f, 1f)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(xpProgress)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                Brush.horizontalGradient(listOf(AmberAccent, OrangeAccent))
                            )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Daily Goal Ring Card
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)),
            border = androidx.compose.foundation.BorderStroke(1.dp, OrangeAccent.copy(alpha = 0.25f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "DAILY ACTIVE GOAL",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "$todayMinutes",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = if (todayMinutes >= profile.dailyGoalMinutes) EmeraldSuccess else OrangeAccent
                        )
                        Text(
                            text = " / ${profile.dailyGoalMinutes}m",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                    Text(
                        text = if (todayMinutes >= profile.dailyGoalMinutes) "Goal achieved today! 🔥" else "${profile.dailyGoalMinutes - todayMinutes}m remaining",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (todayMinutes >= profile.dailyGoalMinutes) EmeraldSuccess else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Ring
                val goalProgress = if (profile.dailyGoalMinutes > 0) todayMinutes.toFloat() / profile.dailyGoalMinutes.toFloat() else 0f
                CircularProgressRing(
                    progress = goalProgress,
                    strokeWidth = 8.dp,
                    modifier = Modifier.size(72.dp),
                    gradientColors = if (todayMinutes >= profile.dailyGoalMinutes) listOf(EmeraldSuccess, Color(0xFF34D399)) else listOf(OrangeAccent, AmberAccent)
                ) {
                    Text(
                        text = "${(goalProgress * 100).toInt()}%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3 Key Stats Box Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Workouts
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 14.dp, horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "$totalWorkouts", fontSize = 20.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
                    Text(text = "WORKOUTS", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // Streak
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 14.dp, horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "$streak 🔥", fontSize = 20.sp, fontWeight = FontWeight.Black, color = OrangeAccent)
                    Text(text = "STREAK", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // Calories
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 14.dp, horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "$totalCalories", fontSize = 20.sp, fontWeight = FontWeight.Black, color = AmberAccent)
                    Text(text = "KCAL", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // AI Personal Routine Generator Card
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)),
            border = androidx.compose.foundation.BorderStroke(
                1.5.dp,
                Brush.horizontalGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary))
            ),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onOpenAiRoutine() }
                .testTag("ai_routine_banner_card")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "✨", fontSize = 26.sp)
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "AI CUSTOM ROUTINE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.sp
                        )
                    }
                    Text(
                        text = "Build Your Routine with AI",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Tailored for your current rank & today's focus",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "GENERATE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // AI Features Quick Row: Ask AI Coach & Multi-Day Split
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier
                    .weight(1f)
                    .clickable { onOpenAiCoach() }
                    .testTag("home_ask_ai_coach_card")
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🤖", fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Ask AI Coach",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Form & injury tips",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier
                    .weight(1f)
                    .clickable { onOpenMultiDayPlan() }
                    .testTag("home_multi_day_plan_card")
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "📅", fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Weekly Split",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Multi-day plans",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Recommended Section
        if (recommendedWorkout != null) {
            Text(
                text = "RECOMMENDED TODAY",
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = OrangeAccent,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, OrangeAccent.copy(alpha = 0.35f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectWorkout(recommendedWorkout) }
                    .testTag("recommended_workout_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(OrangeAccent.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FitnessCenter,
                            contentDescription = null,
                            tint = OrangeAccent,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = recommendedWorkout.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Text(
                                text = "${recommendedWorkout.durationMinutes}m • ${recommendedWorkout.calories} kcal • ${recommendedWorkout.category}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(OrangeAccent),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Start",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Browse All Workouts CTA
        Button(
            onClick = { onNavigateToTab(NavTab.LIBRARY) },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("browse_workouts_button"),
            colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "Browse Full Library 📚",
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
        }
    }
}
