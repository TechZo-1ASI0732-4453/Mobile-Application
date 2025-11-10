package com.techzo.cambiazo.data.remote.chat

data class ChatPayload(
    val senderId: String,
    val receiverId: String,
    val conversationId: String,
    val content: String,
)