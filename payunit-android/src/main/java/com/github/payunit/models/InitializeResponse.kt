package com.github.payunit.models

data class InitializeResponse(
    val status: String,
    val statusCode: Int,
    val message: String,
    val data: TransactionData
)

data class TransactionData(
    val t_id: String,
    val t_sum: String,
    val t_url: String,
    val transaction_id: String,
    val transaction_url: String,
    val providers: List<Provider>,
    val currency: String,
)

data class Provider(
    val shortcode: String,
    val name: String,
    val logo: String,
    val provider_type: String,  // "MOBILE_AND_WALLET" or "WORLD"
    val status: String,
    val country: Country
)

data class Country(
    val country_name: String,
    val country_code: String
)