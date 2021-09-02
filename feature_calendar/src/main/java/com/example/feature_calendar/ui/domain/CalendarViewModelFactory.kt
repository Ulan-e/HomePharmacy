package com.example.feature_calendar.ui.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.feature_calendar.ui.data.EventsRepository

class CalendarViewModelFactory(
        private val repository: EventsRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = CalendarViewModel(repository) as T
}