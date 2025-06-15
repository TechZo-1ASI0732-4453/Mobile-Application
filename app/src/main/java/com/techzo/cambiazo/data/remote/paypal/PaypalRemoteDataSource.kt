package com.techzo.cambiazo.data.remote.paypal

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PaypalRemoteDataSource(
    private val service: PaypalService,
    private val clientId: String,
    private val secret: String
) {
    suspend fun createPayPalOrder(
        amount: String,
        currency: String = "USD"
    ): String = withContext(Dispatchers.IO) {

        val creds = android.util.Base64.encodeToString(
            "$clientId:$secret".toByteArray(),
            android.util.Base64.NO_WRAP
        )

        val token = service
            .obtenerToken("Basic $creds")
            .access_token

        val body = OrderRequest(
            intent = "CAPTURE",
            purchase_units = listOf(
                PurchaseUnit(
                    Amount(currency_code = currency, value = amount)
                )
            )
        )

        service.crearOrden("Bearer $token", body).id
    }
}