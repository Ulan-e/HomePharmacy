package com.example.feature_medicine.data

import androidx.annotation.DrawableRes

data class MedicineWarningElement(
        @DrawableRes
        var medicineFormImage: Int,
        var warningTypeText: String,
        var warningReasonText: String,
)
