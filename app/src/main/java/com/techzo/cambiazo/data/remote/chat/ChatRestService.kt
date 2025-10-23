package com.techzo.cambiazo.data.remote.chat

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ChatRestService {
    @GET("chat/active/{userId}")
    suspend fun getActiveConversations(@Path("userId") userId: String): Response<List<ActiveConversationDto>>

    @POST("chat/read/{userId}/{conversationId}")
    suspend fun markRead(
        @Path("userId") userId: String,
        @Path("conversationId") conversationId: String
    ): Response<Unit>
}