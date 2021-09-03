package com.example.feature_medicine.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.feature_medicine.R
import com.example.feature_medicine.databinding.MedicineInformationItemBinding
import com.example.global_data.data.Medicine
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MedicineAdapter(
        private val listener: Listener
) : RecyclerView.Adapter<MedicineAdapter.ViewHolder>(), Filterable {

    var medicinesList: ArrayList<Medicine> = arrayListOf()
    private var medicinesListFiltered: ArrayList<Medicine> = arrayListOf()

    fun setData(data: ArrayList<Medicine>) {
        medicinesList = data
        medicinesListFiltered = medicinesList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.medicine_information_item, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = medicinesListFiltered[position]
        holder.binding.root.setOnClickListener { listener.onItemClick(currentItem) }
        holder.binding.medicineNameText.text = currentItem.medicineName
        holder.binding.medicineAmountText.text =
                "${currentItem.medicineCurrentAmount}/${currentItem.medicineMaxAmount}"

        val simpleStringToDataFormat = SimpleDateFormat("dd/MM/yy")
        val dateToConvert = simpleStringToDataFormat.parse(currentItem.expirationDate)
        val simpleDateFormatForYear = SimpleDateFormat("yyyy")
        val yearText = simpleDateFormatForYear.format(dateToConvert)
        val simpleDateFormatForMonth = SimpleDateFormat("MMMM")
        val monthLocaleName = simpleDateFormatForMonth.format(dateToConvert)
        holder.binding.medicineExpirationDateText.text = "$monthLocaleName $yearText"

        if (currentItem.medicineCurrentAmount <= 3) {
            holder.binding.medicineItemAttentionIconDivideLine.visibility = View.VISIBLE
            holder.binding.allMedicineWarningIcon.visibility = View.VISIBLE
            holder.binding.medicineAmountText.setTextColor(ContextCompat.getColor(holder.binding.allMedicineWarningIcon.context, R.color.orange))
            holder.binding.medicineExpirationDateText.setTextColor(Color.BLACK)
        } else if (currentItem.medicineCurrentAmount <= 0) {
            holder.binding.medicineItemAttentionIconDivideLine.visibility = View.VISIBLE
            holder.binding.allMedicineWarningIcon.visibility = View.VISIBLE
            holder.binding.medicineAmountText.setTextColor(Color.RED)
            holder.binding.medicineExpirationDateText.setTextColor(Color.BLACK)
        }

        when (getMedicineExpiringStatus(currentItem)) {
            ExpiringStatus.NOT_EXPIRING -> {
                holder.binding.medicineItemAttentionIconDivideLine.visibility = View.GONE
                holder.binding.allMedicineWarningIcon.visibility = View.GONE
                holder.binding.medicineExpirationDateText.setTextColor(Color.BLACK)
                holder.binding.medicineAmountText.setTextColor(ContextCompat.getColor(holder.binding.allMedicineWarningIcon.context, R.color.main_grey))
            }
            ExpiringStatus.EXPIRING -> {
                holder.binding.medicineItemAttentionIconDivideLine.visibility = View.VISIBLE
                holder.binding.allMedicineWarningIcon.visibility = View.VISIBLE
                holder.binding.medicineExpirationDateText.setTextColor(ContextCompat.getColor(holder.binding.allMedicineWarningIcon.context, R.color.orange))
                holder.binding.medicineAmountText.setTextColor(ContextCompat.getColor(holder.binding.allMedicineWarningIcon.context, R.color.main_grey))
            }
            ExpiringStatus.EXPIRED -> {
                holder.binding.medicineItemAttentionIconDivideLine.visibility = View.VISIBLE
                holder.binding.allMedicineWarningIcon.visibility = View.VISIBLE
                holder.binding.medicineExpirationDateText.setTextColor(Color.RED)
                holder.binding.medicineAmountText.setTextColor(ContextCompat.getColor(holder.binding.allMedicineWarningIcon.context, R.color.main_grey))
            }
        }
    }

    override fun getItemCount(): Int = medicinesListFiltered.size

    @SuppressLint("SimpleDateFormat")
    private fun getMedicineExpiringStatus(item: Medicine): ExpiringStatus {
        val secondsInAMonth: Long = 30 * 24 * 60 * 60
        val dateFormat = SimpleDateFormat("dd/MM/yy")
        val currentDate = Date()
        val expirationDate = dateFormat.parse(item.expirationDate)
        if (expirationDate.time - currentDate.time <= 0) {
            return ExpiringStatus.EXPIRED
        }
        if ((expirationDate.time - currentDate.time) / 1000 <= secondsInAMonth) {
            return ExpiringStatus.EXPIRING
        }
        return ExpiringStatus.NOT_EXPIRING
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = MedicineInformationItemBinding.bind(view)
    }

    interface Listener {
        fun onItemClick(medicine: Medicine)
    }

    enum class ExpiringStatus {
        NOT_EXPIRING,
        EXPIRING,
        EXPIRED
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint?.toString()?.toLowerCase(Locale.ROOT) ?: ""
                if (charString.isEmpty()) medicinesListFiltered = medicinesList else {
                    val filteredList = ArrayList<Medicine>()
                    medicinesList
                            .filter {
                                (it.medicineName.toLowerCase(Locale.ROOT).contains(charString))
                            }
                            .forEach {
                                filteredList.add(it)
                            }
                    medicinesListFiltered = filteredList

                }
                return FilterResults().apply { values = medicinesListFiltered }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {

                medicinesListFiltered = if (results?.values == null)
                    ArrayList()
                else
                    results.values as ArrayList<Medicine>
                notifyDataSetChanged()
            }
        }
    }
}