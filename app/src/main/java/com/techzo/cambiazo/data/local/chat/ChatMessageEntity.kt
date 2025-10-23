package com.techzo.cambiazo.data.local.chat

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.techzo.cambiazo.domain.MessageType
import com.techzo.cambiazo.domain.SendStatus

@Entity(
    tableName = "chat_messages",
    indices = [
        Index(value = ["conversationId", "createdAt", "localId"]),
        Index(value = ["serverId"], unique = true),
        Index(value = ["clientMessageId"], unique = true)
    ]
)
data class ChatMessageEntity(
    @PrimaryKey val localId: String,
    val serverId: String?,
    val clientMessageId: String?,
    val conversationId: String,
    val senderId: String,
    val receiverId: String,
    val content: String,
    val type: MessageType = MessageType.TEXT,
    val status: SendStatus = SendStatus.SENT,
    val createdAt: Long,
    val isMine: Boolean
)