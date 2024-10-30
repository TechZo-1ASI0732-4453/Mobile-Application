package com.techzo.cambiazo.data.remote.products

import com.techzo.cambiazo.domain.Product
import com.techzo.cambiazo.domain.ProductCategory
import com.techzo.cambiazo.domain.User

data class ProductDto(
    val id: Int,
    val available: Boolean,
    val boost: Boolean,
    val description: String,
    val desiredObject: String,
    val image: String,
    val user: User,
    val location: Location,
    val name: String,
    val price: Int,
    val productCategory: ProductCategory
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
        productCategory = productCategory,
        user = user,
        location = location
    )
}