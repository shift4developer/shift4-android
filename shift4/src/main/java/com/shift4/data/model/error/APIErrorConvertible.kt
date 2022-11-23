package com.shift4.data.model.error

internal interface APIErrorConvertible {
    fun toAPIError(): APIError
}