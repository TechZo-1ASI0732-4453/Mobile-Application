package com.techzo.cambiazo.data.remote.products

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
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

    @Headers("Content-Type: application/json",
        "Accept: application/json")
    @GET("products/{id}")
    suspend fun getProductById(@Path("id") id: Int): Response<ProductDto>

    @Headers("Content-Type: application/json",
        "Accept: application/json")
    @GET("products/user/{id}")
    suspend fun getProductsByUserId(@Path("id") id: Int): Response<List<ProductDto>>

    @Headers("Content-Type: application/json","Accept: application/json")
    @DELETE("products/delete/{productId}")
    suspend fun deleteProduct(@Path("productId") productId: Int): Response<Unit>

    //post
    @Headers("Content-Type: application/json","Accept: application/json")
    @POST("products")
    suspend fun createProduct(@Body productDto: CreateProductDto): Response<CreateProductDto>
}