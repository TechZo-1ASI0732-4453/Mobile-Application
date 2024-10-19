package com.techzo.cambiazo.data.remote.products

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface FavoriteProductService {

    @Headers("Content-Type: application/json", "Accept: application/json")
    @GET("favorite-products/{userId}")
    suspend fun getFavoriteProductsByUserId(@Path("userId") userId: Int): Response<List<FavoriteProductDto>>

    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("favorite-products")
    suspend fun addFavoriteProduct(@Body favoriteProductDto: FavoriteProductDto): Response<FavoriteProductDto>

    @Headers("Content-Type: application/json", "Accept: application/json")
    @DELETE("favorite-products/delete/{userId}/{favoriteProductId}")
    suspend fun removeFavoriteProduct(@Path("userId") userId: Int, @Path("favoriteProductId") favoriteProductId: Int): Response<Unit>
}
