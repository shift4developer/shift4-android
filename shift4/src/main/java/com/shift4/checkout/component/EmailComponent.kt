package com.shift4.checkout.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.core.widget.addTextChangedListener
import com.shift4.R
import com.shift4.databinding.ComShift4LayoutEmailBinding


internal class EmailComponent @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    private var emailFlag = false
    var emailChangedListener: (String?) -> Unit = {}
    var email: String?
        get() {
            return binding.textInputEmail.text?.toString()
        }
        set(value) {
            if (!emailFlag) {
                emailFlag = true
                binding.textInputEmail.setText(value)
                emailFlag = false
            }
        }

    var error: String?
    set(value) {
        if (value == null) {
            binding.constraintLayoutInputEmail.setBackgroundResource(R.drawable.com_shift4_rounded_edge)
        } else {
            binding.constraintLayoutInputEmail.setBackgroundResource(R.drawable.com_shift4_rounded_edge_with_error)
        }
        binding.textViewEmailError.text = value
        if (value.isNullOrEmpty()) {
            binding.textViewEmailError.visibility = View.GONE
        } else {
            binding.textViewEmailError.visibility = View.VISIBLE
        }
    }
    get() {
        return null
    }

    override fun setEnabled(enabled: Boolean) {
        binding.textInputEmail.isEnabled = enabled
    }

    override fun isEnabled(): Boolean {
        return binding.textInputEmail.isEnabled
    }

    private val binding =
        ComShift4LayoutEmailBinding.inflate(LayoutInflater.from(context), this, true)

    init {

    }

    override fun clearFocus() {
        super.clearFocus()
        binding.textInputEmail.clearFocus()
    }

    fun initialize() {
        binding.textInputEmail.addTextChangedListener {
            if (!emailFlag) {
                emailFlag = true
                emailChangedListener(it?.toString())
                emailFlag = false
            }
        }
    }
}