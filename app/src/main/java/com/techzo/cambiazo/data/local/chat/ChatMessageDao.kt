package com.techzo.cambiazo.data.local.chat

import androidx.room.*
import com.techzo.cambiazo.domain.SendStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatMessageDao {

    @Query("""
        SELECT * FROM chat_messages
        WHERE conversationId = :cid
        ORDER BY createdAt ASC, localId ASC
    """)
    fun observeByConversation(cid: String): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(message: ChatMessageEntity)

    @Update
    suspend fun update(message: ChatMessageEntity)

    @Query("SELECT * FROM chat_messages WHERE serverId = :sid LIMIT 1")
    suspend fun findByServerId(sid: String): ChatMessageEntity?

    @Query("UPDATE chat_messages SET status = :status WHERE localId = :localId")
    suspend fun updateStatus(localId: String, status: SendStatus)

    @Query("""
        UPDATE chat_messages
        SET serverId = :serverId, status = :status, createdAt = :createdAt
        WHERE localId = :localId
    """)
    suspend fun attachServerInfo(localId: String, serverId: String?, status: SendStatus, createdAt: Long)

    @Query("""
        SELECT * FROM chat_messages
        WHERE conversationId = :conversationId
          AND senderId       = :senderId
          AND content        = :content
          AND createdAt     >= :minCreatedAt
          AND (
                (exchangeId IS NULL AND :exchangeId IS NULL)
             OR (exchangeId = :exchangeId)
          )
        ORDER BY createdAt DESC
        LIMIT 1
    """)
    suspend fun findRecentLocalSendingMatch(
        conversationId: String,
        senderId: String,
        content: String,
        minCreatedAt: Long,
        exchangeId: String?
    ): ChatMessageEntity?

    @Transaction
    suspend fun upsertFromServer(incoming: ChatMessageEntity, dedupeWindowMs: Long = 30_000L) {
        val byServer = incoming.serverId?.let { findByServerId(it) }
        if (byServer != null) {
            update(
                byServer.copy(
                    content    = incoming.content,
                    type       = incoming.type,
                    status     = incoming.status,
                    createdAt  = incoming.createdAt,
                    exchangeId = incoming.exchangeId,
                    latitude   = incoming.latitude,
                    longitude  = incoming.longitude,
                )
            )
            return
        }

        val candidate = findRecentLocalSendingMatch(
            conversationId = incoming.conversationId,
            senderId       = incoming.senderId,
            content        = incoming.content,
            minCreatedAt   = incoming.createdAt - dedupeWindowMs,
            exchangeId     = incoming.exchangeId
        )
        if (candidate != null) {
            attachServerInfo(
                localId   = candidate.localId,
                serverId  = incoming.serverId,
                status    = SendStatus.SENT,
                createdAt = incoming.createdAt
            )
            return
        }

        insert(incoming)
    }
}
