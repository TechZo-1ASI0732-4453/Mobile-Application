package com.techzo.cambiazo.domain.model

data class User(
    val username: String,
    val token: String
)

data class UserSignUp(
    val id: Int,
    val username: String,
    val roles: List<String>
)