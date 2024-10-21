package com.techzo.cambiazo.data.repository

import com.techzo.cambiazo.common.Resource
import com.techzo.cambiazo.data.remote.reviews.ReviewService
import com.techzo.cambiazo.data.remote.reviews.toReview
import com.techzo.cambiazo.data.remote.reviews.toReviewAverageUser
import com.techzo.cambiazo.domain.Review
import com.techzo.cambiazo.domain.ReviewAverageUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ReviewRepository @Inject constructor(
    private val reviewService: ReviewService
) {
    suspend fun getAverageRatingByUserId(userId: Int): Resource<ReviewAverageUser> =
        withContext(Dispatchers.IO) {
            try {
                val response = reviewService.getAverageRatingByUserId(userId)
                if (response.isSuccessful) {
                    response.body()?.let { reviewAverageUserDto ->
                        return@withContext Resource.Success(data = reviewAverageUserDto.toReviewAverageUser())
                    }
                    return@withContext Resource.Error("No se encontraron datos para el usuario")
                }
                return@withContext Resource.Error(response.message())
            } catch (e: Exception) {
                return@withContext Resource.Error(e.message ?: "Ocurrió un error")
            }
        }

    suspend fun getReviewsByUserId(userId: Int): Resource<List<Review>> =
        withContext(Dispatchers.IO) {
            try {
                val response = reviewService.getReviewsByUserId(userId)
                if (response.isSuccessful) {
                    response.body()?.let { reviewDtoList ->
                        val reviews = reviewDtoList.map { it.toReview() }
                        return@withContext Resource.Success(data = reviews)
                    }
                    return@withContext Resource.Error("No se encontraron reseñas para el usuario")
                }
                return@withContext Resource.Error(response.message())
            } catch (e: Exception) {
                return@withContext Resource.Error(e.message ?: "Ocurrió un error")
            }
        }
}
