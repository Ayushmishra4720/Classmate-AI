package com.example.ui.screens.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.Assignment
import com.example.data.model.AttendanceSubject
import com.example.ui.components.AyushBrandHeader
import com.example.ui.components.MetricStatCard
import com.example.ui.navigation.Screen
import com.example.ui.theme.*
import com.example.ui.viewmodel.StudentViewModel

@Composable
fun DashboardScreen(
    viewModel: StudentViewModel,
    onNavigateTo: (String) -> Unit
) {
    val profile by viewModel.userProfile.collectAsStateWithLifecycle()
    val subjects by viewModel.attendanceSubjects.collectAsStateWithLifecycle()
    val assignments by viewModel.assignments.collectAsStateWithLifecycle()
    val revisions by viewModel.revisionTasks.collectAsStateWithLifecycle()
    val pomodoroSessions by viewModel.pomodoroSessions.collectAsStateWithLifecycle()

    // Calculate live stats
    val totalAttended = subjects.sumOf { it.attendedClasses }
    val totalClasses = subjects.sumOf { it.totalClasses }
    val overallAttendance = if (totalClasses == 0) 100.0 else (totalAttended.toDouble() / totalClasses) * 100.0
    val pendingAssignmentsCount = assignments.count { it.status != "SUBMITTED" }
    val dueRevisionsCount = revisions.count { !it.isReviewedToday }
    val totalFocusMinutes = pomodoroSessions.sumOf { it.durationMinutes }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Image & Brand Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(20.dp)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.hero_student_banner),
                        contentDescription = "Classmate AI Banner",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color(0xCC0F172A)
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Hello, ${profile?.name ?: "Student"}! 👋",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "${profile?.major ?: "Computer Science"} • Sem ${profile?.semester ?: 6}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFCBD5E1)
                        )
                    }
                }
            }
        }

        // Ayush Brand Banner
        item {
            AyushBrandHeader()
        }

        // Live Real-Time 4 Metrics Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricStatCard(
                        title = "Attendance",
                        value = "${String.format("%.1f", overallAttendance)}%",
                        subtitle = if (overallAttendance >= 75.0) "✓ Safe above 75%" else "⚠ Low Attendance Alert",
                        icon = Icons.Filled.CheckCircle,
                        accentColor = if (overallAttendance >= 75.0) EmeraldSuccess else RoseError,
                        onClick = { onNavigateTo(Screen.Attendance.route) },
                        modifier = Modifier.weight(1f)
                    )
                    MetricStatCard(
                        title = "Pending Tasks",
                        value = "$pendingAssignmentsCount",
                        subtitle = "${assignments.count { it.status == "SUBMITTED" }} submitted",
                        icon = Icons.Filled.Assignment,
                        accentColor = CyanSecondary,
                        onClick = { onNavigateTo(Screen.Assignments.route) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricStatCard(
                        title = "Current CGPA",
                        value = "${profile?.currentCgpa ?: 8.85}",
                        subtitle = "Target: ${profile?.targetCgpa ?: 9.0}",
                        icon = Icons.Filled.Calculate,
                        accentColor = AmberTertiary,
                        onClick = { onNavigateTo(Screen.CgpaCalculator.route) },
                        modifier = Modifier.weight(1f)
                    )
                    MetricStatCard(
                        title = "Focus Time",
                        value = "${totalFocusMinutes / 60}h ${totalFocusMinutes % 60}m",
                        subtitle = "$dueRevisionsCount revisions due",
                        icon = Icons.Filled.Timer,
                        accentColor = VioletAccent,
                        onClick = { onNavigateTo(Screen.PomodoroTimer.route) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // AI Quick Tools Hub (Grid)
        item {
            Text(
                text = "AI Academic Superpowers",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AiFeatureCard(
                        title = "AI Doubt Solver",
                        desc = "Step-by-step solutions",
                        icon = Icons.Filled.Psychology,
                        gradient = listOf(Color(0xFF4F46E5), Color(0xFF6366F1)),
                        onClick = { onNavigateTo(Screen.DoubtSolver.route) },
                        modifier = Modifier.weight(1f)
                    )
                    AiFeatureCard(
                        title = "Notes Summarizer",
                        desc = "Flashcards & Key points",
                        icon = Icons.Filled.MenuBook,
                        gradient = listOf(Color(0xFF0EA5E9), Color(0xFF06B6D4)),
                        onClick = { onNavigateTo(Screen.NotesSummarizer.route) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AiFeatureCard(
                        title = "Quiz Generator",
                        desc = "Custom MCQ tests",
                        icon = Icons.Filled.Quiz,
                        gradient = listOf(Color(0xFF8B5CF6), Color(0xFFA855F7)),
                        onClick = { onNavigateTo(Screen.QuizGenerator.route) },
                        modifier = Modifier.weight(1f)
                    )
                    AiFeatureCard(
                        title = "Assignment Helper",
                        desc = "Outlines & Research",
                        icon = Icons.Filled.SmartToy,
                        gradient = listOf(Color(0xFFF59E0B), Color(0xFFD97706)),
                        onClick = { onNavigateTo(Screen.AssignmentHelper.route) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AiFeatureCard(
                        title = "Study Planner",
                        desc = "Day-by-day exam plan",
                        icon = Icons.Filled.CalendarMonth,
                        gradient = listOf(Color(0xFF10B981), Color(0xFF059669)),
                        onClick = { onNavigateTo(Screen.StudyPlanner.route) },
                        modifier = Modifier.weight(1f)
                    )
                    AiFeatureCard(
                        title = "Resume ATS",
                        desc = "Score & Keywords",
                        icon = Icons.Filled.Description,
                        gradient = listOf(Color(0xFFEC4899), Color(0xFFDB2777)),
                        onClick = { onNavigateTo(Screen.ResumeAnalyzer.route) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Active Subjects Attendance Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Subject Attendance Quick Check",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = { onNavigateTo(Screen.Attendance.route) }) {
                    Text("View All")
                }
            }
        }

        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                items(subjects) { subject ->
                    SubjectMiniCard(
                        subject = subject,
                        onMarkPresent = { viewModel.markAttendance(subject.id, true) },
                        onMarkAbsent = { viewModel.markAttendance(subject.id, false) }
                    )
                }
            }
        }

        // Pending Assignments Due Soon
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Upcoming Assignments",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = { onNavigateTo(Screen.Assignments.route) }) {
                    Text("Manage")
                }
            }
        }

        if (assignments.isEmpty()) {
            item {
                Text(
                    text = "No pending assignments. You're all caught up! 🎉",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(assignments.take(3)) { item ->
                AssignmentCardItem(
                    assignment = item,
                    onStatusToggle = {
                        val next = if (item.status == "PENDING") "IN_PROGRESS" else if (item.status == "IN_PROGRESS") "SUBMITTED" else "PENDING"
                        viewModel.updateAssignmentStatus(item, next)
                    }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun AiFeatureCard(
    title: String,
    desc: String,
    icon: ImageVector,
    gradient: List<Color>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag("ai_feature_card_${title.lowercase().replace(' ', '_')}"),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(colors = gradient))
                .padding(14.dp)
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0x33FFFFFF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFF1F5F9),
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun SubjectMiniCard(
    subject: AttendanceSubject,
    onMarkPresent: () -> Unit,
    onMarkAbsent: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = subject.code,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subject.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${String.format("%.0f", subject.percentage)}%",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (subject.percentage >= 75.0) EmeraldSuccess else RoseError
                )
                Text(
                    text = "${subject.attendedClasses}/${subject.totalClasses}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { (subject.percentage / 100.0).toFloat().coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape),
                color = if (subject.percentage >= 75.0) EmeraldSuccess else RoseError,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilledTonalButton(
                    onClick = onMarkPresent,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("+1 Pres", style = MaterialTheme.typography.labelSmall)
                }
                OutlinedButton(
                    onClick = onMarkAbsent,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("+1 Abs", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Composable
fun AssignmentCardItem(
    assignment: Assignment,
    onStatusToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = assignment.subject,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = assignment.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
                Text(
                    text = "Priority: ${assignment.priority}",
                    style = MaterialTheme.typography.bodySmall,
                    color = when (assignment.priority) {
                        "HIGH" -> RoseError
                        "MEDIUM" -> AmberTertiary
                        else -> EmeraldSuccess
                    }
                )
            }

            Surface(
                color = when (assignment.status) {
                    "SUBMITTED" -> EmeraldSuccess.copy(alpha = 0.15f)
                    "IN_PROGRESS" -> CyanSecondary.copy(alpha = 0.15f)
                    else -> AmberTertiary.copy(alpha = 0.15f)
                },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.clickable { onStatusToggle() }
            ) {
                Text(
                    text = assignment.status,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = when (assignment.status) {
                        "SUBMITTED" -> EmeraldSuccess
                        "IN_PROGRESS" -> CyanSecondary
                        else -> AmberTertiary
                    },
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }
}
