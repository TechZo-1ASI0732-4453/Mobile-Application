package com.techzo.cambiazo.data.remote.location

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface DistrictService {
    @Headers("Content-Type: application/json",
        "Accept: application/json")
    @GET("districts")
    suspend fun getDistricts(): Response<List<DistrictDto>>

    //by id
    @Headers("Content-Type: application/json",
        "Accept: application/json")
    @GET("districts/{id}")
    suspend fun getDistrictById(@Path("id") id: Int): Response<DistrictDto>
}