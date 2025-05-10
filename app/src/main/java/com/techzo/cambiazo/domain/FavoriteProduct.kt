package com.techzo.cambiazo.domain

data class FavoriteProduct(
    val id: Int,
    val product: Product,
    val userId: Int
)


data class FavoriteProductRequest(
    val id: Int,
    val productId: Int,
    val userId: Int
)