package com.techzo.cambiazo.data.remote.auth

import com.techzo.cambiazo.data.remote.products.ProductDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
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

}