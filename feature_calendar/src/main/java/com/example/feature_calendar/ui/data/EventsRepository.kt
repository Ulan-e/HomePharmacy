package com.example.feature_calendar.ui.data

import android.util.Log
import com.example.global_data.data.db.Event
import com.example.global_data.data.db.MedicineDatabase

class EventsRepository(
        private val medicineDatabase: MedicineDatabase
) {

    companion object {
        private const val TAG = "MedicineRepository"
    }

    private var events = mutableListOf<Event>()

    suspend fun getEvents(): List<Event> {
        val events = medicineDatabase.eventsDao().fetchAll()
        this.events.clear()
        this.events.addAll(events)
        Log.d(TAG, "Size of events ${events.size}")
        return events
    }

    suspend fun updateEvent(event: Event) {
        medicineDatabase.eventsDao().update(event)
        Log.d(TAG, "inserted event $event")
    }
    suspend fun deleteeEvent(event: Event) {
        medicineDatabase.eventsDao().delete(event)
        Log.d(TAG, "inserted event $event")
    }
}