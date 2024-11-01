package com.techzo.cambiazo.data.remote.auth

import com.google.gson.annotations.SerializedName

data class UserSignInRequestDto(
    @SerializedName("username")
    val username: String,
    @SerializedName("password")
    val password: String
)
