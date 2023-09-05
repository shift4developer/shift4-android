package com.shift4.data.api

import com.shift4.data.repository.BasicAuthInterceptor
import com.shift4.data.repository.RefererInterceptor
import com.shift4.data.repository.UserAgentInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

internal class RequestBuilder(
    publicKey: String,
    baseUrl: String,
    authorize: Boolean
) {
    private val client = OkHttpClient.Builder()
        .addInterceptor(BasicAuthInterceptor(publicKey, authorize))
        .addInterceptor(RefererInterceptor(baseUrl, true))
        .addInterceptor(UserAgentInterceptor())
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    fun <T> buildService(service: Class<T>): T {
        return retrofit.create(service)
    }
}