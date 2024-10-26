package com.techzo.cambiazo.data.remote.products

import com.techzo.cambiazo.domain.FavoriteProduct

data class FavoriteProductDto(
    val id: Int,
    val productId: Int,
    val userId: Int
)

fun FavoriteProductDto.toFavoriteProduct(): FavoriteProduct {
    return FavoriteProduct(id = this.id, productId = this.productId, userId = this.userId)
}

fun FavoriteProduct.toFavoriteProductDto(): FavoriteProductDto {
    return FavoriteProductDto(id = this.id, productId = this.productId, userId = this.userId)
}
