package com.example.core_ui

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.core_ui.databinding.CustomSearchBarBinding

class CustomSearchBar
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : ConstraintLayout(context, attrs, defStyleAttr) {

    private var _binding: CustomSearchBarBinding? = null

    init {
        val view = View.inflate(context, R.layout.custom_search_bar, this)
        _binding = CustomSearchBarBinding.bind(view)
        this.background = ColorDrawable(Color.TRANSPARENT)
        _binding?.searchBarText?.background = ColorDrawable(Color.TRANSPARENT)
    }

    fun setOnFocusListener(click: (() -> Unit)? = null) {
        _binding?.searchBarText?.clearFocus()
       /* _binding?.searchBarText?.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                click?.invoke()
            }
        }*/
    }
}