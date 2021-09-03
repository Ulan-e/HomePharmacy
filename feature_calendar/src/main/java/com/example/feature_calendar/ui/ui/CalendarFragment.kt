package com.example.feature_calendar.ui.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentUris
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.CalendarContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.feature_calendar.R
import com.example.feature_calendar.databinding.FragmentCalendarBinding
import com.example.feature_calendar.ui.data.CalendarEvent
import com.example.feature_calendar.ui.data.EventsRepository
import com.example.feature_calendar.ui.domain.CalendarViewModel
import com.example.feature_calendar.ui.domain.CalendarViewModelFactory
import com.example.global_data.data.db.MedicineDatabase
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import me.everything.providers.android.calendar.CalendarProvider
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

typealias aptekaEvent = me.everything.providers.android.calendar.Event

class CalendarFragment : Fragment(), EventsAdapter.Listener {

    companion object {
        private const val CALENDAR_ID = 1L
    }

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    @SuppressLint("SimpleDateFormat")
    private val sdf = SimpleDateFormat("dd-M-yyyy")

    @SuppressLint("SimpleDateFormat")
    private val dateFormat = SimpleDateFormat("dd/MM/yy")

    private val WEEK: Long = 7 * 24 * 60 * 60 * 1000

    private var events = mutableListOf<aptekaEvent>()

    private lateinit var model: CalendarViewModel
    private val eventsAdapter: EventsAdapter by lazy {
        EventsAdapter(this@CalendarFragment)
    }
    private val eventsRepository: EventsRepository by lazy {
        EventsRepository(MedicineDatabase.getInstance(requireContext()))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkPermissions()

        val cal = Calendar.getInstance()
        setEventBySelectedDate(cal)

        initViewModel()

        getCalendarEvents()

        initBottomSheetDialog()

        handleCalendarClick()
    }

    private fun initViewModel() {
        // init viewModel
        val eventsViewModelFactory = CalendarViewModelFactory(eventsRepository)
        model = ViewModelProvider(this, eventsViewModelFactory).get(CalendarViewModel::class.java)
    }

    private fun checkPermissions() {
        Dexter.withContext(requireContext())
                .withPermissions(
                        Manifest.permission.WRITE_CALENDAR,
                        Manifest.permission.READ_CALENDAR
                ).withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        if (report.areAllPermissionsGranted()) {

                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(p0: MutableList<com.karumi.dexter.listener.PermissionRequest>?, p1: PermissionToken?) {
                        p1?.continuePermissionRequest()
                    }
                })
                .onSameThread()
                .check()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleCalendarClick() = with(binding) {
        medicineCalendar.setOnDateChangeListener { _, i, i1, i2 ->
            val pickedCalendar = Calendar.getInstance()
            pickedCalendar.set(i, i1, i2)

            setEventBySelectedDate(pickedCalendar)

            currentDateTextView.text = dateToCurrentDateText(pickedCalendar.time, i2)
        }
    }

    private fun setEventBySelectedDate(calendar: Calendar) {
        val pickedEvents = mutableListOf<aptekaEvent>()
        val pickedDate = calendar.timeInMillis
        for (e in events) {
            if (e.rRule.contains("DAILY")) {
                if (pickedDate >= e.dTStart && pickedDate <= e.lastDate) {
                    pickedEvents.add(e)
                }
            }
            if (e.rRule.contains("WEEKLY")) {
                if (pickedDate >= e.dTStart && pickedDate <= e.lastDate) {
                    for (week in e.dTStart..e.lastDate step WEEK) {
                        val d1 = dateFormat.format(Date(week))
                        val d2 = dateFormat.format(pickedDate)

                        Log.d("ulanbek", "d1 $d1 ")
                        Log.d("ulanbek", "d2 $d2")

                        if (d1 == d2) {
                            pickedEvents.add(e)
                        }
                    }
                }
            }
        }
            showCurrentEvents(pickedEvents as ArrayList<aptekaEvent>)
    }

    private fun showCurrentEvents(data: ArrayList<aptekaEvent>) = with(binding) {
        if(data.isNotEmpty()) {
            binding.emptyList.visibility = View.GONE
        }else{
            binding.emptyList.visibility = View.VISIBLE
        }
        val resultData = mutableListOf<CalendarEvent>()
        data.forEach { resultData.add(mapLibraryEventToEvent(it)) }
        eventsAdapter.setData(resultData as java.util.ArrayList<CalendarEvent>)
        eventsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        eventsRecyclerView.adapter = eventsAdapter
    }

    private fun mapLibraryEventToEvent(event: aptekaEvent) =
            CalendarEvent(
                    id = event.id,
                    medicineName = event.title,
                    medicineCount = event.description,
                    time = event.dTStart,
                    status = event.status
            )

    private fun initBottomSheetDialog() = with(binding) {
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
                    BottomSheetBehavior.STATE_EXPANDED -> {
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

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}

        }

        bottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback)

        //setting bottom sheet title date and it's listener
        val calendar: Calendar = Calendar.getInstance()
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        currentDateTextView.text = dateToCurrentDateText(Date(), dayOfMonth)

        bottomSheetStateArrow.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_arrow_down_green, null))
    }

    private fun getCalendarEvents() {
        val calendarProvider = CalendarProvider(context)
        val fetchedEvents = calendarProvider.getEvents(CALENDAR_ID).list
        Log.d("ulanbek", "fetchedEvents ${fetchedEvents.size}")
        if (fetchedEvents != null) {
            events.addAll(fetchedEvents)
            for (item in events) {
                // Log.d("ulanbek", "event.id ${item.id}")
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun dateToCurrentDateText(date: Date, dayOfMonth: Int): String {
        val simpleDateFormatForDay = SimpleDateFormat("EEEE")
        val dayOfTheWeekLocaleName = simpleDateFormatForDay.format(date)
        val simpleDateFormatForMonth = SimpleDateFormat("MMMM")
        val monthLocaleName = simpleDateFormatForMonth.format(date)
        val currentDateTextIncomplete = "$dayOfTheWeekLocaleName, $dayOfMonth $monthLocaleName"
        return (currentDateTextIncomplete.substring(0, 1).toUpperCase(Locale.getDefault()) + currentDateTextIncomplete.substring(1))
    }

    override fun onAcceptEvent(position: Int, event: CalendarEvent) {
       // eventsAdapter.removeItem(position)
        val newEvent = CalendarEvent(
                medicineName = event.medicineName,
                medicineCount = event.medicineCount,
                time = event.time,
                status = event.status
        )

    }

    private fun deleteEvent(eventId: Int) {
        var deleteUri: Uri? = null
        deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, java.lang.String.valueOf(eventId).toLong())
        val rows: Int = requireContext().contentResolver.delete(deleteUri, null, null)
        Toast.makeText(requireContext(), "Event deleted $rows", Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}