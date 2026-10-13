package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.*

@Database(
    entities = [
        UserProfile::class,
        AttendanceSubject::class,
        AttendanceLog::class,
        Assignment::class,
        CgpaSemester::class,
        CgpaCourse::class,
        DoubtSession::class,
        DoubtMessage::class,
        NotesSummary::class,
        QuizItem::class,
        StudyPlan::class,
        RevisionTask::class,
        PomodoroSession::class,
        ResumeAnalysis::class,
        StudentNotification::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "classmate_ai_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
