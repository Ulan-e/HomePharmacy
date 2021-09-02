package com.example.feature_medicine.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import android.widget.TextView.OnEditorActionListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.core_ui.CustomTypeView
import com.example.feature_medicine.R
import com.example.feature_medicine.data.MedicineRepository
import com.example.feature_medicine.databinding.FragmentMedicineInfoBinding
import com.example.feature_medicine.domain.MainViewModel
import com.example.feature_medicine.domain.MedicineViewModelFactory
import com.example.global_data.data.Medicine
import com.example.global_data.data.db.MedicineDatabase
import com.example.global_data.events.CalendarEvent
import com.example.global_data.events.CalendarEventHelper
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.text.SimpleDateFormat
import java.util.*


class MedicineInfoFragment : Fragment() {

    companion object {
        private const val GOOGLE_URL = "http://www.google.com"
        const val FINISHING_DATE_ENTERING_ID = 1
        const val EXPIRATION_DATE_ENTERING_ID = 2
        const val TYPE_RADIO_GROUP_ID = 11
        const val CATEGORIES_RADIO_GROUP_ID = 12
        const val OFTENNESS_RADIO_GROUP_ID = 13

        private const val EVERY_DAY = "Ежедневно"
        private const val EVERY_WEEK = "Еженедельно"
        private const val IF_NECESSARY = "По необходимости"
    }

    private lateinit var currentMedicine: Medicine
    private lateinit var model: MainViewModel

    private var _binding: FragmentMedicineInfoBinding? = null
    private val binding get() = _binding!!
    private var currentRadioGroup: Int = 0

    private var expiringDateLong: Date? = null
    private var eventStartTime: Long = 0L

    @SuppressLint("SimpleDateFormat")
    private val dateFormat = SimpleDateFormat("dd/MM/yy")

    @SuppressLint("SimpleDateFormat")
    private val timeFormat = SimpleDateFormat("HH:mm")

    private val medicineRepository: MedicineRepository by lazy {
        MedicineRepository(MedicineDatabase.getInstance(requireContext()))
    }

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private var isCreateMedicine = false
    private var isWeek = false

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMedicineInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // get args
        val args = MedicineInfoFragmentArgs.fromBundle(requireArguments())
        isCreateMedicine = args.isCreateMedicine
        currentMedicine = args.medicine


        // init view model
        val medicineViewModelFactory = MedicineViewModelFactory(medicineRepository)
        model = ViewModelProvider(this, medicineViewModelFactory).get(MainViewModel::class.java)

        createCustomView(1, "Тип")

        createOfftenesText()

        setEditModeEnable(isCreateMedicine)

        setMedicine(currentMedicine)

        setTitleRightButton()

        initBottomSheetDialog()

        initToolbar(view)

        onClickListeners(view)

