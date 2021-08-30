package com.example.global_data.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Medicine::class], version = 2)
abstract class MedicineDatabase : RoomDatabase() {

    abstract fun medicinesDao(): MedicinesDao

    companion object {
        private const val DATABASE_NAME = "medicine_d"

        @Volatile
        private var instance: MedicineDatabase? = null

        fun getInstance(context: Context): MedicineDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): MedicineDatabase {
            return Room.databaseBuilder(context, MedicineDatabase::class.java, DATABASE_NAME)
                    .build()
        }
    }
}