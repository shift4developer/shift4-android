package com.shift4.checkout.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import com.shift4.R

internal class ButtonComponent @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    private lateinit var progressBarButton: ProgressBar
    private lateinit var imageViewCheck: ImageView
    private lateinit var buttonPayment: Button

    enum class State {
        NORMAL, PROGRESS, SUCCESS
    }

    var onClickListener: () -> Unit = {}
    var state: State = State.NORMAL
        set(value) {
            field = value
            when (value) {
                State.NORMAL -> {
                    progressBarButton.visibility = View.INVISIBLE
                    imageViewCheck.visibility = View.INVISIBLE
                }
                State.PROGRESS -> {
                    progressBarButton.visibility = View.VISIBLE
                    progressBarButton.isEnabled = true
                    buttonPayment.text = null
                }
                State.SUCCESS -> {
                    progressBarButton.visibility = View.INVISIBLE
                    imageViewCheck.visibility = View.VISIBLE
                    buttonPayment.text = null
                    buttonPayment.background = ResourcesCompat.getDrawable(
                        resources, R.drawable.com_shift4_button_success, null
                    )
                }
            }
        }

    init {
        LayoutInflater.from(context).inflate(R.layout.com_shift4_layout_button, this, true)

        progressBarButton = findViewById<ProgressBar>(R.id.progressBarButton)
        imageViewCheck = findViewById<ImageView>(R.id.imageViewCheck)
        buttonPayment = findViewById<Button>(R.id.buttonPayment)

        state = State.NORMAL

        buttonPayment.setOnClickListener {
            onClickListener()
        }
    }

    fun setText(@StringRes id: Int) {
        buttonPayment.setText(id)
    }

    fun setText(text: String) {
        buttonPayment.text = text
    }

    override fun setEnabled(enabled: Boolean) {
        buttonPayment.isEnabled = enabled
    }
}