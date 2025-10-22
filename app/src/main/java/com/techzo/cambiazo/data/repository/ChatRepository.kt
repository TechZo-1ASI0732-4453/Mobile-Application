package com.techzo.cambiazo.data.repository

import com.techzo.cambiazo.data.remote.chat.ChatService
import com.techzo.cambiazo.domain.Chat
import javax.inject.Inject

class ChatRepository @Inject constructor(private val service: ChatService) {

    fun connect(conversationId: String, onMessage: (Chat) -> Unit) {
        service.connect(conversationId, onMessage)
    }

    fun sendMessage(message: Chat) {
        service.sendMessage(message)
    }

    fun disconnect() {
        service.disconnect()
    }
}