package com.scheme.data

import androidx.room.*
import com.scheme.models.DayEvent
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addEvent(event: DayEvent): Long

    @Update
    suspend fun update(event: DayEvent)

    @Delete
    suspend fun delete(event: DayEvent)

    @Query("DELETE FROM event_table WHERE auto<>'user'")
    suspend fun deleteAll()

    @Query("SELECT * FROM event_table WHERE auto=:sec or auto='user'")
    fun readAll(sec: String): Flow<List<DayEvent>>
}