package com.techzo.cambiazo.data.remote.products

import com.techzo.cambiazo.domain.ProductCategory


data class ProductCategoryDto(
    val id: Int,
    val name: String
)
fun ProductCategoryDto.toProductCategory(): ProductCategory {
    return ProductCategory(
        id = id,
        name = name
    )
}