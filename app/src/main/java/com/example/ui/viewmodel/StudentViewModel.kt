package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.api.GeminiService
import com.example.data.local.AppDatabase
import com.example.data.model.*
import com.example.data.repository.StudentRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class ThemeMode { SYSTEM, LIGHT, DARK, OLED }
enum class ThemePalette { INDIGO, EMERALD, SUNSET, VIOLET, OCEAN }

class StudentViewModel(
    application: Application,
    private val repository: StudentRepository
) : AndroidViewModel(application) {

    // Theme Mode & Palette
    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private val _themePalette = MutableStateFlow(ThemePalette.INDIGO)
    val themePalette: StateFlow<ThemePalette> = _themePalette.asStateFlow()

    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
        viewModelScope.launch {
            userProfile.value?.let { current ->
                repository.updateProfile(current.copy(themeMode = mode.name))
            }
        }
    }

    fun setThemePalette(palette: ThemePalette) {
        _themePalette.value = palette
        viewModelScope.launch {
            userProfile.value?.let { current ->
                repository.updateProfile(current.copy(themePalette = palette.name))
            }
        }
    }

    // Custom API Key in-memory
    private val _customApiKey = MutableStateFlow("")
    val customApiKey: StateFlow<String> = _customApiKey.asStateFlow()

    fun setCustomApiKey(key: String) {
        _customApiKey.value = key
    }

    // Global UI Message / Toast
    private val _userMessage = MutableSharedFlow<String>()
    val userMessage: SharedFlow<String> = _userMessage.asSharedFlow()

    fun showMessage(msg: String) {
        viewModelScope.launch { _userMessage.emit(msg) }
    }

    // --- State from Repository ---
    val userProfile: StateFlow<UserProfile?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val attendanceSubjects: StateFlow<List<AttendanceSubject>> = repository.attendanceSubjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val assignments: StateFlow<List<Assignment>> = repository.assignments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingAssignments: StateFlow<List<Assignment>> = repository.pendingAssignments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val semesters: StateFlow<List<CgpaSemester>> = repository.semesters
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCourses: StateFlow<List<CgpaCourse>> = repository.allCourses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val doubtSessions: StateFlow<List<DoubtSession>> = repository.doubtSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notesSummaries: StateFlow<List<NotesSummary>> = repository.notesSummaries
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val quizzes: StateFlow<List<QuizItem>> = repository.quizzes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val studyPlans: StateFlow<List<StudyPlan>> = repository.studyPlans
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val revisionTasks: StateFlow<List<RevisionTask>> = repository.revisionTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pomodoroSessions: StateFlow<List<PomodoroSession>> = repository.pomodoroSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val resumeAnalyses: StateFlow<List<ResumeAnalysis>> = repository.resumeAnalyses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<StudentNotification>> = repository.notifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadNotificationCount: StateFlow<Int> = repository.unreadNotificationCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        viewModelScope.launch {
            repository.checkAndSeedInitialData()
        }
    }

    // --- Profile Operations ---
    fun updateProfile(profile: UserProfile) {
        viewModelScope.launch {
            repository.updateProfile(profile)
            showMessage("Profile updated successfully")
        }
    }

    // --- Attendance Operations ---
    fun addSubject(name: String, code: String, attended: Int, total: Int, target: Double, prof: String, room: String, colorHex: String) {
        viewModelScope.launch {
            repository.addAttendanceSubject(
                AttendanceSubject(
                    name = name,
                    code = code,
                    attendedClasses = attended,
                    totalClasses = total,
                    targetPercentage = target,
                    professor = prof,
                    room = room,
                    colorHex = colorHex
                )
            )
            showMessage("Added subject $name")
        }
    }

    fun markAttendance(subjectId: Long, isPresent: Boolean) {
        viewModelScope.launch {
            repository.markAttendance(subjectId, isPresent)
            showMessage(if (isPresent) "Marked Present! Attendance updated." else "Marked Absent.")
        }
    }

    fun deleteSubject(subject: AttendanceSubject) {
        viewModelScope.launch {
            repository.deleteAttendanceSubject(subject)
            showMessage("Deleted subject ${subject.name}")
        }
    }

    // --- Assignment Operations ---
    fun addAssignment(title: String, subject: String, dueDate: Long, priority: String, description: String) {
        viewModelScope.launch {
            repository.addAssignment(
                Assignment(
                    title = title,
                    subject = subject,
                    dueDateMillis = dueDate,
                    priority = priority,
                    description = description
                )
            )
            showMessage("Assignment '$title' added!")
        }
    }

    fun updateAssignmentStatus(assignment: Assignment, newStatus: String) {
        viewModelScope.launch {
            repository.updateAssignment(assignment.copy(status = newStatus))
            showMessage("Status changed to $newStatus")
        }
    }

    fun deleteAssignment(assignment: Assignment) {
        viewModelScope.launch {
            repository.deleteAssignment(assignment)
            showMessage("Assignment removed")
        }
    }

    // --- CGPA Operations ---
    fun addSemester(semesterNumber: Int, name: String, credits: Double, sgpa: Double) {
        viewModelScope.launch {
            repository.addSemester(
                CgpaSemester(
                    semesterNumber = semesterNumber,
                    semesterName = name,
                    totalCredits = credits,
                    sgpa = sgpa
                )
            )
            recalculateOverallCgpa()
            showMessage("Semester $semesterNumber added")
        }
    }

    fun deleteSemester(semester: CgpaSemester) {
        viewModelScope.launch {
            repository.deleteSemester(semester)
            recalculateOverallCgpa()
            showMessage("Semester removed")
        }
    }

    fun addCourseToSemester(semesterId: Long, code: String, name: String, credits: Double, gradePoint: Double, letterGrade: String) {
        viewModelScope.launch {
            repository.addCourse(
                CgpaCourse(
                    semesterId = semesterId,
                    courseCode = code,
                    courseName = name,
                    credits = credits,
                    gradePoint = gradePoint,
                    letterGrade = letterGrade
                )
            )
            showMessage("Course $name added")
        }
    }

    private suspend fun recalculateOverallCgpa() {
        // compute cumulative weighted SGPA across semesters
        val currentSems = semesters.value
        if (currentSems.isNotEmpty()) {
            val totalCreditPoints = currentSems.sumOf { it.totalCredits * it.sgpa }
            val totalCredits = currentSems.sumOf { it.totalCredits }
            if (totalCredits > 0) {
                val overall = (totalCreditPoints / totalCredits * 100).toInt() / 100.0
                userProfile.value?.let { p ->
                    repository.updateProfile(p.copy(currentCgpa = overall))
                }
            }
        }
    }

    // --- AI Doubt Solver State & Actions ---
    private val _currentDoubtSessionId = MutableStateFlow<Long?>(null)
    val currentDoubtSessionId: StateFlow<Long?> = _currentDoubtSessionId.asStateFlow()

    private val _isDoubtLoading = MutableStateFlow(false)
    val isDoubtLoading: StateFlow<Boolean> = _isDoubtLoading.asStateFlow()

    val currentDoubtMessages: StateFlow<List<DoubtMessage>> = _currentDoubtSessionId
        .flatMapLatest { id ->
            if (id != null) repository.getDoubtMessages(id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun startNewDoubtSession(subject: String, title: String = "Question on $subject") {
        viewModelScope.launch {
            val id = repository.createDoubtSession(title, subject)
            _currentDoubtSessionId.value = id
        }
    }

    fun selectDoubtSession(sessionId: Long) {
        _currentDoubtSessionId.value = sessionId
    }

    fun askDoubt(question: String, subject: String) {
        val sessId = _currentDoubtSessionId.value
        viewModelScope.launch {
            val activeId = if (sessId == null) {
                val newId = repository.createDoubtSession(question.take(30) + "...", subject)
                _currentDoubtSessionId.value = newId
                newId
            } else sessId

            _isDoubtLoading.value = true
            val historyPairs = currentDoubtMessages.value.map { it.sender to it.content }
            val result = repository.askDoubt(activeId, subject, question, historyPairs)
            _isDoubtLoading.value = false
            if (result.isFailure) {
                showMessage("Failed to reach AI: ${result.exceptionOrNull()?.localizedMessage ?: "Unknown error"}")
            }
        }
    }

    // --- AI Notes Summarizer State & Actions ---
    private val _isSummarizing = MutableStateFlow(false)
    val isSummarizing: StateFlow<Boolean> = _isSummarizing.asStateFlow()

    private val _activeSummary = MutableStateFlow<NotesSummary?>(null)
    val activeSummary: StateFlow<NotesSummary?> = _activeSummary.asStateFlow()

    fun summarizeNotes(title: String, subject: String, content: String) {
        viewModelScope.launch {
            _isSummarizing.value = true
            val result = repository.summarizeAndSaveNotes(title, subject, content)
            _isSummarizing.value = false
            result.onSuccess { summary ->
                _activeSummary.value = summary
                showMessage("Notes summarized & flashcards generated!")
            }.onFailure { err ->
                showMessage("AI Summarization failed: ${err.localizedMessage}")
            }
        }
    }

    fun selectNotesSummary(summary: NotesSummary) {
        _activeSummary.value = summary
    }

    fun deleteNotesSummary(summary: NotesSummary) {
        viewModelScope.launch {
            repository.deleteNotesSummary(summary)
            if (_activeSummary.value?.id == summary.id) {
                _activeSummary.value = null
            }
            showMessage("Summary deleted")
        }
    }

    // --- AI Quiz Generator State & Actions ---
    private val _isGeneratingQuiz = MutableStateFlow(false)
    val isGeneratingQuiz: StateFlow<Boolean> = _isGeneratingQuiz.asStateFlow()

    private val _activeQuiz = MutableStateFlow<QuizItem?>(null)
    val activeQuiz: StateFlow<QuizItem?> = _activeQuiz.asStateFlow()

    private val _activeQuizQuestions = MutableStateFlow<List<QuizQuestionDto>>(emptyList())
    val activeQuizQuestions: StateFlow<List<QuizQuestionDto>> = _activeQuizQuestions.asStateFlow()

    private val _userSelectedAnswers = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val userSelectedAnswers: StateFlow<Map<Int, Int>> = _userSelectedAnswers.asStateFlow()

    fun generateQuiz(topic: String, subject: String, difficulty: String, numQuestions: Int = 5) {
        viewModelScope.launch {
            _isGeneratingQuiz.value = true
            _userSelectedAnswers.value = emptyMap()
            val result = repository.generateAndSaveQuiz(topic, subject, difficulty, numQuestions)
            _isGeneratingQuiz.value = false
            result.onSuccess { quiz ->
                _activeQuiz.value = quiz
                _activeQuizQuestions.value = repository.parseQuizQuestions(quiz.questionsJson)
                showMessage("Generated ${quiz.totalQuestions} questions on $topic!")
            }.onFailure { err ->
                showMessage("Failed to generate quiz: ${err.localizedMessage}")
            }
        }
    }

    fun selectAnswer(questionIndex: Int, optionIndex: Int) {
        _userSelectedAnswers.value = _userSelectedAnswers.value + (questionIndex to optionIndex)
    }

    fun submitQuiz() {
        val quiz = _activeQuiz.value ?: return
        val questions = _activeQuizQuestions.value
        var score = 0
        questions.forEachIndexed { index, q ->
            val selected = _userSelectedAnswers.value[index]
            if (selected == q.correctIndex) {
                score++
            }
        }
        viewModelScope.launch {
            repository.updateQuizResult(quiz, score)
            _activeQuiz.value = quiz.copy(score = score, isCompleted = true)
            showMessage("Quiz Completed! Your score: $score/${questions.size}")
        }
    }

    fun loadQuizForReview(quiz: QuizItem) {
        _activeQuiz.value = quiz
        _activeQuizQuestions.value = repository.parseQuizQuestions(quiz.questionsJson)
    }

    fun deleteQuiz(quiz: QuizItem) {
        viewModelScope.launch {
            repository.deleteQuiz(quiz)
            if (_activeQuiz.value?.id == quiz.id) {
                _activeQuiz.value = null
                _activeQuizQuestions.value = emptyList()
            }
            showMessage("Quiz removed")
        }
    }

    // --- AI Assignment Helper ---
    private val _isAssignmentAiLoading = MutableStateFlow(false)
    val isAssignmentAiLoading: StateFlow<Boolean> = _isAssignmentAiLoading.asStateFlow()

    fun requestAssignmentAiHelp(assignment: Assignment, details: String) {
        viewModelScope.launch {
            _isAssignmentAiLoading.value = true
            val result = repository.getAssignmentAIHelp(assignment, details)
            _isAssignmentAiLoading.value = false
            result.onSuccess {
                showMessage("AI Assignment Guidance generated!")
            }.onFailure { err ->
                showMessage("AI Help error: ${err.localizedMessage}")
            }
        }
    }

    // --- AI Study Planner ---
    private val _isGeneratingPlan = MutableStateFlow(false)
    val isGeneratingPlan: StateFlow<Boolean> = _isGeneratingPlan.asStateFlow()

    private val _activePlan = MutableStateFlow<StudyPlan?>(null)
    val activePlan: StateFlow<StudyPlan?> = _activePlan.asStateFlow()

    fun generateStudyPlan(examTitle: String, daysRemaining: Int, dailyHours: Double, topics: String) {
        viewModelScope.launch {
            _isGeneratingPlan.value = true
            val result = repository.generateAndSaveStudyPlan(examTitle, daysRemaining, dailyHours, topics)
            _isGeneratingPlan.value = false
            result.onSuccess { plan ->
                _activePlan.value = plan
                showMessage("Study Roadmap created!")
            }.onFailure { err ->
                showMessage("Study plan generation error: ${err.localizedMessage}")
            }
        }
    }

    fun selectStudyPlan(plan: StudyPlan) {
        _activePlan.value = plan
    }

    fun updatePlanProgress(plan: StudyPlan, progress: Int) {
        viewModelScope.launch {
            repository.updateStudyPlanProgress(plan, progress)
            _activePlan.value = plan.copy(progressPercentage = progress)
        }
    }

    fun deleteStudyPlan(plan: StudyPlan) {
        viewModelScope.launch {
            repository.deleteStudyPlan(plan)
            if (_activePlan.value?.id == plan.id) {
                _activePlan.value = null
            }
            showMessage("Study plan removed")
        }
    }

    // --- Spaced Repetition Revision Scheduler ---
    fun addRevisionTopic(topicName: String, subject: String, notes: String = "") {
        viewModelScope.launch {
            repository.addRevisionTask(topicName, subject, notes)
            showMessage("Added '$topicName' to spaced repetition schedule")
        }
    }

    fun reviewTask(task: RevisionTask, wasSuccessful: Boolean) {
        viewModelScope.launch {
            repository.advanceRevisionStage(task, wasSuccessful)
            showMessage(if (wasSuccessful) "Great! Next review scheduled further out." else "Reset for sooner review.")
        }
    }

    fun deleteRevisionTask(task: RevisionTask) {
        viewModelScope.launch {
            repository.deleteRevisionTask(task)
            showMessage("Topic removed from revision schedule")
        }
    }

    // --- Pomodoro Timer State & Coroutine Loop ---
    private val _pomodoroMode = MutableStateFlow("FOCUS") // FOCUS (25m), SHORT_BREAK (5m), LONG_BREAK (15m)
    val pomodoroMode: StateFlow<String> = _pomodoroMode.asStateFlow()

    private val _pomodoroSecondsRemaining = MutableStateFlow(25 * 60)
    val pomodoroSecondsRemaining: StateFlow<Int> = _pomodoroSecondsRemaining.asStateFlow()

    private val _isPomodoroRunning = MutableStateFlow(false)
    val isPomodoroRunning: StateFlow<Boolean> = _isPomodoroRunning.asStateFlow()

    private var pomodoroJob: Job? = null

    fun setPomodoroMode(mode: String) {
        _pomodoroMode.value = mode
        _isPomodoroRunning.value = false
        pomodoroJob?.cancel()
        _pomodoroSecondsRemaining.value = when (mode) {
            "SHORT_BREAK" -> 5 * 60
            "LONG_BREAK" -> 15 * 60
            else -> 25 * 60
        }
    }

    fun togglePomodoroTimer(subjectTag: String = "General Study") {
        if (_isPomodoroRunning.value) {
            _isPomodoroRunning.value = false
            pomodoroJob?.cancel()
        } else {
            _isPomodoroRunning.value = true
            pomodoroJob = viewModelScope.launch {
                while (_pomodoroSecondsRemaining.value > 0) {
                    delay(1000L)
                    _pomodoroSecondsRemaining.value -= 1
                }
                _isPomodoroRunning.value = false
                val totalMinutes = when (_pomodoroMode.value) {
                    "SHORT_BREAK" -> 5
                    "LONG_BREAK" -> 15
                    else -> 25
                }
                repository.logPomodoroSession(totalMinutes, _pomodoroMode.value, subjectTag)
                showMessage("Pomodoro Session Complete! 🎉")
            }
        }
    }

    fun resetPomodoro() {
        _isPomodoroRunning.value = false
        pomodoroJob?.cancel()
        setPomodoroMode(_pomodoroMode.value)
    }

    // --- AI Resume Analyzer State & Actions ---
    private val _isAnalyzingResume = MutableStateFlow(false)
    val isAnalyzingResume: StateFlow<Boolean> = _isAnalyzingResume.asStateFlow()

    private val _activeResumeAnalysis = MutableStateFlow<ResumeAnalysis?>(null)
    val activeResumeAnalysis: StateFlow<ResumeAnalysis?> = _activeResumeAnalysis.asStateFlow()

    fun analyzeResume(resumeText: String, targetRole: String) {
        viewModelScope.launch {
            _isAnalyzingResume.value = true
            val result = repository.analyzeAndSaveResume(resumeText, targetRole)
            _isAnalyzingResume.value = false
            result.onSuccess { analysis ->
                _activeResumeAnalysis.value = analysis
                showMessage("Resume analyzed! ATS Score: ${analysis.atsScore}/100")
            }.onFailure { err ->
                showMessage("Resume analysis error: ${err.localizedMessage}")
            }
        }
    }

    fun selectResumeAnalysis(analysis: ResumeAnalysis) {
        _activeResumeAnalysis.value = analysis
    }

    fun deleteResumeAnalysis(analysis: ResumeAnalysis) {
        viewModelScope.launch {
            repository.deleteResumeAnalysis(analysis)
            if (_activeResumeAnalysis.value?.id == analysis.id) {
                _activeResumeAnalysis.value = null
            }
            showMessage("Resume analysis removed")
        }
    }

    // --- Notifications ---
    fun markNotificationRead(id: Long) {
        viewModelScope.launch { repository.markNotificationAsRead(id) }
    }
    fun markNotificationAsRead(id: Long) = markNotificationRead(id)

    fun markAllNotificationsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsAsRead()
            showMessage("All notifications marked as read")
        }
    }
    fun markAllNotificationsAsRead() = markAllNotificationsRead()

    fun deleteNotification(id: Long) {
        viewModelScope.launch { repository.deleteNotification(id) }
    }

    // Helper parser pass-throughs
    fun parseFlashcards(json: String) = repository.parseFlashcards(json)
    fun parseQuizQuestions(json: String) = repository.parseQuizQuestions(json)
    fun parseStudyDays(json: String) = repository.parseStudyDays(json)
}

class StudentViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val db = AppDatabase.getDatabase(application)
        val geminiService = GeminiService()
        val repository = StudentRepository(db.appDao(), geminiService)
        return StudentViewModel(application, repository) as T
    }
}
