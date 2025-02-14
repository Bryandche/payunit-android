package com.github.payunit.models

data class PaymentStatusResponse(
    val status: String,
    val statusCode: Int,
    val message: String,
    val data: PaymentData
)

data class PaymentData(
    val transaction_amount: Int,
    val transaction_status: String,
    val transaction_id: String,
    val purchaseRef: String,
    val notify_url: String,
    val callback_url: String,
    val transaction_currency: String,
    val transaction_gateway: String,
    val message: String
)
