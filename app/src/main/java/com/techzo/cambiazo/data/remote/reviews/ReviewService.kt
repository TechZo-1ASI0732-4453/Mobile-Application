package com.techzo.cambiazo.data.remote.reviews

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface ReviewService {
    @Headers("Content-Type: application/json", "Accept: application/json")
    @GET("reviews/user-receptor/{userId}")
    suspend fun getReviewsByUserId(@Path("userId") userId: Int): Response<List<ReviewDto>>

    @Headers("Content-Type: application/json", "Accept: application/json")
    @GET("reviews/average-count/{userId}")
    suspend fun getAverageRatingByUserId(@Path("userId") userId: Int): Response<ReviewAverageUserDto>


    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("reviews")
    suspend fun addReview(@Body reviewDto: ReviewDto): Response<ReviewDto>

    @Headers("Content-Type: application/json", "Accept: application/json")
    @DELETE("reviews/delete/{reviewId}")
    suspend fun deleteReview(@Path("reviewId") reviewId: Int): Response<ReviewDto>
}