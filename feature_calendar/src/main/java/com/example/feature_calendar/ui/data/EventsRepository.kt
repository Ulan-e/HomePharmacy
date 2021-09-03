package com.example.feature_calendar.ui.data

import android.util.Log
import com.example.global_data.data.Medicine
import com.example.global_data.data.db.Event
import com.example.global_data.data.db.MedicineDatabase

class EventsRepository(
        private val medicineDatabase: MedicineDatabase
) {

    companion object {
        private const val TAG = "MedicineRepository"
    }

    private var medicines = mutableListOf<Medicine>()

    suspend fun getEvents(): List<Medicine> {
        val medicines = medicineDatabase.medicinesDao().fetchAll()
        this.medicines.clear()
        this.medicines.addAll(medicines)
        Log.d(TAG, "Size of medicines ${medicines.size}")
        return medicines
    }

    suspend fun updateEvent(medicine: Medicine) {
        medicineDatabase.medicinesDao().update(medicine)
        Log.d(TAG, "updated medicine $medicine")
    }

    suspend fun deleteEvent(medicine: Medicine) {
        medicineDatabase.medicinesDao().delete(medicine)
        Log.d(TAG, "deleted medicine $medicine")
    }
}