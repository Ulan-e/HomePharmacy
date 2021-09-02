package com.example.feature_medicine.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.feature_medicine.R
import com.example.feature_medicine.data.MedicineWarningElement
import com.example.feature_medicine.databinding.AllMedicineElementBinding
import com.example.feature_medicine.databinding.MedicineWarningElementBinding
private const val FIRST_ITEM_VIEW_TYPE = 1
private const val OTHER_ITEMS_VIEW_TYPE = 2

class MedicineWarningElementAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val medicineWarningElements: ArrayList<MedicineWarningElement> = arrayListOf()
    var onAllMedicineItemClick: (() -> Unit)? = null
    var personalMedicineNumber: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == FIRST_ITEM_VIEW_TYPE) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.all_medicine_element, parent, false)
            AllMedicineViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.medicine_warning_element, parent, false)
            MedicineWarningElementViewHolder(view)
        }
    }

    override fun getItemCount(): Int = medicineWarningElements.size

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            FIRST_ITEM_VIEW_TYPE
        } else {
            OTHER_ITEMS_VIEW_TYPE
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == OTHER_ITEMS_VIEW_TYPE) {
            val currentWarningElement: MedicineWarningElement = medicineWarningElements[position]
            holder as MedicineWarningElementViewHolder
            holder.medicineWarningElementBinding.medicineFormIcon.setImageResource(currentWarningElement.medicineFormImage)
            holder.medicineWarningElementBinding.medicineWarningType.text = currentWarningElement.warningTypeText
            holder.medicineWarningElementBinding.medicineWarningCause.text = currentWarningElement.warningReasonText
        } else {
            holder as AllMedicineViewHolder
            holder.allMedicineElementBinding.allMedicineNumber.text = personalMedicineNumber.toString()
            holder.itemView.setOnClickListener {
                onAllMedicineItemClick?.invoke()
            }
        }
    }

    fun setAllMedicinesSize(size: Int){
        this.personalMedicineNumber = size
        notifyDataSetChanged()
    }

    inner class MedicineWarningElementViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val medicineWarningElementBinding = MedicineWarningElementBinding.bind(view)
    }

    inner class AllMedicineViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val allMedicineElementBinding = AllMedicineElementBinding.bind(view)
    }
}