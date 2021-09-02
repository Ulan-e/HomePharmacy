package com.example.feature_calendar.ui.domain

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feature_calendar.ui.data.EventsRepository
import com.example.global_data.data.db.Event
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class CalendarViewModel(
        private val eventsRepository: EventsRepository
) : ViewModel() {

    @SuppressLint("SimpleDateFormat")
    private val sdf = SimpleDateFormat("dd-M-yyyy")

    val localEvents = MutableLiveData<List<Event>>()

    fun getLocalEvents(date: Date) {
        viewModelScope.launch {
            val newEvents = mutableListOf<Event>()
            val medicines = eventsRepository.getEvents()
            for (item in medicines) {
                if (newCompare(date, item.time)) {
                    newEvents.add(item)
                }
            }
            localEvents.postValue(newEvents)
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun convertStringToDate(text: String): Date {
        return sdf.parse(text)
    }

    private fun newCompare(pickedDate: Date, dbDateStr: String): Boolean {
        return sdf.format(pickedDate).equals(sdf.format(convertStringToDate(dbDateStr)))
    }

    fun updateEvent(event: Event) {
        viewModelScope.launch {
            eventsRepository.updateEvent(event)
        }
    }

    fun deleteEvent(event: Event) {
        viewModelScope.launch {
            eventsRepository.deleteeEvent(event)
        }
    }
}