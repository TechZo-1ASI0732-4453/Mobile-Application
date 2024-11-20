package com.techzo.cambiazo.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class User(
    val id: Int,
    val username: String,
    val name: String,
    val phoneNumber: String,
    val profilePicture: String,
    val createdAt: Date,
    val isGoogleAccount: Boolean,
    val roles: List<String>
): Parcelable


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
    val roles: List<String>,
    val isGoogleAccount: Boolean = false
)

data class UserEdit(
    val username: String,
    val name: String,
    val phoneNumber: String,
    val profilePicture: String
)

data class UserUsername(
    val name: String
)