package com.scheme.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.scheme.models.DayEvent
import com.scheme.models.Lecture

@Database(entities = [Lecture::class, DayEvent::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun lectureDao(): LectureDao
    abstract fun eventDao(): EventDao

}