        onClickListenersForBottomSheetDialog()
    }

    private fun setTitleRightButton() = with(binding) {
        if (isCreateMedicine) {
            medicineInfoCustomToolbar.setCenterTitle(getString(R.string.new_medicine_text))
            btnDelete.visibility = View.GONE
            medicineInfoCustomToolbar.setRightButtonTitle(getString(R.string.add_text))
        } else {
            btnDelete.visibility = View.VISIBLE
            medicineInfoCustomToolbar.setRightButtonTitle(getString(R.string.change_text))
        }
    }

    private fun onClickListenersForBottomSheetDialog() = with(binding) {
        medicineRadioButtonListBackButton.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        imageButtonCategory.setOnClickListener {
            medicineRadioButtonListTitle.text = getString(R.string.categories_title)
            fullingARadioGroup(medicineRepository.medicineCategoriesList, medicineRadioButtonListRadioGroup, CATEGORIES_RADIO_GROUP_ID)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            medicineRadioButtonListRadioGroup.setOnCheckedChangeListener { group, checkedId ->
                val chosenRadioButton = group.findViewById<RadioButton>(checkedId)
                zTextViewCategory.text = chosenRadioButton?.text
                currentMedicine.medicineCategory = chosenRadioButton?.text.toString()
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }

        imageButtonType.setOnClickListener {
            medicineRadioButtonListTitle.text = getString(R.string.type_title_text)
            fullingARadioGroup(medicineRepository.medicineTypeList, medicineRadioButtonListRadioGroup, TYPE_RADIO_GROUP_ID)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            medicineRadioButtonListRadioGroup.setOnCheckedChangeListener { group, checkedId ->
                val chosenRadioButton = group.findViewById<RadioButton>(checkedId)
                zTextViewType.text = chosenRadioButton?.text
                currentMedicine.medicineType = chosenRadioButton?.text.toString()
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }

        imageButtonOftenness.setOnClickListener {
            medicineRadioButtonListTitle.text = getString(R.string.oftenness_of_taking_title_text)
            fullingARadioGroup(medicineRepository.oftennessList, medicineRadioButtonListRadioGroup, OFTENNESS_RADIO_GROUP_ID)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            medicineRadioButtonListRadioGroup.setOnCheckedChangeListener { group, checkedId ->
                val chosenRadioButton = group.findViewById<RadioButton>(checkedId)
                zTextViewTakingOftenness.text = chosenRadioButton?.text
                setRepeatType(chosenRadioButton?.text.toString())
                currentMedicine.medicineTakingOftenness = chosenRadioButton?.text.toString()
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }
    }

    private fun onClickListeners(view: View) = with(binding) {
        titleSearch.setOnClickListener {
            openWebMedicineInstruction()
        }

        imageButtonFreshUntil.setOnClickListener {
            showingDatePickerDialog(EXPIRATION_DATE_ENTERING_ID)
        }

        zTextViewFreshUntil.setOnClickListener {
            showingDatePickerDialog(EXPIRATION_DATE_ENTERING_ID)
        }

        imageButtonFinishingDate.setOnClickListener {
            showingDatePickerDialog(FINISHING_DATE_ENTERING_ID)
        }

        zTextViewFinishingDate.setOnClickListener {
            showingDatePickerDialog(FINISHING_DATE_ENTERING_ID)
        }

        imageButtonAmountPerUnit.setOnClickListener {
            zEditTextAmountPerUnit.requestFocus()
        }

        imageButtonMaxAmount.setOnClickListener {
            zEditTextMaxAmount.requestFocus()
        }

        imageButtonCurrentAmount.setOnClickListener {
            zEditTextCurrentAmount.requestFocus()
        }

        btnDelete.setOnClickListener {
            showDeleteConfirmationDialog(view)
        }
    }

    private fun initToolbar(view: View) = with(binding) {
        medicineInfoCustomToolbar.setBackTitleVisibility(!isCreateMedicine)
        medicineInfoCustomToolbar.setOnClickListenerBackButton(isCreateMedicine) {
            if (isCreateMedicine) {
                Navigation.findNavController(view)
                        .navigate(MedicineInfoFragmentDirections
                                .actionFragmentInfoToMainPage("all", false))
            } else {
                Navigation.findNavController(view)
                        .navigate(MedicineInfoFragmentDirections
                                .actionFragmentInfoToMainPage("all", false))
            }
        }

        medicineInfoCustomToolbar.setOnClickListenerRightButton {
            if (isCreateMedicine) {
                medicineInfoCustomToolbar.setRightButtonTitle(getString(R.string.add_text))
                Dexter.withContext(requireContext())
                        .withPermissions(
                                Manifest.permission.WRITE_CALENDAR,
                                Manifest.permission.READ_CALENDAR
                        ).withListener(object : MultiplePermissionsListener {
                            override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                                if (report.areAllPermissionsGranted()) {
                                    addMedicine(false)
                                }
                            }

                            override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest?>?, token: PermissionToken?) {
                                token?.continuePermissionRequest()
                            }
                        })
                        .onSameThread()
                        .check()

            } else {
                medicineInfoCustomToolbar.setRightButtonTitle(getString(R.string.ready))
                setEditModeEnable(isEnable = true)
                medicineInfoCustomToolbar.setOnClickListenerBackButton(true) {
                    Navigation.findNavController(view)
                            .navigate(MedicineInfoFragmentDirections
                                    .actionFragmentInfoToMainPage("all", false))
                }
                medicineInfoCustomToolbar.setOnClickListenerRightButton {
                    addMedicine(isUpdate = true)
                }
            }
        }
    }

    private fun initBottomSheetDialog() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.medicineRadioButtonListBottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        bottomSheetBehavior.skipCollapsed
        bottomSheetBehavior.isFitToContents = false
        val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        }
        bottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback)
    }

    private fun addMedicine(isUpdate: Boolean) = with(binding) {
        if (isEditTextsNotEmpty()) {
            currentMedicine.medicineName = medicineNameRowEditText.text.toString()
            currentMedicine.medicineMaxAmount = zEditTextMaxAmount.text.toString()
            currentMedicine.medicineCurrentAmount = zEditTextCurrentAmount.text.toString().toInt()
            currentMedicine.medicineTakingOftenness = zEditTextAmountPerUnit.text.toString()
            currentMedicine.notes = notesText.text.toString()

            currentMedicine.startedTakingDate = dateFormat.format(Date())

            if (isUpdate) {
                model.updateMedicine(currentMedicine)
            } else {
                createCalendarEvent()
                model.insertMedicine(currentMedicine)
            }
            showSuccessCreatedMedicine(isUpdate, binding.root)
        } else {
            Snackbar.make(binding.root, "Заполните все поля", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun fullingARadioGroup(strings: ArrayList<String>, radioGroup: RadioGroup, radioGroupId: Int) {
        if (radioGroupId != currentRadioGroup) {
            currentRadioGroup = radioGroupId
            var stringToMark = ""
            when (radioGroupId) {
                TYPE_RADIO_GROUP_ID -> {
                    stringToMark = currentMedicine.medicineType
                }
                CATEGORIES_RADIO_GROUP_ID -> {
                    stringToMark = currentMedicine.medicineCategory
                }
                OFTENNESS_RADIO_GROUP_ID -> {
                    stringToMark = currentMedicine.medicineTakingOftenness
                }
            }
            clearRadioGroup(radioGroup)
            for (listItem in strings) {
                val newRadioButton = RadioButton(context)
                newRadioButton.textSize = 18F
                val params = RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                params.setMargins(16, 16, 16, 16)
                newRadioButton.layoutParams = params
                if (listItem == stringToMark) {
                    newRadioButton.isChecked = true
                }
                newRadioButton.text = listItem
                radioGroup.addView(newRadioButton)
            }
        }
    }

    @SuppressLint("NewApi")
    private fun showingDatePickerDialog(enteringDate: Int) {
        val datePickerDialog = context?.let { DatePickerDialog(it) }
        datePickerDialog?.setOnDateSetListener { _, year, month, dayOfMonth ->
            val realMonth = month + 1
            when (enteringDate) {
                FINISHING_DATE_ENTERING_ID -> {
                    currentMedicine.finishingTakingDate = "$dayOfMonth/$realMonth/$year"
                    val expiringCalendar = Calendar.getInstance()
                    expiringCalendar.set(Calendar.YEAR, year)
                    expiringCalendar.set(Calendar.MONTH, month)
                    expiringCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    expiringDateLong = expiringCalendar.time
                    binding.zTextViewFinishingDate.text = currentMedicine.finishingTakingDate
                }
                EXPIRATION_DATE_ENTERING_ID -> {
                    currentMedicine.expirationDate = "$dayOfMonth/$realMonth/$year"
                    binding.zTextViewFreshUntil.text = currentMedicine.expirationDate
                }
            }
        }
        datePickerDialog?.show()
    }

    private fun showTimePick(tag: Int) {
        val calendar = Calendar.getInstance()
        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)

            val time = timeFormat.format(calendar.time)
            val linearLayout = binding.planningLayout as LinearLayout
            val customType = linearLayout.findViewWithTag<CustomTypeView>(tag) as CustomTypeView

            customType.setTakingTime(time)
            eventStartTime = calendar.timeInMillis
        }
        TimePickerDialog(requireContext(), timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true)
                .show()
    }

    private fun showDeleteConfirmationDialog(view: View) {
        val builder = AlertDialog.Builder(requireContext()).create()
        val dialogView: View = layoutInflater.inflate(R.layout.dialog_delete_confirmation, null)

        dialogView.apply {
            findViewById<Button>(R.id.btn_cancel).setOnClickListener {
                builder.dismiss()
            }
            findViewById<Button>(R.id.btn_delete).setOnClickListener {
                model.deleteMedicine(currentMedicine)
                Navigation.findNavController(view)
                        .navigate(MedicineInfoFragmentDirections
                                .actionFragmentInfoToMainPage("all", false))
                builder.dismiss()
            }

            findViewById<CheckBox>(R.id.check_box_save_data_in_calendar)
                    .setOnCheckedChangeListener { _, isChecked ->
                        Log.d("Frag", "isCheck $isChecked")
                    }
        }
        builder.apply {
            setView(dialogView)
            setCanceledOnTouchOutside(false)
            show()
        }
    }

    private fun showSuccessCreatedMedicine(isUpdate: Boolean, view: View) {
        val builder = AlertDialog.Builder(requireContext())
        val message = if (isUpdate) getString(R.string.update_medicine_text) else getString(R.string.added_new_medicine_text)
        builder.apply {
            setTitle(getString(R.string.ready))
            setMessage(message)
            setPositiveButton(getString(R.string.okey)) { dialog, _ ->
                Navigation.findNavController(view)
                        .navigate(MedicineInfoFragmentDirections
                                .actionFragmentInfoToMainPage("all", false))
                dialog.dismiss()
            }
            show()
        }
    }

    private fun clearRadioGroup(radioGroup: RadioGroup) {
        for (i in radioGroup.childCount - 1 downTo 0) {
            val o = radioGroup.getChildAt(i)
            if (o is RadioButton) {
                radioGroup.removeViewAt(i)
            }
        }
    }

    private fun isEditTextsNotEmpty(): Boolean {
        binding.apply {
            return medicineNameRowEditText.text.isNotEmpty() && zTextViewType.text.isNotEmpty() &&
                    zTextViewCategory.text.isNotEmpty() && zEditTextMaxAmount.text.isNotEmpty() &&
                    zTextViewFreshUntil.text.isNotEmpty() && zTextViewFreshUntil.text != "Дата" &&
                    zEditTextCurrentAmount.text.isNotEmpty() && zTextViewTakingOftenness.text.isNotEmpty()
        }
    }

    private fun setMedicine(medicine: Medicine) = with(binding) {
        medicineInfoCustomToolbar.setCenterTitle(medicine.medicineName)
        medicineNameRowEditText.setText(medicine.medicineName)
        zTextViewCategory.text = medicine.medicineCategory
        zEditTextMaxAmount.setText(medicine.medicineMaxAmount)
        zTextViewFreshUntil.text = medicine.expirationDate
        zEditTextCurrentAmount.setText(medicine.medicineCurrentAmount)
        zEditTextAmountPerUnit.setText(medicine.medicineTakingOftenness)
        zTextViewFinishingDate.text = medicine.finishingTakingDate
        notesText.setText(medicine.notes)

        zTextViewType.text = medicine.medicineType
        setRepeatType(medicine.medicineType)
    }

    private fun setEditModeEnable(isEnable: Boolean) = with(binding) {
        medicineNameRowEditText.isEnabled = isEnable
        medicineNameRowEditText.isClickable = isEnable
        imageButtonType.isEnabled = isEnable
        imageButtonOftenness.isEnabled = isEnable
        imageButtonCategory.isEnabled = isEnable
        imageButtonAmountPerUnit.isEnabled = isEnable
        imageButtonCurrentAmount.isEnabled = isEnable
        imageButtonFinishingDate.isEnabled = isEnable
        imageButtonInstruction.isEnabled = isEnable
        imageButtonFreshUntil.isEnabled = isEnable
        imageButtonInstruction.isEnabled = isEnable
        freshUntilTextView.isEnabled = isEnable
        zEditTextMaxAmount.isEnabled = isEnable
        zEditTextAmountPerUnit.isEnabled = isEnable
        zTextViewFinishingDate.isEnabled = isEnable
        zTextViewFreshUntil.isEnabled = isEnable
        notesText.isEnabled = isEnable
    }

    private fun createOfftenesText() = with(binding) {
        planningLayout.apply {
            zEditTextAmountPerUnit.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    val count = v.text.toString().toIntOrNull()
                    val type = if (currentMedicine.medicineType == "") "таблеток" else currentMedicine.medicineType

                    if (count != null) {
                        createCustomView(count, type)
                    }
                    hideKeyboard()
                    true
                } else false
            })
        }
    }

    private fun createCustomView(count: Int, type: String) {
        val linearLayout = binding.planningLayout as LinearLayout
        linearLayout.removeAllViews()
        for (item in 1..count) {
            val eventType = CustomTypeView(requireContext())
            val params = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
            params.addRule(RelativeLayout.CENTER_IN_PARENT)
            eventType.layoutParams = params
            eventType.setMedicineType(type)
            eventType.tag = item
            val lin = binding.planningLayout as LinearLayout
            eventType.setOnClickListenerAddButton {
                binding.zTextViewTakingOftenness.text = count.toString()
                showTimePick(item)
            }
            lin.addView(eventType)
        }
    }

    private fun createCustomView(tag: Int, takingTime: String, type: String) {
        val linearLayout = binding.planningLayout as LinearLayout
        linearLayout.removeAllViews()
        val eventType = CustomTypeView(requireContext())
        val params = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        params.addRule(RelativeLayout.CENTER_IN_PARENT)
        eventType.layoutParams = params
        eventType.setTakingTime(takingTime)
        eventType.setMedicineType(type)
        eventType.tag = tag
        val lin = binding.planningLayout as LinearLayout
        eventType.setOnClickListenerAddButton {
            showTimePick(tag)
        }
        lin.addView(eventType)
    }

    private fun createCalendarEvent() = with(binding) {
        val calendarEventHelper = CalendarEventHelper(requireContext())
        calendarEventHelper.setURI()
        if (medicineNameRowText.text.isNotEmpty()) {
            val event = expiringDateLong?.let {
                CalendarEvent(
                        id = 1,
                        title = medicineNameRowEditText.text.toString(),
                        description = currentMedicine.medicineType,
                        location = "Moscow",
                        startTime = eventStartTime,
                        endTime = it,
                        status = 0,
                        timeZone = TimeZone.getDefault().id
                )
            }
            calendarEventHelper.addEvent(isWeek, event)
        } else {
            Snackbar.make(binding.root, "Заполните все поля", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun setRepeatType(type: String) {
        when (type) {
            EVERY_DAY -> {
                setRepeatTakingMedicine(EVERY_DAY)
                isWeek = false
            }
            EVERY_WEEK -> {
                setRepeatTakingMedicine(EVERY_WEEK)
                isWeek = true
            }
            IF_NECESSARY -> {
                setRepeatTakingMedicine(IF_NECESSARY)
            }
        }
    }

    private fun setWeekOftennessVisibility(isVisible: Boolean)= with(binding){
        if(isVisible){
            oftennessPerUnitRowText.visibility = View.VISIBLE
            zEditTextAmountPerUnit.visibility = View.VISIBLE
            imageButtonAmountPerUnit.visibility = View.VISIBLE
            z7DivideLine.visibility = View.VISIBLE
        }else{
            oftennessPerUnitRowText.visibility = View.GONE
            zEditTextAmountPerUnit.visibility = View.GONE
            imageButtonAmountPerUnit.visibility = View.GONE
            z7DivideLine.visibility = View.GONE
        }
    }

    private fun setRepeatTakingMedicine(type: String) = with(binding) {
        when(type){
            EVERY_DAY -> {
                oftennessPerUnitRowText.visibility = View.VISIBLE
                zEditTextAmountPerUnit.visibility = View.VISIBLE
                imageButtonAmountPerUnit.visibility = View.VISIBLE
                z7DivideLine.visibility = View.VISIBLE
                takingFinishigDateRowText.visibility = View.VISIBLE
                zTextViewFinishingDate.visibility = View.VISIBLE
                imageButtonFinishingDate.visibility = View.VISIBLE
            }
            EVERY_WEEK -> {
                oftennessPerUnitRowText.visibility = View.GONE
                zEditTextAmountPerUnit.visibility = View.GONE
                imageButtonAmountPerUnit.visibility = View.GONE
                z7DivideLine.visibility = View.GONE
            }
            IF_NECESSARY ->{
                oftennessPerUnitRowText.visibility = View.GONE
                zEditTextAmountPerUnit.visibility = View.GONE
                imageButtonAmountPerUnit.visibility = View.GONE
                z7DivideLine.visibility = View.GONE
                takingFinishigDateRowText.visibility = View.GONE
                zTextViewFinishingDate.visibility = View.GONE
                imageButtonFinishingDate.visibility = View.GONE
            }
        }
    }

    private fun openWebMedicineInstruction() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(GOOGLE_URL)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}