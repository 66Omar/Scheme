package com.scheme.data

import com.scheme.models.Lecture
import com.scheme.retrofit.LectureRetrofit
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LectureRepository @Inject constructor(
    private val lectureDao: LectureDao,
    private val lectureRetrofit: LectureRetrofit
    ) {

    suspend fun requestData(university: String, faculty: String, year: String): List<Lecture> {
        val items = ArrayList<Lecture>()
        val lectures = lectureRetrofit.getData(university, faculty, year)
        for (lecture in lectures.lectures) {
            val temp = Lecture(lecture[0], lecture[1], lecture[2], lecture[3], lecture[4],
                lecture[5].toInt(), lecture[6].toInt(), lecture[7].toInt(), lecture[8].toInt(), 1)
            items.add(temp)
        }
        return items.toList()
    }

    suspend fun getUniversities(): List<String> {
        return lectureRetrofit.getUniversities().options
    }

    suspend fun getFaculties(university: String): List<String> {
        return lectureRetrofit.getFaculties(university).options
    }

    suspend fun getYears(university: String, faculty: String): List<String> {
        return lectureRetrofit.getYears(university, faculty).options
    }

    suspend fun getSections(university: String, faculty: String, year: String): List<String> {
        return lectureRetrofit.getSections(university, faculty, year).options
    }

    suspend fun getVersion(university: String, faculty: String, year: String): String {
        return lectureRetrofit.getVersion(university, faculty, year).version
    }
    
    suspend fun insert(lecture: Lecture) {
        lectureDao.addLecture(lecture)
    }

    suspend fun delete(lecture: Lecture) {
        lectureDao.deleteLecture(lecture)
    }

    suspend fun deleteAll() {
        lectureDao.deleteAll()
    }

    fun getAll(section: String): Flow<List<Lecture>>? {
        return lectureDao.readAllLectures(section)
    }

    fun getLectureTitles(): Flow<List<String>> {
        return lectureDao.getLectureTitles()
    }

    fun getDoctorNames(lecture: String): Flow<List<String>> {
        return lectureDao.getDoctorNames(lecture)
    }

    fun getFilteredLectures(lec: String, doc: String): Flow<List<Lecture>> {
        return lectureDao.getFilteredLectures(lec, doc)
    }

    fun getAllSections(): Flow<List<String>> {
        return lectureDao.getAllSections()
    }

}