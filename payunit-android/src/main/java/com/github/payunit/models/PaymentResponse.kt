package com.github.payunit.models

data class ProcessPaymentResponse(
    val status: String,
    val statusCode: Int,
    val message: String,
    val data: PaymentResponseData
)

data class PaymentResponseData(
    val transaction_id: String,
    val payment_status: String,
    val amount: Int
)