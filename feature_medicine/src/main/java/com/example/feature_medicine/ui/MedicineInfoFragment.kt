package com.example.feature_medicine.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.feature_medicine.R
import com.example.feature_medicine.data.MedicineRepository
import com.example.feature_medicine.databinding.FragmentMedicineInfoBinding
import com.example.feature_medicine.domain.MainViewModel
import com.example.feature_medicine.domain.MedicineViewModelFactory
import com.example.global_data.data.Medicine
import com.example.global_data.data.MedicineDatabase
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

class MedicineInfoFragment : Fragment() {

    companion object {
        const val FINISHING_DATE_ENTERING_ID = 1
        const val EXPIRATION_DATE_ENTERING_ID = 2
        const val TYPE_RADIO_GROUP_ID = 11
        const val CATEGORIES_RADIO_GROUP_ID = 12
        const val OFTENNESS_RADIO_GROUP_ID = 13

        private const val IS_CREATE_MEDICINE = "is_create_medicine"
        private const val MEDICINE_ARG = "medicine_arg"

        fun newInstance(isCreated: Boolean, medicine: Medicine) = MedicineInfoFragment().apply {
            arguments = Bundle(2).apply {
                putBoolean(IS_CREATE_MEDICINE, isCreated)
                putParcelable(MEDICINE_ARG, medicine)
            }
        }
    }

    private val itemInfo: Medicine = Medicine()
    private lateinit var model: MainViewModel

    private var _binding: FragmentMedicineInfoBinding? = null
    private val binding get() = _binding!!
    private var currentRadioGroup: Int = 0

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMedicineInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SimpleDateFormat")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val edit  = MedicineInfoFragment
        val medicineRepository: MedicineRepository by lazy {
            MedicineRepository(MedicineDatabase.getInstance(requireContext()))
        }
        val medicineViewModelFactory = MedicineViewModelFactory(medicineRepository)
        model = ViewModelProvider(this, medicineViewModelFactory).get(MainViewModel::class.java)

        binding.apply {

            val isMedicineInfo = arguments?.getBoolean(IS_CREATE_MEDICINE)
            val medicine: Medicine? = arguments?.getParcelable(MEDICINE_ARG)

            if (isMedicineInfo == true) {
                setEditModeEnable(false)
                medicine?.let { setMedicine(it) }
            } else {
                setEditModeEnable(true)
            }

            initBottomSheetDialog()

            medicineRadioButtonListBackButton.setOnClickListener {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }

            medicineInfoCustomToolbar.setOnClickListenerBackButton {
                Navigation.findNavController(view).popBackStack()
            }

            medicineInfoCustomToolbar.setOnClickListenerRightButton {
                if (checkFields()) {
                    itemInfo.id = Random().nextInt()
                    itemInfo.medicineName = medicineNameRowEditText.text.toString()
                    itemInfo.medicineMaxAmount = zEditTextMaxAmount.text.toString()
                    itemInfo.medicineCurrentAmount = zEditTextCurrentAmount.text.toString().toInt()
                    model.insertMedicine(itemInfo)
                    showSuccessCreatedMedicine(view)
                } else {
                    Snackbar.make(binding.root, "Заполните все поля", Snackbar.LENGTH_SHORT).show()
                }
            }

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

            imageButtonCategory.setOnClickListener {
                medicineRadioButtonListTitle.text = getString(R.string.categories_title)
                fullingARadioGroup(medicineRepository.medicineCategoriesList, medicineRadioButtonListRadioGroup, CATEGORIES_RADIO_GROUP_ID)
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                medicineRadioButtonListRadioGroup.setOnCheckedChangeListener { group, checkedId ->
                    val chosenRadioButton = group.findViewById<RadioButton>(checkedId)
                    zTextViewCategory.text = chosenRadioButton?.text
                    itemInfo.medicineCategory = chosenRadioButton?.text.toString()
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
                    itemInfo.medicineType = chosenRadioButton?.text.toString()
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
                    itemInfo.medicineTakingOftenness = chosenRadioButton?.text.toString()
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                }
            }
        }

