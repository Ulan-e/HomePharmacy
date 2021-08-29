package com.example.core_ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.example.core_ui.databinding.MedicineCustomToolbarBinding

class MedicineCustomToolbar
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : Toolbar(context, attrs, defStyleAttr) {


    private var _binding: MedicineCustomToolbarBinding? = null

    init {
        val view = View.inflate(context, R.layout.medicine_custom_toolbar, this)
        _binding = MedicineCustomToolbarBinding.bind(view)
        applyAttributes(attrs)
    }

    private fun applyAttributes(attrs: AttributeSet?) {
        attrs?.let {
            val attributes = context.obtainStyledAttributes(it, R.styleable.MedicineCustomToolbar, 0, 0)
            with(attributes) {
                try {
                    val title = getString(R.styleable.MedicineCustomToolbar_medicine_toolbar_title)
                    _binding?.medicineToolbarTitle?.text = title
                } finally {
                    recycle()
                }
            }
        }
    }


    fun setOnClickListenerAddButton(click: (() -> Unit)? = null) {
        _binding?.medicineToolbarAddButton?.setOnClickListener {
            click?.invoke()
        }
    }
}




