package com.example.feature_calendar.ui.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CalendarEvent(
        var id: Long = 0L,
        var time: Long,
        var medicineName: String = "",
        var medicineCount: String = "",
        var status: String = "",
        var visibiltity: Int = -1
) : Parcelable