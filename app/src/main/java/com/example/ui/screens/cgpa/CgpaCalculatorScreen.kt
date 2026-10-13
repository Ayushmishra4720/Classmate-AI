package com.example.ui.screens.cgpa

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
import com.example.data.model.CgpaSemester
import com.example.ui.components.EmptyStateView
import com.example.ui.theme.*
import com.example.ui.viewmodel.StudentViewModel

@Composable
fun CgpaCalculatorScreen(viewModel: StudentViewModel) {
    val semesters by viewModel.semesters.collectAsStateWithLifecycle()
    val profile by viewModel.userProfile.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var targetCgpaInput by remember { mutableStateOf((profile?.targetCgpa ?: 9.0).toString()) }

    // Live calculations
    val totalCredits = semesters.sumOf { it.totalCredits }
    val totalCreditPoints = semesters.sumOf { it.totalCredits * it.sgpa }
    val cumulativeCgpa = if (totalCredits > 0) totalCreditPoints / totalCredits else 0.0
    val targetCgpa = targetCgpaInput.toDoubleOrNull() ?: 9.0

    // Remaining required SGPA calculation for next semester (assuming 22 credits)
    val remainingCreditsNextSem = 22.0
    val requiredSgpaForTarget = if (totalCredits > 0) {
        val neededTotalPoints = targetCgpa * (totalCredits + remainingCreditsNextSem)
        val neededPointsNext = neededTotalPoints - totalCreditPoints
        neededPointsNext / remainingCreditsNextSem
    } else targetCgpa

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                icon = { Icon(Icons.Filled.Add, contentDescription = "Add Semester") },
                text = { Text("Add Semester") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_semester_fab")
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .testTag("cgpa_calculator_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Main CGPA Display Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Cumulative GPA (CGPA)",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = String.format("%.2f", cumulativeCgpa),
                                    style = MaterialTheme.typography.headlineLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(AmberTertiary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Filled.EmojiEvents,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

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
                                    Text("Total Credits", style = MaterialTheme.typography.labelSmall)
                                    Text("${totalCredits.toInt()}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                }
                            }
                            Surface(
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Completed Semesters", style = MaterialTheme.typography.labelSmall)
                                    Text("${semesters.size}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Target CGPA Predictor & Simulator Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Target CGPA Simulator",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(Icons.Filled.TrendingUp, contentDescription = null, tint = EmeraldSuccess)
                        }

                        OutlinedTextField(
                            value = targetCgpaInput,
                            onValueChange = { targetCgpaInput = it },
                            label = { Text("Target Goal CGPA (e.g. 9.0)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "To achieve ${String.format("%.2f", targetCgpa)} CGPA:",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (requiredSgpaForTarget in 0.0..10.0) {
                                        "You need an SGPA of ${String.format("%.2f", requiredSgpaForTarget)} in your next semester (22 credits)."
                                    } else if (requiredSgpaForTarget > 10.0) {
                                        "Target ${String.format("%.2f", targetCgpa)} exceeds maximum possible 10.0 SGPA in a single semester. Maintain 9.5+ across upcoming semesters."
                                    } else {
                                        "You have already surpassed your target goal! 🎉"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (requiredSgpaForTarget <= 10.0) MaterialTheme.colorScheme.onSurfaceVariant else RoseError
                                )
                            }
                        }
                    }
                }
            }

            // Semester-wise SGPA Breakdown
            item {
                Text(
                    text = "Semester Breakdown (${semesters.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            if (semesters.isEmpty()) {
                item {
                    EmptyStateView(
                        icon = Icons.Filled.Calculate,
                        title = "No Semesters Recorded",
                        description = "Add your past semesters and SGPA to compute your official CGPA.",
                        actionText = "Add Semester 1",
                        onAction = { showAddDialog = true }
                    )
                }
            } else {
                items(semesters) { sem ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
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
                                Text(text = sem.semesterName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Text(text = "Credits: ${sem.totalCredits.toInt()} • Points: ${String.format("%.1f", sem.totalCredits * sem.sgpa)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = AmberTertiary.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "SGPA: ${String.format("%.2f", sem.sgpa)}",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = AmberTertiary,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                                IconButton(onClick = { viewModel.deleteSemester(sem) }) {
                                    Icon(Icons.Filled.DeleteOutline, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddSemesterDialog(
            nextSemNumber = semesters.size + 1,
            onDismiss = { showAddDialog = false },
            onAdd = { num, name, credits, sgpa ->
                viewModel.addSemester(num, name, credits, sgpa)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun AddSemesterDialog(
    nextSemNumber: Int,
    onDismiss: () -> Unit,
    onAdd: (num: Int, name: String, credits: Double, sgpa: Double) -> Unit
) {
    var semName by remember { mutableStateOf("Semester $nextSemNumber") }
    var creditsStr by remember { mutableStateOf("22") }
    var sgpaStr by remember { mutableStateOf("8.5") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Semester Result", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = semName,
                    onValueChange = { semName = it },
                    label = { Text("Semester Label") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
                OutlinedTextField(
                    value = creditsStr,
                    onValueChange = { creditsStr = it },
                    label = { Text("Total Credits (e.g. 22)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
                OutlinedTextField(
                    value = sgpaStr,
                    onValueChange = { sgpaStr = it },
                    label = { Text("SGPA (e.g. 8.85)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val cr = creditsStr.toDoubleOrNull() ?: 20.0
                    val sg = sgpaStr.toDoubleOrNull() ?: 8.0
                    onAdd(nextSemNumber, semName, cr, sg)
                },
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Add Semester")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
