package com.techzo.cambiazo.data.local.chat

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "conversations", indices = [Index("lastUpdatedAt")])
data class ConversationEntity(
    @PrimaryKey val conversationId: String,
    val peerUserId: String,
    val lastMessagePreview: String,
    val lastUpdatedAt: Long,
    val unreadCount: Int
)