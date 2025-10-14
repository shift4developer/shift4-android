package com.shift4.data.api

import com.shift4.data.repository.AcceptHeaderInterceptor
import com.shift4.data.repository.AuthorizationInterceptor
import com.shift4.data.repository.RefererInterceptor
import com.shift4.data.repository.UserAgentInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

internal class RequestBuilder(
    publicKey: String,
    merchantId: String?,
    baseUrl: String,
    authorize: Boolean
) {
    private val client = OkHttpClient.Builder().apply {
        if (authorize) {
            addInterceptor(AuthorizationInterceptor(publicKey, merchantId))
        }
        addInterceptor(RefererInterceptor(baseUrl))
        addInterceptor(UserAgentInterceptor())
        addInterceptor(AcceptHeaderInterceptor("*/*"))
    }.build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    fun <T> buildService(service: Class<T>): T {
        return retrofit.create(service)
    }
}