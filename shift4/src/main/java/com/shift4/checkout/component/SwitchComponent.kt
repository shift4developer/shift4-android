package com.shift4.checkout.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.shift4.R
import com.shift4.databinding.ComShift4LayoutSwitchBinding

internal class SwitchComponent @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    var onCheckedListener: (Boolean) -> Unit = {}

    private val binding =
        ComShift4LayoutSwitchBinding.inflate(
            LayoutInflater.from(context),
            this,
            true
        )

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.Shift4SwitchComponent,
            0, 0).apply {

            try {
                binding.textViewSwitchTitle.text = getString(R.styleable.Shift4SwitchComponent_title)
                val subtitle = getString(R.styleable.Shift4SwitchComponent_subtitle) ?: ""
                binding.textViewSwitchSubtitle.text = subtitle
                binding.textViewSwitchSubtitle.visibility = if (subtitle.isEmpty()) View.GONE else View.VISIBLE
            } finally {
                recycle()
            }
        }
        binding.switchRememberCard.setOnCheckedChangeListener { _, value ->
            onCheckedListener(value)
        }
    }

    override fun setEnabled(enabled: Boolean) {
        binding.switchRememberCard.isEnabled = enabled
    }

    override fun isEnabled(): Boolean {
        return binding.switchRememberCard.isEnabled
    }

    var checked: Boolean
        get() {
            return binding.switchRememberCard.isChecked
        }
        set(value) {
            binding.switchRememberCard.isChecked = value
        }
}