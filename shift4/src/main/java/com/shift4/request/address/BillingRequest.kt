package com.shift4.request.address

class BillingRequest(
    val name: String, val address: AddressRequest, val vat: String?
)