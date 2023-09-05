package com.shift4.checkout.component

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputEditText
import com.shift4.R
import com.shift4.data.model.CreditCard
import com.shift4.utils.*


internal class CardComponent @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    private var textInputCardNumber: TextInputEditText
    private var textInputExpiration: TextInputEditText
    private var textInputCVC: TextInputEditText

    private var constraintLayoutInputCardNumber: ConstraintLayout
    private var constraintLayoutInputCVC: ConstraintLayout
    private var constraintLayoutInputExpDate: ConstraintLayout
    private var textViewCardError: TextView
    private var imageViewCardBrand: ImageView

    private var creditCard: CreditCard = CreditCard.empty

    private val cvcInputFilter = CVCInputFilter()
    private var numberFlag = false
    private var expirationFlag = false
    private var cvcFlag = false

    internal var cardChangedListener: (String?) -> Unit = {}
    var expirationChangedListener: (String?) -> Unit = {}
    var cvcChangedListener: (String?) -> Unit = {}

   init {
        LayoutInflater.from(context).inflate(R.layout.com_shift4_layout_card, this, true)

        textInputCardNumber = findViewById<TextInputEditText>(R.id.textInputCardNumber)
        textInputExpiration = findViewById<TextInputEditText>(R.id.textInputExpiration)
        textInputCVC = findViewById<TextInputEditText>(R.id.textInputCVC)

        constraintLayoutInputCardNumber =
            findViewById<ConstraintLayout>(R.id.constraintLayoutInputCardNumber)
        constraintLayoutInputCVC = findViewById<ConstraintLayout>(R.id.constraintLayoutInputCVC)
        constraintLayoutInputExpDate =
            findViewById<ConstraintLayout>(R.id.constraintLayoutInputExpDate)
        textViewCardError = findViewById<TextView>(R.id.textViewCardError)
        imageViewCardBrand = findViewById<ImageView>(R.id.imageViewCardBrand)


        textInputCardNumber.setBackgroundColor(
            context.resources.getColor(
                android.R.color.transparent,
                null
            )
        )
        textInputExpiration.setBackgroundColor(
            context.resources.getColor(
                android.R.color.transparent,
                null
            )
        )
        textInputCVC.setBackgroundColor(
            context.resources.getColor(
                android.R.color.transparent,
                null
            )
        )
    }

    internal var card: String?
        get() {
            return textInputCardNumber.text?.toString()
        }
        set(value) {
            if (!numberFlag) {
                numberFlag = true
                textInputCardNumber.setText(value)
                numberFlag = false
            }
        }

    internal var expiration: String?
        get() {
            return textInputExpiration.text?.toString()
        }
        set(value) {

            if (!expirationFlag) {
                expirationFlag = true
                textInputExpiration.setText(value)
                expirationFlag = false
            }
        }

    internal var cvc: String?
        get() {
            return textInputCVC.text?.toString()
        }
        set(value) {
            if (!cvcFlag) {
                cvcFlag = true
                textInputCVC.setText(value)
                cvcFlag = false
            }
        }

    internal var error: String?
        set(value) {
            if (value == null) {
                constraintLayoutInputCardNumber.setBackgroundResource(R.drawable.com_shift4_rounded_edge)
                constraintLayoutInputCVC.setBackgroundResource(R.drawable.com_shift4_rounded_edge)
                constraintLayoutInputExpDate.setBackgroundResource(R.drawable.com_shift4_rounded_edge)
            } else {
                constraintLayoutInputCardNumber.setBackgroundResource(R.drawable.com_shift4_rounded_edge_with_error)
                constraintLayoutInputCVC.setBackgroundResource(R.drawable.com_shift4_rounded_edge_with_error)
                constraintLayoutInputExpDate.setBackgroundResource(R.drawable.com_shift4_rounded_edge_with_error)
            }
            textViewCardError.text = value
            if (value.isNullOrEmpty()) {
                textViewCardError.visibility = View.GONE
            } else {
                textViewCardError.visibility = View.VISIBLE
            }
        }
        get() {
            return null
        }

    internal fun setCreditCard(creditCard: CreditCard, cvc: Boolean) {
        this.creditCard = creditCard
        numberFlag = true
        expirationFlag = true
        cvcFlag = true
        textInputCardNumber.setText(creditCard.readable)
        textInputExpiration.setText(creditCard.expPlaceholder)
        if (cvc) {
            hideKeyboard()
            textInputCVC.setText("•••")
        } else {
            textInputCVC.text = null
            textInputCVC.requestFocus()
        }
        updateCardBrand()
        numberFlag = false
        expirationFlag = false
        cvcFlag = false
    }

    override fun setEnabled(enabled: Boolean) {
        textInputCardNumber.isEnabled = enabled
        textInputExpiration.isEnabled = enabled
        textInputCVC.isEnabled = enabled
    }

    override fun isEnabled(): Boolean {
        return textInputCardNumber.isEnabled && textInputExpiration.isEnabled && textInputCVC.isEnabled
    }

    fun initialize() {
        textInputCardNumber.filters = arrayOf(
            CreditCardInputFilter()
        )

        textInputExpiration.filters = arrayOf(
            ExpirationInputFilter()
        )

        textInputCVC.filters = arrayOf(
            cvcInputFilter
        )

        textInputCardNumber.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                return@setOnKeyListener false
            }
            return@setOnKeyListener false
        }

        textInputCardNumber.addTextChangedListener {
            if (!numberFlag) {
                numberFlag = true
                creditCard =
                    CreditCard(textInputCardNumber.text?.toString())
                updateCardBrand()
                if (creditCard.correct) {
                    textInputExpiration.requestFocus()
                }
                cvcInputFilter.card = creditCard
                cardChangedListener(it?.toString())
                numberFlag = false
            }
        }

        textInputExpiration.addTextChangedListener {
            if (!expirationFlag) {
                expirationFlag = true
                if (ExpirationDateFormatter().format(
                        textInputExpiration.text.toString(),
                        false
                    ).resignFocus
                ) {
                    textInputCVC.requestFocus()
                }
                expirationChangedListener(it?.toString())
                expirationFlag = false
            }
        }

        textInputCVC.addTextChangedListener {
            if (!cvcFlag) {
                cvcFlag = true
                if (it.toString().length == creditCard.cvcLength) {
                    hideKeyboard()
                }
                cvcChangedListener(it?.toString())
                cvcFlag = false
            }
        }

        textInputCVC.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard()
            }
            return@setOnEditorActionListener true
        }

        textInputExpiration.disableCopyPaste()
        textInputCVC.disableCopyPaste()
    }

    internal fun clean() {
        numberFlag = true
        expirationFlag = true
        cvcFlag = true
        textInputCardNumber.text = null
        textInputExpiration.text = null
        textInputCVC.text = null
        creditCard = CreditCard.empty
        updateCardBrand()
        numberFlag = false
        expirationFlag = false
        cvcFlag = false
    }

    override fun clearFocus() {
        super.clearFocus()
        textInputCardNumber.clearFocus()
        textInputExpiration.clearFocus()
        textInputCVC.clearFocus()
    }

    fun setFocus() {
        if (card.isNullOrEmpty()) {
            textInputCardNumber.requestFocus()
        } else {
            textInputCVC.requestFocus()
        }
    }

    private fun updateCardBrand() {
        imageViewCardBrand.setImageDrawable(creditCard.image(resources))
    }

    private fun hideKeyboard() {
        clearFocus()

        (context
            ?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(windowToken, 0)
    }
}