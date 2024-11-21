package com.techzo.cambiazo.data.remote.products

import com.techzo.cambiazo.domain.FavoriteProduct
import com.techzo.cambiazo.domain.FavoriteProductRequest
import com.techzo.cambiazo.domain.Product

data class FavoriteProductDto(
    val id: Int,
    val product: Product,
    val userId: Int
)

data class FavoriteProductRequestDto(
    val id: Int,
    val productId: Int,
    val userId: Int
)

fun FavoriteProductRequestDto.toFavoriteProduct(): FavoriteProductRequest {
    return FavoriteProductRequest(id = this.id, productId = productId, userId = this.userId)
}

fun FavoriteProductDto.toFavoriteProduct(): FavoriteProduct {
    return FavoriteProduct(id = this.id, product = this.product, userId = this.userId)
}

fun FavoriteProduct.toFavoriteProductDto(): FavoriteProductDto {
    return FavoriteProductDto(id = this.id, product = this.product, userId = this.userId)
}
