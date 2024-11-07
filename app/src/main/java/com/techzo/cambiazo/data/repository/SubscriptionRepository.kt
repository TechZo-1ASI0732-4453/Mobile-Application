package com.techzo.cambiazo.data.repository

import com.techzo.cambiazo.common.Resource
import com.techzo.cambiazo.data.remote.auth.toUser
import com.techzo.cambiazo.data.remote.products.CreateProductDto
import com.techzo.cambiazo.data.remote.products.toProduct
import com.techzo.cambiazo.data.remote.subscriptions.SubscriptionRequestDto
import com.techzo.cambiazo.data.remote.subscriptions.SubscriptionResponseDto
import com.techzo.cambiazo.data.remote.subscriptions.SubscriptionService
import com.techzo.cambiazo.data.remote.subscriptions.toPlan
import com.techzo.cambiazo.data.remote.subscriptions.toSubscription
import com.techzo.cambiazo.domain.Plan
import com.techzo.cambiazo.domain.Product
import com.techzo.cambiazo.domain.Subscription
import com.techzo.cambiazo.domain.SubscriptionResponse
import com.techzo.cambiazo.domain.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SubscriptionRepository(private val subscriptionService: SubscriptionService) {

    suspend fun getSubscriptionByUserId(id: Int): Resource<Subscription> = withContext(Dispatchers.IO) {
        try {
            val response = subscriptionService.getUserSubscription(id)
            if (response.isSuccessful) {
                response.body()?.let { subscriptionDto ->
                    return@withContext Resource.Success(data = subscriptionDto.toSubscription())
                }
                return@withContext Resource.Error("No subscriptions found")
            }
            return@withContext Resource.Error(response.message())
        } catch (e: Exception) {
            return@withContext Resource.Error(e.message ?: "An error occurred")
        }
    }


    suspend fun createSubscription(subscription: SubscriptionRequestDto): Resource<SubscriptionResponse> = withContext(Dispatchers.IO) {
        try {
            val response = subscriptionService.createSubscription(subscription)
            if (response.isSuccessful) {
                response.body()?.let { subscriptionResponseDto ->
                    return@withContext Resource.Success(data = subscriptionResponseDto.toSubscription())
                }
                return@withContext Resource.Error("Response body is null")
            }
            return@withContext Resource.Error(response.message())
        } catch (e: Exception) {
            return@withContext Resource.Error(e.message ?: "Ocurrió un error")
        }
    }


    suspend fun updateSubscriptionById(subscriptionId: Int, subscription: SubscriptionRequestDto ): Resource<SubscriptionResponseDto> = withContext(Dispatchers.IO) {
        try {
            val response = subscriptionService.updateSubscription(subscriptionId, subscription)
            if (response.isSuccessful) {
                response.body()?.let { subscription ->
                    return@withContext Resource.Success(data = subscription)
                }
                return@withContext Resource.Error("No subscriptions found")
            }
            return@withContext Resource.Error(response.message())
        } catch (e: Exception) {
            return@withContext Resource.Error(e.message ?: "An error occurred")
        }
    }


    suspend fun getPlans(): Resource<List<Plan>> = withContext(Dispatchers.IO) {
        try {
            val response = subscriptionService.getPlans()
            if (response.isSuccessful) {
                response.body()?.let { plansDto ->
                    val plans = plansDto.map { it.toPlan() }
                    return@withContext Resource.Success(data = plans)
                }
                return@withContext Resource.Error("No plans found")
            }
            return@withContext Resource.Error(response.message())
        } catch (e: Exception) {
            return@withContext Resource.Error(e.message ?: "An error occurred")
        }
    }

}