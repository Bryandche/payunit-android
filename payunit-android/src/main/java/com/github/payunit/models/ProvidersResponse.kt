package com.github.payunit.models

data class ProvidersResponse(
    val status: String,
    val statusCode: Int,
    val message: String,
    val data: List<ProviderData>
)

data class ProviderData(
    val shortcode: String,
    val name: String,
    val logo: String,
    val provider_type: String,  // "MOBILE_AND_WALLET" or "WORLD"
    val country: CountryData
)

data class CountryData(
    val country_name: String,
    val country_code: String
)

