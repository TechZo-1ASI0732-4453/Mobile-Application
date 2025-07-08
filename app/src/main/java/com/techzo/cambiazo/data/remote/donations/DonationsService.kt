package com.techzo.cambiazo.data.remote.donations

import retrofit2.http.GET

interface DonationsService {
    @GET("donations/ongs")
    suspend fun getAllOngs(): List<OngDto>
}
