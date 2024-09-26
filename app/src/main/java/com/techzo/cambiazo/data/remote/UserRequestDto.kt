package com.techzo.cambiazo.data.remote

import com.google.gson.annotations.SerializedName

data class UserRequestDto(
    @SerializedName("username")
    val username: String,
    @SerializedName("password")
    val password: String
)
