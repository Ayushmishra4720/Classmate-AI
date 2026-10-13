package com.example.ui.screens.tools

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.AyushBrandHeader
import com.example.ui.navigation.Screen
import com.example.ui.theme.*

data class ToolHubItem(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val color: Color,
    val route: String
)

@Composable
fun ToolsHubScreen(onNavigate: (String) -> Unit) {
    val toolItems = listOf(
        ToolHubItem("AI Doubt Solver", "Step-by-step math, coding, and theory solutions", Icons.Filled.Psychology, MaterialTheme.colorScheme.primary, Screen.DoubtSolver.route),
        ToolHubItem("Notes Summarizer", "Condense chapters into key points & flashcards", Icons.Filled.MenuBook, Color(0xFF0EA5E9), Screen.NotesSummarizer.route),
        ToolHubItem("AI Quiz Generator", "Test topic mastery with instant grading", Icons.Filled.Quiz, Color(0xFF8B5CF6), Screen.QuizGenerator.route),
        ToolHubItem("Assignment Helper", "Break down research questions & structure", Icons.Filled.SmartToy, Color(0xFFEC4899), Screen.AssignmentHelper.route),
        ToolHubItem("Study Planner", "Exam roadmap schedule generator", Icons.Filled.CalendarMonth, Color(0xFFF59E0B), Screen.StudyPlanner.route),
        ToolHubItem("Spaced Revision", "Ebbinghaus curve retention scheduler", Icons.Filled.Update, Color(0xFF10B981), Screen.RevisionScheduler.route),
        ToolHubItem("Pomodoro Focus", "Timed intervals with subject tracking", Icons.Filled.Timer, Color(0xFFEF4444), Screen.PomodoroTimer.route),
        ToolHubItem("CGPA Calculator", "10-point Indian University SGPA/CGPA engine", Icons.Filled.Calculate, Color(0xFF6366F1), Screen.CgpaCalculator.route),
        ToolHubItem("Resume ATS Engine", "Keyword match and feedback for internships", Icons.Filled.Description, Color(0xFF14B8A6), Screen.ResumeAnalyzer.route),
        ToolHubItem("Analytics Dashboard", "Attendance, velocity, and study metrics", Icons.Filled.BarChart, Color(0xFF3B82F6), Screen.Analytics.route),
        ToolHubItem("Student Profile", "Academic records and goals", Icons.Filled.Person, Color(0xFF8B5CF6), Screen.Profile.route),
        ToolHubItem("Settings", "Alert preferences & app configuration", Icons.Filled.Settings, Color(0xFF64748B), Screen.Settings.route)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("tools_hub_screen")
    ) {
        AyushBrandHeader()

        Spacer(modifier = Modifier.height(12.dp))

        Text("All AI & Productivity Tools", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text("16 Integrated tools designed exclusively for students", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(modifier = Modifier.height(12.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(toolItems) { tool ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onNavigate(tool.route) }
                        .testTag("tool_card_${tool.route}"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(tool.color.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(tool.icon, contentDescription = null, tint = tool.color, modifier = Modifier.size(22.dp))
                        }

                        Text(tool.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text(tool.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
                    }
                }
            }
        }
    }
}
