package com.techzo.cambiazo.data.remote.invoice

data class CreateInvoicePayload(
    val totalAmount: Double,
    val concept: String,
    val userId: Int
)

data class InvoiceResponse(
    val id: Int,
    val totalAmount: Double,
    val concept: String,
    val createdAt: String
)
