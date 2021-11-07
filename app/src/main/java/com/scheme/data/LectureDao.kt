package com.scheme.data

import androidx.room.*
import com.scheme.models.Lecture
import kotlinx.coroutines.flow.Flow

@Dao
interface LectureDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addLecture(lecture: Lecture)

    @Delete
    suspend fun deleteLecture(lecture: Lecture)

    @Update
    suspend fun update(lecture: Lecture)

    @Query("DELETE FROM lecture_table WHERE auto<>0")
    suspend fun deleteAll()

    @Query("SELECT * FROM lecture_table WHERE section=:sec AND auto=1 or auto = 0")
    fun readAllLectures(sec: String?): Flow<List<Lecture>>

    @Query("SELECT * FROM lecture_table WHERE section=:sec AND auto=1 or auto = 0")
    fun readAllLecturesAsList(sec: String?): List<Lecture>

    @Query("SELECT DISTINCT lectureName FROM lecture_table")
     fun getLectureTitles(): Flow<List<String>>

    @Query("SELECT DISTINCT doctor FROM lecture_table WHERE lectureName=:lec")
     fun getDoctorNames(lec: String): Flow<List<String>>

    @Query("SELECT * from lecture_table WHERE lectureName=:lec AND doctor=:doc")
    fun getFilteredLectures(lec: String, doc: String): Flow<List<Lecture>>

    @Query("SELECT DISTINCT section FROM lecture_table")
    fun getAllSections(): Flow<List<String>>

}