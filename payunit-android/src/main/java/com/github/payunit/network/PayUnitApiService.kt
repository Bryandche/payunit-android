package com.github.payunit.network

import com.github.payunit.models.InitializeRequest
import com.github.payunit.models.InitializeResponse
import com.github.payunit.models.PaymentStatusResponse
import com.github.payunit.models.ProcessPaymentRequest
import com.github.payunit.models.ProcessPaymentResponse
import com.github.payunit.models.ProvidersResponse
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