package com.example.feature_medicine.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.core_ui.MedicineCustomToolbar
import com.example.feature_medicine.R
import com.example.feature_medicine.data.MedicineRepository
import com.example.feature_medicine.databinding.FragmentDrugsBinding
import com.example.feature_medicine.domain.MainViewModel
import com.example.feature_medicine.domain.MedicineViewModelFactory
import com.example.global_data.data.Medicine
import com.example.global_data.data.db.MedicineDatabase

class MedicineFragment : Fragment() {

    lateinit var binding: FragmentDrugsBinding
    var mainViewModel: MainViewModel? = null

    private var medicineToolbar: MedicineCustomToolbar? = null
    private var notificationsRecyclerView: RecyclerView? = null
    private var categoriesRecyclerView: RecyclerView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_drugs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val medicineRepository: MedicineRepository by lazy {
            MedicineRepository(MedicineDatabase.getInstance(requireContext()))
        }
        val medicineViewModelFactory = MedicineViewModelFactory(medicineRepository)
        mainViewModel = ViewModelProvider(this, medicineViewModelFactory).get(MainViewModel::class.java)
        binding = FragmentDrugsBinding.bind(view)

        mainViewModel?.localMedicines?.observe(viewLifecycleOwner, {
            (notificationsRecyclerView?.adapter as MedicineWarningElementAdapter).setAllMedicinesSize(it.size)
        })

       /* binding.medicineSearchBar.setOnFocusListener {
             val action = MedicineFragmentDirections.actionDrugsDestinationFragmentToAllMedicineDestinationFragment("all", true)
            findNavController().navigate(action)
        }*/

        //initializing views
        medicineToolbar = binding.medicineToolbar
        notificationsRecyclerView = binding.notificationsRecyclerView
        categoriesRecyclerView = binding.categoriesRecyclerView

        medicineToolbar?.setOnClickListenerAddButton {
            val action = MedicineFragmentDirections.actionDrugsDestinationFragmentToMedicineInfoDestionationFragment(true, Medicine())
            findNavController().navigate(action)
        }

        notificationsRecyclerView?.adapter = MedicineWarningElementAdapter().apply {
            onAllMedicineItemClick = {
                val action = MedicineFragmentDirections.actionDrugsDestinationFragmentToAllMedicineDestinationFragment("all", false)
                findNavController().navigate(action)
            }
        }

        //creating an observer that will monitor if medicineWarningElements had changed
        mainViewModel?.warningMedicines?.observe(viewLifecycleOwner, { medicinesList ->
            (notificationsRecyclerView?.adapter as MedicineWarningElementAdapter).medicineWarningElements.clear()
            (notificationsRecyclerView?.adapter as MedicineWarningElementAdapter).medicineWarningElements.addAll(medicinesList)
            (notificationsRecyclerView?.adapter as MedicineWarningElementAdapter).notifyDataSetChanged()
        })

        //adding item decoration to notification recycle view
        notificationsRecyclerView?.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.HORIZONTAL).apply {
            setDrawable(ResourcesCompat.getDrawable(resources, R.drawable.recycle_view_horizontal_separation_drawable, null)!!)
        })

        categoriesRecyclerView?.adapter = MedicineCategoriesAdapter(object : MedicineCategoriesAdapter.Listener {
            override fun onItemClick(category: String) {
                val action = MedicineFragmentDirections.actionDrugsDestinationFragmentToAllMedicineDestinationFragment(category, false)
                findNavController().navigate(action)
            }

        })
        //creating an observer for categories list
        mainViewModel?.categories?.observe(viewLifecycleOwner, { categoriesList ->
            (categoriesRecyclerView?.adapter as MedicineCategoriesAdapter).categoriesNames.clear()
            (categoriesRecyclerView?.adapter as MedicineCategoriesAdapter).categoriesNames.addAll(categoriesList)
            (categoriesRecyclerView?.adapter as MedicineCategoriesAdapter).notifyDataSetChanged()
        })

        //adding item decoration to categoriesRecyclerView
        categoriesRecyclerView?.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL).apply {
            setDrawable(ResourcesCompat.getDrawable(resources, R.drawable.recycle_view_vertical_separation_drawable, null)!!)
        })
    }
}