package com.techzo.cambiazo.data.remote

import com.techzo.cambiazo.domain.model.Product


/*
"id": 1,
"name": "Lámpara de Mesa",
"description": "Lámpara de mesa moderna y elegante.",
"desiredObject": "Busco un juego de sábanas de algodón.",
"price": 50,
"image": "https://http2.mlstatic.com/D_NQ_NP_899399-MLA50279736451_062022-O.webp",
"boost": true,
"available": true,
"productCategoryId": 16,
"userId": 1,
"districtId": 162
 */

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
