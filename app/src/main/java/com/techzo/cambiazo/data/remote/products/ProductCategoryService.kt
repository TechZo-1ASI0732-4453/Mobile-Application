package com.techzo.cambiazo.data.remote.products

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface ProductCategoryService {

    @Headers("Content-Type: application/json",
        "Accept: application/json")
    @GET("product-categories")
    suspend fun getProductCategories(): Response<List<ProductCategoryDto>>

    @GET("product-categories/{id}")
    suspend fun getCategoryById(@Path("id") id: Int): Response<ProductCategoryDto>

}