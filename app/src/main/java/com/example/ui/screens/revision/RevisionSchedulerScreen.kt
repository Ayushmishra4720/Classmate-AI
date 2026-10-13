package com.example.ui.screens.revision

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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.RevisionTask
import com.example.ui.components.EmptyStateView
import com.example.ui.theme.*
import com.example.ui.viewmodel.StudentViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun RevisionSchedulerScreen(viewModel: StudentViewModel) {
    val tasks by viewModel.revisionTasks.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }

    val dueToday = tasks.filter { !it.isReviewedToday }
    val mastered = tasks.filter { it.masteryLevel >= 4 }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                icon = { Icon(Icons.Filled.Add, contentDescription = "Add") },
                text = { Text("Add Revision Topic") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_revision_fab")
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .testTag("revision_scheduler_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero
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
                            Icon(Icons.Filled.Update, contentDescription = null, tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Spaced Repetition Scheduler",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "Beats the Ebbinghaus Forgetting Curve with timed reviews: 1, 3, 7, 14, and 30 days.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

            // Stats row
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Due Today", style = MaterialTheme.typography.labelSmall)
                            Text("${dueToday.size}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = if (dueToday.isNotEmpty()) AmberTertiary else EmeraldSuccess)
                        }
                    }
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Mastered Topics", style = MaterialTheme.typography.labelSmall)
                            Text("${mastered.size}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = EmeraldSuccess)
                        }
                    }
                }
            }

            // Topics Due Section
            item {
                Text(
                    text = "Scheduled Revisions (${tasks.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            if (tasks.isEmpty()) {
                item {
                    EmptyStateView(
                        icon = Icons.Filled.Update,
                        title = "No Revision Topics Added",
                        description = "Add important concepts you want to permanently retain in long-term memory.",
                        actionText = "Add Topic",
                        onAction = { showAddDialog = true }
                    )
                }
            } else {
                items(tasks) { task ->
                    RevisionTaskCard(
                        task = task,
                        onRemembered = { viewModel.reviewTask(task, true) },
                        onForgot = { viewModel.reviewTask(task, false) },
                        onDelete = { viewModel.deleteRevisionTask(task) }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddRevisionDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { topic, subj, notes ->
                viewModel.addRevisionTopic(topic, subj, notes)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun RevisionTaskCard(
    task: RevisionTask,
    onRemembered: () -> Unit,
    onForgot: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    val nextReviewStr = dateFormat.format(Date(task.nextReviewDate))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
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
                    Text(text = task.subject, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text(text = task.topicName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    if (task.notes.isNotBlank()) {
                        Text(text = task.notes, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.DeleteOutline, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Stage ${task.intervalStage}/5 • Next: $nextReviewStr",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Mastery: ${task.masteryLevel}/5 ⭐",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = AmberTertiary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { (task.intervalStage / 5f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onRemembered,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Remembered")
                }
                OutlinedButton(
                    onClick = onForgot,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = RoseError),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Forgot (Reset)")
                }
            }
        }
    }
}

@Composable
fun AddRevisionDialog(
    onDismiss: () -> Unit,
    onAdd: (topic: String, subject: String, notes: String) -> Unit
) {
    var topic by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Spaced Revision Topic", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = topic,
                    onValueChange = { topic = it },
                    label = { Text("Topic Name (e.g. Master Theorem)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Subject (e.g. Algorithms)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Quick Memory Hooks / Formulas (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    shape = RoundedCornerShape(10.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (topic.isNotBlank()) {
                        onAdd(topic, subject.ifBlank { "General" }, notes)
                    }
                },
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Schedule Topic")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
