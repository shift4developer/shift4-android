package com.shift4.data.repository

import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

internal class AuthorizationInterceptor(
    private val publicKey: String,
    private val merchantId: String? = null
) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .header("Authorization", Credentials.basic(publicKey, ""))
        if (!merchantId.isNullOrBlank()) {
            request.header("Shift4-Merchant", merchantId)
        }
        return chain.proceed(request.build())
    }
}