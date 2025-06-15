package com.techzo.cambiazo.data.remote.paypal

import retrofit2.http.*

interface PaypalService {

    @Headers(
        "Accept: application/json",
        "Accept-Language: en_US"
    )
    @FormUrlEncoded
    @POST("v1/oauth2/token")
    suspend fun obtenerToken(
        @Header("Authorization") auth: String,
        @Field("grant_type") grantType: String = "client_credentials"
    ): AccessTokenResponse


    @Headers("Content-Type: application/json")
    @POST("v2/checkout/orders")
    suspend fun crearOrden(
        @Header("Authorization") auth: String,
        @Body body: OrderRequest
    ): OrderResponse
}
