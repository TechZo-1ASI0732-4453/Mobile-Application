package com.techzo.cambiazo.data.repository

import com.techzo.cambiazo.common.Resource
import com.techzo.cambiazo.data.remote.invoice.CreateInvoicePayload
import com.techzo.cambiazo.data.remote.invoice.InvoiceResponse
import com.techzo.cambiazo.data.remote.invoice.InvoiceService
import jakarta.inject.Inject

class InvoiceRepository @Inject constructor(
    private val service: InvoiceService
) {
    suspend fun createInvoice(payload: CreateInvoicePayload): Resource<InvoiceResponse> {
        return try {
            val response = service.createInvoice(payload)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error("Error al crear la factura: ${response.code()}")
            }
        } catch (e: Exception) {
            Resource.Error("Excepción: ${e.localizedMessage}")
        }
    }
}
