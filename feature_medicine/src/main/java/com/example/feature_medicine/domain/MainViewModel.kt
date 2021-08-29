package com.example.feature_medicine.domain

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feature_medicine.data.MedicineInfoItem
import com.example.feature_medicine.data.MedicineRepository
import com.example.feature_medicine.data.MedicineWarningElement
import com.example.global_data.data.Medicine
import kotlinx.coroutines.launch

class MainViewModel (
    private val medicineRepository: MedicineRepository
): ViewModel() {

    private val _medicines: MutableLiveData<List<MedicineWarningElement>> = MutableLiveData(medicineRepository.medicineWarningElementsList)
    private val _categories: MutableLiveData<ArrayList<String>> = MutableLiveData(medicineRepository.medicineCategoriesList)
    private val _personalMedicineNumber: MutableLiveData<Int> = MutableLiveData(medicineRepository.personalMedicineNumber)
    private val _medicineInfoItemsList: MutableLiveData<ArrayList<MedicineInfoItem>> = MutableLiveData(medicineRepository.medicineInfoItemsList)

    val localMedicines = MutableLiveData<List<Medicine>>()

    val medicines: LiveData<List<MedicineWarningElement>> get() = _medicines
    val categories: LiveData<ArrayList<String>> get() = _categories
    val personalMedicineNumber: LiveData<Int> get() = _personalMedicineNumber
    val medicineInfoItemsList: LiveData<ArrayList<MedicineInfoItem>> get() = _medicineInfoItemsList

    init {
        getLocalMedicines()
    }

    private fun getLocalMedicines() {
        viewModelScope.launch {
            val medicines = medicineRepository.getMedicinesList()
            localMedicines.postValue(medicines)
            Log.d("med", "view model fetched medicine ${medicines.size}")
        }
    }

    fun insertMedicine(medicine: Medicine){
        viewModelScope.launch {
            medicineRepository.insertMedicine(medicine)
            Log.d("med", "view model insert medicine ${medicine.toString()}")
        }
    }

    fun deleteMedicine(medicine: Medicine){
        viewModelScope.launch {
            Log.d("med", "view model delete medicine $medicine")
            medicineRepository.deleteMedicine(medicine)
        }
    }
}