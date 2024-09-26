package com.techzo.cambiazo.data.remote

import com.techzo.cambiazo.domain.model.ProductCategory

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