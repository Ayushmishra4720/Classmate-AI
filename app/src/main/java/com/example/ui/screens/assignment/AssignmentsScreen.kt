package com.example.ui.screens.assignment

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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Assignment
import com.example.ui.components.EmptyStateView
import com.example.ui.components.LoadingAiState
import com.example.ui.theme.*
import com.example.ui.viewmodel.StudentViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AssignmentsScreen(
    viewModel: StudentViewModel,
    onNavigateToAiHelper: () -> Unit
) {
    val assignments by viewModel.assignments.collectAsStateWithLifecycle()
    val isAiLoading by viewModel.isAssignmentAiLoading.collectAsStateWithLifecycle()
    var selectedFilter by remember { mutableStateOf("ALL") }
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedAssignmentForAi by remember { mutableStateOf<Assignment?>(null) }
    var aiHelpPrompt by remember { mutableStateOf("") }
    var showAiHelpDialog by remember { mutableStateOf(false) }

    val filteredAssignments = when (selectedFilter) {
        "PENDING" -> assignments.filter { it.status == "PENDING" }
        "IN_PROGRESS" -> assignments.filter { it.status == "IN_PROGRESS" }
        "SUBMITTED" -> assignments.filter { it.status == "SUBMITTED" }
        "HIGH_PRIORITY" -> assignments.filter { it.priority == "HIGH" }
        else -> assignments
    }

    val filters = listOf("ALL" to "All Tasks", "PENDING" to "Pending", "IN_PROGRESS" to "In Progress", "SUBMITTED" to "Completed", "HIGH_PRIORITY" to "High Priority")

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                icon = { Icon(Icons.Filled.Add, contentDescription = "Add") },
                text = { Text("New Assignment") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_assignment_fab")
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .testTag("assignments_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // AI Helper Banner Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { onNavigateToAiHelper() },
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
                            Icon(Icons.Filled.SmartToy, contentDescription = null, tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "AI Assignment Assistant",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "Get research outlines, citation guidance & step-by-step structures.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                        Icon(Icons.Filled.ChevronRight, contentDescription = null)
                    }
                }
            }

            // Filter Chips
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(filters) { (key, label) ->
                        FilterChip(
                            selected = selectedFilter == key,
                            onClick = { selectedFilter = key },
                            label = { Text(label, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }
            }

            if (filteredAssignments.isEmpty()) {
                item {
                    EmptyStateView(
                        icon = Icons.Filled.Assignment,
                        title = "No Assignments Found",
                        description = "Add university tasks, problem sets, and lab reports with deadlines.",
                        actionText = "Add Assignment",
                        onAction = { showAddDialog = true }
                    )
                }
            } else {
                items(filteredAssignments) { item ->
                    AssignmentDetailCard(
                        assignment = item,
                        onStatusChange = { newStatus -> viewModel.updateAssignmentStatus(item, newStatus) },
                        onDelete = { viewModel.deleteAssignment(item) },
                        onAskAi = {
                            selectedAssignmentForAi = item
                            aiHelpPrompt = item.description
                            showAiHelpDialog = true
                        }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddAssignmentDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { title, subject, dueDays, priority, desc ->
                val due = System.currentTimeMillis() + dueDays * 24 * 3600 * 1000L
                viewModel.addAssignment(title, subject, due, priority, desc)
                showAddDialog = false
            }
        )
    }

    if (showAiHelpDialog && selectedAssignmentForAi != null) {
        val target = selectedAssignmentForAi!!
        AlertDialog(
            onDismissRequest = { showAiHelpDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.SmartToy, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("AI Guidance for ${target.title}", style = MaterialTheme.typography.titleMedium)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp)
                ) {
                    if (isAiLoading) {
                        LoadingAiState("Analyzing requirements with Gemini...")
                    } else if (target.aiAssistanceSummary.isNotBlank()) {
                        Text(
                            text = target.aiAssistanceSummary,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        OutlinedTextField(
                            value = aiHelpPrompt,
                            onValueChange = { aiHelpPrompt = it },
                            label = { Text("Assignment Details / Requirements") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 4
                        )
                    }
                }
            },
            confirmButton = {
                if (target.aiAssistanceSummary.isBlank()) {
                    Button(
                        onClick = {
                            viewModel.requestAssignmentAiHelp(target, aiHelpPrompt)
                        },
                        enabled = !isAiLoading
                    ) {
                        Text("Generate AI Outline")
                    }
                } else {
                    Button(onClick = { showAiHelpDialog = false }) {
                        Text("Done")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showAiHelpDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun AssignmentDetailCard(
    assignment: Assignment,
    onStatusChange: (String) -> Unit,
    onDelete: () -> Unit,
    onAskAi: () -> Unit
) {
    val dateFormat = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
    val dueDateStr = dateFormat.format(Date(assignment.dueDateMillis))
    val isOverdue = assignment.dueDateMillis < System.currentTimeMillis() && assignment.status != "SUBMITTED"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .testTag("assignment_item_${assignment.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = assignment.subject,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Surface(
                            color = when (assignment.priority) {
                                "HIGH" -> RoseError.copy(alpha = 0.15f)
                                "MEDIUM" -> AmberTertiary.copy(alpha = 0.15f)
                                else -> EmeraldSuccess.copy(alpha = 0.15f)
                            },
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "${assignment.priority} PRIORITY",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = when (assignment.priority) {
                                    "HIGH" -> RoseError
                                    "MEDIUM" -> AmberTertiary
                                    else -> EmeraldSuccess
                                },
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = assignment.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    if (assignment.description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = assignment.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2
                        )
                    }
                }

                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.DeleteOutline, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Event,
                        contentDescription = null,
                        tint = if (isOverdue) RoseError else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isOverdue) "Overdue: $dueDateStr" else "Due: $dueDateStr",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = if (isOverdue) FontWeight.Bold else FontWeight.Normal,
                        color = if (isOverdue) RoseError else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                FilledTonalButton(
                    onClick = onAskAi,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("AI Guidance", style = MaterialTheme.typography.labelSmall)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Status Selector Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val statuses = listOf("PENDING" to "Pending", "IN_PROGRESS" to "In Progress", "SUBMITTED" to "Done")
                statuses.forEach { (statusKey, label) ->
                    val isSelected = assignment.status == statusKey
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent)
                            .clickable { onStatusChange(statusKey) }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddAssignmentDialog(
    onDismiss: () -> Unit,
    onAdd: (title: String, subject: String, dueDays: Int, priority: String, desc: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var daysStr by remember { mutableStateOf("3") }
    var priority by remember { mutableStateOf("HIGH") }
    var desc by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Assignment", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title (e.g. Distributed Consensus Lab)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Subject (e.g. Distributed Systems)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = daysStr,
                        onValueChange = { daysStr = it },
                        label = { Text("Days until Due") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
                Text("Priority Level:", style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("HIGH", "MEDIUM", "LOW").forEach { p ->
                        FilterChip(
                            selected = priority == p,
                            onClick = { priority = p },
                            label = { Text(p, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Instructions / Requirements") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    shape = RoundedCornerShape(10.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val days = daysStr.toIntOrNull() ?: 3
                        onAdd(title, subject.ifBlank { "General" }, days, priority, desc)
                    }
                },
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Save Assignment")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
