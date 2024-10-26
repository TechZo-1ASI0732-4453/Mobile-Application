package com.techzo.cambiazo.domain

data class Exchange(
    val id: Int,
    val productOwn: ExchangeProduct,
    val productChange: ExchangeProduct,
    val userOwn: User,
    val userChange: User,
    val status: String,
    val exchangeDate: String?,
    val createdAt: String,
    val updatedAt: String
)
