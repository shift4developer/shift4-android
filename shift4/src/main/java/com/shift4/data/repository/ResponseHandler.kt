package com.shift4.data.repository

import com.shift4.data.api.Result
import com.shift4.data.model.error.APIError
import retrofit2.HttpException

internal class ResponseHandler {
    fun <T : Any> handleSuccess(data: T): Result<T> {
        return Result.success(data)
    }

    fun <T : Any> handleException(e: Exception): Result<T> {
        return when (e) {
            is HttpException -> {
                val body = e.response()?.errorBody()?.string()
                Result.error(ErrorUtils().parseError(body!!), null)
            }
            else -> Result.error(getErrorMessage(Int.MAX_VALUE), null)
        }
    }

    private fun getErrorMessage(code: Int): APIError {
        return when (code) {
            401 -> APIError.unknown
            else -> APIError.unknown
        }
    }
}