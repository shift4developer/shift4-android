package com.shift4.data.repository

import com.shift4.utils.UserAgentGenerator
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

internal class UserAgentInterceptor() : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val authenticatedRequest = request
            .newBuilder()
            .header("User-Agent", UserAgentGenerator().userAgent())
            .build()
        return chain.proceed(authenticatedRequest)
    }
}