package com.example.ui.screens.attendance

import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AttendanceSubject
import com.example.ui.components.EmptyStateView
import com.example.ui.theme.*
import com.example.ui.viewmodel.StudentViewModel

@Composable
fun AttendanceTrackerScreen(viewModel: StudentViewModel) {
    val subjects by viewModel.attendanceSubjects.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }

    // Summary calculations
    val totalAttended = subjects.sumOf { it.attendedClasses }
    val totalClasses = subjects.sumOf { it.totalClasses }
    val overallPercentage = if (totalClasses == 0) 100.0 else (totalAttended.toDouble() / totalClasses) * 100.0
    val safeSubjectsCount = subjects.count { it.percentage >= it.targetPercentage }
    val criticalSubjectsCount = subjects.count { it.percentage < it.targetPercentage }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                icon = { Icon(Icons.Filled.Add, contentDescription = "Add Subject") },
                text = { Text("Add Subject") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_subject_fab")
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .testTag("attendance_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Summary Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (overallPercentage >= 75.0) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Overall Attendance",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "${String.format("%.1f", overallPercentage)}%",
                                    style = MaterialTheme.typography.headlineLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (overallPercentage >= 75.0) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(if (overallPercentage >= 75.0) EmeraldSuccess else RoseError),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    if (overallPercentage >= 75.0) Icons.Filled.CheckCircle else Icons.Filled.Warning,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Classes Attended", style = MaterialTheme.typography.labelSmall)
                                    Text("$totalAttended / $totalClasses", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                }
                            }
                            Surface(
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Safe Subjects", style = MaterialTheme.typography.labelSmall)
                                    Text("$safeSubjectsCount / ${subjects.size}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = EmeraldSuccess)
                                }
                            }
                        }
                    }
                }
            }

            // Subject List Section
            item {
                Text(
                    text = "Enrolled Subjects & Bunk Criteria",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            if (subjects.isEmpty()) {
                item {
                    EmptyStateView(
                        icon = Icons.Filled.CheckCircle,
                        title = "No Subjects Added Yet",
                        description = "Add your university courses to track attendance and get real-time 75% criteria alerts.",
                        actionText = "Add First Subject",
                        onAction = { showAddDialog = true }
                    )
                }
            } else {
                items(subjects) { subject ->
                    AttendanceSubjectCard(
                        subject = subject,
                        onMarkPresent = { viewModel.markAttendance(subject.id, true) },
                        onMarkAbsent = { viewModel.markAttendance(subject.id, false) },
                        onDelete = { viewModel.deleteSubject(subject) }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddSubjectDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { name, code, attended, total, target, prof, room ->
                viewModel.addSubject(name, code, attended, total, target, prof, room, "#4F46E5")
                showAddDialog = false
            }
        )
    }
}

@Composable
fun AttendanceSubjectCard(
    subject: AttendanceSubject,
    onMarkPresent: () -> Unit,
    onMarkAbsent: () -> Unit,
    onDelete: () -> Unit
) {
    val isSafe = subject.percentage >= subject.targetPercentage
    val safeBunks = subject.safeBunks
    val neededToAttend = subject.requiredToAttend

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .testTag("attendance_card_${subject.code}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = subject.code,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = subject.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (subject.professor.isNotBlank() || subject.room.isNotBlank()) {
                        Text(
                            text = listOfNotNull(subject.professor.takeIf { it.isNotBlank() }, subject.room.takeIf { it.isNotBlank() }).joinToString(" • "),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.DeleteOutline, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress and Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "${String.format("%.1f", subject.percentage)}%",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isSafe) EmeraldSuccess else RoseError
                )
                Text(
                    text = "${subject.attendedClasses} / ${subject.totalClasses} classes attended",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { (subject.percentage / 100.0).toFloat().coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape),
                color = if (isSafe) EmeraldSuccess else RoseError,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Bunk Calculator Advice Banner
            Surface(
                color = if (isSafe) EmeraldSuccess.copy(alpha = 0.1f) else RoseError.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        if (isSafe) Icons.Filled.BeachAccess else Icons.Filled.Warning,
                        contentDescription = null,
                        tint = if (isSafe) EmeraldSuccess else RoseError,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isSafe) {
                            if (safeBunks > 0) "You can safely bunk $safeBunks more class${if (safeBunks > 1) "es" else ""} without dropping below ${subject.targetPercentage.toInt()}%."
                            else "On the edge! Do not miss the next class."
                        } else {
                            "Must attend next $neededToAttend class${if (neededToAttend > 1) "es" else ""} consecutively to reach ${subject.targetPercentage.toInt()}%."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = if (isSafe) EmeraldSuccess else RoseError
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Mark Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onMarkPresent,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_present_${subject.code}"),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Present")
                }

                OutlinedButton(
                    onClick = onMarkAbsent,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_absent_${subject.code}"),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = RoseError),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Filled.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Absent")
                }
            }
        }
    }
}

@Composable
fun AddSubjectDialog(
    onDismiss: () -> Unit,
    onAdd: (name: String, code: String, attended: Int, total: Int, target: Double, prof: String, room: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var attendedStr by remember { mutableStateOf("0") }
    var totalStr by remember { mutableStateOf("0") }
    var targetStr by remember { mutableStateOf("75") }
    var professor by remember { mutableStateOf("") }
    var room by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Subject", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Subject Name (e.g. Algorithms)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it },
                    label = { Text("Subject Code (e.g. CS301)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = attendedStr,
                        onValueChange = { attendedStr = it },
                        label = { Text("Attended") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = totalStr,
                        onValueChange = { totalStr = it },
                        label = { Text("Total") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
                OutlinedTextField(
                    value = professor,
                    onValueChange = { professor = it },
                    label = { Text("Professor (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
                OutlinedTextField(
                    value = room,
                    onValueChange = { room = it },
                    label = { Text("Room / Lab (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val att = attendedStr.toIntOrNull() ?: 0
                        val tot = totalStr.toIntOrNull() ?: 0
                        val tgt = targetStr.toDoubleOrNull() ?: 75.0
                        onAdd(name, code.ifBlank { name.take(5).uppercase() }, att, tot, tgt, professor, room)
                    }
                },
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Add Subject")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
