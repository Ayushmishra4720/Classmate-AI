package com.example.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(viewModel: StudentViewModel) {
    val profile by viewModel.userProfile.collectAsStateWithLifecycle()

    var isEditing by remember { mutableStateOf(false) }
    var name by remember(profile) { mutableStateOf(profile?.name ?: "Ayush") }
    var email by remember(profile) { mutableStateOf(profile?.email ?: "ayush29072004@gmail.com") }
    var studentId by remember(profile) { mutableStateOf(profile?.studentId ?: "NIT-CS-2026-042") }
    var userIdStr by remember(profile) { mutableStateOf(profile?.userId?.toString() ?: "101") }
    var university by remember(profile) { mutableStateOf(profile?.university ?: "National Institute of Technology") }
    var major by remember(profile) { mutableStateOf(profile?.major ?: "Computer Science & Engineering") }
    var minor by remember(profile) { mutableStateOf(profile?.minor ?: "Data Science & Artificial Intelligence") }
    var enrollmentYear by remember(profile) { mutableStateOf(profile?.enrollmentYear?.toString() ?: "2022") }
    var expectedGradYear by remember(profile) { mutableStateOf(profile?.expectedGraduationYear?.toString() ?: "2026") }
    var gpaStr by remember(profile) { mutableStateOf(profile?.gpa?.toString() ?: "8.85") }
    var semester by remember(profile) { mutableStateOf(profile?.semester?.toString() ?: "6") }
    var targetCgpa by remember(profile) { mutableStateOf(profile?.targetCgpa?.toString() ?: "9.0") }
    var academicGoals by remember(profile) { mutableStateOf(profile?.academicGoals ?: "Maintain 9.0+ CGPA, publish AI Systems research, and crack top-tier tech placements.") }
    var preferredLearningStyle by remember(profile) { mutableStateOf(profile?.preferredLearningStyle ?: "Visual & Hands-on Coding") }
    var extracurriculars by remember(profile) { mutableStateOf(profile?.extracurricularActivities ?: "Lead of Open Source Club, Competitive Programmer (Codeforces Candidate Master), Hackathon Mentor") }
    var bio by remember(profile) { mutableStateOf(profile?.bio ?: "Classmate AI Student | Aspiring AI & Systems Engineer") }

    val learningStyles = listOf("Visual & Hands-on Coding", "Auditory & Lectures", "Kinesthetic / Practical Labs", "Reading & In-depth Documentation")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("profile_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            AyushBrandHeader()
        }

        // Student Hero Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (profile?.name?.take(1) ?: "A").uppercase(),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = profile?.name ?: "Ayush",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = profile?.email ?: "ayush29072004@gmail.com",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Student ID: ${profile?.studentId ?: "NIT-CS-2026-042"}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Auth User ID #${profile?.userId ?: 101}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // Academic Profile Details
        item {
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
                        Column {
                            Text("Database Student Profile", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("Synced with PostgreSQL / Room DB", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        IconButton(onClick = { isEditing = !isEditing }, modifier = Modifier.testTag("edit_profile_button")) {
                            Icon(
                                if (isEditing) Icons.Filled.Close else Icons.Filled.Edit,
                                contentDescription = "Toggle Edit",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Divider()

                    if (isEditing) {
                        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email Address") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = studentId, onValueChange = { studentId = it }, label = { Text("Student ID (student_id)") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = userIdStr, onValueChange = { userIdStr = it }, label = { Text("User ID (foreign key)") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = university, onValueChange = { university = it }, label = { Text("University / Institution") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = major, onValueChange = { major = it }, label = { Text("Major") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = minor, onValueChange = { minor = it }, label = { Text("Minor") }, modifier = Modifier.fillMaxWidth())

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(value = enrollmentYear, onValueChange = { enrollmentYear = it }, label = { Text("Enrollment Year") }, modifier = Modifier.weight(1f))
                            OutlinedTextField(value = expectedGradYear, onValueChange = { expectedGradYear = it }, label = { Text("Graduation Year") }, modifier = Modifier.weight(1f))
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(value = gpaStr, onValueChange = { gpaStr = it }, label = { Text("GPA / CGPA") }, modifier = Modifier.weight(1f))
                            OutlinedTextField(value = semester, onValueChange = { semester = it }, label = { Text("Semester") }, modifier = Modifier.weight(1f))
                        }

                        OutlinedTextField(value = academicGoals, onValueChange = { academicGoals = it }, label = { Text("Academic Goals") }, modifier = Modifier.fillMaxWidth(), minLines = 2)

                        Text("Preferred Learning Style", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            learningStyles.take(2).forEach { style ->
                                FilterChip(
                                    selected = preferredLearningStyle == style,
                                    onClick = { preferredLearningStyle = style },
                                    label = { Text(style.take(16), fontSize = 11.sp) }
                                )
                            }
                        }

                        OutlinedTextField(value = extracurriculars, onValueChange = { extracurriculars = it }, label = { Text("Extracurricular Activities") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
                        OutlinedTextField(value = bio, onValueChange = { bio = it }, label = { Text("Bio & Personal Note") }, modifier = Modifier.fillMaxWidth(), minLines = 2)

                        Button(
                            onClick = {
                                profile?.let {
                                    viewModel.updateProfile(
                                        it.copy(
                                            name = name,
                                            email = email,
                                            studentId = studentId,
                                            userId = userIdStr.toLongOrNull() ?: 101L,
                                            university = university,
                                            major = major,
                                            minor = minor,
                                            enrollmentYear = enrollmentYear.toIntOrNull() ?: 2022,
                                            expectedGraduationYear = expectedGradYear.toIntOrNull() ?: 2026,
                                            gpa = gpaStr.toDoubleOrNull() ?: 8.85,
                                            currentCgpa = gpaStr.toDoubleOrNull() ?: 8.85,
                                            semester = semester.toIntOrNull() ?: 6,
                                            targetCgpa = targetCgpa.toDoubleOrNull() ?: 9.0,
                                            academicGoals = academicGoals,
                                            preferredLearningStyle = preferredLearningStyle,
                                            extracurricularActivities = extracurriculars,
                                            bio = bio
                                        )
                                    )
                                }
                                isEditing = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Save Changes to Database")
                        }
                    } else {
                        ProfileInfoRow(label = "Student ID (student_id)", value = profile?.studentId ?: "NIT-CS-2026-042")
                        ProfileInfoRow(label = "User ID (user_id FK)", value = "${profile?.userId ?: 101}")
                        ProfileInfoRow(label = "University", value = profile?.university ?: "National Institute of Technology")
                        ProfileInfoRow(label = "Major", value = profile?.major ?: "Computer Science & Engineering")
                        ProfileInfoRow(label = "Minor", value = profile?.minor ?: "Data Science & Artificial Intelligence")
                        ProfileInfoRow(label = "Enrollment Year", value = "${profile?.enrollmentYear ?: 2022}")
                        ProfileInfoRow(label = "Expected Graduation", value = "${profile?.expectedGraduationYear ?: 2026}")
                        ProfileInfoRow(label = "GPA / Current CGPA", value = "${profile?.gpa ?: 8.85} / 10.0")
                        ProfileInfoRow(label = "Target CGPA", value = "${profile?.targetCgpa ?: 9.0} / 10.0")
                        ProfileInfoRow(label = "Current Semester", value = "Semester ${profile?.semester ?: 6}")

                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Academic Goals", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        Text(profile?.academicGoals ?: "Maintain 9.0+ CGPA, publish AI Systems research, and crack top placements.", style = MaterialTheme.typography.bodySmall)

                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Preferred Learning Style", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
                        Text(profile?.preferredLearningStyle ?: "Visual & Hands-on Coding", style = MaterialTheme.typography.bodySmall)

                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Extracurricular Activities", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.Bold)
                        Text(profile?.extracurricularActivities ?: "Lead of Open Source Club, Competitive Programmer, Hackathon Mentor", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
    }
}
