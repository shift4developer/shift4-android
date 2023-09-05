package com.shift4.data.repository

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

internal class RefererInterceptor(private val baseURL: String, private val referer: Boolean) :
    Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val authenticatedRequest = request
            .newBuilder()
            .header("Referer", baseURL)
            .build()
        return if (referer) chain.proceed(authenticatedRequest) else chain.proceed(request)
    }
}