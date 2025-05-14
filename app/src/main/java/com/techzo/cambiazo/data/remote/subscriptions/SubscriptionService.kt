package com.techzo.cambiazo.data.remote.subscriptions

import com.techzo.cambiazo.data.remote.products.ProductDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface SubscriptionService {

    // SUBSCRIPTIONS
    @Headers("Content-Type: application/json", "Accept: application/json")
    @GET("subscriptions/user/{id}")
    suspend fun getUserSubscription(@Path("id") id: Int): Response<SubscriptionDto>


    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("subscriptions")
    suspend fun createSubscription(@Body subscriptionDto: SubscriptionRequestDto): Response<SubscriptionResponseDto>

    @PUT("subscriptions/status/{subscriptionId}")
    suspend fun updateSubscription(
        @Path("subscriptionId") subscriptionId: Int,
        @Body subscriptionRequest: SubscriptionRequestDto
    ): Response<SubscriptionResponseDto>

    //PLANS
    @Headers("Content-Type: application/json", "Accept: application/json")
    @GET("plans")
    suspend fun getPlans(): Response<List<PlansDto>>


}