package com.techzo.cambiazo.domain

import java.util.Date


data class User(
    val id: Int,
    val username: String,
    val name: String,
    val phoneNumber: String,
    val profilePicture: String,
    val createdAt: Date,
    val roles: List<String>
)

data class UserSignIn(
    val id: Int,
    val username: String,
    val name: String,
    val phoneNumber: String,
    val profilePicture: String,
    val token: String
)

data class UserSignUp(
    val id: Int,
    val username: String,
    val roles: List<String>
)
