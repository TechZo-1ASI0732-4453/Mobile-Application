package com.techzo.cambiazo.data.remote.donations

import retrofit2.http.GET

interface DonationsService {
    @GET("/api/v2/donations/ongs")
    suspend fun getAllOngs(): List<OngDto>
}
