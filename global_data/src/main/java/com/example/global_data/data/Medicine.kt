package com.example.global_data.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class Medicine (
        @PrimaryKey var id: Int,
        var medicineName: String,
        var medicineType: String,
        var medicineCategory: String,
        var linkToInstruction: String,
        //dd/MM/yy
        var startedTakingDate: String,
        var expirationDate: String,
        var finishingTakingDate: String,
        var medicineMaxAmount: String,
        var medicineCurrentAmount: Int,
        var isAmountCountable: Boolean,
        var medicineTakingOftenness: String,
        var notes: String,
        var warningType: Int
): Parcelable{
    constructor() : this(0, "", "", "", "", "",
            "", "", "", 0, false, "", "", 0)
}