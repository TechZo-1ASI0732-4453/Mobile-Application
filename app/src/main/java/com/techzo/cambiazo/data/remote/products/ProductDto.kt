package com.techzo.cambiazo.data.remote.products

import com.techzo.cambiazo.domain.Product


data class ProductDto(
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

fun ProductDto.toProduct(): Product {
    return Product(
        id = id,
        name = name,
        description = description,
        desiredObject = desiredObject,
        price = price,
        image = image,
        boost = boost,
        available = available,
        productCategoryId = productCategoryId,
        userId = userId,
        districtId = districtId
    )
}
