package com.shift4.data.repository

import com.google.gson.Gson
import com.shift4.data.model.error.APIError
import com.shift4.data.model.error.APIErrorGatewayResponse
import com.shift4.data.model.error.APIErrorResponse

internal class ErrorUtils {
    fun parseError(responseBody: String): APIError {
        val error: APIErrorResponse? = try {
            Gson().fromJson(responseBody, APIErrorResponse::class.java)
        } catch (e: Exception) {
            null
        }
        val gatewayError: APIErrorGatewayResponse? = try {
            Gson().fromJson(responseBody, APIErrorGatewayResponse::class.java)
        } catch (e: Exception) {
            null
        }

        if (error != null) {
            return error.toAPIError()
        }
        if (gatewayError != null) {
            return gatewayError.toAPIError()
        }

        return APIError.unknown
    }
}