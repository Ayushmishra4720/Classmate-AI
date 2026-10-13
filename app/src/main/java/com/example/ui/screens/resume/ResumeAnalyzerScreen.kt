package com.example.ui.screens.resume

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
import com.example.data.model.ResumeAnalysis
import com.example.ui.components.AyushBrandHeader
import com.example.ui.components.EmptyStateView
import com.example.ui.components.LoadingAiState
import com.example.ui.theme.*
import com.example.ui.viewmodel.StudentViewModel

@Composable
fun ResumeAnalyzerScreen(viewModel: StudentViewModel) {
    val analyses by viewModel.resumeAnalyses.collectAsStateWithLifecycle()
    val isAnalyzing by viewModel.isAnalyzingResume.collectAsStateWithLifecycle()
    val activeAnalysis by viewModel.activeResumeAnalysis.collectAsStateWithLifecycle()

    var resumeTextInput by remember {
        mutableStateOf(
            "Ayush | Computer Science Student\n" +
            "Skills: Kotlin, Jetpack Compose, React, TypeScript, FastAPI, PostgreSQL, Python, Git, Docker\n" +
            "Experience: Built full-stack apps with real-time AI and database integration.\n" +
            "Education: B.Tech in CSE (CGPA: 8.85)\n" +
            "Projects: Classmate AI - Full-stack Student Companion app with 16 AI and productivity tools."
        )
    }
    var targetRoleInput by remember { mutableStateOf("AI / Full-Stack Software Engineer") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("resume_analyzer_screen"),
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
                        Icon(Icons.Filled.Description, contentDescription = null, tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "AI Resume & ATS Analyzer",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Extract ATS scores, keyword matching, formatting critique, and impactful bullet rewrites.",
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
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Paste Resume Content", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

                    OutlinedTextField(
                        value = targetRoleInput,
                        onValueChange = { targetRoleInput = it },
                        label = { Text("Target Job Role / Internship Position") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = resumeTextInput,
                        onValueChange = { resumeTextInput = it },
                        label = { Text("Resume Text / Experience / Bullet Points") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 6,
                        shape = RoundedCornerShape(10.dp)
                    )

                    Button(
                        onClick = {
                            if (resumeTextInput.isNotBlank() && targetRoleInput.isNotBlank()) {
                                viewModel.analyzeResume(resumeTextInput, targetRoleInput)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("analyze_resume_button"),
                        enabled = !isAnalyzing && resumeTextInput.isNotBlank() && targetRoleInput.isNotBlank(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Analyze with Gemini ATS Engine")
                    }
                }
            }
        }

        if (isAnalyzing) {
            item {
                LoadingAiState(message = "Gemini is parsing keywords, ATS compatibility, and phrasing impact...")
            }
        }

        // Active Analysis Report
        activeAnalysis?.let { analysis ->
            item {
                ResumeAnalysisCard(analysis = analysis, onDelete = { viewModel.deleteResumeAnalysis(analysis) })
            }
        }

        // Historic Saved Analyses
        if (analyses.isNotEmpty()) {
            item {
                Text("Saved Resume Analysis History", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            items(analyses) { itemAnalysis ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = itemAnalysis.targetRole, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Text(text = "ATS Score: ${itemAnalysis.atsScore}/100", style = MaterialTheme.typography.bodySmall, color = if (itemAnalysis.atsScore >= 75) EmeraldSuccess else AmberTertiary, fontWeight = FontWeight.Bold)
                            }
                            IconButton(onClick = { viewModel.deleteResumeAnalysis(itemAnalysis) }) {
                                Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = RoseError, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ResumeAnalysisCard(analysis: ResumeAnalysis, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("ATS Assessment Report", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Text(analysis.targetRole, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }

                Surface(
                    color = if (analysis.atsScore >= 75) EmeraldSuccess.copy(alpha = 0.15f) else AmberTertiary.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Score: ${analysis.atsScore}/100",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (analysis.atsScore >= 75) EmeraldSuccess else AmberTertiary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            Divider()

            if (analysis.strengths.isNotBlank()) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Key Strengths", style = MaterialTheme.typography.titleSmall, color = EmeraldSuccess, fontWeight = FontWeight.Bold)
                    Text(analysis.strengths, style = MaterialTheme.typography.bodyMedium)
                }
            }

            if (analysis.weaknesses.isNotBlank()) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Areas for Improvement", style = MaterialTheme.typography.titleSmall, color = RoseError, fontWeight = FontWeight.Bold)
                    Text(analysis.weaknesses, style = MaterialTheme.typography.bodyMedium)
                }
            }

            if (analysis.missingKeywords.isNotBlank()) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Recommended ATS Keywords", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Text(analysis.missingKeywords, style = MaterialTheme.typography.bodyMedium)
                }
            }

            if (analysis.suggestions.isNotBlank()) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Actionable Feedback & Suggestions", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Text(analysis.suggestions, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
