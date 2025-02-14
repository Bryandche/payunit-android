package com.github.payunit.network

import com.github.payunit.utils.Constants
import com.github.payunit.network.interceptors.AuthInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class PayUnitApiClient(
    private val apiUsername: String,
    private val apiPassword: String,
    private val token: String,
    private val mode: String
    ) {
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor(apiUsername,apiPassword,token,mode))
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        //  TODO: Set baseUrl to LIVE_URL for live and test mode for production
//        .baseUrl(if(mode == "live") Constants.LIVE_URL else Constants.SANDBOX_URL)
        .baseUrl(Constants.LIVE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: PayUnitApiService = retrofit.create(PayUnitApiService::class.java)
}