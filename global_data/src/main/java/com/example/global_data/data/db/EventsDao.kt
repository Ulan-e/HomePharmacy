package com.example.global_data.data.db

import androidx.room.*
import com.example.global_data.data.db.Event

@Dao
interface EventsDao {

    @Insert
    suspend fun insert(event: Event)

    @Update
    suspend fun update(event: Event)

    @Query("SELECT * FROM event")
    suspend fun fetchAll(): List<Event>

    @Delete
    suspend fun delete(event: Event)
}