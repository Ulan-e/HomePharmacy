package com.example.feature_medicine.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feature_medicine.data.MedicineRepository
import com.example.feature_medicine.data.MedicineWarningElement
import com.example.global_data.data.db.Event
import com.example.global_data.data.Medicine
import kotlinx.coroutines.launch

class MainViewModel (
    private val medicineRepository: MedicineRepository
): ViewModel() {

    private val _warningMedicines: MutableLiveData<List<MedicineWarningElement>> = MutableLiveData()
    private val _categories: MutableLiveData<ArrayList<String>> = MutableLiveData(medicineRepository.medicineCategoriesList)

    val localMedicines = MutableLiveData<List<Medicine>>()
    val localEvents = MutableLiveData<List<Event>>()

    val warningMedicines: LiveData<List<MedicineWarningElement>> get() = _warningMedicines
    val categories: LiveData<ArrayList<String>> get() = _categories

    init {
        getLocalMedicines()
        getLocalEvents()
    }

    private fun getLocalMedicines() {
        viewModelScope.launch {
            val medicines = medicineRepository.getMedicinesList()
            localMedicines.postValue(medicines)
            val warnings = medicineRepository.getMedicineWarningElementsList(medicines)
            _warningMedicines.postValue(warnings)
        }
    }

    private fun getLocalEvents() {
        viewModelScope.launch {
            val medicines = medicineRepository.getEventsList()
            localEvents.postValue(medicines)
        }
    }

    fun insertMedicine(medicine: Medicine){
        viewModelScope.launch {
            medicineRepository.insertMedicine(medicine)
        }
    }

    fun updateMedicine(medicine: Medicine){
        viewModelScope.launch {
            medicineRepository.updateMedicine(medicine)
        }
    }

    fun insertEvent(event: Event){
        viewModelScope.launch {
            medicineRepository.insertEvent(event)
        }
    }

    fun deleteMedicine(medicine: Medicine){
        viewModelScope.launch {
            medicineRepository.deleteMedicine(medicine)
        }
    }
}