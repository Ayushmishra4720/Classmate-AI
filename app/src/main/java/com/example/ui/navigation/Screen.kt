package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    // Bottom Nav Core Hubs
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Filled.Dashboard, Icons.Outlined.Dashboard, "nav_dashboard")
    object DoubtSolver : Screen("doubt_solver", "AI Doubt", Icons.Filled.Psychology, Icons.Outlined.Psychology, "nav_doubt_solver")
    object Attendance : Screen("attendance", "Attendance", Icons.Filled.CheckCircle, Icons.Outlined.CheckCircle, "nav_attendance")
    object Assignments : Screen("assignments", "Assignments", Icons.Filled.Assignment, Icons.Outlined.Assignment, "nav_assignments")
    object ToolsHub : Screen("tools_hub", "AI Tools", Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome, "nav_tools_hub")

    // Extended Feature Screens
    object NotesSummarizer : Screen("notes_summarizer", "Notes Summarizer", Icons.Filled.MenuBook, Icons.Outlined.MenuBook, "nav_notes_summarizer")
    object QuizGenerator : Screen("quiz_generator", "Quiz Generator", Icons.Filled.Quiz, Icons.Outlined.Quiz, "nav_quiz_generator")
    object AssignmentHelper : Screen("assignment_helper", "AI Assignment Helper", Icons.Filled.SmartToy, Icons.Outlined.SmartToy, "nav_assignment_helper")
    object StudyPlanner : Screen("study_planner", "Study Planner", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth, "nav_study_planner")
    object RevisionScheduler : Screen("revision_scheduler", "Spaced Revision", Icons.Filled.Update, Icons.Outlined.Update, "nav_revision_scheduler")
    object CgpaCalculator : Screen("cgpa_calculator", "CGPA & SGPA", Icons.Filled.Calculate, Icons.Outlined.Calculate, "nav_cgpa_calculator")
    object PomodoroTimer : Screen("pomodoro_timer", "Pomodoro Focus", Icons.Filled.Timer, Icons.Outlined.Timer, "nav_pomodoro_timer")
    object Analytics : Screen("analytics", "Analytics", Icons.Filled.BarChart, Icons.Outlined.BarChart, "nav_analytics")
    object ResumeAnalyzer : Screen("resume_analyzer", "Resume ATS", Icons.Filled.Description, Icons.Outlined.Description, "nav_resume_analyzer")
    object Notifications : Screen("notifications", "Notifications", Icons.Filled.Notifications, Icons.Outlined.Notifications, "nav_notifications")
    object Profile : Screen("profile", "Student Profile", Icons.Filled.Person, Icons.Outlined.Person, "nav_profile")
    object Settings : Screen("settings", "Settings", Icons.Filled.Settings, Icons.Outlined.Settings, "nav_settings")
    object Auth : Screen("auth", "Sign In", Icons.Filled.Lock, Icons.Outlined.Lock, "nav_auth")
}
