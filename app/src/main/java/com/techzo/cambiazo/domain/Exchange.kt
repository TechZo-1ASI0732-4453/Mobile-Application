package com.techzo.cambiazo.domain

data class Exchange(
    val id: Int,
    val productOwn: Product,
    val productChange: Product,
    val userOwn: User,
    val userChange: User,
    val status: String,
)
