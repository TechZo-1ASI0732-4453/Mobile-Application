package com.techzo.cambiazo.domain

data class Review (
    val message: String,
    val rating: Int,
    val state: String,
    val exchangeId: Int,
    val userAuthorId: Int,
    val userReceptorId: Int,
    val userAuthor: User,
    val userReceptor: User
)

data class ReviewAverageUser (
    val averageRating: Double,
    val countReviews: Int,
)