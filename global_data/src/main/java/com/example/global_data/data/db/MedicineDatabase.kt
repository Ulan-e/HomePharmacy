package com.example.global_data.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.global_data.data.Medicine

@Database(entities = [Medicine::class, Event::class], version = 1)
abstract class MedicineDatabase : RoomDatabase() {

    abstract fun medicinesDao(): MedicinesDao
    abstract fun eventsDao(): EventsDao

    companion object {
        private const val DATABASE_NAME = "medicines_basedd"

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