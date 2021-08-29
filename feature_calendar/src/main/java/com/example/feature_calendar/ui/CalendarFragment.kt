package com.example.feature_calendar.ui

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.example.feature_calendar.R
import com.example.feature_calendar.databinding.FragmentCalendarBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

class CalendarFragment : Fragment() {

    private lateinit var binding: FragmentCalendarBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding = FragmentCalendarBinding.bind(view).apply {
            //setting on click listeners
            calendarToolbar.setOnClickListenerAddButton {}
            calendarToolbar.setOnClickListenerSearchButton {}

            //bottom sheet settings
            val bottomSheetBehavior = BottomSheetBehavior.from(medicineNotificationsBottomSheet)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            bottomSheetStateArrow.setOnClickListener { bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED }
            bottomSheetBehavior.isHideable = false
            bottomSheetBehavior.isFitToContents = false
            bottomSheetBehavior.peekHeight = 96
            bottomSheetBehavior.halfExpandedRatio = 0.375F

            val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState) {
                        BottomSheetBehavior.STATE_EXPANDED ->
                        {
                            bottomSheetStateArrow.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_arrow_down_green, null))
                            bottomSheetStateArrow.setOnClickListener { bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED }
                        }
                        BottomSheetBehavior.STATE_HALF_EXPANDED -> bottomSheetStateArrow.setImageDrawable(null)
                        BottomSheetBehavior.STATE_COLLAPSED -> {
                            bottomSheetStateArrow.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_arrow_up, null))
                            bottomSheetStateArrow.setOnClickListener { bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED }
                        }
                        BottomSheetBehavior.STATE_DRAGGING -> bottomSheetStateArrow.setImageDrawable(null)
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                }

            }

            bottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback)

            //setting bottom sheet title date and it's listener
            val calendar : Calendar = Calendar.getInstance()
            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
            currentDateTextView.text = dateToCurrentDateText(Date(), dayOfMonth)

            bottomSheetStateArrow.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_arrow_down_green, null))

            medicineCalendar.setOnDateChangeListener { _, i, i1, i2 ->
                val currentLocalDate: LocalDate = LocalDate.of(i, i1 + 1, i2)
                val currentDate = java.sql.Date.from(currentLocalDate.atStartOfDay()
                        .atZone(ZoneId.systemDefault())
                        .toInstant())
                currentDateTextView.text = dateToCurrentDateText(currentDate, currentLocalDate.dayOfMonth)
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun dateToCurrentDateText(dateToConvert: Date, dayOfMonth: Int) : String {
        val simpleDateFormatForDay = SimpleDateFormat("EEEE")
        val dayOfTheWeekLocaleName = simpleDateFormatForDay.format(dateToConvert)
        val simpleDateFormatForMonth = SimpleDateFormat("MMMM")
        val monthLocaleName = simpleDateFormatForMonth.format(dateToConvert)
        val currentDateTextIncomplete = "$dayOfTheWeekLocaleName, $dayOfMonth $monthLocaleName"
        return (currentDateTextIncomplete.substring(0, 1).toUpperCase(Locale.getDefault()) + currentDateTextIncomplete.substring(1))
    }
}