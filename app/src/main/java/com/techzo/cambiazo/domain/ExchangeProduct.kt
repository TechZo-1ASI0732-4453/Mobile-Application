package com.techzo.cambiazo.domain

data class ExchangeProduct(
    val id: Int,
    val createdAt: String,
    val updatedAt: String,
    val name: String,
    val description: String,
    val desiredObject: String,
    val price: Int,
    val image: String,
    val boost: Boolean,
    val available: Boolean,
    val productCategoryId: Int,
    val userId: Int,
    val districtId: Int,
)
