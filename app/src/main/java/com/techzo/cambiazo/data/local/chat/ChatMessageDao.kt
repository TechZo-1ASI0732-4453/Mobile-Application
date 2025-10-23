package com.techzo.cambiazo.data.local.chat

import androidx.room.*
import com.techzo.cambiazo.domain.SendStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatMessageDao {

    @Query("SELECT * FROM chat_messages WHERE conversationId = :cid ORDER BY createdAt ASC")
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

    @Transaction
    suspend fun upsertFromServer(incoming: ChatMessageEntity) {
        val byServer = incoming.serverId?.let { findByServerId(it) }
        if (byServer != null) {
            update(incoming.copy(localId = byServer.localId))
            return
        }
        val byClient = incoming.clientMessageId?.let { findByClientMessageId(it) }
        if (byClient != null) {
            update(incoming.copy(localId = byClient.localId))
            return
        }
        insert(incoming)
    }
}
