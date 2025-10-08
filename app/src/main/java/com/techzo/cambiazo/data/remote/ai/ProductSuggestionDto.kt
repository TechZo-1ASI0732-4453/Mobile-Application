package com.techzo.cambiazo.data.remote.ai

data class ProductSuggestionDto(
    val name: String?,
    val description: String?,
    val price: String?,
    val category: String?,
    val suggest: String?,
    val score: Int?
)