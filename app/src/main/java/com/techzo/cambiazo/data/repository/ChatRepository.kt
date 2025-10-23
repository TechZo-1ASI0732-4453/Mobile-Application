package com.techzo.cambiazo.data.repository

import com.techzo.cambiazo.common.Constants
import com.techzo.cambiazo.data.local.chat.ChatMessageDao
import com.techzo.cambiazo.data.local.chat.ChatMessageEntity
import com.techzo.cambiazo.data.local.chat.ConversationDao
import com.techzo.cambiazo.data.local.chat.ConversationEntity
import com.techzo.cambiazo.data.remote.chat.*
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
    private val messageDao: ChatMessageDao,
    private val conversationDao: ConversationDao,
    private val chatRest: ChatRestService
) {
    private val io = CoroutineScope(Dispatchers.IO)

    init {
        service.setChatConsumer { dto ->
            io.launch { upsertIncoming(dto) }
        }
        service.connectIfNeeded()
    }

    fun startUserInbox(userId: String) {
        service.subscribeInbox(userId) { active ->
            io.launch {
                upsertConversation(active)
                service.subscribeConversation(active.conversationId)
            }
        }
        io.launch { refreshActiveConversations(userId) }
    }

    private suspend fun refreshActiveConversations(userId: String) {
        val resp = chatRest.getActiveConversations(userId)
        val list = resp.body().orEmpty()
        list.forEach {
            upsertConversation(it)
            service.subscribeConversation(it.conversationId)
        }
    }

    private suspend fun upsertConversation(ac: ActiveConversationDto) {
        val ts = millisFromIsoSafe(ac.updatedAt) ?: System.currentTimeMillis()
        val entity = ConversationEntity(
            conversationId = ac.conversationId,
            peerUserId = ac.peerId ?: "",
            lastMessagePreview = ac.lastMessage ?: "",
            lastUpdatedAt = ts,
            unreadCount = ac.unreadCount
        )
        conversationDao.upsert(entity)
    }

    fun observeConversation(conversationId: String, currentUserId: String): Flow<List<Chat>> =
        messageDao.observeByConversation(conversationId)
            .map { list -> list.map { it.toDomain() } }

    fun subscribeConversation(conversationId: String) {
        service.subscribeConversation(conversationId)
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
            onOk = { /* esperamos eco */ },
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

    fun disconnectAll() = service.disconnectAll()

    private suspend fun upsertIncoming(dto: ServerChatDto) {
        val existingByClientId = dto.clientMessageId?.takeIf { it.isNotBlank() }?.let { cid ->
            messageDao.findByClientMessageId(cid)
        }
        if (existingByClientId != null) {
            messageDao.updateStatus(existingByClientId.localId, SendStatus.SENT)
            if (dto.id != null && existingByClientId.serverId == null) {
                messageDao.update(existingByClientId.copy(serverId = dto.id, status = SendStatus.SENT))
            }
            return
        }

        val existingByServerId = dto.id?.takeIf { it.isNotBlank() }?.let { sid ->
            messageDao.findByServerId(sid)
        }
        if (existingByServerId != null) return

        val currentUserId = Constants.user?.id?.toString() ?: ""
        val isMine = dto.senderId == currentUserId

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
            isMine = isMine
        )
        messageDao.insert(entity)
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