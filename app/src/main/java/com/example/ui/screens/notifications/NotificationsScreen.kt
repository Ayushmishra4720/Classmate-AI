package com.example.ui.screens.notifications

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
import com.example.data.model.StudentNotification
import com.example.ui.components.AyushBrandHeader
import com.example.ui.components.EmptyStateView
import com.example.ui.theme.*
import com.example.ui.viewmodel.StudentViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NotificationsScreen(viewModel: StudentViewModel) {
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val unreadCount by viewModel.unreadNotificationCount.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("notifications_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            AyushBrandHeader()
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Notifications", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("$unreadCount unread updates", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                if (unreadCount > 0) {
                    TextButton(onClick = { viewModel.markAllNotificationsAsRead() }) {
                        Text("Mark all read")
                    }
                }
            }
        }

        if (notifications.isEmpty()) {
            item {
                EmptyStateView(
                    icon = Icons.Filled.NotificationsNone,
                    title = "No Notifications",
                    description = "You are completely up to date with attendance, assignments, and revisions!"
                )
            }
        } else {
            items(notifications) { notif ->
                val dateFormat = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
                val timeStr = dateFormat.format(Date(notif.timestamp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (notif.isRead) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        val (icon, color) = when (notif.category) {
                            "ATTENDANCE" -> Icons.Filled.CheckCircle to AmberTertiary
                            "ASSIGNMENT" -> Icons.Filled.Assignment to MaterialTheme.colorScheme.primary
                            "POMODORO" -> Icons.Filled.Timer to EmeraldSuccess
                            else -> Icons.Filled.Info to MaterialTheme.colorScheme.primary
                        }

                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(color.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = notif.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = if (notif.isRead) FontWeight.Normal else FontWeight.Bold
                                )
                                Text(text = timeStr, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = notif.message, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                        }

                        IconButton(
                            onClick = { viewModel.deleteNotification(notif.id) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Filled.Close, contentDescription = "Dismiss", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}
