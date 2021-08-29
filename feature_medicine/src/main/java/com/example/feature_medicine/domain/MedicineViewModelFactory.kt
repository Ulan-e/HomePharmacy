package com.example.feature_medicine.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.feature_medicine.data.MedicineRepository

class MedicineViewModelFactory(
        private val repository: MedicineRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = MainViewModel(repository) as T
}