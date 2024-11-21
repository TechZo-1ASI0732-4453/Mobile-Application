package com.techzo.cambiazo.data.remote.auth

import com.google.gson.annotations.SerializedName
import com.techzo.cambiazo.domain.UserSignIn

data class UserSignInResponseDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("username")
    val username: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("phoneNumber")
    val phoneNumber: String,
    @SerializedName("profilePicture")
    val profilePicture: String,
    @SerializedName("isGoogleAccount")
    val isGoogleAccount: Boolean,
    @SerializedName("token")
    val token: String,

)

fun UserSignInResponseDto.toUserSignIn() = UserSignIn(
    id = id,
    username = username,
    name = name,
    phoneNumber = phoneNumber,
    profilePicture = profilePicture,
    isGoogleAccount = isGoogleAccount,
    token = token
)