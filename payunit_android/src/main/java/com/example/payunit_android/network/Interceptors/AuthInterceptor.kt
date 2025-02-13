package com.example.payunit_android.network.Interceptors
import okhttp3.Interceptor
import okhttp3.Response
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class AuthInterceptor(
    private val apiUsername: String,
    private val apiPassword: String,
    private val token: String,
    private val mode: String
) : Interceptor {
    @OptIn(ExperimentalEncodingApi::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val credentials = "$apiUsername:$apiPassword"
        val encodedAuth = Base64.Default.encode(credentials.encodeToByteArray())

        val request = chain.request().newBuilder()
            .addHeader("x-api-key", token)
            .addHeader("mode", mode)
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Basic $encodedAuth")
            .build()

        return chain.proceed(request)
    }
}
