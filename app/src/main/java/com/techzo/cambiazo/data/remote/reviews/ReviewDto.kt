package com.techzo.cambiazo.data.remote.reviews

import com.techzo.cambiazo.domain.Review
import com.techzo.cambiazo.domain.ReviewAverageUser
import com.techzo.cambiazo.domain.User

data class ReviewDto(
    val message: String,
    val rating: Int,
    val state: String,
    val exchangeId: Int,
    val userAuthorId: Int,
    val userReceptorId: Int,
    val userAuthor: User,
    val userReceptor: User
)

fun ReviewDto.toReview(): Review {
    return Review(
        message = message,
        rating = rating,
        state = state,
        exchangeId = exchangeId,
        userAuthorId = userAuthorId,
        userReceptorId = userReceptorId,
        userAuthor = userAuthor,
        userReceptor = userReceptor
    )
}

data class ReviewAverageUserDto(
    val averageRating: Double,
    val countReviews: Int,
)

fun ReviewAverageUserDto.toReviewAverageUser(): ReviewAverageUser {
    return ReviewAverageUser(
        averageRating = averageRating,
        countReviews = countReviews
    )
}