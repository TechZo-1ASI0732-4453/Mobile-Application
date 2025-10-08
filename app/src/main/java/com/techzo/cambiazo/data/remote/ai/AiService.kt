package com.techzo.cambiazo.data.remote.ai

import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface AiService {
    @Multipart
    @POST("exchanges/ai/suggest")
    suspend fun suggestFromImage(
        @Query("userId") userId: Long,
        @Part file: MultipartBody.Part
    ): ProductSuggestionDto
}
