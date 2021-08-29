package com.example.core_ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.example.core_ui.databinding.CalendarCustomToolbarBinding

class CalendarCustomToolbar
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : Toolbar(context, attrs, defStyleAttr) {

    private var _binding: CalendarCustomToolbarBinding? = null

    init {
        val view = View.inflate(context, R.layout.calendar_custom_toolbar, this)
        _binding = CalendarCustomToolbarBinding.bind(view)
        applyAttributes(attrs)
    }

    private fun applyAttributes(attrs: AttributeSet?) {
        attrs?.let {
            val attributes = context.obtainStyledAttributes(it, R.styleable.CalendarCustomToolbar, 0, 0)
            with(attributes) {
                try {
                    val title = getString(R.styleable.CalendarCustomToolbar_calendar_toolbar_title)
                    _binding?.calendarToolbarTitle?.text = title
                } finally {
                    recycle()
                }
            }
        }
    }


    fun setOnClickListenerAddButton(click: (() -> Unit)? = null) {
        _binding?.calendarToolbarAddButton?.setOnClickListener {
            click?.invoke()
        }
    }

    fun setOnClickListenerSearchButton(click: (() -> Unit)? = null) {
        _binding?.calendarToolbarSearchButton?.setOnClickListener {
            click?.invoke()
        }
    }

}




