package com.techzo.cambiazo.domain

data class ExchangeResponse(
    val id: Int,
    val productOwnId: Int,
    val productChangeId: Int,
    val status: String,
    val exchangeDate: String,
    val createdAt: String,
    val updatedAt: String
)
