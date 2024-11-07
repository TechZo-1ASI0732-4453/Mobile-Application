package com.techzo.cambiazo.domain


import java.util.Date

data class Subscription (
    val id: Int,
    val startDate: String,
    val endDate: String,
    val state: String,
    val userId: Int,
    val plan: Plan
)

data class SubscriptionResponse (
    val id: Int,
    val startDate: String,
    val endDate: String,
    val state: String,
    val userId: Int,
    val planId: Int
)

data class Plan(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val benefits: List<Benefit>
)

data class Benefit(
    val id: Int,
    val description: String,
    val planId: Int
)