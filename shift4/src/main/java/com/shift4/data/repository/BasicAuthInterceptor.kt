package com.shift4.data.repository

import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

internal class BasicAuthInterceptor(private val publicKey: String, private val authorize: Boolean) :
    Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val authenticatedRequest = request
            .newBuilder()
            .header("Authorization", Credentials.basic(publicKey, ""))
            .header("User-Agent", "TEST1")
            .build()
        return if (authorize) chain.proceed(authenticatedRequest) else chain.proceed(request)
    }
}