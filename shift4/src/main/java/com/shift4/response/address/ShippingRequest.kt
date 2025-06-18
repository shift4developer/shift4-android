package com.shift4.response.address

import com.shift4.request.address.AddressRequest

data class ShippingRequest(
    val name: String? = null, val address: AddressRequest? = null
)