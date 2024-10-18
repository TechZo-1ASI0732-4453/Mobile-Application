package com.techzo.cambiazo.data.remote.reviews

import com.techzo.cambiazo.domain.Review

data class ReviewDto(
    val message: String,
    val rating: Int,
    val state: String,
    val exchangeId: Int,
    val userAuthorId: Int,
    val userReceptorId: Int
)

fun ReviewDto.toReview(): Review {
    return Review(
        message = message,
        rating = rating,
        state = state,
        exchangeId = exchangeId,
        userAuthorId = userAuthorId,
        userReceptorId = userReceptorId
    )
}