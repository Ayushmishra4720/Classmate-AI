package com.example.data.repository

import com.example.data.api.GeminiService
import com.example.data.api.ParsedResumeAnalysis
import com.example.data.api.SummarizedNotesOutput
import com.example.data.local.AppDao
import com.example.data.model.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.TimeUnit

class StudentRepository(
    private val appDao: AppDao,
    private val geminiService: GeminiService
) {
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    // Flows
    val userProfile: Flow<UserProfile?> = appDao.getUserProfile()
    val attendanceSubjects: Flow<List<AttendanceSubject>> = appDao.getAllAttendanceSubjects()
    val assignments: Flow<List<Assignment>> = appDao.getAllAssignments()
    val pendingAssignments: Flow<List<Assignment>> = appDao.getPendingAssignments()
    val semesters: Flow<List<CgpaSemester>> = appDao.getAllSemesters()
    val allCourses: Flow<List<CgpaCourse>> = appDao.getAllCourses()
    val doubtSessions: Flow<List<DoubtSession>> = appDao.getAllDoubtSessions()
    val notesSummaries: Flow<List<NotesSummary>> = appDao.getAllNotesSummaries()
    val quizzes: Flow<List<QuizItem>> = appDao.getAllQuizzes()
    val studyPlans: Flow<List<StudyPlan>> = appDao.getAllStudyPlans()
    val revisionTasks: Flow<List<RevisionTask>> = appDao.getAllRevisionTasks()
    val pomodoroSessions: Flow<List<PomodoroSession>> = appDao.getAllPomodoroSessions()
    val resumeAnalyses: Flow<List<ResumeAnalysis>> = appDao.getAllResumeAnalyses()
    val notifications: Flow<List<StudentNotification>> = appDao.getAllNotifications()
    val unreadNotificationCount: Flow<Int> = appDao.getUnreadNotificationCount()

    // Initialize Default Student Data on First Launch
    suspend fun checkAndSeedInitialData() {
        val existingProfile = appDao.getUserProfileDirect()
        if (existingProfile == null) {
            // 1. Seed Profile
            val profile = UserProfile(
                id = 1,
                userId = 101L,
                name = "Ayush",
                email = "ayush29072004@gmail.com",
                studentId = "NIT-CS-2026-042",
                university = "National Institute of Technology",
                major = "Computer Science & Engineering",
                minor = "Data Science & Artificial Intelligence",
                enrollmentYear = 2022,
                expectedGraduationYear = 2026,
                gpa = 8.85,
                targetCgpa = 9.0,
                currentCgpa = 8.85,
                academicGoals = "Maintain 9.0+ CGPA, publish AI Systems research, and crack top-tier tech placements.",
                preferredLearningStyle = "Visual & Hands-on Coding",
                extracurricularActivities = "Lead of Open Source Club, Competitive Programmer (Codeforces Candidate Master), Hackathon Mentor",
                semester = 6,
                bio = "Classmate AI Student | Aspiring AI & Systems Engineer",
                avatarIndex = 0,
                themeMode = "SYSTEM",
                themePalette = "INDIGO",
                isLoggedIn = true
            )
            appDao.insertOrUpdateProfile(profile)

            // 2. Seed Real Attendance Subjects
            val subjects = listOf(
                AttendanceSubject(name = "Data Structures & Algorithms", code = "CS301", attendedClasses = 24, totalClasses = 28, targetPercentage = 75.0, professor = "Dr. Raman", room = "LH-102", colorHex = "#4F46E5"),
                AttendanceSubject(name = "Operating Systems", code = "CS302", attendedClasses = 19, totalClasses = 26, targetPercentage = 75.0, professor = "Prof. Mehra", room = "CS-Lab 3", colorHex = "#0EA5E9"),
                AttendanceSubject(name = "Computer Networks", code = "CS303", attendedClasses = 22, totalClasses = 24, targetPercentage = 75.0, professor = "Dr. Ananya", room = "LH-204", colorHex = "#10B981"),
                AttendanceSubject(name = "Artificial Intelligence", code = "CS304", attendedClasses = 27, totalClasses = 30, targetPercentage = 75.0, professor = "Dr. Vikram", room = "AI Center", colorHex = "#8B5CF6"),
                AttendanceSubject(name = "Software Engineering", code = "CS305", attendedClasses = 18, totalClasses = 25, targetPercentage = 75.0, professor = "Prof. Sneha", room = "LH-105", colorHex = "#F59E0B")
            )
            subjects.forEach { appDao.insertAttendanceSubject(it) }

            // 3. Seed Real Assignments
            val now = System.currentTimeMillis()
            val day = 24 * 60 * 60 * 1000L
            val assignmentsList = listOf(
                Assignment(title = "B+ Tree Indexing Implementation", subject = "Data Structures", dueDateMillis = now + 2 * day, priority = "HIGH", status = "IN_PROGRESS", description = "Implement B+ tree insert and split operations in C++/Java with visualization."),
                Assignment(title = "Virtual Memory Page Replacement Simulator", subject = "Operating Systems", dueDateMillis = now + 4 * day, priority = "HIGH", status = "PENDING", description = "Simulate FIFO, LRU, and Optimal page replacement algorithms."),
                Assignment(title = "Socket Programming Multi-Client Chat", subject = "Computer Networks", dueDateMillis = now + 7 * day, priority = "MEDIUM", status = "PENDING", description = "Build TCP socket server with multi-threaded client handling."),
                Assignment(title = "A* Search Algorithm Grid Pathfinding", subject = "Artificial Intelligence", dueDateMillis = now - 1 * day, priority = "LOW", status = "SUBMITTED", description = "Heuristic grid search solver.", obtainedMarks = 95)
            )
            assignmentsList.forEach { appDao.insertAssignment(it) }

            // 4. Seed Semesters & CGPA
            val sem1Id = appDao.insertSemester(CgpaSemester(semesterNumber = 1, semesterName = "Semester 1", totalCredits = 20.0, sgpa = 8.70))
            val sem2Id = appDao.insertSemester(CgpaSemester(semesterNumber = 2, semesterName = "Semester 2", totalCredits = 22.0, sgpa = 8.90))
            val sem3Id = appDao.insertSemester(CgpaSemester(semesterNumber = 3, semesterName = "Semester 3", totalCredits = 24.0, sgpa = 8.80))
            val sem4Id = appDao.insertSemester(CgpaSemester(semesterNumber = 4, semesterName = "Semester 4", totalCredits = 22.0, sgpa = 9.00))
            val sem5Id = appDao.insertSemester(CgpaSemester(semesterNumber = 5, semesterName = "Semester 5", totalCredits = 23.0, sgpa = 8.85))

            // 5. Seed Revision Tasks (Spaced Repetition)
            val revisions = listOf(
                RevisionTask(topicName = "Dijkstra & Floyd-Warshall Algorithms", subject = "DSA", firstStudiedDate = now - 7 * day, nextReviewDate = now, intervalStage = 3, masteryLevel = 4, notes = "Remember negative edge weights caveat for Dijkstra"),
                RevisionTask(topicName = "Process Synchronization & Semaphores", subject = "Operating Systems", firstStudiedDate = now - 3 * day, nextReviewDate = now + 1 * day, intervalStage = 2, masteryLevel = 3, notes = "Dining philosophers problem and deadlock prevention"),
                RevisionTask(topicName = "TCP 3-Way Handshake & Congestion Control", subject = "Networks", firstStudiedDate = now - 1 * day, nextReviewDate = now + 2 * day, intervalStage = 1, masteryLevel = 3, notes = "AIMD and slow start thresholds")
            )
            revisions.forEach { appDao.insertRevisionTask(it) }

            // 6. Seed Notifications
            val notifs = listOf(
                StudentNotification(title = "Welcome to Classmate AI!", message = "One App. Every Student Need. Powered by AI. Created by Ayush.", category = "SYSTEM", timestamp = now),
                StudentNotification(title = "Assignment Due Soon", message = "B+ Tree Indexing Implementation is due in 2 days!", category = "ASSIGNMENT", timestamp = now - 3600000),
                StudentNotification(title = "Attendance Alert: Operating Systems", message = "Current attendance is 73.1%. Attend next 2 classes to cross 75% target.", category = "ATTENDANCE", timestamp = now - 7200000)
            )
            notifs.forEach { appDao.insertNotification(it) }
        }
    }

    // Profile Actions
    suspend fun updateProfile(profile: UserProfile) {
        appDao.insertOrUpdateProfile(profile)
    }

    // Attendance Actions
    suspend fun addAttendanceSubject(subject: AttendanceSubject): Long {
        return appDao.insertAttendanceSubject(subject)
    }

    suspend fun updateAttendanceSubject(subject: AttendanceSubject) {
        appDao.updateAttendanceSubject(subject)
    }

    suspend fun deleteAttendanceSubject(subject: AttendanceSubject) {
        appDao.deleteAttendanceSubject(subject)
    }

    suspend fun markAttendance(subjectId: Long, isPresent: Boolean, note: String = "") {
        val subject = appDao.getAttendanceSubjectById(subjectId) ?: return
        val newAttended = if (isPresent) subject.attendedClasses + 1 else subject.attendedClasses
        val newTotal = subject.totalClasses + 1
        appDao.updateAttendanceSubject(subject.copy(attendedClasses = newAttended, totalClasses = newTotal))
        appDao.insertAttendanceLog(
            AttendanceLog(
                subjectId = subjectId,
                dateMillis = System.currentTimeMillis(),
                status = if (isPresent) "PRESENT" else "ABSENT",
                note = note
            )
        )
    }

    fun getLogsForSubject(subjectId: Long): Flow<List<AttendanceLog>> = appDao.getLogsForSubject(subjectId)

    // Assignment Actions
    suspend fun addAssignment(assignment: Assignment): Long {
        val id = appDao.insertAssignment(assignment)
        appDao.insertNotification(
            StudentNotification(
                title = "New Assignment Added",
                message = "${assignment.title} (${assignment.subject}) has been added.",
                category = "ASSIGNMENT"
            )
        )
        return id
    }

    suspend fun updateAssignment(assignment: Assignment) {
        appDao.updateAssignment(assignment)
    }

    suspend fun deleteAssignment(assignment: Assignment) {
        appDao.deleteAssignment(assignment)
    }

    // CGPA Actions
    suspend fun addSemester(semester: CgpaSemester): Long = appDao.insertSemester(semester)
    suspend fun updateSemester(semester: CgpaSemester) = appDao.updateSemester(semester)
    suspend fun deleteSemester(semester: CgpaSemester) {
        appDao.deleteSemester(semester)
        appDao.deleteCoursesForSemester(semester.id)
    }

    suspend fun addCourse(course: CgpaCourse): Long = appDao.insertCourse(course)
    suspend fun deleteCourse(course: CgpaCourse) = appDao.deleteCourse(course)
    fun getCoursesForSemester(semesterId: Long): Flow<List<CgpaCourse>> = appDao.getCoursesForSemester(semesterId)

    // AI Doubt Solver
    fun getDoubtMessages(sessionId: Long): Flow<List<DoubtMessage>> = appDao.getMessagesForSession(sessionId)

    suspend fun createDoubtSession(title: String, subject: String): Long {
        return appDao.insertDoubtSession(DoubtSession(title = title, subject = subject))
    }

    suspend fun askDoubt(sessionId: Long, subject: String, question: String, history: List<Pair<String, String>>): Result<String> {
        // Save user message
        appDao.insertDoubtMessage(
            DoubtMessage(
                sessionId = sessionId,
                sender = "user",
                content = question,
                subject = subject
            )
        )

        // Call Gemini
        val result = geminiService.solveDoubt(subject, question, history)
        if (result.isSuccess) {
            val answer = result.getOrNull() ?: "I couldn't process your question."
            appDao.insertDoubtMessage(
                DoubtMessage(
                    sessionId = sessionId,
                    sender = "ai",
                    content = answer,
                    subject = subject
                )
            )
        }
        return result
    }

    // AI Notes Summarizer
    suspend fun summarizeAndSaveNotes(title: String, subject: String, content: String): Result<NotesSummary> {
        val result = geminiService.summarizeNotes(content, subject)
        return result.map { output ->
            val flashcardsAdapter = moshi.adapter<List<FlashcardItem>>(Types.newParameterizedType(List::class.java, FlashcardItem::class.java))
            val flashcardsJson = flashcardsAdapter.toJson(output.flashcards)

            val entity = NotesSummary(
                title = title,
                subject = subject,
                originalContent = content,
                summaryText = output.summary,
                keyTakeaways = output.keyPoints,
                flashcardsJson = flashcardsJson
            )
            val id = appDao.insertNotesSummary(entity)
            entity.copy(id = id)
        }
    }

    suspend fun deleteNotesSummary(summary: NotesSummary) = appDao.deleteNotesSummary(summary)

    // AI Quiz Generator
    suspend fun generateAndSaveQuiz(topic: String, subject: String, difficulty: String, numQuestions: Int = 5): Result<QuizItem> {
        val result = geminiService.generateQuiz(topic, subject, difficulty, numQuestions)
        return result.map { questions ->
            val adapter = moshi.adapter<List<QuizQuestionDto>>(Types.newParameterizedType(List::class.java, QuizQuestionDto::class.java))
            val json = adapter.toJson(questions)

            val quiz = QuizItem(
                topic = topic,
                subject = subject,
                difficulty = difficulty,
                questionsJson = json,
                totalQuestions = questions.size,
                score = 0,
                isCompleted = false
            )
            val id = appDao.insertQuiz(quiz)
            quiz.copy(id = id)
        }
    }

    suspend fun updateQuizResult(quiz: QuizItem, score: Int) {
        appDao.updateQuiz(quiz.copy(score = score, isCompleted = true))
    }

    suspend fun deleteQuiz(quiz: QuizItem) = appDao.deleteQuiz(quiz)

    // AI Assignment Helper
    suspend fun getAssignmentAIHelp(assignment: Assignment, promptDetails: String): Result<String> {
        val result = geminiService.getAssignmentHelp(assignment.title, assignment.subject, promptDetails)
        if (result.isSuccess) {
            val help = result.getOrNull() ?: ""
            appDao.updateAssignment(assignment.copy(aiAssistanceSummary = help))
        }
        return result
    }

    // AI Study Planner
    suspend fun generateAndSaveStudyPlan(examTitle: String, daysRemaining: Int, dailyHours: Double, topics: String): Result<StudyPlan> {
        val result = geminiService.generateStudyPlan(examTitle, daysRemaining, dailyHours, topics)
        return result.map { days ->
            val adapter = moshi.adapter<List<StudyDayDto>>(Types.newParameterizedType(List::class.java, StudyDayDto::class.java))
            val json = adapter.toJson(days)

            val plan = StudyPlan(
                examTitle = examTitle,
                targetDateMillis = System.currentTimeMillis() + daysRemaining * 24 * 3600 * 1000L,
                dailyHours = dailyHours,
                topics = topics,
                planScheduleJson = json
            )
            val id = appDao.insertStudyPlan(plan)
            plan.copy(id = id)
        }
    }

    suspend fun updateStudyPlanProgress(plan: StudyPlan, progress: Int) {
        appDao.updateStudyPlan(plan.copy(progressPercentage = progress, isCompleted = progress >= 100))
    }

    suspend fun deleteStudyPlan(plan: StudyPlan) = appDao.deleteStudyPlan(plan)

    // Spaced Repetition Revision Scheduler
    suspend fun addRevisionTask(topicName: String, subject: String, notes: String = ""): Long {
        val now = System.currentTimeMillis()
        val nextDate = now + 1 * 24 * 3600 * 1000L // 1 day for stage 1
        return appDao.insertRevisionTask(
            RevisionTask(
                topicName = topicName,
                subject = subject,
                firstStudiedDate = now,
                nextReviewDate = nextDate,
                intervalStage = 1,
                masteryLevel = 1,
                notes = notes
            )
        )
    }

    suspend fun advanceRevisionStage(task: RevisionTask, wasSuccessful: Boolean) {
        val intervals = listOf(1, 3, 7, 14, 30) // Days interval for spaced repetition
        val nextStage = if (wasSuccessful) (task.intervalStage + 1).coerceAtMost(5) else 1
        val daysToAdd = intervals.getOrElse(nextStage - 1) { 30 }
        val nextDate = System.currentTimeMillis() + daysToAdd * 24 * 3600 * 1000L
        val nextMastery = if (wasSuccessful) (task.masteryLevel + 1).coerceAtMost(5) else (task.masteryLevel - 1).coerceAtLeast(1)

        appDao.updateRevisionTask(
            task.copy(
                intervalStage = nextStage,
                nextReviewDate = nextDate,
                masteryLevel = nextMastery,
                isReviewedToday = true
            )
        )
    }

    suspend fun deleteRevisionTask(task: RevisionTask) = appDao.deleteRevisionTask(task)

    // Pomodoro Timer
    suspend fun logPomodoroSession(durationMinutes: Int, sessionType: String, subjectTag: String, notes: String = ""): Long {
        val id = appDao.insertPomodoroSession(
            PomodoroSession(
                durationMinutes = durationMinutes,
                sessionType = sessionType,
                subjectTag = subjectTag,
                notes = notes
            )
        )
        appDao.insertNotification(
            StudentNotification(
                title = "Focus Session Completed!",
                message = "Great job! You completed $durationMinutes mins on $subjectTag.",
                category = "POMODORO"
            )
        )
        return id
    }

    // AI Resume Analyzer
    suspend fun analyzeAndSaveResume(resumeText: String, targetRole: String): Result<ResumeAnalysis> {
        val result = geminiService.analyzeResume(resumeText, targetRole)
        return result.map { analysis ->
            val entity = ResumeAnalysis(
                targetRole = targetRole,
                atsScore = analysis.atsScore,
                strengths = analysis.strengths,
                weaknesses = analysis.weaknesses,
                missingKeywords = analysis.missingKeywords,
                suggestions = analysis.suggestions,
                rawFeedback = analysis.feedback
            )
            val id = appDao.insertResumeAnalysis(entity)
            entity.copy(id = id)
        }
    }

    suspend fun deleteResumeAnalysis(analysis: ResumeAnalysis) = appDao.deleteResumeAnalysis(analysis)

    // Notifications
    suspend fun markNotificationAsRead(id: Long) = appDao.markNotificationAsRead(id)
    suspend fun markAllNotificationsAsRead() = appDao.markAllNotificationsAsRead()
    suspend fun deleteNotification(id: Long) = appDao.deleteNotification(id)

    // JSON Helper parsers
    fun parseFlashcards(json: String): List<FlashcardItem> {
        return try {
            val adapter = moshi.adapter<List<FlashcardItem>>(Types.newParameterizedType(List::class.java, FlashcardItem::class.java))
            adapter.fromJson(json) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun parseQuizQuestions(json: String): List<QuizQuestionDto> {
        return try {
            val adapter = moshi.adapter<List<QuizQuestionDto>>(Types.newParameterizedType(List::class.java, QuizQuestionDto::class.java))
            adapter.fromJson(json) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun parseStudyDays(json: String): List<StudyDayDto> {
        return try {
            val adapter = moshi.adapter<List<StudyDayDto>>(Types.newParameterizedType(List::class.java, StudyDayDto::class.java))
            adapter.fromJson(json) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
