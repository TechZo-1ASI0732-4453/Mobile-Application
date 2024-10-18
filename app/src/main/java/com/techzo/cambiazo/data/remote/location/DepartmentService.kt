package com.techzo.cambiazo.data.remote.location

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface DepartmentService {
    @Headers("Content-Type: application/json",
        "Accept: application/json")
    @GET("departments")
    suspend fun getDepartments(): Response<List<DepartmentDto>>


    //by id
    @Headers("Content-Type: application/json",
        "Accept: application/json")
    @GET("departments/{id}")
    suspend fun getDepartmentById(@Path("id") id: Int): Response<DepartmentDto>
}
