package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.model.AppThemePreset
import com.example.model.UserProfile
import com.example.viewmodel.NavTab

private const val SHARED_APP_URL = "https://ais-pre-3tgfntxbodzk7gtk3t7cp5-501343879625.asia-east1.run.app"
private const val APP_PACKAGE_ID = "com.aistudio.inffit.vrtkxz"

@Composable
fun AppNavigationDrawer(
    profile: UserProfile,
    currentTab: NavTab,
    currentTheme: AppThemePreset,
    onNavigateToTab: (NavTab) -> Unit,
    onOpenAiRoutine: () -> Unit,
    onOpenAiAssistant: () -> Unit,
    onOpenMultiDayPlan: () -> Unit,
    onOpenGoals: () -> Unit,
    onOpenSavedPlans: () -> Unit,
    onOpenThemes: () -> Unit,
    onSignOut: () -> Unit,
    onCloseDrawer: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    ModalDrawerSheet(
        drawerContainerColor = MaterialTheme.colorScheme.surface,
        drawerTonalElevation = 4.dp,
        modifier = Modifier
            .width(320.dp)
            .fillMaxHeight()
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(scrollState)
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Header: Athlete Profile Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                    )
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                        RoundedCornerShape(20.dp)
                    )
                    .clickable {
                        onNavigateToTab(NavTab.PROFILE)
                        onCloseDrawer()
                    }
                    .padding(16.dp)
                    .testTag("drawer_profile_header")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.sweepGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.secondary,
                                        MaterialTheme.colorScheme.primary
                                    )
                                )
                            )
                            .padding(2.5.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = profile.name.take(1).uppercase().ifBlank { "A" },
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = profile.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Lvl ${profile.level}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = " • ${profile.rankTitle}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = "${profile.xp} Total XP",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Navigation Items Section
            Text(
                text = "APP NAVIGATION",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            DrawerItem(
                label = "Home Dashboard",
                icon = Icons.Default.Home,
                selected = currentTab == NavTab.HOME,
                onClick = {
                    onNavigateToTab(NavTab.HOME)
                    onCloseDrawer()
                },
                testTag = "drawer_item_home"
            )

            DrawerItem(
                label = "Workout Library",
                icon = Icons.Default.FitnessCenter,
                selected = currentTab == NavTab.LIBRARY,
                onClick = {
                    onNavigateToTab(NavTab.LIBRARY)
                    onCloseDrawer()
                },
                testTag = "drawer_item_library"
            )

            DrawerItem(
                label = "Progress & Heatmap",
                icon = Icons.Default.BarChart,
                selected = currentTab == NavTab.PROGRESS,
                onClick = {
                    onNavigateToTab(NavTab.PROGRESS)
                    onCloseDrawer()
                },
                testTag = "drawer_item_progress"
            )

            DrawerItem(
                label = "Athlete Profile",
                icon = Icons.Default.Person,
                selected = currentTab == NavTab.PROFILE,
                onClick = {
                    onNavigateToTab(NavTab.PROFILE)
                    onCloseDrawer()
                },
                testTag = "drawer_item_profile"
            )

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(14.dp))

            // Special AI & Theme Features
            Text(
                text = "SPECIAL FEATURES",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            DrawerItem(
                label = "🤖 Ask AI Fitness Coach",
                icon = Icons.Default.Psychology,
                selected = false,
                onClick = {
                    onCloseDrawer()
                    onOpenAiAssistant()
                },
                testTag = "drawer_item_ai_coach"
            )

            DrawerItem(
                label = "✨ AI Routine Studio",
                icon = Icons.Default.AutoAwesome,
                selected = false,
                onClick = {
                    onCloseDrawer()
                    onOpenAiRoutine()
                },
                testTag = "drawer_item_ai_routine"
            )

            DrawerItem(
                label = "📅 Multi-Day Plan Generator",
                icon = Icons.Default.CalendarMonth,
                selected = false,
                onClick = {
                    onCloseDrawer()
                    onOpenMultiDayPlan()
                },
                testTag = "drawer_item_multi_day_plan"
            )

            DrawerItem(
                label = "🎯 Fitness Goals & Targets",
                icon = Icons.Default.Flag,
                selected = false,
                onClick = {
                    onCloseDrawer()
                    onOpenGoals()
                },
                testTag = "drawer_item_goals"
            )

            DrawerItem(
                label = "📋 Saved Training Plans",
                icon = Icons.Default.Bookmark,
                selected = false,
                onClick = {
                    onCloseDrawer()
                    onOpenSavedPlans()
                },
                testTag = "drawer_item_saved_plans"
            )

            DrawerItem(
                label = "🎨 Theme Locker (${currentTheme.displayName})",
                icon = Icons.Default.Palette,
                selected = false,
                onClick = {
                    onCloseDrawer()
                    onOpenThemes()
                },
                testTag = "drawer_item_theme"
            )

            DrawerItem(
                label = "🚪 Sign Out Account",
                icon = Icons.AutoMirrored.Filled.Logout,
                selected = false,
                onClick = {
                    onCloseDrawer()
                    onSignOut()
                },
                testTag = "drawer_item_sign_out"
            )

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(14.dp))

            // APK & App Hub Section ("Apk ka code")
            Text(
                text = "APK & SHARE HUB",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // APK Share Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                    .padding(14.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.SystemUpdate,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "INFFIT Mobile APK",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.primary)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "v1.2",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                    }

                    Text(
                        text = "App ID: $APP_PACKAGE_ID",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Copy Link Button
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .clickable {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("INFFIT App Link", SHARED_APP_URL)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "App URL Copied to clipboard! 📋", Toast.LENGTH_SHORT).show()
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Copy Link",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        // Share Link Button
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.primary)
                                .clickable {
                                    val shareText = "Hey! Check out INFFIT – high-intensity training, animated athletic visualizer, AI routine coach, and rank-based unlockable themes! Try the live app here: $SHARED_APP_URL"
                                    val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                        putExtra(Intent.EXTRA_TEXT, shareText)
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, "Share INFFIT APK Link"))
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Share APK",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(16.dp))

            // Footer
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "INFFIT Athletic Training v1.2",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Jetpack Compose • Material 3 • Gemini AI",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun DrawerItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    NavigationDrawerItem(
        label = {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = if (selected) FontWeight.Black else FontWeight.SemiBold
            )
        },
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(22.dp)
            )
        },
        selected = selected,
        onClick = onClick,
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
            selectedIconColor = MaterialTheme.colorScheme.primary,
            selectedTextColor = MaterialTheme.colorScheme.primary,
            unselectedContainerColor = Color.Transparent,
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unselectedTextColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .padding(vertical = 3.dp)
            .testTag(testTag)
    )
}
