package com.example.global_data.data.db

import androidx.room.*
import com.example.global_data.data.Medicine

@Dao
interface MedicinesDao {

    @Insert
    suspend fun insert(medicine: Medicine)

    @Update
    suspend fun update(medicine: Medicine)

    @Query("SELECT * FROM medicine")
    suspend fun fetchAll(): List<Medicine>

    @Delete
    suspend fun delete(medicine: Medicine)
}