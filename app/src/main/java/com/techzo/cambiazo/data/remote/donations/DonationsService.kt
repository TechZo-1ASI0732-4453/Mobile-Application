package com.techzo.cambiazo.data.remote.donations

import com.techzo.cambiazo.data.remote.products.ProductDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface DonationsService {
    @GET("donations/ongs")
    suspend fun getAllOngs(): List<OngDto>

    @Headers("Content-Type: application/json","Accept: application/json")
    @GET("donations/ongs/{id}/details")
    suspend fun getOngById(@Path("id") id: Int): Response<OngDetailDto>
}
