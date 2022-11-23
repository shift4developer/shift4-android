package com.shift4.data.model.error

import com.google.gson.annotations.SerializedName
import com.shift4.utils.empty

internal data class APIErrorGatewayResponse(
    @SerializedName("error") private val error: String,
    @SerializedName("errorMessage") private val message: String?
): APIErrorConvertible {
    override fun toAPIError(): APIError {
        if ((message ?: String.empty).startsWith("email: ")) {
            return APIError(APIError.Type.Unknown, APIError.Code.InvalidEmail, message?.replace("email: ", ""))
        }
        return APIError(APIError.Type.Unknown, APIError.Code(error), message ?: String.empty)
    }
}