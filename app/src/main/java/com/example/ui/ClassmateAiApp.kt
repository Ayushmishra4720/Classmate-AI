package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.example.ui.navigation.Screen
import com.example.ui.screens.analytics.AnalyticsDashboardScreen
import com.example.ui.screens.assignment.AssignmentHelperScreen
import com.example.ui.screens.assignment.AssignmentsScreen
import com.example.ui.screens.attendance.AttendanceTrackerScreen
import com.example.ui.screens.auth.AuthScreen
import com.example.ui.screens.cgpa.CgpaCalculatorScreen
import com.example.ui.screens.dashboard.DashboardScreen
import com.example.ui.screens.doubt.DoubtSolverScreen
import com.example.ui.screens.notes.NotesSummarizerScreen
import com.example.ui.screens.notifications.NotificationsScreen
import com.example.ui.screens.pomodoro.PomodoroTimerScreen
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.quiz.QuizGeneratorScreen
import com.example.ui.screens.resume.ResumeAnalyzerScreen
import com.example.ui.screens.revision.RevisionSchedulerScreen
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.screens.study.StudyPlannerScreen
import com.example.ui.screens.tools.ToolsHubScreen
import com.example.ui.theme.*
import com.example.ui.viewmodel.StudentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassmateAiApp(viewModel: StudentViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Dashboard.route

    val snackbarHostState = remember { SnackbarHostState() }
    val unreadNotifs by viewModel.unreadNotificationCount.collectAsStateWithLifecycle(0)

    LaunchedEffect(Unit) {
        viewModel.userMessage.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    val bottomNavItems = listOf(
        Screen.Dashboard,
        Screen.DoubtSolver,
        Screen.Attendance,
        Screen.Assignments,
        Screen.ToolsHub
    )

    val showBottomBar = bottomNavItems.any { it.route == currentRoute } ||
            currentRoute in listOf(
                Screen.NotesSummarizer.route,
                Screen.QuizGenerator.route,
                Screen.AssignmentHelper.route,
                Screen.StudyPlanner.route,
                Screen.RevisionScheduler.route,
                Screen.CgpaCalculator.route,
                Screen.PomodoroTimer.route,
                Screen.Analytics.route,
                Screen.ResumeAnalyzer.route,
                Screen.Notifications.route,
                Screen.Profile.route,
                Screen.Settings.route
            )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (currentRoute != Screen.Auth.route) {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "Classmate AI",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "One App. Every Student Need.",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    navigationIcon = {
                        if (!bottomNavItems.any { it.route == currentRoute }) {
                            IconButton(
                                onClick = { navController.popBackStack() },
                                modifier = Modifier.testTag("nav_back_button")
                            ) {
                                Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                            }
                        } else {
                            IconButton(
                                onClick = { navController.navigate(Screen.Profile.route) },
                                modifier = Modifier.testTag("profile_icon_button")
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("A", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                            }
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { navController.navigate(Screen.Notifications.route) },
                            modifier = Modifier.testTag("notifications_icon_button")
                        ) {
                            BadgedBox(
                                badge = {
                                    if (unreadNotifs > 0) {
                                        Badge(
                                            containerColor = RoseError,
                                            contentColor = Color.White
                                        ) {
                                            Text("$unreadNotifs")
                                        }
                                    }
                                }
                            ) {
                                Icon(Icons.Outlined.Notifications, contentDescription = "Notifications")
                            }
                        }

                        IconButton(
                            onClick = { navController.navigate(Screen.Settings.route) },
                            modifier = Modifier.testTag("settings_icon_button")
                        ) {
                            Icon(Icons.Outlined.Settings, contentDescription = "Settings")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        },
        bottomBar = {
            if (showBottomBar && currentRoute != Screen.Auth.route) {
                NavigationBar(
                    modifier = Modifier.testTag("bottom_nav_bar"),
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp
                ) {
                    bottomNavItems.forEach { screen ->
                        val selected = currentRoute == screen.route
                        NavigationBarItem(
                            modifier = Modifier.testTag(screen.testTag),
                            selected = selected,
                            onClick = {
                                if (currentRoute != screen.route) {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (selected) screen.selectedIcon else screen.unselectedIcon,
                                    contentDescription = screen.title
                                )
                            },
                            label = {
                                Text(
                                    text = screen.title,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Core Hub 1: Dashboard
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    viewModel = viewModel,
                    onNavigateTo = { route -> navController.navigate(route) }
                )
            }

            // Core Hub 2: AI Doubt Solver
            composable(Screen.DoubtSolver.route) {
                DoubtSolverScreen(viewModel = viewModel)
            }

            // Core Hub 3: Attendance Tracker
            composable(Screen.Attendance.route) {
                AttendanceTrackerScreen(viewModel = viewModel)
            }

            // Core Hub 4: Assignments Tracker
            composable(Screen.Assignments.route) {
                AssignmentsScreen(
                    viewModel = viewModel,
                    onNavigateToAiHelper = { navController.navigate(Screen.AssignmentHelper.route) }
                )
            }

            // Core Hub 5: Tools Grid Hub
            composable(Screen.ToolsHub.route) {
                ToolsHubScreen(onNavigate = { route -> navController.navigate(route) })
            }

            // Extended Feature 6: Notes Summarizer
            composable(Screen.NotesSummarizer.route) {
                NotesSummarizerScreen(viewModel = viewModel)
            }

            // Extended Feature 7: Quiz Generator
            composable(Screen.QuizGenerator.route) {
                QuizGeneratorScreen(viewModel = viewModel)
            }

            // Extended Feature 8: AI Assignment Helper
            composable(Screen.AssignmentHelper.route) {
                AssignmentHelperScreen(viewModel = viewModel)
            }

            // Extended Feature 9: Study Planner
            composable(Screen.StudyPlanner.route) {
                StudyPlannerScreen(viewModel = viewModel)
            }

            // Extended Feature 10: Spaced Revision
            composable(Screen.RevisionScheduler.route) {
                RevisionSchedulerScreen(viewModel = viewModel)
            }

            // Extended Feature 11: CGPA Calculator
            composable(Screen.CgpaCalculator.route) {
                CgpaCalculatorScreen(viewModel = viewModel)
            }

            // Extended Feature 12: Pomodoro Focus
            composable(Screen.PomodoroTimer.route) {
                PomodoroTimerScreen(viewModel = viewModel)
            }

            // Extended Feature 13: Analytics Dashboard
            composable(Screen.Analytics.route) {
                AnalyticsDashboardScreen(viewModel = viewModel)
            }

            // Extended Feature 14: Resume ATS Analyzer
            composable(Screen.ResumeAnalyzer.route) {
                ResumeAnalyzerScreen(viewModel = viewModel)
            }

            // Extended Feature 15: Notifications
            composable(Screen.Notifications.route) {
                NotificationsScreen(viewModel = viewModel)
            }

            // Extended Feature 16: Profile
            composable(Screen.Profile.route) {
                ProfileScreen(viewModel = viewModel)
            }

            // Settings
            composable(Screen.Settings.route) {
                SettingsScreen(
                    viewModel = viewModel,
                    onNavigateToAuth = { navController.navigate(Screen.Auth.route) }
                )
            }

            // Auth
            composable(Screen.Auth.route) {
                AuthScreen(
                    viewModel = viewModel,
                    onAuthSuccess = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Auth.route) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
