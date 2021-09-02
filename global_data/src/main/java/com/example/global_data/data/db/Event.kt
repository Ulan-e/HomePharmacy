package com.example.global_data.data.db

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class Event(
        @PrimaryKey var id: Int = 0,
        var name: String = "",
        var medicineName: String = "",
        var startDate: String,
        var endDate: String,
        var time: String,
        var pillsCount: String = ""
) : Parcelable
