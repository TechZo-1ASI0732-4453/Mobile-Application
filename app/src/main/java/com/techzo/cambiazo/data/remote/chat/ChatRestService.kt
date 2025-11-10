package com.techzo.cambiazo.data.remote.chat

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ChatRestService {

    @GET("chat/active/{userId}")
    suspend fun getActiveConversations(
        @Path("userId") userId: String
    ): Response<List<ActiveConversationDto>>

    @POST("chat/read/{userId}/{conversationId}")
    suspend fun markRead(
        @Path("userId") userId: String,
        @Path("conversationId") conversationId: String
    ): Response<Unit>

    @GET("chat/messages/{conversationId}")
    suspend fun getMessages(
        @Path("conversationId") conversationId: String
    ): Response<List<ServerChatDto>>

    @POST("chat/conversations/open")
    suspend fun openConversation(
        @Query("conversationId") conversationId: String? = null,
        @Query("exchangeId") exchangeId: String? = null
    ): Response<String>
}
