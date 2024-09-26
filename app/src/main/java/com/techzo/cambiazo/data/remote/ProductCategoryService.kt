package com.techzo.cambiazo.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers

interface ProductCategoryService {

    @Headers("Content-Type: application/json",
        "Accept: application/json")
    @GET("product-categories")
    suspend fun getProductCategories(): Response<List<ProductCategoryDto>>
}