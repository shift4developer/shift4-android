package com.shift4.checkout.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import com.shift4.R
import com.shift4.databinding.ComShift4LayoutButtonBinding

internal class ButtonComponent @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    enum class State {
        NORMAL,
        PROGRESS,
        SUCCESS
    }

    var onClickListener: () -> Unit = {}
    var state: State = State.NORMAL
        set(value) {
            field = value
            when (value) {
                State.NORMAL -> {
                    binding.progressBarButton.visibility = View.INVISIBLE
                    binding.imageViewCheck.visibility = View.INVISIBLE
                }
                State.PROGRESS -> {
                    binding.progressBarButton.visibility = View.VISIBLE
                    binding.progressBarButton.isEnabled = true
                    binding.buttonPayment.text = null
                }
                State.SUCCESS -> {
                    binding.progressBarButton.visibility = View.INVISIBLE
                    binding.imageViewCheck.visibility = View.VISIBLE
                    binding.buttonPayment.text = null
                    binding.buttonPayment.background = ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.com_shift4_button_success,
                        null
                    )
                }
            }
        }

    private val binding =
        ComShift4LayoutButtonBinding.inflate(
            LayoutInflater.from(context),
            this,
            true
        )

    init {
        state = State.NORMAL

        binding.buttonPayment.setOnClickListener {
            onClickListener()
        }
    }

    fun setText(@StringRes id: Int) {
        binding.buttonPayment.setText(id)
    }

    fun setText(text: String) {
        binding.buttonPayment.text = text
    }

    override fun setEnabled(enabled: Boolean) {
        binding.buttonPayment.isEnabled = enabled
    }
}