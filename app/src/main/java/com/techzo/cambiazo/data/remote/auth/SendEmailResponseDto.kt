package com.techzo.cambiazo.data.remote.auth

data class SendEmailResponseDto(
    val name: String,
    val isGoogleAccount: Boolean
)

data class NewPassword(
    val newPassword: String,
)