        val dateFormat = SimpleDateFormat("dd/MM/yy")
        itemInfo.startedTakingDate = dateFormat.format(Date())
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

    private fun fullingARadioGroup(strings: ArrayList<String>, radioGroup: RadioGroup, radioGroupId: Int) {
        if (radioGroupId != currentRadioGroup) {
            currentRadioGroup = radioGroupId
            var stringToMark = ""
            when (radioGroupId) {
                TYPE_RADIO_GROUP_ID -> {
                    stringToMark = itemInfo.medicineType
                }
                CATEGORIES_RADIO_GROUP_ID -> {
                    stringToMark = itemInfo.medicineCategory
                }
                OFTENNESS_RADIO_GROUP_ID -> {
                    stringToMark = itemInfo.medicineTakingOftenness
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

    private fun showingDatePickerDialog(enteringDate: Int) {
        val datePickerDialog = context?.let { DatePickerDialog(it) }
        datePickerDialog?.setOnDateSetListener { _, year, month, dayOfMonth ->
            when (enteringDate) {
                FINISHING_DATE_ENTERING_ID -> {
                    itemInfo.finishingTakingDate = "$dayOfMonth/$month/$year"
                    binding.zTextViewFinishingDate.text = itemInfo.finishingTakingDate
                }
                EXPIRATION_DATE_ENTERING_ID -> {
                    itemInfo.expirationDate = "$dayOfMonth/$month/$year"
                    binding.zTextViewFreshUntil.text = itemInfo.expirationDate
                }
            }
        }
        datePickerDialog?.show()
    }

    private fun showDeleteConfirmationDialog(view: View) {
        val builder = AlertDialog.Builder(requireContext()).create()
        val dialogView: View = layoutInflater.inflate(R.layout.dialog_delete_confirmation, null)

        dialogView.apply {
            findViewById<Button>(R.id.btn_cancel).setOnClickListener {
                builder.dismiss()
            }
            findViewById<Button>(R.id.btn_delete).setOnClickListener {
                model.deleteMedicine(itemInfo)
                Navigation.findNavController(view).popBackStack()
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

    private fun showSuccessCreatedMedicine(view: View) {
        val builder = AlertDialog.Builder(requireContext())
        builder.apply {
            setTitle(getString(R.string.ready))
            setMessage(getString(R.string.added_new_medicine_text))
            setPositiveButton(getString(R.string.okey)) { dialog, _ ->
                Navigation.findNavController(view).popBackStack()
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

    private fun checkFields(): Boolean {
        binding.apply {
            return medicineNameRowEditText.text.isNotEmpty() && zTextViewType.text.isNotEmpty() &&
                    zTextViewCategory.text.isNotEmpty() && zEditTextMaxAmount.text.isNotEmpty() &&
                    zTextViewFreshUntil.text.isNotEmpty() && zTextViewFreshUntil.text != "Дата" &&
                    zEditTextCurrentAmount.text.isNotEmpty() && zTextViewTakingOftenness.text.isNotEmpty()
        }
    }

    private fun setMedicine(medicine: Medicine) = with(binding) {
        medicineInfoCustomToolbar.setTitle(medicine.medicineName)
        medicineNameRowEditText.setText(medicine.medicineName)
        zTextViewType.text = medicine.medicineType
        zTextViewCategory.text = medicine.medicineCategory
        zEditTextMaxAmount.setText(medicine.medicineMaxAmount)
        zTextViewFreshUntil.text = medicine.expirationDate
        zEditTextCurrentAmount.setText(medicine.medicineCurrentAmount)
        zTextViewTakingOftenness.text = medicine.medicineTakingOftenness
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
    }

    private fun openWebMedicineInstruction() {
        val url = "http://www.google.com"
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}