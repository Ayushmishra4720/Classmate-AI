package com.example.data.local

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // User Profile
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profile WHERE id = 1")
    suspend fun getUserProfileDirect(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfile)

    // Attendance Subjects
    @Query("SELECT * FROM attendance_subjects ORDER BY name ASC")
    fun getAllAttendanceSubjects(): Flow<List<AttendanceSubject>>

    @Query("SELECT * FROM attendance_subjects WHERE id = :id")
    suspend fun getAttendanceSubjectById(id: Long): AttendanceSubject?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendanceSubject(subject: AttendanceSubject): Long

    @Update
    suspend fun updateAttendanceSubject(subject: AttendanceSubject)

    @Delete
    suspend fun deleteAttendanceSubject(subject: AttendanceSubject)

    // Attendance Logs
    @Query("SELECT * FROM attendance_logs WHERE subjectId = :subjectId ORDER BY dateMillis DESC")
    fun getLogsForSubject(subjectId: Long): Flow<List<AttendanceLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendanceLog(log: AttendanceLog): Long

    // Assignments
    @Query("SELECT * FROM assignments ORDER BY dueDateMillis ASC")
    fun getAllAssignments(): Flow<List<Assignment>>

    @Query("SELECT * FROM assignments WHERE status != 'SUBMITTED' ORDER BY dueDateMillis ASC")
    fun getPendingAssignments(): Flow<List<Assignment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignment(assignment: Assignment): Long

    @Update
    suspend fun updateAssignment(assignment: Assignment)

    @Delete
    suspend fun deleteAssignment(assignment: Assignment)

    // CGPA Semesters & Courses
    @Query("SELECT * FROM cgpa_semesters ORDER BY semesterNumber ASC")
    fun getAllSemesters(): Flow<List<CgpaSemester>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSemester(semester: CgpaSemester): Long

    @Update
    suspend fun updateSemester(semester: CgpaSemester)

    @Delete
    suspend fun deleteSemester(semester: CgpaSemester)

    @Query("SELECT * FROM cgpa_courses WHERE semesterId = :semesterId")
    fun getCoursesForSemester(semesterId: Long): Flow<List<CgpaCourse>>

    @Query("SELECT * FROM cgpa_courses")
    fun getAllCourses(): Flow<List<CgpaCourse>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course: CgpaCourse): Long

    @Delete
    suspend fun deleteCourse(course: CgpaCourse)

    @Query("DELETE FROM cgpa_courses WHERE semesterId = :semesterId")
    suspend fun deleteCoursesForSemester(semesterId: Long)

    // Doubt Sessions & Messages
    @Query("SELECT * FROM doubt_sessions ORDER BY createdAt DESC")
    fun getAllDoubtSessions(): Flow<List<DoubtSession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDoubtSession(session: DoubtSession): Long

    @Delete
    suspend fun deleteDoubtSession(session: DoubtSession)

    @Query("SELECT * FROM doubt_messages WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    fun getMessagesForSession(sessionId: Long): Flow<List<DoubtMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDoubtMessage(message: DoubtMessage): Long

    @Query("DELETE FROM doubt_messages WHERE sessionId = :sessionId")
    suspend fun deleteMessagesForSession(sessionId: Long)

    // Notes Summaries
    @Query("SELECT * FROM notes_summaries ORDER BY createdAt DESC")
    fun getAllNotesSummaries(): Flow<List<NotesSummary>>

    @Query("SELECT * FROM notes_summaries WHERE id = :id")
    suspend fun getNotesSummaryById(id: Long): NotesSummary?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotesSummary(summary: NotesSummary): Long

    @Delete
    suspend fun deleteNotesSummary(summary: NotesSummary)

    // Quizzes
    @Query("SELECT * FROM quizzes ORDER BY dateTaken DESC")
    fun getAllQuizzes(): Flow<List<QuizItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuiz(quiz: QuizItem): Long

    @Update
    suspend fun updateQuiz(quiz: QuizItem)

    @Delete
    suspend fun deleteQuiz(quiz: QuizItem)

    // Study Plans
    @Query("SELECT * FROM study_plans ORDER BY targetDateMillis ASC")
    fun getAllStudyPlans(): Flow<List<StudyPlan>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudyPlan(plan: StudyPlan): Long

    @Update
    suspend fun updateStudyPlan(plan: StudyPlan)

    @Delete
    suspend fun deleteStudyPlan(plan: StudyPlan)

    // Revision Tasks
    @Query("SELECT * FROM revision_tasks ORDER BY nextReviewDate ASC")
    fun getAllRevisionTasks(): Flow<List<RevisionTask>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRevisionTask(task: RevisionTask): Long

    @Update
    suspend fun updateRevisionTask(task: RevisionTask)

    @Delete
    suspend fun deleteRevisionTask(task: RevisionTask)

    // Pomodoro Sessions
    @Query("SELECT * FROM pomodoro_sessions ORDER BY completedAt DESC")
    fun getAllPomodoroSessions(): Flow<List<PomodoroSession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPomodoroSession(session: PomodoroSession): Long

    // Resume Analyses
    @Query("SELECT * FROM resume_analyses ORDER BY analyzedAt DESC")
    fun getAllResumeAnalyses(): Flow<List<ResumeAnalysis>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResumeAnalysis(analysis: ResumeAnalysis): Long

    @Delete
    suspend fun deleteResumeAnalysis(analysis: ResumeAnalysis)

    // Notifications
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<StudentNotification>>

    @Query("SELECT COUNT(*) FROM notifications WHERE isRead = 0")
    fun getUnreadNotificationCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: StudentNotification): Long

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationAsRead(id: Long)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllNotificationsAsRead()

    @Query("DELETE FROM notifications WHERE id = :id")
    suspend fun deleteNotification(id: Long)
}
