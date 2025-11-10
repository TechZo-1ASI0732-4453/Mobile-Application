package com.techzo.cambiazo.data.remote.chat

data class ActiveConversationDto(
    val conversationId: String,
    val peerId: String?,
    val lastMessage: String?,
    val updatedAt: String?,
    val unreadCount: Int,
    val exchangeId: String? = null
)