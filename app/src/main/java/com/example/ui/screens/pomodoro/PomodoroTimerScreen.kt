package com.example.ui.screens.pomodoro

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AyushBrandHeader
import com.example.ui.theme.*
import com.example.ui.viewmodel.StudentViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PomodoroTimerScreen(viewModel: StudentViewModel) {
    val mode by viewModel.pomodoroMode.collectAsStateWithLifecycle()
    val secondsLeft by viewModel.pomodoroSecondsRemaining.collectAsStateWithLifecycle()
    val isRunning by viewModel.isPomodoroRunning.collectAsStateWithLifecycle()
    val sessions by viewModel.pomodoroSessions.collectAsStateWithLifecycle()

    var selectedTag by remember { mutableStateOf("Data Structures") }
    val subjects = listOf("Data Structures", "Operating Systems", "Networks", "AI & ML", "Exam Prep", "General")

    val totalDurationSeconds = when (mode) {
        "SHORT_BREAK" -> 5 * 60
        "LONG_BREAK" -> 15 * 60
        else -> 25 * 60
    }

    val progress = ((totalDurationSeconds - secondsLeft).toFloat() / totalDurationSeconds).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "pomo_progress")

    val minutes = secondsLeft / 60
    val seconds = secondsLeft % 60
    val formattedTime = String.format("%02d:%02d", minutes, seconds)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("pomodoro_screen"),
        contentPadding = PaddingValues(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            AyushBrandHeader()
        }

        // Mode Selector Chips
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                FilterChip(
                    selected = mode == "FOCUS",
                    onClick = { viewModel.setPomodoroMode("FOCUS") },
                    label = { Text("Focus (25m)") },
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                FilterChip(
                    selected = mode == "SHORT_BREAK",
                    onClick = { viewModel.setPomodoroMode("SHORT_BREAK") },
                    label = { Text("Short Break (5m)") },
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                FilterChip(
                    selected = mode == "LONG_BREAK",
                    onClick = { viewModel.setPomodoroMode("LONG_BREAK") },
                    label = { Text("Long Break (15m)") },
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }

        // Big Circular Dial / Timer Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(230.dp)
                    ) {
                        CircularProgressIndicator(
                            progress = { animatedProgress },
                            modifier = Modifier.fillMaxSize(),
                            strokeWidth = 12.dp,
                            color = when (mode) {
                                "SHORT_BREAK" -> EmeraldSuccess
                                "LONG_BREAK" -> Color(0xFF06B6D4)
                                else -> MaterialTheme.colorScheme.primary
                            },
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = formattedTime,
                                style = MaterialTheme.typography.displayMedium,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = when (mode) {
                                    "SHORT_BREAK" -> "Relax & Hydrate"
                                    "LONG_BREAK" -> "Recharge Energy"
                                    else -> "Deep Work Focus"
                                },
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Controls Row
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedIconButton(
                            onClick = { viewModel.resetPomodoro() },
                            modifier = Modifier.size(52.dp)
                        ) {
                            Icon(Icons.Filled.Refresh, contentDescription = "Reset Timer")
                        }

                        Button(
                            onClick = { viewModel.togglePomodoroTimer(selectedTag) },
                            modifier = Modifier
                                .height(52.dp)
                                .widthIn(min = 140.dp)
                                .testTag("pomodoro_start_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isRunning) RoseError else MaterialTheme.colorScheme.primary
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(
                                if (isRunning) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (isRunning) "Pause" else "Start Focus", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Subject Tag Selection
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Focus Subject Tag:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        subjects.take(3).forEach { subj ->
                            FilterChip(
                                selected = selectedTag == subj,
                                onClick = { selectedTag = subj },
                                label = { Text(subj, style = MaterialTheme.typography.labelSmall) }
                            )
                        }
                    }
                }
            }
        }

        // Logged Focus History
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Focus Log History (${sessions.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                val totalMins = sessions.sumOf { it.durationMinutes }
                Text("Total: ${totalMins}m", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
        }

        if (sessions.isEmpty()) {
            item {
                Text(
                    "No completed focus sessions yet. Complete your first 25m focus sprint!",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }
        } else {
            items(sessions) { sess ->
                val dateFormat = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
                val timeStr = dateFormat.format(Date(sess.completedAt))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldSuccess.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.Timer, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(text = sess.subjectTag, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Text(text = timeStr, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "+${sess.durationMinutes} min",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
