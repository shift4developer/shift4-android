package com.shift4.checkout.component

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import androidx.core.widget.addTextChangedListener
import com.shift4.R
import com.shift4.data.model.CreditCard
import com.shift4.utils.*


internal class CardComponent @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    private var creditCard: CreditCard = CreditCard.empty

    private val cvcInputFilter = CVCInputFilter()
    private var numberFlag = false
    private var expirationFlag = false
    private var cvcFlag = false

    private val binding =
        com.shift4.databinding.ComShift4LayoutCardBinding.inflate(
            LayoutInflater.from(context),
            this,
            true
        )

    internal var cardChangedListener: (String?) -> Unit = {}
    var expirationChangedListener: (String?) -> Unit = {}
    var cvcChangedListener: (String?) -> Unit = {}

    internal var card: String?
        get() {
            return binding.textInputCardNumber.text?.toString()
        }
        set(value) {
            binding.textInputCardNumber.setText(value)
        }

    internal var expiration: String?
        get() {
            return binding.textInputExpiration.text?.toString()
        }
        set(value) {
            binding.textInputExpiration.setText(value)
        }

    internal var cvc: String?
        get() {
            return binding.textInputCVC.text?.toString()
        }
        set(value) {
            binding.textInputCVC.setText(value)
        }

    internal var error: String?
        set(value) {
            if (value == null) {
                binding.constraintLayoutInputCardNumber.setBackgroundResource(R.drawable.com_shift4_rounded_edge)
                binding.constraintLayoutInputCVC.setBackgroundResource(R.drawable.com_shift4_rounded_edge)
                binding.constraintLayoutInputExpDate.setBackgroundResource(R.drawable.com_shift4_rounded_edge)
            } else {
                binding.constraintLayoutInputCardNumber.setBackgroundResource(R.drawable.com_shift4_rounded_edge_with_error)
                binding.constraintLayoutInputCVC.setBackgroundResource(R.drawable.com_shift4_rounded_edge_with_error)
                binding.constraintLayoutInputExpDate.setBackgroundResource(R.drawable.com_shift4_rounded_edge_with_error)
            }
            binding.textViewCardError.text = value
            if (value.isNullOrEmpty()) {
                binding.textViewCardError.visibility = View.GONE
            } else {
                binding.textViewCardError.visibility = View.VISIBLE
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
        binding.textInputCardNumber.setText(creditCard.readable)
        binding.textInputExpiration.setText("••/••")
        if (cvc) {
            hideKeyboard()
            binding.textInputCVC.setText("•••")
        } else {
            binding.textInputCVC.text = null
            binding.textInputCVC.requestFocus()
        }
        updateCardBrand()
    }

    override fun setEnabled(enabled: Boolean) {
        binding.textInputCardNumber.isEnabled = enabled
        binding.textInputExpiration.isEnabled = enabled
        binding.textInputCVC.isEnabled = enabled
    }

    override fun isEnabled(): Boolean {
        return binding.textInputCardNumber.isEnabled && binding.textInputExpiration.isEnabled && binding.textInputCVC.isEnabled
    }

    init {
        binding.textInputCardNumber.filters = arrayOf(
            CreditCardInputFilter()
        )

        binding.textInputExpiration.filters = arrayOf(
            ExpirationInputFilter()
        )

        binding.textInputCVC.filters = arrayOf(
            cvcInputFilter
        )

        binding.textInputCardNumber.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
//                clearTextIfSavedEmail()
                return@setOnKeyListener false
            }
            return@setOnKeyListener false
        }

        binding.textInputCardNumber.addTextChangedListener {
            if (numberFlag) {
                numberFlag = false
            } else {
                creditCard =
                    CreditCard(binding.textInputCardNumber.text?.toString())
                updateCardBrand()
                if (creditCard.correct) {
                    binding.textInputExpiration.requestFocus()
                }
                cvcInputFilter.card = creditCard
                cardChangedListener(it?.toString())
            }
        }

        binding.textInputExpiration.addTextChangedListener {
            if (expirationFlag) {
                expirationFlag = false
            } else {
                if (ExpirationDateFormatter().format(
                        binding.textInputExpiration.text.toString(),
                        false
                    ).resignFocus
                ) {
                    binding.textInputCVC.requestFocus()
                }
                expirationChangedListener(it?.toString())
            }
        }

        binding.textInputCVC.addTextChangedListener {
            if (cvcFlag) {
                cvcFlag = false
            } else {
//                if (verifiedCard) {
//                    clearTextIfSavedEmail()
//                }
                if (it.toString().length == creditCard.cvcLength) {
                    hideKeyboard()
                }
                cvcChangedListener(it?.toString())
            }
        }

        binding.textInputCVC.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard()
            }
            return@setOnEditorActionListener true
        }

        binding.textInputExpiration.disableCopyPaste()
        binding.textInputCVC.disableCopyPaste()
    }

    internal fun clean() {
        numberFlag = true
        expirationFlag = true
        cvcFlag = true
        binding.textInputCardNumber.text = null
        binding.textInputExpiration.text = null
        binding.textInputCVC.text = null
        creditCard = CreditCard.empty
        updateCardBrand()
    }

    override fun clearFocus() {
        super.clearFocus()
        binding.textInputCardNumber.clearFocus()
        binding.textInputExpiration.clearFocus()
        binding.textInputCVC.clearFocus()
    }

    fun setFocus() {
        binding.textInputCardNumber.requestFocus()
    }

    private fun updateCardBrand() {
        binding.imageViewCardBrand.setImageDrawable(creditCard.image(resources))
    }

    private fun hideKeyboard() {
        clearFocus()

        (context
            ?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(windowToken, 0)
    }
}