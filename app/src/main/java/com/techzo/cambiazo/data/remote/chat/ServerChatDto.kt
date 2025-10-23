package com.techzo.cambiazo.data.remote.chat

data class ServerChatDto(
    val senderId: String,
    val receiverId: String,
    val conversationId: String,
    val content: String,
    val timestamp: String? = null,
    val id: String? = null,
    val clientMessageId: String? = null
)
