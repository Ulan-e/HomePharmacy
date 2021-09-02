package com.example.feature_medicine.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.feature_medicine.R
import com.example.feature_medicine.data.MedicineInfoItem
import com.example.feature_medicine.databinding.MedicineInformationItemBinding
import java.text.SimpleDateFormat

class MedicineInfoItemAdapter : RecyclerView.Adapter<MedicineInfoItemAdapter.ViewHolder>() {

    var medicineInfoItemsList: ArrayList<MedicineInfoItem> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.medicine_information_item, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = medicineInfoItemsList[position]
        holder.binding.medicineNameText.text = currentItem.medicineName
        holder.binding.medicineAmountText.text = currentItem.medicineAmount

        val simpleStringToDataFormat = SimpleDateFormat("dd/MM/yy")
        val dateToConvert = simpleStringToDataFormat.parse(currentItem.medicineExpirationShortDate)
        val simpleDateFormatForYear = SimpleDateFormat("yyyy")
        val yearText = simpleDateFormatForYear.format(dateToConvert)
        val simpleDateFormatForMonth = SimpleDateFormat("MMMM")
        val monthLocaleName = simpleDateFormatForMonth.format(dateToConvert)
        holder.binding.medicineExpirationDateText.text="$monthLocaleName $yearText"

        when (currentItem.isThereAWarning) {
            NO_WARNING ->  {
                holder.binding.medicineItemAttentionIconDivideLine.visibility = View.GONE
                holder.binding.allMedicineWarningIcon.visibility = View.GONE
                holder.binding.medicineExpirationDateText.setTextColor(Color.BLACK)
                holder.binding.medicineAmountText.setTextColor(ContextCompat.getColor(holder.binding.allMedicineWarningIcon.context, R.color.main_grey))
            }
            FEW_MEDICINE_LEFT -> {
                holder.binding.medicineItemAttentionIconDivideLine.visibility = View.VISIBLE
                holder.binding.allMedicineWarningIcon.visibility = View.VISIBLE
                holder.binding.medicineAmountText.setTextColor(ContextCompat.getColor(holder.binding.allMedicineWarningIcon.context, R.color.orange))
                holder.binding.medicineExpirationDateText.setTextColor(Color.BLACK)
            }
            NO_MEDICINE_LEFT -> {
                holder.binding.medicineItemAttentionIconDivideLine.visibility = View.VISIBLE
                holder.binding.allMedicineWarningIcon.visibility = View.VISIBLE
                holder.binding.medicineAmountText.setTextColor(Color.RED)
                holder.binding.medicineExpirationDateText.setTextColor(Color.BLACK)
            }
            MEDICINE_EXPIRING -> {
                holder.binding.medicineItemAttentionIconDivideLine.visibility = View.VISIBLE
                holder.binding.allMedicineWarningIcon.visibility = View.VISIBLE
                holder.binding.medicineExpirationDateText.setTextColor(ContextCompat.getColor(holder.binding.allMedicineWarningIcon.context, R.color.orange))
                holder.binding.medicineAmountText.setTextColor(ContextCompat.getColor(holder.binding.allMedicineWarningIcon.context, R.color.main_grey))
            }
            MEDICINE_EXPIRED -> {
                holder.binding.medicineItemAttentionIconDivideLine.visibility = View.VISIBLE
                holder.binding.allMedicineWarningIcon.visibility = View.VISIBLE
                holder.binding.medicineExpirationDateText.setTextColor(Color.RED)
                holder.binding.medicineAmountText.setTextColor(ContextCompat.getColor(holder.binding.allMedicineWarningIcon.context, R.color.main_grey))
            }
        }
    }

    override fun getItemCount(): Int = medicineInfoItemsList.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val binding = MedicineInformationItemBinding.bind(view)
    }

    companion object {
        private const val NO_WARNING = 0
        private const val FEW_MEDICINE_LEFT : Int = 1
        private const val MEDICINE_EXPIRING : Int = 2
        private const val NO_MEDICINE_LEFT : Int = 3
        private const val MEDICINE_EXPIRED : Int = 4
    }
}