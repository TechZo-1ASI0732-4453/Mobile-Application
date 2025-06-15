package com.techzo.cambiazo.data.remote.paypal

data class AccessTokenResponse(
    val access_token: String,
    val token_type: String,
    val expires_in: Long
)

data class OrderRequest(
    val intent: String,
    val purchase_units: List<PurchaseUnit>
)

data class PurchaseUnit(
    val amount: Amount
)

data class Amount(
    val currency_code: String,
    val value: String
)

data class OrderResponse(
    val id: String
)