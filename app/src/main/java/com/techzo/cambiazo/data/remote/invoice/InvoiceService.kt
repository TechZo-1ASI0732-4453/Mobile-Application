package com.techzo.cambiazo.data.remote.invoice

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface InvoiceService {

    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("invoices")
    suspend fun createInvoice(@Body payload: CreateInvoicePayload): Response<InvoiceResponse>
}