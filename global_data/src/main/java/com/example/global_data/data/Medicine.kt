package com.example.global_data.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class Medicine(
        @PrimaryKey(autoGenerate = true) var id: Int = 0,
        var medicineName: String = "",
        var medicineType: String = "",
        var medicineCategory: String = "",
        var linkToInstruction: String = "",
        var startedTakingDate: String = "",
        var expirationDate: String = "",
        var finishingTakingDate: String = "",
        var medicineMaxAmount: String = "",
        var medicineCurrentAmount: Int = 0,
        var isAmountCountable: Boolean = true,
        var medicineTakingOftenness: String = "",
        var notes: String = ""
) : Parcelable