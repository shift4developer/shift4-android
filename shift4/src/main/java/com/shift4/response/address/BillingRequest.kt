package com.shift4.response.address

import com.shift4.request.address.AddressRequest

data class BillingRequest(
    val name: String? = null, val address: AddressRequest? = null, val vat: String? = null
)