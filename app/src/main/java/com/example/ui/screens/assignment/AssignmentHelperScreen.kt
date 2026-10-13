package com.example.ui.screens.assignment

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
import com.example.data.model.Assignment
import com.example.ui.components.LoadingAiState
import com.example.ui.theme.*
import com.example.ui.viewmodel.StudentViewModel

@Composable
fun AssignmentHelperScreen(viewModel: StudentViewModel) {
    val assignments by viewModel.assignments.collectAsStateWithLifecycle()
    val isAiLoading by viewModel.isAssignmentAiLoading.collectAsStateWithLifecycle()

    var customTitle by remember { mutableStateOf("") }
    var customSubject by remember { mutableStateOf("Computer Science") }
    var customPrompt by remember { mutableStateOf("") }
    var aiResult by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("assignment_helper_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Banner
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
                        Icon(Icons.Filled.SmartToy, contentDescription = null, tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "AI Assignment Assistant",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Generate comprehensive research outlines, methodologies, and structured writing guides.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        // Input Form
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Describe Assignment or Problem Statement",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = customTitle,
                        onValueChange = { customTitle = it },
                        label = { Text("Assignment Topic / Project Title") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = customSubject,
                        onValueChange = { customSubject = it },
                        label = { Text("Subject Domain") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = customPrompt,
                        onValueChange = { customPrompt = it },
                        label = { Text("Requirements, Questions, or Deliverables") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 4,
                        shape = RoundedCornerShape(10.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                customTitle = "Design and Simulation of 4-bit ALU"
                                customSubject = "Computer Architecture"
                                customPrompt = "Design a 4-bit Arithmetic Logic Unit with operations for Addition, Subtraction, AND, OR, XOR. Outline required logic gates, multiplexer selection lines, truth tables, and Verilog implementation guidelines."
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Try Sample", style = MaterialTheme.typography.labelSmall)
                        }

                        Button(
                            onClick = {
                                if (customTitle.isNotBlank()) {
                                    val dummyAssignment = Assignment(title = customTitle, subject = customSubject, dueDateMillis = 0, priority = "HIGH", description = customPrompt)
                                    viewModel.requestAssignmentAiHelp(dummyAssignment, customPrompt)
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            enabled = customTitle.isNotBlank() && !isAiLoading
                        ) {
                            Icon(Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Get AI Guide")
                        }
                    }
                }
            }
        }

        if (isAiLoading) {
            item {
                LoadingAiState(message = "Gemini is assembling comprehensive assignment breakdown...")
            }
        }

        // Active Tracked Assignments Quick Assistant
        if (assignments.isNotEmpty()) {
            item {
                Text(
                    text = "Quick Assistant for Enrolled Assignments",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            items(assignments) { a ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = a.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Text(text = a.subject, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                            }
                            Button(
                                onClick = {
                                    customTitle = a.title
                                    customSubject = a.subject
                                    customPrompt = a.description
                                    viewModel.requestAssignmentAiHelp(a, a.description)
                                },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("Get Guide", style = MaterialTheme.typography.labelSmall)
                            }
                        }

                        if (a.aiAssistanceSummary.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Divider()
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = a.aiAssistanceSummary,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}
