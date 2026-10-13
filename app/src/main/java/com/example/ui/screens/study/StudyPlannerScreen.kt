package com.example.ui.screens.study

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.data.model.StudyDayDto
import com.example.data.model.StudyPlan
import com.example.ui.components.EmptyStateView
import com.example.ui.components.LoadingAiState
import com.example.ui.theme.*
import com.example.ui.viewmodel.StudentViewModel

@Composable
fun StudyPlannerScreen(viewModel: StudentViewModel) {
    val studyPlans by viewModel.studyPlans.collectAsStateWithLifecycle()
    val isGenerating by viewModel.isGeneratingPlan.collectAsStateWithLifecycle()
    val activePlan by viewModel.activePlan.collectAsStateWithLifecycle()

    var examTitleInput by remember { mutableStateOf("Operating Systems End-Term Exam") }
    var daysRemainingInput by remember { mutableStateOf("7") }
    var dailyHoursInput by remember { mutableStateOf("3.5") }
    var topicsInput by remember { mutableStateOf("Processes & Threads, CPU Scheduling, Synchronization & Semaphores, Deadlocks, Memory Paging & Segmentation, Virtual Memory, File Systems") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("study_planner_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Banner
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
                        Icon(Icons.Filled.CalendarMonth, contentDescription = null, tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "AI Exam Study Planner",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Generates optimal day-by-day revision timetables based on exam deadlines and available study hours.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        if (isGenerating) {
            item {
                LoadingAiState(message = "Gemini is building your personalized exam roadmap...")
            }
        } else if (activePlan != null) {
            val daysList = viewModel.parseStudyDays(activePlan!!.planScheduleJson)
            item {
                ActiveStudyPlanView(
                    plan = activePlan!!,
                    days = daysList,
                    onProgressUpdate = { p -> viewModel.updatePlanProgress(activePlan!!, p) },
                    onClose = { viewModel.selectStudyPlan(activePlan!!) }
                )
            }
        } else {
            // Plan Generator Form
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Create New Study Roadmap",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )

                        OutlinedTextField(
                            value = examTitleInput,
                            onValueChange = { examTitleInput = it },
                            label = { Text("Exam Name (e.g. Midterm Physics)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = daysRemainingInput,
                                onValueChange = { daysRemainingInput = it },
                                label = { Text("Days Left") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            OutlinedTextField(
                                value = dailyHoursInput,
                                onValueChange = { dailyHoursInput = it },
                                label = { Text("Daily Study Hours") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }

                        OutlinedTextField(
                            value = topicsInput,
                            onValueChange = { topicsInput = it },
                            label = { Text("Syllabus / Topics to Cover") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            shape = RoundedCornerShape(10.dp)
                        )

                        Button(
                            onClick = {
                                if (examTitleInput.isNotBlank() && topicsInput.isNotBlank()) {
                                    val days = daysRemainingInput.toIntOrNull() ?: 5
                                    val hours = dailyHoursInput.toDoubleOrNull() ?: 3.0
                                    viewModel.generateStudyPlan(examTitleInput, days, hours, topicsInput)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Generate AI Schedule")
                        }
                    }
                }
            }
        }

        // Saved Plans
        item {
            Text(
                text = "Active Study Roadmaps (${studyPlans.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        if (studyPlans.isEmpty() && activePlan == null) {
            item {
                EmptyStateView(
                    icon = Icons.Filled.CalendarMonth,
                    title = "No Study Plans",
                    description = "Generate an AI study schedule before your upcoming exams."
                )
            }
        } else {
            items(studyPlans) { plan ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { viewModel.selectStudyPlan(plan) },
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
                            Text(text = plan.examTitle, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text(text = "${plan.dailyHours} hrs/day • Progress: ${plan.progressPercentage}%", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { (plan.progressPercentage / 100.0).toFloat().coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth(0.8f)
                                    .height(4.dp)
                                    .clip(CircleShape),
                                color = EmeraldSuccess
                            )
                        }

                        IconButton(onClick = { viewModel.deleteStudyPlan(plan) }) {
                            Icon(Icons.Filled.DeleteOutline, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ActiveStudyPlanView(
    plan: StudyPlan,
    days: List<StudyDayDto>,
    onProgressUpdate: (Int) -> Unit,
    onClose: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Study Roadmap", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Text(text = plan.examTitle, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            }

            Divider()

            days.forEach { dayDto ->
                StudyDayCard(dayDto = dayDto)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text("Update Completion Progress:", style = MaterialTheme.typography.labelMedium)
            Slider(
                value = plan.progressPercentage.toFloat(),
                onValueChange = { onProgressUpdate(it.toInt()) },
                valueRange = 0f..100f
            )
            Text(
                text = "${plan.progressPercentage}% Completed",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = EmeraldSuccess
            )
        }
    }
}

@Composable
fun StudyDayCard(dayDto: StudyDayDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Day ${dayDto.dayNumber}: ${dayDto.focusTopic}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "${dayDto.hoursAllocated} hrs",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = AmberTertiary
                )
            }

            dayDto.tasks.forEach { task ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.RadioButtonUnchecked,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = task, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
