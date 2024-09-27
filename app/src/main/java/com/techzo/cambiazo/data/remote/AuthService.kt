package com.techzo.cambiazo.data.remote

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthService {
    @Headers(
        value = [
            "Content-Type: application/json",
            "accept: application/json"
        ]
    )
    @POST("authentication/sign-in")
    fun signIn(@Body request: UserRequestDto): Call<UserResponseDto>

    @Headers(
        value = [
            "Content-Type: application/json",
            "accept: application/json"
        ]
    )
    @POST("authentication/sign-up")
    fun signUp(@Body request: SignUpRequestDto): Call<SignUpResponseDto>
}