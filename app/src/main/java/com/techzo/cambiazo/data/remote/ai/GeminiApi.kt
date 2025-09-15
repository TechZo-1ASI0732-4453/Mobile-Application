package com.techzo.cambiazo.data.remote.ai

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface GeminiApi {
    @Multipart
    @POST("files")
    suspend fun uploadFile(
        @Header("Authorization") bearer: String,
        @Part content: MultipartBody.Part
    ): Response<String>

    @POST("predictions")
    suspend fun createPrediction(
        @Header("Authorization") bearer: String,
        @Body body: String
    ): Response<String>

    @GET("predictions/{id}")
    suspend fun getPrediction(
        @Header("Authorization") bearer: String,
        @Path("id") id: String
    ): Response<String>
}