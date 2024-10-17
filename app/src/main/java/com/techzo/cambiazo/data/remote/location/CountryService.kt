package com.techzo.cambiazo.data.remote.location

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface CountryService {

    @Headers("Content-Type: application/json",
        "Accept: application/json")
    @GET("countries")
    suspend fun getCountries(): Response<List<CountryDto>>

    //by id
    @Headers("Content-Type: application/json",
        "Accept: application/json")
    @GET("countries/{id}")
    suspend fun getCountryById(@Path("id") id: Int): Response<CountryDto>
}