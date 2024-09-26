package com.techzo.cambiazo.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface ProductService {
    @Headers("Content-Type: application/json",
        "Accept: application/json")
    @GET("products")
    suspend fun getProducts(): Response<List<ProductDto>>

    @Headers("Content-Type: application/json",
        "Accept: application/json")
    @GET("products/product-category/{id}")
    suspend fun getProductsByCategoryId(@Path("id") id: Int): Response<List<ProductDto>>
}