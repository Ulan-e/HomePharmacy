package com.example.feature_medicine.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.feature_medicine.R
import com.example.feature_medicine.databinding.CategoryElementBinding
import com.example.feature_medicine.ui.MedicineCategoriesAdapter.*

class MedicineCategoriesAdapter(
        private val listener: Listener
) : RecyclerView.Adapter<ViewHolder>() {

    val categoriesNames: ArrayList<String> = arrayListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.category_element, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = categoriesNames.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val binding = CategoryElementBinding.bind(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.categoryButton.text = categoriesNames[position]
        holder.binding.categoryButton.background = ColorDrawable(Color.TRANSPARENT)
        holder.binding.categoryButton.setOnClickListener {
            listener.onItemClick(categoriesNames[position])
        }
    }

    interface Listener{
        fun onItemClick(category: String)
    }
}