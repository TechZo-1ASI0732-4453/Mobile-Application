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

    @Query("SELECT * FROM chat_messages WHERE clientMessageId = :cid LIMIT 1")
    suspend fun findByClientMessageId(cid: String): ChatMessageEntity?

    @Query("UPDATE chat_messages SET status = :status WHERE localId = :localId")
    suspend fun updateStatus(localId: String, status: SendStatus)

    @Query("UPDATE chat_messages SET serverId = :serverId, status = :status, createdAt = :createdAt WHERE localId = :localId")
    suspend fun attachServerInfo(localId: String, serverId: String?, status: SendStatus, createdAt: Long)

    @Transaction
    suspend fun upsertFromServer(incoming: ChatMessageEntity) {
        val byServer = incoming.serverId?.let { findByServerId(it) }
        if (byServer != null) {
            // ya existe por serverId; opcionalmente refresca contenido/createdAt
            update(byServer.copy(
                content = incoming.content,
                type = incoming.type,
                status = incoming.status,
                createdAt = incoming.createdAt
            ))
            return
        }
        val byClient = incoming.clientMessageId?.let { findByClientMessageId(it) }
        if (byClient != null) {
            attachServerInfo(
                localId = byClient.localId,
                serverId = incoming.serverId,
                status = SendStatus.SENT,
                createdAt = incoming.createdAt
            )
            return
        }
        insert(incoming)
    }
}
