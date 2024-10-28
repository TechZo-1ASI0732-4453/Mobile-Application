package com.techzo.cambiazo.data.repository

import com.techzo.cambiazo.common.Resource
import com.techzo.cambiazo.data.remote.reviews.ReviewRequestDto
import com.techzo.cambiazo.data.remote.reviews.ReviewService
import com.techzo.cambiazo.data.remote.reviews.toReview
import com.techzo.cambiazo.data.remote.reviews.toReviewAverageUser
import com.techzo.cambiazo.domain.Review
import com.techzo.cambiazo.domain.ReviewAverageUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import kotlinx.coroutines.async


class ReviewRepository(private val reviewService: ReviewService) {

    suspend fun getAverageRatingAndReviewsByUserId(userId: Int): Resource<Pair<ReviewAverageUser, List<Review>>> =
        withContext(Dispatchers.IO) {
            try {
                val averageRatingDeferred = async { fetchAverageRating(userId) }
                val reviewsDeferred = async { fetchReviews(userId) }

                val averageRatingResult = averageRatingDeferred.await()
                val reviewsResult = reviewsDeferred.await()

                if (averageRatingResult is Resource.Success && reviewsResult is Resource.Success) {
                    Resource.Success(Pair(averageRatingResult.data!!, reviewsResult.data!!))
                } else {
                    val errorMessage = listOfNotNull(
                        averageRatingResult.message,
                        reviewsResult.message
                    ).joinToString("; ")
                    Resource.Error(errorMessage.ifEmpty { "Error al cargar las reseñas y calificaciones" })
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Ocurrió un error al cargar las reseñas y calificaciones")
            }
        }

    private suspend fun fetchAverageRating(userId: Int): Resource<ReviewAverageUser> {
        val response = reviewService.getAverageRatingByUserId(userId)
        return if (response.isSuccessful) {
            response.body()?.let { Resource.Success(it.toReviewAverageUser()) }
                ?: Resource.Error("No se encontró datos para el usuario")
        } else {
            Resource.Error(response.message())
        }
    }

    private suspend fun fetchReviews(userId: Int): Resource<List<Review>> {
        val response = reviewService.getReviewsByUserId(userId)
        return if (response.isSuccessful) {
            response.body()?.let { reviewsDto ->
                Resource.Success(reviewsDto.map { it.toReview() })
            } ?: Resource.Error("No se encontraron reseñas")
        } else {
            Resource.Error(response.message())
        }
    }

    suspend fun getReviewsByUserId(userId: Int): Resource<List<Review>> = withContext(Dispatchers.IO) {
        try {
            val response = reviewService.getReviewsByUserId(userId)
            if (response.isSuccessful) {
                response.body()?.let { reviewsDto ->
                    val reviews = reviewsDto.map { it.toReview() }
                    return@withContext Resource.Success(data = reviews)
                }
                return@withContext Resource.Error("No reviews found")
            }
            return@withContext Resource.Error(response.message())
        } catch (e: Exception) {
            return@withContext Resource.Error(e.message ?: "An error occurred")
        }
    }

    suspend fun addReview(message: String, rating: Int, state:String, userAuthorId:Int, userReceptorId: Int, exchangeId: Int): Resource<Review> = withContext(Dispatchers.IO) {
        try {
            val response = reviewService.addReview(ReviewRequestDto(message, rating, state,exchangeId, userAuthorId, userReceptorId))
            if (response.isSuccessful) {
                response.body()?.let { reviewDto ->
                    return@withContext Resource.Success(data = reviewDto.toReview())
                }
                return@withContext Resource.Error("No review found")
            }
            return@withContext Resource.Error(response.message())
        } catch (e: Exception) {
            return@withContext Resource.Error(e.message ?: "An error occurred")
        }
    }
}