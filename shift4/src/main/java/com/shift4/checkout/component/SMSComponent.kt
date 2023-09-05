package com.shift4.checkout.component

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.res.ResourcesCompat
import com.shift4.R

internal class SMSComponent @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    private var textInputSMSCode: com.alimuzaffar.lib.pin.PinEntryEditText

    init {
        LayoutInflater.from(context).inflate(R.layout.com_shift4_layout_sms, this, true)

        textInputSMSCode = findViewById(R.id.textInputSMSCode)

        textInputSMSCode.setOnPinEnteredListener {
            if (it.toString().length == 6) {
                smsEnteredListener(it.toString())
            }
        }
    }

    var smsEnteredListener: (String) -> Unit = {}
    var sms: String
        get() {
            return textInputSMSCode.text.toString()
        }
        set(value) {
            textInputSMSCode.setText(value)
        }

    fun clean() {
        textInputSMSCode.text = null
    }

    fun focus() {
        textInputSMSCode.requestFocus()
    }

    override fun clearFocus() {
        super.clearFocus()
        textInputSMSCode.clearFocus()
    }

    fun blinkError() {
        textInputSMSCode.text = null
        textInputSMSCode.setPinBackground(
            ResourcesCompat.getDrawable(
                resources, R.drawable.com_shift4_rounded_edge_with_error, null
            )
        )
        Handler(Looper.getMainLooper()).postDelayed(
            {
                textInputSMSCode.setPinBackground(
                    ResourcesCompat.getDrawable(
                        resources, R.drawable.com_shift4_pin_background, null
                    )
                )
            },
            1000,
        )
    }
}