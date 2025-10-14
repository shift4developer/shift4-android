package com.shift4.data.repository

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

internal class AcceptHeaderInterceptor(val acceptValue: String) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .addHeader("Accept", acceptValue)
            .build()
        return chain.proceed(request)
    }
}