package com.techzo.cambiazo.domain

import com.techzo.cambiazo.data.remote.chat.ChatMessagePayload
import com.techzo.cambiazo.data.remote.chat.ServerChatDto
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import java.util.UUID

data class Chat(
    val localId: String = UUID.randomUUID().toString(),
    val serverId: String? = null,

    val conversationId: String,
    val senderId: String,
    val receiverId: String,

    val content: String,
    val type: MessageType = MessageType.TEXT,

    val status: SendStatus = SendStatus.SENDING,
    val createdAtMillis: Long = System.currentTimeMillis(),
    val isMine: Boolean,

    val latitude: Double? = null,
    val longitude: Double? = null,

    val timestampIso: String = isoFromMillisSafe(createdAtMillis)
) {
    companion object {
        fun fromServer(dto: ServerChatDto, currentUserId: String): Chat {
            val ts = millisFromIsoSafe(dto.timestamp) ?: System.currentTimeMillis()
            val domainType = when (dto.type) {
                ChatMessagePayload.MessageType.LOCATION -> MessageType.LOCATION
                else -> MessageType.TEXT
            }
            return Chat(
                localId = UUID.randomUUID().toString(),
                serverId = dto.id,
                conversationId = dto.conversationId,
                senderId = dto.senderId,
                receiverId = dto.receiverId,
                content = dto.content.orEmpty(),
                type = domainType,
                status = SendStatus.SENT,
                createdAtMillis = ts,
                isMine = dto.senderId == currentUserId,
                timestampIso = isoFromMillisSafe(ts)
            )
        }

        fun newLocal(
            me: String,
            peer: String,
            conversationId: String,
            content: String,
            type: MessageType = MessageType.TEXT
        ): Chat {
            val now = System.currentTimeMillis()
            val id = UUID.randomUUID().toString()
            return Chat(
                localId = id,
                serverId = null,
                conversationId = conversationId,
                senderId = me,
                receiverId = peer,
                content = content,
                type = type,
                status = SendStatus.SENDING,
                createdAtMillis = now,
                isMine = true,
                timestampIso = isoFromMillisSafe(now)
            )
        }
    }

    fun toPayload(
        exchangeId: String? = null,
        messageType: ChatMessagePayload.MessageType =
            if (type == MessageType.LOCATION) ChatMessagePayload.MessageType.LOCATION
            else ChatMessagePayload.MessageType.TEXT
    ): ChatMessagePayload = ChatMessagePayload(
        id = localId,
        senderId = senderId,
        receiverId = receiverId,
        conversationId = conversationId,
        exchangeId = exchangeId,
        content = content,
        type = messageType,
        latitude = null,
        longitude = null,
        locationLabel = null,
        timestamp = isoFromMillisSafe(createdAtMillis)
    )

    val isFailed get() = status == SendStatus.FAILED
    val isSending get() = status == SendStatus.SENDING
}

internal fun isoFromMillisSafe(ms: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    return sdf.format(java.util.Date(ms))
}

internal fun millisFromIsoSafe(iso: String?): Long? {
    if (iso.isNullOrBlank()) return null
    val tz = TimeZone.getTimeZone("UTC")

    val normalized = if (iso.endsWith("Z") && !iso.contains('.')) {
        iso.removeSuffix("Z") + ".000Z"
    } else iso

    val formats = listOf(
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US),
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
    )
    for (fmt in formats) {
        try {
            fmt.timeZone = tz
            val d = fmt.parse(normalized)
            if (d != null) return d.time
        } catch (_: Throwable) { /* ignore */ }
    }
    return null
}
