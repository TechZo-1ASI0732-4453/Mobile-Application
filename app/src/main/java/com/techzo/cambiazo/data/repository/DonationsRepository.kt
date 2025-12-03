package com.techzo.cambiazo.data.repository

import com.techzo.cambiazo.common.Resource
import com.techzo.cambiazo.data.remote.donations.OngDto
import com.techzo.cambiazo.data.remote.donations.DonationsService
import com.techzo.cambiazo.domain.OngDetail
import com.techzo.cambiazo.data.remote.donations.toOng
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DonationsRepository @Inject constructor(
    private val donationsService: DonationsService
) {
    suspend fun getAllOngs(): List<OngDto> {
        return donationsService.getAllOngs()
    }

    suspend fun getOngById(id: Int): Resource<OngDetail> = withContext(Dispatchers.IO) {
        try {
            val response = donationsService.getOngById(id)
            if (response.isSuccessful) {
                response.body()?.let { ong->
                    return@withContext Resource.Success(data = ong.toOng())
                }
                return@withContext Resource.Error("No se encontró la ONG")
            }
            return@withContext Resource.Error(response.message())
        } catch (e: Exception) {
            return@withContext Resource.Error(e.message ?: "Ocurrió un error")
        }
    }
}