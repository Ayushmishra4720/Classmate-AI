package com.example.ui.screens.notes

import androidx.compose.animation.AnimatedVisibility
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
import com.example.data.model.FlashcardItem
import com.example.data.model.NotesSummary
import com.example.ui.components.EmptyStateView
import com.example.ui.components.LoadingAiState
import com.example.ui.theme.*
import com.example.ui.viewmodel.StudentViewModel

@Composable
fun NotesSummarizerScreen(viewModel: StudentViewModel) {
    val summaries by viewModel.notesSummaries.collectAsStateWithLifecycle()
    val isSummarizing by viewModel.isSummarizing.collectAsStateWithLifecycle()
    val activeSummary by viewModel.activeSummary.collectAsStateWithLifecycle()

    var titleInput by remember { mutableStateOf("") }
    var subjectInput by remember { mutableStateOf("Computer Science") }
    var contentInput by remember { mutableStateOf("") }
    var isCreatingNew by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("notes_summarizer_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
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
                        Icon(Icons.Filled.MenuBook, contentDescription = null, tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "AI Notes Synthesizer",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Transforms raw lecture transcripts into high-yield summaries & smart flashcards.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        // Input or Active Summary View
        if (isSummarizing) {
            item {
                LoadingAiState(message = "Gemini is distilling your notes into key takeaways and flashcards...")
            }
        } else if (activeSummary != null) {
            item {
                ActiveSummaryDetail(
                    summary = activeSummary!!,
                    flashcards = viewModel.parseFlashcards(activeSummary!!.flashcardsJson),
                    onClose = { viewModel.selectNotesSummary(null as NotesSummary? ?: return@ActiveSummaryDetail) },
                    onDelete = { viewModel.deleteNotesSummary(activeSummary!!) }
                )
            }
        } else {
            // New Note Input Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Summarize New Lecture / Topic",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )

                        OutlinedTextField(
                            value = titleInput,
                            onValueChange = { titleInput = it },
                            label = { Text("Topic / Title (e.g. Memory Management & Paging)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )

                        OutlinedTextField(
                            value = subjectInput,
                            onValueChange = { subjectInput = it },
                            label = { Text("Subject (e.g. Operating Systems)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )

                        OutlinedTextField(
                            value = contentInput,
                            onValueChange = { contentInput = it },
                            label = { Text("Paste your notes / textbook paragraphs...") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 5,
                            shape = RoundedCornerShape(10.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    titleInput = "TCP vs UDP Comparison"
                                    subjectInput = "Computer Networks"
                                    contentInput = "TCP is a connection-oriented, reliable protocol with sequence numbers, acknowledgements, flow control (sliding window), and congestion control (slow start, AIMD). UDP is connectionless, unreliable, lightweight, and suitable for real-time applications like video streaming and VoIP where low latency matters more than packet loss."
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Try Sample", style = MaterialTheme.typography.labelSmall)
                            }

                            Button(
                                onClick = {
                                    if (contentInput.isNotBlank()) {
                                        viewModel.summarizeNotes(
                                            title = titleInput.ifBlank { "Untitled Note" },
                                            subject = subjectInput.ifBlank { "General" },
                                            content = contentInput
                                        )
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                enabled = contentInput.isNotBlank()
                            ) {
                                Icon(Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Summarize")
                            }
                        }
                    }
                }
            }
        }

        // Saved Summaries List
        item {
            Text(
                text = "Saved Summaries & Flashcard Decks (${summaries.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        if (summaries.isEmpty() && activeSummary == null) {
            item {
                EmptyStateView(
                    icon = Icons.Filled.MenuBook,
                    title = "No Summaries Yet",
                    description = "Paste any lecture notes above to generate structured summaries and flashcards."
                )
            }
        } else {
            items(summaries) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { viewModel.selectNotesSummary(item) },
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
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = item.subject,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = item.summaryText.take(90) + "...",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(onClick = { viewModel.deleteNotesSummary(item) }) {
                            Icon(Icons.Filled.DeleteOutline, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ActiveSummaryDetail(
    summary: NotesSummary,
    flashcards: List<FlashcardItem>,
    onClose: () -> Unit,
    onDelete: () -> Unit
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
                    Text(text = summary.subject, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Text(text = summary.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                IconButton(onClick = onClose) {
                    Icon(Icons.Filled.Close, contentDescription = "Close")
                }
            }

            Divider()

            Text(text = "Executive Summary", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text(text = summary.summaryText, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)

            if (summary.keyTakeaways.isNotBlank()) {
                Text(text = "Key Takeaways", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                Text(text = summary.keyTakeaways, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
            }

            if (flashcards.isNotEmpty()) {
                Text(text = "Interactive Flashcards (${flashcards.size})", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = AmberTertiary)
                flashcards.forEachIndexed { idx, card ->
                    FlashcardInteractiveView(index = idx + 1, flashcard = card)
                }
            }
        }
    }
}

@Composable
fun FlashcardInteractiveView(index: Int, flashcard: FlashcardItem) {
    var isRevealed by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { isRevealed = !isRevealed },
        colors = CardDefaults.cardColors(
            containerColor = if (isRevealed) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Card #$index",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = if (isRevealed) "Tap to hide" else "Tap to reveal answer",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Q: ${flashcard.question}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )

            AnimatedVisibility(visible = isRevealed) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    Divider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "A: ${flashcard.answer}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
