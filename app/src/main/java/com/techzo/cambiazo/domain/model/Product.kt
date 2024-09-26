package com.techzo.cambiazo.domain.model

data class Product(
    val id: Int,
    val name: String,
    val description: String,
    val desiredObject: String,
    val price: Int,
    val image: String,
    val boost: Boolean,
    val available: Boolean,
    val productCategoryId: Int,
    val userId: Int,
    val districtId: Int
)
