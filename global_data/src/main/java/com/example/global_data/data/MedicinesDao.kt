package com.example.global_data.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MedicinesDao {

    @Insert
    suspend fun insert(medicine: Medicine)

    @Query("SELECT * FROM medicine")
    suspend fun fetchAll(): List<Medicine>

    @Delete
    suspend fun delete(medicine: Medicine)
}