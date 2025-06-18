package com.shift4.checkout.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputEditText
import com.shift4.R


internal class EmailComponent @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    private var textInputEmail: TextInputEditText
    private var textViewEmailError: TextView
    private var textInputName: TextInputEditText
    private var constraintLayoutInputEmail: ConstraintLayout

    init {
        LayoutInflater.from(context).inflate(R.layout.com_shift4_layout_email, this, true)

        textInputEmail =
            findViewById<TextInputEditText>(R.id.textInputEmail)
        textInputName = findViewById<TextInputEditText>(R.id.textInputName)
        textViewEmailError = findViewById<TextView>(R.id.textViewEmailError)
        constraintLayoutInputEmail = findViewById<ConstraintLayout>(R.id.constraintLayoutInputEmail)

        textInputEmail.setBackgroundColor(
            context.resources.getColor(
                android.R.color.transparent, null
            )
        )
    }

    var emailChangedListener: (String?) -> Unit = {}
    var nameChangedListener: (String?) -> Unit = {}
    var email: String?
        get() {
            return textInputEmail.text?.toString()
        }
        set(value) {
            textInputEmail.setText(value)
        }

    var name: String?
        get() {
            return textInputName.text?.toString()
        }
        set(value) {
            textInputName.setText(value)
        }

    var error: String?
        set(value) {
            if (value == null) {
                constraintLayoutInputEmail.setBackgroundResource(R.drawable.com_shift4_rounded_edge)
            } else {
                constraintLayoutInputEmail.setBackgroundResource(R.drawable.com_shift4_rounded_edge_with_error)
            }
            textViewEmailError.text = value
            if (value.isNullOrEmpty()) {
                textViewEmailError.visibility = View.GONE
            } else {
                textViewEmailError.visibility = View.VISIBLE
            }
        }
        get() {
            return null
        }

    override fun setEnabled(enabled: Boolean) {
        textInputEmail.isEnabled = enabled
        textInputName.isEnabled = enabled
    }

    override fun isEnabled(): Boolean {
        return textInputEmail.isEnabled && textInputName.isEnabled
    }

    override fun clearFocus() {
        super.clearFocus()
        textInputEmail.clearFocus()
        textInputName.clearFocus()
    }

    fun initialize() {
        textInputEmail.addTextChangedListener {
            emailChangedListener(it?.toString())
        }
        textInputName.addTextChangedListener {
            nameChangedListener(it?.toString())
        }
    }
}