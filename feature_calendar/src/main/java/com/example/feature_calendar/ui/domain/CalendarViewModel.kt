package com.example.feature_calendar.ui.domain

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feature_calendar.ui.data.EventsRepository
import com.example.global_data.data.Medicine
import com.example.global_data.data.db.Event
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class CalendarViewModel(
        private val eventsRepository: EventsRepository
) : ViewModel() {

    @SuppressLint("SimpleDateFormat")
    private val sdf = SimpleDateFormat("dd-M-yyyy")

    val localMedicines = MutableLiveData<List<Medicine>>()

    init {
        getLocalMedicines()
    }

    private fun getLocalMedicines() {
        viewModelScope.launch {
            val medicines = eventsRepository.getEvents()
            localMedicines.postValue(medicines)
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun convertStringToDate(text: String): Date {
        return sdf.parse(text)
    }

    private fun newCompare(pickedDate: Date, dbDateStr: String): Boolean {
        return sdf.format(pickedDate).equals(sdf.format(convertStringToDate(dbDateStr)))
    }

    fun updateEvent(medicine: Medicine) {
        viewModelScope.launch {
            eventsRepository.updateEvent(medicine)
        }
    }

    fun deleteEvent(medicine: Medicine) {
        viewModelScope.launch {
            eventsRepository.deleteEvent(medicine)
        }
    }
}