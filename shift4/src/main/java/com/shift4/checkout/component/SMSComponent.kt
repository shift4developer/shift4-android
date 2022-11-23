package com.shift4.checkout.component

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.res.ResourcesCompat
import com.shift4.R
import com.shift4.databinding.ComShift4LayoutSmsBinding

internal class SMSComponent @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    private val binding =
        ComShift4LayoutSmsBinding.inflate(
            LayoutInflater.from(context),
            this,
            true
        )

    var smsEnteredListener: (String) -> Unit = {}
    var sms: String
        get() {
            return binding.textInputSMSCode.text.toString()
        }
        set(value) {
            binding.textInputSMSCode.setText(value)
        }

    fun clean() {
        binding.textInputSMSCode.text = null
    }

    fun focus() {
        binding.textInputSMSCode.requestFocus()
    }

    override fun clearFocus() {
        super.clearFocus()
        binding.textInputSMSCode.clearFocus()
    }

    fun blinkError() {
        binding.textInputSMSCode.text = null
        binding.textInputSMSCode.setPinBackground(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.com_shift4_rounded_edge_with_error,
                null
            )
        )
        Handler(Looper.getMainLooper()).postDelayed(
            {
                binding.textInputSMSCode.setPinBackground(
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.com_shift4_pin_background,
                        null
                    )
                )
            },
            1000,
        )
    }

    init {
        binding.textInputSMSCode.setOnPinEnteredListener {
            if (it.toString().length == 6) {
                smsEnteredListener(it.toString())
            }
        }
    }
}