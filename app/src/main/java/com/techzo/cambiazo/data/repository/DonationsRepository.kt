package com.techzo.cambiazo.data.repository

import com.techzo.cambiazo.data.remote.donations.OngDto
import com.techzo.cambiazo.data.remote.donations.DonationsService
import jakarta.inject.Inject

class DonationsRepository @Inject constructor(
    private val donationsService: DonationsService
) {
    suspend fun getAllOngs(): List<OngDto> {
        return donationsService.getAllOngs()
    }
}