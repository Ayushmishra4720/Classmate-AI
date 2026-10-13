package com.example.ui.screens.quiz

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.data.model.QuizItem
import com.example.data.model.QuizQuestionDto
import com.example.ui.components.EmptyStateView
import com.example.ui.components.LoadingAiState
import com.example.ui.theme.*
import com.example.ui.viewmodel.StudentViewModel

@Composable
fun QuizGeneratorScreen(viewModel: StudentViewModel) {
    val quizzes by viewModel.quizzes.collectAsStateWithLifecycle()
    val isGenerating by viewModel.isGeneratingQuiz.collectAsStateWithLifecycle()
    val activeQuiz by viewModel.activeQuiz.collectAsStateWithLifecycle()
    val activeQuestions by viewModel.activeQuizQuestions.collectAsStateWithLifecycle()
    val selectedAnswers by viewModel.userSelectedAnswers.collectAsStateWithLifecycle()

    var topicInput by remember { mutableStateOf("Binary Search Trees & AVL") }
    var subjectInput by remember { mutableStateOf("Data Structures") }
    var difficulty by remember { mutableStateOf("MEDIUM") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("quiz_generator_screen"),
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
                        Icon(Icons.Filled.Quiz, contentDescription = null, tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "AI Adaptive Quiz Generator",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Generate instant exam-level multiple choice questions with explanations.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        if (isGenerating) {
            item {
                LoadingAiState(message = "Gemini is generating high-yield exam questions...")
            }
        } else if (activeQuiz != null && activeQuestions.isNotEmpty()) {
            // Active Quiz Taking / Review View
            item {
                ActiveQuizView(
                    quiz = activeQuiz!!,
                    questions = activeQuestions,
                    selectedAnswers = selectedAnswers,
                    onSelectAnswer = { qIdx, optIdx -> viewModel.selectAnswer(qIdx, optIdx) },
                    onSubmit = { viewModel.submitQuiz() },
                    onClose = { viewModel.loadQuizForReview(activeQuiz!!) }
                )
            }
        } else {
            // Generator Form
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Generate Custom MCQ Exam",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )

                        OutlinedTextField(
                            value = topicInput,
                            onValueChange = { topicInput = it },
                            label = { Text("Topic (e.g. Dynamic Programming, Semaphores)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )

                        OutlinedTextField(
                            value = subjectInput,
                            onValueChange = { subjectInput = it },
                            label = { Text("Subject (e.g. Computer Science)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Text("Select Difficulty Level:", style = MaterialTheme.typography.labelMedium)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("EASY", "MEDIUM", "HARD").forEach { diff ->
                                FilterChip(
                                    selected = difficulty == diff,
                                    onClick = { difficulty = diff },
                                    label = { Text(diff, style = MaterialTheme.typography.labelSmall) }
                                )
                            }
                        }

                        Button(
                            onClick = {
                                if (topicInput.isNotBlank()) {
                                    viewModel.generateQuiz(topicInput, subjectInput, difficulty, 5)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("generate_quiz_button"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Generate 5 MCQs with AI")
                        }
                    }
                }
            }
        }

        // Quiz History Section
        item {
            Text(
                text = "Past Quiz Attempts (${quizzes.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        if (quizzes.isEmpty()) {
            item {
                EmptyStateView(
                    icon = Icons.Filled.Quiz,
                    title = "No Quizzes Attempted",
                    description = "Generate your first AI quiz above to test your mastery before exams."
                )
            }
        } else {
            items(quizzes) { q ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { viewModel.loadQuizForReview(q) },
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
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = q.subject,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Surface(
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = q.difficulty,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = q.topic, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text(
                                text = if (q.isCompleted) "Score: ${q.score} / ${q.totalQuestions} (${(q.score.toDouble() / q.totalQuestions * 100).toInt()}%)" else "Incomplete",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (q.isCompleted) EmeraldSuccess else AmberTertiary,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        IconButton(onClick = { viewModel.deleteQuiz(q) }) {
                            Icon(Icons.Filled.DeleteOutline, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ActiveQuizView(
    quiz: QuizItem,
    questions: List<QuizQuestionDto>,
    selectedAnswers: Map<Int, Int>,
    onSelectAnswer: (Int, Int) -> Unit,
    onSubmit: () -> Unit,
    onClose: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "${quiz.subject} • ${quiz.difficulty}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Text(text = quiz.topic, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                if (quiz.isCompleted) {
                    Surface(
                        color = EmeraldSuccess.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Score: ${quiz.score}/${quiz.totalQuestions}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldSuccess,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Divider()

            questions.forEachIndexed { qIndex, question ->
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Q${qIndex + 1}. ${question.question}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )

                    question.options.forEachIndexed { optIndex, optionText ->
                        val isSelected = selectedAnswers[qIndex] == optIndex
                        val isCorrect = optIndex == question.correctIndex
                        val isSubmitted = quiz.isCompleted

                        QuizOptionRow(
                            optLetter = ('A' + optIndex),
                            optionText = optionText,
                            isSelected = isSelected,
                            isCorrect = isCorrect,
                            isSubmitted = isSubmitted,
                            onClick = { onSelectAnswer(qIndex, optIndex) }
                        )
                    }

                    if (quiz.isCompleted && question.explanation.isNotBlank()) {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "💡 Explanation: ${question.explanation}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                }
            }

            if (!quiz.isCompleted) {
                Button(
                    onClick = onSubmit,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Submit Answers & Grade Quiz")
                }
            }
        }
    }
}

@Composable
fun QuizOptionRow(
    optLetter: Char,
    optionText: String,
    isSelected: Boolean,
    isCorrect: Boolean,
    isSubmitted: Boolean,
    onClick: () -> Unit
) {
    val borderColor = when {
        isSubmitted && isCorrect -> EmeraldSuccess
        isSubmitted && isSelected && !isCorrect -> RoseError
        isSelected -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.outline
    }

    val containerColor = when {
        isSubmitted && isCorrect -> EmeraldSuccess.copy(alpha = 0.15f)
        isSubmitted && isSelected && !isCorrect -> RoseError.copy(alpha = 0.15f)
        isSelected -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.surface
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(containerColor)
            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
            .clickable(enabled = !isSubmitted) { onClick() }
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$optLetter. $optionText",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            if (isSubmitted && isCorrect) {
                Icon(Icons.Filled.CheckCircle, contentDescription = "Correct", tint = EmeraldSuccess, modifier = Modifier.size(18.dp))
            } else if (isSubmitted && isSelected && !isCorrect) {
                Icon(Icons.Filled.Cancel, contentDescription = "Wrong", tint = RoseError, modifier = Modifier.size(18.dp))
            }
        }
    }
}
