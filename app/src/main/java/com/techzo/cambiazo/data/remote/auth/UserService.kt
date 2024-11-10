package com.techzo.cambiazo.data.remote.auth

import com.techzo.cambiazo.data.remote.products.ProductDto
import com.techzo.cambiazo.domain.UserEdit
import com.techzo.cambiazo.domain.UserSignIn
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserService {

    @Headers("Content-Type: application/json",
        "Accept: application/json")
    @GET("users")
    suspend fun getUsers(): Response<List<UserDto>>

    @Headers("Content-Type: application/json",
        "Accept: application/json")
    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: Int): Response<UserDto>

    @Headers("Content-Type: application/json",
        "Accept: application/json")
    @GET("users/username/{username}")
    suspend fun getUserByUsername(@Path("username") username: String): Response<Unit>

    @Headers("Content-Type: application/json",
        "Accept: application/json")
    @PUT("users/edit/profile/{id}")
    suspend fun updateUserById(@Path("id") id: Int, @Body user: UserEdit): Response<UserSignIn>

    @Headers("Content-Type: application/json",
        "Accept: application/json")
    @DELETE("users/delete/{userId}")
    suspend fun deleteUser(@Path("userId") userId: Int): Response<Unit>

    @Headers("Content-Type: application/json",
        "Accept: application/json")
    @GET("users/email/{email}")
    suspend fun getUserByEmail(@Path("email") email: String): Response<SendEmailResponseDto>


    @Headers("Content-Type: application/json",
        "Accept: application/json")
    @PUT("users/edit/password/{username}")
    suspend fun updateUserPassword(@Path("username") username: String, @Body newPassword: NewPassword): Response<Unit>

}