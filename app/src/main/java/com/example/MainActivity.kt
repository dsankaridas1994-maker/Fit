package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AiAssistantDialog
import com.example.ui.components.AiRoutineDialog
import com.example.ui.components.AppNavigationDrawer
import com.example.ui.components.CreateWorkoutDialog
import com.example.ui.components.GoalsDialog
import com.example.ui.components.MultiDayPlanDialog
import com.example.ui.components.SavedPlansDialog
import com.example.ui.screens.SignInScreen
import com.example.ui.screens.attemptAutoSignIn
import com.example.ui.screens.signOutUser
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import com.example.ui.components.EditProfileDialog
import com.example.ui.components.OnboardingDialog
import com.example.ui.components.ShareStoryCardDialog
import com.example.ui.components.WorkoutFinishDialog
import com.example.ui.components.WorkoutPreviewDialog
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ProgressScreen
import com.example.ui.screens.WorkoutPlayerScreen
import com.example.ui.theme.InffitTheme
import com.example.ui.theme.OrangeAccent
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.NavTab

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val coroutineScope = rememberCoroutineScope()
            val credentialManager = remember { androidx.credentials.CredentialManager.create(context) }
            var currentUser by remember { mutableStateOf(Firebase.auth.currentUser) }

            DisposableEffect(Unit) {
                val listener = FirebaseAuth.AuthStateListener { auth ->
                    currentUser = auth.currentUser
                    auth.currentUser?.uid?.let { uid ->
                        viewModel.syncUserFromCloud(uid)
                    }
                }
                Firebase.auth.addAuthStateListener(listener)
                onDispose {
                    Firebase.auth.removeAuthStateListener(listener)
                }
            }

            LaunchedEffect(Unit) {
                attemptAutoSignIn(
                    context = context,
                    credentialManager = credentialManager,
                    onAuthSuccess = {
                        currentUser = Firebase.auth.currentUser
                        currentUser?.uid?.let { uid ->
                            viewModel.syncUserFromCloud(uid)
                        }
                    },
                    onUnauthenticated = { },
                    scope = coroutineScope
                )
            }

            val preferences by viewModel.preferences.collectAsStateWithLifecycle()

            InffitTheme(
                darkTheme = preferences.isDarkTheme,
                themePreset = preferences.selectedTheme
            ) {
                if (currentUser == null) {
                    SignInScreen(
                        onAuthSuccess = {
                            currentUser = Firebase.auth.currentUser
                            currentUser?.uid?.let { uid ->
                                viewModel.syncUserFromCloud(uid)
                            }
                        }
                    )
                } else {
                    MainContent(
                        viewModel = viewModel,
                        onSignOut = {
                            signOutUser(
                                context = context,
                                credentialManager = credentialManager,
                                onSignOutComplete = {
                                    currentUser = null
                                },
                                scope = coroutineScope
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MainContent(
    viewModel: MainViewModel,
    onSignOut: () -> Unit
) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    val preferences by viewModel.preferences.collectAsStateWithLifecycle()
    val allWorkouts by viewModel.allWorkouts.collectAsStateWithLifecycle()
    val totalWorkouts by viewModel.totalWorkouts.collectAsStateWithLifecycle()
    val totalMinutes by viewModel.totalMinutes.collectAsStateWithLifecycle()
    val totalCalories by viewModel.totalCalories.collectAsStateWithLifecycle()
    val currentStreak by viewModel.currentStreak.collectAsStateWithLifecycle()
    val maxStreak by viewModel.maxStreak.collectAsStateWithLifecycle()
    val history by viewModel.history.collectAsStateWithLifecycle()
    val achievements by viewModel.achievements.collectAsStateWithLifecycle()

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val progressTimeframe by viewModel.progressTimeframeDays.collectAsStateWithLifecycle()

    val showOnboarding by viewModel.showOnboarding.collectAsStateWithLifecycle()
    val previewWorkout by viewModel.previewWorkout.collectAsStateWithLifecycle()
    val playerState by viewModel.playerState.collectAsStateWithLifecycle()
    val finishSummary by viewModel.finishSummary.collectAsStateWithLifecycle()
    val showShareDialog by viewModel.showShareDialog.collectAsStateWithLifecycle()
    val showEditProfileDialog by viewModel.showEditProfileDialog.collectAsStateWithLifecycle()
    val showCreateWorkoutDialog by viewModel.showCreateWorkoutDialog.collectAsStateWithLifecycle()
    val showAiRoutineDialog by viewModel.showAiRoutineDialog.collectAsStateWithLifecycle()
    val isAiGenerating by viewModel.isAiGenerating.collectAsStateWithLifecycle()
    val generatedAiWorkout by viewModel.generatedAiWorkout.collectAsStateWithLifecycle()

    val goals by viewModel.goals.collectAsStateWithLifecycle()
    val savedPlans by viewModel.savedPlans.collectAsStateWithLifecycle()
    val showAiAssistantDialog by viewModel.showAiAssistantDialog.collectAsStateWithLifecycle()
    val showMultiDayPlanDialog by viewModel.showMultiDayPlanDialog.collectAsStateWithLifecycle()
    val showGoalsDialog by viewModel.showGoalsDialog.collectAsStateWithLifecycle()
    val showSavedPlansDialog by viewModel.showSavedPlansDialog.collectAsStateWithLifecycle()

    val todayActiveMinutes = viewModel.getTodayActiveMinutes()
    val dailyActivities = viewModel.getDailyActivityHistory(progressTimeframe)

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Active Fullscreen Player
    if (playerState != null) {
        WorkoutPlayerScreen(
            playerState = playerState!!,
            isVoiceEnabled = preferences.isVoiceEnabled,
            onToggleVoice = { viewModel.toggleVoice() },
            onCompleteRepExercise = { viewModel.completeRepExercise() },
            onIncrementReps = { viewModel.incrementReps() },
            onSkipRest = { viewModel.skipRest() },
            onTogglePause = { viewModel.togglePause() },
            onSkipExercise = { viewModel.skipExercise() },
            onPreviousExercise = { viewModel.previousExercise() },
            onQuit = { viewModel.quitWorkout() },
            onAdjustWeight = { viewModel.adjustWeight(it) },
            onNextSet = { viewModel.nextSetOrComplete() }
        )
        return
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppNavigationDrawer(
                profile = profile,
                currentTab = currentTab,
                currentTheme = preferences.selectedTheme,
                onNavigateToTab = { viewModel.selectTab(it) },
                onOpenAiRoutine = { viewModel.openAiRoutineDialog() },
                onOpenAiAssistant = { viewModel.openAiAssistant() },
                onOpenMultiDayPlan = { viewModel.openMultiDayPlanDialog() },
                onOpenGoals = { viewModel.openGoalsDialog() },
                onOpenSavedPlans = { viewModel.openSavedPlansDialog() },
                onOpenThemes = { viewModel.selectTab(NavTab.PROFILE) },
                onSignOut = onSignOut,
                onCloseDrawer = { scope.launch { drawerState.close() } }
            )
        }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .navigationBarsPadding()
                    .testTag("bottom_nav_bar")
            ) {
                // Home Tab
                NavigationBarItem(
                    selected = currentTab == NavTab.HOME,
                    onClick = { viewModel.selectTab(NavTab.HOME) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == NavTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                            contentDescription = "Home",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(
                            text = "Home",
                            fontWeight = if (currentTab == NavTab.HOME) FontWeight.ExtraBold else FontWeight.Medium,
                            fontSize = 11.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_tab_home")
                )

                // Library Tab
                NavigationBarItem(
                    selected = currentTab == NavTab.LIBRARY,
                    onClick = { viewModel.selectTab(NavTab.LIBRARY) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == NavTab.LIBRARY) Icons.Filled.FitnessCenter else Icons.Outlined.FitnessCenter,
                            contentDescription = "Library",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(
                            text = "Library",
                            fontWeight = if (currentTab == NavTab.LIBRARY) FontWeight.ExtraBold else FontWeight.Medium,
                            fontSize = 11.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_tab_library")
                )

                // Progress Tab
                NavigationBarItem(
                    selected = currentTab == NavTab.PROGRESS,
                    onClick = { viewModel.selectTab(NavTab.PROGRESS) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == NavTab.PROGRESS) Icons.Filled.BarChart else Icons.Outlined.BarChart,
                            contentDescription = "Progress",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(
                            text = "Progress",
                            fontWeight = if (currentTab == NavTab.PROGRESS) FontWeight.ExtraBold else FontWeight.Medium,
                            fontSize = 11.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_tab_progress")
                )

                // Profile Tab
                NavigationBarItem(
                    selected = currentTab == NavTab.PROFILE,
                    onClick = { viewModel.selectTab(NavTab.PROFILE) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == NavTab.PROFILE) Icons.Filled.Person else Icons.Outlined.Person,
                            contentDescription = "Me",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(
                            text = "Me",
                            fontWeight = if (currentTab == NavTab.PROFILE) FontWeight.ExtraBold else FontWeight.Medium,
                            fontSize = 11.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_tab_profile")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "tab_content"
            ) { targetTab ->
                when (targetTab) {
                    NavTab.HOME -> HomeScreen(
                        profile = profile,
                        todayMinutes = todayActiveMinutes,
                        streak = currentStreak,
                        totalWorkouts = totalWorkouts,
                        totalCalories = totalCalories,
                        recommendedWorkout = allWorkouts.firstOrNull(),
                        onNavigateToTab = { viewModel.selectTab(it) },
                        onSelectWorkout = { viewModel.openPreview(it) },
                        onOpenAiRoutine = { viewModel.openAiRoutineDialog() },
                        onOpenAiCoach = { viewModel.openAiAssistant() },
                        onOpenMultiDayPlan = { viewModel.openMultiDayPlanDialog() },
                        onOpenDrawer = { scope.launch { drawerState.open() } }
                    )

                    NavTab.LIBRARY -> LibraryScreen(
                        workouts = allWorkouts,
                        searchQuery = searchQuery,
                        selectedCategory = selectedCategory,
                        onSearchChange = { viewModel.setSearchQuery(it) },
                        onCategoryChange = { viewModel.setSelectedCategory(it) },
                        onSelectWorkout = { viewModel.openPreview(it) },
                        onCreateCustomWorkout = { viewModel.openCreateWorkout() },
                        onOpenAiRoutine = { viewModel.openAiRoutineDialog() },
                        onOpenDrawer = { scope.launch { drawerState.open() } }
                    )

                    NavTab.PROGRESS -> ProgressScreen(
                        currentStreak = currentStreak,
                        maxStreak = maxStreak,
                        totalWorkouts = totalWorkouts,
                        totalMinutes = totalMinutes,
                        totalCalories = totalCalories,
                        timeframeDays = progressTimeframe,
                        dailyActivities = dailyActivities,
                        history = history,
                        achievements = achievements,
                        onSelectTimeframe = { viewModel.setProgressTimeframe(it) }
                    )

                    NavTab.PROFILE -> ProfileScreen(
                        profile = profile,
                        preferences = preferences,
                        onEditProfileClick = { viewModel.openEditProfile() },
                        onOpenGoals = { viewModel.openGoalsDialog() },
                        onOpenSavedPlans = { viewModel.openSavedPlansDialog() },
                        onToggleTheme = { viewModel.toggleTheme() },
                        onToggleVoice = { viewModel.toggleVoice() },
                        onToggleSound = { viewModel.toggleSound() },
                        onToggleVibration = { viewModel.toggleVibration() },
                        onSelectTheme = { viewModel.selectTheme(it) },
                        onResetData = { viewModel.resetAllData() },
                        onSignOut = onSignOut,
                        onDeleteAccount = {
                            viewModel.deleteAccount {
                                onSignOut()
                            }
                        }
                    )
                }
            }
        }
    }
}

    // Onboarding Dialog
    if (showOnboarding) {
        OnboardingDialog(
            onComplete = { name, goal ->
                viewModel.completeOnboarding(name, goal)
            }
        )
    }

    // Workout Details Preview
    previewWorkout?.let { workout ->
        WorkoutPreviewDialog(
            workout = workout,
            onStart = { viewModel.startWorkout(workout) },
            onDismiss = { viewModel.closePreview() }
        )
    }

    // Workout Finished Celebration
    finishSummary?.let { summary ->
        WorkoutFinishDialog(
            workoutTitle = summary.workout.title,
            durationMinutes = summary.durationMinutes,
            caloriesBurned = summary.calories,
            xpGained = summary.xpGained,
            unlockedAchievements = summary.unlockedAchievements,
            onShareStory = { viewModel.openShareDialog() },
            onDone = { viewModel.closeFinishSummary() }
        )
    }

    // Share Story Card Dialog
    if (showShareDialog && finishSummary != null) {
        val summary = finishSummary!!
        ShareStoryCardDialog(
            userName = profile.name,
            userRank = profile.rankTitle,
            workoutTitle = summary.workout.title,
            durationMinutes = summary.durationMinutes,
            caloriesBurned = summary.calories,
            xpGained = summary.xpGained,
            streakDays = currentStreak,
            onDismiss = { viewModel.closeShareDialog() }
        )
    }

    // Edit Profile Dialog
    if (showEditProfileDialog) {
        EditProfileDialog(
            profile = profile,
            onSave = { name, age, fitnessGoal, experienceLevel, preferredWorkoutType, availableEquipment, workoutFrequency, dailyGoalMinutes ->
                viewModel.updateFullProfile(
                    name = name,
                    age = age,
                    fitnessGoal = fitnessGoal,
                    experienceLevel = experienceLevel,
                    preferredWorkoutType = preferredWorkoutType,
                    availableEquipment = availableEquipment,
                    workoutFrequency = workoutFrequency,
                    dailyGoalMinutes = dailyGoalMinutes
                )
            },
            onDismiss = { viewModel.closeEditProfile() }
        )
    }

    // Create Custom Workout Dialog
    if (showCreateWorkoutDialog) {
        CreateWorkoutDialog(
            onSave = { newWorkout ->
                viewModel.addCustomWorkout(newWorkout)
            },
            onDismiss = { viewModel.closeCreateWorkout() }
        )
    }

    // AI Routine Generator Dialog
    if (showAiRoutineDialog) {
        AiRoutineDialog(
            athleteName = profile.name,
            athleteLevel = profile.rankTitle,
            isGenerating = isAiGenerating,
            generatedWorkout = generatedAiWorkout,
            onGenerate = { focus, duration, notes ->
                viewModel.generateAiRoutine(focus, duration, notes)
            },
            onStartWorkout = { workout ->
                viewModel.startAiRoutineImmediately(workout)
            },
            onSaveToLibrary = { workout ->
                viewModel.saveAiRoutineToLibrary(workout)
            },
            onDismiss = { viewModel.closeAiRoutineDialog() }
        )
    }

    // AI Fitness Coach Assistant Dialog
    if (showAiAssistantDialog) {
        AiAssistantDialog(
            athlete = profile,
            onDismiss = { viewModel.closeAiAssistant() }
        )
    }

    // Multi-Day Periodized Plan Generator Dialog
    if (showMultiDayPlanDialog) {
        MultiDayPlanDialog(
            athlete = profile,
            onSavePlan = { plan ->
                viewModel.saveWorkoutPlan(plan)
            },
            onDismiss = { viewModel.closeMultiDayPlanDialog() }
        )
    }

    // Fitness Goals Dialog
    if (showGoalsDialog) {
        GoalsDialog(
            goals = goals,
            onAddGoal = { goal ->
                viewModel.addGoal(goal)
            },
            onToggleGoalComplete = { goal ->
                viewModel.toggleGoalComplete(goal)
            },
            onDeleteGoal = { goalId ->
                viewModel.deleteGoal(goalId)
            },
            onDismiss = { viewModel.closeGoalsDialog() }
        )
    }

    // Saved Workout Plans Dialog
    if (showSavedPlansDialog) {
        SavedPlansDialog(
            savedPlans = savedPlans,
            onStartWorkout = { workout ->
                viewModel.startWorkout(workout)
            },
            onDeletePlan = { planId ->
                viewModel.deleteWorkoutPlan(planId)
            },
            onDismiss = { viewModel.closeSavedPlansDialog() }
        )
    }
}
