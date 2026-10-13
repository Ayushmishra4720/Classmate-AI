package com.example.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AyushBrandHeader
import com.example.ui.theme.*
import com.example.ui.viewmodel.StudentViewModel
import com.example.ui.viewmodel.ThemeMode
import com.example.ui.viewmodel.ThemePalette

@Composable
fun SettingsScreen(viewModel: StudentViewModel, onNavigateToAuth: () -> Unit) {
    val currentThemeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val currentThemePalette by viewModel.themePalette.collectAsStateWithLifecycle()

    var notificationsEnabled by remember { mutableStateOf(true) }
    var smartAttendanceAlerts by remember { mutableStateOf(true) }
    var autoSpacedRepetition by remember { mutableStateOf(true) }
    var highPrecisionCgpa by remember { mutableStateOf(true) }

    val palettes = listOf(
        Triple(ThemePalette.INDIGO, "Indigo Tech", IndigoPrimary),
        Triple(ThemePalette.EMERALD, "Emerald Scholar", EmeraldPrimary),
        Triple(ThemePalette.SUNSET, "Sunset Crimson", SunsetPrimary),
        Triple(ThemePalette.VIOLET, "Cyberpunk Violet", VioletPrimary),
        Triple(ThemePalette.OCEAN, "Oceanic Sapphire", OceanPrimary)
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("settings_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            AyushBrandHeader()
        }

        item {
            Text("App Settings & Preferences", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }

        // Appearance & Themes Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Palette, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Appearance & Themes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }

                    Text("Theme Mode", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ThemeMode.values().forEach { mode ->
                            val isSelected = currentThemeMode == mode
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.setThemeMode(mode) },
                                label = { Text(mode.name, fontSize = 12.sp) },
                                modifier = Modifier.weight(1f),
                                leadingIcon = {
                                    when (mode) {
                                        ThemeMode.SYSTEM -> Icon(Icons.Filled.SettingsBrightness, contentDescription = null, modifier = Modifier.size(16.dp))
                                        ThemeMode.LIGHT -> Icon(Icons.Filled.LightMode, contentDescription = null, modifier = Modifier.size(16.dp))
                                        ThemeMode.DARK -> Icon(Icons.Filled.DarkMode, contentDescription = null, modifier = Modifier.size(16.dp))
                                        ThemeMode.OLED -> Icon(Icons.Filled.Brightness2, contentDescription = null, modifier = Modifier.size(16.dp))
                                    }
                                }
                            )
                        }
                    }

                    Divider()

                    Text("Multiple Theme Palettes", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(palettes) { (palette, name, color) ->
                            val isSelected = currentThemePalette == palette
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { viewModel.setThemePalette(palette) }
                                    .padding(horizontal = 12.dp, vertical = 10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = name,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        // Notification Preferences
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("AI & Automation Triggers", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                    SettingToggleRow(
                        title = "Smart Attendance Notifications",
                        subtitle = "Alert when any subject falls below 75% target threshold",
                        checked = smartAttendanceAlerts,
                        onCheckedChange = { smartAttendanceAlerts = it }
                    )

                    Divider()

                    SettingToggleRow(
                        title = "Daily Spaced Repetition Reminders",
                        subtitle = "Notify when scheduled revision intervals are due",
                        checked = autoSpacedRepetition,
                        onCheckedChange = { autoSpacedRepetition = it }
                    )

                    Divider()

                    SettingToggleRow(
                        title = "Assignment Deadline Warnings",
                        subtitle = "Remind 48 hours prior to due submission timestamps",
                        checked = notificationsEnabled,
                        onCheckedChange = { notificationsEnabled = it }
                    )

                    Divider()

                    SettingToggleRow(
                        title = "10-Point CGPA Scale Calculation",
                        subtitle = "Standard Indian University (NIT/IIT/VTU) Grading Scheme",
                        checked = highPrecisionCgpa,
                        onCheckedChange = { highPrecisionCgpa = it }
                    )
                }
            }
        }

        // App Information
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Classmate AI", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Tagline: “One App. Every Student Need. Powered by AI.”", style = MaterialTheme.typography.bodyMedium)
                    Text("Creator: Ayush", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    Text("Version: 2.0.0 (Geometric Balance Multi-Theme Edition)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        // Account / Sign Out
        item {
            Button(
                onClick = onNavigateToAuth,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Filled.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Switch Account / Sign In", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun SettingToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
