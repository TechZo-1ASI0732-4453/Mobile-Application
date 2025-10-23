package com.techzo.cambiazo.data.repository

import com.techzo.cambiazo.data.local.chat.ChatMessageDao
import com.techzo.cambiazo.data.local.chat.ChatMessageEntity
import com.techzo.cambiazo.data.remote.chat.ChatPayload
import com.techzo.cambiazo.data.remote.chat.ChatService
import com.techzo.cambiazo.data.remote.chat.ServerChatDto
import com.techzo.cambiazo.domain.Chat
import com.techzo.cambiazo.domain.MessageType
import com.techzo.cambiazo.domain.SendStatus
import com.techzo.cambiazo.domain.isoFromMillisSafe
import com.techzo.cambiazo.domain.millisFromIsoSafe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val service: ChatService,
    private val messageDao: ChatMessageDao
) {
    private val io = CoroutineScope(Dispatchers.IO)

    fun observeConversation(conversationId: String, currentUserId: String): Flow<List<Chat>> =
        messageDao.observeByConversation(conversationId)
            .map { list -> list.map { it.toDomain() } }

    fun subscribe(conversationId: String, currentUserId: String) {
        service.ensureConnected()
        service.subscribeConversation(conversationId) { dto ->
            io.launch { upsertIncoming(dto, currentUserId) }
        }
    }

    fun sendMessage(me: String, peer: String, conversationId: String, content: String) {
        val now = System.currentTimeMillis()
        val clientId = UUID.randomUUID().toString()
        val type = if (content.startsWith("L0C4t10N:")) MessageType.LOCATION else MessageType.TEXT

        val local = ChatMessageEntity(
            localId = clientId,
            serverId = null,
            clientMessageId = clientId,
            conversationId = conversationId,
            senderId = me,
            receiverId = peer,
            content = content,
            type = type,
            status = SendStatus.SENDING,
            createdAt = now,
            isMine = true
        )

        io.launch { messageDao.insert(local) }

        val payload = ChatPayload(
            senderId = me,
            receiverId = peer,
            conversationId = conversationId,
            content = content,
            clientMessageId = clientId
        )

        service.sendPayload(
            payload,
            onOk = { io.launch { messageDao.updateStatus(local.localId, SendStatus.SENT) } },
            onError = { io.launch { messageDao.updateStatus(local.localId, SendStatus.FAILED) } }
        )

        io.launch {
            delay(15_000)
            val stillSending = messageDao.findByClientMessageId(clientId)
            if (stillSending?.status == SendStatus.SENDING) {
                messageDao.updateStatus(local.localId, SendStatus.FAILED)
            }
        }
    }

    private suspend fun upsertIncoming(dto: ServerChatDto, currentUserId: String) {
        if (dto.senderId == currentUserId) return

        val ts = millisFromIsoSafe(dto.timestamp) ?: System.currentTimeMillis()
        val entity = ChatMessageEntity(
            localId = UUID.randomUUID().toString(),
            serverId = dto.id,
            clientMessageId = dto.clientMessageId,
            conversationId = dto.conversationId,
            senderId = dto.senderId,
            receiverId = dto.receiverId,
            content = dto.content,
            type = if (dto.content.startsWith("L0C4t10N:")) MessageType.LOCATION else MessageType.TEXT,
            status = SendStatus.SENT,
            createdAt = ts,
            isMine = false
        )
        messageDao.upsertFromServer(entity)
    }

    private fun ChatMessageEntity.toDomain(): Chat =
        Chat(
            localId = localId,
            serverId = serverId,
            clientMessageId = clientMessageId,
            conversationId = conversationId,
            senderId = senderId,
            receiverId = receiverId,
            content = content,
            type = type,
            status = status,
            createdAtMillis = createdAt,
            isMine = isMine,
            timestampIso = isoFromMillisSafe(createdAt)
        )
}