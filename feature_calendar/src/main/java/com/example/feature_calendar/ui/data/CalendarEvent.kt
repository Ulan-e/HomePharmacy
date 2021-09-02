package com.example.feature_calendar.ui.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CalendarEvent(
        var id: Long = 0L,
        var name: String = "",
        var medicineName: String = "",
        var time: String,
        var pillsCount: String = ""
) : Parcelable