package com.techzo.cambiazo.data.remote.chat

data class ChatMessagePayload(
    val id: String? = null,
    val senderId: String,
    val receiverId: String,
    val conversationId: String?,
    val exchangeId: String? = null,
    val content: String? = null,
    val type: MessageType = MessageType.TEXT,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val locationLabel: String? = null,
    val timestamp: String? = null
) {
    enum class MessageType { TEXT, LOCATION }
}
