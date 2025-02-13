package com.example.payunit_android.network

import ProcessPaymentRequest
import ProcessPaymentResponse
import com.example.payunit_android.models.InitializeRequest
import com.example.payunit_android.models.InitializeResponse
import com.example.payunit_android.models.PaymentStatusResponse
import com.example.payunit_android.models.ProvidersResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query


interface PayUnitApiService {

    @POST("api/gateway/initialize")
    suspend fun initializePayment(
        @Body request: InitializeRequest
    ): Response<InitializeResponse>

    @POST("/api/gateway/makepayment")
    suspend fun processMobilePayment(
        @Body request: ProcessPaymentRequest
    ): Response<ProcessPaymentResponse>

    @GET("api/gateway/paymentstatus/{transactionId}")
    fun getPaymentStatus(
        @Path("transactionId") transactionId: String,
    ): Call<PaymentStatusResponse>

    @GET("api/gateway/gateways")
    fun getProviders(
        @Query("t_url") tUrl: String,
        @Query("t_id") tId: String,
        @Query("t_sum") tSum: String
    ): Response<ProvidersResponse>
}