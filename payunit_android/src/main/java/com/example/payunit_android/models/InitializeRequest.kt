package com.example.payunit_android.models

data class InitializeRequest(
    val total_amount: Int,
    val currency: String,
    val transaction_id: String,
    val return_url: String,
    val notify_url: String,
    val payment_country: String
)
