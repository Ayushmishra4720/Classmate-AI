package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val userId: Long = 101L,
    val name: String = "Ayush",
    val email: String = "ayush29072004@gmail.com",
    val studentId: String = "NIT-CS-2026-042",
    val university: String = "National Institute of Technology",
    val major: String = "Computer Science & Engineering",
    val minor: String = "Data Science & Artificial Intelligence",
    val enrollmentYear: Int = 2022,
    val expectedGraduationYear: Int = 2026,
    val gpa: Double = 8.85,
    val targetCgpa: Double = 9.0,
    val currentCgpa: Double = 8.85,
    val academicGoals: String = "Maintain 9.0+ CGPA, publish AI Systems research, and crack top-tier tech placements.",
    val preferredLearningStyle: String = "Visual & Hands-on Coding", // Visual, Auditory, Kinesthetic, Reading/Writing
    val extracurricularActivities: String = "Lead of Open Source Club, Competitive Programmer (Codeforces Candidate Master), Hackathon Mentor",
    val semester: Int = 6,
    val bio: String = "Classmate AI Student | Aspiring AI & Systems Engineer",
    val avatarIndex: Int = 0,
    val themeMode: String = "SYSTEM", // "SYSTEM", "LIGHT", "DARK", "OLED"
    val themePalette: String = "INDIGO", // "INDIGO", "EMERALD", "SUNSET", "VIOLET", "OCEAN"
    val isLoggedIn: Boolean = true
)

@Entity(tableName = "attendance_subjects")
data class AttendanceSubject(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val code: String,
    val attendedClasses: Int = 0,
    val totalClasses: Int = 0,
    val targetPercentage: Double = 75.0,
    val professor: String = "",
    val room: String = "",
    val colorHex: String = "#4F46E5"
) {
    val percentage: Double
        get() = if (totalClasses == 0) 100.0 else (attendedClasses.toDouble() / totalClasses) * 100.0

    // Calculates how many consecutive classes can be safely bunked without falling below target
    val safeBunks: Int
        get() {
            if (totalClasses == 0) return 0
            val target = targetPercentage / 100.0
            if (percentage < targetPercentage) return 0
            // attended / (total + x) >= target => attended >= target*(total + x) => x <= (attended - target*total)/target
            val allowed = ((attendedClasses - target * totalClasses) / target).toInt()
            return allowed.coerceAtLeast(0)
        }

    // Calculates how many consecutive classes must be attended to reach target
    val requiredToAttend: Int
        get() {
            if (totalClasses == 0 || percentage >= targetPercentage) return 0
            val target = targetPercentage / 100.0
            // (attended + y) / (total + y) >= target => attended + y >= target*total + target*y => y*(1-target) >= target*total - attended
            val needed = Math.ceil((target * totalClasses - attendedClasses) / (1.0 - target)).toInt()
            return needed.coerceAtLeast(0)
        }
}

@Entity(tableName = "attendance_logs")
data class AttendanceLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subjectId: Long,
    val dateMillis: Long,
    val status: String, // "PRESENT", "ABSENT", "CANCELLED"
    val note: String = ""
)

@Entity(tableName = "assignments")
data class Assignment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val subject: String,
    val dueDateMillis: Long,
    val priority: String = "MEDIUM", // "HIGH", "MEDIUM", "LOW"
    val status: String = "PENDING", // "PENDING", "IN_PROGRESS", "SUBMITTED"
    val description: String = "",
    val aiAssistanceSummary: String = "",
    val maxMarks: Int = 100,
    val obtainedMarks: Int? = null
)

@Entity(tableName = "cgpa_semesters")
data class CgpaSemester(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val semesterNumber: Int,
    val semesterName: String,
    val totalCredits: Double = 0.0,
    val sgpa: Double = 0.0
)

@Entity(tableName = "cgpa_courses")
data class CgpaCourse(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val semesterId: Long,
    val courseCode: String,
    val courseName: String,
    val credits: Double,
    val gradePoint: Double, // 10.0 for O/A+, 9.0 for A, etc.
    val letterGrade: String = "A"
)

@Entity(tableName = "doubt_sessions")
data class DoubtSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val subject: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "doubt_messages")
data class DoubtMessage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long,
    val sender: String, // "user", "ai"
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val subject: String = ""
)

@Entity(tableName = "notes_summaries")
data class NotesSummary(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val subject: String,
    val originalContent: String,
    val summaryText: String,
    val keyTakeaways: String = "", // formatted bullet points
    val flashcardsJson: String = "", // JSON list of flashcards
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "quizzes")
data class QuizItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val topic: String,
    val subject: String,
    val difficulty: String = "MEDIUM", // "EASY", "MEDIUM", "HARD"
    val questionsJson: String, // JSON payload containing questions and choices
    val totalQuestions: Int = 5,
    val score: Int = 0,
    val isCompleted: Boolean = false,
    val dateTaken: Long = System.currentTimeMillis()
)

@Entity(tableName = "study_plans")
data class StudyPlan(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val examTitle: String,
    val targetDateMillis: Long,
    val dailyHours: Double,
    val topics: String,
    val planScheduleJson: String, // structured day milestones
    val progressPercentage: Int = 0,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "revision_tasks")
data class RevisionTask(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val topicName: String,
    val subject: String,
    val firstStudiedDate: Long,
    val nextReviewDate: Long,
    val intervalStage: Int = 1, // 1 (1 day), 2 (3 days), 3 (7 days), 4 (14 days), 5 (30 days)
    val masteryLevel: Int = 1, // 1 to 5
    val notes: String = "",
    val isReviewedToday: Boolean = false
)

@Entity(tableName = "pomodoro_sessions")
data class PomodoroSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val durationMinutes: Int,
    val sessionType: String = "FOCUS", // "FOCUS", "SHORT_BREAK", "LONG_BREAK"
    val subjectTag: String = "General Study",
    val completedAt: Long = System.currentTimeMillis(),
    val notes: String = ""
)

@Entity(tableName = "resume_analyses")
data class ResumeAnalysis(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val targetRole: String,
    val atsScore: Int, // 0 to 100
    val strengths: String,
    val weaknesses: String,
    val missingKeywords: String,
    val suggestions: String,
    val rawFeedback: String,
    val analyzedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class StudentNotification(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val message: String,
    val category: String, // "ASSIGNMENT", "ATTENDANCE", "REVISION", "EXAM", "POMODORO", "SYSTEM"
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val actionRoute: String = ""
)

// Helper DTOs for JSON parsing
data class FlashcardItem(
    val question: String,
    val answer: String
)

data class QuizQuestionDto(
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

data class StudyDayDto(
    val dayNumber: Int,
    val dateLabel: String,
    val focusTopic: String,
    val tasks: List<String>,
    val hoursAllocated: Double
)
