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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
        service.setChatConsumer { dto -> io.launch { upsertIncoming(dto) } }
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
    suspend fun syncConversation(conversationId: String) {
        try {
            val resp = chatRest.getMessages(conversationId)
            if (!resp.isSuccessful) return
            val list = resp.body().orEmpty()

            list.forEach { dto ->
                upsertIncoming(dto, fromSync = true)
            }
        } catch (_: Throwable) {
        }
    }

    private suspend fun upsertConversation(ac: ActiveConversationDto) {
        val ts = DateTimeUtils.parseIsoToEpochMillis(ac.updatedAt) ?: System.currentTimeMillis()
        val entity = ConversationEntity(
            conversationId = ac.conversationId,
            peerUserId = ac.peerId ?: "",
            lastMessagePreview = ac.lastMessage ?: "",
            lastUpdatedAt = ts,
            unreadCount = ac.unreadCount,
            exchangeId = ac.exchangeId
        )
        conversationDao.upsert(entity)
    }

    fun observeConversation(conversationId: String, currentUserId: String): Flow<List<Chat>> =
        messageDao.observeByConversation(conversationId).map { it.map { e -> e.toDomain() } }

    fun subscribeConversation(conversationId: String) = service.subscribeConversation(conversationId)

    /** Abre/asegura la conversación con exchangeId y retorna el cid */
    suspend fun openConversation(conversationId: String? = null, exchangeId: String? = null): String? {
        return try {
            val resp = chatRest.openConversation(conversationId, exchangeId)
            if (resp.isSuccessful) resp.body() else null
        } catch (_: Throwable) { null }
    }

    /** Envío con tipo explícito; si no hay cid, lo crea en backend con exchangeId. */
    fun sendMessage(
        me: String,
        peer: String,
        conversationId: String,
        content: String? = null,
        exchangeId: String? = null,
        type: MessageType = MessageType.TEXT,
        latitude: Double? = null,
        longitude: Double? = null,
        locationLabel: String? = null
    ) {
        io.launch {
            val cid = if (conversationId.isBlank()) {
                openConversation(exchangeId, exchangeId) ?: return@launch
            } else conversationId

            val now = System.currentTimeMillis()
            val clientId = UUID.randomUUID().toString()

            val local = ChatMessageEntity(
                localId = clientId,
                serverId = null,
                conversationId = cid,
                senderId = me,
                receiverId = peer,
                content = content.orEmpty(),
                type = type,
                status = SendStatus.SENDING,
                createdAt = now,
                isMine = true,
                latitude = latitude,
                longitude = longitude,
                exchangeId = exchangeId
            )
            messageDao.insert(local)

            subscribeConversation(cid)

            val payload = ChatMessagePayload(
                id = clientId,
                senderId = me,
                receiverId = peer,
                conversationId = cid,
                exchangeId = exchangeId,
                content = content,
                type = when (type) {
                    MessageType.TEXT -> ChatMessagePayload.MessageType.TEXT
                    MessageType.LOCATION -> ChatMessagePayload.MessageType.LOCATION
                },
                latitude = latitude,
                longitude = longitude,
                locationLabel = locationLabel,
                timestamp = DateTimeUtils.nowIsoUtcMillis()
            )

            service.sendPayload(
                payload,
                onOk   = { /* el eco por WS (ServerChatDto) marcará SENT */ },
                onError= { io.launch { messageDao.updateStatus(local.localId, SendStatus.FAILED) } }
            )
        }
    }

    fun disconnectAll() = service.disconnectAll()

    private suspend fun upsertIncoming(
        dto: ServerChatDto,
        fromSync: Boolean = false
    ) {
        val currentUserId = Constants.user?.id?.toString() ?: ""
        val ts = DateTimeUtils.parseIsoToEpochMillis(dto.timestamp) ?: System.currentTimeMillis()

        val ackId = dto.id
        if (!fromSync && !ackId.isNullOrBlank()) {
            try { messageDao.updateStatus(ackId, SendStatus.SENT) } catch (_: Throwable) { }
            if (dto.senderId == currentUserId) return
        }

        val domainType = when (dto.type) {
            ChatMessagePayload.MessageType.LOCATION -> MessageType.LOCATION
            else -> MessageType.TEXT
        }

        val incoming = ChatMessageEntity(
            localId = UUID.randomUUID().toString(),
            serverId = dto.id,
            conversationId = dto.conversationId,
            senderId = dto.senderId,
            receiverId = dto.receiverId,
            content = dto.content.orEmpty(),
            type = domainType,
            status = SendStatus.SENT,
            createdAt = ts,
            isMine = dto.senderId == currentUserId,
            exchangeId = dto.exchangeId,
            latitude = dto.latitude,
            longitude = dto.longitude,
        )

        messageDao.upsertFromServer(incoming, dedupeWindowMs = 60_000L)
    }


    private fun ChatMessageEntity.toDomain(): Chat =
        Chat(
            localId = localId,
            serverId = serverId,
            conversationId = conversationId,
            senderId = senderId,
            receiverId = receiverId,
            content = content,
            type = type,
            status = status,
            createdAtMillis = createdAt,
            isMine = isMine,
            latitude = latitude ,
            longitude = longitude ,
            timestampIso = DateTimeUtils.toIsoUtcMillis(java.util.Date(createdAt))
        )
}
