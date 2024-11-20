package com.techzo.cambiazo.data.remote.products

data class CreateProductDto(
    val available: Boolean,
    val boost: Boolean,
    val description: String,
    val desiredObject: String,
    val districtId: Int,
    val image: String,
    val name: String,
    val price: Int,
    val productCategoryId: Int,
    val userId: Int
)