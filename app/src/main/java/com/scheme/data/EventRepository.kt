package com.scheme.data

import com.scheme.models.DayEvent
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class EventRepository @Inject constructor(private val eventDao: EventDao) {
    suspend fun insert(event: DayEvent): Long {
        return eventDao.addEvent(event)
    }

    suspend fun update(event: DayEvent) {
        eventDao.update(event)
    }

    suspend fun delete(event: DayEvent) {
        eventDao.delete(event)
    }

    suspend fun deleteAll() {
        eventDao.deleteAll()
    }

    fun getAll(section: String): Flow<List<DayEvent>> {
        return eventDao.readAll(section)
    }

    suspend fun getAllAsList(): List<DayEvent> {
        return eventDao.readAllAsList()
    }

}