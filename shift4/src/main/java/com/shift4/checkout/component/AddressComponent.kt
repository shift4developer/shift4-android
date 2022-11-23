package com.shift4.checkout.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.core.widget.addTextChangedListener
import com.shift4.data.model.address.Address
import com.shift4.data.model.address.Billing
import com.shift4.data.model.address.Shipping
import com.shift4.databinding.ComShift4LayoutAddressBinding


internal class AddressComponent @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    private val binding =
        ComShift4LayoutAddressBinding.inflate(
            LayoutInflater.from(context),
            this,
            true
        )

    var onAddressUpdated: (shipping: Shipping?, billing: Billing?) -> Unit = { _, _ -> }

    private var shouldCollectShipping = false
    private var shouldCollectBilling = false

    val billing: Billing?
        get() {
            val name = binding.textInputName.text.toString()
            val street = binding.textInputStreet.text.toString()
            val zip = binding.textInputZip.text.toString()
            val city = binding.textInputCity.text.toString()
            val country = binding.textInputCountry.cpViewHelper.selectedCountry.value?.alpha2
            val vat = binding.textInputVat.text.toString()

            return if (name.isNotEmpty() && street.isNotEmpty() && zip.isNotEmpty() && city.isNotEmpty() && country != null) {
                Billing(name, Address(street, zip, city, country), vat)
            } else {
                null
            }
        }
    val shipping: Shipping?
        get() {
            val name = binding.textInputShippingName.text.toString()
            val street = binding.textInputShippingStreet.text.toString()
            val zip = binding.textInputShippingZip.text.toString()
            val city = binding.textInputShippingCity.text.toString()
            val country =
                binding.textInputShippingCountry.cpViewHelper.selectedCountry.value?.alpha2

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
    private var sameAddress: Boolean = false

    init {
        binding.sameShippingSwitchComponent.onCheckedListener = {
            sameAddress = it
            if (it) {
                binding.textViewShippingAddress.visibility = View.GONE
                binding.textViewBillingAddress.visibility = View.GONE
                binding.linearLayoutShippingSection.visibility = View.GONE
            } else {
                binding.textViewShippingAddress.visibility = View.VISIBLE
                binding.textViewBillingAddress.visibility = View.VISIBLE
                binding.linearLayoutShippingSection.visibility = View.VISIBLE
            }
            onAddressUpdated(shipping, billing)
        }

        listOf(
            binding.textInputName,
            binding.textInputStreet,
            binding.textInputZip,
            binding.textInputCity,
            binding.textInputVat,
            binding.textInputShippingName,
            binding.textInputShippingStreet,
            binding.textInputShippingZip,
            binding.textInputShippingCity,
        ).forEach {
            it.addTextChangedListener {
                onAddressUpdated(shipping, billing)
            }
        }
        binding.textInputCountry.cpViewHelper.onCountryChangedListener =
            { onAddressUpdated(shipping, billing) }
        binding.textInputShippingCountry.cpViewHelper.onCountryChangedListener =
            { onAddressUpdated(shipping, billing) }
    }

    fun setup(shipping: Boolean, billing: Boolean) {
        this.shouldCollectShipping = shipping
        this.shouldCollectBilling = billing
        if (shipping) {
            binding.sameShippingSwitchComponent.visibility = View.VISIBLE
            binding.textViewShippingAddress.visibility = View.VISIBLE
            binding.textViewBillingAddress.visibility = View.VISIBLE
            binding.linearLayoutShippingSection.visibility = View.VISIBLE
            binding.sameShippingSwitchComponent.checked = true
        } else if (billing) {
            binding.sameShippingSwitchComponent.visibility = View.GONE
            binding.textViewShippingAddress.visibility = View.GONE
            binding.textViewBillingAddress.visibility = View.GONE
            binding.linearLayoutShippingSection.visibility = View.GONE
        } else {
            binding.linearLayoutAddress.visibility = View.GONE
        }
    }

    override fun setEnabled(enabled: Boolean) {
        binding.textInputName.isEnabled = enabled
        binding.textInputStreet.isEnabled = enabled
        binding.textInputZip.isEnabled = enabled
        binding.textInputCity.isEnabled = enabled
        binding.textInputVat.isEnabled = enabled
        binding.textInputShippingName.isEnabled = enabled
        binding.textInputShippingStreet.isEnabled = enabled
        binding.textInputShippingZip.isEnabled = enabled
        binding.textInputShippingCity.isEnabled = enabled
        binding.textInputCountry.isEnabled = enabled
        binding.textInputShippingCountry.isEnabled = enabled
        binding.sameShippingSwitchComponent.isEnabled = enabled
    }

    override fun isEnabled(): Boolean {
        return false
    }
}