package com.example.ui.screens.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AyushBrandHeader
import com.example.ui.components.MetricStatCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.StudentViewModel

@Composable
fun AnalyticsDashboardScreen(viewModel: StudentViewModel) {
    val profile by viewModel.userProfile.collectAsStateWithLifecycle()
    val subjects by viewModel.attendanceSubjects.collectAsStateWithLifecycle()
    val assignments by viewModel.assignments.collectAsStateWithLifecycle()
    val quizzes by viewModel.quizzes.collectAsStateWithLifecycle()
    val pomodoroSessions by viewModel.pomodoroSessions.collectAsStateWithLifecycle()
    val revisionTasks by viewModel.revisionTasks.collectAsStateWithLifecycle()

    val totalClasses = subjects.sumOf { it.totalClasses }
    val totalAttended = subjects.sumOf { it.attendedClasses }
    val avgAttendance = if (totalClasses > 0) (totalAttended.toDouble() / totalClasses * 100).toInt() else 85

    val completedAssignments = assignments.count { it.status == "SUBMITTED" }
    val assignmentRate = if (assignments.isNotEmpty()) (completedAssignments.toDouble() / assignments.size * 100).toInt() else 100

    val quizAvg = if (quizzes.isNotEmpty()) {
        val totalScore = quizzes.sumOf { it.score }
        val totalQuestions = quizzes.sumOf { it.totalQuestions }
        if (totalQuestions > 0) (totalScore.toDouble() / totalQuestions * 100).toInt() else 0
    } else 0

    val totalFocusMins = pomodoroSessions.sumOf { it.durationMinutes }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("analytics_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            AyushBrandHeader()
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.BarChart, contentDescription = null, tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Student Performance Analytics",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Comprehensive real-time telemetry across academic and productivity dimensions.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        // 2x2 Metric Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricStatCard(
                        title = "Overall Attendance",
                        value = "$avgAttendance%",
                        subtitle = "$totalAttended / $totalClasses classes",
                        icon = Icons.Filled.CheckCircle,
                        accentColor = if (avgAttendance >= 75) EmeraldSuccess else RoseError,
                        modifier = Modifier.weight(1f)
                    )
                    MetricStatCard(
                        title = "Current CGPA",
                        value = "${profile?.currentCgpa ?: 8.85}",
                        subtitle = "Target: ${profile?.targetCgpa ?: 9.0}",
                        icon = Icons.Filled.School,
                        accentColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricStatCard(
                        title = "Quiz Mastery",
                        value = "$quizAvg%",
                        subtitle = "${quizzes.size} exams taken",
                        icon = Icons.Filled.Quiz,
                        accentColor = AmberTertiary,
                        modifier = Modifier.weight(1f)
                    )
                    MetricStatCard(
                        title = "Total Focus Time",
                        value = "${totalFocusMins}m",
                        subtitle = "${pomodoroSessions.size} focus sprints",
                        icon = Icons.Filled.Timer,
                        accentColor = Color(0xFF06B6D4),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Attendance Subject Breakdown Bars
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Subject Attendance Health", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                    subjects.forEach { subj ->
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = subj.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                                Text(
                                    text = "${subj.percentage.toInt()}% (${subj.attendedClasses}/${subj.totalClasses})",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (subj.percentage >= subj.targetPercentage) EmeraldSuccess else RoseError
                                )
                            }
                            LinearProgressIndicator(
                                progress = { (subj.percentage / 100.0).toFloat().coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(CircleShape),
                                color = if (subj.percentage >= subj.targetPercentage) EmeraldSuccess else RoseError,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Productivity & Assignment Breakdown
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Assignment Completion Velocity", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Submitted Assignments", style = MaterialTheme.typography.bodyMedium)
                        Text("$completedAssignments / ${assignments.size} ($assignmentRate%)", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = EmeraldSuccess)
                    }

                    LinearProgressIndicator(
                        progress = { (assignmentRate / 100f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(CircleShape),
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Spaced Revision Mastered", style = MaterialTheme.typography.bodyMedium)
                        val mastered = revisionTasks.count { it.masteryLevel >= 4 }
                        Text("$mastered / ${revisionTasks.size}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = AmberTertiary)
                    }
                }
            }
        }
    }
}
