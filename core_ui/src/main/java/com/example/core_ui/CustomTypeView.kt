package com.example.core_ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import com.example.core_ui.databinding.CustomTypeBinding

class CustomTypeView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private var _binding: CustomTypeBinding? = null

    init {
        val view = View.inflate(context, R.layout.custom_type, this)
        _binding = CustomTypeBinding.bind(view)
        applyAttributes(attrs)
    }

    private fun applyAttributes(attrs: AttributeSet?) {
        attrs?.let {
            val attributes = context.obtainStyledAttributes(it, R.styleable.CalendarCustomToolbar, 0, 0)
            with(attributes) {
                try {

                } finally {
                    recycle()
                }
            }
        }
    }

    fun setOnClickListenerAddButton(click: (() -> Unit)? = null) {
        _binding?.textViewTime?.setOnClickListener {
            click?.invoke()
        }
    }

    fun setTakingTime(text: String) {
        _binding?.textViewTime?.text = text
    }

    fun setMedicineType(text: String) {
        _binding?.textViewPillsCount?.text = text
    }
}