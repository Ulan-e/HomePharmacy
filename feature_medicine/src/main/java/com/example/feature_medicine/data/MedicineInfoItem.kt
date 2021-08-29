package com.example.feature_medicine.data

data class MedicineInfoItem (
        var medicineName: String,
        var medicineAmount: String,
        var medicineExpirationShortDate: String,
        var medicineAmountNumber: Int,
        var medicineExpirationTime: Long,
        var isThereAWarning: Int
)