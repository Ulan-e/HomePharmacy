package com.example.core_ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import com.example.core_ui.databinding.AllMedicineCustomToolbarBinding

class AllMedicineCustomToolbar
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : Toolbar(context, attrs, defStyleAttr) {

    private var _binding: AllMedicineCustomToolbarBinding? = null

    init {
        val view = View.inflate(context, R.layout.all_medicine_custom_toolbar, this)
        _binding = AllMedicineCustomToolbarBinding.bind(view)
    }

    fun setListenerSearchView(listener: SearchView.OnQueryTextListener){
        _binding?.searchView?.setOnQueryTextListener(listener)
    }

    fun setOnClickListenerBackButton(click: (() -> Unit)? = null) {
        _binding?.allMedicineBackButton?.setOnClickListener {
            click?.invoke()
        }
    }
}