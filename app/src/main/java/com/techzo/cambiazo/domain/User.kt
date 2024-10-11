package com.techzo.cambiazo.domain

import com.google.gson.annotations.SerializedName

data class User(
    val id: Int,
    val username: String,
    val name: String,
    val phoneNumber: String,
    val profilePicture: String,
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
