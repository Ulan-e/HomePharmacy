package com.example.feature_medicine.ui

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.feature_medicine.R
import com.example.feature_medicine.data.MedicineRepository
import com.example.feature_medicine.databinding.FragmentAllMedicineBinding
import com.example.feature_medicine.domain.MainViewModel
import com.example.feature_medicine.domain.MedicineViewModelFactory
import com.example.global_data.data.Medicine
import com.example.global_data.data.MedicineDatabase

class AllMedicineFragment : Fragment() {

    private lateinit var binding: FragmentAllMedicineBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var medicineSortingSpinner: Spinner
    private lateinit var medicineAdapter: MedicineAdapter

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_all_medicine, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val medicineRepository: MedicineRepository by lazy {
            MedicineRepository(MedicineDatabase.getInstance(requireContext()))
        }
        val medicineViewModelFactory = MedicineViewModelFactory(medicineRepository)
        mainViewModel = ViewModelProvider(this, medicineViewModelFactory).get(MainViewModel::class.java)

        mainViewModel.localMedicines.observe(viewLifecycleOwner, { medicineInfoItemsList ->
            initRecyclerView(medicineInfoItemsList)
        })

        binding = FragmentAllMedicineBinding.bind(view).apply {
            allMedicineCustomToolbar.setOnClickListenerBackButton {
                Navigation.findNavController(view).popBackStack()
            }
            medicineSortingSpinner = sortingSpinner
            medicineAdapter = MedicineAdapter(object : MedicineAdapter.Listener{
                override fun onItemClick(medicine: Medicine) {

                }
            })
            allMedicineRecycleView.adapter = medicineAdapter
        }

        ArrayAdapter.createFromResource(
                requireContext(),
                R.array.sorting_options_array,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            medicineSortingSpinner.adapter = adapter
        }

        medicineSortingSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            private val BY_ALPHABET: Int = 0
            private val BY_EXPIRING_DATE: Int = 1
            private val BY_AMOUNT: Int = 2

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position) {
                    BY_ALPHABET -> {
                        medicineAdapter.medicineInfoItemsList.sortWith(compareBy { it.medicineName })
                        medicineAdapter.notifyDataSetChanged()
                    }
                    BY_EXPIRING_DATE -> {
                        /* medicineAdapter.medicineInfoItemsList.sortWith(compareBy { it.medicineExpirationTime })
                         medicineAdapter.notifyDataSetChanged()*/
                    }
                    BY_AMOUNT -> {
                        /* medicineAdapter.medicineInfoItemsList.sortWith(compareBy { it.medicineAmountNumber })
                         medicineAdapter.notifyDataSetChanged()*/
                    }
                }
            }
        }

        /*       mainViewModel.medicineInfoItemsList.observe(viewLifecycleOwner, { medicineInfoItemsList ->
                   (medicineInfoRecyclerView.adapter as MedicineInfoItemAdapter).medicineInfoItemsList.clear()
                   (medicineInfoRecyclerView.adapter as MedicineInfoItemAdapter).medicineInfoItemsList.addAll(medicineInfoItemsList)
                   (medicineInfoRecyclerView.adapter as MedicineInfoItemAdapter).notifyDataSetChanged()
               })*/



        binding.allMedicineRecycleView.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL).apply {
            setDrawable(ResourcesCompat.getDrawable(resources, R.drawable.recycler_view_vertical_grey_separation_line, null)!!)
        })
    }

    private fun initRecyclerView(list: List<Medicine>) {
        for (i in list) {
            Log.d("med", "fetched lekartvo = ${i}")
        }
        medicineAdapter.medicineInfoItemsList.clear()
        medicineAdapter.medicineInfoItemsList.addAll(list)
        medicineAdapter.notifyDataSetChanged()
    }
}