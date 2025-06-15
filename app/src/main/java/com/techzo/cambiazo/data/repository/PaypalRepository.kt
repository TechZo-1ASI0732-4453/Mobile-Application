package com.techzo.cambiazo.data.repository

import android.util.Base64
import com.techzo.cambiazo.data.remote.paypal.Amount
import com.techzo.cambiazo.data.remote.paypal.OrderRequest
import com.techzo.cambiazo.data.remote.paypal.PaypalService
import com.techzo.cambiazo.data.remote.paypal.PurchaseUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object PaypalRepository {

    private const val CLIENT_ID =
        "AWoxCShd6JWv2hDNHIKP9x3DjypVC9f19TC3zZt8ou7H_KjMJQx2pOFUHgi9-bJOKG7fZmN5v1HJYfy-"
    private const val SECRET =
        "EBNZWHb4IzRmpMWGEEKdJb0tMQk4K7qSYTY-W0W75fSwWdaweVj3Gl_tdem2ZqqZ_oMub1VMCsROrfpN"

    private val service: PaypalService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api-m.sandbox.paypal.com/")
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PaypalService::class.java)
    }

    suspend fun createOrder(amount: String, currency: String = "USD"): String =
        withContext(Dispatchers.IO) {

            val creds = Base64.encodeToString(
                "$CLIENT_ID:$SECRET".toByteArray(),
                Base64.NO_WRAP
            )

            val token = service.obtenerToken("Basic $creds").access_token

            val body = OrderRequest(
                intent = "CAPTURE",
                purchase_units = listOf(
                    PurchaseUnit(Amount(currency, amount))
                )
            )

            service.crearOrden("Bearer $token", body).id
        }
}