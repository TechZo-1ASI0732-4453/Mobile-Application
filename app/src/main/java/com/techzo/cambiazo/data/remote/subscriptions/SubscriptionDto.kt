package com.techzo.cambiazo.data.remote.subscriptions

import com.techzo.cambiazo.domain.Benefit
import com.techzo.cambiazo.domain.Plan
import com.techzo.cambiazo.domain.Subscription
import com.techzo.cambiazo.domain.SubscriptionResponse

data class SubscriptionDto(
    val id: Int,
    val startDate: String,
    val endDate: String,
    val state: String,
    val userId: Int,
    val plan: Plan
)

fun SubscriptionDto.toSubscription(): Subscription {
    return Subscription(
        id = id,
        startDate = startDate,
        endDate = endDate,
        state = state,
        userId = userId,
        plan = plan
    )
}

data class  SubscriptionRequestDto(
    val state: String,
    val userId: Int,
    val planId: Int
)

data class SubscriptionResponseDto(
    val id: Int,
    val state: String,
    val planId: Int,
    val userId: Int,
    val startDate: String,
    val endDate: String,
)

fun SubscriptionResponseDto.toSubscription(): SubscriptionResponse {
    return SubscriptionResponse(
        id = id,
        state = state,
        planId = planId,
        userId = userId,
        startDate = startDate,
        endDate = endDate
    )
}

data class PlansDto(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val benefits: List<Benefit>
)

fun PlansDto.toPlan(): Plan {
    return Plan(
        id = id,
        name = name,
        description = description,
        price = price,
        benefits = benefits
    )
}

data class BenefitsDto(
    val id: Int,
    val description: String,
    val planId: String
)