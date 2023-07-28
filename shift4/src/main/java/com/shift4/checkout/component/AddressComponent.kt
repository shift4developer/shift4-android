package com.shift4.checkout.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputEditText
import com.shift4.R
import com.shift4.data.model.address.Address
import com.shift4.data.model.address.Billing
import com.shift4.data.model.address.Shipping


internal class AddressComponent @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    private lateinit var textInputName:TextInputEditText
    private lateinit var textInputStreet:TextInputEditText
    private lateinit var textInputZip:TextInputEditText
    private lateinit var textInputCity:TextInputEditText
    private lateinit var textInputCountry :com.hbb20.CountryCodePicker
    private lateinit var textInputVat:TextInputEditText

    private lateinit var textInputShippingName :TextInputEditText
    private lateinit var textInputShippingStreet:TextInputEditText
    private lateinit var textInputShippingZip :TextInputEditText
    private lateinit var textInputShippingCity :TextInputEditText
    private lateinit var textInputShippingCountry :com.hbb20.CountryCodePicker

    private lateinit var sameShippingSwitchComponent : SwitchComponent
    private lateinit var textViewShippingAddress : TextView
    private lateinit var textViewBillingAddress :TextView
    private lateinit var linearLayoutShippingSection: LinearLayout
    private lateinit var linearLayoutAddress: LinearLayout

    var onAddressUpdated: (shipping: Shipping?, billing: Billing?) -> Unit = { _, _ -> }

    private var shouldCollectShipping = false
    private var shouldCollectBilling = false

  init {
        LayoutInflater.from(context).inflate(R.layout.com_shift4_layout_address, this, true)

        textInputName = findViewById<TextInputEditText>(R.id.textInputName)
        textInputStreet = findViewById<TextInputEditText>(R.id.textInputStreet)
        textInputZip = findViewById<TextInputEditText>(R.id.textInputZip)
        textInputCity = findViewById<TextInputEditText>(R.id.textInputCity)
        textInputCountry = findViewById<com.hbb20.CountryCodePicker>(R.id.textInputCountry)
        textInputVat = findViewById<TextInputEditText>(R.id.textInputVat)

        textInputShippingName = findViewById<TextInputEditText>(R.id.textInputShippingName)
        textInputShippingStreet =
            findViewById<TextInputEditText>(R.id.textInputShippingStreet)
        textInputShippingZip = findViewById<TextInputEditText>(R.id.textInputShippingZip)
        textInputShippingCity = findViewById<TextInputEditText>(R.id.textInputShippingCity)
        textInputShippingCountry =
            findViewById<com.hbb20.CountryCodePicker>(R.id.textInputShippingCountry)

        sameShippingSwitchComponent =
            findViewById<SwitchComponent>(R.id.sameShippingSwitchComponent)
        textViewShippingAddress = findViewById<TextView>(R.id.textViewShippingAddress)
        textViewBillingAddress = findViewById<TextView>(R.id.textViewBillingAddress)
        linearLayoutShippingSection =
            findViewById<LinearLayout>(R.id.linearLayoutShippingSection)
        linearLayoutAddress = findViewById<LinearLayout>(R.id.linearLayoutAddress)
    }

    var billing: Billing?
        get() {
            val name = textInputName.text.toString()
            val street = textInputStreet.text.toString()
            val zip = textInputZip.text.toString()
            val city = textInputCity.text.toString()
            val country = textInputCountry.selectedCountryNameCode
            val vat = textInputVat.text.toString()

            return if (name.isNotEmpty() && street.isNotEmpty() && zip.isNotEmpty() && city.isNotEmpty() && country != null) {
                Billing(name, Address(street, zip, city, country), vat)
            } else {
                null
            }
        }
        set(value) {
            if (value != null) {
                textInputName.setText(value.name)
                textInputStreet.setText(value.address.line1)
                textInputZip.setText(value.address.zip)
                textInputCity.setText(value.address.city)
//            textInputCountry.cpViewHelper.selectedCountry = value.address.country
                textInputVat.setText(value.vat)
            }
        }
    var shipping: Shipping?
        get() {
            val name = textInputShippingName.text.toString()
            val street = textInputShippingStreet.text.toString()
            val zip = textInputShippingZip.text.toString()
            val city = textInputShippingCity.text.toString()
            val country = textInputShippingCountry.selectedCountryNameCode

            return if (sameAddress) {
                billing?.let {
                    Shipping(it.name, it.address)
                }
            } else if (name.isNotEmpty() && street.isNotEmpty() && zip.isNotEmpty() && city.isNotEmpty() && country != null) {
                Shipping(name, Address(street, zip, city, country))
            } else {
                null
            }
        }
        set(value) {
            if (value != null) {
                textInputShippingName.setText(value.name)
                textInputShippingStreet.setText(value.address.line1)
                textInputShippingZip.setText(value.address.zip)
                textInputShippingCity.setText(value.address.city)
//            textInputCountry.cpViewHelper.selectedCountry = value.address.country
            }
        }
    private var sameAddress: Boolean = false

    init {
        sameShippingSwitchComponent.onCheckedListener = {
            sameAddress = it
            if (it) {
                textViewShippingAddress.visibility = View.GONE
                textViewBillingAddress.visibility = View.GONE
                linearLayoutShippingSection.visibility = View.GONE
            } else {
                textViewShippingAddress.visibility = View.VISIBLE
                textViewBillingAddress.visibility = View.VISIBLE
                linearLayoutShippingSection.visibility = View.VISIBLE
            }
            onAddressUpdated(shipping, billing)
        }

        listOf(
            textInputName,
            textInputStreet,
            textInputZip,
            textInputCity,
            textInputVat,
            textInputShippingName,
            textInputShippingStreet,
            textInputShippingZip,
            textInputShippingCity,
        ).forEach {
            it.setBackgroundColor(context.resources.getColor(android.R.color.transparent, null))
            it.addTextChangedListener {
                onAddressUpdated(shipping, billing)
            }
        }
        textInputCountry.setCountryForNameCode("US")
        textInputShippingCountry.setCountryForNameCode("US")
        textInputCountry.setOnCountryChangeListener { onAddressUpdated(shipping, billing) }
        textInputShippingCountry.setOnCountryChangeListener { onAddressUpdated(shipping, billing) }
    }

    fun setup(shipping: Boolean, billing: Boolean) {
        this.shouldCollectShipping = shipping
        this.shouldCollectBilling = billing
        if (shipping) {
            sameShippingSwitchComponent.visibility = View.VISIBLE
            textViewShippingAddress.visibility = View.VISIBLE
            textViewBillingAddress.visibility = View.VISIBLE
            linearLayoutShippingSection.visibility = View.VISIBLE
            sameShippingSwitchComponent.checked = true
        } else if (billing) {
            sameShippingSwitchComponent.visibility = View.GONE
            textViewShippingAddress.visibility = View.GONE
            textViewBillingAddress.visibility = View.GONE
            linearLayoutShippingSection.visibility = View.GONE
        } else {
            linearLayoutAddress.visibility = View.GONE
        }
    }

    override fun setEnabled(enabled: Boolean) {
        textInputName.isEnabled = enabled
        textInputStreet.isEnabled = enabled
        textInputZip.isEnabled = enabled
        textInputCity.isEnabled = enabled
        textInputVat.isEnabled = enabled
        textInputShippingName.isEnabled = enabled
        textInputShippingStreet.isEnabled = enabled
        textInputShippingZip.isEnabled = enabled
        textInputShippingCity.isEnabled = enabled
        textInputCountry.isEnabled = enabled
        textInputShippingCountry.isEnabled = enabled
        sameShippingSwitchComponent.isEnabled = enabled
    }

    override fun isEnabled(): Boolean {
        return false
    }
}