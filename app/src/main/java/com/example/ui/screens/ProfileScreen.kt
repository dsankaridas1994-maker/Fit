package com.example.ui.screens

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.UserPreferences
import com.example.model.UserProfile
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.CrimsonDanger
import com.example.ui.theme.OrangeAccent

@Composable
fun ProfileScreen(
    profile: UserProfile,
    preferences: UserPreferences,
    onEditProfileClick: () -> Unit,
    onOpenGoals: () -> Unit,
    onOpenSavedPlans: () -> Unit,
    onToggleTheme: () -> Unit,
    onToggleVoice: () -> Unit,
    onToggleSound: () -> Unit,
    onToggleVibration: () -> Unit,
    onSelectTheme: (com.example.model.AppThemePreset) -> Unit,
    onResetData: () -> Unit,
    onSignOut: () -> Unit,
    onDeleteAccount: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    var showResetDialog by remember { mutableStateOf(false) }
    var showSignOutDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }
    var isReminderEnabled by remember { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp, bottom = 96.dp)
    ) {
        Text(
            text = "Profile",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Profile Hero Card
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, OrangeAccent.copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(Brush.sweepGradient(listOf(OrangeAccent, AmberAccent, OrangeAccent)))
                            .padding(3.dp),
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
                                text = profile.name.take(1).uppercase().ifBlank { "B" },
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                color = OrangeAccent
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = profile.name,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Rank: ",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${profile.rankTitle} (Level ${profile.level})",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = OrangeAccent
                            )
                        }
                        Text(
                            text = "Daily Goal: ${profile.dailyGoalMinutes} mins",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // XP Progress bar
                val xpProg = (profile.currentLevelXp / 100f).coerceIn(0f, 1f)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "PROGRESS TO LEVEL ${profile.level + 1}", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = "${profile.currentLevelXp} / 100 XP", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = AmberAccent)
                }

                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(xpProg)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Brush.horizontalGradient(listOf(OrangeAccent, AmberAccent)))
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Athlete Blueprint Specifications Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth().testTag("athlete_specs_card")
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ATHLETE BLUEPRINT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )

                    Text(
                        text = "Edit Specs",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = OrangeAccent,
                        modifier = Modifier
                            .clickable { onEditProfileClick() }
                            .padding(4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Goal & Level
                    SpecBadge(
                        label = "TARGET GOAL",
                        value = profile.fitnessGoal,
                        modifier = Modifier.weight(1f)
                    )
                    SpecBadge(
                        label = "EXPERIENCE",
                        value = "${profile.experienceLevel} (${profile.age}y)",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SpecBadge(
                        label = "EQUIPMENT",
                        value = profile.availableEquipment,
                        modifier = Modifier.weight(1f)
                    )
                    SpecBadge(
                        label = "FREQUENCY",
                        value = "${profile.workoutFrequency} Days / Week",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Settings Group
        Text(
            text = "SETTINGS & PREFERENCES",
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            color = OrangeAccent,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                // Edit Profile Row
                SettingsActionRow(
                    icon = Icons.Default.Edit,
                    label = "Edit Profile",
                    onClick = onEditProfileClick,
                    testTag = "edit_profile_row"
                )

                // Theme Toggle
                SettingsToggleRow(
                    icon = Icons.Default.DarkMode,
                    label = "Dark Theme",
                    isChecked = preferences.isDarkTheme,
                    onCheckedChange = { onToggleTheme() },
                    statusText = if (preferences.isDarkTheme) "Dark" else "Light",
                    testTag = "toggle_theme_switch"
                )

                // Voice Coach Toggle
                SettingsToggleRow(
                    icon = Icons.Default.RecordVoiceOver,
                    label = "AI Voice Coach (TTS)",
                    isChecked = preferences.isVoiceEnabled,
                    onCheckedChange = { onToggleVoice() },
                    statusText = if (preferences.isVoiceEnabled) "ON" else "OFF",
                    testTag = "toggle_voice_switch"
                )

                // Sound FX Toggle
                SettingsToggleRow(
                    icon = Icons.AutoMirrored.Filled.VolumeUp,
                    label = "Sound Ticks & Chimes",
                    isChecked = preferences.isSoundEnabled,
                    onCheckedChange = { onToggleSound() },
                    statusText = if (preferences.isSoundEnabled) "ON" else "OFF",
                    testTag = "toggle_sound_switch"
                )

                // Vibration Toggle
                SettingsToggleRow(
                    icon = Icons.Default.Vibration,
                    label = "Haptic Vibration",
                    isChecked = preferences.isVibrationEnabled,
                    onCheckedChange = { onToggleVibration() },
                    statusText = if (preferences.isVibrationEnabled) "ON" else "OFF",
                    testTag = "toggle_vib_switch"
                )

                // Fitness Goals & Targets
                SettingsActionRow(
                    icon = Icons.Default.Flag,
                    label = "Fitness Goals & Targets",
                    onClick = onOpenGoals,
                    testTag = "profile_goals_row"
                )

                // Saved AI Training Plans
                SettingsActionRow(
                    icon = Icons.Default.Bookmark,
                    label = "Saved Training Plans",
                    onClick = onOpenSavedPlans,
                    testTag = "profile_saved_plans_row"
                )

                // Daily Training Reminders Toggle
                SettingsToggleRow(
                    icon = Icons.Default.Notifications,
                    label = "Daily Training Reminder",
                    isChecked = isReminderEnabled,
                    onCheckedChange = { isReminderEnabled = it },
                    statusText = if (isReminderEnabled) "08:00 AM" else "OFF",
                    testTag = "toggle_reminder_switch"
                )

                // Share App with Friends for Review
                SettingsActionRow(
                    icon = Icons.Default.Share,
                    label = "Share App with Friends to Review",
                    onClick = {
                        val shareText = "Hey! Check out INFFIT – high-intensity training, animated athletic visualizer, AI routine coach, and rank-based unlockable themes! Try the live app here: https://ais-pre-3tgfntxbodzk7gtk3t7cp5-501343879625.asia-east1.run.app"
                        val sendIntent = Intent(Intent.ACTION_SEND).apply {
                            putExtra(Intent.EXTRA_TEXT, shareText)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Share INFFIT for Review"))
                    },
                    testTag = "share_app_review_row"
                )

                // Sign Out of Account
                SettingsActionRow(
                    icon = Icons.AutoMirrored.Filled.Logout,
                    label = "Sign Out of Account",
                    onClick = { showSignOutDialog = true },
                    testTag = "profile_sign_out_row"
                )

                // Delete Account
                SettingsActionRow(
                    icon = Icons.Default.DeleteForever,
                    label = "Delete Account & Cloud Data",
                    onClick = { showDeleteAccountDialog = true },
                    testTag = "profile_delete_account_row"
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Unlockable Themes Section
        Text(
            text = "UNLOCKABLE THEMES (RANK PROGRESSION)",
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            com.example.model.AppThemePreset.entries.forEach { preset ->
                val isUnlocked = profile.level >= preset.requiredLevel
                val isSelected = preferences.selectedTheme == preset

                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) {
                            preset.primaryColor.copy(alpha = 0.15f)
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        }
                    ),
                    border = if (isSelected) {
                        androidx.compose.foundation.BorderStroke(1.5.dp, preset.primaryColor)
                    } else if (isUnlocked) {
                        androidx.compose.foundation.BorderStroke(1.dp, preset.primaryColor.copy(alpha = 0.3f))
                    } else null,
                    modifier = Modifier.fillMaxWidth().testTag("theme_card_${preset.id}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            // Theme Color Palette Swatch (tri-color dot)
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(preset.darkBackground)
                                    .padding(6.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clip(CircleShape)
                                        .background(preset.primaryColor)
                                )
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clip(CircleShape)
                                        .background(preset.secondaryColor)
                                )
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clip(CircleShape)
                                        .background(preset.tertiaryColor)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Text(
                                    text = preset.displayName,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (isUnlocked) "Unlocked: Level ${preset.requiredLevel} (${preset.requiredRank})" else "Requires Level ${preset.requiredLevel} (${preset.requiredRank})",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isUnlocked) preset.primaryColor else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(preset.primaryColor)
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "ACTIVE ✓",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }
                        } else if (isUnlocked) {
                            Button(
                                onClick = { onSelectTheme(preset) },
                                colors = ButtonDefaults.buttonColors(containerColor = preset.primaryColor),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("equip_theme_${preset.id}")
                            ) {
                                Text(text = "Equip", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🔒", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Lvl ${preset.requiredLevel}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Help & FAQ Accordions
        Text(
            text = "HELP & FREQUENTLY ASKED",
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        FaqAccordion(
            question = "How are active minutes and streaks counted?",
            answer = "Any workout you complete logs actual active training time toward your daily goal. Completing at least one workout every day continues your streak unbroken!"
        )

        FaqAccordion(
            question = "How are calories burned estimated?",
            answer = "INFFIT calculates calorie consumption based on calibrated MET values for high-intensity calisthenics, core exercises, and HIIT intervals, averaging 7-10 kcal per minute."
        )

        FaqAccordion(
            question = "How does the XP & Rank leveling work?",
            answer = "You earn 15 XP for each workout minute plus a 25 XP completion bonus. Reach higher thresholds to level up your rank from Novice to Warrior, Titan, Beast, and Legend!"
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Reset All Data Button
        OutlinedButton(
            onClick = { showResetDialog = true },
            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("reset_data_button"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = CrimsonDanger)
        ) {
            Icon(imageVector = Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Reset All Training Data", fontWeight = FontWeight.Bold)
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text(text = "Reset Training Data?", fontWeight = FontWeight.Black) },
            text = { Text(text = "This will reset all workouts completed, streak, XP, and history. This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onResetData()
                        showResetDialog = false
                    }
                ) {
                    Text(text = "Reset Everything", color = CrimsonDanger, fontWeight = FontWeight.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text(text = "Cancel", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    if (showSignOutDialog) {
        AlertDialog(
            onDismissRequest = { showSignOutDialog = false },
            title = { Text(text = "Sign Out of Account?", fontWeight = FontWeight.Black) },
            text = { Text(text = "Your progress is securely backed up to Cloud Firestore. You can sign back in anytime with your Google account.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSignOutDialog = false
                        onSignOut()
                    }
                ) {
                    Text(text = "Sign Out", color = CrimsonDanger, fontWeight = FontWeight.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSignOutDialog = false }) {
                    Text(text = "Cancel", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    if (showDeleteAccountDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAccountDialog = false },
            title = { Text(text = "Permanently Delete Account?", fontWeight = FontWeight.Black, color = CrimsonDanger) },
            text = { Text(text = "This will permanently remove your athlete profile, cloud workout history, training plans, and settings from Firebase. This action cannot be reversed.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteAccountDialog = false
                        onDeleteAccount()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonDanger)
                ) {
                    Text(text = "Delete Forever", fontWeight = FontWeight.Black, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAccountDialog = false }) {
                    Text(text = "Cancel", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
private fun SpecBadge(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.6f))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Column {
            Text(
                text = label,
                fontSize = 9.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun SettingsActionRow(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp)
            .testTag(testTag),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = OrangeAccent, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        }
        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun SettingsToggleRow(
    icon: ImageVector,
    label: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    statusText: String,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .testTag(testTag),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon(imageVector = icon, contentDescription = null, tint = OrangeAccent, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = statusText,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (isChecked) OrangeAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(end = 8.dp)
            )
            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = OrangeAccent)
            )
        }
    }
}

@Composable
private fun FaqAccordion(
    question: String,
    answer: String
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = question,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = OrangeAccent
                )
            }

            AnimatedVisibility(visible = expanded) {
                Text(
                    text = answer,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }
        }
    }
}
